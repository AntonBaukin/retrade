/*===============================================================+
 |                                                     desktop   |
 |   Desktop Support Scripts for ReTrade                         |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


// +----: Global Configurations :--------------------------------+

jQuery.fx.interval = 41.6


// +----: Is Touch Device :--------------------------------------+

ReTrade.isTouch = ZeT.scope(function()
{
	return ('ontouchstart' in window) ||
	  (navigator.msMaxTouchPoints > 0)
})


// +----: Repeated Task :----------------------------------------+

/**
 * Repeated task executes the task given with
 * the configured interval and notifies each
 * listener attached on the results of the
 * task execution. It's also possible to
 * assign the results externally and to
 * notify the listener.
 *
 * Arguments of the constructor are: options, or
 * the interval number (interval option) and the
 * task function (task option).
 *
 * Listener callback is invoked with arguments:
 *  0) current resulting value (object);
 *  1) this RepeatedTask instance.
 *
 * Task function returns the result object on
 * each invocation. If the result is undefined,
 * current value is not changed, and listeners
 * are not invoked. They are also not invoked
 * when current value is undefined.
 */
ReTrade.RepeatedTask = ZeT.defineClass('ReTrade.RepeatedTask', {

	init              : function()
	{
		if(ZeT.iso(arguments[0]))
			this.opts = opts
		else
		{
			var i = arguments[0]
			var f = arguments[1]

			if(ZeT.isf(i) && ZeT.isn(f))
			{
				i = arguments[1]
				f = arguments[0]
			}

			this.opts = { interval: i }
			if(!ZeT.isu(f)) this.opts.task = f
		}

		this.task     = this.opts.task
		this.interval = this.opts.interval

		ZeT.assert(ZeT.isn(this.interval))
		ZeT.assert(this.interval > 0)
		ZeT.assert(ZeT.isu(this.task) || ZeT.isf(this.task))

		//~: the callbacks array
		this._ls = []; this._istarted = 0
	},

	result            : function(value, notify)
	{
		if(ZeT.isu(value))
			return this._result

		//=: assign the result
		this._result = value

		//?: {has notification}
		if(notify !== false)
			this.notify()

		return this
	},

	on                : function(/* now | delay, callback, detach */)
	{
		var cb   = arguments[0]
		var de   = arguments[1]
		var now  = cb
		var self = this

		//?: {invoke callback now}
		if(!ZeT.isf(now))
		{
			cb  = arguments[1]
			de  = arguments[2]
			ZeT.assert(ZeT.isb(now) || (ZeT.isn(now) && now >= 0))
		}

		ZeT.assert(ZeT.isf(cb))

		if(de === true)
			ZeTA.remove(this._ls, cb)
		else if(this._ls.indexOf(cb) < 0)
			this._ls.push(cb)

		//?: {invoke callback now}
		if((now === true) || (now === 0))
			!ZeT.isu(this._result) && cb(this._result, this)
		//?: {delayed invocation}
		else if(ZeT.isn(now))
			ZeT.timeout(now, function()
			{
				if(!ZeT.isu(self._result))
					cb(self._result, self)
			})

		return this
	},

	isActive          : function()
	{
		return !!this._started
	},

	start             : function()
	{
		if(!ZeT.isx(this._started))
		{
			this._istarted++
			return this
		}

		//?: {the first invocation}
		if(this._istarted === 0)
		{
			//~: invoke right now
			this.trigger()

			//~: create the timer
			if(!this._trigger_)
				this._trigger_ = ZeT.fbind(this.trigger, this)
			this._started = setInterval(this._trigger_, this.interval)
		}

		//~: increment
		this._istarted++

		return this
	},

	stop              : function(force)
	{
		if(ZeT.isx(this._started))
			return this

		//?: {last stop request}
		if((force === true) || (this._istarted == 1))
		{
			clearInterval(this._started)
			delete this._started
			this._istarted = 0

			return this
		}

		//~: decrement
		ZeT.assert(this._istarted > 1)
		this._istarted--
	},

	trigger           : function()
	{
		if(!ZeT.isf(this.task)) return

		//!: invoke the task
		var x = this.task()

		//?: {has no new result}
		if(ZeT.isu(x)) return
		this._result = xs

		//?: {has value} call back
		if(!ZeT.isu(this._result))
			this.notify()
	},

	notify            : function()
	{
		for(var i = 0;(i < this._ls.length);i++)
			this._ls[i](this._result, this)
	}
})


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
	 * The first (optional) or the last argument
	 * may be plain object with the event options.
	 *
	 * Else arguments are concatenated in the message
	 * as it is for ZeTS.cat().
	 *
	 * Returns instance of ReTrade.Message, or undefined.
	 */
	event             : function()
	{
		//~: build the options
		var opts = ReTrade.Message.prototype.
		  options.call(this, ZeT.a(arguments))

		//?: {has no options}
		if(ZeT.isu(opts)) return undefined

		//~: the color
		opts.color = opts.color || 'N'

		//!: create and run the message
		return new ReTrade.Message(opts).trigger()
	},

	error             : function()
	{
		//~: build the options
		var opts = ReTrade.Message.prototype.
		  options.call(this, ZeT.a(arguments))

		//?: {has no options}
		if(ZeT.isu(opts)) return undefined

		//~: the color
		opts.color = opts.color || 'R'

		//!: create and run the message
		return new ReTrade.Message(opts).trigger()
	},

	/**
	 * Activates the desktop after installing
	 * it's panels. Invoked on Ext-Ready.
	 */
	activate          : function()
	{
		var sf = this, ps = ZeT.keys(this.panels)
		ZeT.each(ps, function(k) {
			sf.controller(k).triggerVoid()
		})
	},

	/**
	 * Returns the controller of the panel defined
	 * by it's bind or the position.
	 */
	controller        : function(panel)
	{
		if(ZeT.iss(panel)) panel = this.panels[panel]
		if(!panel || (panel.extjsfBind !== true))
			throw 'Not a panel argument!'
		return ZeT.assertn(panel.desktopPanelController)
	},

	/**
	 * Registers or returns the controller of root-panel
	 * inserted into the desktop panel by the given position.
	 */
	rootController    : function(position, controller)
	{
		var rc = this._root_controllers

		if(ZeTS.ises(position)) throw 'Not a position!'
		if(!rc) this._root_controllers = rc = {}

		if(ZeT.isu(controller)) return rc[position]
		if(controller === null) delete rc[position]; else
		{
			ZeT.assert(!rc[position] || (rc[position] === controller),
			  'Can not replace root controller in position [',
			  position, '] as there is other instance registered!')

			rc[position] = controller
		}

		return this
	},

	rootPanel         : function(bind, domain)
	{
		if(!bind) return this._root_panel
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain)
		this._root_panel = bind
		return this
	},

	bindPanel         : function(pos, bind, domain)
	{
		if(ZeT.iss(bind))
			bind = extjsf.bind(bind, domain)

		//~: register the bind of the panel
		this.panels[pos] = bind

		//~: assign the panel controller
		this._bind_control(bind, pos)

		//~: hook the component creation
		bind.on('added', ZeT.fbind(this._on_panel_added, this, pos))
	},

	loadDesktopPanel  : function(url, opts)
	{
		var pos    = (opts = opts || {}).position;
		if(!pos) pos = 'center';

		var panel  = this.controller(pos).contentPanel();
		panel = panel && panel.co();
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
		ZeT.undelay(params) //<-- resolve delayed parameters

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

		  url: retrade_go_url(url), 'params': params,
		  target: panel, autoLoad: true, scripts: true,
		  ajaxOptions: {'method': method}
		})
	},

	swapPanels        : function(one, two)
	{
		var cone = this.rootController(one)
		var ctwo = this.rootController(two)

		//~: temporarily remove the contents of the panels
		if(cone) cone.remove(false)
		if(ctwo) ctwo.remove(false)

		//~: swap the positions
		if(cone) cone.position(two)
		if(ctwo) ctwo.position(one)

		//~: insert the contents back
		if(cone) cone.insert()
		if(ctwo) ctwo.insert()

		return this
	},

	applyWindowBox    : function(opts)
	{
		var win = ZeT.assertn(extjsf.co(opts))
		var box, xy = win.getXY()

		if(opts.prevsize) //?: {remember current size}
			this.prevsizeComp({ component: win, save: opts.prevsize })

		//~: default settings
		if(!opts.width)      opts.width  = win.getWidth()
		if(!opts.height)     opts.height = win.getHeight()
		if(!ZeT.isn(opts.x)) opts.x      = xy[0]
		if(!ZeT.isn(opts.y)) opts.y      = xy[1]

		//?: {has align strategy}
		if(win.extjsfBind && win.extjsfBind.WinAlign)
		{
			box = this.calcWindowSize(opts)
			win.extjsfBind.WinAlign.resizeTo(box.width, box.height)
			box.x = win.getX()
			box.y = win.getY()
		}
		//~: general move-resize
		else
		{
			box = this.calcWindowBox(opts)
			win.setPosition(box.x, box.y).setSize(box.width, box.height)
		}

		return box
	},

	calcWindowSize    : function(opts)
	{
		var r = { width: extjsf.pt(480), height: extjsf.pt(360) }

		//~: width & height
		this._size_pt(opts)
		if(opts.width)  r.width  = opts.width
		if(opts.height) r.height = opts.height

		ZeT.assert(ZeT.isn(r.width))
		ZeT.assert(ZeT.isn(r.height))

		return r
	},

	calcWindowBox     : function(opts)
	{
		var r = this.calcWindowSize(opts)

		//~: x-position
		var maxy = Ext.getBody().getViewSize()
		if(!ZeT.isu(opts.x)) r.x = opts.x
		else if(opts.event) r.x = opts.event.getX()
		else r.x = (maxy.width - r.width)/2

		//~: x-offset
		var x = r.x; if(opts['+x']) x += opts['+x']
		else if(opts['+xpt']) x += extjsf.pt(opts['+xpt'])
		if(x + r.width > maxy.width) x = maxy.width - r.width
		if(x >= 0) r.x = x

		//~: y-position
		if(!ZeT.isu(opts.y)) r.y = opts.y
		else if(opts.event) r.y = opts.event.getY()
		else r.y = (maxy.height - r.height)/2

		//~: y-offset
		var y = r.y; if(opts['-y']) y -= opts['-y']
		else if(opts['-ypt']) y -= extjsf.pt(opts['-ypt'])
		if(opts['-height']) y -= opts.height

		if(y < 0) y = 0
		if(y + r.height > maxy.height)
			y = maxy.height - r.height
		if(y < 0) y = 0
		r.y = y

		return r
	},

	/**
	 * The arguments are the same as for extjsf.co().
	 * On the first call, remembers the size of the component.
	 * On the following call once returns the size to the original.
	 * Returns true when the component was re-sized.
	 *
	 * Set 'save' option true to always remember present position
	 * instead of the resizing back. If it's value is a string, it
	 * is used as a marker within the stack of previous sizes.
	 *
	 * Option 'marker' allows to pop the items from the sizes
	 * stack till the item having it (is also removed). If
	 * marker is undefined, the first item is always selected.
	 */
	prevsizeComp      : function(opts)
	{
		var s, x = {}, comp = extjsf.co.apply(this, arguments)
		if(!comp || !comp.extjsfBind) return undefined
		if(comp === opts) opts = {}

		//?: {restoring current size}
		if(!opts.save && comp.extjsfBind.prevSize)
		{
			var stack = comp.extjsfBind.prevSize
			ZeT.assert(ZeT.isa(stack) && stack.length)

			//~: find the position to take
			var i = 0; if(!ZeTS.ises(opts.marker))
				for(var j = stack.length - 1;(j >= 0);j--)
					if(stack[j].marker === opts.marker)
						{ i = j; break; }

			//~: restore the size
			var box = stack[i]
			box.component = comp
			this.resizeComp(box)
			delete box.component

			//?: {has nothing left}
			if(i == 0) delete comp.extjsfBind.prevSize; else
				stack.splice(i, stack.length - i)

			return true
		}

		//?: {has no stack}
		if(!(stack = comp.extjsfBind.prevSize))
			comp.extjsfBind.prevSize = stack = []

		//~: remember the size
		box = comp.getSize()
		stack.push(box)

		//?: {has marker}
		if(!ZeTS.ises(opts.save))
			box.marker = opts.save

		return false
	},

	resizeToHeight    : function(win, opts, target)
	{
		ZeT.assertn(opts)

		if(ZeT.iss(win))
			win = extjsf.co(win, opts.domain)
		ZeT.assert(win && (win.isComponent === true))

		if(ZeT.iss(target))
			target = extjsf.co(win, opts.domain)
		if(!ZeT.isa(target))
			target = [target]

		//?: {the height was adjusted}
		if(!opts.force && this.prevsizeComp(win)) return

		//~: calculate the bounds
		var y, Y, H = win.body.getHeight()
		ZeT.each(target, function(t)
		{
			if(ZeT.iss(t))
				t = extjsf.co(t, opts.domain)
			ZeT.assert(t && (t.isComponent === true))

			var b = t.getBox()
			if(ZeT.isu(y) || (b.y < y)) y = b.y
			if(ZeT.isu(Y) || (b.bottom > Y)) Y = b.bottom
		})

		var dH = opts['+h']; if(!ZeT.isn(dH)) dH = 0
		if(ZeT.isn(opts['+hpt'])) dH += extjsf.pt(opts['+hpt'])

//		ZeT.log('H = ', H, ', Y - y = ', Y - y,
//		  ', (H - (Y - y + dH)) = ', (H - (Y - y + dH)),
//		  '; win.setHeight(win.getHeight() - ',
//		  (H - (Y - y + dH)), ') = win.setHeight(',
//		   win.getHeight() - (H - (Y - y + dH)), ')'
//		)

		//?: {have no components}
		if(ZeT.isu(y) || ZeT.isu(Y)) return
		ZeT.assert(y <= Y)

		//~: remember the size
		this.prevsizeComp({component: win, save: opts.prevsize || true})

		//?: {forbid move}
		var ske; if(opts.move === false)
		{
			ZeT.assertn(win.extjsfBind)
			ZeT.assertn(win.extjsfBind.WinAlign)
			ske = win.extjsfBind.WinAlign.skipEvents(true)
		}

		//~: set proper height
		win.setHeight(win.getHeight() - (H - (Y - y + dH)))

		//~: restore move reactions
		if(opts.move === false)
			win.extjsfBind.WinAlign.skipEvents(ske)
	},

	resizeComp        : function(opts)
	{
		var co = extjsf.co(opts)
		if(!co) return

		this._size_pt(opts)

		//?: {has align strategy}
		if(co.extjsfBind && co.extjsfBind.WinAlign)
		{
			var box = this.calcWindowSize(opts)
			co.extjsfBind.WinAlign.resizeTo(box.width, box.height)
		}
		else
		{
			if(ZeT.isn(opts.width) && ZeT.isn(opts.height))
				co.setSize(opts.width, opts.height)
			else if(ZeT.isn(opts.width))
				co.setWidth(opts.width)
			else if(ZeT.isn(opts.height))
				co.setHeight(opts.height)
		}

		return this
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
		if(!this._ready_points) this._ready_points = {}
		var rp = this._ready_points[name]
		if(rp) rp.ready = !!ready
		else this._ready_points[name] = { 'name': name, 'ready': ready }

		//ZeT.log('Ready point [', name, ']: ', ready)

		//~: inspect whether all are ready
		var keys = ZeT.keys(this._ready_points)
		this._ready_go = true
		for(var i = 0;(i < keys.length);i++)
			if(!this._ready_points[keys[i]].ready)
				{ this._ready_go = false; break }

		//?: {all are ready} go!
		if(this._ready_go && this._ready_fs)
		{
			var fs = this._ready_fs
			this._ready_fs = null //<-- !: clear ready callbacks list

			for(i = 0;(i < fs.length);i++)
				fs[i]()
		}

		return this
	},

	onReady           : function(f)
	{
		ZeT.assert(ZeT.isf(f))
		if(this._ready_go !== false) return f()

		if(!this._ready_fs) this._ready_fs = []
		this._ready_fs.push(f)

		return this
	},

	_size_pt          : function(opts)
	{
		if(opts.widthpt)
		{
			opts.width = extjsf.pt(opts.widthpt)
			delete opts.widthpt
		}

		if(opts.heightpt)
		{
			opts.height = extjsf.pt(opts.heightpt)
			delete opts.heightpt
		}
	},

	_on_panel_added   : function(pos, panel)
	{
		this.collapsing.onPanelAdded(panel, pos)
	},

	_bind_control     : function(panelBind, pos)
	{
		if(panelBind.desktopPanelController) return

		var opts = { desktop: this }
		opts.position = ZeT.asserts(pos)

		panelBind.desktopPanelController =
		  ZeT.createInstance('ReTrade.PanelController', opts)
		panelBind.desktopPanelController.rootBind(panelBind)
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
		this.opts = opts || {}
	},

	position          : function(pos)
	{
		if(!ZeT.iss(pos)) return this.opts.position
		this.opts.position = pos
		return this
	},

	rootBind          : function(panelBind)
	{
		if(!panelBind) return this._root_bind
		this._root_bind = panelBind
		return this
	},

	/**
	 * Bind of the main controls area of the panel.
	 */
	mainTopbar        : function(bind, domain)
	{
		if(!bind) return this._main_topbar
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain)
		this._main_topbar = bind
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
	mainMenuProc      : function(f)
	{
		if(ZeT.isu(f))
			return this._main_menu_proc

		ZeT.assert(ZeT.isf(f))
		this._main_menu_proc = f
		return this
	},

	/**
	 * Extension point to add root panel controls
	 * onto the top bar.
	 */
	mainTopbarExt     : function(bind, domain)
	{
		if(!bind) return this._main_topbar_ext
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain)
		this._main_topbar_ext = bind
		return this
	},

	/**
	 * The panel to insert the content components of the panel.
	 * It usually has fit layout.
	 */
	contentPanel      : function(bind, domain)
	{
		if(!bind) return this._content_panel
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain)
		this._content_panel = bind
		return this
	},

	/**
	 * If set, void panel is displayed
	 * when else panels have no content.
	 */
	voidPanel         : function(bind, domain)
	{
		if(!bind) return this._void_panel
		if(!bind.extjsfBind)
			bind = extjsf.bind(bind, domain)
		this._void_panel = bind
		return this
	},

	/**
	 * Tells whether to show void panel: else panels
	 * are empty and void panel is assigned.
	 */
	isVoid            : function()
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
	triggerVoid       : function()
	{
		//?: {has no void panel}
		var vo = this.isVoid()
		if(ZeT.isu(vo)) return

		this._void_panel.visible(vo)
		ZeT.each(this._all_panes(), function(p) {
			p && p.visible(!vo)
		})
	},

	_all_panes        : function()
	{
		return [ this._main_topbar,
		  this._main_topbar_ext, this._content_panel ]
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
		this.opts = ZeT.assertn(opts)

		//~: domain
		this.domain()

		//~: desktop
		this.desktop()

		//~: the root-panel bind
		this.bind()

		//~: the position
		this.position()

		opts.bind.rootPanelController = this
		opts.bind.on('beforedestroy', ZeT.fbind(this._on_destroy, this))
	},

	domain            : function()
	{
		return ZeT.asserts(this.opts.domain, 'No Domain name specified!')
	},

	desktop           : function()
	{
		return ZeT.assertn(this.opts.desktop, 'No Desktop instance!')
	},

	panelController   : function()
	{
		return ZeT.assertn(this.desktop().controller(this.opts.position),
		  'No Desktop panel controller at the position [', this.opts.position, ']!')
	},

	bind              : function()
	{
		return ZeT.assertn(this.opts.bind, 'No root-panel Bind instance!')
	},

	position          : function(pos)
	{
		if(ZeTS.ises(pos))
			//?: {the position key is undefined}
			return ZeT.asserts(this.opts.position, 'No Desktop position key!')

		//~: there is no panel controller at that position
		ZeT.assertn(this.desktop().controller(pos),
		  'No Desktop panel controller at the position [', pos, ']!')

		this.opts.position = pos
		return this
	},

	topbarItems       : function(items)
	{
		if(!items) return this._topbar_items
		this._topbar_items = items
		return this
	},

	/**
	 * Takes two forms. First, when the first arguments
	 * is boolean: returns insert (true), or remove (false)
	 * strategy of extending the main menu.
	 *
	 * Second, two arguments, both functions: to insert
	 * and to remove the menus. Installed by the root panel.
	 */
	topbarMenu        : function()
	{
		if(arguments.length == 2)
		{
			var i = arguments[0], r = arguments[1]

			ZeT.assert(ZeT.isf(i) && ZeT.isf(r))
			this._topbar_menu = { insert: i, remove: r }

			return this
		}

		ZeT.assert(arguments.length == 1)
		ZeT.assert(ZeT.isb(arguments[0]))

		if(!this._topbar_menu) return undefined
		return this._topbar_menu[(arguments[0])?('insert'):('remove')]
	},

	toolbar           : function(bind)
	{
		if(!bind) return this._toolbar
		this._toolbar = bind
		return this
	},

	statusbar         : function(bind)
	{
		if(!bind) return this._statusbar
		this._statusbar = bind
		return this
	},

	destroy           : function()
	{
		this.remove()

		//?: {the domain must be destroyed}
		if(this.opts['domainOwner'])
			extjsf.domain(this.domain()).destroy()

		return this
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

		//~: hide void panel
		this.panelController().triggerVoid()
	},

	remove            : function(destroy)
	{
		//~: remove the main content
		this._remove_content(destroy)

		//~: remove the top bar controls
		this._remove_topbar(destroy)

		//~: clear registration of this controller
		this._unregister()

		//~: show void panel
		var pc = this.panelController()
		ZeT.timeout(100, ZeT.fbind(pc.triggerVoid, pc))
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
		if(this.opts['notools'] === true) return
		if(this._tools_are_set) return
		this._tools_are_set = true

		var tools = this.bind().$raw().tools
		if(!ZeT.isa(tools)) tools = []

		//~: add the tools of the panel
		this._add_tools(tools)

		if(tools.length)
			this.bind().props({'tools': tools})
	},

	_add_tools        : function(tools)
	{
		//~: add tool to save web link
		this._add_link_tool(tools)

		//~: add panel move left-right tools
		this._add_move_tools(tools)
	},

	_add_link_tool    : function(tools)
	{
		if(!ZeT.iso(this.opts.webLink)) return

		tools.push({ xtype: 'tool', cls: 'retrade-web-link-tool',
		  handler: ZeT.fbind(this._web_link, this),
		  margin: extjsf.pts(0, 8, 0, 2), tooltipType: 'title',
		  tooltip: 'Создать постоянную ссылку на панель'
		})
	},

	_add_move_tools   : function(tools)
	{
		if(this.opts['nomove'] === true) return

		//~: add <<
		tools.push({ xtype: 'tool', type: 'left',
		  handler: ZeT.fbind(this._move_left, this),
		  margin: extjsf.pts(0, 8, 0, 2), tooltipType: 'title',
		  tooltip: 'Передвинуть панель в левую область'
		})

		var m = extjsf.pts(0, 0, 0, 2)
		if(this.bind().$raw()['closable'])
			m = extjsf.pts(0, 8, 0, 2)

		//~: add >>
		tools.push({ xtype: 'tool', type: 'right',
		  handler: ZeT.fbind(this._move_right, this),
		  margin: m, tooltipType: 'title',
		  tooltip: 'Передвинуть панель в правую область'
		})
	},

	_insert_content   : function()
	{
		var cnt = this.panelController().contentPanel()
		if(!cnt || !cnt.co()) return //<-- no content panel

		//?: the bind component is not created yet
		if(!this.bind().co())
		{
			//~: create the root panel component
			this.bind().co(Ext.ComponentManager.create(
			  this.bind().buildProps()))

			//?: {has toolbar} dock it
			if(this.toolbar()) this.bind().co().
			  addDocked(this.toolbar().buildProps())
			//?: {has status bar} dock it
			if(this.statusbar()) this.bind().co().
			  addDocked(this.statusbar().buildProps())
		}

		//~: add the component
		cnt.co().add(this.bind().co())

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

	_remove_content   : function(destroy)
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

	_insert_topbar    : function()
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

	_remove_topbar    : function(destroy)
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

	_move_left        : function()
	{
		var prv, cur = this.position()

		if(cur === 'left')   prv = 'right'
		if(cur === 'center') prv = 'left'
		if(cur === 'right')  prv = 'center'

		this.desktop().swapPanels(prv, cur)
	},

	_move_right       : function()
	{
		var nxt, cur = this.position()

		if(cur === 'left')   nxt = 'center'
		if(cur === 'center') nxt = 'right'
		if(cur === 'right')  nxt = 'left'

		this.desktop().swapPanels(cur, nxt)
	},

	_web_link         : function()
	{
		if(ZeTS.ises(this.opts.webLink.panel))
			this.opts.webLink.panel = 'center'
		retrade_add_user_web_link(this.opts.webLink)
	},

	_on_destroy       : function()
	{
		this._on_destruction = true
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
		this._structs = {}
	},

	desktop           : function(desktop)
	{
		if(!desktop) return this.desktop
		this.desktop = desktop
		return this
	},

	onPanelAdded      : function(panel, pos)
	{
		var self = this, bind = extjsf.asbind(panel)
		ZeT.assertn(bind, 'Desktop panel has no ExtJSF Bind!')
		bind.desktopCollapsing = { position: pos }

		panel.on({
			beforecollapse : ZeT.fbind(self._before_collapse, self, bind),
			collapse       : ZeT.fbind(self._after_collapse, self, bind),
			beforeexpand   : ZeT.fbind(self._before_expand, self, bind),
			expand         : ZeT.fbind(self._after_expand, self, bind),
			afterlayout    : ZeT.fbind(self._after_layout, self, bind)
		})
	},

	_collapsed_click  : function(panel)
	{
		panel.co().expand()
	},

	_before_collapse  : function(panel)
	{
		var w = panel.co().getWidth()
		var W = panel.co().ownerCt.getWidth()

		//~: original width is relative
		panel.desktopCollapsing.originalWidth = (w / W)
		panel.desktopCollapsing.setOriginalWidth = true
	},

	_after_collapse   : function(panel)
	{
		//?: {has no bound click handler}
		if(!panel.desktopCollapsing.click)
			panel.desktopCollapsing.click =
			  ZeT.fbind(this._collapsed_click, this, panel)

		ZeT.timeout(500, function()
		{
			panel.co().getEl().removeListener(
			  'click', panel.desktopCollapsing.click)
			panel.co().getEl().on(
			  'click', panel.desktopCollapsing.click)
		})
	},

	_before_expand    : function(panel)
	{
		if(panel.desktopCollapsing.click)
			panel.co().getEl().removeListener(
			  'click', panel.desktopCollapsing.click)
	},

	_after_expand     : function(panel)
	{},

	_after_layout     : function(panel)
	{
		if(panel.desktopCollapsing.setOriginalWidth)
		{
			var W = panel.co().ownerCt.getWidth()
			var w = Math.round(W * panel.desktopCollapsing.originalWidth)

			//!: prevents infinite recursion
			panel.desktopCollapsing.setOriginalWidth = false
			panel.co().setWidth(w)
		}
	}
})


