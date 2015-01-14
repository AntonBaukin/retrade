/*===============================================================+
 |                                                     desktop   |
 |   Desktop Support Scripts for ReTrade                         |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


// +----: ReTrade Namespace :------------------------------------+

var ReTrade = ZeT.define('ReTrade', {})



// +----: Desktop :----------------------------------------------+

ReTrade.Desktop = ZeT.defineClass('ReTrade.Desktop', {

	/**
	 * The system panels (Binds) of the desktop created
	 * on the page load. Available for read only.
	 */
	panels            : {},

	init              : function(opts)
	{
		//~: set collapsing strategy
		this.collapsing = (opts || {}).collapsing ||
		  ZeT.createInstance('ReTrade.DesktopCollapsing');
		this.collapsing.desktop(this)
	},

	/**
	 * Displays notification box with the given DOM
	 * node (or message string).
	 */
	event             : function(node, opts)
	{
		var e = this._event; if(!e) this._event = e = {
		  zindex: 99999
		}

		var xopts = {
		  zIndex: e.zindex--,
		  defaultContentTag: 'div',
		  defaultContentClass: 'retrade-desktop-event-content',
		  borderClassNames: 'retrade-bofull-pale-XYZ',
		  eventFrameClassNames: 'retrade-desktop-event retrade-border-free-size',
		  'top%': 0.1,
		  moveDuration: 2000,
		  moveEasing: 'easeOut',
		  eventVisibleTime: 10000,
		  autoRemove: true,
		  clickHandler: null, // function(Ext.Element, Ext.fx.Anim): true to call default
		  skipDefaultClick: false
		}

		ZeT.extend(xopts, opts)

		if(ZeT.iss(node))
		{
			var n = document.createElement(xopts.defaultContentTag);
			n.innerHTML = Ext.String.htmlEncode(node);
			ZeTD.classes(n, xopts.defaultContentClass)
			node = n;
		}

		if(!ZeTD.isn(node)) throw 'ReTrade.Desktop.event() got not a DOM node!';

		//~: pipe the node: wrap into border
		node = Ext.get(ZeT.Layout.procPipeCall(

		  ZeT.Layout.Proc.Node, { node: node },

		  ZeT.Border.Full, ZeT.Border.full({ classes: xopts.borderClassNames }),

		  ZeT.Layout.Proc.Wrap, { html: '<div/>',
		    classes: xopts.eventFrameClassNames,
		    styles: { display: 'none', zIndex: xopts.zIndex }
		  }
		));

		//~: append the node and show it
		Ext.getBody().appendChild(node)
		node.setStyle({top: -9999}).show()

		var moveto = !ZeT.isu(xopts.top)?(xopts.top):
		  Math.ceil(Ext.getBody().getHeight() * xopts['top%']);

		var anim = new Ext.fx.Anim({
		  target: new Ext.fx.target.Element(node),
		  duration: xopts.moveDuration, easing: xopts.moveEasing,
		  from: { top: -node.getHeight() }, to: { top: moveto },
		  callback: ZeT.timeout(xopts.eventVisibleTime, function()
		  {
		    if(node.retradeEventClicked) return;
		    if(xopts.autoRemove) node.fadeOut({ duration: 2000, remove: true })
		  })
		})

		node.on('click', function()
		{
			var x = true;

			if(ZeT.isf(xopts.clickHandler))
				x = xopts.clickHandler(node, anim)

			if((x === true) && !xopts.skipDefaultClick)
				if(node.retradeEventClicked) new Ext.fx.Anim({
				  target: new Ext.fx.target.Element(node),
				  duration: 1000, easing: xopts.moveEasing,
				  to: { opacity: 0, top: node.getTop() + 100 },
				  callback: function(){ node.remove() }})
				else
				{
					anim.end()
					node.retradeEventClicked = true;
				}
		})

		return { element: node, animation: anim };
	},

	error             : function(node, opts)
	{
		var xopts = {

		  borderClassNames: 'retrade-bofull-brick-XYZ',

		  defaultContentClass: [
		    'retrade-desktop-event-content',
		    'retrade-desktop-event-error-content'
		  ]
		};

		ZeT.extend(xopts, opts)
		return this.event(node, xopts);
	},

	/**
	 * Returns the controller of the panel defined
	 * by it's bind or the position.
	 */
	controller        : function(panel)
	{
		if(ZeT.iss(panel)) panel = this.panels[panel];
		if(!panel || (panel.extjsfBind !== true))
			throw 'Not a panel argument!';
		return panel.desktopPanelController;
	},

	/**
	 * Registers or returns the controller of root-panel
	 * inserted into the desktop panel by the given position.
	 */
	rootController    : function(position, controller)
	{
		var rc = this._root_controllers;

		if(ZeTS.ises(position)) throw 'Not a position!';
		if(!rc) this._root_controllers = rc = {};

		if(ZeT.isu(controller)) return rc[position];
		if(controller === null) delete rc[position];
		else
		{
			if(rc[position] && (rc[position] !== controller))
				throw 'Can not replace root controller in position [' +
				  position + '] as there is other instance registered!';

			rc[position] = controller;
		}

		return this;
	},

	rootPanel         : function(bind, domain)
	{
		if(!bind) return this._root_panel;
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain);
		this._root_panel = bind;
		return this;
	},

	bindPanel         : function(pos, bind, domain)
	{
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain);

		//~: register the bind of the panel
		this.panels[pos] = bind;

		//~: assign the panel controller
		this._bind_control(bind)

		//~: hook the component creation
		bind.on('added', ZeT.fbind(this._on_panel_added, this, pos))
	},

	loadDesktopPanel  : function(url, opts)
	{
		var pos    = (opts = opts || {}).position;
		if(!pos) pos = 'center';

		var panel  = this.controller(pos).contentPanel();
		panel = panel && panel.component();
		if(!panel) throw 'Can not load root panel without the component!'
		panel.removeAll(true) //<-- clean the component

		//~: define the domain
		var domain = opts.domain;
		if(!ZeT.iss(domain)) domain = extjsf.tempDomain('desktop:root-panel:');

		//~: define the method
		var method = 'GET';
		if(ZeT.iss(opts.method)) method = opts.method;

		//~: create the parameters
		var params = opts.params || {};
		ZeT.delayedProp(params) //<-- resolve delayed parameters

		//~: set the domain parameter
		params.extjs_domain = domain;

		//~: set the view id parameter
		params.view = ZeT.iss(opts.view)?(opts.view):(extjsf.genViewId());

		//~: set the view mode parameter (defaults to body)
		params.mode = ZeT.iss(opts.mode)?(opts.mode):('body');

		//~: set the position parameter
		params.extjs_desktop_position = pos;

		//!: load the content
		Ext.create('Ext.ComponentLoader', {

		  'url': url, 'params': params,
		  target: panel, autoLoad: true, scripts: true,
		  ajaxOptions: {'method': method}
		})
	},

	swapPanels        : function(one, two)
	{
		var cone = this.rootController(one);
		var ctwo = this.rootController(two);

		//~: temporarily remove the contents of the panels
		if(cone) cone.remove(false)
		if(ctwo) ctwo.remove(false)

		//~: swap the positions
		if(cone) cone.position(two)
		if(ctwo) ctwo.position(one)

		//~: insert the contents back
		if(cone) cone.insert()
		if(ctwo) ctwo.insert()

		return this;
	},

	calcWindowBox     : function(opts)
	{
		var r = {x: 0, y: 0, width: extjsf.pt(480), height: extjsf.pt(360)}

		this._size_pt(opts)
		if(opts.width)  r.width  = opts.width;
		if(opts.height) r.height = opts.height;

		//~: x
		if(!ZeT.isu(opts.x)) r.x = opts.x;
		else if(opts.event) r.x = opts.event.getX();

		var x = r.x, max = document.body.offsetWidth;

		if(opts['+x']) x += opts['+x'];
		else if(opts['+xpt']) x += extjsf.pt(opts['+xpt']);
		if(x + r.width > max) x = max - r.width;
		if(x >= 0) r.x = x;


		//~: y
		if(!ZeT.isu(opts.y)) r.y = opts.y;
		else if(opts.event) r.y = opts.event.getY();

		var y = r.y, may = document.body.offsetHeight;

		if(opts['-y']) y -= opts['-y'];
		else if(opts['-ypt']) y -= extjsf.pt(opts['-ypt']);
		if(opts['-height']) y -= opts.height;

		if(y < 0) y = 0;
		if(y + r.height > may) y = may - r.height;
		if(y < 0) y = 0;
		r.y = y;

		return r;
	},

	expandSizeMin     : function(opts)
	{
		var s, x = {}, comp = ZeT.assertn(extjsf.component(opts));

		this._size_pt(opts)

		if(opts.width && opts.height)
			s = comp.getSize();
		else if(opts.width)
			s = { width: comp.getWidth() }
		else if(opts.height)
			s = { height: comp.getHeight() }

		comp.retradePrevSize = s;

		if(opts.width && (opts.width <= s.width))
			delete opts.width;

		if(opts.height && (opts.height <= s.height))
			delete opts.height;

		return this.resizeComp(opts);
	},

	trySqueezeWnd     : function(opts)
	{
		var co = ZeT.assertn(extjsf.component(opts));

		var bw = document.body.offsetWidth  - 2;
		var bh = document.body.offsetHeight - 2;

		var ww = co.getBox();
		var wx = (ww.x + ww.width  <= bw)?(ww.x):(bw - ww.width);
		var wy = (ww.y + ww.height <= bh)?(ww.y):(bh - ww.height);

		co.setPagePosition(wx, wy)
		return this;
	},

	prevsizeComp      : function(opts)
	{
		var s, x = {}, comp = extjsf.component(opts);
		if(!comp || !comp.retradePrevSize) return;

		comp.retradePrevSize.component = comp;
		this.resizeComp(comp.retradePrevSize)
		delete comp.retradePrevSize;

		return this;
	},

	resizeComp        : function(opts)
	{
		var comp = extjsf.component(opts);
		if(!comp) return;

		this._size_pt(opts)

		if(opts.width && opts.height)
			comp.setSize(opts.width, opts.height)
		else if(opts.width)
			comp.setWidth(opts.width)
		else if(opts.height)
			comp.setHeight(opts.height)

		return this;
	},

	/**
	 * Ready point is an extension point added to the desktop
	 * to allow various nested components to tell the desktop
	 * are they ready or not. When all the points are ready,
	 * desktop fires ready event. This allows client code
	 * to activate when all complex layout is ready.
	 *
	 * Note that the name of the point must be unique.
	 * Ready argument must be a boolean, or undefined.
	 */
	readyPoint        : function(name, ready)
	{
		ZeT.assert(ZeT.isu(ready) || ZeT.isb(ready))

		//~: add the point if it absents
		if(!this._ready_points) this._ready_points = {};
		var rp = this._ready_points[name];
		if(rp) rp.ready = !!ready;
		else this._ready_points[name] = { 'name': name, 'ready': ready };

		//~: inspect whether all are ready
		var keys = ZeT.keys(this._ready_points);
		this._ready_go = true;
		for(var i = 0;(i < keys.length);i++)
			if(!this._ready_points[keys[i]].ready)
			{ this._ready_go = false; break; }

		//?: {all are ready} go!
		if(this._ready_go && this._ready_fs)
		{
			var fs = this._ready_fs;
			this._ready_fs = null; //<-- !: clear ready callbacks list

			for(i = 0;(i < fs.length);i++)
				fs[i]()
		}

		return this;
	},

	onReady           : function(f)
	{
		if(this._ready_go !== false) { f(); return this; }

		if(!this._ready_fs) this._ready_fs = [];
		this._ready_fs.push(f)

		return this;
	},

	_size_pt          : function(opts)
	{
		if(opts.widthpt)
		{
			opts.width = extjsf.pt(opts.widthpt);
			delete opts.widthpt;
		}

		if(opts.heightpt)
		{
			opts.height = extjsf.pt(opts.heightpt);
			delete opts.heightpt;
		}
	},

	_on_panel_added   : function(pos, panel)
	{
		this.collapsing.onPanelAdded(pos, panel)
	},

	_bind_control     : function(panel, pos)
	{
		if(!panel.desktopPanelController)
			panel.desktopPanelController = ZeT.createInstance(
			  'ReTrade.PanelController', {position: pos});
	}
})


