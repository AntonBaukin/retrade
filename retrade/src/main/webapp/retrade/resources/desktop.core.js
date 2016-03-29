/*===============================================================+
 |                                                     desktop   |
 |   Core Desktop Support Scripts for ExtJSF                     |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


// +----: Desktop :----------------------------------------------+

/**
 * Desktop is a root controller of the entire page built
 * with Ext JS panels that are named regions. Each panel
 * has related top-bar extension panel where buttons or
 * menus are placed depending on the actual content
 * of the region panel.
 *
 * The main (and biggest) working region panel is named
 * as 'center'. It's top-bar has main application menu.
 */
extjsf.Desktop = ZeT.defineClass('extjsf.Desktop',
{
	init             : function(opts)
	{
		this.opts = opts || {}

		//~: the panels of the desktop
		this.panels = {}

		//~: set collapsing strategy
		this.collapsing = this.opts.collapsing ||
		  ZeT.createInstance('extjsf.Desktop.Collapsing')

		//~: bind the desktop with the collapsing startegy
		this.collapsing.desktop(this)
	},

	/**
	 * Returns the controller of the panel defined
	 * by it's bind or the position.
	 */
	controller        : function(/* panel | position */)
	{
		var p = arguments[0]

		//?: {is a position}
		if(ZeT.iss(p)) p = this.panels[p]

		//?: {not a bind}
		ZeT.assert(extjsf.isbind(p))

		return ZeT.assertn(this.$data(p).controller)
	},

	/**
	 * Registers panel Bind in the desktop. Leading
	 * arguments are of extjsf.bind(), the last one
	 * is the panel position string.
	 */
	bindPanel        : function(/* ... position */)
	{
		var b = extjsf.bind.apply(extjsf, arguments)
		var p = arguments[arguments.length - 1]
		ZeT.asserts(p, 'Desktop panel position is empty!')

		//~: register the bind of the panel
		this.panels[p] = b

		//~: assign the panel controller
		this.$bind_controller(b, p)

		//~: hook the component creation
		b.when(ZeT.fbind(this.$on_panel_added, this, b, p))
	},

	/**
	 * Activates the desktop after installing
	 * it's panels. Invoked on Ext-ready.
	 */
	activate         : function()
	{
		var self = this

		ZeT.each(ZeT.keys(this.panels), function(k)
		{
			self.controller(k).triggerVoid()
		})

		return this
	},

	$data            : function(panel)
	{
		var d = panel.$desktop
		return (d)?(d):(panel.$desktop = {})
	},

	$bind_controller : function(panel, pos)
	{
		ZeT.assert(!this.$data(panel).controller)

		//~: create controller instance
		this.$data(panel).controller =
		  this.$new_controller(panel, pos)

		//~: bind the controller to this panel
		this.$data(panel).controller.panel(panel)
	},

	$new_controller  : function(panel, pos)
	{
		return ZeT.createInstance(
		  'extjsf.Desktop.Region', {
		  desktop: this, position: pos
		})
	},

	$on_panel_added  : function(panel, pos)
	{
		this.collapsing.onPanelAdded(panel, pos)
	}
})


// +----: Desktop Collapsing :------------------------------------+

/**
 * Strategy that handles collapsing of a Desktop panels.
 * Note that strategy instance is assigned to a Desktop
 * instance, but not to instance of a panel (Bind).
 * Effectively, this strategy is a Singleton.
 */
