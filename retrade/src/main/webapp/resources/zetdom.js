/*===============================================================+
 |                                                     zetdom    |
 |   Zero ZeT  Java Script Library                               |
 |                                   / anton.baukin@gmail.com /  |
 +---------------------------------------------------------------+
 | -> zetobj
 +===============================================================*/


/**
 * The namespace of ZeT Document Layout library.
 */
ZeT.Layout = ZeT.define('ZeT.Layout', {})


// +----: ZeTD: -------------------------------------------------+

var ZeTD = ZeT.define('ZeTD',
{
// +----: Test Functions :---->

	/**
	 * Checks the object given is a tag node.
	 * Optional tag name must be lower-case.
	 */
	isn              : function(node, tag)
	{
		if(!node || (node.nodeType !== 1))
			return false

		return (!tag)?(true):ZeT.iss(node.tagName) &&
		  (node.tagName.toLowerCase() === tag)
	},

	/**
	 * Checks the object given is a DOM node.
	 */
	isxn             : function(node)
	{
		return !!(node && !ZeT.isu(node.nodeType));
	},

	/**
	 * Node is global only when it's ancestor is
	 * the window document.
	 */
	isgn             : function(node)
	{
		while(ZeTD.isxn(node))
			if(node == document) return true;
			else node = node.parentNode;
		return false;
	},

	isoc             : function(node)
	{
		var c = node.firstChild;
		return !!(c && !c.nextSibling);
	},


// +----: Search Functions :---->

	n                : function(id_or_node, dom)
	{
		if(ZeT.i$x(id_or_node))  return undefined;
		if(ZeTD.isn(id_or_node)) return id_or_node;
		if(!ZeT.iss(id_or_node)) throw 'ZeTD: node ID is not a string!';

		if(!dom || ZeT.i$f(dom, 'getElementById'))
			return (dom || document).getElementById(id_or_node);

		var opts = {id: id_or_node};
		ZeT.Layout.Treeters.findNodeById.proc(dom, opts)
		return opts.result;
	},

	xn               : function(id_or_node, dom)
	{
		if(ZeT.i$x(id_or_node))  return undefined;
		if(ZeTD.isn(id_or_node)) return id_or_node;
		if(!ZeT.iss(id_or_node)) return undefined;

		if(!dom || ZeT.i$f(dom, 'getElementById'))
			return (dom || document).getElementById(id_or_node);

		var opts = {id: id_or_node};
		ZeT.Layout.Treeters.findNodeById.proc(dom, opts)
		return opts.result;
	},

	/**
	 * Returns the first child element node.
	 */
	first            : function(node)
	{
		if(!ZeTD.isn(node)) return null
		for(var n = node.firstChild;(n);n = n.nextSibling)
			if(ZeTD.isn(n)) return n
		return null
	},

	/**
	 * Searches for the first descendant element
	 * having the tag name given. Note that the
	 * tag argument must be lower-cased.
	 */
	downtag          : function(node, tag)
	{
		if(!node || (node.nodeType !== 1))     return null
		if(node.tagName.toLowerCase() === tag) return node

		for(var n = node.firstChild;(n);n = n.nextSibling)
			if(node = ZeTD.downtag(n, tag)) return node
		return null
	},

	uptag             : function(node, tag)
	{
		while(node) if(ZeTD.isn(node, tag)) return node
			else node = ZeTD.isn(node.parentNode)?(node.parentNode):(null)
		return undefined
	},


// +----: Update Functions :---->

	update           : function(/* node, items */)
	{
		var node = ZeTD.isn(this)?(this):(arguments[0])
		if(!ZeTD.isn(node)) return node

		//~: remove existing child nodes
		while(node.hasChildNodes())
			node.removeChild(node.lastChild)

		//~: append the new ones
		for(var i = (node == this)?(0):(1);(i < arguments.length);i++)
		{
			var x, a = arguments[i]

			if(ZeT.isn(a)) a = '' + a
			if(ZeT.iss(a)) x = ZeTD.html(a)
			else if(ZeTD.isxn(a))
			{
				if(a.parentNode) a.parentNode.removeChild(a)
				x = [a]
			}

			if(x) for(var j = 0;(j < x.length);j++)
				node.appendChild(x[j])
		}

		return node
	},

	/**
	 * Takes HTML string and converts it to an array
	 * of nodes (detached from the parent).
	 */
	html             : function(html)
	{
		if(!ZeT.iss(html)) return undefined;

		var n = document.createElement('div');
		n.innerHTML = html;
		var r = ZeT.a(n.childNodes);

		for(var i = 0;(i < r.length);i++)
			n.removeChild(r[i])
		return r;
	},

	/**
	 * Adds CSS styles to the node given.
	 * Parameter 'style' must be a map-object
	 * with standard JavaScript Node keys-values.
	 */
	styles           : function(node, styles)
	{
		if(!ZeTD.isn(node) || !styles) return node;

		var keys = ZeT.keys(styles);
		for(var i = 0;(i < keys.length);i++)
			node.style[keys[i]] = styles[keys[i]];

		return node;
	},

	/**
	 * Single string name or array of class names. If a name
	 * has +prefix, the class is added; -removes the class.
	 *
	 * If there are names without these prefixes, they are
	 * concatenated and rewrite the class name before
	 * and and remove operations.
	 *
	 * Remove operation is done before the add one.
	 * (To change the order of names.)
	 */
	classes           : function(node, classes)
	{
		if(ZeT.iss(node)) node = ZeTD.n(node)
		if(!ZeTD.isn(node)) return node

		if(ZeT.iss(classes))  classes = [classes];
		if(!ZeT.isa(classes)) return node;

		var c, n = [], m = [], p = [];
		var x = node.className || '';

		//~: separate the class names
		for(var i = 0;(i < classes.length);i++)
			if(ZeTS.first(c = classes[i]) == '-')
				m.push(c.substring(1))
			else if(ZeTS.first(c) == '+')
				p.push(c.substring(1))
			else n.push(c)

		if(n.length) x = n.join(' ');

		//~: do remove
		var r = x.split(/\s+/);
		for(i = 0;(i < m.length);i++)
		{
			var k = r.indexOf(m[i]);
			if(k != -1) r[k] = '';
		}

		//~: do add
		x = r.join(' ');
		if(p.length) x = x.concat((x.length?(' '):('')) + p.join(' '));

		node.className = x;
		return node;
	},

	hasclass          : function(node, c1ass)
	{
		if(!ZeTD.isn(node) || !node.className) return undefined;

		var nc = ' '.concat(node.className, ' ');
		var cn = ' '.concat(c1ass, ' ');

		return (nc.indexOf(cn) != -1);
	},

	/**
	 * Writes attributes to the node given.
	 * The values must be strings or numbers;
	 * null or undefined to remove it.
	 */
	attrs             : function(node, attrs)
	{
		ZeT.assert(ZeTD.isn(node))
		if(!attrs) return node

		var k, v, names = ZeT.keys(attrs)
		for(var i = 0;(i < names.length);i++)
		{
			if(!ZeT.iss(k = names[i])) continue

			//?: {remove attribute}
			if(ZeT.i$x(v = attrs[k])) node.removeAttribute(k); else
			{
				if(ZeT.isn(v)) v = '' + v
				ZeT.assert(ZeT.iss(v), 'DOM Node attribute [', k,
				  '] is not a string, but: [', v, ']')

				node.setAttribute(k, v)
			}
		}

		return node
	},

	attr              : function(node, name, value)
	{
		ZeT.assert(ZeTD.isn(node))
		ZeT.asserts(name)

		if(ZeT.i$x(value)) node.removeAttribute(name); else
		{
			if(ZeT.isn(value)) value = '' + value
			ZeT.assert(ZeT.iss(value), 'DOM Node attribute [', name,
			  '] is not a string, but: [', value, ']')

			node.setAttribute(name, value)
		}
	},


// +----: Browsers Detection :---->

	IE                : (function()
	{
		var DM = document.documentMode;

		try{ document.documentMode = ''; } catch(e) {}
		var res = typeof document.documentMode == 'number' || eval('/*@cc_on!@*/!1');
		try{ document.documentMode = DM; } catch(e) {}

		return !!res
	}())
})


