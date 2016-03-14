/*===============================================================+
 |                                                               |
 |   ReTrade Login                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ReTradeLogin =
{
	domain      : '',

	path        : '',

	mobile      : '',

	/**
	 * Issues login procedure. Callback receives
	 * the token object, or an Error.
	 */
	login       : function(opts, callback)
	{
		var self = this, token = {}
		//console.log('ReTradeLogin logging in...')

		//~: domain
		var domain = opts.domain || this.domain
		if(!domain || !domain.length)
			throw 'ReTradeLogin error: give domain!'

		//~: login
		var login = opts.login
		if(!login || !login.length)
			throw 'ReTradeLogin error: give login!'

		//~: password
		var password = opts.password
		if(!password || !password.length)
			throw 'ReTradeLogin error: give password!'

		//~: prepare url
		var url = '' + (opts.url || '/')
		if(url.charAt(url.length - 1) != '/')
			url += '/'
		if(url.lastIndexOf('/auth/') == -1)
			url += 'auth/'
		token.url = url += 'servlet/session'

		//~: greet step
		jQuery.get({ url: url, data: { step : 'greet' } }).
		  fail(this._fail).done(function(greet) { try
		{
			if(!greet || !greet.length)
				throw 'ReTradeLogin got empty greet!'
			//console.log('ReTradeLogin greet [' + greet + ']')

			//~: extract Rs
			var b  = greet.indexOf('Rs=')
			var e  = (b != -1) && greet.indexOf('&', b)
			if(e == -1) e = greet.length

			var Rs = e && greet.substring(b + 3, e)
			if(!Rs || !Rs.length)
				throw 'ReTradeLogin got wrong greet response!'
			var rs = CryptoJS.enc.Hex.parse(Rs)
			//console.log('ReTradeLogin Rs [' + Rs + ']')

			//~: generate Rc
			var Rc = ReTradeLogin._Rc()
			var rc = CryptoJS.enc.Hex.parse(Rc)
			//console.log('ReTradeLogin Rc [' + Rc + ']')

			//~: has the password
			password = CryptoJS.SHA1(CryptoJS.enc.Utf8.parse(password))
			//console.log('ReTradeLogin P  [' + password + ']')

			//~: digest H
			var sha = CryptoJS.algo.SHA1.create()

			sha.update(rc)
			sha.update(rs)
			sha.update(CryptoJS.enc.Utf8.parse(domain))
			sha.update(CryptoJS.enc.Utf8.parse(login))
			sha.update(password)

			var H   = sha.finalize().toString().toUpperCase()
			//console.log('ReTradeLogin H  [' + H + ']')

			var d   = { step: 'login', domain: domain, login: login, Rc: Rc, H: H }

			function onfail(xhr)
			{
				self._fail(xhr, opts, d)
			}

			//~: login step
			jQuery.get({ url: url + '?' + greet, data: d }).
			  fail(onfail).done(function(sid) { try
			{
				if(!sid || !sid.length)
					throw 'ReTradeLogin got no session ID! (Login failed.)'

				//~: extract session ID
				b = sid.indexOf('sid=')
				e = (b != -1) && sid.indexOf('&', b)
				if(e == -1) e = sid.length
				token.sessionId = e && sid.substring(b + 4, e)
				//console.log('ReTradeLogin logged in! sid = ' + token.sessionId)

				if(!token.sessionId || !token.sessionId.length)
					throw 'ReTradeLogin got wrong session ID!'

				//~: digest the session key
				sha = CryptoJS.algo.SHA1.create()

				sha.update(rc)
				sha.update(rs)
				sha.update(token.sessionId)
				sha.update(password)

				token.sessionKey = sha.finalize()
				//console.log('ReTradeLogin session key [' + token.sessionKey.toString().toUpperCase() + ']')
				token.sessionNum = 0

				//!: issue test touch with bind
				token.bind = true
				ReTradeLogin.touch(token, opts, callback)
			}
			catch(e)
			{
				//console.log('ReTradeLogin login error: ' + e)
				if(opts.onerror)
					opts.onerror(e)
			}})
		}
		catch(e)
		{
			//console.log('ReTradeLogin greet error: ' + e)
			if(opts.onerror)
				opts.onerror(e)
		}})

		//?: {has callback}
		if(opts.onsuccess)
			opts.onsuccess(token, opts)

		return token
	},

	/**
	 * Does touch and returns the (same) token, or an Error.
	 */
	touch       : function(token, opts, callback)
	{
		var self = this

		//~: digest H
		var sha  = CryptoJS.algo.SHA1.create()
		var seq  = ++token.sessionNum

		sha.update(token.sessionId)
		sha.update(CryptoJS.enc.Hex.parse(ReTradeLogin._long(seq)))
		sha.update(token.sessionKey)

		var H = sha.finalize().toString().toUpperCase()
		//console.log('ReTradeLogin touch seq ' + seq +', H [' + H + ']')

		var params = {
		  step: 'touch', sid: token.sessionId,
		  sequence: seq, H : H
		}

		//?: {perform secure bind}
		if(token.bind === true)
		{
			//~: open bind key sent to the server
			params.bind = '' + new Date().getTime()

			//~: private bind key
			sha = CryptoJS.algo.SHA1.create()

			sha.update(token.sessionId)
			sha.update(CryptoJS.enc.Hex.parse(ReTradeLogin._long(seq)))
			sha.update(params.bind) //<-- here is bind hashed
			sha.update(token.sessionKey)

			token.bind = sha.finalize().toString().toUpperCase()
			//console.log('ReTradeLogin bind key [' + token.bind + ']')
		}

		function onfail(xhr)
		{
			self._fail(xhr, opts, params)
		}

		//~: touch step
		jQuery.get({ url: token.url, data: params }).
		  fail(onfail).done(function(res)
		{
			if('touched' != res)
				res = new Error('ReTradeLogin touch request failed!')
			else
				res = token

			callback(res)
		})
	},

	_fail       : function(xhr, opts, data)
	{
		var e = new Error('ReTradeLogin had failed! Code: [' +
		  xhr.status + '] message: [' + xhr.statusText + ']')

		if(opts.onerror)
			opts.onerror(e, data)
		else
			throw e
	},

	_Rc         : function()
	{
		var hex = '0123456789ABCDEF'
		var res = ''

		for(var i = 0;(i < 20);i++)
		{
			res += hex.charAt(Math.floor(16 * Math.random()))
			res += hex.charAt(Math.floor(16 * Math.random()))
		}
		return res
	},

	_long       : function(l)
	{
		var hex = '0123456789ABCDEF'
		var res = ''

		for(var i = 0;(i < 8);i++)
		{
			var b = l & 0xFF; l >>>= 8

			//HINT: higher comes first!

			res += hex.charAt((b & 0xF0) >>> 4)
			res += hex.charAt((b & 0x0F)      )
		}
		return res
	}
}