// +----: Desktop Instance :-------------------------------------+

ReTrade.desktop = ZeT.defineInstance('ReTrade.desktop', ReTrade.Desktop);


// +----: Message :----------------------------------------------+

ReTrade.Message = ZeT.defineClass('ReTrade.Message', {

	DEFAULTS          : {

		defaultContentTag    : 'div',
		defaultContentClass  : 'retrade-desktop-event-content',

		eventFrameClassNames : [

			'retrade-desktop-event',
			'retrade-border-free-size'
		],

		borderImplClass      : ZeT.Border.Shadow,

		borderClassNames     : {

			N: 'retrade-boshadow-N-XYZ',
			R: 'retrade-boshadow-R-XYZ',
			G: 'retrade-boshadow-G-XYZ',
			O: 'retrade-boshadow-O-XYZ'
		},

		animation            : {
			'top%': 15, moveDuration: 2000, moveVisibleTime: 6000,
			moveEasing: 'easeOutQuad', fadeDuration: 500,
			slideOffDuration: 500, slideOffMove: 100
		},

		autoRemove           : true,
		removeOnSecondClick  : true
	},

	init              : function(opts)
	{
		//~: deeply extend with the defaults clone
		this.opts = ZeT.deepExtend(opts, this.DEFAULTS)
	},

	/**
	 * Scans for the options in the arguments array.
	 * Sets to null all the positions in the arguments
	 * array that are taken.
	 *
	 * Single argument is not treated as an option.
	 */
	options           : function(args)
	{
		ZeT.asserta(args)
		if(args.length == 0) return

		//~: find the options
		var opts; ZeT.each([0, args.length - 1], function(i)
		{
			if(ZeT.iso(args[i])) { opts = args[i]; args[i] = null; return false }
		})

		//~: message text
		opts = opts || {}
		if(!opts.text && !opts.html && !opts.node)
		{
			opts.text = ZeTS.cat.apply(ZeTS, args)
			if(ZeTS.ises(opts.text)) return
		}

		return opts
	},

	trigger           : function()
	{
		//?: {node is not placed in the DOM yet}
		if(!ZeTD.isgn(this.node()))
			((this.opts.parent)?ZeTD.n(this.opts.parent):(document.body)).
			  appendChild(this.node())

		//~: animate the message
		this._animate()

		return this
	},

	node              : function()
	{
		//?: {has the node created}
		if(this._node) return this._node

		//?: {has node provided}
		if(ZeTD.isn(this.opts.node))
			return this._node = this.opts.node
		else
		{
			//~: create the node
			this._node = document.createElement(
			  ZeT.asserts(this.opts.defaultContentTag))

			//?: {has message text}
			if(!ZeTS.ises(this.opts.text))
				this._node.innerHTML = ZeTD.escape(this.opts.text)
			//?: {has html markup}
			else if(!ZeTS.ises(this.opts.html))
				this._node.innerHTML = this.opts.html
		}

		//~: wrap the node
		this._node = this._wrap(this._node)

		//~: listen the click
		$(this._node).click(ZeT.fbind(this._click, this))

		return this._node
	},

	_click            : function(e)
	{
		//?: {animation callback}
		if(ZeT.isf(this._ani_click))
			this._ani_click(e)
	},

	_animate          : function()
	{
		ZeT.assertn(this.opts.animation)

		//~: animate up-down movement
		this._ani_up_down()
	},

	_wrap             : function(node)
	{
		var self = this
		ZeT.assert(ZeTD.isn(node))

		//~: create the border pipe
		return ZeT.Layout.procPipeCall(

			//~: take the argument node
			ZeT.Layout.Proc.Node, { node: node },

			//~: wrap it with the border
			ZeT.assertn(self.opts.borderImplClass),
			self._border_opts(),

			//~: wrap with the frame
			ZeT.Layout.Proc.Wrap, { html: '<div/>',
			  classes : self.opts.eventFrameClassNames,
			  styles  : { display: 'none', zIndex: self._zindex() }
			}
		)
	},

	_border_opts      : function()
	{
		//~: access border color
		var c = ZeT.asserts(this.opts.color || 'N')
		c = ZeT.asserts(this.opts.borderClassNames[c],
		  'No border class template is found for the color [', c, ']!')

		//~: border class
		var bcls = ZeT.assertn(this.opts.borderImplClass)

		return ZeT.Border.create({ classes: c, border: bcls })
	},

	_zindex           : function()
	{
		if(!ZeT.isi(this.opts.zindex))
			this.opts.zindex = 99999
		return this.opts.zindex--
	},

	_ani_up_down      : function()
	{
		var self = this, n = $(this.node())

		//~: place at the upper position
		n.offset({top: -9999}).show() //<-- show to have the borders

		//~: not set it to touch the frame
		n.offset({top: -n.outerHeight()})

		//~: move-to top position
		var moveto; if(!ZeT.isu(this.opts.top))
			moveto = this.opts.top
		else
		{
			var H = $(document).innerHeight()
			ZeT.isn(this.opts.animation['top%'])
			ZeT.assert(H > 0)

			moveto = Math.ceil(H * this.opts.animation['top%'] / 100)
		}

		//~: click handler
		var clicked = 0
		this._ani_click = function()
		{
			clicked++

			//?: {first click} stop
			if(clicked == 1)
				n.stop() //<-- stop moving down

			//?: {second click}
			if((clicked == 2) && self.opts.removeOnSecondClick)
				self._ani_fade(true)
		}

		ZeT.assert(ZeT.isi(this.opts.animation.moveVisibleTime))
		ZeT.assert(ZeT.isi(this.opts.animation.moveDuration))
		ZeT.asserts(this.opts.animation.moveEasing)
		ZeT.assert(ZeT.isi(moveto))

		//~: animate it's move down
		n.animate({ top: moveto }, {

		  duration: this.opts.animation.moveDuration,
		  easing: this.opts.animation.moveEasing,
		  always: function()
		  {
				//?: {not clicked & auto-hide}
				if(!clicked && self.opts.autoRemove)
					ZeT.timeout(self.opts.animation.moveVisibleTime, function()
					{
						if(!clicked) self._ani_fade()
					})
		  }
		})
	},

	_ani_fade         : function(slider)
	{
		var self = this, n = $(this.node())

		if(slider)
		{
			ZeT.assert(ZeT.isi(this.opts.animation.slideOffDuration))

			var p = { top: '+=' + this.opts.animation.slideOffMove, opacity: 0 }

			var o = { always: ZeT.fbind(self._ani_hide, self),
			  duration: this.opts.animation.slideOffDuration
			}

			n.animate(p, o)
		}
		else
		{
			ZeT.assert(ZeT.isi(this.opts.animation.fadeDuration))

			n.fadeOut({
			  duration: this.opts.animation.fadeDuration,
			  always: ZeT.fbind(self._ani_hide, self)
			})
		}
	},

	_ani_hide         : function()
	{
		delete self._ani_click
		$(this.node()).remove()
	}
})