// +----: ZeT.Struct :-------------------------------------------+

/**
 * ZeT.Struct instance is associated with the DOM node as
 * one-to-one. Structure contains the information on the
 * library object created the node or referred by the one.
 *
 * As some utilities of the library are for general
 * processing of the widgets, the structure contains
 * unified bunch of useful data.
 *
 * A special intent of Struct is to be set to a widget
 * stored as a Template. When creating an instance of the
 * template, Struct is used to build a widget based on
 * the Struct assigned to the template.
 */
ZeT.Struct = ZeT.defineClass('ZeT.Struct', {

	ZeT_Struct        : true,

	/**
	 * Associates the Struct with the node given.
	 */
	init              : function(node, opts)
	{
		this.opts   = opts || {};
		this._funcs = {};
		this._share = [];
		this._node  = this._init_node(node);
	},

	node              : function()
	{
		var node = this._node;

		//?: {is global node with ID} store by ID
		if(ZeTD.isxn(node) && ZeT.i$xfalse(this.opts, 'nobyid'))
			if(!ZeTS.ises(node.id) && ZeTD.isgn(node))
				this._node = node.id;

		if(ZeT.iss(node))
			node = ZeTD.n(node);

		if(!ZeTD.isxn(node)) throw 'ZeT.Struct: structure is not ' +
		  'bount to an existing DOM node!';

		return node;
	},

	/**
	 * Assigns the shared state of this struct to
	 * the shared state of the struct given.
	 *
	 * The shared state includes:
	 *  · the template;
	 *  · functions, see func();
	 *  · shared fields, see share().
	 *
	 * The options allows to select what to copy:
	 *  · template (true);
	 *  · func     (true);
	 *  · fields   (true);
	 *  · share    (true).
	 *
	 * Fields copy means to copy the shared fields of
	 * this instance. Share option means to copy the
	 * shared list itself.
	 */
	assign            : function(struct, opts)
	{
		struct = ZeT.struct(struct, true);

		if(ZeT.i$xtrue(opts, 'fields'))
			this._assign_fields(struct, opts)

		if(ZeT.i$xtrue(opts, 'template'))
			struct._template = this._template;

		if(ZeT.i$xtrue(opts, 'func'))
			struct._funcs = ZeT.extend({}, this._funcs);

		if(ZeT.i$xtrue(opts, 'share'))
			struct._share = this._share.slice();

		return this;
	},

	/**
	 * The template used to create this node,
	 * or the source node of the template itself.
	 */
	template          : function(template)
	{
		if(!template)
		{
			var t = this._template;
			if(ZeT.iss(t)) t = ZeT.Layout.template(t);
			if(t && t['ZeT_Layout_Template']) return t;
			return undefined;
		}

		this._template = template;
		return this;
	},

	/**
	 * Registers and returns the functions specific
	 * to the structure.
	 *
	 * When creating a copy of Struct, the functions
	 * from the store are also copied, what is not
	 * true for the functions directly set as a field
	 * to the objects.
	 *
	 * The arguments of the call are:
	 *  · name (String);
	 *  · name (String), func (Function);
	 *  · {name: func}.
	 *
	 * Third variant allows to set several functions.
	 */
	func              : function()
	{
		var funcs = arguments[0]; if(!funcs) return this;
		if(ZeT.iss(funcs)) if(ZeT.isu(arguments[1]))
			return this._funcs[name];
		else funcs = {funcs: arguments[1]};

		var n, f, names = ZeT.keys(funcs);

		for(var i = 0;(i < names.length);i++)
			if(ZeT.iss(n = names[i])) this._funcs[n] =
			  ZeT.isf(f = funcs[n])?(f):(undefined);

		return this;
	},

	/**
	 * When assigning this instance to other
	 * Struct it is possible to copy the fields
	 * directly when calling them to be shared.
	 *
	 * Send array of string names of the fields.
	 * The fields are added. To remove field
	 * prefix it with '-'. To clear the list
	 * of fields send null.
	 */
	share             : function(fields)
	{
		if(ZeT.iss(fields)) fields = [fields];

		if(!ZeT.isa())
		{
			if(fields === null) this._share = [];
			return this;
		}

		for(var i = 0;(i < fields.length);i++)
		{
			var f = fields[i]; if(ZeTS.ises(f)) continue;
			if(ZeTS.first(f) == '-')
			{
				var p = this._share.indexOf(f = f.substring(1));
				if(p != -1) this._share.splice(p, 1)
			}
			else if(this._share.indexOf(f) == -1)
				this._share.push(f)
		}
		return this;
	},

	_init_node        : function(node)
	{
		var res = node;

		//?: {is global node with ID} store by ID
		if(ZeTD.isxn(node) && ZeT.i$xfalse(this.opts, 'nobyid'))
			if(!ZeTS.ises(node.id) && ZeTD.isgn(node))
				res = node.id;

		ZeT.struct(node, this)
		return res;
	},

	_assign_fields    : function(struct)
	{
		var k, s = this._share;

		for(var i = 0;(i < s.length);i++)
			struct[k] = this[k = s[i]];
	}
})


// +----: ZeT.struct() :-----------------------------------------+

/**
 * Returns ZeT.Struct instance, or assigns it to the
 * DOM node given.
 *
 * The possible arguments are:
 *
 *  · DOM node (or ID string), [true]
 *  returns Struct assigned to this node;
 *  If second argument is true the exception
 *  is raised if no struct is assigned.
 *
 *  · DOM node (or ID string), Struct instance
 *  assigns the struct to the node;
 *
 *  · Struct instance
 *  just returns this struct testing it's class.
 */
ZeT.struct = ZeT.define('ZeT.struct()', function(n, s)
{
	var e = (s === true); if(e) s = null;

	if(ZeTD.isxn(n = ZeTD.xn(n)))
	{
		if(s && s['ZeT_Struct']) //<-- assign to node
			return n['ZeT_Struct'] = s;
		s = n['ZeT_Struct'];
	}
	else s = n; //<-- just test the structure

	if(s && s['ZeT_Struct']) return s;

	if(e) throw 'ZeT.struct(): argument is not ' +
	  'a ZeT.Struct or a DOM node that refers it!';
	return undefined;
})


