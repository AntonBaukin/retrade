/*===============================================================+
 |                                                     border    |
 |   Borders Processing for Zero ZeT Library                     |
 |                                   / anton.baukin@gmail.com /  |
 +---------------------------------------------------------------+
 | -> zetobj, zetdom
 +===============================================================*/

/**
 * The namespace of ZeT Border processors library.
 */
ZeT.Border = ZeT.define('ZeT.Border', {})



// +----: ZeT.Border.Base :--------------------------------------+

/**
 * Implementation base for border templates.
 */
ZeT.Border.Base = ZeT.defineClass('ZeT.Border.Base', {

	init              : function(opts)
	{
		this.opts = opts || {};
	},

	/**
	 * Define the strings with the keys of
	 * the template areas.
	 */
	KEYS              : [],

	XYZ               : null,

	proc              : function(node)
	{
		var tmplt  = this._template();
		var cells  = this._border_cells();
		var rnode  = tmplt.cloneNode(this.opts['cloneOpts']);
		var ways   = tmplt.fillWays(rnode, cells)
		var struct = this._init_struct(rnode, ways, cells)

		if(ZeTD.isxn(node))
			this._replace_node(rnode, node, struct)
		return rnode;
	},

	_border_cells     : function()
	{
		ZeT.asserta(this.KEYS)

		var k, r = {};
		for(var j = 0;(j < this.KEYS.length);j++)
			r[k = this.KEYS[j]] = this.opts[k];

		return r;
	},

	_init_struct      : function(node, ways /*, cells*/)
	{
		var struct = new ZeT.Struct(node);

		struct.template(this._template())
		struct.content = ways['cnt'];

		return struct;
	},

	_template         : function()
	{
		return ZeT.assertn(this.XYZ, 'ZeT.Border.Base.XYZ template is not defined!')
	},

	_replace_node     : function(border, node, struct)
	{
		if(node.parentNode)
			node.parentNode.replaceChild(border, node)

		if(struct.content)
			struct.content.appendChild(node)
	}
})



// +----: ZeT.Border.Full :--------------------------------------+

/**
 * A ZeT.Layout.Proc processing class that creates a border
 * structure. It is also able to surround the node is being
 * processed into the border structure.
 *
 * The constructor options are:
 *
 *  · ???  (* see the table)
 *
 *  the contents of the border layout cells. You may define
 *  a content in the constructor options and replace it
 *  in the processing one. The format of the content is
 *  described in 'ZeT.Layout.fill()' function.
 *
 * Note that it is not allowed to place SHARED node instances
 * in the constructor. Use plain HTML or templates instead!
 *
 * The templates must be referenced by direct instances!
 * (As content of the node may be plain text.)
 *
 * Wrapping table looks as follows:
 *
 *    +---------+---------~---------+---------+
 *    |   ltc   |        thh        |   rtc   |
 *    +-----+---+---------~---------+---+-----+
 *    | lvu |                           | rvu |
 *    +-----+                           +-----+
 *    ~ lvm ~          cxx-[cnt]        ~ rvm ~
 *    +-----+                           +-----+
 *    | lvd |                           | rvd |
 *    +-----+---+---------~---------+---+-----+
 *    |   lbc   |        bhh        |   rbc   |
 *    +---------+---------~---------+---------+
 *
 * The 'cnt' content is used only when the border creator
 * is not invoked to wrap the node processed by a pipe.
 */