// +----: Win Align :--------------------------------------------+

/**
 * Controller for floating components (windows)
 * that detects the movement and the browser resize
 * to allow the component to sticky-touch the document.
 *
 * The target window may be specified in one of the
 * following variants:
 *
 * · window Bind name, domain string;
 * · Bind instance;
 * · Ext component;
 * · factory function.
 *
 * The latter variant allows to pass a function to
 * return a Bind, or a Component. When it returns
 * a defined result, it is never invoked again.
 */
ReTrade.WinAlign = ZeT.defineClass('ReTrade.WinAlign', {

	/**
	 * The initial (default) alignment may be provided
	 * via 'align' option.
	 */
	init              : function(opts)
	{
		ZeT.assertn(this.opts = opts)
		var window = opts.window

		//?: {window by the domain}
		if(ZeT.iss(window))
		{
			ZeT.assert(ZeT.iss(opts.domain))
			window = extjsf.bind(window, opts.domain)
			ZeT.assert(extjsf.isbind(window))
		}

		//?: {is a bind}
		if(extjsf.isbind(this._window = window))
		{
			//~: save this strategy reference
			window.WinAlign = this

			//~: bind the listeners
			this._listen()
		}
		else
		{
			ZeT.assertn(window)

			//~: must be a floating component
			if(window.isComponent === true)
			{
				ZeT.assertn(window.zIndexManager)

				//~: save this strategy reference
				if(window.extjsfBind)
					window.extjsfBind.WinAlign = this

				//~: bind the listeners
				this._listen()
			}
			//~: or a factory
			else
				ZeT.assert(ZeT.isf(window))
		}

		var win = this.win(false)
		if(win && win.getWidth() && win.getHeight())
			//?: initial alignment given
			if(ZeT.iss(opts.align))
			{
				this._align = opts.align
				this.resizeTo(null, null, true)
			}
			else
			{
				var xy = win.getXY()
				this._on_wnd_move(win, xy[0], xy[1])
			}
	},

	skipEvents        : function(ske)
	{
		var old = !!this._skip_events
		if(ZeT.isu(ske)) return old

		if(ske) this._skip_events = true
		else delete this._skip_events

		return old
	},

	/**
	 * Sets size the window and properly places it
	 * according to the current alignment.
	 */
	resizeTo          : function(w, h, doalign)
	{
		var xy, a

		var win = this.win()
		if(!win) return

		if(!ZeT.isn(w)) w = win.getWidth()
		if(!ZeT.isn(h)) h = win.getHeight()

		if(doalign !== false)
		{
			xy = win.getXY()
			a  = this._align_to(xy, w, h)
		}

		var ske = this.skipEvents(true); try
		{
			if((win.getWidth() != w) || (win.getHeight() != h))
				win.setSize(w, h)

			if(doalign !== false)
				if(a) win.alignTo(document.body, a)
				else  win.setXY(xy)
		}
		finally
		{
			this.skipEvents(ske)
		}
	},

	win               : function(required /* = true */)
	{
		//?: {already destroyed}
		if(!this._window) if(required === false)
			return undefined
		else
			throw new Error('Window is not provided!')

		var win; if(extjsf.isbind(this._window))
			win = this._window.co()
		else if(this._window.isComponent === true)
			win = this._window
		else
		{
			ZeT.assert(ZeT.isf(this._window))

			//?: {built a result}
			if(win = this._window())
			{
				if(extjsf.isbind(win))
					win = (this._window = win).co()
				else
				{
					ZeT.assert(win.isComponent === true)
					ZeT.assertn(win.zIndexManager)
					this._window = win
				}

				//!: listen now
				this._listen()
			}
		}

		if(!win && !(required === false))
			throw new Error('Window is not provided!')

		return (win)?(win):(undefined)
	},

	bind              : function()
	{
		if(extjsf.isbind(this._window))
			return this._window

		var w = this.win(false), b = w && w.extjsfBind
		return (b && b.extjsfBind === true)?(b):(undefined)
	},

	/**
	 * Optional parameters arguments allows to assign
	 * the alignment-positioning parameters to options
	 * required to build an Ext Window.
	 */
	pos               : function(p)
	{
		var win = this.win(false)

		//?: {has the position saved}
		var xy; if(ZeT.isa(xy = this._xy))
		{
			var B = Ext.getBody().getViewSize()
			var W = B.width, H = B.height

			//?: {position is out of frame}
			if((xy[0] > W) || (xy[1] > H))
				delete this._xy
			else
			{
				//HINT: we also update the defined position
				//  to meet the partial alignment.

				if(ZeT.isf(win && win.setXY))
				{
					this._align_to(xy, win.getWidth(), win.getHeight())
					win.setXY(this._align_to(xy))
				}

				if(ZeT.iso(p))
				{
					this._align_to(xy, p.width, p.height)
					p.x = xy[0]; p.y = xy[1]
				}
			}
		}

		//?: {has no direct position}
		if(!ZeT.isa(this._xy))
		{
			//?: {has no alignment}
			var a = this._align_to(); if(ZeT.isu(a))
			{
				this._align = this.opts.align || 'cc'
				a = this._align_to()
			}

			//~: do align the window
			if(a && ZeT.isf(win && win.alignTo))
				win.alignTo(document.body, a)
			if(a && ZeT.iso(p))
				p.defaultAlign = a
		}

		if(ZeT.iso(p))
		{
			if(!ZeT.isa(this._wh))
				this._wh = [ extjsf.pt(430), extjsf.pt(330) ]

			if(!ZeT.isn(p.width))
				p.width  = this._wh[0]
			if(!ZeT.isn(p.height))
				p.height = this._wh[1]
		}
	},

	/**
	 * Returns existing alignment code.
	 *
	 * When xy is true, and the alignment is partial
	 * (has '0' in one axis), returns not a string,
	 * but { align: ..., x: ... | y: ... }
	 */
	align             : function()
	{
		//?: {has no alignment}
		var a = this._align
		if(!ZeT.iss(a))
			return undefined

		//?: {invalid code}
		ZeT.assert(a != '00')
		ZeT.assert(a.length == 2)

		//?: {is not partial}
		var i = a.indexOf('0')
		if(i == -1) return a

		//~: current window
		var win = this.win(false)
		if(!win) return undefined

		return (i == 0)?{ align: a, x: win.getX() }:
		  { align: a, y: win.getY() }
	},

	/**
	 * This implementation wraps the listeners of the
	 * window object to allow to change further the align
	 * strategy not attaching new listeners.
	 */
	_listen           : function()
	{
		//~: collection of listeners
		var wal = (this.bind() && this.bind().WinAlignListeners) || {}

		//?: {need destroy previous strategy}
		if(ZeT.isf(wal.destroy)) wal.destroy()

		//~: set this listeners
		ZeT.extend(wal, {
			move     : ZeT.fbind(this._on_wnd_move, this),
			resize   : ZeT.fbind(this._on_wnd_resize, this),
			destroy  : ZeT.fbind(this._destroy, this),
			collapse : ZeT.fbind(this._on_collapse, this),
			expand   : ZeT.fbind(this._on_expand, this),
			reframe  : ZeT.fbind(this._on_resize, this)
		})

		//?: {has not bound them yet}
		if(!this.bind() || !this.bind().WinAlignListeners)
		{
			//~: target window move
			this._on('move', function(){ wal.move.apply(this, arguments) })

			//~: target window resize
			this._on('resize', function(){ wal.resize.apply(this, arguments) })

			//~: target window destroy
			this._on('beforedestroy', function(){ wal.destroy.apply(this, arguments) })

			//~: target window collapsed
			this._on('beforecollapse', function(){ wal.collapse.apply(this, arguments) })

			//~: target window expanded
			this._on('expand', function(){ wal.expand.apply(this, arguments) })

			//~: browser window resize
			if(!wal._reframe)
			{
				wal._reframe = function(){ wal.reframe.apply(this, arguments) }
				Ext.get(window).addListener('resize', wal._reframe)
			}
		}

		//~: remember window resize listener
		this._on_resize_ = wal._reframe

		//~: remember the listeners
		if(this.bind()) this.bind().WinAlignListeners = wal
	},

	_destroy          : function()
	{
		//~: collection of listeners
		var wal = this.bind() && this.bind().WinAlignListeners

		if(ZeT.isf(this._on_resize_))
		{
			Ext.get(window).removeListener('resize', this._on_resize_)
			if(wal && (wal._reframe == this._on_resize_))
				delete wal._reframe
			delete this._on_resize_

		}

		delete this._window
	},

	_on               : function()
	{
		ZeT.assertn(this._window)
		if(extjsf.isbind(this._window))
			this._window.on.apply(this._window, arguments)
		else
		{
			var win = ZeT.assertn(this.win())
			win.on.apply(win, arguments)
		}
	},

	/**
	 * Checks current alignment and returns one
	 * of the values, if available: 'bl-bl',
	 * 'br-br', 'b-b', 'l-l', 'r-r', 'c-c'.
	 *
	 * If current alignment is not one of those,
	 * returns undefined, and may also update
	 * the given point-array for partial values
	 * of the present alignment.
	 */
	_align_to         : function(xy, w, h)
	{
		var a = this._align
		if(!ZeT.iss(a)) return undefined
		ZeT.assert((a.length == 2) && (a != '00'))

		switch(a) //?: {any of complete alignments}
		{
			case 'lb': return 'bl-bl'
			case 'rb': return 'br-br'
			case 'cb': return 'b-b'
			case 'lc': return 'l-l'
			case 'rc': return 'r-r'
			case 'cc': return 'c-c'
		}

		//?: {has no coordinates to update}
		if(!ZeT.isa(xy)) return undefined

		//~: proceed with the partial alignment
		var ax = a.charAt(0), ay = a.charAt(1)

		//?: {left}
		if(ax == 'l') xy[0] = 0

		var B = Ext.getBody().getViewSize()
		var W = B.width, H = B.height

		//?: {right}
		if((ax == 'r') && ZeT.isn(w))
			xy[0] = W - w

		//?: {x-center}
		if((ax == 'c') && ZeT.isn(w))
			xy[0] = Math.floor(0.5*(W - w))

		//?: {bottom}
		if((ay == 'b') && ZeT.isn(h))
			xy[1] = document.body.offsetHeight - h

		//?: {y-center}
		if((ay == 'c') && ZeT.isn(h))
			xy[1] = Math.floor(0.5*(H - h))
	},

	_is_skip          : function()
	{
		return this._skip_events || this._collapsing
	},

	_on_wnd_move      : function(win, x, y)
	{
		//?: {not need to react}
		if(this._is_skip()) return

		//~: clear align and the classes
		delete this._align
		delete this._xy
		this._align_cls()

		var B = Ext.getBody().getViewSize()
		var W = B.width, H = B.height
		var w = win.getWidth()
		var h = win.getHeight()
		var D = extjsf.inch(0.5) //<-- sticky tolerance
		var a, ax = '0', ay = '0'

		//?: {touch left}
		if(x <= D)
			{ ax = 'l'; x = 0 }
		//?: {touch right}
		else if(Math.abs(W - x - w) <= D)
			{ ax = 'r'; x = W - w }
		//?: {touch x-center}
		else if(Math.abs((W - w)* 0.5 - x) <= D)
			{ ax = 'c'; x = Math.floor((W - w)* 0.5) }

		//HINT: we do not align to the top as there are the controls!

		//?: {touch bottom}
		if(Math.abs(H - y - h) <= D)
			{ ay = 'b'; y = H - h }
		//?: {touch y-center}
		else if(Math.abs((H - h)* 0.5 - y) <= D)
			{ ay = 'c'; y = Math.floor((H - h)* 0.5) }

		//ZeT.log('Moved to align: ', ax + ay)

		//?: {has some alignment}
		if((a = ax + ay) != '00')
		{
			this._align = a

			//~: assign align class
			this._align_cls(a)

			//?: {has fully defined}
			a = this._align_to()
			if(a) return win.alignTo(document.body, a)
		}
		//~: save the position
		else this._xy = [x, y]

		//!: assign the position
		win.setXY([x, y])
	},

	_on_wnd_resize    : function(win, w, h)
	{
		if(this._is_skip()) return

		//~: new size
		this._wh = [w, h]

		//?: {has alignment}
		if(ZeT.iss(this._align))
			return this.resizeTo()

		var xy = win.getXY()
		this._on_wnd_move(win, xy[0], xy[1])
	},

	_align_save       : function()
	{
		if(!this._align) return

		this._align_save = this._align
		delete this._align

		if(!this._xy && this.win(false))
			this._xy = this.win().getXY()
	},

	_align_restore    : function()
	{
		if(!this._align_save) return

		this._align = this._align_save
		delete this._align_save

		if(this._xy && this.win(false))
		{
			var xy = this.win().getXY()

			if((xy[0] != this._xy[0]) || (xy[1] != this._xy[1]))
			{
				delete this._align
				delete this._xy
			}
		}
	},

	_on_collapse      : function()
	{
		this._collapsing = true
		this._align_save()
	},

	_on_expand        : function()
	{
		delete this._collapsing
		this._align_restore()
	},

	_on_resize        : function()
	{
		if(this._is_skip()) return
		this.resizeTo()
	},

	_align_cls        : function(a)
	{
		var win = ZeT.assertn(this.win())
		var  el = ZeT.assertn(win.getEl())
		var xcl = (a)?('retrade-winalign-' + a):('')

		//~: remove all existing classes
		ZeTD.eachc(el.dom, function(c)
		{
			if(ZeTS.starts(c, 'retrade-winalign-'))
				if(c != xcl) el.removeCls(c)
		})

		//~: add new class
		if(!ZeTS.ises(xcl))
			el.addCls(xcl)
	}
})