// +----: ZeT.Layout.Template (Core) :---------------------------+


/**
 * Unique DOM ID of global area to place the
 * templates by default. The templates of this
 * area are not automatically removed.
 */
ZeT.Layout.TemplateGlobal = 'ZeT_Layout_Template_GlobalArea';

/**
 * Creates and inserts HTML template DOM element into
 * the page. After creating the node, the instance
 * created is assigned to this node by the key
 * 'ZeT_Layout_Template'.
 *
 * Constructor arguments:
 *
 *  · opts    (required) options;
 *  · html    (required) HTML markup text, or a DOM node.
 *
 * The options:
 *
 *  · id      (optional)
 *
 *  the string ID of this template unique within the area.
 *  Is related to the DOM id only when the area is a node
 *  bound to the global document tree;
 *
 *  · area    (optional)
 *
 *  the area where to create and store the template DOM
 *  sub-tree. If not provided, an element is created,
 *  but not attached to the global DOM tree. A string value
 *  is assumed as the ID within the global DOM. If no
 *  such node exist it is created and inserted on the first
 *  demand as a hidden node;
 *
 *  · global  (false)
 *
 *  if area option is not set, but this option is, the
 *  global area is selected;
 *
 *  · clean   (true)
 *
 *  tells to remove from the template node tree all text
 *  nodes with whitespace-empty string values;
 *
 *  · downtag (optional)
 *
 *   lower-cased name of the descending tag of the template
 *   to return on the template node.
 *
 *
 * The following callbacks may be provided as options.
 * All of them are invoked in the order of the list
 * after the template's node was placed in the area
 * (on the first demand):
 *
 *  · first      function(this)
 *
 *  the first callback invoked;
 *
 *  · trace      (see 'traceTree()' method)
 *
 *  if this predicate function is defined, traceTree()
 *  method would be invoked on the template's tree,
 *  and the resulting map is registered as the ways;
 *
 *  · last       function(this)
 *
 *  the latest callback invoked. It may alter the
 *  resulting template's tree;
 *
 *  · cloneDone  function(node, this, opts): Node
 *
 *  invoked on each template clone operation.
 *  See cloneNode() for details.
 */
ZeT.Layout.Template = ZeT.defineClass('ZeT.Layout.Template', {

	ZeT_Layout_Template : true,

	init             : function(opts, html)
	{
		//~: assign the template options & the id
		this.opts = ZeT.assertn(opts, 'ZeT.Layout.Template: no options!')
		if(ZeT.iss(this.opts.id)) this.id = this.opts.id

		//?: {has no content}
		ZeT.assert(ZeT.iss(html) || ZeTD.isxn(html),
		  'ZeT.Layout.Template: has no HTML or DOM node!')
		this.html = html

		this._init_area()
	},

	/**
	 * Returns the DOM node of this template being already
	 * created and inserted in the templates area.
	 */
	node             : function()
	{
		this._check_area()
		return this._get_node()
	},

	xnode            : function()
	{
		var node = this.node()

		if(ZeT.iss(this.opts.downtag))
			return ZeT.assertn(ZeTD.downtag(node, this.opts.downtag))

		return node
	},

	_init_area       : function()
	{
		var area = this.opts['area']

		if(!area && (this.opts['global'] === true))
			area = ZeT.Layout.TemplateGlobal

		if(ZeT.i$x(area)) area = this._create_area()

		//?: {the area is string id} init it later
		if(ZeT.iss(area)) { this._area_id = area; this._area_gl = true } else
		//?: {the area is defined, but not a node}
		ZeT.assert(ZeTD.isn(area), 'ZeT.Layout.Template: ',
		  'area option is set, but is not a string or DOM node!')

		if(ZeTD.isn(area))
		{
			this._area_gl = ZeTD.isgn(area)
			if(!ZeTS.ises(area.id)) this._area_id = area.id
		}

		//?: {is global node having DOM id} place the area
		if(this._area_gl && !ZeTS.ises(this._area_id))
			if(!(area = this._find_gl_area(this._area_id)))
			{
				if(!ZeTD.isn(area)) area = this._create_area()
				this._place_gl_area(area)
			}

		ZeT.assert(ZeTD.isn(area), 'ZeT.Layout.Template: ',
		  'could not create templates area node!')

		this._bind_template(area)
	},

	_get_area        : function()
	{
		var area = this._area; if(area) return area

		if(this._area_id && this._area_gl)
			return this._find_gl_area(this._area_id, true)
		return (this._area = this._create_area())
	},

	_create_area     : function()
	{
		var area = document.createElement('div')
		if(this._area_id) area.id = this._area_id
		ZeTD.styles(area, { display: 'none'})
		return area
	},

	_find_gl_area    : function(area_id, error)
	{
		var area  = ZeTD.n(area_id) //<-- find it in the global DOM
		if(area) return area

		var areas = document['ZeT_Layout_TemplateAreas']
		if(areas && !areas.length)
		{
			delete document['ZeT_Layout_TemplateAreas']
			areas = null
		}

		if(areas) for(var i = 0;(i < areas.length);i++)
			if(areas[i].id === area_id) return areas[i]

		ZeT.assert(!error, 'ZeT.Layout.Template: global templates ',
		  'area [', area_id, '] was not bound properly!')
		return null
	},

	_place_gl_area   : function(area)
	{
		if(!document.body) //<-- not available yet, delay
		{
			var areas = document['ZeT_Layout_TemplateAreas']

			if(!areas) document['ZeT_Layout_TemplateAreas'] = areas = []
			for(var i = 0;(i < areas.length);i++) //<-- ?: {exists}
				if(area.id === areas[i].id) { area = null; break }
			if(area) areas.push(area)

			return this._area_ck = true //<-- do check area later
		}

		var ref = ZeTD.first(document.body)
		var ctn = function() { return document.createTextNode('\n\n') }
		var bdy = document.body

		if(ref)
		{
			bdy.insertBefore(ctn(), ref) //<-- support for saved documents
			bdy.insertBefore(area,  ref)
			bdy.insertBefore(ctn(), ref)
		}
		else
		{
			bdy.appendChild(ctn())
			bdy.appendChild(area)
			bdy.appendChild(ctn())
		}

		return false
	},

	/**
	 * When template objects are in scripts placed in the header
	 * part of the page, there is no document body object yet,
	 * and it is not possible to insert anything there.
	 * For this reason we wait until the body is available.
	 */
	_check_area      : function()
	{
		if(!this._area_ck || !this._area_gl || !this._area_id)
		{ this._area_ck = false; return }

		var area = this._find_gl_area(this._area_id, true)

		//?: {the area is already a global node}
		if(ZeTD.isgn(area)) { this._area_ck = false; return }
		this._area_ck = this._place_gl_area(area)
		if(this._area_ck) return //<-- will check again

		//~: remove document registration of the area
		var areas = document['ZeT_Layout_TemplateAreas']
		if(!areas) return

		var areai = areas.indexOf(area)
		if(areai != -1) areas.splice(areai, 1)
		if(!areas.length) delete document['ZeT_Layout_TemplateAreas']
	},

	_get_node        : function()
	{
		if(this._node) return this._node

		this._node = this._insert_node()
		this._notify_inserted()
		return this._node
	},

	_insert_node     : function()
	{
		var node, area = this._get_area()

		//?: {has global area} support the saved pages (reuse node)
		if(this._area_gl && this.id && (node = ZeTD.n(this.id)))
			node = this._prep_node(node)
		else
		{
			node = this._create_node()
			this._place_node(area, node)
		}

		this._unbind_template(area)
		return node
	},

	_create_node     : function()
	{
		var node = this.html; if(ZeT.iss(this.html))
			node = this._create_str_node()

		ZeT.assert(ZeTD.isn(node), 'ZeT.Layout.Template: ',
		  'the template instantiated is not a DOM node!')

		return this._prep_node(node)
	},

	_create_str_node : function()
	{
		var node = document.createElement('div')

		ZeTD.update(node, this.html)
		node['ZeT_Layout_Template'] = { rootless: true }

		return node
	},

	_prep_node       : function(node)
	{
		var x = node['ZeT_Layout_Template']; if(!x) x = {}

		if(ZeT.i$xtrue(this.opts, 'clean'))
			ZeT.Layout.Treeters.cleanWs.proc(node)

		if(x.rootless && ZeTD.isoc(node))
		{	//<-- string (html) has one child
			node = node.firstChild //!: not remove it:
			x.rootless = false     // saved pages support
		}

		if(this.id) node.id = this.id
		x.template = this; node['ZeT_Layout_Template'] = x

		return node
	},

	_place_node      : function(area, node)
	{
		area.appendChild(node)

		//~: add line break for saved pages
		area.appendChild(document.createTextNode('\n'))
	},

	_bind_template   : function(area)
	{
		var l = area['ZeT_Layout_Templates']
		if(!l) area['ZeT_Layout_Templates'] = l = []
		l.push(this)
	},

	_unbind_template : function(area)
	{
		var l = area['ZeT_Layout_Templates']; if(!l) return

		for(var i = 0;(i < l.length);i++) if(l[i] == this)
		{
			l.splice(i, 1)
			if(!l.length) delete area['ZeT_Layout_Templates']
			return
		}
	},

	_notify_inserted : function()
	{
		this._notify_first()
		this._notify_trace()
		this._notify_last()
	},

	_notify_first    : function()
	{
		if(ZeT.i$f(this.opts, 'first'))
			this.opts['first'](this)
	},

	_notify_trace    : function()
	{
		if(ZeT.i$f(this.opts, 'trace'))
			this.ways(this.traceTree(this.opts['trace']))
	},

	_notify_last     : function()
	{
		if(ZeT.i$f(this.opts, 'last'))
			this.opts['last'](this)
	}
})