// +----: Desktop Panel Controller :------------------------------+

/**
 * Controller of the desktop panel. Assigned to the
 * panel's bind by 'desktopPanelController' attribute.
 */
ZeT.defineClass('ReTrade.PanelController', {

	init              : function(opts)
	{
		this.opts = opts || {};
	},

	/**
	 * Bind of the main controls area of the panel.
	 */
	mainTopbar        : function(bind, domain)
	{
		if(!bind) return this._main_topbar;
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain);
		this._main_topbar = bind;
		return this;
	},

	/**
	 * Extension point to add root panel controls
	 * onto the top bar.
	 */
	mainTopbarExt     : function(bind, domain)
	{
		if(!bind) return this._main_topbar_ext;
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain);
		this._main_topbar_ext = bind;
		return this;
	},

	/**
	 * The panel to insert the content components of the panel.
	 * It usually has fit layout.
	 */
	contentPanel      : function(bind, domain)
	{
		if(!bind) return this._content_panel;
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain);
		this._content_panel = bind;
		return this;
	},

	position          : function(pos)
	{
		if(!ZeT.iss(pos)) return this.opts.position;
		this.opts.position = pos;
		return this;
	}
})


// +----: Desktop Root Panel :-----------------------------------+

/**
 * Each root panel' Bind has has an instance of this class
 * as 'rootPanelController' property. It is to control the
 * behaviour of the panel on the desktop.
 */
