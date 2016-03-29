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