// +----: ZeT.Layout.template() :--------------------------------+

/**
 * Finds ZeT.Layout.Template instance by the given
 * template id within the area given.
 *
 * If area is not defined, the global default one
 * is taken. It has id ZeT.Layout.TemplateGlobal.
 *
 * If area option is string, it must be a valid DOM
 * ID of the area node placed in the global tree.
 *
 * Else, area must be the same DOM node that was given
 * when creating the template.
 *
 * When area is placed in the global DOM tree, the template
 * search operation is much faster as it is regular node
 * search by it's ID.
 *
 * When template is not found this function throws exception!
 */
ZeT.Layout.template = ZeT.define('ZeT.Layout.template()',
  function(id, opts)
{
	if(id && id['ZeT_Layout_Template']) return id
	ZeT.asserts(id, 'ZeT.Layout.template(): ',
	  'template ID must be a valid Node ID string!')

	var area = opts && opts['area']
	if(ZeT.isu(area)) area = ZeT.Layout.TemplateGlobal

	//?: {the area is given by it's global id}
	if(ZeT.iss(area))
	{
		var n = ZeTD.n(id) //<-- search the template globally first
		var x = n && n['ZeT_Layout_Template']
		if(x && x.template) return x.template

		//!: the template is not placed in the global area yet
		var a = ZeTD.n(area), as = !a &&
		  document['ZeT_Layout_TemplateAreas']

		//?: {not found it} search in the delayed
		if(as) for(var i = 0;(i < as.length);i++)
			if(area === as[i].id) { a = as[i]; break }

		if(a) area = a;
	}

	//?: {area node is not defined}
	ZeT.assert(ZeTD.isn(area), 'ZeT.Layout.template(): ',
	  'templates Area DOM node is not defined and found!')

	var ti = new ZeT.Layout.Treeter(function(node, opts)
	{
		if(node.id == id) { opts.result = node; return false }
		if(opts.treetlv >= 1) return ZeT.Layout.Treeter_SKIP
		return node
	})

	//!: search for the template directly in area node
	var sr = {}; ti.proc(area, sr); if(sr.result) return sr.result

	//~: search in the area's registry array
	var ra = area['ZeT_Layout_Templates']
	if(ra) for(var j = 0;(j < ra.length);j++)
		if(ra[j] && (ra[j].id === id))
			return ra[j]

	ZeT.assert(!ZeT.i$xtrue(opts, 'assert'), 'ZeT.Layout.template(): ',
	  'template id [', id, '] was not found in area: [',
	  (area.id?(area.id):(area)), ']!')

	return undefined
})


// +----: ZeT.Layout.Template (Clone) :--------------------------+

/**
 * This extension to Template is to create clones (copies)
 * of the template DOM node.
 *
 * The shared options of the clone operation are:
 *
 *   · id            (optional)
 *
 *   the ID of the copied DOM node;
 *
 *   · root          ('div')
 *
 *   if the template string has created several roots
 *   (still under automatically created root node), this
 *   option defines the tag name of the root element of
 *   the clone. If this option is false the result of the
 *   clone operation would be an array of nodes. Note that
 *   all callbacks still get the phony root node;
 *
 *   · cloneDone     function(node, opts, this): Node
 *
 *   callback invoked when the copy completes,
 *   and the copied node is ready. It may return Node
 *   to overwrite the result.
 *
 * If the template node contains ZeT.Struct instance having
 * func-registered function 'cloneDone', it is also called.
 *
 * The order of invocation clone callbacks is:
 *  · template' options cloneDone;
 *  · template node' Struct func cloneDone;
 *  · cloneNode() options cloneDone.
 */
