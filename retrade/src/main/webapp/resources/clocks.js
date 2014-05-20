/*===============================================================+
 |                                                      clocks   |
 |   Clocks Script for ReTrade                                   |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

// +----: ReTrade Namespace :------------------------------------+

var ReTrade = ZeT.define('ReTrade', {})


// +----: ReTrade Clocks :---------------------------------------+

ReTrade.Clocks = ZeT.defineClass('ReTrade.Clocks', {

	init  : function(opts)
	{
		this.opts = opts || {};

		//~: create node by the template
		var t = this._tx();
		var n = t.cloneNode();

		//?: {has id}
		if(this.opts.node)
		{
			ZeT.assert(!ZeTS.ises(opts.node))
			n.id = opts.node;
		}

		//~: create the structure
		this.struct = new ZeT.Struct(n);

		//?: {has parent}
		var p = this.opts.parent;
		if(ZeT.iss(p)) p = ZeTD.n(p);
		if(this.opts.parent)
		{
			ZeT.assert(ZeTD.isn(p))
			p.appendChild(n)
		}

		//?: {do start on create}
		if(this.opts.start)
			this.start()
	},

	start : function()
	{
		if(this._timef) return this;
		this._ss = 0;
		this._timef = ZeT.fbind(this._time, this);
		this._timei = setInterval(this._timef, 1000)
		return this;
	},

	stop  : function()
	{
		if(!this._timef) return this;
		delete this._timef
		clearInterval(this._timei)
		delete this._timei
	},

	_time : function()
	{
		var n = this.struct.node();
		var t = new Date();
		var x = [t.getHours(), t.getMinutes(), t.getSeconds()];

		if(this.opts.test)
		{
			var i; if(ZeT.isu(this._testn))
			this._testn = i = 0; else i = ++this._testn;
			if(i > 83) this._testn = i = 0;

			if(i < 60) { x[0] = 0; x[1] = i }
			else { x[0] = i - 60; x[1] = 0 }
		}

		this._h(n, x[0]); this._m(n, x[1]); this._s(n, x[2])
	},

	_h    : function(n, h)
	{
		if(this._hv === h) return;
		this._hv = h;

		var t  = this._tx();
		var xh = t.walk('XH', n);
		var hx = t.walk('HX', n);
		var XH = Math.floor(h / 10);
		var HX = h % 10;

		if(XH != this._xh)
		{
			this._xh = XH;
			ZeTD.classes(xh, ['retrade-clocks-XH', 'retrade-clocks-' + XH + 'HMM'])
		}

		if(HX != this._hx)
		{
			this._hx = HX;
			ZeTD.classes(hx, ['retrade-clocks-HX', 'retrade-clocks-H' + HX + 'MM'])
		}
	},

	_m    : function(n, m)
	{
		if(this._mv === m) return;
		this._mv = m;

		var t  = this._tx();
		var xm = t.walk('XM', n);
		var mx = t.walk('MX', n);
		var XM = Math.floor(m / 10);
		var MX = m % 10;

		if(XM !== this._xm)
		{
			this._xm = XM;
			ZeTD.classes(xm, ['retrade-clocks-XM', 'retrade-clocks-HH' + XM  + 'M'])
		}

		if(MX !== this._mx)
		{
			this._mx = MX;
			ZeTD.classes(mx, ['retrade-clocks-MX', 'retrade-clocks-HHM' + MX])
		}
	},

	_s    : function(n, s)
	{
		var t = this._tx();
		n = t.walk('dots', n);
		ZeTD.styles(n, {display: ((this._ss++ % 2) == 0)?('none'):('')})
	},

	_tx   : function()
	{
		if(this._template)
			return this._template

		return this._template = new ZeT.Layout.Template(
		  {trace : ZeT.Layout.Template.Ways.traceAtNodes},
		  this._ts
		);
	},

	_ts   : ""+
		"<div>\n"+
		"  <div class = 'retrade-clocks-frame'></div>\n"+
		"  <div>@XH</div><div>@HX</div>\n"+
		"  <div class = 'retrade-clocks-dots'>@dots</div>\n"+
		"  <div>@XM</div><div>@MX</div>\n"+
		"  <div class = 'retrade-clocks-glass'></div>\n"+
		"</div>"
})