// +----: SelSet :-----------------------------------------------+

/**
 * Selection Set implementation class.
 */
ReTrade.SelSet = ZeT.defineClass('ReTrade.SelSet', {

	init              : function()
	{},

	domain            : function(domain)
	{
		if(!ZeT.iss(domain))
			return this._domain
		this._domain = domain
		return this
	},

	view              : function(view)
	{
		if(ZeTS.ises(view))
			return this._view
		this._view = view
		return this
	},

	model             : function(model)
	{
		if(ZeTS.ises(model))
			return this._model
		this._model = model
		return this
	},

	storeId           : function(storeId)
	{
		if(!storeId) return this._storeId
		this._storeId = ZeT.asserts(storeId)
		return this
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
			return this._place
		this._place = bind
		return this
	},

	/**
	 * Loads Selection Set controls into the placeholder.
	 */
	loadPlace         : function(url)
	{
		if(!this.place() || !this.place().co())
			throw 'SelSet place is not specified or not built yet!'

		var self = this
		Ext.create('Ext.ComponentLoader', {

		  'url': url, target: self.place().co(),
		  autoLoad: true, scripts: true,
		  ajaxOptions: { method: 'GET' }, params: {
		    mode: 'body', view: self.view(),
		    extjs_domain: self.domain()
		  }
		})
	},

	button            : function(nodeid)
	{
		if(!nodeid) return this._toggle_btn
		this._toggle_btn = ZeT.asserts(nodeid)
		return this
	},

	menu              : function(nodeid)
	{
		if(!nodeid) return this._main_menu
		this._main_menu = ZeT.asserts(nodeid)
		return this
	},

	ctlitems          : function(its)
	{
		if(ZeT.iss(its))
			return this._ctl_items && this._ctl_items[its]

		ZeT.assert(ZeT.iso(its))
		this._ctl_items = its
		return this
	},

	buildMenu         : function(model)
	{
		//~: menu ul-node
		var self = this, menu = $('#' + ZeT.asserts(this.menu()))

		//~: menu model
		ZeT.assert(ZeT.isa(model))
		this._menu_model = model

		//~: remove all menu items
		menu.find('li.retrade-selset-menu-item').remove()

		//~: insert all menu items
		ZeT.each(model, function(mi)
		{
			var txt = $('<span/>')
			var ico = $('<span/>').addClass('x-menu-item-checkbox retrade-icon-nav-16')
			var   a = $('<a/>', { href: '#' }).append(ico, txt)
			var  li = $('<li/>').append(a)

			//~: item title
			if(!ZeTS.ises(mi.title))
				txt.text(mi.title)
			//~: item name
			else
				txt.text(ZeT.asserts(mi.name))

			//?: {default item}
			if(ZeTS.ises(mi.name))
				a.attr('title', 'Выборка по умолчанию')

			//?: {current item}
			a.addClass((mi.current === true)?('x-menu-item-checked'):('x-menu-item-unchecked'))

			//~: react on item click
			a.click(function(e)
			{
				e.preventDefault()
				self._menu_item_click(mi)
			})

			//!: add the item
			menu.append(li.addClass('retrade-selset-menu-item'))
		})

		return this
	},

	url               : function(what, url)
	{
		if(ZeTS.ises(what)) return undefined
		if(!this._urls) this._urls = {}

		if(!ZeTS.ises(url))
		{
			this._urls[what] = url
			return this
		}

		return ZeT.asserts(this._urls[what], 'No [', what,
		  '] URL is defined for Selection Set!')
	},

	toggle            : function(active, opts)
	{
		var btn = $('#' + ZeT.asserts(this.button()))

		//?: {has no option}
		if(ZeT.isu(active)) active = !this.active
		this.active = !!active

		//~: update look of the toggle button
		btn.blur().toggleClass('current', this.active)

		if(active) this._open_wnd(opts)
		else       this._close_wnd(opts)

		this._onoff()
		return this
	},

	/**
	 * Adds on-off listener. To remove it,
	 * set second argument true.
	 */
	onoff             : function(f, remove)
	{
		if(!ZeT.isf(f)) return undefined
		if(!this._ons) this._ons = []

		ZeTA.remove(this._ons, f)
		if(remove !== true)
			this._ons.push(f)
		return this
	},

	adder             : function(f)
	{
		if(ZeT.isf(f)) this._adder = f
		return this
	},

	changer           : function(f)
	{
		if(ZeT.isf(f)) this._changer = f
		return this
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
		if(!bind || !bind.extjsfBind) return

		var self = this, acol, onoff

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
			bind.selSetActive = ison

			if(!ison) acol.hide(); else
			{
				acol.show()
				bind.co().getView().refresh()
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
			return this._items

		if(ZeT.isu(op))
		{
			this._items = {}
			op = true
		}

		if(!this._items) this._items = {}

		for(var i = 0;(i < items.length);i++)
			if(op) this._items[items[i]] = true
			else delete this._items[items[i]]

		this._onoff()
		return this
	},

	winmain           : function()
	{
		//?: {has no bind attached}
		if(!this._winmain)
			this._winmain = extjsf.bind('winmain-selset', this.domain())

		return this._winmain
	},

	reload            : function(opts)
	{
		var self    = this
		var winmain = this.winmain()
		var window  = winmain && winmain.co();
		if(!window) return

		//~: clear the window
		winmain.clearComponent({notListeners: true})

		var params = {
		  mode: 'body', domain: self.domain(),
		  view: self.view(), model: self.model()
		}

		//!: reload content
		Ext.create('Ext.ComponentLoader', {
		  target: window, url: self.url('winmain'),
		  ajaxOptions: { method: 'GET' },
		  params: params, autoLoad: true, scripts: true
		})
	},

	_add_object       : function()
	{
		var m  = arguments[5]
		var id = m.get('selsetItemKey') || m.getId()

		if(!m.get('selsetDisabled'))
			this._adder(id, (this._items && (this._items[id] === true)))
	},

	_create_wnd       : function(opts)
	{
		opts = ZeT.deepClone(opts || {})

		var self = this, params = {
			mode: 'body', domain: self.domain(),
			view: self.view(), model: self.model()
		}

		if(opts.params) {
			ZeT.extend(params, opts.params)
			delete opts.params
		}

		var props = ZeT.deepExtend(opts, {
			xtype: 'window', layout: 'fit',
			autoShow: false, cls: 'retrade-selset-window',

			loader: { autoLoad: true, scripts: true, params: params,
				url: self.url('winmain'), ajaxOptions: { method: 'GET' }
			}
		})

		//~: assign previous position and size
		ZeT.extend(props, this._wnd_pos)

		//~: define the window bind
		var winmain = extjsf.domain(self.domain()).
			bind('winmain-selset', new extjsf.Bind())
		winmain.props(props)

		//~: create positioning strategy
		winmain.on('show', function()
		{
			var b = self._wnd_pos
			var a = ZeT.iss(b)?(b):(b && b.align)

			new ReTrade.WinAlign({ window: winmain,
			  align: ZeT.iss(a)?(a):(b)?(null):('rc') })
		})

		//~: close window listener
		winmain.on('beforeclose', function()
		{
			self.toggle(false)
		})

		return winmain
	},

	_open_wnd         : function(opts)
	{
		var winmain = this.winmain()
		if(!winmain) this._winmain = winmain =
		  ZeT.assertn(this._create_wnd(opts))

		//?: {display existing window}
		if(winmain.co()) return winmain.co().toFront()

		//~: create window & display it
		winmain.co(Ext.create('Ext.window.Window', winmain.buildProps()))
		winmain.co().show()
	},

	_close_wnd        : function(opts)
	{
		if(!this._winmain) return

		var winmain = this._winmain
		delete this._winmain
		delete this._wnd_pos

		if(winmain.co())
		{
			var p, wa = winmain.WinAlign
			var box   = winmain.co().getBox()

			//?: {has alignment code}
			if(wa) p = wa.align()

			//?: {save direct position}
			if(!p) p = box; else {
				if(ZeT.iss(p)) p = { align: p }
				p.width  = box.width
				p.height = box.height
			}

			this._wnd_pos = p
			winmain.co().close()
		}
	},

	_onoff            : function(ison)
	{
		if(!this._ons) return
		if(ZeT.isu(ison)) ison = this.active

		for(var i = 0;(i < this._ons.length);i++)
			this._ons[i].call(window, ison)
	},

	_menu_item_click  : function(mi)
	{
		var self = this

		//?: {has tis item checked} do nothing
		if(mi.current === true) return

		//~: invoke the change script
		this._changer(mi.name, function()
		{
			Ext.data.StoreManager.lookup(self.storeId()).load()
			if(self.winmain()) self.reload()
		})
	},

	_sel_col_rnd      : function(col, rec)
	{
		var id = rec.get('selsetItemKey') || rec.getId()
		id = id && parseInt(id)

		if(!id || rec.get('selsetDisabled'))
		{
			col.items[0].disabled = true
			col.items[0].iconCls = 'retrade-selcol-none'

			return
		}

		var on = this._items && (this._items[id] === true)
		if(on) col.items[0].iconCls = 'retrade-selcol-checked'
		else   col.items[0].iconCls = 'retrade-selcol-add'

		col.items[0].disabled = false
	}
})


// +----: SelSet Instance :--------------------------------------+

ReTrade.selset = ZeT.defineInstance('ReTrade.selset', ReTrade.SelSet)


// +----: ReTrade Visual :---------------------------------------+

ReTrade.Visual = ZeT.defineClass('ReTrade.Visual', {

	init              : function(opts)
	{
		this.opts = this.opts || opts || {}
		this._init_struct()
	},

	_init_struct      : function()
	{
		//~: create node by the template
		var t = this._tx()
		var n = t.cloneNode()

		//?: {has id}
		if(this.opts.node)
			n.id = ZeT.asserts(this.opts.node)

		//~: create the structure
		this.struct = new ZeT.Struct(n)

		//?: {has parent}
		var p = this.opts.parent
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
		if(this.$class.static.template)
			return this.$class.static.template

		return this.$class.static.template = new ZeT.Layout.Template(
		  {trace : ZeT.Layout.Template.Ways.traceAtNodes},
		  ZeT.asserts(this._ts, 'Visual template string is required!'))
	}
})


// +----: ReTrade Clocks :---------------------------------------+

ReTrade.Clocks = ZeT.defineClass('ReTrade.Clocks', ReTrade.Visual, {

	init  : function(opts)
	{
		this.$callSuper(opts)

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
		this._time() //<-- set the clocks now
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

	LH2W     : 5/35, //<-- left border aspect ratio
	RH2W     : 5/35, //<-- right border aspect ratio

	init     : function(opts)
	{
		this.$callSuper(opts)

		//~: assign aspect ratios
		if(!ZeT.isn(this.opts.lh2w))
			this.opts.lh2w = this.LH2W
		if(!ZeT.isn(this.opts.rh2w))
			this.opts.rh2w = this.RH2W

		//~: do layout
		if(!this.layout())
			return ZeT.log('Events Number layout is nor ready now!')

		//~: set the default color
		this.color()
	},

	layout   : function()
	{
		var t = this._tx()
		var n = this.struct.node()
		var p = ZeTD.uptag(t.walk('L', n), 'table')
		var h = p.offsetHeight; if(!h) return false

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

	set      : function(v, c)
	{
		//~: set the value
		this._val(v)

		//?: {has color provided}
		if(!ZeT.isu(c))
			this.color(c)

		//?: {has resize callback}
		if(ZeT.isf(this._onresize))
		{
			//?: {is size changed}
			var w; if((w = this.width()) !== this._width)
			{
				this._width = w
				this._onresize.call(this, this, w)
			}
		}

		return this
	},

	color    : function(c)
	{
		if(ZeTS.ises(c)) c = this.opts.defcolor

		if(this._current_color)
		{
			ZeTD.classes(this.struct.node(), '-' + this._current_color)
			this._current_color = null
		}

		if(!ZeTS.ises(c))
		{
			this._current_color = c
			ZeTD.classes(this.struct.node(), ['+retrade-eventsnum', '+' + c])
		}

		return this
	},

	onresize : function(callback)
	{
		this._onresize = callback
		return this
	},

	width    : function()
	{
		return this.struct.node().offsetWidth
	},

	_val     : function(v)
	{
		var n = this._tx().walk('N', this.struct.node())

		if(ZeT.isn(v)) v = '' + v
		ZeTD.update(n, this.value = v)

		//?: {allow shrink}
		if(this.opts.notshrink === false) return
		ZeTD.styles(n, { minWidth: '' + n.offsetWidth + 'px' })
	},

	_ts      : ""+
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


// +----: ReTrade Events Menu :----------------------------------+

ReTrade.EventsMenu = ZeT.defineClass('ReTrade.EventsMenu', ReTrade.Visual, {

	init            : function(opts)
	{
		this.$callSuper(opts)

		//~: add the items
		this._add_items()

		//~: init filter controls
		this._fctl_init()

		//~: init item controls
		this._ictl_init()
	},

	react           : function(reactor)
	{
		ZeT.assert(ZeT.isf(reactor))
		this.reactor = reactor
		return this
	},

	set             : function(model)
	{
		this.model = ZeT.assertn(model)

		model.updated = false
		this.update()
		model.updated = true

		return this
	},

	update          : function()
	{
		ZeT.assertn(this.model)

		//~: update the numbers from the model
		this._numbers()

		//~: fill with the items
		this._set_items()

		//~: set the filter
		this._set_filter()

		return this
	},

	reset           : function()
	{
		this._item_reset()
	},

	/**
	 * Returns array with the displayed model items.
	 */
	page            : function()
	{
		var res = []

		for(var i = 0;(i < this.items.length);i++)
			if(!this.items[i].model) return res
			else res.push(this.items[i].model)

		return res
	},

	first           : function()
	{
		for(var i = 0;(i < this.items.length);i++)
			if(this.items[i].model)
				return this.items[i].model
		return undefined
	},

	last            : function()
	{
		for(var i = this.items.length - 1;(i >= 0);i--)
			if(this.items[i].model)
				return this.items[i].model
		return undefined
	},

	COLORS          : ['N', 'G', 'O', 'R'],

	_numbers        : function()
	{
		var n = this.struct.node()
		var t = this._tx()

		var numbers = this.model.numbers || {}
		ZeT.each(this.COLORS, function(c)
		{
			ZeTD.update(t.walk('F'+c, n), numbers[c])
		})
	},

	_set_items      : function()
	{
		var m = this.model, ids = {}

		if(ZeT.isu(m.items)) m.items = []
		ZeT.assert(ZeT.isa(m.items))

		//~: check & map the items of the model
		for(var i = 0;(i < m.items.length);i++)
		{
			var x = ZeT.assertn(m.items[i])
			ZeT.assertn(x.id)   //<-- {id}
			ZeT.asserts(x.time) //<-- {time}
			ZeT.asserts(x.date) //<-- {date}
			ZeT.asserts(x.text) //<-- {text}

			if(ZeT.isu(x.color)) x.color = 'N'
			ZeT.assert(this.COLORS.indexOf(x.color) >= 0)

			//~: map (id - > item)
			ids[x.id] = x
		}

		//?: {has first item provided}
		if(m.firstid && !m.updated)
			this.firstid = m.firstid

		//?: {has the first there} take it
		var e, b = m.items.indexOf(ids[this.firstid])
		if(b < 0) b = 0

		//~: the end border
		if(!m.items[b]) { this.firstid = null; e = 0 } else
		{
			this.firstid = ZeT.assertn(m.items[b].id)
			e = b + this.opts.length
			if(e > m.items.length) e = m.items.length
		}

		//~: set the items in the range
		for(i = b;(i < e);i++)
			this._set_item(i-b, m.items[i])

		//~: show-hide items
		for(i = 0;(i < this.items.length);i++)
			if(b + i < e) $(this.items[i].node).show(); else
			{
				$(this.items[i].node).hide()
				delete this.items[i].model
			}

		//~: reset current item
		this._item_reset()
	},

	_set_item       : function(i, m)
	{
		var x = ZeT.assertn(this.items[i])
		var t = this._ti()

		//~: assign the model
		x.model = ZeT.assertn(m)

		//=: time, date, text
		ZeTD.update(t.walk('T', x.node), m.time)
		ZeTD.update(t.walk('D', x.node), m.date)
		ZeTD.update(t.walk('M', x.node), m.text)

		//~: remove all the color classes
		var rm; if(!(rm = this.$class.static._colors_rm))
		{
			rm = this.$class.static._colors_rm = []
			ZeT.each(this.COLORS, function(c) { rm.push('-'+c) })
		}

		//~: set item color as the class
		ZeTD.classes(x.node, rm)
		ZeTD.classes(x.node, '+'+m.color)
	},

	_set_filter     : function()
	{
		if(!this.model.filter) this.model.filter = 'N'
		ZeT.assert(this.COLORS.indexOf(this.model.filter) >= 0)

		//~: select the filtering node
		var self = this; ZeT.each(this.COLORS, function(c)
		{
			var p = self._tx().walk('F'+c, self.struct.node()).parentNode.parentNode
			ZeTD.classes(p, (c == self.model.filter)?('+selected'):('-selected'))
		})

		//~: disable previous-next buttons
		ZeTD.classes(this._tx().walk('LT', this.struct.node()),
		  (this._newer())?('-disabled'):('+disabled'))
		ZeTD.classes(this._tx().walk('RT', this.struct.node()),
		  (this._older())?('-disabled'):('+disabled'))
	},

	_newer          : function()
	{
		if(!this.model) return undefined
		if(!ZeT.isx(this.model.newer)) return true

		var f = this.first(); if(!f) return undefined
		var i = this.model.items.indexOf(f)

		ZeT.assert(i >= 0)
		return (i != 0)
	},

	_older          : function()
	{
		if(!this.model) return undefined
		if(!ZeT.isx(this.model.older)) return true

		var f = this.last(); if(!f) return undefined
		var i = this.model.items.indexOf(f)

		ZeT.assert(i >= 0)
		return (i < this.model.items.length - 1)
	},

	_react          : function(type, event)
	{
		ZeT.asserts(type); ZeT.assertn(event)
		event.type = type
		event.that = this

		if(ZeT.isf(this.reactor))
			this.reactor(event)
	},

	_add_items      : function()
	{
		var t = this._ti()
		var r = ZeT.assertn(this._tx().walk('EV', this.struct.node()))

		//?: {has no items length}
		ZeT.assert(ZeT.isi(this.opts.length) && (this.opts.length > 0),
		  'Events Menu has no length option!')

		//~: create the items
		this.items = []
		for(var i = 0;(i < this.opts.length);i++)
		{
			var n = t.cloneNode()

			//~: hide & attach, listen click
			$(n).hide().data({ EventsMenu: this, index: i }).
			  click(this._item_click).
			  mouseenter(this._item_mouse_in)

			//~: add to the table
			r.appendChild(n)
			this.items.push({ node: n, index: i })
		}
	},

	_item_click     : function(e)
	{
		var n = $(this), self = n.data('EventsMenu')
		if(!self) return; else e.stopPropagation()
		var i = self.items[ZeT.assertn(n.data('index'))]

		//~: reset the current item
		var x = self.clicked
		self._item_reset()

		//?: {clicked item released}
		if(x === i) return

		//~: on item
		ZeTD.classes((self.clicked = i).node, '+clicked')
		self._item_ctl(i, true)
	},

	_item_mouse_in  : function()
	{
		var n = $(this), self = n.data('EventsMenu')
		if(!self || self.clicked) return
		var i = self.items[ZeT.assertn(n.data('index'))]

		if(self.over)
			if(self.over === i) return
			else self._item_reset()

		//~: on item
		ZeTD.classes((self.over = i).node, '+clicked')
		self._item_ctl(i, true, true)
	},

	_item_reset     : function()
	{
		if(this.clicked)
		{
			this._item_ctl(this.clicked, false)
			ZeTD.classes(this.clicked.node, '-clicked')
			delete this.clicked
		}

		if(this.over)
		{
			this._item_ctl(this.over, false)
			ZeTD.classes(this.over.node, '-clicked')
			delete this.over
		}
	},

	_fctl_init      : function()
	{
		//~: assign filter listeners
		var self = this; ZeT.each(this.COLORS, function(c)
		{
			$(self._tx().walk('F'+c, self.struct.node())).
			  click(ZeT.fbind(self._fctl_click, self, c))
		})

		//~: assign prev and next page listeners
		$(this._tx().walk('LT', this.struct.node())).
		  click(ZeT.fbind(this._page_click, this, false))
		$(this._tx().walk('RT', this.struct.node())).
		  click(ZeT.fbind(this._page_click, this, true))
	},

	_fctl_click     : function(color)
	{
		if(this.model && (this.model.filter != color))
			this._react('filter', { 'color': color })
	},

	_page_click     : function(next)
	{
		if(!next && this._newer())
			this._react('page', { newer: true })

		if(next  && this._older())
			this._react('page', { older: true })
	},

	_item_ctl       : function(i, ison, temp)
	{
		var n = $(this._ti().walk('I', i.node))
		var c = $(this._tx().walk('IC', this.struct.node()))

		//~: hide-show numbers, show-hide mass-controls
		if(!temp)
		{
			$(this._tx().walk('NU', this.struct.node())).toggle(!ison)
			$(this._tx().walk('AL', this.struct.node())).toggle(ison)
		}

		c.toggle(ison); if(!ison) return

		//~: set proper y-position
		c.offset({top: n.offset().top + (n.outerHeight() - c.outerHeight())/2})

		//~: set all enabled by default
		c.children().addClass('enabled')

		//~: disable the one of the color
		c.children('.'+ i.model.color).removeClass('enabled')

		//~: scripted class
		ZeTD.classes(c[0], (ZeTS.ises(i.model.script)?('-'):('+')) + 'scripted')
	},

	_ictl_init      : function()
	{
		var t = this._tx(), self = this

		$(t.walk('IA', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'action'))

		$(t.walk('IN', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'N'))

		$(t.walk('IG', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'G'))

		$(t.walk('IO', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'O'))

		$(t.walk('IR', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'R'))

		$(t.walk('ID', this.struct.node())).
		  click(ZeT.fbind(this._ictl_click, this, 'delete'))

		$(t.walk('AN', this.struct.node())).click(function()
		{
			self._react('page-color', { color: 'N' })
		})

		$(t.walk('AD', this.struct.node())).click(function()
		{
			self._react('page-delete', {})
		})
	},

	_ictl_click     : function(ctl, e)
	{
		//?: {has no item selected}
		var i = this.clicked || this.over
		if(!i) return
		e.stopPropagation()

		//?: {delete}
		if(ctl == 'delete')
			this._react('item-delete', { item: i.model })
		//?: {action}
		else if(ctl == 'action')
			this._react('item-action', { item: i.model })
		//?: {control is a color}
		else if(this.COLORS.indexOf(ctl) >= 0)
			if(i.model.color != ctl)
				this._react('item-color', { item: i.model, color: ctl })
	},

	_ti             : function()
	{
		if(this.$class.static.itemplate)
			return this.$class.static.itemplate

		return this.$class.static.itemplate = new ZeT.Layout.Template(
		  {trace : ZeT.Layout.Template.Ways.traceAtNodes, downtag: 'tr'}, this._tis)
	},

	_ts             : ""+
		"<div class='retrade-eventsnum-menu-area'>"+
		"  <div class='retrade-eventsnum-menu-a'></div>"+
		"  <div class='retrade-eventsnum-menu-b'></div>"+
		"  <div class='retrade-eventsnum-menu-c'></div>"+
		"  <div class='retrade-eventsnum-menu-d'></div>"+
		"  <div class='retrade-eventsnum-menu-e'></div>"+
		"  <div class='retrade-eventsnum-menu-item-controls' style='display:none'>@IC"+
		"    <div class='script enabled' title='Выполнить действие для сообщения'>@IA</div>"+
		"    <div class='N enabled' title='Отметить сообщение как неактивное'>@IN</div>"+
		"    <div class='G enabled' title='Отметить сообщение как успех'>@IG</div>"+
		"    <div class='O enabled' title='Отметить сообщение как важное'>@IO</div>"+
		"    <div class='R enabled' title='Отметить сообщение как срочное'>@IR</div>"+
		"    <div class='delete enabled' title='Удалить сообщение'>@ID</div>"+
		"  </div>"+
		"  <table cellpadding='0' cellspacing='0' border='0' class='retrade-eventsnum-menu'>"+
		"    <tbody><!--@EV-->"+
		"      <tr>"+
		"        <td>"+
		"          <div class='retrade-eventsnum-controls'>"+
		"            <div class='left' title='Следующие сообщения'>@LT</div>"+
		"            <div class='right' title='Предыдущие сообщения'>@RT</div>"+
		"            <div class='numbers'>@NU"+
		"              <table cellspacing='0' cellpadding='0' border='0'><tr>"+
		"                <td class='N selected'><div title='Отображать все сообщения'><div>@FN</div></div></td>"+
		"                <td class='G'><div title='Отображать сообщения об успехе'><div>@FG</div></div></td>"+
		"                <td class='O'><div title='Отображать важные и срочные сообщения'><div>@FO</div></div></td>"+
		"                <td class='R'><div title='Отображать срочные сообщения'><div>@FR</div></div></td>"+
		"              </tr></table>"+
		"            </div>"+
		"            <div class='allitems' style='display:none'>@AL"+
		"              <div class='descr'>Для всей<br/>страницы</div>"+
		"              <div class='gray' title='Отметить все собщения на странице как неактивные'>@AN</div>"+
		"              <div class='delete' title='Удалить все собщения на странице'>@AD</div>"+
		"            </div>"+
		"          </div>"+
		"        </td>"+
		"      </tr>"+
		"    </tbody>"+
		"  </table>"+
		"</div>",

	_tis            : ""+
		"<table><tr class='retrade-eventsnum-menu-item'>"+
		"  <td><div class='retrade-eventsnum-menu-data-ext'>"+
		"    <div class='retrade-eventsnum-menu-data'>@I"+
		"      <span><span>@T</span><span>@D</span></span>"+
		"      <span></span><span>@M</span></div>"+
		"</div></td></tr></table>"
})


// +----: ReTrade Events Control :-------------------------------+

ReTrade.EventsControl = ZeT.defineClass('ReTrade.EventsControl',
{
	init            : function(opts)
	{
		ZeT.assertn(this.opts = opts)
		ZeT.assertn(this.opts.number, 'No options for Events Clocks!')
		ZeT.assertn(this.opts.menu, 'No options for Events Menu!')

		//~: create the number
		this.number = new ReTrade.EventsNumber(this.opts.number)

		//~: create the menu
		this.menu = new ReTrade.EventsMenu(this.opts.menu)

		//~: init the controls
		this._init_ctl()

		//~: set the interval timer
		setInterval(ZeT.fbind(this._interval, this), this.opts.interval || 1000)
	},

	toggle          : function(show)
	{
		this._menu_toggle(show)
		return this
	},

	set             : function(model)
	{
		ZeT.assertn(model)
		ZeT.assert(ZeT.isi(model.txn), 'Model transaction number is not defined!')

		//~: update the model
		this.menu.set(model)
		this._interval()

		//~: update the first item in the proxy
		if(this.opts.proxy) this.opts.proxy.
		  pageSize(this.menu.opts.length).
		  first(this.menu.firstid)

		return this
	},

	/**
	 * Updates not all the model, but just the numbers section.
	 * If transaction number changes, may execute request to
	 * the data proxy.
	 */
	numbers         : function(model)
	{
		ZeT.assertn(model)
		ZeT.assertn(this.menu.model)

		//=: numbers
		this.menu.model.numbers = ZeT.assertn(model.numbers)

		//?: {has tx-number}
		var istxn; if(!ZeT.isu(model.txn))
		{
			ZeT.assert(ZeT.isi(model.txn))

			//=: tx-number
			istxn = (this.menu.model.txn != model.txn)
			this.menu.model.txn = model.txn
		}

		//~: update the menu
		this.menu.update()

		//?: {has tx-number changed & page not full}
		ZeT.assert(ZeT.isi(this.menu.opts.length))
		if(istxn && this.opts.proxy)
			if(this.menu.page().length != this.menu.opts.length)
				this.opts.proxy.fetch('older') //<-- always older is needed

		return this
	},

	_init_ctl       : function()
	{
		//~: on document click
		$(document).click(ZeT.fbind(this._doc_click, this))

		//~: on number area click
		$(this.number.struct.node()).
		  click(ZeT.fbind(this._num_click, this))

		//~: react on menu events
		this.menu.react(ZeT.fbind(this._react, this))
	},

	_num_click      : function(e)
	{
		this._menu_toggle(!$(this.menu.struct.node()).is(':visible'))
		e.stopPropagation()
	},

	_menu_toggle    : function(show)
	{
		var m = $(this.menu.struct.node())

		if(!show) m.hide(); else
		{
			var n = $(this.number.struct.node())
			var o = this.menu.opts.offset
			if(!ZeT.isn(o)) o = 0

			if(ZeT.isf(this.opts.onshow))
				this.opts.onshow(m, this)

			this.menu.reset()

			m.show().offset({
			  top : n.offset().top  + n.outerHeight() + 2,
			  left: n.offset().left + n.outerWidth()  - m.outerWidth() + o
			})
		}
	},

	_doc_click      : function(e)
	{
		var m = $(this.menu.struct.node())

		//?: {got out of the menu}
		if(m.is(':visible') && !ZeTD.inx(m, e))
			m.hide()
	},

	_interval       : function()
	{
		if(!this.interval) this.interval = { i: 0 }

		//?: {even tick} shift the number (each 2 secs)
		if(this.interval.i%2 == 0)
			this._shift_number()

		this.interval.i++ //<-- before the pause

		//~: refresh numbers pause (30 intervals)
		var rp = (this.opts.pause || 30)
		ZeT.assert(ZeT.isi(rp) && (rp > 0))
		if(this.interval.i%rp == 0)
			if(this.opts.proxy)
				this.opts.proxy.numbers()
	},

	_shift_number   : function()
	{
		var c = this.interval.color
		if(!c) c = this.number.opts.defcolor
		if(!c) c = this.menu.COLORS[0]

		var i = this.menu.COLORS.indexOf(c)
		ZeT.assert(i >= 0)

		var x; for(var j = 0;!x && (j < this.menu.COLORS.length);j++)
		{
			if(++i >= this.menu.COLORS.length) i = 0
			var n = ZeT.get(this.menu, 'model', 'numbers', this.menu.COLORS[i])

			if(ZeT.isi(n) && (n > 0))
				x = { color: this.menu.COLORS[i], number: n }
		}

		if(!x) this.number.color(c); else
		{
			this.number.set(x.number, x.color)
			this.interval.color = x.color
		}
	},

	_react          : function(e)
	{
		if(e.type == 'item-action')
			this._do_action(e)
		else if(e.type == 'item-delete')
			this._do_item_delete(e)
		else if(e.type == 'page-delete')
			this._do_page_delete(e)
		else if(e.type == 'item-color')
			this._do_item_color(e)
		else if(e.type == 'page-color')
			this._do_page_color(e)
		else if(e.type == 'filter')
			this._do_filter(e)
		else if(e.type == 'page')
			this._do_page(e)
	},

	_do_action      : function(e)
	{
		if(!ZeTS.ises(e.item.script))
			ZeT.xeval(e.item.script)
	},

	_do_item_delete : function(e)
	{
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)
		var its = ZeT.assertn(this.menu.model.items)

		//~: search for the item to delete
		var i = its.indexOf(e.item)
		ZeT.assert(i >= 0)

		//~: the first item
		var f = this.menu.first()

		//!: remove that item
		its.splice(i, 1)

		//?: {the item removed was the first one}
		if(f === e.item)
			//?: {has more items}
			if(i < its.length)
				this.menu.firstid = ZeT.assertn(its[i].id)
			//~: take the previous page
			else if(its.length)
			{
				ZeT.assert(ZeT.isi(this.menu.opts.length))
				i = its.length - this.menu.opts.length
				this.menu.firstid = ZeT.assertn(its[(i >= 0)?(i):(0)].id)
			}

		//!: update the menu
		this.menu.update()

		//~: update the data proxy
		if(this.opts.proxy) this.opts.proxy.
		  first(this.menu.firstid).
		  remove([e.item])
	},

	_do_page_delete : function(e)
	{
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)
		var its  = ZeT.assertn(this.menu.model.items)
		var page = ZeT.asserta(e.that.page())

		//~: remove the slice of items
		var i = its.indexOf(page[0])
		ZeT.assert(i >= 0)
		for(var j = 0;(j < page.length);j++)
			ZeT.assert(its[i + j] === page[j])
		its.splice(i, page.length)

		//~: take the next or previous page
		if(its.length && (i + 1 >= its.length))
		{
			ZeT.assert(ZeT.isi(this.menu.opts.length))
			i = its.length - this.menu.opts.length
			this.menu.firstid = ZeT.assertn(its[(i >= 0)?(i):(0)].id)
		}

		//!: update the menu
		this.menu.update()

		//~: update the data proxy
		if(this.opts.proxy) this.opts.proxy.
		  first(this.menu.firstid).
		  remove(page)
	},

	_do_item_color  : function(e)
	{
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)

		//~: update the color in the model item
		e.item.color = e.color
		this.menu.update()

		//~: update the data proxy
		if(this.opts.proxy)
			this.opts.proxy.color([e.item])
	},

	_do_page_color  : function(e)
	{
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)

		//~: update the color in the model items of the page
		var page = this.menu.page()
		ZeT.each(page, function(i){ i.color = e.color })
		this.menu.update()

		//~: update the data proxy
		if(this.opts.proxy)
			this.opts.proxy.color(page)
	},

	_do_filter      : function(e)
	{
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)

		//~: display filter color in the model
		this.menu.model.filter = e.color
		e.that.update()

		//~: update the data proxy
		if(this.opts.proxy)
			this.opts.proxy.filter(e.color)
	},

	_do_page        : function(e)
	{
		ZeT.assert(e.newer ^ e.older)

		//~: access the page of the menu
		ZeT.assert(e.that == this.menu)
		ZeT.assertn(this.menu.model)
		var its  = ZeT.assertn(this.menu.model.items)
		var page = ZeT.asserta(e.that.page())

		//~: current position & page size
		var i    = its.indexOf(page[0])
		ZeT.assert(i >= 0)
		var P    = this.menu.opts.length
		ZeT.assert(ZeT.isi(P) && (P > 0))
		var f    = false //<-- fetch data

		//?: {move left}
		if(e.newer)
		{
			i -= P; if(i < P) f = 'newer'
			this.menu.firstid = ZeT.assertn(its[(i >= 0)?(i):(0)].id)
		}

		//?: {move right}
		if(e.older)
		{
			i += P; if(i + 2*P > its.length) f = 'older'

			if(i < its.length)
				this.menu.firstid = ZeT.assertn(its[i].id)
		}

		//~: update the menu
		this.menu.update()

		if(this.opts.proxy)
		{
			//~: update the first position in the proxy
			this.opts.proxy.first(this.menu.firstid)

			//?: {fetch new data}
			if(f) this.opts.proxy.fetch(f)
		}
	}
})