ZeT.extendClass('ZeT.Layout.Template', {

	cloneNode        : function(opts)
	{
		opts = opts || {}

		var node = ZeT.assertn(this.node(), 'ZeT.Layout.Template: ',
		  'no DOM node is found for template clone operation!')

		var copy = node.cloneNode(true)

		//?: {has down-tag}
		if(ZeT.iss(this.opts.downtag))
		{
			copy = ZeT.assertn(ZeTD.downtag(copy, this.opts.downtag))
			copy.parentNode.removeChild(copy)
		}

		return this._clone_node(copy, opts, node)
	},

	_clone_node      : function(node, opts, source)
	{
		var x = source['ZeT_Layout_Template']

		if(x.rootless && (opts.root === false))
			return ZeT.a(node.childNodes)

		if(x.rootless)
		{
			var rtag = ZeT.iss(opts.root)?(opts.root):('div')
			var root = document.createElement(rtag)
			ZeTD.update.apply(root, ZeT.a(node.childNodes))
			node = root
		}

		//~: assign or remove the id
		ZeTD.attr(node, 'id', ZeT.iss(opts.id)?(opts.id):(null))

		if(node['ZeT_Layout_Template'])
			node['ZeT_Layout_Template'] = undefined

		return this._on_clone_done(node, opts, source)
	},

	_on_clone_done   : function(node, opts, source)
	{
		node = this._on_clone_done_t(node, opts, source)
		node = this._on_clone_done_s(node, opts, source)
		return this._on_clone_done_o(node, opts, source)
	},

	_on_clone_done_t : function(node, opts, source)
	{
		var r; if(ZeT.i$f(this.opts, 'cloneDone'))
			r = this.opts['cloneDone'](node, opts, this)
		return ZeTD.isxn(r)?(r):(node)
	},

	_on_clone_done_s : function(node, opts, source)
	{
		var struct  = ZeT.struct(source)
		if(!struct) return node
		var r, func = struct.func('cloneDone')

		if(ZeT.isf(func)) r = func(node, opts, this)
		return ZeTD.isxn(r)?(r):(node)
	},

	_on_clone_done_o : function(node, opts, source)
	{
		var r; if(ZeT.i$f(opts, 'cloneDone'))
			r = opts['cloneDone'](node, opts, this)
		return ZeTD.isxn(r)?(r):(node)
	}
})


// +----: ZeT.Layout.Template (Ways) :---------------------------+

/**
 * A way in the DOM tree of a template is an array of indices
 * of the nodes starting from the template' root. To create a
 * way use 'walk()' method.
 */
ZeT.extendClass('ZeT.Layout.Template', {

	/**
	 * Returns map-object of the ways registered in this
	 * template object. If optional argument is defined,
	 * it is added to the ways registered. But if this
	 * map-object is null, the ways are cleared.
	 */
	ways             : function(ways)
	{
		if(!this._ways) this._ways = {};
		if(ZeT.isu(ways)) return this._ways;
		if(ways === null) return (this._ways = {});
		return ZeT.extend(this._ways, ways);
	},

	way              : function(way)
	{
		if(!this._ways) this._ways = {};
		return this._ways[way];
	},

	/**
	 * Creates a way from one of the descendant nodes of the
	 * template (the argument) to the root of the template.
	 */
	trace            : function(node)
	{
		if(!ZeTD.isxn(node)) return undefined;
		var xnode = node, stack = [], root  = this.xnode();

		//go up to the root
		while(xnode)
		{
			stack.push(xnode) //<-- the root is always added
			if(xnode == root) break;
			xnode = xnode.parentNode;
		}

		//?: {did not rich the template root}
		if(xnode !== root) return undefined;

		var inds  = [];

		//c: calc the indices
		for(var i = stack.length - 1;(i > 0);i--)
		{
			var c = stack[i - 1]; //<-- the child
			var p = stack[i];     //<-- it's parent, till the root
			var l = p.childNodes;

			for(var j = 0;(j < l.length);j++)
				if(l[j] == c) { inds.push(j); break; }
		}

		return inds;
	},

	/**
	 * Iterates over the template DOM tree invoking the
	 * handler function given. The handler is a
	 *
	 *  function(node, template): result
	 *
	 * When result is not defined, the node given is
	 * not related to any way. Else the object returned
	 * has the fields followed:
	 *
	 *  · key
	 *
	 *  obligatory not empty string with the name
	 *  of the way;
	 *
	 *  · node
	 *
	 *  optional node to trace to. Default to the node
	 *  given to the handler;
	 *
	 *  · removeNode
	 *
	 *  orders to remove the node (with Treeter).
	 *
	 * Handler may return string value if result object
	 * has only the key field.
	 *
	 * Note that the handler may alter or remove the
	 * node if it returns else node in the result.
	 */
	traceTree        : function(handler)
	{
		if(!ZeT.isf(handler)) throw 'ZeT.Layout.Template: ' +
		  'traceTree() handler is not a function!';

		//~: collect the nodes
		var self = this, found = [], result = {};
		var iter = new ZeT.Layout.Treeter(function(node)
		{
			var r = handler(node, self);
			if(ZeT.iss(r)) r = {key: r};
			if(!r || ZeTS.ises(r.key)) return node;

			if(!ZeTD.isxn(r.node)) r.node = node;
			found.push(r)
			return r.removeNode?(undefined):(node);
		});

		iter.proc(this.xnode())

		//~: trace the nodes collected
		for(var i = 0;(i < found.length);i++)
		{
			var w = self.trace(found[i].node);
			if(w.length) result[found[i].key] = w;
		}

		return result;
	},

	/**
	 * Goes by the way given, returns the node at the end.
	 * The way may be an array of indices, or a key in
	 * the 'ways()' map-object.
	 */
	walk             : function(way, node)
	{
		if(ZeT.iss(way))  way = this.ways()[way];
		if(!ZeT.isa(way)) throw 'ZeT.Layout.Template: ' +
		  'can not walk() by not a way indices array!';

		node = ZeTD.isxn(node)?(node):(this.node());
		for(var i = 0;(node && (i < way.length));i++)
			node = node.childNodes[way[i]];

		return node;
	},

	/**
	 * Invokes 'walk()' method on all the ways registered
	 * and returns a map-object with the nodes walked.
	 */
	walkAll          : function(node)
	{
		var self = this, res = {};

		node = ZeTD.isxn(node)?(node):(this.node());
		this._walk_all(function(key)
		{
			var n = self.walk(key, node);
			if(n) res[key] = n;
		})

		return res;
	},

	/**
	 * This method takes all the ways of the template
	 * and invokes the method given on each of the nodes.
	 *
	 * The argument lists are as follows:
	 *
	 *  · node, callback;
	 *  · callback
	 *
	 * Node may be defined by it's ID. If node is not
	 * given, the template's one is taken.
	 *
	 * Callback function takes the following arguments:
	 *   (node by the way, way key).
	 *
	 * When callback returns false, iteration breaks.
	 */
	walkEach        : function()
	{
		var self = this
		var n = arguments[0], f = arguments[1]

		if(ZeT.iss(n)) n = ZeT.assertn(ZeTD.n(n))
		if(!ZeTD.isxn(n)) { n = this.node(); f = arguments[0] }
		ZeT.assert(ZeT.isf(f))

		this._walk_all(function(key)
		{
			var x = self.walk(key, n)
			if(x) return f(x, key)
		})
	},

	/**
	 * This method takes all the ways of the template
	 * and invokes fill operation on each of the node
	 * by the way. The argument lists are as follows:
	 *
	 *  · node, fills [, Fill];
	 *  · fills [, Fill].
	 *
	 * Node may be defined by it's ID. If node is not
	 * given, the template's one is taken (to fill
	 * the template itself).
	 *
	 * 'Fill' is class ZeT.Layout.Fill by default, or
	 * it's subclass. Parameter 'fills' defines map-object
	 * of Fill options to update the content. Each key
	 * of the map is also the name of the way registered.
	 *
	 * The result of this call is map-object of the
	 * nodes traced by the names. Note that only those
	 * nodes are filled that had 'fills' key defined.
	 */
	fillWays      : function()
	{
		var node  = arguments[0], fills, Fill;

		if(!ZeTD.isxn(node = ZeTD.xn(node)))
		{
			node  = this.node();
			fills = arguments[0];
			Fill  = arguments[1];
		}
		else
		{
			fills = arguments[1];
			Fill  = arguments[2];
		}

		var self = this, res = {}; if(!fills) return res;
		if(!ZeT.isf(Fill)) Fill = ZeT.Layout.Fill;

		this._walk_all(function(key)
		{
			var n = self.walk(key, node);
			res[key] = n;

			if(n && fills[key])
				new Fill(fills[key]).fill(n)
		})

		return res;
	},

	_walk_all     : function(f)
	{
		var key, keys = ZeT.keys(this.ways());
		for(var i = 0;(i < keys.length);i++)
			if(ZeT.iss(key = keys[i]))
				if(f(key) === false)
					break
	}
})


