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


// +----: ZeT.Border.Full :--------------------------------------+

/**
 * A ZeT.Layout.Proc processing class that creates a border
 * structure. It is also able to surround the node is being
 * processed into the border structure.
 *
 * The constructor options are:
 *
 *  · ???  (* see the tables)
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
 *    +-----+---------------------------+-----+
 *    | ltc |           [upt]           | rtc |
 *    +-----+---------------------------+-----+
 *    | lvu |                           | rvu |
 *    +-----+                           +-----+
 *    ~ lvm ~            cnt            ~ rvm ~
 *    +-----+                           +-----+
 *    | lvd |                           | rvd |
 *    +-----+---------------------------+-----+
 *    | lbc |           [bot]           | rbc |
 *    +-----+---------------------------+-----+
 *
 *      upt +-----+-------~-------+-----+
 *     cell | ltr |      thh      | rtl |
 *          +-----+-------~-------+-----+
 *
 *      bot +-----+-------~-------+-----+
 *     cell | lbr |      bhh      | rbl |
 *          +-----+-------~-------+-----+
 *
 * where letters define the following:
 *  · L left, · R right, · T top, · B bottom,
 *  · H horizontal, · V vertical, · D down,
 *  · U up, · C corner, · M middle.
 *
 * The cells of layout structure (and the options) are:
 * 'ltc', 'upt', 'ltr', 'thh', 'rtl', 'rtc', 'lvu', 'rvu',
 * 'lvm', 'cnt', 'rvm', 'lvd', 'rvd', 'lbc', 'bot', 'lbr',
 * 'bhh', 'rbl', 'rbc'.
 *
 * The 'cnt' content is used only when the border creator is
 * not invoked to wrap the node processed by a pipe.
 */
ZeT.Border.Full = ZeT.defineClass('ZeT.Border.Full', {

	init              : function(opts)
	{
		this.opts = opts || {};
	},

	KEYS              : [
	 'ltc', 'upt', 'ltr', 'thh', 'rtl', 'rtc',  'lvu',
	 'rvu', 'lvm', 'rvm', 'lvd', 'cnt', 'xcnt', 'rvd',
	 'lbc', 'bot', 'lbr', 'bhh', 'rbl', 'rbc'
	],

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
		return ZeT.Border.Full.xyz;
	},

	_replace_node     : function(border, node, struct)
	{
		if(node.parentNode)
			node.parentNode.replaceChild(border, node)

		if(struct.content)
			struct.content.appendChild(node)
	}
})

ZeT.Border.Full.xyz = ZeT.define('ZeT.Border.Full.xyz',
new ZeT.Layout.Template({
  trace : ZeT.Layout.Template.Ways.traceAtNodes
},

  "<table cellpadding='0' cellspacing='0' border='0'>"+
  "<tbody>"+
  "  <tr>"+
  "    <td>@ltc<div/></td>"+
  "    <td>@upt"+
  "      <table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>"+
  "        <tr>"+
  "          <td style='width:00.0001%;'>@ltr<div/></td>"+
  "          <td style='width:99.9999%;'>@thh</td>"+
  "          <td style='width:00.0001%;'>@rtl<div/></td>"+
  "        </tr>"+
  "      </table>"+
  "    </td>"+
  "    <td>@rtc<div/></td>"+
  "  </tr>"+
  "  <tr>"+
  "    <td>@lvu<div/></td>"+
  "    <td></td>"+
  "    <td>@rvu<div/></td>"+
  "  </tr>"+
  "  <tr>"+
  "    <td>@lvm</td>"+

  //HINT: the inner table is needed to allow all possible styles
  //      for the content. Placing the content not in a inner
  //      table may break 'xcnt' negative margin!

  "    <td><div>@xcnt<table cellpadding='0' cellspacing='0' border='0'>" +
     "<tr><td>@cnt</td></tr></table></div>"+
  "    <td>@rvm</td>"+
  "  </tr>"+
  "  <tr>"+
  "    <td>@lvd<div/></td>"+
  "    <td></td>"+
  "    <td>@rvd<div/></td>"+
  "  </tr>"+
  "  <tr>"+
  "    <td>@lbc<div/></td>"+
  "    <td>@bot"+
  "      <table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>"+
  "        <tr>"+
  "          <td style='width:00.0001%;'>@lbr<div/></td>"+
  "          <td style='width:99.9999%;'>@bhh</td>"+
  "          <td style='width:00.0001%;'>@rbl<div/></td>"+
  "        </tr>"+
  "      </table>"+
  "    </td>"+
  "    <td>@rbc<div/></td>"+
  "  </tr>"+
  "</tbody>"+
  "</table>"
))

/**
 * The keys of all public cells of Full Border template.
 */
ZeT.Border.Full.KEYS_ALL = ZeT.Border.Full.prototype.KEYS;

/**
 * The keys of all border cells of Full Border template.
 * (Collecting keys are removed.)
 */
ZeT.Border.Full.KEYS_BRD = ZeTA.remove(
  ZeTA.copy(ZeT.Border.Full.KEYS_ALL),
  'upt', 'bot', 'cnt', 'xcnt');


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
 *  · keys    (ZeT.Border.Full.KEYS_ALL)
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

	var pat   = opts['pattern']; if(!ZeT.iss(pat)) pat = 'XYZ';
	var fills = opts['fills']; if(!fills) fills = {};
	var clss  = opts.classes; if(!ZeT.iss(clss)) clss = null;

	var keys  = (opts = opts || {}).keys;
	if(!ZeT.isa(keys)) keys = ZeT.Border.Full.KEYS_ALL;

	for(var i = 0;(i < keys.length);i++)
	{
		var k  = keys[i], e = res[k] || {};
		var f  = fills[k] || {};
		res[k] = ZeT.extend(e, f);

		var c  = fills['classes'];
		if(!c && clss) c = clss.replace(pat, k);

		c = ZeTA.merge(e.classes, c);
		if(c.length) e.classes = c;
	}

	if(node)
	{
		if(!res.cnt) res.cnt = {};
		res.cnt.node = node;
		return ZeT.Layout.proc(new ZeT.Border.Full(res))();
	}

	return res;
})