ZeT.defineClass('ReTrade.DesktopRootPanelController', {

	init              : function(opts)
	{
		if(!opts) throw 'No options!'; this.opts = opts;

		//?: domain
		this.domain()

		//?: desktop
		this.desktop()

		//?: the root-panel bind
		this.bind()

		//?: the position
		this.position()

		opts.bind.rootPanelController = this;
		opts.bind.on('beforedestroy', ZeT.fbind(this._on_destroy, this))
	},

	domain            : function()
	{
		if(!ZeT.iss(this.opts.domain)) throw 'No Domain name specified!';
		return this.opts.domain;
	},

	desktop           : function()
	{
		if(!this.opts.desktop) throw 'No Desktop instance!';
		return this.opts.desktop;
	},

	panelController   : function()
	{
		var res = this.desktop().controller(this.opts.position);

		if(!res) throw 'No Desktop panel controller at the position [' +
		  this.opts.position + ']!';
		return res;
	},

	bind              : function()
	{
		if(!this.opts.bind) throw 'No root-panel Bind instance!';
		return this.opts.bind;
	},

	position          : function(pos)
	{
		if(ZeTS.ises(pos))
		{
			//?: {the position key is undefined}
			if(ZeTS.ises(this.opts.position))
				throw 'No Desktop position key!';

			return this.opts.position;
		}

		//~: there is no panel controller at that position
		if(!this.desktop().controller(pos))
			throw 'No Desktop panel controller at the position [' + pos + ']!';

		this.opts.position = pos;
		return this;
	},

	topbarItems       : function(items)
	{
		if(!items) return this._topbar_items;
		this._topbar_items = items;
		return this;
	},

	toolbar           : function(bind)
	{
		if(!bind) return this._toolbar;
		this._toolbar = bind;
		return this;
	},

	statusbar         : function(bind)
	{
		if(!bind) return this._statusbar;
		this._statusbar = bind;
		return this;
	},

	destroy           : function()
	{
		this.remove()

		//?: {the domain must be destroyed}
		if(this.opts['domainOwner'])
			extjsf.deleteDomain(this.domain())

		return this;
	},

	/**
	 * Inserts the controls of this panel into the Desktop.
	 * The panel to insert is defined by the position.
	 */
	insert            : function()
	{
		//~: register this controller
		this._register()

		//~: set the tools of root-panel
		this._set_tools()

		//~: insert the top bar controls
		this._insert_topbar()

		//~: insert the main content
		this._insert_content()
	},

	remove            : function(destroy)
	{
		//~: remove the main content
		this._remove_content(destroy)

		//~: remove the top bar controls
		this._remove_topbar(destroy)

		//~: clear registration of this controller
		this._unregister()
	},

	_register         : function()
	{
		this.desktop().rootController(this.position(), this)
	},

	_unregister       : function()
	{
		this.desktop().rootController(this.position(), null)
	},

	_set_tools        : function()
	{
		if(this.opts['notools'] === true) return;
		if(this._tools_are_set) return;
		this._tools_are_set = true;

		var tools = this.bind().extjsPropsRaw().tools;
		if(!ZeT.isa(tools)) tools = [];

		//~: add the tools of the panel
		this._add_tools(tools)

		if(tools.length)
			this.bind().extjsProps({'tools': tools})
	},

	_add_tools        : function(tools)
	{
		//~: add panel move left-right tools
		this._add_move_tools(tools)
	},

	_add_move_tools   : function(tools)
	{
		if(this.opts['nomove'] === true) return;

		//~: add <<
		tools.push({ xtype: 'tool', type: 'left',
		  handler: ZeT.fbind(this._move_left, this),
		  margin: extjsf.pts(0, 8, 0, 2)
		})

		var m = extjsf.pts(0, 0, 0, 2)
		if(this.bind().extjsPropsRaw()['closable'])
			m = extjsf.pts(0, 8, 0, 2);

		//~: add >>
		tools.push({ xtype: 'tool', type: 'right',
		  handler: ZeT.fbind(this._move_right, this), margin: m
		})
	},

	_insert_content   : function()
	{
		var cnt = this.panelController().contentPanel();
		if(!cnt || !cnt.component()) return; //<-- no content panel

		//?: the bind component is not created yet
		if(!this.bind().component())
		{
			//~: create the root panel component
			this.bind().component(Ext.ComponentManager.create(
			  this.bind().extjsProps()))

			//?: {has toolbar} dock it
			if(this.toolbar()) this.bind().component().
			  addDocked(this.toolbar().extjsProps())
			//?: {has status bar} dock it
			if(this.statusbar()) this.bind().component().
			  addDocked(this.statusbar().extjsProps())
		}

		//~: add the component
		cnt.component().add(this.bind().component())

		//~: add the removed content nodes
		var nodes = this._removed_content_nodes; if(ZeT.isa(nodes))
		{
			var body = cnt.component().getEl().down('.retrade-desktop-panel-content');
			if(!body) throw 'No desktop panel content element found!';

			for(var i = 0;(i < nodes.length);i++)
				body.dom.appendChild(nodes[i])

			delete this._removed_content_nodes; //<-- to not add them further
		}
	},

	_remove_content   : function(destroy)
	{
		var cnt = this.panelController().contentPanel();
		if(!cnt || !cnt.component()) return; //<-- no content panel

		//HINT: the component is automatically removed on destroy event
		if(!this._on_destruction)
			cnt.component().remove(this.bind().component(), destroy)

		if(destroy) this.bind().component(null) //<-- remove reference

		//~: remove plain DOM nodes left in the content panel
		var body = cnt.component().getEl().down('.retrade-desktop-panel-content');
		if(!body) throw 'No desktop panel content element found!';

		//~: collect all the child nodes
		var node = body.dom.firstChild, nodes = [];
		while(node)
		{
			nodes.push(node)
			var next = node.nextSibling;
			body.dom.removeChild(node)
			node = next;
		}

		if(!destroy) this._removed_content_nodes = nodes;
	},

	_insert_topbar    : function()
	{
		var ext  = this.panelController().mainTopbarExt();
		if(!ext || !ext.component()) return; //<-- no top bar to insert

		var tbis = this._topbar_items; if(!ZeT.isa(tbis)) return;

		for(var i = 0;(i < tbis.length);i++)
		{
			//?: {the bind has no component already created} create it now
			if(!tbis[i].component()) tbis[i].component(
			   Ext.ComponentManager.create(tbis[i].extjsProps()))

			ext.component().add(tbis[i].component());
		}
	},

	_remove_topbar    : function(destroy)
	{
		var ext  = this.panelController().mainTopbarExt();
		if(!ext || !ext.component()) return; //<-- no top bar to insert

		var tbis = this._topbar_items; if(!ZeT.isa(tbis)) return;

		for(var i = 0;(i < tbis.length);i++)
		{
			ext.component().remove(tbis[i].component(), destroy)
			if(destroy) tbis[i].component(null) //<-- remove reference
		}
	},

	_move_left        : function()
	{
		var prv, cur = this.position();

		if(cur === 'left')   prv = 'right';
		if(cur === 'center') prv = 'left';
		if(cur === 'right')  prv = 'center';

		this.desktop().swapPanels(prv, cur)
	},

	_move_right       : function()
	{
		var nxt, cur = this.position();

		if(cur === 'left')   nxt = 'center';
		if(cur === 'center') nxt = 'right';
		if(cur === 'right')  nxt = 'left';

		this.desktop().swapPanels(cur, nxt)
	},

	_on_destroy       : function()
	{
		this._on_destruction = true;
		this.destroy()
	}
})