// +----: ReTrade Events Data Proxy :----------------------------+

ZeT.defineClass('ReTrade.EventsDataProxy',
{
	pageSize        : function(ps)
	{
		ZeT.assert(ZeT.isi(ps))
		ZeT.assert(ps > 0)
		this._page = ps
		return this
	},

	first           : function(id)
	{
		this._first = id
		return this
	},

	remove          : function(items)
	{
		ZeT.asserta(items)

		//~: issue the request
		this.request('delete', this._query(
		  ZeT.map(items, 'id').join(' ')
		))
	},

	color           : function(items)
	{
		ZeT.asserta(items)

		//~: build the query parameters
		var query = []; ZeT.each(items, function(i)
		{
			query.push(ZeT.assertn(i.id))
			query.push(ZeT.asserts(i.color))
		})

		//~: issue the request
		this.request('color', query.join(' '))
	},

	fetch           : function(dir)
	{
		ZeT.assert((dir === 'older') || (dir === 'newer'))

		//~: issue the request
		this.request('fetch', this._query(dir))
	},

	filter          : function(color)
	{
		ZeT.assert(['R','G','O','N'].indexOf(color) >= 0)
		this._color = color

		//~: issue the request
		this.request('filter', this._query())
	},

	numbers         : function()
	{
		//~: issue the request
		this.request('numbers')
	},

	request         : function(task, query)
	{},

	_query          : function(str)
	{
		var f = this._first || '?'
		var c = ZeT.asserts(this._color || 'N')

		return ZeTS.cat('>', f, ' ', c, '; ', str)
	}
})


