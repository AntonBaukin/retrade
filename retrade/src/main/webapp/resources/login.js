/*===============================================================+
 |                                                      login    |
 |   ReTrade Login                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ReTradeLogin = window.ReTradeLogin;
if(!ReTradeLogin) window.ReTradeLogin = ReTradeLogin =
{
	login   : function(opts)
	{
		var token = {};
		//console.log('ReTradeLogin logging in...')

		//~: domain
		var domain = opts.domain;
		if(!domain || !domain.length)
			throw 'ReTradeLogin error: give domain!'

		//~: login
		var login = opts.login;
		if(!login || !login.length)
			throw 'ReTradeLogin error: give login!'

		//~: password
		var password = opts.password;
		if(!password || !password.length)
			throw 'ReTradeLogin error: give password!'

		//~: prepare url
		var url = '' + (opts.url || '/');
		if(url.charAt(url.length - 1) != '/')
			url += '/';
		if(url.lastIndexOf('/auth/') == -1)
			url += 'auth/';
		token.url = url += 'servlet/session';

		try
		{
			//~: greet step
			var greet = ReTradeLogin._get({
			  url: url, params: { step : 'greet' }
			});

			if(!greet || !greet.length)
				throw 'ReTradeLogin got empty greet!';
			//console.log('ReTradeLogin greet [' + greet + ']')

			//~: extract Rs
			var b  = greet.indexOf('Rs=');
			var e  = (b != -1) && greet.indexOf('&', b);
			if(e == -1) e = greet.length;

			var Rs = e && greet.substring(b + 3, e);
			if(!Rs || !Rs.length)
				throw 'ReTradeLogin got wrong greet response!';
			var rs = CryptoJS.enc.Hex.parse(Rs)
			//console.log('ReTradeLogin Rs [' + Rs + ']')

			//~: generate Rc
			var Rc = ReTradeLogin._Rc();
			var rc = CryptoJS.enc.Hex.parse(Rc);
			//console.log('ReTradeLogin Rc [' + Rc + ']')

			//~: has the password
			password = CryptoJS.SHA1(CryptoJS.enc.Utf8.parse(password));
			//console.log('ReTradeLogin P  [' + password + ']')

			//~: digest H
			var sha = CryptoJS.algo.SHA1.create();

			sha.update(rc)
			sha.update(rs)
			sha.update(CryptoJS.enc.Utf8.parse(domain))
			sha.update(CryptoJS.enc.Utf8.parse(login))
			sha.update(password)

			var H   = sha.finalize().toString().toUpperCase();
			//console.log('ReTradeLogin H  [' + H + ']')

			//~: login step
			var sid = ReTradeLogin._get({
			  url: url + '?' + greet,  params: {

			    step: 'login', domain: domain, login: login,
			    Rc: Rc, H: H
			  }
			});

			if(!sid || !sid.length)
				throw 'ReTradeLogin got no session ID! (Login failed.)'

			//~: extract session ID
			b = sid.indexOf('sid=');
			e = (b != -1) && sid.indexOf('&', b);
			if(e == -1) e = sid.length;
			token.sessionId = e && sid.substring(b + 4, e);
			//console.log('ReTradeLogin logged in! sid = ' + token.sessionId)

			if(!token.sessionId || !token.sessionId.length)
				throw 'ReTradeLogin got wrong session ID!'

			//~: digest the session key
			sha = CryptoJS.algo.SHA1.create();

			sha.update(rc)
			sha.update(rs)
			sha.update(token.sessionId)
			sha.update(password)

			token.sessionKey = sha.finalize();
			//console.log('ReTradeLogin session key [' + token.sessionKey.toString().toUpperCase() + ']')
			token.sessionNum = 0;

			//!: issue test touch with bind
			token.bind = true;
			ReTradeLogin.touch(token)
		}
		catch(e)
		{
			//console.log('ReTradeLogin error: ' + e)
			if(opts.onerror)
				opts.onerror(e)
			return null;
		}

		//?: {has callback}
		if(opts.onsuccess)
			opts.onsuccess(token, opts)

		return token;
	},

	touch   : function(token)
	{
		//~: digest H
		var sha = CryptoJS.algo.SHA1.create();
		var seq = ++token.sessionNum;

		sha.update(token.sessionId)
		sha.update(CryptoJS.enc.Hex.parse(ReTradeLogin._long(seq)))
		sha.update(token.sessionKey)

		var H   = sha.finalize().toString().toUpperCase();
		//console.log('ReTradeLogin touch seq ' + seq +', H [' + H + ']')

		var pms = {
		  step: 'touch', sid: token.sessionId,
		  sequence: seq, H : H
		};

		//?: {perform secure bind}
		if(token.bind === true)
		{
			//~: open bind key sent to the server
			pms.bind = '' + new Date().getTime();

			//~: private bind key
			sha = CryptoJS.algo.SHA1.create();

			sha.update(token.sessionId)
			sha.update(CryptoJS.enc.Hex.parse(ReTradeLogin._long(seq)))
			sha.update(pms.bind) //<-- here is bind hashed
			sha.update(token.sessionKey)

			token.bind = sha.finalize().toString().toUpperCase();
			//console.log('ReTradeLogin bind key [' + token.bind + ']')
		}

		//~: touch step
		var res = ReTradeLogin._get({url: token.url, params: pms});
		if('touched' != res)
			throw 'ReTradeLogin touch request failed!'
	},

	_get    : function(x)
	{
		var res = null;
		var err = null;

		////console.log('ReTradeLogin jQuery available: ' + !!jQuery)
		////console.log('ReTradeLogin get url [' + x.url + ']')

		//~: invoke via jQuery
		if(jQuery) $.ajax({

		  url: x.url, async : false,
		  data: x.params || '', dataType: 'text',

		  error   : function(xhr)
		  {
				err = 'Code: [' + xhr.status +
				  '] message: [' + xhr.statusText + ']';
				//console.log('ReTradeLogin get error! ' + err)
		  },

		  success : function(data)
		  {
				res = data;
				////console.log('ReTradeLogin got [' + data + ']')
		  }
		})

		if(err) throw 'ReTradeLogin had failed! ' + err;

		return res;
	},

	_Rc     : function()
	{
		var hex = '0123456789ABCDEF';
		var res = '';

		for(var i = 0;(i < 20);i++)
		{
			res += hex.charAt(Math.floor(16 * Math.random()));
			res += hex.charAt(Math.floor(16 * Math.random()));
		}
		return res;
	},

	_long   : function(l)
	{
		var hex = '0123456789ABCDEF';
		var res = '';

		for(var i = 0;(i < 8);i++)
		{
			var b = l & 0xFF; l >>>= 8;

			//HINT: higher comes first!

			res += hex.charAt((b & 0xF0) >>> 4);
			res += hex.charAt((b & 0x0F)      );
		}
		return res;
	}
}