// +----: Desktop Collapsing :------------------------------------+

/**
 * Strategy that handles collapsing of the desktop panels.
 */
ZeT.defineClass('ReTrade.DesktopCollapsing', {

	init              : function(opts)
	{
		this._structs = {};
	},

	desktop           : function(desktop)
	{
		if(!desktop) return this.desktop;
		this.desktop = desktop;
		return this;
	},

	onPanelAdded      : function(pos, panel)
	{
		var self = this;

		panel.on({
			beforecollapse : ZeT.fbind(self._before_collapse, self, pos),
			collapse       : ZeT.fbind(self._after_collapse, self, pos),
			beforeexpand   : ZeT.fbind(self._before_expand, self, pos),
			expand         : ZeT.fbind(self._after_expand, self, pos),
			afterlayout    : ZeT.fbind(self._after_layout, self, pos)
		})
	},

	_changed          : function(pos, before)
	{
		if(pos == 'center') return;

		var center = this.desktop.panels.center;
		if(center) center = center.component();
		if(!center) return;

		var panel  = this.desktop.panels[pos];
		if(panel) panel = panel.component();
		if(!panel) return;

		if(before)
		{
			var colnum = this._cnt_collapsed();
			center.flex = (colnum === 0)?(0):(1);
		}

		if(!before && (this._get(pos).collapsed === false))
			this._get(pos).setOriginalWidth = true;
	},

	_get              : function(pos)
	{
		var res = this._structs[pos];
		if(!res) this._structs[pos] = res = {};
		return res;
	},

	_before_collapse  : function(pos, panel)
	{
		this._get(pos).collapsed = true;
		this._get(pos).originalWidth = panel.getWidth();
		this._changed(pos, true)
	},

	_after_collapse   : function(pos, panel)
	{
		this._changed(pos, false)
		this._update_collapsed(pos, panel)
	},

	_before_expand    : function(pos, panel)
	{
		this._get(pos).collapsed = false;
		this._revert_collapsed(pos, panel)
		this._changed(pos, true)
	},

	_after_expand     : function(pos, panel)
	{
		this._changed(pos, false)
	},

	_after_layout     : function(pos, panel)
	{
		if(this._get(pos).setOriginalWidth === true)
		{
			this._get(pos).setOriginalWidth = false;
			panel.setWidth(this._get(pos).originalWidth)
		}
	},

	_update_collapsed : function(pos, panel)
	{
		if(pos == 'center') return;

		var el = panel.getEl(); if(!el) return;
		var uc = this._get(pos)._update_collapsed = {};
		uc.styles = {};

		el.select('div').each(function(div)
		{
			var id = div.getAttribute('id'); if(!id) return;
			uc.styles[id] = {cursor: div.getStyle('cursor')};
			div.setStyle({cursor: 'pointer'})
		})

		uc.onclick = function()
		{
			panel.expand()
		}

		el.addListener('click', uc.onclick)
	},

	_revert_collapsed : function(pos, panel)
	{
		if(pos == 'center') return;

		var el = panel.getEl(); if(!el) return;
		var uc = this._get(pos)._update_collapsed;
		if(!uc) return; delete this._get(pos)._update_collapsed;

		el.select('div').each(function(div)
		{
			var id = div.getAttribute('id'); if(!id) return;
			var st = uc.styles[id]; if(!st) return;

			div.setStyle(st)
		})

		el.removeListener('click', uc.onclick)
	},

	_cnt_collapsed    : function()
	{
		var res = 0, strs = this._structs;
		var kys = ZeT.keys(strs);

		for(var i = 0;(i < kys.length);i++)
			if(strs[kys[i]].collapsed === true) res++;
		return res;
	}
})