$(document).ready(function()
{
	//?: {has no domain}
	if(!ReTradeLogin.domain.length)
		return $('#nodomain-content').show()

	$('#login-layout').show()
	login_focus()

	function login_status()
	{
		var l = $.trim($('#login').val())
		var p = $.trim($('#password').val())

		if(!l.length || !p.length)
		{
			$('#button').removeClass('btn-primary').addClass('btn-default')
			return (!l.length)?$('#login'):$('#password')
		}

		$('#button').removeClass('btn-default').addClass('btn-primary')
	}

	function login_focus()
	{
		var f = login_status()
		if(f) return ani(f.focus(), 'flash')
		$('#button').focus()
	}

	function ani(n, cls)
	{
		var ts = new Date().getTime()
		n.data('anits', ts).addClass(cls).addClass('animated')

		setTimeout(function()
		{
			if(n.data('anits') != ts) return
			n.removeClass(cls).removeClass('animated')
		}, 2000)

		return n
	}

	function login_try()
	{
		if(login_focus()) return

		var request = {
			login    : $.trim($('#login').val()),
			password : $.trim($('#password').val()),
			onerror  : function(e)
			{
				ani($('#button'), 'shake')
				if(e instanceof Error) console.log(e.message)
			}
		}

		//!: invoke secured login procedure
		ReTradeLogin.login(request, function(token)
		{
			//?: {not logged in} shake the button
			if(!token || (token instanceof Error))
				request.onerror(token)

			//~: send web-session bind request
			var req = jQuery.ajax({ url: window.location.href.toString(),
				data: { bind: token.bind, mobile: $('#mobile').is(':checked') }
			})

			req.done(function()
			{
				//~: reload the requested page
				var loc = window.location.toString()

				//?: {had directly requested a page} reload it
				if(loc.indexOf('/go/login/') == -1)
					window.location.reload()
				else
					window.location = ReTradeLogin.path +
					($('#mobile').is(':checked')?('/index.html'):('/go/index'))
			})
		})
	}

	//~: activate button on edit
	$('#login, #password').on('keydown keyup focus blur', login_status).
	  on('cut paste', function() { setTimeout($.proxy(login_status, this, arguments), 100) })

	//~: login try on click
	$('#button').on('click', login_try)

	//~: login try on enter
	$('#login, #password, #button').on('keypress', function(e)
	{
		if(e.which == 13) login_try()
	})

	//?: {is mobile}
	if(ReTradeLogin.mobile === 'true')
		$('#mobile').prop('checked', true)

	//?: {is not mobile}
	else if(ReTradeLogin.mobile === 'false')
		$('#mobile').prop('checked', false)

	//~: detect is mobile
	else $('#mobile').prop('checked', jQuery.browser.mobile)
})