ZeT.Border.Full = ZeT.defineClass('ZeT.Border.Full', 'ZeT.Border.Base', {

	KEYS : (function(){

		if(ZeTD.IE) return [
		 'top', 'ltc', 'thx', 'thh', 'rtc',
		 'lvu', 'mup', 'rvu',
		 'lvm', 'rvm', 'cxx', 'cnt',
		 'lvd', 'mbo', 'rvd',
		 'bot', 'lbc', 'bhx', 'bhh', 'rbc'
		]

		return [
		 'top', 'ltc', 'thx', 'thh', 'rtc',
		 'lft', 'lvu', 'cyy', 'cnt', 'rvu',
		 'rht', 'lvm', 'rvm', 'lvd', 'rvd',
		 'bot', 'lbc', 'bhx', 'bhh', 'rbc'
		]

	}()),

	XYZ  : (function(){

		var TEMPLATE = ZeTD.IE &&

		  "<table cellpadding='0' cellspacing='0' border='0' style='width: 100%; " +
		  "height:100%'><tr><td colspan='3'>@top<div><div>@ltc</div><div>@thx<div>@thh" +
		  "</div></div><div>@rtc</div></div></td></tr><tr><td>@lvu<div></div></td><td>" +
		  "@mup<div></div></td><td>@rvu<div></div></td></tr><tr><td>@lvm<div></div>" +
		  "</td><td>@cxx<div><div><table cellpadding='0' cellspacing='0' border='0'>" +
		  "<tr><td>@cnt</td></tr></table></div></div></td><td>@rvm<div></div></td>" +
		  "</tr><tr><td>@lvd<div></div></td><td>@mbo<div></div></td><td>@rvd<div></div>" +
		  "</td></tr><tr><td colspan='3'>@bot<div><div>@lbc</div><div>@bhx<div>@bhh</div>" +
		  "</div><div>@rbc</div></div></td></tr></table>";

		TEMPLATE = TEMPLATE ||

		  "<table cellpadding='0' cellspacing='0' border='0' style='width:100%; " +
		  "height:100%'><tr><td colspan='3'>@top<div><div>@ltc</div><div>@thx<div>" +
		  "@thh</div></div><div>@rtc</div></div></td></tr><td>@lft<table cellpadding='0' " +
		  "cellspacing='0' border='0'><tr><td>@lvu<div/></td></tr><tr><td>@lvm<div/>" +
		  "</td></tr><tr><td>@lvd<div/></td></tr></table></td><td>@cyy<table " +
		  "cellpadding='0' cellspacing='0' border='0'><tr><td>@cnt</td></tr></table>" +
		  "</td><td>@rht<table cellpadding='0' cellspacing='0' border='0'><tr><td>@rvu" +
		  "<div/></td></tr><tr><td>@rvm<div/></td></tr><tr><td>@rvd<div/></td></tr>" +
		  "</table></td><tr><td colspan='3'>@bot<div><div>@lbc</div><div>@bhx<div>@bhh" +
		  "</div></div><div>@rbc</div></div></td></tr></table>";

		return new ZeT.Layout.Template(
		  { trace : ZeT.Layout.Template.Ways.traceAtNodes }, TEMPLATE)
	}())
});


// +----: ZeT.Border.full() :------------------------------------+

/**
 * Builds border options to create complex border with
 * all the cells of the template set. The border contains
 * images for the cells. They are set as the backgrounds
 * for the tables cells. Use CSS classes to define them.
 *
 * This function is ready to be included in options
 * processing pipe.
 *
 * The income options are:
 *
 *  · border  ZeT.Border.Full
 *
 *  the border class (or it's definition key);
 *
 *  · keys    (ZeT.Border.Full.KEYS)
 *
 *  the keys of the cells to initialize. By default
 *  they are keys for all lower-level cells except
 *  the content one;
 *
 *  . pattern ('XYZ')
 *
 *  the pattern (substring) to replace with the
 *  names of the cells;
 *
 *  . classes
 *
 *  the pattern string of the class of each cell;
 *
 *  . fills
 *
 *  map-object with fill parameters of each cell.
 */
ZeT.Border.full = ZeT.define('ZeT.Border.full()', function(opts)
{
	var res   = this, node = this;
	if((res == ZeT.Border) || (res == window)) res = {};
	if(ZeTD.isxn(node)) res = {}; else node = null;

	var bcls  = ZeT.Border.Full;
	if(opts.border) bcls = opts.border;
	if(ZeT.iss(bcls)) bcls = ZeT.assertn(ZeT.defined(bcls),
	  'ZeT border class [', bcls , '] is not defined!')

	var pat   = opts['pattern']; if(!ZeT.iss(pat)) pat = 'XYZ';
	var fills = opts['fills']; if(!fills) fills = {};
	var clss  = opts.classes; if(!ZeT.iss(clss)) clss = null;

	var keys  = (opts = opts || {}).keys;
	if(!ZeT.isa(keys)) keys = ZeT.asserta(bcls.prototype.KEYS);

	for(var i = 0;(i < keys.length);i++)
	{
		var k  = keys[i], e = res[k] || {};
		var f  = fills[k] || {};
		res[k] = ZeT.extend(e, f);

		var c  = fills['classes'];
		if(!c && clss) c = clss.replace(pat, k);

		c = ZeTA.merge(ZeTA.copy(e.classes), c);
		if(c.length) e.classes = c;
	}

	if(node)
	{
		if(!res.cnt) res.cnt = {};
		res.cnt.node = node;

		return ZeT.Layout.proc(new bcls(res))();
	}

	return res;
})