// +----: ZeT.Layout.Template.Ways :-----------------------------+

/**
 * The collection if Template ways search predicates
 * and related helper functions.
 */
ZeT.Layout.Template.Ways = ZeT.define('ZeT.Layout.Template.Ways', {

	/**
	 * This Ways handler inspects all text nodes
	 * and comments whether they do start with '@'.
	 *
	 * Note that this handler immediately removes
	 * the text nodes selected, but lefts the comments.
	 */
	traceAtNodes  : function(node)
	{
		//~: access the key text
		var key; if(node.nodeType == 3) key = node.nodeValue
		else if(node.nodeType == 8) key = node.data
		if(!ZeT.iss(key)) return
		if(!/^\s*@/.test(key)) return

		return {
		  key: ZeTS.trim(key).substring(1),
		  node: node.parentNode,
		  removeNode: (node.nodeType == 3)
		}
	}
})


// +----: ZeT.Layout.Fill :--------------------------------------+

/**
 * Use this function-object to update the content
 * of the node, it's attributes and even the parent.
 *
 * The options are (prefix 'p' means parent node):
 *
 *  · cmd                ('U')
 *
 *  the command of fill operation. See cmd() method;
 *
 *  · styles, pstyles    (optional)
 *
 *  map-object with the style in format of style property
 *  of DOM nodes. See ZeTD.style();
 *
 *  · classes, pclasses  (optional)
 *
 *  string or array of class names. (See ZeTD.classes().);
 *
 *  · attrs, pattrs       (optional)
 *
 *  object with attributes to set. (See ZeTD.attrs().);
 *
 *  · ref                (0)
 *
 *  for insert operation defines the reference node (or
 *  it's index) to insert before;
 *
 *  · node      (-template)
 *
 *  single item (or an array) of DOM nodes or HTML content
 *  strings to append to or insert into the node;
 *
 *  . template  (-node)
 *
 *  defines ZeT.Layout.Template instance (or it's ID in the
 *  global area). Nodes from cloned template are taken
 *  to fill the target.
 *
 *  This class is ready to be included in a processing pipe.
 *  The processing argument must be a Node instance.
 */
ZeT.Layout.Fill = ZeT.defineClass('ZeT.Layout.fill()',
{
	ZeT_Layout_Fill  : true,

	/**
	 * Creates fill operation with the options defined.
	 */
	init             : function(opts)
	{
		this.opts = opts || {};
	},

	/**
	 * One of the options, the command of fill operation.
	 * 'U' updates the node replacing it's current content.
	 * 'A' appends to the node (nodes), 'I' inserts.
	 */
	cmd              : function()
	{
		return ZeT.iss(this.opts.cmd)?(this.opts.cmd):('U');
	},

	/**
	 * Processes fill operation depending on the command
	 * defined in the options.
	 *
	 * Node may be a string (ID), or DOM node, or an array
	 * of strings or nodes.
	 *
	 * The result of the call is the income node.
	 */
	fill             : function(node)
	{
		if(ZeT.isa(node))
		{
			for(var i = 0;(i < node.length);i++)
				this.fill(node[i])
			return node;
		}

		if(ZeT.iss(node)) node = ZeTD.n(node);
		if(!ZeTD.isxn(node)) return node;

		node = this._fill_cmd(node);
		this._refine(node)
		return node;
	},

	proc             : function(node)
	{
		return this.fill(node);
	},

	_fill_cmd        : function(node)
	{
		switch(this.cmd())
		{
			case 'U': return this._fill_cmd_u(node);
			case 'A': return this._fill_cmd_a(node);
			case 'I': return this._fill_cmd_i(node);
		}
		return node;
	},

	_fill_cmd_u      : function(node)
	{
		var src = this._fill_src();

		if(ZeTD.isxn(src))
			ZeTD.update(node, src)
		else if(ZeT.isa(src))
			ZeTD.update.apply(node, src)

		return node;
	},

	_fill_cmd_a      : function(node)
	{
		var src = this._fill_src();

		for(var i = 0;(i < src.length);i++)
			node.appendChild(src[i])
		return node;
	},

	_fill_cmd_i      : function(node)
	{
		var ref = this.opts['ref'];
		var chs = node.childNodes;

		if(ZeT.isi(ref))
			ref = (ref < chs.length)?(chs[ref]):(null);

		if(ZeTD.isxn(ref))
		{
			var xref = ref; ref = null;
			for(var j = 0;(j < chs.length);j++)
				if(xref == chs[j]) { ref = chs[j]; break; }
		}

		if(!ZeTD.isxn(ref)) ref = node.firstChild;
		if(!ZeTD.isxn(ref)) return this._fill_cmd_a(node);

		var src = this._fill_src();
		for(var k = 0;(k < src.length);k++)
		   node.insertBefore(src[k], ref)

		return node;
	},

	_refine          : function(node)
	{
		this._styles(node)
		this._classes(node)
		this._attrs(node)
	},

	_styles          : function(node)
	{
		if(this.opts['styles'])
			ZeTD.styles(node, this.opts['styles'])

		if(this.opts['pstyles'])
			ZeTD.styles(node.parentNode, this.opts['pstyles'])
	},

	_classes         : function(node)
	{
		if(this.opts['classes'])
			ZeTD.classes(node, this.opts['classes'])

		if(this.opts['pclasses'])
			ZeTD.classes(node.parentNode, this.opts['pclasses'])
	},

	_attrs           : function(node)
	{
		if(this.opts['attrs'])
			ZeTD.attrs(node, this.opts['attrs'])

		if(this.opts['pattrs'])
			ZeTD.attrs(node.parentNode, this.opts['pattrs'])
	},

	_fill_src        : function()
	{
		var node  = this.opts['node'];
		var tmplt = this._fill_tmplt();

		if(tmplt)
			node = tmplt.cloneNode(this.opts['cloneOpts']);
		else if(ZeT.iss(node))
			node = ZeTD.html(node);

		if(ZeTD.isxn(node))
			node = [node];
		else if(!ZeT.isa(node) && !ZeT.isu(node))
			node = [];

		return node;
	},

	_fill_tmplt      : function()
	{
		var tmplt = this.opts['template'];

		if(ZeT.iss(tmplt))
			tmplt = ZeT.Layout.template(tmplt)

		//?: {is not that class}
		if(tmplt && !(tmplt['ZeT_Layout_Template'] === true))
			throw 'ZeT.Layout.Fill: template option is not a ' +
			  'ZeT.Layout.Template instance!';

		return tmplt;
	}
})