ZeT.defineClass('extjsf.Desktop.Collapsing', {

	init             : function(opts)
	{
		this.opts = opts || {}
	},

	desktop          : function(desktop)
	{
		if(!desktop) return this.desktop
		this.desktop = desktop
		return this
	},

	onPanelAdded     : function(panel, position)
	{
		if(!extjsf.isbind(panel))
			panel = extjsf.bind(panel)

		ZeT.assert(extjsf.isbind(panel),
		  'Desktop panel has no ExtJSF Bind!')

		//?: {empty position}
		ZeT.asserts(position,
		  'Desktop panel has no position!')

		this.$bind_panel(panel, position)
	},

	$data            : function(panel)
	{
		if(!panel.$desktop) panel.$desktop = {}
		var d = panel.$desktop.collapsing
		return (d)?(d):(panel.$desktop.collapsing = {})
	},

	$bind_panel      : function(panel, pos)
	{
		var self = this; function mbind(m)
		{
			return ZeT.fbind(m, self, panel)
		}

		//=: place collapsing data in the bind
		ZeT.extend(this.$data(panel), { position: pos,
		  click: mbind(self.$collapsed_click)
		})

		//~: react on desktop panel events
		panel.on({
		  beforecollapse : mbind(self.$before_collapse),
		  collapse       : mbind(self.$after_collapse),
		  beforeexpand   : mbind(self.$before_expand),
		  expand         : mbind(self.$after_expand),
		  afterlayout    : mbind(self.$after_layout)
		})
	},

	$collapsed_click : function(panel)
	{
		panel.co().expand()
	},

	$before_collapse : function(panel)
	{
		var d = this.$data(panel)
		var w = panel.co().getWidth()
		var W = panel.co().ownerCt.getWidth()

		//~: original width is relative
		d.originalWidth = (w / W)
		d.setOriginalWidth = true
	},

	$after_collapse  : function(panel)
	{
		var self = this; ZeT.timeout(500, function()
		{
			var e = panel.co().getEl()
			var c = self.$data(panel).click

			e.removeListener('click', c)
			e.addListener('click', c)
		})
	},

	$before_expand   : function(panel)
	{
		panel.co().getEl().removeListener(
		  'click', this.$data(panel).click)
	},

	$after_expand    : function(panel)
	{},

	$after_layout    : function(panel)
	{
		if(!this.$data(panel).setOriginalWidth)
			return

		var d = this.$data(panel)
		var W = panel.co().ownerCt.getWidth()
		var w = Math.round(W * d.originalWidth)

		//!: prevents infinite recursion
		delete d.setOriginalWidth
		panel.co().setWidth(w)
	}
})


// +----: Desktop Panel Controller :------------------------------+

/**
 * Controller of a Desktop region panel. Instances
 * are created by the Desktop when adding panels.
 */