// +----: ReTrade Tiles :----------------------------------------+

/**
 * Creates the tiles component. Each tile has content
 * to place in the limited space of a tile. Tiles are
 * re-sized from minimum width + height up to the
 * maximum ones. Tiles are placed in the container
 * from left-top corner to right and down. Layout
 * is flexible, it's so to make the tiles appear
 * uniformly in the area container.
 *
 * The following parameters are obligatory:
 *
 * · min   [w, h, w%, h%]
 *
 *   array of the minimum dimensions. First
 *   two numbers are pixels, second two are
 *   optional percents of the container
 *   inner dimensions free space;
 *
 * · max   [w, h, w%, h%]
 *
 *   the same as 'min', but for the maximum
 *   dimensions of a tile. Defaults to minimum;
 *
 * · area  DOM node or ID
 *
 *   string ID or node of the container to place
 *   the tiles into.
 *
 *
 * Optional parameters are:
 *
 * · cellClass, cellStyle
 *
 *   CSS class name, see ZeTD.classes(); and
 *   CSS styles object, see ZeTD.styles() of
 *   every cell of the layout table;
 *
 * · tableClass, tableStyle
 *
 *   CSS class name and styles of the layout table
 *   created in the container area each time the
 *   layout geometry is updated;
 *
 * · rows    function(r, R, H) : integer
 *
 *   function that selects integer value of rows
 *   number between the values range given. By
 *   default the maximum value is selected;
 *
 * · columns function(c, C, W) : integer
 *
 *   same as rows, but selects the number of
 *   columns of the range given;
 *
 * · absolute (default is false)
 *
 *   orders for each cell of the tiles grid
 *   to get absolute position relative to
 *   the area is also being absolute.
 */