// +----: ZeT.Layout.Treeter :-----------------------------------+

/**
 * Skip constant for tree iteration callback.
 * See ZeT.Layout.Treeter.
 */
ZeT.Layout.Treeter_SKIP = ZeT.Layout.Treeter_SKIP || {};

/**
 * 'Treeter' means 'Tree Iterator'. This iteration functor is
 * for traversing a DOM-like tree. It is reentable (by default),
 * and may process several trees. In ZeT library tree iterators
 * are used to instantiate the HTML templates.
 *
 * While traversing it is possible to update, replace or delete
 * the nodes, and insert new ones.
 *
 * The order of traversing (in this implementation) is in-depth.
 * It is the same as the order of elements in the HTML text.
 * The root node is processed by a special method.
 *
 * If a child node is removed or replaced, the processing does
 * not go recursively into it.
 *
 * The callback is of type: function(node, opts).
 *
 * At the call-time there are temporary options available:
 *
 *  · treetlv
 *  the level in the sub-tree, where the root has 0;
 *
 *  · treeter
 *  this treeter instance;
 *
 *  · treetit
 *  the number of phases to process the tree (default 0).
 *  Allows to collect the data on the first phases. Each
 *  iteration opts.treetit is decremented and stops on -1.
 *
 *
 * Depending on the result of the callback call, the node:
 *
 *  · on undefined   is removed;
 *  · on differ node is replaced;
 *  · on same node   is left unchanged.
 *
 * On SKIP result the sub-tree is not processed. The root node
 * is skipped and returned when the callback returns not a node.
 *
 * If the node is removed it and it's sub-tree are not processed.
 * If the node is replaced it itself is not processed, but it's
 * sub-tree is. (This refers the root node too.)
 */
ZeT.Layout.Treeter = ZeT.defineClass('ZeT.Layout.Treeter', {

	ZeT_Layout_Treeter : true,

	/**
	 * The constructor takes the arguments functions
	 * forming a ZeT.pipe!
	 */
	init             : function(/* piped callbacks */)
	{
		this.callback = ZeT.pipe.apply(null, arguments);
	},

	/**
	 * Traverses the tree. Returns the argument node.
	 */
	proc             : function(node, opts)
	{
		if(!ZeTD.isn(node = ZeTD.n(node)))
			return undefined;

		opts = opts || {};
		opts.treeter = this;
		if(!ZeT.isn(opts.treetit)) opts.treetit = 0;

		while(opts.treetit >= 0)
		{
			opts.treetlv = 0;
			var r = this._onnode(node, opts);
			if(!ZeTD.isn(r)) return node;

			opts.treetlv = 1;
			this._proc(node, opts)

			opts.treetit--
		}

		opts.treetlv = 1;
		this._proc(node, opts)

		opts.treetlv = undefined;
		opts.treeter = undefined;
		return node;
	},

	/**
	 * Called on each node including the root one.
	 *
	 * The returned value is a node, or undefined.
	 * If the node is not the same as the original,
	 * the node replaces the original.
	 */
	_onnode           : function(node, opts)
	{
		return !ZeT.isf(this.callback)?(node):
		  this.callback(node, opts, this);
	},

	_proc             : function(node, opts)
	{
		for(var n = node.firstChild;(n);)
		{
			{//<-- local scope
				var s = n.nextSibling; //<-- in case of alter
				var r = this._onnode(n, opts);

				//?: skip the sub-tree
				if(r === ZeT.Layout.Treeter_SKIP)
				{
					n = s;
					continue;
				}

				//?: {the node was removed}
				if(ZeT.isu(r))
				{
					r = n; n = s;
					node.removeChild(r)
					continue;
				}

				//?: {not a node} do exit
				if(!ZeTD.isxn(r))
					return false;

				//?: {the node is replaced}
				if(n != r)
				{
					node.replaceChild(r, n)
					n = r;
				}

				//!: do nothing on the same node
			}

			//!: go recursively (into elements)
			if(ZeTD.isn(n))
			{
				opts.treetlv++
				if(!this._proc(n, opts))
					return false
				opts.treetlv--
			}

			n = s;
		}

		return true
	}
})


// +----: ZeT.Layout.Treeters :----------------------------------+

/**
 * The collection of ready to use tree iterators.
 */
ZeT.Layout.Treeters = ZeT.define('ZeT.Layout.Treeters', {

	/**
	 * Searches for the node by the given 'opts.id' string.
	 * The result is stored in 'opts.result'.
	 */
	findNodeById      : new ZeT.Layout.Treeter(function(node, opts)
	{
		//?: {this node is not that having goal ID}
		if(!ZeTD.isn(node) || !(node.id === opts.id))
			return node //<-- will left the node unchanged

		opts.result = node;
		return false //<-- will break
	}),

	findNodeByClass   : new ZeT.Layout.Treeter(function(node, opts)
	{
		//?: {this node is not that having goal class}
		if(!ZeTD.isn(node) || !ZeTD.hasclass(node, opts.class))
			return node //<-- will left the node unchanged

		opts.result = node;
		return false //<-- will break
	}),

	cleanWs           : new ZeT.Layout.Treeter(function(node)
	{
		if((node.nodeType === 3) && ZeTS.ises(node.nodeValue))
			return undefined //<-- will remove this text node
		return node //<-- will left the node unchanged
	})
})


// +----: ZeT.Layout.Proc :--------------------------------------+


/**
 * The collection of classes to create, update and
 * initialize DOM trees.
 *
 * Each processing class must have method:
 *   function proc(node, opts): Node
 *
 * The resulting node may be undefined, equal to
 * the argument node, or else node to replace the
 * argument in the DOM tree is being processed.
 */
ZeT.Layout.Proc = ZeT.Layout.Proc || {}


/**
 * Processing instances are objects. When a function
 * is needed, call this function to wrap the instance.
 *
 * The arguments may be as follows:
 *
 *  · Proc, opts
 *
 *  takes the processing class and creates the instance
 *  with the options given;
 *
 *  · proc
 *
 *  takes given processing instance.
 */
ZeT.Layout.proc = ZeT.define('ZeT.Layout.proc()', function()
{
	var proc = arguments[0];

	if(ZeT.isf(proc))
		proc = new proc(arguments[1]);

	if(!ZeT.i$f(proc, 'proc')) throw 'ZeT.Layout.proc(): ' +
		'can not wrap not a processor instance!';

	return function()
	{
		return proc.proc.apply(proc, arguments)
	}
})

ZeT.Layout.procPipe = ZeT.define('ZeT.Layout.procPipe()', function()
{
	var pipe = [];

	for(var i = 0;(i < arguments.length);i++)
	{
		var proc = arguments[i];

		if(ZeT.isf(proc))
		{
			var opts;

			if(i + 1 < arguments.length)
			{
				opts = arguments[i + 1];

				//?: {next argument is not an options}
				if(ZeT.isf(opts) || ZeT.i$f(opts, 'proc'))
					opts = null;
				if(opts) i++; //<-- skip the options
			}

			proc = (opts)?(new proc(opts)):(new proc());
		}

		if(!ZeT.i$f(proc, 'proc')) throw 'ZeT.Layout.procPipe(): ' +
			'can not wrap not a processor instance by index ' + i + '!';

		pipe.push(ZeT.Layout.proc(proc))
	}

	return ZeT.pipe.apply(this, pipe);
})