// +----: Desktop Instance :-------------------------------------+

ReTrade.desktop = ZeT.defineInstance('ReTrade.desktop', ReTrade.Desktop);


// +----: SelSet :-----------------------------------------------+


/**
 * Selection Set implementation class.
 */
ReTrade.SelSet = ZeT.defineClass('ReTrade.SelSet', {

	init              : function(opts)
	{},

	/**
	 * ExtJSF domain where the selection set is created.
	 */
	domain            : function(domain)
	{
		if(!ZeT.iss(domain))
			return this._domain;
		this._domain = domain;
		return this;
	},

	/**
	 * ReTrade view id where the selection set is created.
	 */
	view              : function(view)
	{
		if(ZeTS.ises(view))
			return this._view;
		this._view = view;
		return this;
	},

	model             : function(model)
	{
		if(ZeTS.ises(model))
			return this._model;
		this._model = model;
		return this;
	},

	/**
	 * ExtJS Store ID of the store with SelSet model.
	 */
	storeId           : function(storeId)
	{
		if(ZeTS.ises(storeId))
			return this._storeId;
		this._storeId = storeId;
		return this;
	},

	reloadStore       : function()
	{
		if(!ZeTS.ises(this._storeId))
			Ext.data.StoreManager.lookup(this._storeId).reload()
	},

	/**
	 * Returns or sets the Bind of component that
	 * would contain the split button of the SelSet UI.
	 */
	place             : function(bind)
	{
		if(!bind || !bind.extjsfBind)
			return this._place;
		this._place = bind;
		return this;
	},

	/**
	 * Loads Selection Set controls into the place.
	 */
	loadPlace         : function(url)
	{
		if(!this.place() || !this.place().component())
			throw 'SelSet place is not specified or not built yet!'

		var self = this;

		Ext.create('Ext.ComponentLoader', {

		  'url': url, target: this.place().component(),
		  autoLoad: true, scripts: true,
		  ajaxOptions: { method: 'GET' }, params: {
		    mode: 'body', view: self.view(),
		    extjs_domain: self.domain()
		  }
		})
	},

	toggleButton      : function(bind)
	{
		if(ZeT.iss(bind))
			bind = extjsf.bind(bind, this.domain());

		if(!bind || !bind.extjsfBind)
			return this._toggle_btn && this._toggle_btn.component();

		this._toggle_btn = bind;
		return this;
	},

	menu              : function(bind)
	{
		if(ZeT.iss(bind))
			bind = extjsf.bind(bind, this.domain());

		if(!bind || !bind.extjsfBind)
			return this._main_menu && this._main_menu.component();

		this._main_menu = bind;
		return this;
	},

	buildMenu         : function(model)
	{
		if(!this.menu()) throw 'Selection Set UI menu is not defined!'

		var item, items = this._menu_items;

		if(!items) this._menu_items = items = [];
		this._menu_model = model;

		//~: add or rename menu items
		this._current_menu_item = 0;
		for(var i = 0;(i < model.length);i++)
		{
			//?: {has menu item for this index}
			if(i < items.length)
				item = items[i];
			else
			{
				var vid  = this.view() + '_selset_menu_item' + i;
				item = extjsf.defineBind(vid, this.domain());
				items.push(item)

				//~: bind click listener
				item.on('click', ZeT.fbind(this._menu_item_click, this, i))

				//~: create item component
				item.component(Ext.create('Ext.menu.CheckItem', item.extjsProps()))

				//~: add it to the menu
				this.menu().add(item.component())
			}

			//~: selection set name (or title for default set)
			item.component().setText(model[i].title || model[i].name)

			//~: current status
			if(model[i].current)
			{
				this._current_menu_item = i;
				this.selset = model[i].name;
			}

			item.component().setChecked(model[i].current, true)
		}

		//~: show-hide items
		for(i = 0;(i < items.length);i++)
			if(i < model.length)
				items[i].component().show()
			else
				items[i].component().hide()

		return this;
	},

	url               : function(what, url)
	{
		if(ZeTS.ises(what)) return undefined;
		if(!this._urls) this._urls = {};

		if(!ZeTS.ises(url))
		{
			this._urls[what] = url;
			return this;
		}

		url = this._urls[what];
		if(ZeTS.ises(url))
			throw 'No [' + what + '] URL is defined for Selection Set!'
		return url;
	},

	toggle            : function(opts)
	{
		if(!opts) opts = {};

		var act = opts.active;
		var btn = this.toggleButton();

		if(ZeT.isu(act) && btn)
			act = btn.pressed;
		this.active = act;

		if(btn && (btn.pressed != act))
			btn.toggle(act, true)

		if(act) this._open_wnd(opts)
		else    this._close_wnd(opts)

		this._onoff()
		return this;
	},

	/**
	 * Adds on-off listener. To remove it,
	 * set second argument true.
	 */
	onoff            : function(f, remove)
	{
		if(!ZeT.isf(f)) return undefined;
		if(!this._ons) this._ons = [];

		ZeTA.remove(this._ons, f)
		if(remove !== true)
			this._ons.push(f)
		return this;
	},

	adder             : function(f)
	{
		if(ZeT.isf(f)) this._adder = f;
		return this;
	},

	changer           : function(f)
	{
		if(ZeT.isf(f)) this._changer = f;
		return this;
	},

	/**
	 * Binds the grid given to the selection set.
	 * Inserts action column on the grid ready.
	 * This column is shown on selection set activation.
	 */
	grid              : function(bind, domain)
	{
		if(ZeT.iss(bind) && ZeT.iss(domain))
			bind = extjsf.bind(bind, domain)
		if(!bind || !bind.extjsfBind) return;

		var self = this, acol, onoff;

		bind.on('added', function()
		{
			this.headerCt.insert(0, acol = Ext.create('Ext.ux.column.ActionCell', {
			  width: 28, flex:0, align: 'center', autoShow: false,
			  hidden: !self.active, hideable: false, draggable: false,
			  renderer: function(v, md, rec)
			  {
			     self._sel_col_rnd(this, rec)
			  },
			  resizable: false, menuDisabled: true, items: [{
			    iconCls: 'retrade-selcol-add',
			    handler: ZeT.fbind(self._add_object, self)
			  }]
			}))
		})

		this.onoff(onoff = function(ison)
		{
			bind.selSetActive = ison;

			if(!ison) acol.hide(); else
			{
				acol.show()
				bind.component().getView().refresh()
			}
		})

		bind.on('beforedestroy', function()
		{
			self.onoff(onoff, true)
		})
	},

	items             : function(items, op)
	{
		if(!ZeT.isa(items))
			return this._items;

		if(ZeT.isu(op))
		{
			this._items = {};
			op = true;
		}

		if(!this._items) this._items = {};

		for(var i = 0;(i < items.length);i++)
			if(op) this._items[items[i]] = true;
			else delete this._items[items[i]];

		this._onoff()
		return this;
	},

	winmain           : function()
	{
		//?: {has no bind attached}
		if(!this._winmain)
			this._winmain = extjsf.bind('winmain-selset', this.domain());

		return this._winmain;
	},

	reload            : function(opts)
	{
		var self    = this;
		var winmain = this.winmain();
		var window  = winmain && winmain.component(); if(!window) return;

		//~: clear the window
		winmain.clearComponent({notListeners: true})

		var params = {
		  mode: 'body', domain: self.domain(),
		  view: self.view(), model: self.model()
		};

		//!: reload content
		Ext.create('Ext.ComponentLoader', {
		  target: window, url: self.url('winmain'),
		  ajaxOptions: { method: 'GET' },
		  params: params, autoLoad: true, scripts: true
		})
	},

	_add_object       : function()
	{
		var m  = arguments[5];
		var id = m.get('selsetItemKey') || m.getId();

		if(!m.get('selsetDisabled'))
			this._adder(id, (this._items && (this._items[id] === true)))
	},

	_open_wnd         : function(opts)
	{
		var winmain = this.winmain();
		if(!winmain) this._winmain = winmain =
		  ZeT.assert(this._create_wnd(opts));

		//!: load selection set window in the same (root) domain
		if(winmain.component())
			return winmain.component().toFront()

		//~: create window & load the content
		winmain.component(Ext.create('Ext.window.Window', winmain.extjsProps()))

		//~: set the window position
		if(this._win_has_no_xy)
		{
			winmain.component().alignTo(document.body, 'r-r')
			this._win_has_no_xy = false;
			this._win_on_right  = true;
		}
	},

	_close_wnd        : function(opts)
	{
		if(!this._winmain) return;
		var winmain = this._winmain;
		delete this._winmain

		if(winmain.component())
			winmain.component().close()

		if(this._on_resize_)
		{
			Ext.get(window).removeListener('resize', this._on_resize_)
			delete this._on_resize_
		}
	},

	_create_wnd       : function(opts)
	{
		var self = this;

		//~: window size
		if(!self._win_wh) self._win_wh = [extjsf.pt(400), extjsf.pt(250)];

		var params = {
			mode: 'body', domain: self.domain(),
			view: self.view(), model: self.model()
		};

		if(opts && opts.params) ZeT.extend(params, opts.params)

		var props = {
			xtype: 'window', title: 'Загрузка выборки...',
			width: self._win_wh[0], height: self._win_wh[1],
			layout: 'fit', collapsible: false, autoShow: true,
			cls: 'retrade-selset-window', style: { opacity: 0.95 },

			loader: {
				url: self.url('winmain'), ajaxOptions: {method: 'GET'},
				autoLoad: true, scripts: true, params: params
			}
		};

		//?: {has position}
		if(!self._win_xy) self._win_has_no_xy = true;
		else
		{
			props.x = self._win_xy[0];
			props.y = self._win_xy[1];

			//?: {position is out of frame}
			if((props.x + 10 > document.body.offsetWidth) ||
			   (props.y + 10 > document.body.offsetHeight))
			{
				delete props.x;
				delete props.y
				delete self._win_xy
				self._win_has_no_xy = true;
			}
		}

		//~: define the window bind
		var winmain = extjsf.defineBind('winmain-selset', self.domain()).extjsProps(props)

		//~: window moved
		winmain.on('move', function(win, x0, y1)
		{
			if(self._win_has_no_xy) return;
			self._win_on_right = false;

			//~: stick to the left
			var dd = extjsf.inch(0.5);
			var x1 = (x0 <= dd)?(0):(x0);

			//~: stick to the right
			if(Math.abs(document.body.offsetWidth - x1 - win.getWidth()) <= dd)
			{
				x1 = document.body.offsetWidth - win.getWidth();
				self._win_on_right = true;
			}

			if(x1 != x0) win.setX(x1)
			self._win_xy = [x1, y1];
		})

		//~: window resize
		winmain.on('resize', function(win, w, h)
		{
			if(self._win_has_no_xy) return;
			self._win_wh = [w, h];
		})

		//~: close window listener
		winmain.on('beforeclose', function()
		{
			self.toggle({ active: false, windowClosing: true })
		})

		if(!this._on_resize_)
		{
			this._on_resize_ = ZeT.fbind(this._on_resize, this);
			Ext.get(window).addListener('resize', this._on_resize_)
		}

		return winmain;
	},

	_onoff            : function(ison)
	{
		if(!this._ons) return;
		if(ZeT.isu(ison)) ison = this.active;

		for(var i = 0;(i < this._ons.length);i++)
			this._ons[i].call(window, ison)
	},

	_menu_item_click  : function(index)
	{
		if(!this._menu_model || !this._menu_items) return;
		if(index >= this._menu_model.length) return;

		if(index == this._current_menu_item)
		{
			this._menu_items[index].component().setChecked(true, true)
			return;
		}

		this._menu_items[this._current_menu_item].component().setChecked(false, true)
		this._menu_items[index].component().setChecked(true, true)
		this._current_menu_item = index;
		this.selset = this._menu_model[index].name;

		var self = this;
		this._changer(this._menu_model[index].name, function()
		{
			Ext.data.StoreManager.lookup(self.storeId()).load()
			if(self.winmain()) self.reload()
		})
	},

	_sel_col_rnd      : function(col, rec)
	{
		var id = rec.get('selsetItemKey') || rec.getId();
		id = id && parseInt(id);

		if(!id || rec.get('selsetDisabled'))
		{
			col.items[0].disabled = true;
			col.items[0].iconCls = 'retrade-selcol-none';

			return;
		}

		var on = this._items && (this._items[id] === true);
		if(on) col.items[0].iconCls = 'retrade-selcol-checked';
		else   col.items[0].iconCls = 'retrade-selcol-add';

		col.items[0].disabled = false;
	},

	_on_resize        : function()
	{
		var win = this._winmain && this._winmain.component();
		if(!win) return

		//?: {window pinned to the right}
		if(this._win_on_right)
			win.setX(document.body.offsetWidth - win.getWidth())
	}
})