ZeT.defineClass('extjsf.Desktop.Region',
{
	init             : function(opts)
	{
		ZeT.assert(ZeT.iso(opts))
		this.opts = opts
	},

	/**
	 * Returns or alters the position of the panel.
	 */
	position         : function(pos)
	{
		if(!arguments.length)
			return this.opts.position

		this.opts.position = ZeT.asserts(pos)
		return this
	},

	/**
	 * Assigns or returns Bind of the panel.
	 */
	panel            : function(panel)
	{
		if(!arguments.length)
			return this._root_bind

		ZeT.assert(extjsf.isbind(panel))
		this._panel = panel
		return this
	},

	/**
	 * Assigns or returns Bind of the extension
	 * panel with control buttons and menus.
	 * Arguments are of extjsf.bind().
	 */
	mainTopbar       : function(/* ... */)
	{
		if(!arguments.length)
			return this._main_topbar

		this._main_topbar = ZeT.assertn(
		  extjsf.bind.apply(extjsf, arguments))

		return this
	},

	/**
	 * Binds processor of inserting-removing the main
	 * menu components dependend on the panel contents.
	 * (Extension point for the main menus.)
	 *
	 * Callback function has the following arguments:
	 *  0) inserting flag (true), or removing (false);
	 *  1) callback function of the component that
	 *     implements the required action (inserts,
	 *     or removes).
	 */
	mainMenuProc     : function(f)
	{
		if(ZeT.isu(f))
			return this._main_menu_proc

		this._main_menu_proc = ZeT.assertf(f)
		return this
	},

	/**
	 * Extension point to add root panel controls
	 * onto the top bar. Arguments are of extjsf.bind().
	 */
	mainTopbarExt    : function(/* ... */)
	{
		if(!arguments.length)
			return this._main_topbar_ext

		this._main_topbar_ext = ZeT.assertn(
		  extjsf.bind.apply(extjsf, arguments))

		return this
	},

	/**
	 * The panel to insert the content components
	 * of the panel. It usually has fit layout.
	 * Arguments are of extjsf.bind().
	 */
	contentPanel     : function(/* ... */)
	{
		if(!arguments.length)
			return this._content_panel

		this._content_panel = ZeT.assertn(
		  extjsf.bind.apply(extjsf, arguments))

		return this
	},

	/**
	 * If set, void panel is displayed when
	 * content panel is empty. Arguments are
	 * of extjsf.bind().
	 */
	voidPanel        : function(/* ... */)
	{
		if(!arguments.length)
			return this._void_panel

		this._void_panel = ZeT.assertn(
		  extjsf.bind.apply(extjsf, arguments))

		return this
	},

	/**
	 * Tells whether to show void panel: else panels
	 * are empty and void panel is assigned.
	 */
	isVoid           : function()
	{
		if(!this._void_panel)
			return undefined

		if(!this._void_panel.co())
			return undefined

		if(this._main_topbar)
		{
			var c = this._main_topbar.co()
			if(c && c.items.getCount()) return false
		}

		if(this._main_topbar_ext)
		{
			c = this._main_topbar_ext.co()
			if(c && c.items.getCount()) return false
		}

		if(this._content_panel)
		{
			c = this._content_panel.co()
			if(c && c.items.getCount()) return false
		}

		return true
	},

	/**
	 * Checks whether the panel is void and
	 * hides all content except the void panel.
	 */
	triggerVoid      : function()
	{
		//?: {has no void panel}
		var vo = this.isVoid()
		if(ZeT.isu(vo)) return

		//~: show-hide void panel
		this._void_panel.visible(vo)

		//~: hide-show all else panels
		ZeT.each(this.$all_panes(),
		  function(p) { p && p.visible(!vo) })
	},

	$all_panes       : function()
	{
		return [
		  this._main_topbar,
		  this._main_topbar_ext,
		  this._content_panel
		]
	}
})


// +----: Desktop Root Panel :-----------------------------------+

/**
 * Each root panel' Bind has has an instance of this class
 * as 'desktopPanelController' property. It is to control
 * the behaviour of the panel on the desktop.
 */