ReTrade.Tiles = ZeT.defineClass('ReTrade.Tiles', {

	ReTradeTiles      : true,

	init              : function(opts)
	{
		ZeT.assert(ZeT.iso(opts))
		this.opts = opts

		//~: inspect the dimensions range
		this._check_min_max(opts.min)
		if(opts.max)
			this._check_min_max(opts.max)

		//~: area
		this.area = opts.area
		if(ZeT.iss(this.area))
			this.area = ZeTD.n(this.area)
		ZeT.assert(ZeTD.isn(this.area),
		  'Did not found valid ReTrade.Tiles area element!')

		//?: {has has ID, save it}
		if(!ZeTS.ises(ZeTD.attr(this.area, 'id')))
			this.area = ZeTD.attr(this.area, 'id')

		function mIn() { return arguments[0] }
		function mAx() { return arguments[1] }

		//~: rows strategy
		if(opts.rows == 'min') opts.rows = mIn; else
			if(opts.rows == 'max') opts.rows = mAx

		//~: columns strategy
		if(opts.columns == 'min') opts.columns = mIn; else
			if(opts.columns == 'max') opts.columns = mAx
	},

	/**
	 * Invoke each time you want to update the
	 * geometry of the tiles layout table.
	 *
	 * Returns false when layout was not possible.
	 */
	layout            : function()
	{
		//~: do initial layout
		if(this.updateLayout() === false)
			return false

		//?: {have justification callback}
		if(ZeT.isf(this.beforejustify))
			if(this.beforejustify(this) === false)
				return false

		//~: and justify it
		if(!ZeT.isu(this._justify()))
			return false

		//?: {has layout callback}
		if(ZeT.isf(this.onlayout))
			this.onlayout(this)

		return this
	},

	columns           : function()
	{
		return this.grid && this.grid[0]
	},

	rows              : function()
	{
		return this.grid && this.grid[1]
	},

	/**
	 * Installs callback for tile events.
	 * Each event instance consists of:
	 *
	 * · type    type of event;
	 * · event   jQuery event object;
	 * · cell    tile cell node;
	 * · column  tile cell column;
	 * · row     tile cell row;
	 * · tiles   this Tiles instance.
	 *
	 * Event types are: 'click' mouse click,
	 * 'enter' mouse enter, 'leave' mouse leave.
	 */
	on                : function(cb)
	{
		if(ZeT.isx(cb))
		{
			delete this._ontile
			return this
		}

		ZeT.assert(ZeT.isf(cb))
		this._ontile = cb

		return this
	},

	/**
	 * Callback that invoked for each grid cell.
	 * Parameters are: $(node), x (column index)
	 * and y (row index) starting with 0.
	 * Return false to stop iteration.
	 */
	each              : function(f)
	{
		var x = 0, y = 0, br, self = this
		ZeT.assert(ZeT.isf(f))

		function ic()
		{
			return br = f($(this), x++, y)
		}

		function ir()
		{
			$(this).children().each(ic)
			x = 0; y++; return br
		}

		this._get_grid().children().each(ir)
		return this
	},

	updateLayout      : function()
	{
		//~: get the size of area
		if(!ZeT.isu(this._area_size()))
			return false

		//~: calculate cell min-max
		this._min_max()

		//~: grid dimensions
		this.grid = this._calc_grid()
		ZeT.assert(this.grid[0] >= 1)
		ZeT.assert(this.grid[1] >= 1)

		//~: place the grid
		this._place_grid()
	},

	_area_size        : function()
	{
		var g = this._get_grid()
		this.W = g.innerWidth()
		this.H = g.innerHeight()

		//?: {has no place}
		if(!this.W || !this.H)
			return ZeTS.cat('ReTrade.Tiles area [',
			  this.area, '] grid has zero dimensions!')
	},

	_check_min_max    : function(m)
	{
		ZeT.assert(ZeT.isa(m))
		ZeT.assert(m.length >= 2)
		ZeT.assert(ZeT.isn(m[0]) && (m[0] > 1))
		ZeT.assert(ZeT.isn(m[1]) && (m[1] > 1))

		if(m.length == 2)
			m.push(0, 0)
		else
		{
			ZeT.assert(m.length == 4)
			ZeT.assert(ZeT.isn(m[2]) && m[2] >= 0)
			ZeT.assert((m[2] >= 0) && (m[2] <= 100))
			ZeT.assert(ZeT.isn(m[3]) && m[3] >= 0)
			ZeT.assert((m[3] >= 0) && (m[3] <= 100))
		}
	},

	_min_max          : function()
	{
		var m = this.opts.min
		var M = this.opts.max
		var W = this.W, H = this.H
		ZeT.assert(ZeT.isn(W) && ZeT.isn(H))

		function calc(X, x, p)
		{
			var d = Math.floor(X / x)
			if(d < 1) return X
			var a = (X - x*d) * p / (100 * d)
			return x + a
		}

		var min = [ calc(W, m[0], m[2]), calc(H, m[1], m[3]) ]
		if(!M) return this.min = this.max = min
		var max = [ calc(W, M[0], M[2]), calc(H, M[1], M[3]) ]

		this.min = [ Math.min(min[0], max[0]), Math.min(min[1], max[1]) ]
		this.max = [ Math.max(min[0], max[0]), Math.max(min[1], max[1]) ]
	},

	/**
	 * Finds the best number of columns
	 * and rows in the table.
	 */
	_calc_grid        : function()
	{
		var C = Math.max(Math.floor(this.W / this.min[0]), 1)
		var c = Math.max(Math.ceil(this.W / this.max[0]), 1)
		if(c > C) c = C

		if(ZeT.isf(this.opts.columns) && (c != C))
		{
			var x = this.opts.columns(c, C, this)
			ZeT.assert(ZeT.isi(x))
			ZeT.assert((x >= c) && (x <= C))
			c = C = x
		}

		var R = Math.max(Math.floor(this.H / this.min[1]), 1)
		var r = Math.max(Math.ceil(this.H / this.max[1]), 1)
		if(r > R) r = R

		if(ZeT.isf(this.opts.rows) && (r != R))
		{
			var y = this.opts.rows(r, R, this)
			ZeT.assert(ZeT.isi(y))
			ZeT.assert((y >= r) && (y <= R))
			r = R = y
		}

		return [ Math.max(c, C), Math.max(r, R) ]
	},

	_get_grid        : function()
	{
		var g, self = this

		$(ZeTD.n(this.area)).children().each(function()
		{
			if($(this).data('ReTrade.Tiles') === self)
				{ g = $(this); return false }
		})

		//?: {found it}
		if(g) return g

		//~: create the table
		g = $('<div>')

		//~: classes + styles
		ZeTD.classes(g[0], this.opts.tableClass)
		ZeTD.styles (g[0], this.opts.tableStyle)

		//~: assign this tiles
		g.data('ReTrade.Tiles', this)

		//~: append the component
		$(ZeTD.n(this.area)).append(g)
		return g
	},

	_place_grid       : function()
	{
		//~: search for existing grid
		var self = this, g = this._get_grid()

		//~: select the rows
		var rows = g.children()

		//?: {has to delete some}
		if(rows.length > this.grid[1])
			rows.slice(this.grid[1]).remove()
		//~: insert new rows
		else if(rows.length < this.grid[1])
			ZeT.scope(function()
			{
				for(var i = rows.length;(i < self.grid[1]);i++)
					g.append($('<div/>'))
				rows = g.children()
			})

		//c: update columns of each row
		rows.each(function(y)
		{
			//~: select the columns
			var cols = $(this).children()

			//?: {has some to delete}
			if(cols.length > self.grid[0])
				cols.slice(self.grid[0]).remove()
			//~: insert new columns
			else for(var x = cols.length;(x < self.grid[0]);x++)
			{
				var cell = self._cell(x, y)
				$(this).append(cell)
			}
		})
	},

	_cell             : function(x, y)
	{
		var self = this, cell = $('<div/>')

		//~: assign the styles
		ZeTD.classes(cell[0], this.opts.cellClass)
		ZeTD.styles(cell[0], this.opts.cellStyle)

		//~: this + coordinates
		cell.data('ReTrade.Tiles', this)
		cell.data('ReTrade.Tiles.xy', [x, y])

		function cb(type)
		{
			return ZeT.fbindu(self._event, 0, self, 1, cell[0], 2, type)
		}

		//~: bind events
		cell.on({
			click       : cb('click'),
			mouseenter  : cb('enter'),
			mouseleave  : cb('leave')
		})

		return cell
	},

	_event            : function(self, cell, type, e)
	{
		//?: {not target cell node}
		if(this !== cell) return

		//~: access tile callback
		var on = self._ontile
		if(!ZeT.isf(on)) return

		//~: cell xy
		var xy = $(cell).data('ReTrade.Tiles.xy')
		ZeT.assert(ZeT.isa(xy) && (xy.length == 2))

		//!: invoke the callback
		on({ type: type, event: e, cell: cell,
		  column: xy[0], row: xy[1], tiles: self
		})
	},

	_justify          : function()
	{
		//~: cell width and height
		var w = this.W / this.grid[0]
		var h = this.H / this.grid[1]
		w = Math.max(this.min[0], Math.min(this.max[0], w))
		h = Math.max(this.min[1], Math.min(this.max[1], h))

		//~: gaps between the cells
		var dx = 0; if(this.grid[0] > 1)
			dx = (this.W - w*this.grid[0])/(this.grid[0] - 1)
		var dy = 0; if(this.grid[1] > 1)
			dy = (this.H - h*this.grid[1])/(this.grid[1] - 1)

		//~: access the grid elements
		var self = this
		var g    = this._get_grid()
		var pos  = (this.opts.absolute)?('absolute'):('relative')

		//~: place all the rows
		g.children().each(function(y)
		{
			$(this).css({ position: pos,
			  left: '0px', top: (y*h + y*dy)+'px',
			  height: h+'px', width: self.W+'px'
			})

			//~: place all the cells in the row
			$(this).children().each(function(x)
			{
				$(this).css({ position: pos,
				  left: (x*w + x*dx)+'px', top: '0px',
				  height: h+'px', width: w+'px'
				})
			})
		})
	}
})


// +----: ReTrade Tiles Control :-------------------------+

/**
 * Controller of Tiles component. Supports
 * filling tiles, scrolling and navigation.
 *
 * The following parameters are obligatory:
 *
 * · tiles    ReTrade.Tiles or { options }
 *
 *   defines the Tiles or create options;
 *
 * · content  TilesItem or { options }
 *
 *   Provides the content of each tile of the
 *   component. The id is provided by the
 *   scrolling strategy, or it's simply
 *   sequential index starting from 0
 *   and going top-right-down.
 *
 *
 * Optional parameters are:
 *
 * · onupdate function(this)
 *
 *   invoked each time the tiles are updated.
 *
 * · beforejustify function(this)
 *
 *   invoked before tiles justification.
 *   Here rows and columns number iis known.
 *   Allows to assign the borders of the area;
 *
 * · scrollInterval     milliseconds
 *
 *   tels the repeat rate of scroll control
 *   down (pressed) action. Defaults to 250 ms.
 */
ReTrade.TilesControl = ZeT.defineClass('ReTrade.TilesControl', {

	init              : function(opts)
	{
		var self = this
		ZeT.assert(ZeT.iso(opts))
		this.opts = opts

		//~: take tiles | create them
		ZeT.assert(ZeT.iso(opts.tiles))
		if(opts.tiles.ReTradeTiles === true)
			this.tiles = opts.tiles
		else
			this.tiles = new ReTrade.Tiles(opts.tiles)

		//~: tiles update
		if(ZeT.isf(opts.onupdate))
			this.tiles.onlayout = function()
			{
				opts.onupdate(self)
			}

		//~: justification callback
		if(ZeT.isf(opts.beforejustify))
			this.tiles.beforejustify = function()
			{
				opts.beforejustify(self)
			}

		//~: content provider
		ZeT.assert(ZeT.iso(opts.content))
		if(opts.content.ReTradeTilesItem === true)
			this.content = opts.content
		else
			this.content = new ReTrade.TilesItem(opts.content)

		//~: bind self to the content provider
		this.content.tiles   = this.tiles
		this.content.control = this

		//~: attach tiles events listener
		this.tiles.on(ZeT.fbind(this._ontiles, this))
	},

	update            : function()
	{
		var self = this, layout = this.tiles.layout()

		//~: make the layout, then traverse
		layout && layout.each(function(tile, x, y)
		{
			var id = self.content.scroll(x, y, self)
			self.content.provide(id, tile, self)
		})

		return this
	},

	rows              : function()
	{
		return this.tiles.rows()
	},

	columns           : function()
	{
		return this.tiles.columns()
	},

	scrollStart       : function(left, on)
	{
		var self = this

		function scroll()
		{
			var data = self.content._data
			data.offset(data.offset() + ((left)?(-1):(1)))
			self.update()
		}

		if(on && ZeT.isx(this.scrollTimer))
			this.scrollTimer = setInterval(
			  scroll, this.opts.scrollInterval || 250)

		if(!on && !ZeT.isx(this.scrollTimer))
		{
			clearInterval(this.scrollTimer)
			delete this.scrollTimer
		}
	},

	/**
	 * Tells whether there are items on the left to scroll.
	 */
	isScrollLeft      : function()
	{
		return (this.content._data.offset() > 0)
	},

	isScrollRight     : function()
	{
		var wh = this.columns() * this.rows()
		var s  = this.content._data.size()
		var o  = this.content._data.offset()

		return (s > o + wh)
	},

	/**
	 * Returns true when the number of tiles
	 * exceeds the number of data items.
	 */
	isScollNone       : function()
	{
		var wh = this.columns() * this.rows()
		var s  = this.content._data.size()
		return (s <= wh)
	},

	_ontiles          : function(e)
	{
		e.control = this
		e.index   = this.content.scroll(e.column, e.row, this)

		//!: invoke content strategy
		this.content.on(e)
	}
})


// +----: ReTrade Tiles Item :----------------------------+

/**
 * Content provider for Tiles. Creates wrapping
 * single-cell div element the content there.
 *
 * Required parameters are:
 *
 * · data
 *
 *   ReTrade.TilesData or { options }.
 *   defines strategy of obtaining the content
 *   and server-size updates.
 *
 * Optional parameters are:
 *
 * · wrapClass, wrapStyle
 *
 *   ZeTD.classes() and ZeTD.styles() arguments
 *   for each table-wrapper created.
 *
 * · table
 *
 *   orders to create wrapped table instead of div.
 */