// +----: SelSet Instance :--------------------------------------+

ReTrade.selset = ZeT.defineInstance('ReTrade.selset', ReTrade.SelSet);
ReTrade.desktop.readyPoint('ReTrade.selset')


// +----: ReTrade Visual :---------------------------------------+

ReTrade.Visual = ZeT.defineClass('ReTrade.Visual', {

	_init_struct      : function(opts)
	{
		opts = this.opts = this.opts || opts || {}

		//~: create node by the template
		var t = this._tx()
		var n = t.cloneNode()

		//?: {has id}
		if(this.opts.node)
			n.id = ZeT.asserts(opts.node)

		//~: create the structure
		this.struct = new ZeT.Struct(n)

		//?: {has parent}
		var p = this.opts.parent;
		if(ZeT.iss(p)) p = ZeTD.n(p)
		if(this.opts.parent)
		{
			ZeT.assert(ZeTD.isn(p))
			p.appendChild(n)
		}
	},

	/**
	 * Creates template by this._ts template string.
	 */
	_tx               : function()
	{
		if(this.static.template)
			return this.static.template

		return this.static.template = new ZeT.Layout.Template(
		  {trace : ZeT.Layout.Template.Ways.traceAtNodes},
		  ZeT.asserts(this._ts, 'Visual template string is required!'))
	}
})