ZeT.defineClass('extjsf.Desktop.Panel',
{
	init             : function(opts)
	{
		ZeT.assert(ZeT.iso(opts))
		this.opts = opts

		//~: check domain
		this.domain()

		//~: check desktop
		this.desktop()

		//~: check the root-panel bind
		this.bind()

		//~: check the position
		this.position()

		opts.bind.desktopPanelController = this
	},

	domain           : function()
	{
		return ZeT.asserts(
		  this.opts.domain,
		  'No Domain name specified!'
		)
	},

	desktop          : function()
	{
		return ZeT.assertn(
		  this.opts.desktop,
		  'No Desktop instance!'
		)
	},

	panelController  : function()
	{
		return ZeT.assertn(
		  this.desktop().controller(this.opts.position),
		  'No Desktop panel controller at the position [',
		  this.opts.position, ']!'
		)
	},

	bind             : function()
	{
		return ZeT.assertn(
		  this.opts.bind,
		  'No root-panel Bind instance!'
		)
	},

	position         : function(pos)
	{
		if(ZeTS.ises(pos))
			//?: {the position key is undefined}
			return ZeT.asserts(this.opts.position,
			  'No Desktop position key!')

		//~: there is no panel controller at that position
		ZeT.assertn(this.desktop().controller(pos),
		  'Desktop panel controller at position [',
		   pos, '] was not found!')

		this.opts.position = pos
		return this
	},

	topbarItems      : function(items)
	{
		if(!items) return this._topbar_items
		this._topbar_items = items
		return this
	},

	/**
	 * Takes two forms. First, when the first argument
	 * is boolean: returns insert (true), or remove (false)
	 * strategy of extending the main menu.
	 *
	 * Second, two arguments, both functions: to insert
	 * and to remove the menus. Installed by the root panel.
	 */
	topbarMenu       : function()
	{
		var a0 = arguments[0]

		if(arguments.length == 2)
		{
			var i = a0, r = arguments[1]

			ZeT.assert(ZeT.isf(i) && ZeT.isf(r))
			this._topbar_menu = { insert: i, remove: r }

			return this
		}

		ZeT.assert(arguments.length == 1)
		ZeT.assert(ZeT.isb(a0))

		if(!this._topbar_menu) return undefined
		return this._topbar_menu[(a0)?('insert'):('remove')]
	},

	toolbar          : function(bind)
	{
		if(!bind) return this._toolbar
		this._toolbar = bind
		return this
	},

	status           : function(bind)
	{
		if(!bind) return this._status
		this._status = bind
		return this
	},

	/**
	 * Inserts the controls of this panel into the Desktop.
	 * The panel to insert is defined by the position.
	 */
	insert           : function()
	{
		//~: register this controller
		this.$register()

		//~: set the tools of root-panel
		this.$set_tools()

		//~: insert the top bar controls
		this.$insert_topbar()

		//~: insert the main content
		this.$insert_content()

		//~: hide void panel
		this.panelController().triggerVoid()
	},

	remove           : function(destroy)
	{
		//~: remove the main content
		this.$remove_content(destroy)

		//~: remove the top bar controls
		this.$remove_topbar(destroy)

		//~: clear registration of this controller
		this.$unregister()

		//~: show void panel
		var pc = this.panelController()
		ZeT.timeout(100, ZeT.fbind(pc.triggerVoid, pc))
	},

	$register        : function()
	{
		this.desktop().rootController(this.position(), this)
	},

	$unregister      : function()
	{
		this.desktop().rootController(this.position(), null)
	},

	$set_tools       : function()
	{
		if(this.opts['notools'] === true) return
		if(this._tools_are_set) return
		this._tools_are_set = true

		var tools = this.bind().$raw().tools
		if(!ZeT.isa(tools)) tools = []

		//~: add the tools of the panel
		this.$add_tools(tools)

		if(tools.length)
			this.bind().props({'tools': tools})
	},

	$add_tools       : function(tools)
	{
		//~: add tool to save web link
		this.$add_link_tool(tools)

		//~: add panel move left-right tools
		this.$add_move_tools(tools)
	},

	$add_link_tool   : function(tools)
	{
		if(!ZeT.iso(this.opts.webLink)) return

		tools.push({ xtype: 'tool', cls: 'retrade-web-link-tool',
		  handler: ZeT.fbind(this.$web_link, this),
		  margin: extjsf.pts(0, 8, 0, 2), tooltipType: 'title',
		  tooltip: 'Создать постоянную ссылку на панель'
		})
	},

	$add_move_tools  : function(tools)
	{
		if(this.opts['nomove'] === true) return

		//~: add <<
		tools.push({ xtype: 'tool', type: 'left',
		  handler: ZeT.fbind(this.$move_left, this),
		  margin: extjsf.pts(0, 8, 0, 2), tooltipType: 'title',
		  tooltip: 'Передвинуть панель в левую область'
		})

		var m = extjsf.pts(0, 0, 0, 2)
		if(this.bind().$raw()['closable'])
			m = extjsf.pts(0, 8, 0, 2)

		//~: add >>
		tools.push({ xtype: 'tool', type: 'right',
		  handler: ZeT.fbind(this.$move_right, this),
		  margin: m, tooltipType: 'title',
		  tooltip: 'Передвинуть панель в правую область'
		})
	},

	$insert_content  : function()
	{
		var cnt = this.panelController().contentPanel()
		if(!cnt || !cnt.co()) return //<-- no content panel

		//~: add the component
		cnt.co().add(this.bind().co(true))

		//?: {has toolbar} dock it
		if(this.toolbar())
			this.bind().co().addDocked(this.toolbar().co(true))

		//?: {has status bar} dock it
		if(this.status())
			this.bind().co().addDocked(this.status().co(true))

		//~: add the removed content nodes
		var nodes = this._removed_content_nodes; if(ZeT.isa(nodes))
		{
			var body = cnt.co().getEl().down('.retrade-desktop-panel-content')
			if(!body) throw 'No desktop panel content element found!'

			for(var i = 0;(i < nodes.length);i++)
				body.dom.appendChild(nodes[i])

			delete this._removed_content_nodes //<-- to not add them further
		}
	},

	$remove_content  : function(destroy)
	{
		var cnt = this.panelController().contentPanel()
		if(!cnt || !cnt.co()) return //<-- no content panel

		//HINT: the component is automatically removed on destroy event
		if(!this._on_destruction)
			cnt.co().remove(this.bind().co(), destroy)

		if(destroy) this.bind().co(null) //<-- remove reference

		//~: remove plain DOM nodes left in the content panel
		var body = cnt.co().getEl().down('.retrade-desktop-panel-content')
		if(!body) throw 'No desktop panel content element found!'

		//~: collect all the child nodes
		var node = body.dom.firstChild, nodes = []
		while(node)
		{
			nodes.push(node)
			var next = node.nextSibling
			body.dom.removeChild(node)
			node = next
		}

		if(!destroy) this._removed_content_nodes = nodes
	},

	$insert_topbar   : function()
	{
		var ctrl = this.panelController()

		//?: {center & main menu}
		if(this.topbarMenu(true) && (ctrl.position() == 'center') && ctrl.mainMenuProc())
			return (ctrl.mainMenuProc())(true, this.topbarMenu(true))

		var ext  = ctrl.mainTopbarExt()
		if(!ext || !ext.co()) return //<-- no top bar to insert

		var tbis = this._topbar_items; if(!ZeT.isa(tbis)) return

		for(var i = 0;(i < tbis.length);i++)
		{
			//?: {the bind has no component already created} create it now
			if(!tbis[i].co())
				tbis[i].co(Ext.ComponentManager.create(tbis[i].buildProps()))

			ext.co().add(tbis[i].co())
		}
	},

	$remove_topbar   : function(destroy)
	{
		var ctrl = this.panelController()

		//?: {center & main menu}
		if(this.topbarMenu(false) && (ctrl.position() == 'center') && ctrl.mainMenuProc())
			(ctrl.mainMenuProc())(false, this.topbarMenu(false))

		var ext  = ctrl.mainTopbarExt()
		if(!ext || !ext.co()) return //<-- no top bar to insert

		var tbis = this._topbar_items; if(!ZeT.isa(tbis)) return

		for(var i = 0;(i < tbis.length);i++)
		{
			ext.co().remove(tbis[i].co(), destroy)
			if(destroy) tbis[i].co(null) //<-- remove reference
		}
	},

	$move_left       : function()
	{
		var prv, cur = this.position()

		if(cur === 'left')   prv = 'right'
		if(cur === 'center') prv = 'left'
		if(cur === 'right')  prv = 'center'

		this.desktop().swapPanels(prv, cur)
	},

	$move_right      : function()
	{
		var nxt, cur = this.position()

		if(cur === 'left')   nxt = 'center'
		if(cur === 'center') nxt = 'right'
		if(cur === 'right')  nxt = 'left'

		this.desktop().swapPanels(cur, nxt)
	},

	$web_link        : function()
	{
		if(ZeTS.ises(this.opts.webLink.panel))
			this.opts.webLink.panel = 'center'
		retrade_add_user$web_link(this.opts.webLink)
	}
})