ZeT.Layout.procPipeCall = ZeT.define('ZeT.Layout.procPipeCall()', function()
{
	var pipe = ZeT.Layout.procPipe.apply(this, arguments);
	return pipe.apply(this);
})


// +----: ZeT.Layout.Proc.Node :---------------------------------+

/**
 * Creates DOM node, or retrieves existing global node by
 * it's ID. Supports templates for node creation.
 *
 * The options are:
 *
 *  · node
 *
 *  DOM node or global ID;
 *
 *  · html      (excludes node)
 *
 *  the HTML of the node to create;
 *
 *  · template  (excludes node, html)
 *
 *  instance of ZeT.Layout.Template to create the node.
 *  May be also an ID in the global templates area;
 *
 *  · cloneOpts (optional)
 *
 *  options to give to the template clone operation;
 *
 *  · tag       ('div')
 *
 *  name of the tag to wrap the resulting multiple
 *  roots under the single one. Multiple roots may
 *  occur when creating from HTML or template.
 *
 *  This class is ready to be included in a processing pipe.
 *  If argument Node is given, no node is created.
 */
ZeT.Layout.Proc.Node = ZeT.defineClass('ZeT.Layout.Proc.Node', {

	init              : function(opts)
	{
		this.opts = opts || {};
	},

	proc              : function(node)
	{
		if(!ZeTD.isxn(node))
			node = this._wrap_nodes(this._create_node());

		if(!ZeTD.isxn(node)) throw 'ZeT.Layout.Proc.Node: ' +
		  'could not create DOM node!';

		return node;
	},

	_create_node      : function()
	{
		if(this.opts.template)
			return this._clone_template()

		if(ZeT.iss(this.opts.html))
			return this._create_html()

		return this._defined_node()
	},

	_wrap_nodes       : function(nodes)
	{
		if(ZeTD.isxn(nodes))    return nodes;
		if(!ZeT.isa(nodes))     return undefined;
		if((nodes.length == 1)) return nodes[0];

		for(var i = 0;(i < nodes.length);i++)
			if(!ZeTD.isxn(nodes[i])) return undefined;

		var node = this._create_root();
		ZeTD.update.apply(node, nodes)
		return node;
	},

	_clone_template   : function()
	{
		var template = ZeT.Layout.template(this.opts.template);
		return template.cloneNode(this.opts['cloneOpts']);
	},

	_create_html      : function()
	{
		return ZeTD.html(this.opts.html);
	},

	_create_root      : function()
	{
		return document.createElement(
		  ZeT.iss(this.opts['tag'])?(this.opts['tag']):('div')
		);
	},

	_defined_node     : function()
	{
		if(ZeT.iss(this.opts.node))
			return ZeTD.n(this.opts.node)
		return this.opts.node
	}
})


// +----: ZeT.Layout.Proc.Wrap :---------------------------------+

/**
 * Extends ZeT.Layout.Proc.Node class.
 *
 * Wraps the node processed into the DOM created from
 * HTML or from the template given. Allows to tell
 * where within the DOM tree to append it.
 *
 * The options are:
 *
 *  · node        (forbidden)
 *
 *  ZeT.Layout.Proc.Node class allows to select existing
 *  DOm node, but this class forbids this!
 *
 *  · insertWay   (optional, template-only)
 *
 *  in the case of template defines the way name to
 *  append the precessed node into;
 *
 *  · insertClass (optional, excludes insertWay)
 *
 *  the CSS class name of the node in the created DOM
 *  tree to insert the precessed node into;
 *
 *  · styles      (optional)
 *
 *  map-object with the style in format of style property
 *  of DOM nodes. See ZeTD.style(). Applied to the root
 *  wrapping node;
 *
 *  · classes     (optional)
 *
 *  string or array of class names. See ZeTD.classes().
 *  Applied to the root wrapping node;
 *
 *  · attrs       (optional)
 *
 *  object with attributes to set. See ZeTD.attrs().
 *  Applied to the root wrapping node.
 */
ZeT.Layout.Proc.Wrap = ZeT.defineClass('ZeT.Layout.Proc.Wrap', ZeT.Layout.Proc.Node, {

	proc              : function(node)
	{
		//?: {wrapping not a DOM node}
		if(!ZeTD.isxn(node))
			throw 'Not a DOM node in the processing pipe!'

		//~: create the wrapping DOM element
		var wrap = this._wrap_nodes(this._create_node());
		if(!ZeTD.isn(wrap)) throw 'ZeT.Layout.Proc.Wrap: ' +
		   'could not create DOM node!'

		//~: fill it
		this._fill_wrap(wrap)

		//~: wrap in it
		return this._do_wrap(wrap, node)
	},

	_defined_node     : function()
	{
		if(this.opts.node)
			throw 'ZeT.Layout.Proc.Wrap forbids node option!'
		return null
	},

	_fill_wrap        : function(node)
	{
		if(this.opts.styles)
			ZeTD.styles(node, this.opts.styles)

		if(this.opts.classes)
			ZeTD.classes(node, this.opts.classes)

		if(this.opts.attrs)
			ZeTD.attrs(node, this.opts.attrs)
	},

	_do_wrap          : function(wrap, node)
	{
		//~: append to the position
		this._wrap_pos(wrap).appendChild(node)

		return wrap
	},

	_wrap_pos         : function(wrap)
	{
		//?: {has class position}
		if(!ZeTS.ises(this.opts.insertClass))
		{
			var opts = { class: this.opts.insertClass };
			ZeT.Layout.Treeters.findNodeByClass.proc(wrap, opts)

			return ZeT.assertn(opts.result, 'Not found any node of CSS class [',
			  this.opts.insertClass, '] to wrap!')
		}

		//?: {has template way}
		if(this.opts.template && this.opts.insertWay)
		{
			var template = ZeT.Layout.template(this.opts.template);
			return ZeT.assertn(template.walk(this.opts.insertWay, wrap),
			  'Can not walk the way [', this.opts.insertWay, ']!')
		}

		return wrap
	}
})


// +----: ZeT.Layout.Proc.Append :-------------------------------+

/**
 * Appends the processed node to the 'parent'
 * node defined in the options as a string ID,
 * or as a DOM element.
 *
 * Note that the default parent is body!
 */
ZeT.Layout.Proc.Append = ZeT.defineClass('ZeT.Layout.Proc.Append', {

	init: function(opts)
	{
		this.opts = opts || {};
	},

	proc: function(node)
	{
		if(!ZeTD.isxn(node))
			throw 'Not a DOM node in the processing pipe!'

		//~: find the parent to insert
		var parent = this.opts.parent;

		if(ZeT.isu(parent))
			parent = document.body
		else if(ZeT.iss(parent))
			parent = ZeTD.n(parent)

		if(ZeTD.isn(parent))
		{
			//?: {node has parent} remove it
			if(ZeTD.isn(node.parentNode))
				node.parentNode.removeChild(node)

			//!: append to the new parent
			parent.appendChild(node)
		}

		return node
	}
})