// +----: ReTrade Clocks :---------------------------------------+

ReTrade.Clocks = ZeT.defineClass('ReTrade.Clocks', ReTrade.Visual, {

	init  : function(opts)
	{
		//~: create the structure
		this._init_struct(opts)

		//?: {do start on create}
		if(this.opts.start)
			this.start()
	},

	start : function()
	{
		if(this._timef) return this
		this._ss    = 0
		this._timef = ZeT.fbind(this._time, this)
		this._timei = setInterval(this._timef, 1000)
		return this
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

	_ts   : ""+
		"<div>"+
		"  <div class = 'retrade-clocks-frame'></div>"+
		"  <div>@XH</div><div>@HX</div>"+
		"  <div class = 'retrade-clocks-dots'>@dots</div>"+
		"  <div>@XM</div><div>@MX</div>"+
		"  <div class = 'retrade-clocks-glass'></div>"+
		"</div>"
})


// +----: ReTrade Events Number :--------------------------------+

ReTrade.EventsNumber = ZeT.defineClass('ReTrade.EventsNumber', ReTrade.Visual, {

	LH2W    : 5/35, //<-- left border aspect ratio
	RH2W    : 5/35, //<-- right border aspect ratio

	init    : function(opts)
	{
		//~: create the structure
		this._init_struct(opts)

		if(!ZeT.isn(this.opts.lh2w))
			this.opts.lh2w = this.LH2W
		if(!ZeT.isn(this.opts.rh2w))
			this.opts.rh2w = this.RH2W

		//~: do layout
		this.layout()
	},

	layout : function()
	{
		var t = this._tx()
		var n = this.struct.node()
		var p = ZeTD.uptag(t.walk('L', n), 'table')
		var h = p.offsetHeight; if(!h) return

		ZeTD.styles(t.walk('L', n), { width: '' + Math.floor(h * this.opts.lh2w) + 'px' })
		ZeTD.styles(t.walk('R', n), { width: '' + Math.floor(h * this.opts.rh2w) + 'px' })

		t.walkEach(this.struct.node(), function(div, way)
		{
			if(way != 'N') ZeTD.styles(div, { height: '' + h + 'px' }); else
			{
				var x = ZeTD.uptag(div, 'table')
				ZeTD.styles(x, { top: '-' + h + 'px', height: '' + h + 'px' })
				ZeTD.styles(x.parentNode, { height: '' + h + 'px' })
			}

			if(way == 'NA') ZeTD.styles(div, { top: '-' + 2*h + 'px' })
		})

		return this
	},

	set    : function(v)
	{
		var n = this._tx().walk('N', this.struct.node())

		if(ZeT.isn(v)) v = '' + v
		ZeTD.update(n, v)

		return this
	},

	width  : function()
	{
		return this.struct.node().offsetWidth
	},

	_ts    : ""+
		"<table cellpadding='0' cellspacing='0' border='0'"+
		" class = 'retrade-eventsnum-area'>"+
		"  <tr>"+
		"    <td class = 'retrade-eventsnum-left'>"+
		"      <div>@L</div>"+
		"    </td>"+
		"    <td class='retrade-eventsnum-center'>"+
		"      <div><div>@NB</div><table cellpadding='0' cellspacing='0' border='0'>" +
		         "<tr><td class='retrade-eventsnum-number'>@N"+
		         "</tr></td></table><div>@NA</div></div>"+
		"    </td>"+
		"    <td class='retrade-eventsnum-right'>"+
		"      <div>@R</div>"+
		"    </td>"+
		"  </tr>"+
		"</table>"
})