ReTrade.TilesItem = ZeT.defineClass('ReTrade.TilesItem',
{
	ReTradeTilesItem  : true,

	init              : function(opts)
	{
		ZeT.assert(ZeT.iso(opts))
		this.opts = opts

		//?: {has shadow}
		if(!opts.border && opts.shadow)
			opts.border = new ZeT.Border.Shadow(
			  ZeT.Border.shadow(opts.shadow))

		//~: data models access strategy
		this._data = ZeT.assertn(opts.data)
		ZeT.assert(ZeT.iso(this._data))
		if(this._data.ReTradeTilesData !== true)
			this._data = new ReTrade.TilesData(opts.data)
		ZeT.assert(this._data.ReTradeTilesData === true)
	},

	/**
	 * Provides content of the denoted tile.
	 * Index is integer value starting from 0.
	 */
	provide           : function(index, tile)
	{
		//~: model of this tile
		var m = this._data.model(index)
		tile.toggle(!!m); if(!m) return

		//~: content node
		var w = this.wrapper(tile, true)
		var c = this.node(w)

		//~: index of the tile
		w.data('ReTrade.TilesItem.index', index)

		//~: set the content text
		if(ZeT.iss(m.text))
			c.text(m.text)
	},

	scroll            : function()
	{
		return this._data.scroll.apply(this._data, arguments)
	},

	/**
	 * Returns node nested in the wrapping elements
	 * that is a leaf element to insert tile content.
	 */
	node              : function(tile, create)
	{
		//~: first assume the tile is wrapper
		var wr = $(tile)
		if(wr.data('ReTrade.TilesItem') !== this)
			wr = this.wrapper(tile, create)

		//?: {found it not}
		if(!wr) if(!create) return undefined; else
			throw ZeT.ass('Could not find tile wrapper!')

		var cnt = wr.data('ReTrade.TilesItem.content')
		ZeT.assert(ZeTD.isn(cnt))
		return $(cnt)
	},

	index             : function(tile)
	{
		//~: assume a wrapper is given
		var a = 'ReTrade.TilesItem.index'
		var i = $(tile).data(a)
		if(ZeT.isi(i)) return i

		//~: search for the wrapper
		var w = this.wrapper(tile)
		if(ZeT.isx(w)) return

		i = w.data(a)
		ZeT.assert(ZeT.isi(i))

		return i
	},

	/**
	 * Returns map-object of the content model-related data.
	 */
	data              : function(tile)
	{
		//~: assume tile is the wrapping node
		var xa = 'ReTrade.TilesItem.index'
		var id = $(tile).data(xa)

		//~: search for the wrapping root in the tile
		if(ZeT.isx(id))
		{
			tile = this.wrapper(tile)
			if(!tile) return
			id = $(tile).data(xa)
			ZeT.assert(!ZeT.isx(id))
		}

		return this._data.data(id)
	},

	/**
	 * Returns wrapping node of the tile.
	 */
	wrapper           : function(tile, create)
	{
		var wr, self = this

		$(tile).children().each(function()
		{
			if($(this).data('ReTrade.TilesItem') === self)
				{ wr = $(this); return false }
		})

		if(!wr && create)
			wr = this._wrap(tile)
		return wr
	},

	/**
	 * Invoked by Tiles Control on a tile event.
	 * The event object contains all the fields of
	 * ReTrade.Tiles.on(), plus:
	 *
	 * · index     tile scroll index;
	 * · control   Tiles Control strategy.
	 */
	on                : function(e)
	{
		var T = ReTrade.isTouch
		var w = this.wrapper(e.cell)
		var d = this.data(w)

		//?: {is click on touch}
		if(T && (e.type == 'click'))
			this._select(!d.selected, e)

		//?: {mouse entered}
		if(!T && (e.type == 'enter'))
			this._select(true, e)

		//?: {mouse leaved}
		if(!T && (e.type == 'leave'))
			this._select(false, e)
	},

	_select           : function(selected, event_wrapper)
	{
		//~: access wrapper & data
		var w = (!event_wrapper.cell)?(event_wrapper):
			this.wrapper(event_wrapper.cell)
		var d = this.data(w)

		//?: {can't select}
		if(!d || !this._can_select(selected, d, w)) return
		d.selected = selected

		//?: {has selection class}
		if(ZeT.iss(this.opts.selectedClass))
			if(selected)
				w.addClass(this.opts.selectedClass)
			else
				w.removeClass(this.opts.selectedClass)

		//~: react on selection
		this._on_select(!!selected, d, w)
	},

	_on_select        : function(/* selected, data, wrapper */)
	{},

	_can_select       : function(/* selected, data, wrapper */)
	{
		return true
	},

	_wrap             : function(tile)
	{
		var wr = $('<div/>')

		ZeTD.classes(wr[0], this.opts.wrapClass)
		ZeTD.styles(wr[0], this.opts.wrapStyle)

		wr.data('ReTrade.TilesItem', this)
		tile.append(wr)

		//~: create the content
		var cnt = this._wrap_content(wr)
		wr.data('ReTrade.TilesItem.content', $(cnt)[0])

		return wr
	},

	_wrap_content     : function(wr)
	{
		var cnt, xcnt

		//?: {content in div}
		if(!this.opts.table)
			xcnt = cnt = $('<div/>')
		//~: create content table
		else
		{
			xcnt = $('<table/>', { cellpadding: 0, cellspacing: 0 })
			xcnt.html('<tbody><tr><td/></tr></tbody>')
			cnt = xcnt.find('td')
		}

		//?: {border wrapping}
		if(this.opts.border)
		{
			ZeT.assert(this.opts.border.ZeTBorder === true)
			xcnt = $(this.opts.border.proc(xcnt[0]))
		}

		//~: append content root
		wr.append(xcnt)
		return cnt[0]
	}
})


// +----: ReTrade Tiles Data :----------------------------+

/**
 * Strategy of providing content for the tiles and
 * issuing server-size requests: cut-paste, remove.
 *
 * Give 'array' array as option as the basic variant
 * of mapping data models by scroll index.
 */
ReTrade.TilesData = ZeT.defineClass('ReTrade.TilesData',
{
	ReTradeTilesData  : true,

	init              : function(opts)
	{
		ZeT.assert(ZeT.iso(opts))
		this.opts   = opts
		this._array = opts.array
	},

	/**
	 * Returns data object (model) for the given
	 * index of the tiles scroll. Index starts with
	 * zero and is incremented from tile to tile.
	 */
	model             : function(index)
	{
		var a; if(ZeT.isa(a = this._array))
			return a[index]

		throw ZeT.ass('Unsupported!')
	},

	updated           : function(model)
	{
		if(ZeT.isf(this.opts.onupdate))
			this.opts.onupdate(model, this)
		return this
	},

	scroll            : function(x, y, ctl)
	{
		var w = ctl.columns(), h = ctl.rows()
		var o = this._offset, wh = w * h

		var a; if(ZeT.isa(a = this._array))
		{
			if(!o) return w * y + x

			//?: {got over the end}
			var z = a.length - wh
			if((z >= 0) && (o > z))
				this._offset = o = z

			//?: {got all visible}
			if(a.length <= wh)
				this._offset = o = 0

			return w * y + x + o
		}

		throw ZeT.ass('Unsupported!')
	},

	size              : function()
	{
		var a; if(ZeT.isa(a = this._array))
			return a.length

		throw ZeT.ass('Unsupported!')
	},

	data              : function(index_or_model)
	{
		//~: access the model
		var m = index_or_model
		if(ZeT.isi(m)) m = this.model(m)
		if(ZeT.isx(m)) return

		//~: assign the model id
		var id; if(ZeT.isx(id = m.id))
		{
			if(!this._mid) this._mid = 0
			m.id = this._mid++
		}

		//~: access the data
		if(!this._data) this._data = {}
		var d = this._data[id]

		return (d)?(d):(this._data[id] =
		  { id: m.id, model: m })
	},

	each              : function(f)
	{
		ZeT.assert(ZeT.isf(f))

		if(ZeT.isa(this._array))
			return ZeT.each(this._array, f)

		throw ZeT.ass('Unsupported!')
	},

	remove            : function(m)
	{
		var a; if(ZeT.isa(a = this._array))
		{
			ZeTA.remove(a, m)

			//?: {has offset out of the length}
			if(!ZeT.isx(this._offset))
				if(this._offset > a.length)
					this._offset = a.length
		}

		if(this._data)
			delete this._data[m]

		if(ZeT.isf(this.opts.onremove))
			this.opts.onremove(m, this)
	},

	move              : function(where, items)
	{
		var a; if(ZeT.isa(a = this._array))
		{
			var i = a.indexOf(where)
			ZeT.assert(i >= 0)

			ZeTA.remove.apply(a, items)

			var x = [i, 0]
			x.push.apply(x, items)
			a.splice.apply(a, x)
		}

		if(ZeT.isf(this.opts.onmove))
			this.opts.onmove(where, items, this)
	},

	goto              : function(/* model, tilesItem */)
	{
		if(ZeT.isf(this.opts.goto))
			this.opts.goto.apply(this, arguments)
	},

	offset            : function(o)
	{
		if(ZeT.isx(o))
			return ZeT.isx(this._offset)?(0):(this._offset)

		var a; if(ZeT.isa(a = this._array))
		{
			if(o < 0) o = 0
			if(o > a.length) o = a.length
			this._offset = o
		}
	}
})


// +----: ReTrade Tiles Item Extended :-------------------+

/**
 * Content provider for tiles that supports
 * editing and cut-paste (move) operations.
 */
ReTrade.TilesItemExt = ZeT.defineClass('ReTrade.TilesItemExt', ReTrade.TilesItem,
{
	provide           : function(i, tile)
	{
		this.$applySuper(arguments)

		//~: update the view
		var w = this.wrapper(tile)
		var d = w && this.data(w)
		w && d && this._review(w, d)
	},

	_wrap             : function()
	{
		var self = this, wr = this.$applySuper(arguments)

		function add(cls, f, title, p)
		{
			var node; (p || wr).append(node = $('<div/>').
			  hide().attr('title', title).addClass(cls).
			  click(ZeT.fbind(f, self, wr[0])))
			return node
		}

		add('retrade-tiles-delete', this._delete, 'Удалить')
		add('retrade-tiles-edit',   this._edit,   'Редактировать')
		add('retrade-tiles-goto',   this._goto,   'Активировать ссылку')
		add('retrade-tiles-move',   this._move,   'Вырезать')
		add('retrade-tiles-insert', this._insert, 'Вставить')

		var co; wr.append(co = $('<div/>').hide().
		  addClass('retrade-tiles-colors'))

		function sco(color)
		{
			return ZeT.fbindu(self._set_color, 1, color, 2, true)
		}

		add('N', sco('N'), 'Серый',     co).show()
		add('R', sco('R'), 'Красный',   co).show()
		add('G', sco('G'), 'Зелёный',   co).show()
		add('O', sco('O'), 'Оранжевый', co).show()

		return wr
	},

	_can_select       : function(selected, d)
	{
		return selected || (!d.edited && !d.moved)
	},

	_on_select        : function(selected, d, wr)
	{
		this._review(wr, d, false)
	},

	_review           : function(wr, d, doselect)
	{
		var c = this.node(wr = $(wr))
		var e = wr.find('.retrade-tiles-edit')
		var m = wr.find('.retrade-tiles-move')

		//<: show-hide the controls

		wr.find('.retrade-tiles-edit').toggle(d.selected)
		wr.find('.retrade-tiles-colors').toggle(!!d.edited)

		if(!d.moved && this._get_moved().length)
			wr.find('.retrade-tiles-insert').toggle(d.selected)

		wr.find('.retrade-tiles-goto').toggle(
			!!(d.selected && !d.edited && !d.moved))

		wr.find('.retrade-tiles-insert').toggle(
			!!(d.selected && !d.moved && this._get_moved().length))

		wr.find('.retrade-tiles-delete').toggle(!!d.edited)
		wr.find('.retrade-tiles-move').toggle(!!(d.edited || d.moved))

		//>: show-hide the controls

		if(d.moved) //<-- move related controls
		{
			m.addClass('selected')
			c.css('opacity', 0.25)
		}
		else
		{
			m.removeClass('selected')
			!d.edited && c.css('opacity', 1.0)
		}

		if(d.edited) //<-- edit related controls
		{
			e.addClass('selected')
			c.css('opacity', 0.25)
		}
		else
		{
			e.removeClass('selected')
			!d.moved && c.css('opacity', 1.0)
		}

		//~: paint the border
		if(!d.model.color) d.model.color = 'N'
		ZeT.assert(ZeT.iss(d.model.color))
		ZeT.assert(['N', 'R', 'G', 'O'].indexOf(d.model.color) >= 0)
		this._set_color(wr, d.model.color)
		this._color_controls(wr, d)

		//?: {refresh the selection}
		if(doselect !== false)
			this._select(!!d.selected, wr)
	},

	_edit             : function(wr)
	{
		var d = this.data(wr = $(wr))
		d.edited = !d.edited

		this._review(wr, d)
	},

	_delete           : function(wr)
	{
		//~: tile scroll index
		var i = this.index(wr)
		ZeT.assert(ZeT.isi(i))

		//~: tile content model
		var d = this.data(wr)
		ZeT.assertn(d)

		//~: un-select the model
		d.edited = d.moved = false
		this._select(false, $(wr))

		//!: remove the model
		this._data.remove(d.model, i)

		//~: update the tiles
		this.control.update()
	},

	_move             : function(wr)
	{
		var d = this.data(wr = $(wr))
		d.moved = !d.moved
		if(d.moved) d.edited = false
		this._review(wr, d)
	},

	_get_moved        : function(data)
	{
		var all = [], self = this

		this._data.each(function(m)
		{
			var d = self._data.data(m)
			if(d && d.moved)
				all.push((data)?(d):(m))
		})

		return all
	},

	_insert           : function(wr)
	{
		//~: insert target model
		var m = this.data(wr)
		ZeT.assertn(m && (m = m.model))

		//~: collect all the models moved
		var moved = this._get_moved()
		if(!moved.length) return

		function reset(x)
		{
			x.moved = false
			x.selected = x.edited
		}

		reset(this.data(wr))
		ZeT.each(this._get_moved(true), reset)
		this._data.move(m, moved)

		//~: select current item
		var i = this.index(wr)
		m = this._data.data(i)
		if(m) m.selected = true

		//!: update all the tiles
		this.control.update()
	},

	_goto             : function(wr)
	{
		var i = this.index(wr)
		ZeT.assert(ZeT.isi(i))

		var m = this._data.model(i)
		ZeT.assertn(m)

		this._data.goto(m, this)
	},

	_color_controls   : function(wr, d)
	{
		var co = d.model.color

		function node(c)
		{
			return wr.find('.retrade-tiles-colors > .' + c)[0]
		}

		function cls(c)
		{
			return ((co == c)?('-'):('+')) + 'enabled'
		}

		ZeTD.classes(node('R'), cls('R'))
		ZeTD.classes(node('G'), cls('G'))
		ZeTD.classes(node('O'), cls('O'))
		ZeTD.classes(node('N'), cls('N'))
	},

	_set_color        : function(wr, co, exit_edit)
	{
		var i = this.index(wr)
		ZeT.assert(ZeT.isi(i))

		//~: assign the color to the model
		var m = this._data.model(i)
		var x = (ZeT.assertn(m).color == co)

		//~: update the model
		m.color = co
		if(exit_edit === true)
			this._data.updated(m, this)

		//~: paint the border
		this._color_border(wr, co)

		//~: exit edit mode
		if(!x && (exit_edit === true))
		{
			var d = this.data(wr)
			d.edited = false
			this._review(wr, d, false)
		}
	},

	_color_border     : function(wr, co)
	{
		//~: color replace arrays
		var XR = [ '-N-', '-R-', '-G-', '-O-' ]
		co = '-' + co + '-'

		//~: replace the border color
		$(wr).find('*').each(function()
		{
			var cls = $(this).attr('class')
			if(ZeTS.ises(cls)) return
			for(var i = 0;(i < 4);i++)
				cls = ZeTS.replace(cls, XR[i], co)
			$(this).attr('class', cls)
		})
	}
})