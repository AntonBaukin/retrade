/*===============================================================+
 |                                                     extjsf    |
 |   Ext JS for JavaServer Faces                                 |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


var extjsf = ZeT.define('extjsf', {})


// +----: Domain :-----------------------------------------------+

/**
 * Domain of ExtJSF Binds. Each domain has unique name
 * that is empty string for the default global one.
 */
extjsf.Domain = ZeT.defineClass('extjsf.Domain',
{
	className        : 'extjsf.Domain',
	extjsfDomain     : true,

	init             : function(name)
	{
		//=: domain name
		ZeT.assert(ZeT.iss(name))
		this.name = name

		//ZeT.log('+@[', name, ']')

		//=: domain binds
		this.binds = new ZeT.Map()

		//=: destroy listeners
		this.ondestr = new ZeT.Map()
	},

	/**
	 * Returns the Bind registered by the name that
	 * is unique within a Domain. Optional argument
	 * allows to register a Bind instance. Error is
	 * raised when a Bind is already registered.
	 */
	bind             : function(name /*, bind */)
	{
		ZeT.asserts(name, 'Can not define a Bind by ',
		  'not a string name: ', name)

		if(arguments.length == 1)
			return this.binds.get(name)
		ZeT.assert(arguments.length == 2)

		//~: bind argument
		var bind = arguments[1]
		ZeT.assert(bind.extjsfBind === true)

		//?: {bind with this name is already defined}
		ZeT.assert(!this.binds.contains(name), 'Bind [', name,
		  '] is already defined in Domain [', this.name, ']!')

		//~: register the bind
		this.binds.put(name, bind)
		bind.defined(this.name, name)

		//ZeT.log('@[', this.name, '] + Bind: ', name)

		return bind
	},

	/**
	 * Returns the Bind previously registered and
	 * clears it's registration. If second argument
	 * is not false, destroys the Bind. Note that
	 * destruction of a Bind does not mean it's
	 * Ext JS component is being destroyed!
	 */
	unbind           : function(bind, destroy)
	{
		//?: {bind instance}
		if(extjsf.isbind(bind))
			bind = bind.name

		//?: {not a string name}
		ZeT.asserts(bind, 'Can not lookup a Bind by ',
		  'not a string name: ', bind)

		//~: remove the bind
		var bind = this.binds.remove(bind)
		if(!bind) return

		//?: {do not destroy}
		if(destroy === false)
			return bind

		//!: do destroy
		bind.destroy()

		return bind
	},

	/**
	 * Destroys all currently registered Binds of
	 * the Domain in the order reversed to the
	 * registration one.
	 *
	 * Invokes all the Domain deletion callbacks
	 * currently registered in the order of the
	 * registration as: callback(domain).
	 *
	 * Additional options may be given: they are
	 * passed to each callback and destroy call.
	 */
	destroy          : function(opts)
	{
		var self = this, ondestr = [], binds = []

		//~: collect the callbacks
		this.ondestr.each(function(f){ ondestr.push(f) })

		//~: and invoke them
		this.ondestr.clear()
		ZeT.each(ondestr, function(f)
		{
			try
			{
				f(self, opts)
			}
			catch(e)
			{
				ZeT.log('Error in callback while deleting Domain [',
				  self.name, ']:\n', e)
			}
		})

		//~: collect the binds
		this.binds.reverse(function(b){ binds.push(b) })

		//ZeT.log('-@[', this.name, ']')

		//ZeT.log('-@[', this.name,
		//  ']: ', ZeT.map(binds, 'name'))

		//~: and destroy them
		ZeT.each(binds, function(bind)
		{
			var name = bind.name; try
			{
				bind.destroy(opts)
			}
			catch(e)
			{
				ZeT.log('Error destroying Bind [', name, ']:\n', e)
			}
		})

		//~: sweep all the binds
		this.binds.clear()
	},

	/**
	 * Invokes the callback for each Bind
	 * in this Domain. See ZeT.Map.each().
	 */
	each             : function(f)
	{
		ZeT.assertf(f)
		return this.binds.each(f)
	},

	/**
	 * Registers a callback to invoke when Domain
	 * is being destroyed. Give second argument false
	 * to remove previously registered callback.
	 */
	onDestroy        : function(f, remove)
	{
		ZeT.assertf(f)

		if(remove === false)
			this.ondestr.remove(f)
		else
			this.ondestr.put(f)

		return this
	}
})


// +----: ExtJSF Utilities :-------------------------------------+

ZeT.extend(extjsf,
{
	/**
	 * Creates ExtJSF Domain on the first demand and
	 * registers it globally by the string name.
	 *
	 * Note that ''-empty name means the root Domain
	 * of the entire web page that is never destroyed.
	 */
	domain           : function(name, notcreate)
	{
		ZeT.assert(ZeT.iss(name),
		  'Can not define Domain by not a string name!')

		//~: global registry of the domains
		extjsf.domains = ZeT.define('extjsf.Domain.registry', {})

		//~: lookup there
		var domain = extjsf.domains[name]
		if(domain || notcreate) return domain

		//~: create it
		extjsf.domains[name] = domain = new extjsf.Domain(name)

		//!: un-register on the destruction
		domain.onDestroy(function()
		{
			delete extjsf.domains[name]
		})

		return domain
	},

	/**
	 * Creates unique name to create a Domain
	 * based on the definitive string given.
	 *
	 * If domain name starts with 'global:',
	 * of has ':global:' infix, resulting name
	 * has no counter (:xyz) appended. This is
	 * applyed not to open several windows for
	 * the same object (database key).
	 */
	nameDomain       : function(name)
	{
		ZeT.assert(ZeT.iss(name))

		//?: {domain ends with number}
		if(name.match(/:\d+$/))
			return name

		if(ZeTS.starts(name, ':'))
			name = name.substring(1)

		ZeT.asserts(name)

		name = 'domain:' + name

		//?: {is not global name} increment suffix
		if(name.indexOf(':global:') == -1)
		{
			if(!extjsf._temp_domain_id)
				extjsf._temp_domain_id = 0

			if(!ZeTS.ends(name, ':'))
				name = name + ':'
			name += extjsf._temp_domain_id++
		}
		else if(ZeTS.ends(name, ':'))
			name = name.substring(0, name.length - 1)

		return name
	},

	isdomain         : function(d)
	{
		return !!d && (d.extjsfDomain === true)
	},

	/**
	 * Tells whether the argument given
	 * is an Ext JS Component.
	 */
	isco             : function(co)
	{
		return !!co && (co.isComponent === true)
	},

	isbind           : function(bind)
	{
		return !!bind && (bind.extjsfBind === true)
	},

	/**
	 * Tries to guess Bind instance from the arguments.
	 * Supports name + domain pair, direct Bind instance,
	 * and a bound Component as the arguments.
	 */
	bind             : function(/* name, domain | bind | co */)
	{
		var a0 = arguments[0], a1

		if(ZeT.iss(a0))
		{
			a1 = arguments[1]
			if(ZeT.iss(a1))
			{
				a1 = extjsf.domain(a1)
				if(!a1) return
			}

			ZeT.assert(extjsf.isdomain(a1))
			return a1.bind(a0)
		}

		if(extjsf.isbind(a0))
			return a0

		//?: {is a Component}
		if(extjsf.isco(a0))
			if(extjsf.isbind(a0.extjsfBind))
				return a0.extjsfBind

		//?: {is options with name: and domain: )}
		if(ZeT.isox(a0) && !ZeT.ises(a0.name))
		{
			if(ZeT.iss(a0.domain))
			{
				a1 = extjsf.domain(a0.domain)
				if(!a1) return
			}

			ZeT.assert(extjsf.isdomain(a1))
			return a1.bind(a0.name)
		}
	},

	/**
	 * Tries to get the Ext JS component from
	 * the arguments given. See bind().
	 */
	co               : function()
	{
		var bind, a0 = arguments[0]

		//?: {has nothing}
		if(ZeT.isx(a0))
			return undefined

		//?: {is a Component}
		if(extjsf.isco(a0))
			return a0

		//?: {refers a Component}
		if(ZeT.isox(a0) && extjsf.isco(a0.component))
			return a0.component

		//~: access the bind
		a0 = extjsf.bind.apply(extjsf, arguments)

		//?: {is a bind}
		if(extjsf.isbind(a0))
			return a0.co()
	},

	/**
	 * If last argument is a function, assignes
	 * it as a handler to the Bind found via
	 * the leading arguments, see extjsf.bind().
	 * If component exists, assigns it too.
	 *
	 * If no function is given, returns currently
	 * assigned handler.
	 */
	handler          : function()
	{
		//~: take the handler
		ZeT.assert(arguments.length)
		var h = arguments[arguments.length - 1]
		if(!ZeT.isf(h)) h = null

		//~: search for the bind and component
		var co, b = extjsf.bind.apply(extjsf, arguments)
		if(b) co = b.co(); else
		{
			//~: search for the component
			co = extjsf.co.apply(extjsf, arguments)

			//?: {no bind, no component}
			if(!co) if(!ZeT.isf(h)) return; else throw ZeT.ass(
			  'Not found component to assign the handler!')
		}

		if(ZeT.isf(h)) //?: {assign handler}
		{
			if(co)
			{
				ZeT.assertf(co.setHandler)
				co.setHandler(h)
			}

			if(b) b.handler = h

			return b || co
		}
		else
		{
			if(co && ZeT.isf(h = co.handler)) return h
			if( b && ZeT.isf(h =  b.handler)) return h
		}
	},

	/**
	 * Wraps Ext.create() with ZeT.delay().
	 */
	delayCreate      : function(extClass, opts)
	{
		Ext.require(extClass)

		return ZeT.delay(function()
		{
			return Ext.create(extClass, opts)
		})
	},

	/**
	 * Takes float or integer number of 'ex' units
	 * and returns the float or integer number of
	 * pixels they take. Optional second argument
	 * tells the number of pixels to add.
	 */
	ex               : function(v, vpx)
	{
		return extjsf.$css_v('ex', v, vpx)
	},

	/**
	 * Takes float or integer number of 'pt' units
	 * (points), then works as ex().
	 */
	pt               : function(v, vpx)
	{
		return extjsf.$css_v('pt', v, vpx)
	},

	/**
	 * Returns string that encodes 2, 3, or 4
	 * numbers into margin-padding code that
	 * Ext JS supports. (It's the same as
	 * in CSS, but in pizels without 'px'.)
	 *
	 * All four numbers stand for: top, right,
	 * bottom, left. Three numbers (x, y, z):
	 * x-top, y-right, z-bottom, y-left. And
	 * two numbers (x, y): x-top, y-right,
	 * x-bottom, y-left.
	 *
	 * Samples. pts(1, 2, 3, 4) is '1 2 3 4'.
	 * pts(1, 2, 3) is '1 2 3 2'. pts(1, 2)
	 * is '1 2 1 2'.
	 */
	pts              : function()
	{
		return extjsf.$css_vs('pt', arguments)
	},

	$css_v           : function(unit, v, vpx)
	{
		if((v == 0) && !vpx) return 0

		ZeT.assert(ZeT.isn(v))
		ZeT.assert(ZeT.isx(vpx) || ZeT.isn(vpx))

		var r = v * extjsf.$css_size(unit) * 0.001
		if(vpx) r += vpx

		//?: {need integer result}
		if(ZeT.isi(v) && (ZeT.isx(vpx) || ZeT.isi(vpx)))
			r = Math.round(r)

		return r
	},

	$css_vs          : ZeT.scope(function(/* unit, a */)
	{
		function sv(s, v)
		{
			if(v == 0) return 0
			ZeT.assert(ZeT.isn(v))
			var r = v * s * 0.001
			return ZeT.isi(v)?Math.round(r):(r)
		}

		return function(unit, a)
		{
			var s = extjsf.$css_size(unit)

			switch(a.length)
			{
				case 4: return ZeTS.catsep(' ',
				  sv(s, a[0]), sv(s, a[1]),
				  sv(s, a[2]), sv(s, a[3])
				)

				case 3:
				{
					var sv1 = sv(s, a[1])

					return ZeTS.catsep(' ',
					  sv(s, a[0]), sv1,
					  sv(s, a[2]), sv1
					)
				}

				case 2:
				{
					var sv0 = sv(s, a[0])
					var sv1 = sv(s, a[1])

					return ZeTS.catsep(' ', sv0, sv1, sv0, sv1)
				}
			}
		}
	}),

	/**
	 * Returns float value with size in pixels
	 * of 1000 given units named as in CSS.
	 */
	$css_size        : function(unit)
	{
		//~: cache map
		var m = extjsf._css_size
		if(!m) extjsf._css_size = m = {}

		//~: lookup in the cache
		var v; if(v = m[unit]) return v

		//~: create invisible node
		var d = document.createElement('div')
		ZeT.extend(d.style, { visibility: 'hidden',
		  width: '1000' + unit, height: '1px'
		})

		ZeT.assertn(document.body,
		  'Document is not yet ready to find CSS size!')

		//~: append, get the size, remove
		document.body.appendChild(d)
		m[unit] = v = d.offsetWidth
		document.body.removeChild(d)

		//~: inspect the result
		ZeT.assert(ZeT.isn(v) && (v > 0),
		  'Can\'t find pixel size of CSS unit [', unit, ']!')

		return v
	}
})


// +----: Bind [ build ] :---------------------------------------+

/**
 * ExtJSF Bind is a controller that connects Ext JS
 * Component with supporting scripts related to JSF
 * specific issues. This is the most general class.
 */
extjsf.Bind = ZeT.defineClass('extjsf.Bind',
{
	className        : 'extjsf.Bind',
	extjsfBind       : true,

	init             : function()
	{
		//~: properties
		this._props = { 'props': {}}

		//=: nested binds (child components)
		this._items = []

		//~: listeners by the names
		this._listeners = {}

		//WARNING: this prevents recursion in Ext.clone()!
		this.constructor = null
	},

	/**
	 * Invoked by a Domain when registering Bind.
	 */
	defined          : function(domain, name)
	{
		ZeT.assert(ZeT.iss(domain))
		ZeT.asserts(name)

		this.domain = domain
		this.name   = name
	},

	/**
	 * Tells JSF (facelet) component id and assigns
	 * the id and Bind name related attributes.
	 */
	ids              : function(clientId)
	{
		//~: JSF component id
		this._client_id = ZeT.asserts(clientId)

		//~: Ext JS component id
		this.props({ id: this.name })

		return this
	},

	viewId           : function(viewId)
	{
		this._view_id = ZeT.asserts(viewId)
		return this
	},

	/**
	 * This call (not required when render-to) tells
	 * the name of the Bind (co-ids):
	 *
	 * 0) of the parent JSF component this one
	 *    is directly placed in;
	 *
	 * 1) the component you want to place this one
	 *    instead of the default defined in [0].
	 *
	 * Second argument allows to implement extension
	 * points: you place component in node of else
	 * sub-tree of JSF components tree.
	 *
	 * Third optional argument (defaults to 'added')
	 * tells the target event name when to add.
	 * Works only in [1] case.
	 *
	 * When function is invoked wothout arguments,
	 * returns the effective parent Bind.
	 */
	parent           : function(parent_coid, target_coid, when)
	{
		if(!arguments.length)
		{
			if(this._target_coid)
				return extjsf.bind(this._target_coid, this.domain)
			else if(this._parent_coid)
				return extjsf.bind(this._parent_coid, this.domain)
			return undefined
		}

		//~: default parent
		delete this._parent_coid
		if(!ZeT.ises(parent_coid))
			this._parent_coid = parent_coid

		//~: targeted parent
		delete this._target_coid
		if(!ZeT.ises(target_coid))
			this._target_coid = target_coid

		//~: add-when event name
		delete this._add_when
		if(this._target_coid && !ZeT.ises(when))
			this._add_when = when

		return this
	},

	/**
	 * Tells DOM node ID to render this component to.
	 * Optional parent arguments tells the name of
	 * the Bind (of the same domain) to attach this
	 * component to. This allows to destroy this
	 * component with the target one.
	 *
	 * Note that when rendering components to the
	 * HTML content nested in Ext JS menus, parent
	 * argument is essential, or menu would be closed
	 * before click event over the component reaches it!
	 */
	renderTo         : function(nodeid, parent)
	{
		//~: render node id
		delete this._render_to
		if(!ZeT.ises(nodeid = ZeTS.trim(nodeid)))
			this._render_to = nodeid

		//?: {has render parent}
		delete this._render_parent
		if(this._render_to && !ZeT.ises(parent))
			this._render_parent = parent

		return this
	},

	/**
	 * Couples this Bind (and the future component)
	 * to the proper container or render place.
	 *
	 * Note that this call may be done before
	 * nested components (if any) are inserted
	 * as JSF facet and their Binds created!
	 */
	install          : function()
	{
		//?: {default add item}
		if(this.$is_install())
			this.$install()

		return this
	},

	/**
	 * Invoked always after the JSF facets with the
	 * nested components are rendered, and always
	 * after the install. Does all required the
	 * component to work.
	 *
	 * Note that at this point Ext JS components
	 * are still might be not available!
	 */
	ready            : function(always)
	{
		//?: {installed or added} do nothing
		if(this._installed_to || this._added_to)
			return this

		//?: {add to targeted parent}
		if(this._target_coid)
			this.$add_to()

		//?: {rendering to a node}
		else if(this._render_to)
			this.$render_to()

		//?: {create always}
		else if(always)
			this.co(true)

		return this
	},

	/**
	 * Invoked (mostly by a Domain) to destroy
	 * this Bind and the component it refers to.
	 */
	destroy          : function(opts)
	{
		//?: {already destroyed}
		if(this._destroyed === true)
			return false

		//?: {skip this component}
		if(opts && ZeT.isa(opts.except))
			if(opts.except.indexOf(this) != -1)
				return

		//?: {internal destroyed}
		if(this.$destroy() === false)
		{
			delete this._destroyed
			return false
		}
	},

	/**
	 * Returns destroy() call bound to this Bind.
	 * The same function is returned on every call.
	 */
	boundDestroy     : function()
	{
		if(!this._bound_destroy)
			this._bound_destroy =
			  ZeT.fbind(this.destroy, this)

		return this._bound_destroy
	},

	/**
	 * Allows to define (and register in the same
	 * domain) a Bind based on this one. The name
	 * of the new Bind is: this bind name + '-' +
	 * nested bind name. Note this when inserting
	 * default facet with the properties!
	 *
	 * Optional second argument is a Bind instance,
	 * or a factory function, or ZeT Class.
	 *
	 * Callback function is invoked with arguments
	 * (this-context equals to this Bind, [1]):
	 *
	 *  0) new (nested) Bind instance;
	 *  1) this (nesting) Bind instance.
	 *
	 * If callback returns false, nested bind is
	 * unregistered from the domain.
	 */
	nest             : function(/* name, [ Bind, ] callback */)
	{
		//?: {this bind is not registered}
		ZeT.asserts(this.name)
		ZeT.assert(ZeT.iss(this.domain))

		var bind, callback = arguments[1]
		var name = ZeT.asserts(arguments[0],
		  'Nested Bind must have not an empty name!')

		if(arguments.length != 2)
		{
			ZeT.assert(arguments.length == 3)
			bind = callback; callback = arguments[2]
		}

		//?: {has no callback}
		ZeT.assertf(callback)

		//~: build the Bind instance
		if(ZeT.isx(bind))
			bind = new extjsf.Bind()
		else if(ZeT.isclass(bind))
			bind = bind.create()
		else if(ZeT.isf(bind))
			bind = bind()

		//?: {not a Bind}
		ZeT.assert(extjsf.isbind(bind))

		//~: initialize
		bind = this.$nest(name, bind)

		//!: invoke the callback
		if(bind && (false === callback.call(this, bind, this)))
			extjsf.domain(this.domain).unbind(bind)

		return this
	},

	/**
	 * Assigns the properties of the component.
	 */
	props            : function(/* [ suffix, ] props */)
	{
		var s = 'props', p = arguments[0]

		//?: {suffix is specified}
		if(arguments.length != 1)
		{
			ZeT.assert(arguments.length == 2)
			s = p; p = arguments[1]
		}

		ZeT.asserts(s)
		ZeT.assert(ZeT.iso(p))

		//~: accumulate the properties
		this._props[s] =
			ZeT.extend(this._props[s], p)

		return this
	},

	/**
	 * Same as props(), but takes only the properties
	 * that are not already defined for the component.
	 */
	merge            : function(/* [ suffix, ] props */)
	{
		var s = 'props', p = arguments[0]

		//?: {suffix is specified}
		if(arguments.length != 1)
		{
			ZeT.assert(arguments.length == 2)
			s = p; p = arguments[1]
		}

		ZeT.asserts(s)
		ZeT.assert(ZeT.iso(p))

		//~: accumulate the properties
		this._props[s] =
			ZeT.deepExtend(this._props[s], p)

		return this
	},

	/**
	 * Reads properties written from the configuration
	 * facet to the internal properties node. Optional
	 * suffix (default is 'props') allows to select DOM
	 * node (containing the properties text) from ones
	 * generated by ExtJSF components with facets.
	 *
	 * If optional compare argument is set to true,
	 * checks and returns whether any data were
	 * loaded from the node. (Optional suffix
	 * may be omitted in this call.)
	 */
	readPropsNode    : function(suffix, compare)
	{
		if(ZeT.isb(suffix))
			{ compare = suffix; suffix = null }

		//?: {default suffix}
		if(ZeT.ises(suffix)) suffix = 'props'

		//~: evaluate the properties node
		var props = this.$eval_props_node(suffix)

		//~: properties of the node
		if(!this._node_props) this._node_props = {}
		if(ZeT.keys(props).length)
			this._node_props[suffix] = props
		else
			delete this._node_props[suffix]

		//!: update the properties now
		this.props(suffix, props)

		return (compare)?(!!this._node_props[suffix]):(this)
	},

	/**
	 * Returns properties previously read from the node
	 * (where they were printed by JSF facet).
	 */
	getPropsNode     : function(suffix)
	{
		//?: {default suffix}
		if(ZeT.ises(suffix)) suffix = 'props'
		return ZeT.get(this._node_props, suffix)
	},

	/**
	 * Answers whether this Bind has child Binds,
	 * or has own properties written in facets,
	 * or has 'html' property. All three boolean
	 * flags are optional.
	 */
	isEmpty          : function(props, items, html)
	{
		if((props !== false) && this.getPropsNode())
			return false

		if((items !== false) && this._items.length)
			return false

		if(html === false)
			return true

		html = this.$raw().html

		//?: {html is delayed} resolve now
		if(ZeT.isDelayed(html))
			this.$raw().html = html = html()

		return ZeT.ises(html)
	},

	/**
	 * Private. Direct access to the properties.
	 */
	$raw             : function(suffix)
	{
		if(ZeT.ises(suffix)) suffix = 'props'
		return this._props[suffix]
	},

	/**
	 * Private. Creates the component.
	 */
	$create          : function()
	{
		return Ext.ComponentManager.create(this.buildProps())
	},

	/**
	 * Private. Actually destroys the Bind and
	 * the component attached. Returns false
	 * if the destroy was not required.
	 */
	$destroy         : function()
	{
		//?: {already destroyed}
		if(this._destroyed === true) return false
		this._destroyed = true

		//ZeT.log('@[', this.domain, '] - Bind: ', this.name)

		var co; if(co = this._component) try
		{
			if(this.$is_destroy(co))
			{
				co.destroy()
				return true
			}
		}
		finally
		{
			delete this._component
		}

		this.$safe_unbind()
	},

	$is_destroy      : function(co)
	{
		return ZeT.isf(co.destroy) &&
		  !co.isDestroyed && !co.destroying
	},

	$safe_unbind     : function()
	{
		if(!this.name) return
		ZeT.assert(ZeT.iss(this.domain))

		var d = extjsf.domain(this.domain, true)
		var b = d && d.bind(this.name)
		if(b == this) d.unbind(this, false)
	},

	$is_install      : function()
	{
		//?: {is not rendering to some else place}
		return !!this._parent_coid &&
		  !this._render_to && !this._target_coid
	},

	/**
	 * Private. Internals of install(). Add the properties
	 * of this Bind to be in the list of child items of
	 * the properties of the parent Bind. Works only when
	 * the parent component is not yet instantiated.
	 *
	 * This is the most common method of building a tree
	 * of components. It conflicts with render-to.
	 */
	$install         : function()
	{
		//?: {allowed to install}
		ZeT.assert(this._parent_coid && !this._added_to
		  && !this._installed_to && !this._rendered_to)

		//~: the domain
		var d = ZeT.assertn(extjsf.domain(this.domain),
		  'This Bind is not registered in any Domain!')

		//~: default parent bind
		var p = ZeT.assertn(d.bind(this._parent_coid),
		  'Parent Bind [', this._parent_coid, '] is not',
		  ' found in the Domain [', this.domain, ']!')

		p.addItem(this) //!: add this bind as a child
		this._installed_to = ZeT.ises(p.coid)?(p):(p.coid)
	},

	$bind_to         : function()
	{
		var co = ZeT.assertn(this._component)
		var cb = co.extjsfBind, bn = cb && cb.name

		ZeT.assert(!cb || (cb == this),
		  'Component already has a Bind assigned: [',
		  bn, '] instead of: [ ', this.name, ']!')

		co.extjsfBind = this

		//~: also, set the events
		this.$set_events()
	},

	$add_to          : function()
	{
		//?: {allowed to add}
		ZeT.assert(this._target_coid && !this._added_to
		  && !this._installed_to && !this._rendered_to)

		//~: take the parent bind
		var p = ZeT.assertn(
		  extjsf.bind(this._target_coid, this.domain),
		  'Not found target Bind [', this._target_coid,
		  '] in domain [', this.domain, ']!')

		this._added_to = this._target_coid
		this.$do_add(p) //!: do add
	},

	$do_add           : function(p)
	{
		var self = this

		function doadd(x)
		{
			if(!x && (x.isComponent !== true)) x = p.co()
			ZeT.assert(x && (x.isComponent === true))
			x.add(self.co(true))
		}

		//?: {use defined event name}
		if(this._add_when)
			p.on(this._add_when, doadd)
		else if(p.co()) //?: {parent already exists}
			p.co().add(this.co(true))
		//~: delay the addition
		else p.on('added', doadd)
	},

	/**
	 * Private. Invoked when the component is rendered
	 * into a DOM node, but not installed into the parent
	 * container as a regular child. Allows to control
	 * the destruction of the component and helps with
	 * tricky handling of click events.
	 */
	$render_to       : function()
	{
		//?: {allowed to install}
		ZeT.assert(this._render_to && !this._added_to
		  && !this._installed_to && !this._rendered_to)

		this._rendered_to = true

		//?: {no bound parent}
		if(!this._render_parent)
			return this.$do_render(this._parent_coid &&
			  extjsf.bind(this._parent_coid, this.domain))

		var p = ZeT.assertn(
		  extjsf.bind(this._render_parent, this.domain),
		  'Bind of the render parent [', this._render_parent,
		  '] is not found at domain [', this.domain, ']!'
		)

		//~: bind the destroy
		p.on('beforedestroy', this.boundDestroy())

		//~: access the parent bind
		this.$do_render(p, this.$render_parent)
	},

	$do_render       : function(p, callback)
	{
		if(!extjsf.isbind(p)) return

		function doit(pco)
		{
			//~: create the component
			this.co(true)

			//~: invoke the callback
			if(callback) callback.call(this, pco)
		}

		//?: {parent is rendered, node exists}
		if(p.co() && (p.co().rendered || Ext.getDom(this._render_to)))
			return doit.call(this, p.co())

		p.on('render', ZeT.fbind(doit, this))
	},

	$render_parent   : function(p)
	{
		var co = ZeT.assertn(this.co())

		//--> up()
		//  used when component is rendered in menu,
		//  or client event will close the menu before
		//  clicking the component!
		var up = co.up; co.up = function(selector)
		{
			//~: invoke original up()
			var r = up.apply(co, arguments)
			if(r) return r

			//?: {is that component}
			if(p === selector)
				return p
			else if(ZeT.iss(selector) && p.is(selector))
				return p

			//~: invoke the render-parent
			return p.up.apply(p, arguments)
		}
	},

	/**
	 * Private. Returns ID of DOM node that
	 * stores the properties of this component.
	 */
	$node_id         : function(suffix)
	{
		var nid

		if(!ZeT.ises(this._node_id))
			nid = this._node_id

		//?: {form from the client id}
		if(!ZeT.ises(this._client_id))
			nid = ZeTS.cat(this._client_id, '-',
			  ZeT.asserts(this.name))

		if(ZeT.iss(suffix) && nid)
			nid = ZeTS.cat(nid, '-', suffix)

		return nid
	},

	/**
	 * Private. Searches for the properties node with
	 * the suffix given and evaluates the properties.
	 */
	$eval_props_node : function(suffix)
	{
		//~: text content of the node
		var x = ZeTX.text(this.$props_node(suffix))

		//~: evaluate the result
		return ZeTS.ises(x)?{}:this.$eval_props(x)
	},

	/**
	 * Private. Searches for the properties node with
	 * the suffix given. Returns Ext.Element.
	 */
	$props_node      : function(suffix)
	{
		return Ext.getDom(ZeTS.cat(this.$node_id(), '-', ZeT.asserts(suffix)))
	},

	/**
	 * Private. Evaluates the properties written
	 * via a facet as JS-object (except the curly
	 * brackets) into real object.
	 */
	$eval_props      : function(props)
	{
		if(ZeT.ises(props)) return
		props = ZeTS.trim(props)

		if(!ZeTS.starts(props, '('))
		{
			if(!ZeTS.starts(props, '{', '['))
				props = ZeTS.cat('{', props, '}')

			props = ZeTS.cat('(', props, ')')
		}

		try
		{
			return eval(props)
		}
		catch(e)
		{
			throw ZeT.ass('Illegal properties of Bind [',
			  this.name, ']: \n', props, '\n', e)
		}
	},

	/**
	 * Private. Initializes the nested Bind.
	 */
	$nest            : function(name, bind)
	{
		//~: register the nested bind
		extjsf.domain(this.domain).
		  bind(ZeTS.cat(this.name, '-', name), bind)

		//~: same client id, component id
		bind.ids(this._client_id)

		//~: parent bind
		bind.parent(this._parent_coid, this._target_coid)

		return bind
	},

	/**
	 * Private. Adds request parameters to call
	 * this component on the server side.
	 */
	$add_params      : function(params)
	{
		//~: default ExtJSF domain
		if(!ZeT.iss(params.domain) && !ZeT.ises(this.domain))
			params.domain = this.domain

		//~: default view id
		if(!ZeT.iss(params.view) && !ZeT.ises(this._view_id))
			params.view = this._view_id

		return params
	}
})


// +----: Bind [ basics ] :--------------------------------------+

extjsf.Bind.extend(
{
	/**
	 * Has three forms of a call:
	 *
	 * · returns the Ext JS component bound;
	 *
	 * · assigns the component if current
	 *   is not defined;
	 *
	 * · when true, installs the component
	 *   on the first demand.
	 *
	 * Always returns a component, or undefined.
	 */
	co               : function()
	{
		if(!arguments.length)
			return this._component
		ZeT.assert(arguments.length == 1)

		var co = arguments[0]

		if(!ZeT.isb(co)) //?: {a component-like}
		{
			if(!this._component)
			{
				this._component = ZeT.assertn(co)
				this.$bind_to()
			}
			else
				ZeT.assert(this._component === co, 'Bind [',
				  this.name, '] got else component assigned!')
		}
		else if(!this._component && (co === true))
		{
			this._component = this.$create()
			this.$bind_to()
		}

		return this._component
	},

	/**
	 * Invokes ZeT.scope() giving the first
	 * argument this Bind instance.
	 */
	scope            : function()
	{
		ZeT.assertf(arguments[arguments.length - 1])

		//~: argument this bind
		var a = ZeT.a(arguments)
		a.splice(0, 0, this)

		ZeT.scope.apply(ZeT, a)
		return this
	},

	/**
	 * Analogue of scope(), but is invoked only
	 * when the component bound is created (added
	 * to the parent container).
	 */
	when             : function()
	{
		if(this.co())
			this.scope.apply(this, arguments)
		else
			this.on('added', ZeT.fbinda(
			  this.scope, this, arguments, true))

		return this
	},

	/**
	 * Tells of sets the component visibility.
	 * Also works when component is not yet created.
	 */
	visible          : function(v)
	{
		var co = this.co()
		var  p = this.$raw()

		if(ZeT.isu(v)) //?: {check visibility}
			return (co)?(co.isVisible()):!ZeT.isu(p.hidden)?
			  (!p.hidden):(ZeT.isu(p.visible) || !!p.visible)

		if(co) co.setVisible(!!v)
		else p.hidden = !v

		return this
	}
})


// +----: Bind [ events ] :--------------------------------------+

extjsf.Bind.extend(
{
	/**
	 * Adds the callback function as an event listener
	 * for the component related to this Bind (it may
	 * not exist at the call time).
	 *
	 * Note that event if the component is already created,
	 * callback is not added to it directly, but is saved
	 * in the Bind-internal list of listeners. Component
	 * has only one listener from the Bind for each event.
	 *
	 * Set remove argument true to delete previously
	 * added listener.
	 *
	 * Second variant of call to this method is to
	 * give object mapping event names to single
	 * functions or array of functions.
	 */
	on               : function(ename, f, remove)
	{
		//?: {single call}
		if(arguments.length > 1)
			this.$on_one.apply(this, arguments)
		else //~: process the object
		{
			var self = this, o = arguments[0]

			ZeT.assert(ZeT.isox(o))
			ZeT.each(o, function(ls, ename)
			{
				if(ZeT.isf(ls))
					return self.$on_one(ename, ls)

				ZeT.assert(ZeT.isa(ls))
				for(var i = 0;(i < ls.length);i++)
					self.$on_one(ename, ls[i])
			})
		}

		return this
	},

	$on_one          : function(ename, f, remove)
	{
		ZeT.asserts(ename)
		ZeT.assertf(f)

		//?: {it's on-bind} !: +side-effect
		if(f == this.$on_bind(ename))
			return

		var l = this.$on_list(ename)
		var i = l.indexOf(f)

		//?: {removing}
		if(remove === true)
		{
			if(i != -1) //?: {found it}
				l.splice(i, 1)
		}
		else
		{
			if(i == -1) //?: {found it not}
				l.push(f)
		}
	},

	/**
	 * Private. Returns internal list of listeners
	 * mapped to the name of event.
	 */
	$on_list         : function(ename)
	{
		var list = this._listeners[ename]

		//?: {event is registered}
		if(list) return list

		//~: register it
		this._listeners[ename] = list = []

		if(this.co()) //?: {component is ready}
			this.co().on(ename, this.$on_bind(ename))

		return list
	},

	/**
	 * Private. Creates and caches bound $on().
	 */
	$on_bind         : function(ename)
	{
		if(!this._bound_on) this._bound_on = {}

		//?: {already created it}
		if(this._bound_on[ename])
			return this._bound_on[ename]

		return this._bound_on[ename] =
		  ZeT.fbind(this.$on, this, ename)
	},

	/**
	 * Private. Registers essential events for
	 * the assigned component.
	 */
	$set_events      : function()
	{
		var co = ZeT.assertn(this._component)
		var de = this.$on_bind('destroy')

		//HINT: it's better for Bind's destroy
		//  listener to be the last in the list!

		co.removeListener('destroy', de)
		co.addListener('destroy', de)
	},

	/**
	 * Private. Returns a map of listeners to give
	 * as Ext JS configuration property when
	 * creating a Component.
	 */
	$listeners       : function()
	{
		//!: always create and destroy
		this.$on_bind('added')
		this.$on_bind('destroy')

		return this._bound_on
	},

	/**
	 * Private. Bound wrapper over the nested listeners.
	 * Invokes them and returns the first defined result.
	 */
	$on              : function()
	{
		var args  = ZeT.a(arguments)
		var ename = args.shift()

		//?: {added} set the component
		if(ename == 'added')
			this.co(args[0])

		var result = this.$on_invoke(ename, args)

		//?: {after the destruction}
		if(ename == 'destroy')
			this.destroy()

		return result
	},

	$on_invoke       : function(ename, args)
	{
		var ls = this._listeners[ename]
		if(!ls) return undefined

		for(var re, i = 0, l = ls.length;(i < l);i++) try
		{
			var r = ls[i].apply(this._component, args)
			if(!ZeT.isu(r) && ZeT.isu(re)) re = r
		}
		catch(e)
		{
			ZeT.log('Error in Bind [', this.name, '] on event [',
			  ename, '] in listener: ', ls[i])

			throw e
		}

		return re
	}
})


// +----: Bind [ children ] :------------------------------------+

extjsf.Bind.extend(
{
	addItem          : function(bind)
	{
		if(ZeT.iss(bind)) bind = extjsf.bind(bind);
		if(bind) this._items.push(bind)
		return this;
	},

	buildProps       : function()
	{
		var res = this.$raw()

		//=: render to
		if(this._render_to)
			res.renderTo = this._render_to

		//~: create child items
		if(!res.items)
		{
			var items = this.$items_build()

			if(ZeT.isa(items) && items.length)
				res.items = items
		}

		//~: resolve delayed properties
		ZeT.undelay(res)

		//~: merge the listeners
		if(ZeT.isox(res.listeners))
			this.on(res.listeners)
		res.listeners = this.$listeners()

		//~: the handler
		if(ZeT.isf(this.handler))
			res.handler = this.handler

		return res
	},

	$items_replace   : function(items)
	{
		var result = this._items

		ZeT.assert(ZeT.isx(items) || ZeT.isa(items))
		this._items = ZeT.isa(items)?(items):[]

		return result
	},

	/**
	 * Private. Builds properties objects for each
	 * child item of this Bind.
	 */
	$items_build     : function()
	{
		var result = []

		ZeT.each(this._items, function(item)
		{
			//?: {has component factory (also, a Bind)}
			if(ZeT.isf(item.co) && item.co())
				return result.push(item.co())

			result.push(item.buildProps())
		})

		return result
	}
})


// +----: Root Bind :--------------------------------------------+

/**
 * Root Binds relate to the top-level components that has no
 * parent Binds, or are owners of a Domain. They are:
 * the viewport, root panels, and floating windows.
 */
extjsf.RootBind = ZeT.defineClass(
  'extjsf.RootBind', extjsf.Bind,
{
	/**
	 * Tells whether to install the callback on the owning
	 * component destroy to also destroy the entire Domain.
	 * By default, any root Bind is an owner.
	 */
	domainOwner      : function(isowner)
	{
		if(!arguments.length)
			return (this._domain_owner !== false)

		ZeT.assert(ZeT.isb(isowner))
		ZeT.assert(ZeT.iss(this.domain))
		this._domain_owner = isowner
		return this
	},

	$destroy         : function()
	{
		var d; try
		{
			d = this.$applySuper(arguments)
		}
		finally
		{
			if(d !== false)
				this.$destroy_domain()
		}
	},

	$destroy_domain  : function()
	{
		//?: {is not registered}
		if(!ZeT.iss(this.name)) return

		//?: {not a domain owner}
		if(this._domain_owner === false) return
		delete this._domain_owner

		//?: {not a default domain}
		ZeT.asserts(this.domain, 'Root Bind [', this.name,
		  '] tries to destroy the default Domain!')

		//!: destroy the domain
		var domain = extjsf.domain(this.domain, true)
		if(domain) domain.destroy()
	}
})


// +----: Action Bind :------------------------------------------+

/**
 * Action Bind is for components that has hidden JSF forms.
 * It's the components that send JSF post request to the
 * server and able to run various actions or commands.
 */
extjsf.ActionBind = ZeT.defineClass(
  'extjsf.ActionBind', extjsf.Bind,
{
	className        : 'extjsf.ActionBind',

	init             : function()
	{
		this.$applySuper(arguments)

		//=: bound handler
		this.handler = ZeT.fbind(this.$handler, this)
	},

	/**
	 * Register the callback on successfull submit.
	 * Callback is invoked as:
	 *
	 *   cb.call(bind, submit opts, data, [action, form])
	 *
	 * Data is object returned from the server (XML
	 * document for body-post response format.)
	 */
	success          : function(cb)
	{
		if(!ZeT.isf(cb))
			return this._on_success
		this._on_success = cb
		return this
	},

	/**
	 * Register the callback on failed submit,
	 * or the validation failure.
	 */
	failure          : function(cb)
	{
		if(!ZeT.isf(cb))
			return this._on_failure
		this._on_failure = cb
		return this
	},

	postMode         : function(mode)
	{
		this._post_mode = ZeT.asserts(mode)
		return this
	},

	/**
	 * Takes HTML form related to this Bind and updates
	 * the action attribute to make it a go-link.
	 *
	 * Go links look like '/context/go/.. component ..'
	 * where the path doesn't end with '.xhtml' suffix.
	 *
	 * Using this call assumes that Go-dispatching
	 * filter is configured on the server side!
	 */
	goAction         : function()
	{
		//~: action attribute
		var a = this.$form_action(), p = '', i

		//?: {action already has '/go/'}
		if(a.indexOf('/go/') != -1)
			return this

		//~: action prefix
		if(ZeTS.starts(a, '/'))
			{ p = '/'; a = a.substring(1) }

		if((i = a.indexOf('/')) != 0)
		{
			p += a.substring(0, i + 1)
			a  = a.substring(i + 1)
		}

		if(!ZeTS.ends(p, '/')) p += '/'

		//~: strip the suffix
		if(ZeTS.ends(a, '.xhtml'))
			a = a.substring(0, a.length - 6);

		//!: update the action attribute
		this.$form_node().set({ action: p + 'go/' + a })

		return this
	},

	/**
	 * Call strategy of the action. Posts the form.
	 */
	$handler         : function(opts)
	{
		//~: copy the options
		opts = ZeT.extend({}, opts)

		//~: request address
		opts.url = this.$form_action()

		//~: parameters of the request
		opts.params = this.$post_params(opts.params, true, true)

		//~: success callback
		var self = this, ons = opts.success, onf = opts.failure
		opts.success = function(response)
		{
			opts.success = ons

			//?: {response validation succeeded}
			if(self.$handle_response(response))
				self.$call_success(opts, response)
			else
				self.$call_failure(opts, response)
		}

		//~: failure callback
		opts.failure = function(response)
		{
			opts.failure = onf
			self.$call_failure(opts, response)
		}

		//!: issue the request
		Ext.Ajax.request(opts)
	},

	/**
	 * Private. Returns Ext.Element of the form node
	 * found by id based on the $node_id().
	 */
	$form_node       : function(optional)
	{
		//~: take the basic node
		var n = this.$node_id()
		if(ZeT.ises(n))
			if(optional === true) return; else
				throw ZeT.ass('Bind form id is not provided!')

		//~: take the node
		var e = Ext.get(n)
		if(!e) if(optional === true) return; else
			throw ZeT.ass('Bind form DOM node [', n , '] is not found!')

		//?: {not a form}
		var tag = ZeT.asserts(ZeT.get(e, 'dom', 'tagName'))
		ZeT.assert(tag.toLowerCase() == 'form',
		  'DOM node by id [', n, '] is not a form!')

		return e
	},

	$form_action     : function()
	{
		var f = this.$form_node()
		return ZeT.asserts(f.getAttribute('action'),
		  'ExtJSF form [', f.id, '] has no action attribute!')
	},

	/**
	 * Private. Collects parameters from the form. Optional
	 * argument allows to specify form action button to include
	 * into the result. (To invoke the action.)
	 */
	$form_params     : function(button, except)
	{
		var res = {}

		//~: select the form inputs
		this.$form_node().select('input').each(function(item)
		{
			var n = item.getAttribute('name'); if(!n) return
			var v = item.getAttribute('value') || ''

			//?: {this field is a submit button} skip it
			if(item.getAttribute('type') == 'submit')
				if((button !== true) && (button !== n))
					return

			if(!except || !except[n])
				res[n] = v
		})

		return res
	},

	/**
	 * Private. Collects parameters for POST request.
	 */
	$post_params     : function(added, form, button)
	{
		//~: request parameters from the facet
		var params = ZeT.extend({}, this.$raw('params'))

		//~: collect parameters from the form
		if(form !== false)
			ZeT.extend(params, this.$form_params(button))

		//~: parameters from the options
		ZeT.extend(params, added)

		//~: resolve delayed parameters
		ZeT.undelay(params)

		//~: additional parameters
		return this.$add_params(params)
	},

	$add_params      : function(params)
	{
		params = this.$applySuper(arguments)

		//~: default view mode
		if(!ZeT.iss(params.mode) && !ZeT.ises(this._post_mode))
			params.mode = this._post_mode

		return params
	},

	$response_xml    : function(x)
	{
		if(!x) return undefined

		if(ZeTX.isnode(x))
			return x

		if(ZeTX.isnode(x.responseXML))
			return x.responseXML
	},

	/**
	 * Handles the results of POST calls resulting the
	 * 'http://extjs.jsf.java.net/response' XMLs.
	 *
	 * Returns true when the validation is correct.
	 */
	$handle_response : function(ajaxres)
	{
		//~: get the response document
		var xml = ZeT.assertn(this.$response_xml(ajaxres),
		  'Form [', this.name, '] got POST response not a XML!')

		//~: response root node
		var root = ZeT.assertn(ZeTX.node(xml, 'response'),
		  'Form [', this.name, '] got POST response XML ',
		  'having no <response> root tag!')

		//HINT: global scripts are not executed in the case
		//  of the validation failure, or absence of this block!

		//~: validation root
		var vnode = ZeT.assertn(ZeTX.node(root, 'validation'),
		  'Form [', this.name, '] got POST response XML ',
		  'having no <validation>  tag!')

		//~: process validated fields
		var script, self = this
		var fields = ZeTX.nodes(vnode, 'field')
		ZeT.each(fields, function(fnode)
		{
			var target = ZeTX.attr(fnode, 'target')
			var field  = !ZeT.ises(target) &&
			  extjsf.co(target, self.domain)
			var error  = ZeTS.trim(ZeTX.text(
			  ZeTX.node(fnode, 'error')))

			//?: {field component is found}
			if(field && field.isFormField && error.length)
				field.markInvalid(error)

			//~: evaluate the field script
			self.$call_scripts(fnode)
		})

		//~: status of the validation
		var success = ZeTX.attr(vnode, 'success')

		//?: {validation success}
		if('true' == ZeTS.trim(success))
		{
			//~: evaluate global scripts
			this.$call_scripts(root)
			return true
		}

		//~: failed, evaluate validation section script
		this.$call_scripts(vnode)
		return false
	},

	$call_success    : function(opts)
	{
		if(ZeT.isf(this._on_success))
			this._on_success.apply(this, arguments)

		if(ZeT.isf(opts.success))
			opts.success.apply(this, arguments)
	},

	$call_failure    : function(opts)
	{
		if(ZeT.isf(this._on_failure))
			this._on_failure.apply(this, arguments)

		if(ZeT.isf(opts.failure))
			opts.failure.apply(this, arguments)
	},

	/**
	 * Private. Takes all script tags in the XML
	 * document and evaluates them.
	 */
	$call_scripts    : function(xml)
	{
		//~: find each <script> node
		var self = this, scripts = ZeTX.nodes(xml, 'script')

		//~: and invoke them
		ZeT.each(scripts, function(script)
		{
			script = ZeTX.text(script)

			if(!ZeT.ises(script)) try
			{
				ZeT.xeval(script)
			}
			catch(e)
			{
				ZeT.log('Error evaluating submit response of [',
				  self.name, '] script is: \n', script, '\n',  e)

				throw e
			}
		})
	}
})


// +----: Form Bind :-------------------------------------------+

extjsf.FormBind = ZeT.defineClass(
  'extjsf.FormBind', extjsf.ActionBind,
{
	className        : 'extjsf.FormBind',

	/**
	 * Assigns additional validator of the form
	 * that is invoked as v.call(bind, form, opts).
	 * Returns this bind.
	 *
	 * Note that this validator does supress
	 * the built-in of the form component.
	 */
	validator        : function(v)
	{
		delete this._validator
		if(ZeT.isf(v)) this._validator = v
		return this
	},

	/**
	 * Binds form's submit() with the options given.
	 * If no options given, returns the previously
	 * bound function (if any), or creates a new one.
	 */
	submitBound      : function(opts)
	{
		if(arguments.length)
		{
			ZeT.assert(arguments.length == 1)
			ZeT.assert(ZeT.iso(arguments[0]))

			this._bound_submit = ZeT.fbind(
			  this.submit, this, arguments[0])
		}
		else if(!this._bound_submit)
			this._bound_submit = ZeT.fbind(this.submit, this)

		return this._bound_submit
	},

	/**
	 * Submits the form by sending POST request
	 * to the back bean (JSF view) configured.
	 * Does validation before sending.
	 *
	 * The options may contain these fields:
	 *
	 * · success   alternative callback on form
	 *   submit request. See callback();
	 *
	 * · failure   alternative callback on form
	 *   submit failure. See failure();
	 *
	 * · params    additional parameters of the
	 *   form post request.
	 *
	 * Returns false when form is invalid, and
	 * no post request was issued.
	 *
	 * Action is Ext.form.action.Action.
	 */
	submit           : function(opts)
	{
		opts = ZeT.extend({}, opts)

		//~: get the Ext form component
		var fco = this.$form_co()

		//~: {the form is not valid}
		if(!this.$validate(opts))
			return false

		//~: submit parameters
		var ps  = this.$post_params(opts)

		//~: action url
		var url = this.$form_action()

		//!: do submit Ext JS form
		fco.submit({ url: url, params: ps,
		  success: ZeT.fbind(this.$eval_result, this, opts),
		  failure: ZeT.fbind(this.$call_failure, this, opts),
		  clientValidation: false //<-- our validation instead
		})

		return this
	},

	$form_co         : function()
	{
		return ZeT.assert(this.co() &&
		  ZeT.isf(this.co().getForm) && this.co().getForm(),
		  'Can not process undefined form [', this.name,
		  '] in domain [', this.domain, ']'
		)
	},

	$post_params     : function(opts)
	{
		//~: form action command (JSF input id and name)
		var button = ZeT.get(opts, 'command')
		if(!ZeT.ises(button)) button = this.$node_id(button)

		//~: additional parameters from the options
		var params = ZeT.get(opts, 'params')

		//~: collect all parameters of the action
		return this.$callSuper(params, true, button)
	},

	$form_params     : function(button, except)
	{
		//~: add ext fields to exception
		except = ZeT.extend(this.$ext_fields(), except)

		return this.$callSuper(button, except)
	},

	/**
	 * Private. Maps fields present in Ext JS form
	 * component by their request (post) names.
	 */
	$ext_fields      : function()
	{
		var res = {}, fco = this.$form_co()

		fco.getFields().each(function(f) {
			res[f.getName()] = f
		})

		return res
	},

	$validate        : function(opts)
	{
		var fco = this.$form_co()

		//?: {default is not valid}
		if(!this._validator && !fco.isValid())
			return false

		//?: {external validator is not}
		else if(this._validator)
			if(false === this._validator.call(this, fco, opts))
				return false

		return true
	},

	$eval_result     : function(opts, form, action)
	{
		var self = this, success = true, xml

		//?: {action has no result}
		ZeT.assertn(action.result, 'Submit of form [', this.name,
		  '] returned no action result! Check:', action)

		//~: display errors of the fields
		ZeT.each(action.result.errors, function(er)
		{
			if(ZeTS.ises(er.target)) return

			//~: get the bind in this domain
			var bn = extjsf.bind(er.target, self.domain)
			var co = bn && bn.co()

			//?: {not found, try else}
			if(!co) co = Ext.getCmp(er.target)

			//?: {component is field}
			if(co && (co.isFormField === true))
				co.markInvalid(er.error)

			success = false
		})

		//~: success status in the response attribute
		if(xml = ZeT.get(form, 'errorReader', 'rawData'))
			//?: {got <response> of the document}
			if(ZeTX.node(xml, 'response'))
				success = this.$handle_response(xml)

		if(success)
			this.$call_success(opts, xml, action, form)
		else
			this.$call_failure(opts, xml, action, form)
	}
})

/**
 * Form validation response model.
 */
Ext.define('extjsf.model.FormValidation',
{
	extend: 'Ext.data.Model',

	idProperty: 'target',

	fields: [

		{name: 'target', type: 'string', mapping: '@target'},
		{name: 'error',  type: 'string'}
	]
})


// +----: Field Bind :------------------------------------------+

extjsf.FieldBind = ZeT.defineClass(
  'extjsf.FieldBind', extjsf.Bind,
{
	className        : 'extjsf.FieldBind',

	ids              : function()
	{
		this.$applySuper(arguments)

		//~: JSF field related properties
		this.props({ inputId: this.$node_id(),
		  name: this.$field_name() })

		return this
	},

	buildProps       : function()
	{
		var res = this.$applySuper(arguments)

		//~: field value
		if(ZeT.isu(res.value))
		{
			var f = Ext.get(this.$field_name())
			if(f) res.value = f.getValue()
		}

		return res
	},

	/**
	 * Creates nested Bind of the label component
	 * ('label' suffix) to add it before or after
	 * the field component during the install.
	 */
	buildLabel       : function(always)
	{
		return this.nest('label',
		  ZeT.fbindu(this.$build_label, this, 1, always))
	},

	install          : function()
	{
		if(!this.label) //?: {has no label}
			return this.$applySuper(arguments)

		//?: {label is before}
		var a = this.label.$raw().labelAlign
		var x = (a === 'right') || (a === 'after')
		if(!x) this.label.install()

		//!: install this field
		this.$applySuper(arguments)

		//~: install label after
		if(x) this.label.install()

		return this
	},

	/**
	 * Returns the value set for the field.
	 * Also works when component is not yet created.
	 */
	value            : function(v)
	{
		var co = this.co()
		var  p = this.$raw()

		if(!arguments.length) //?: {get field value}
			return (co)?(co.getValue()):(p.value)

		if(co) co.setValue(v)
		else p.value = v

		return this
	},

	$build_label     : function(label, always)
	{
		//~: take properties from the field bind
		label.props(this.$raw('label') || {})

		//~: label defaults
		label.props({ xtype: 'label',
		  forId: this.$node_id() })

		//?: {empty label}
		if(!label.readPropsNode(true) && !always)
			return false

		//~: save label to install
		this.label = label

		//~: hide the inlined label
		this.props({ hideLabel: true })

		//~: add the event listeners
		label.on('afterrender',
		  ZeT.fbind(this.$label_events, this))
	},

	/**
	 * Private. Installs events on the label node.
	 * (Invoked after the label is rendered.)
	 */
	$label_events    : function()
	{
		var self = this

		//~: click on the label
		this.label.co().getEl().addListener('click',
		  ZeT.fbind(this.$label_click, this))

		//~: label cursor
		var cursor; function setCursor()
		{
			var el = self.label.co().getEl()

			if(self.co().readOnly)
			{
				if(!cursor) cursor = el.getStyle('cursor')
				el.setStyle('cursor', 'default')
			}
			else if(cursor)
				el.setStyle('cursor', cursor)
		}

		//~: set cursor now
		setCursor()

		//~: mouse entered (set cursor)
		this.label.co().getEl().
		  addListener('mouseenter', setCursor)
	},

	$label_click     : function(e)
	{
		var el = this.label.co().getEl()

		if(this.co().readOnly)
			e.stopEvent()
		else
			this.$field_focus()
	},

	$field_focus     : function()
	{
		//?: {field has picker} expand it
		if(ZeT.isf(this.co().getPicker))
			if(this.co().getPicker())
				return this.co().expand()

		this.co().focus()
	},

	$field_name      : function()
	{
		return this.$node_id('field')
	}
})


// +----: Drop Field Bind :-------------------------------------+

extjsf.DropFieldBind = ZeT.defineClass(
  'extjsf.DropFieldBind', extjsf.FieldBind,
{
	className        : 'extjsf.DropFieldBind',

	$install         : function()
	{
		//~: refresh on double-expand
		this.$expand_refresh()

		//~: label inline store
		if(ZeT.isx(this.$raw().store))
			this.$labels_store()

		//~: set display value
		this.$display_value()

		this.$applySuper(arguments)
	},

	$labels_store    : function()
	{
		var labels = this.$eval_props_node('labels')
		if(!ZeT.isa(labels)) return

		//~: convert 2-array to pairs
		for(var ls = [], i = 0;(i+1 < labels.length);i += 2)
			ls.push([ labels[i], labels[i+1] ])

		//!: set the inline store
		this.props({ store: ls, queryMode: 'local' })
	},

	/**
	 * Private. Directly assigns initial display
	 * value to the input node of the drop list.
	 */
	$display_value   : function()
	{
		var dv = this.$raw().displayValue
		delete this.$raw().displayValue

		if(!ZeT.isu(dv)) this.on('afterrender', function(f)
		{
			f.inputEl.set({ value: dv })
		})
	},

	$refresh_delay   : 2000,

	$expand_refresh  : function()
	{
		var nrone = this.$raw().notRefreshOnExpand
		delete this.$raw().notRefreshOnExpand
		if(nrone) return

		var et; this.on('expand', function()
		{
			var ts = new Date().getTime()
			if(et && (ts - et < this.$refresh_delay))
				bind.co().getStore().reload()
			et = ts
		})
	}
})


// +----: Checkboxes Bind :-------------------------------------+

extjsf.CheckboxesBind = ZeT.defineClass(
  'extjsf.CheckboxesBind', extjsf.Bind,
{
	className        : 'extjsf.CheckboxesBind',

	install          : function()
	{
		var self    = this, i = 0
		var checked = this.$read_checked()
		var labels  = this.$read_labels()

		//c: nest each checkbox
		labels.each(function(label, key)
		{
			self.$nest_check(i++, key, label,
			  checked.contains(key))
		})
	},

	$nest_check      : function(i, key, label, checked)
	{
		var self = this, bind = this.$check_bind.create()
		this.nest(''+i, bind, function()
		{
			self.$init_check(bind, i, key, label, checked)

			if(!self.checks) self.checks = []
			self.checks.push(bind)
		})
	},

	$check_bind      : extjsf.FieldBind,

	$init_check      : function(check, i, key, label, checked)
	{
		//~: clone the properties
		check.merge(ZeT.deepClone(this.$raw()))
		check.props('label', ZeT.deepClone(this.$raw('label')))

		//~: check box label
		check.props({ inputValue: key, checked: checked })
		delete check.$raw().value //<-- must have it not!

		//~: label text
		check.props('label', { text: label })

		//~: check handler
		check.on('change',
		  ZeT.fbind(this.$check_changed, this, check))

		//~: install the check box
		check.buildLabel(true).install()
	},

	$read_checked    : function()
	{
		var f = ZeT.assertn(Ext.get(this.$node_id()))
		var r = new ZeT.Map(), v = f.getValue()
		if(ZeT.ises(v)) return r

		ZeT.each(v.split(','), function(s)
		{
			if(!ZeT.ises(s = ZeTS.trim(s))) r.put(s)
		})

		return r
	},

	$read_labels     : function()
	{
		var  r = new ZeT.Map()
		var ls = this.$eval_props_node('labels')
		if(!ZeT.isa(ls)) return r

		//~: insert 2-array into the map
		for(var i = 0;(i+1 < ls.length);i += 2)
			r.put(ls[i], ls[i+1])

		return r
	},

	$check_changed   : function(check)
	{
		if(this._checking) return
		this._checking = check

		var x = check.$raw().inputValue
		var f = Ext.get(this.$node_id())
		var v = ZeT.ises(f.getValue())?[]:
		  f.getValue().split(',')

		//~: remove or add the value
		if(!check.value()) ZeTA.remove(v, x)
		else if(v.indexOf(x) < 0) v.push(x)

		//~: assign to the hidden field
		v = this.$set_checked(check, v)
		if(ZeT.isu(v)) v = [x]
		f.set({ value: v.join(',') })

		delete this._checking
	},

	$set_checked     : function(check, v)
	{
		//?: {unchecked all}
		if(!v.length && (check.$raw().allowNone === false))
			check.co().setValue(true)
		//?: {checked two of a radio}
		else if((v.length > 1) && check.$raw().onlyOne)
			ZeT.each(this.checks, function(c)
			{
				if((c != check) && c.value()) c.value(false)
			})
		else
			return v
	}
})


// +----: Load Action Bind :------------------------------------+

extjsf.LoadActionBind = ZeT.defineClass(
  'extjsf.LoadActionBind', extjsf.ActionBind,
{
	className        : 'extjsf.LoadActionBind',

	$handler         : function(opts)
	{
		//~: collect the parameters (omitting the form)
		var params = this.$post_params(
		  ZeT.get(opts, 'params'), false)

		//~: GET or POST
		var method = 'POST'; if(params.method === 'GET')
			{ method = 'GET'; delete params.method }

		//!: load the window
		new extjsf.LoadCo({

		  name: 'window', domain: this.domain,
		  form: this.$form_node(), button: true,
		  params: params, method: method

		}).load()
	}
})


// +----: Store Bind :------------------------------------------+

extjsf.StoreBind = ZeT.defineClass(
  'extjsf.StoreBind', extjsf.Bind,
{
	className        : 'extjsf.StoreBind',

	install          : function()
	{
		this.$install()

		//~: create and destroy at a time
		this.$create_when()
		this.$destroy_when()

		return this
	},

	$install         : function()
	{
		//?: {has no page size}
		var ps = this.$raw().pageSize
		ZeT.assert(ZeT.isu(ps) || (ZeT.isi(ps) && (ps >= 0)))
		if(ps === 0) delete this.$raw().pageSize

		if(this.proxy) //?: {has proxy configured}
			this.$install_proxy()

		//~: sort the store before each load
		this.on('beforeload', ZeT.fbind(this.$before_load, this))
	},

	$create_when     : function()
	{
		//~: create store at the closest moment
		Ext.onReady(ZeT.fbind(this.$create_all, this))
	},

	$destroy_when    : function()
	{
		var p = this._parent_coid

		if(p && this.domain) //~: lookup the parent
			p = extjsf.bind(p, this.domain)

		if(!p) return

		//~: destroy the store
		p.on('destroy', this.boundDestroy())

		//~: destroy the proxy
		if(this.proxy)
			p.on('destroy', this.proxy.boundDestroy())
	},

	$create_all      : function()
	{
		this.co(true) //~: create the component

		//~: set the proxy
		if(this.proxy)
			this.proxy.co(this.co().getProxy())
	},

	$create          : function()
	{
		return Ext.create('Ext.data.Store', this.buildProps())
	},

	$destroy         : function()
	{
		var co = this.co()
		var de = this.$applySuper(arguments)

		//?: {component is destroyed}
		if(co && (de === true))
		{
			//?: {store is still registered}
			if(co == Ext.data.StoreManager.lookup(this.name))
				Ext.data.StoreManager.unregister(this.name)

			//~: destroy the proxy
			if(this.proxy)
				this.proxy.destroy()
		}

		return de
	},

	$install_proxy   : function()
	{
		var proxy = ZeT.assertn(this.proxy)

		//~: proxy parameters
		this.$proxy_params(proxy)

		//!: add proxy properties to the store
		this.props({ proxy: proxy.$raw() })
	},

	$proxy_params    : function(proxy)
	{
		//~: proxy request parameters
		var extra  = ZeT.assertn(proxy.$raw('extra'))
		var params = proxy.$raw().extraParams
		if(!params) proxy.props({ extraParams: (params = {})})
		ZeT.assert(ZeT.iso(params))

		//~: model key parameter
		if(ZeT.ises(params.model))
			//?: {direct model key}
			if(!ZeT.ises(extra.key))
				params.model = extra.key
			//?: {model key from the view}
			else if(!ZeT.ises(extra.viewKey))
				params.model = extra.viewKey

		//~: model provider parameter
		if(ZeT.ises(params['model-provider']))
			if(!ZeT.ises(extra.provider))
				params['model-provider'] = extra.provider

		//~: model request parameter
		if(ZeT.ises(params['model-request']))
			if(!ZeT.ises(extra.request))
				params['model-request'] = extra.request

		return this.$add_params(params)
	},

	$before_load     : function()
	{
		//?: {has no proxy} forbid load
		if(!this.proxy) return false

		this.$update_params()
	},

	$update_params   : function()
	{
		var sp = {}, sos = this.co().sorters

		//~: collect sorters
		if(sos) for(var i = 0;(i < sos.getCount());i++)
		{
			//~: check sort property
			var so = sos.getAt(i)
			if(ZeT.ises(so._property)) { sp = {}; break }

			//[i]: sort property
			sp['sortProperty' + i] = so._property

			//[i]: is descending
			if('DESC' === so._direction)
				sp['sortDesc' + i] = true
		}

		//~: proxy parameters
		var params = this.proxy.co().getExtraParams()

		//~: remove previous sort parameters
		for(i = 0;(params['sortProperty' + i]);i++)
		{
			delete params['sortProperty' + i]
			delete params['sortDesc' + i]
		}

		//~: store bind name
		sp.bind = this.name

		//~: add new parameters
		ZeT.extend(params, sp)
	}
})


// +----: Clear Component :-------------------------------------+

/**
 * Strategy to clear a Component. Initialization method
 * takes leading arguments of extjsf.co() or bind().
 * The last argument is treated as options.
 */
extjsf.ClearCo = ZeT.defineClass('extjsf.ClearCo',
{
	className        : 'extjsf.ClearCo',

	init             : function()
	{
		//~: see the bind
		this.bind = extjsf.bind.apply(extjsf, arguments)

		//~: see the component
		if(!this.bind)
			this.co = extjsf.bind.co(extjsf, arguments)

		//~: options
		var opts = arguments[arguments.length - 1]
		this.opts = ZeT.extend({}, ZeT.isox(opts)?(opts):(null))
	},

	/**
	 * Runs the strategy.
	 */
	run              : function()
	{
		ZeT.assertn(this.co(), 'Clear Component strategy',
		  ' could not find the component!')

		//~: remove child items
		this.$rem_items()

		//~: remove the listeners
		this.$rem_listeners()

		//~: remove the docked items
		this.$rem_docked()
	},

	co               : function()
	{
		return this.bind?(this.bind.co()):(this.co)
	},

	$rem_items       : function()
	{
		this.co().removeAll()
	},

	$rem_listeners   : function()
	{
		if(this.opts.notListeners)
			return

		this.co().clearListeners()
	},

	$rem_docked      : function()
	{
		var d = this.co().getDockedItems('component[dock]')

		//~: removing docked item created from binds
		for(var i = 0;(i < d.length);i++)
			if(extjsf.bind(d[i]))
				this.co().removeDocked(d[i])
	}
})

// +----: Window Loader :---------------------------------------+

/**
 * Strategy to load content of a Component (a Window,
 * Panel, or else) from a call to JSF. Implementation
 * wraps Ext.ComponentLoader strategy.
 *
 * The options are:
 *
 * ·   ...   see extjsf.bind() or co();
 *
 * · domain  required name of ExtJSF domain;
 *
 * · url     address to send the request, not required
 *           when form node is specified;
 *
 * · form    string with ID of DOM form node, also used
 *           to take the request address. All fields
 *           nested in the form are sent;
 *
 * · params  additional parameters to send;
 *
 * · method  default method of HTTP request. Defaults
 *           to POST when form is set, of GET otherwise;
 *
 * · button  support for JSF command button when issuing
 *           request. If value is true, takes eah button
 *           (assumed only one). Else, must be a string
 *           with the name of the button;
 *
 * · clear   tells to clean the window before loading,
 *           by default is true;
 *
 * · to      timeout of the request in milliseconds.
 *           Defaults to 30 minutes;
 *
 * · ajax    ajax options supported by Ext.ComponentLoader.
 */
extjsf.LoadCo = ZeT.defineClass('extjsf.LoadCo',
{
	init             : function(opts)
	{
		this.opts = ZeT.extend({}, opts)
	},

	/**
	 * Component to load to. Also supports the default
	 * window of a Domain named 'window'.
	 */
	co               : function()
	{
		//~: lookup the bind
		var b = extjsf.bind(this.opts)
		if(b) return b.co()

		//~: lookup the component
		var co = extjsf.co(this.opts)
		if(co) return co

		//~: take window in the domain
		return ZeT.assertn(
		  extjsf.bind('window', this.opts.domain),
		  'Can not find window in the domain: [',
		  this.opts.domain, ']!')
	},

	bind             : function()
	{
		var b = extjsf.bind(this.co())
		return (b)?(b):extjsf.bind(this.opts)
	},

	load             : function()
	{
		var loader = this.$create()

		//~: clear before loading
		this.$clear()

		//~: prepare before the loading
		this.$prepare()

		//!: reload content
		Ext.create('Ext.ComponentLoader', loader)
	},

	$create          : function()
	{
		return {
		  target: this.co(), url: this.$url(),
		  params: this.$request_params(),
		  ajaxOptions: this.$ajax_opts(),
		  autoLoad: true, scripts: true
		}
	},

	$form            : function()
	{
		return !ZeT.ises(this.opts.form)?
		  Ext.get(this.opts.form):(this.opts.form)
	},

	$url             : function()
	{
		//?: {has option}
		if(!ZeT.ises(this.opts.url))
			return this.opts.url

		//~: require a form
		var form = ZeT.assertn(this.$form(),
		  'Window Loader has neither URL option, nor form!')

		//~: take action attribute
		return ZeT.asserts(form.getAttribute('action'),
		  'Form [', form, '] has no action attribute!')
	},

	$method          : function()
	{
		return !ZeT.ises(this.opts.method)?(this.opts.method):
	     this.$form()?('POST'):('GET')
	},

	$clear           : function()
	{
		//~: clear the component
		if(this.$is_clear_co())
			this.$clear_co()

		//~: clear the domain
		if(this.$is_clear_domain())
			this.$clear_domain()
	},

	$is_clear_co     : function()
	{
		return (this.opts.clear !== false)
	},

	$clear_co        : function()
	{
		new extjsf.ClearCo(this.co(),
		  { notListeners: true }).run()
	},

	$is_clear_domain : function()
	{
		var b = this.bind()

		return this.$is_clear_co() && !!b &&
		  !ZeT.ises(b.domain) && !ZeT.ises(b.name) &&
		  ZeT.isf(b.domainOwner) && b.domainOwner()
	},

	$clear_domain    : function()
	{
		var b = this.bind(), d = b.domain, n = b.name

		//~: destroy the domain
		extjsf.domain(d).destroy({ except: [ b ]})

		//~: put the component back
		extjsf.domain(d).bind(n, b)
	},

	$prepare         : function()
	{
		var co = this.co()

		//~: set temporary title
		if(ZeT.isf(co.setTitle) && !ZeT.ises(co.title))
			co.setTitle('Выполняется запрос...')
	},

	$request_params  : function()
	{
		//~: copy parameters from the options
		var ps = ZeT.extend({}, this.opts.params)

		//~: add form parameters
		if(this.$form())
			this.$form_params(ps)

		//~: resolve delayed parameters
		ZeT.undelay(ps)

		//~: domain parameter
		var b = this.bind()
		if(!ZeT.iss(ps.domain) && b.domain)
			ps.domain = b.domain

		return ps
	},

	$form_params     : function(params)
	{
		var button = this.opts.button

		this.$form().select('input').each(function(item)
		{
			var n = item.getAttribute('name'); if(!n) return
			var v = item.getAttribute('value') || ''

			//?: {this field is a submit button} skip it
			if(item.getAttribute('type') == 'submit')
				if((button !== true) && (button !== n))
					return

			params[n] = v
		})
	},

	$ajax_opts       : function()
	{
		var ao = ZeT.extend({}, this.opts.ajax)

		//=: HTTP method
		ao.method = this.$method()

		//=: timeout
		if(ZeT.isn(this.opts.to))
			ao.timeout = opts.to

		return ao
	}
})


// +----: Utilities :-------------------------------------------+

extjsf.u = ZeT.define('extjsf.utilities',
{
	/**
	 * Makes fields marked with extjsfReadWrite
	 * option (equal true) to be read-only or not.
	 *
	 * Takes leading arguments of extjsf.co() is a
	 * root container of the fields.
	 *
	 * The last argument is boolean telling whether
	 * to turn the fields to read-only (when true)
	 * of editable states. Note that when not a field
	 * is marked, it is set disabled instead.
	 *
	 * Returns the affected components.
	 */
	toggleReadWrite  : function(/* ... is read-only */)
	{
		var co = extjsf.co.apply(extjsf, arguments)
		var ro = !!arguments[arguments.length - 1]

		var fs = []; ZeT.each(co.query(), function(f)
		{
			if(f.extjsfReadWrite !== true) return

			//?: {read-write}
			if(ZeT.isf(f.setReadOnly))
				f.setReadOnly(ro)
			else if(ZeT.isf(f.setDisabled))
				f.setDisabled(ro)
			else
				return

			fs.push(f)
		})

		return fs
	},

	/**
	 * Components that are marked with extjsfBlock
	 * string option are displayed by this call.
	 * All components that are marked with else
	 * block code are hidden.
	 *
	 * Takes leading arguments of extjsf.co() is a
	 * root container of the components. The last
	 * argument names the block.
	 */
	toggleBlock      : function(/* ... block */)
	{
		var co = extjsf.co.apply(extjsf, arguments)
		var bl = ZeT.asserts(arguments[arguments.length - 1])

		var bs = []; ZeT.each(co.query(), function(c)
		{
			if(!ZeT.iss(c.extjsfBlock)) return
			c.setVisible(c.extjsfBlock == bl)
			bs.push(c)
		})

		return bs
	},

	/**
	 * Returns Columns of a Grid. Optional last argument
	 * tells (when true) whether to return only visible
	 * (by default is false, i.e., returns all).
	 *
	 * Leading arguments are of extjsf.co(), the grid.
	 */
	gridColumns      : function(/* ... visible only */)
	{
		var grid = extjsf.co.apply(extjsf, arguments)
		var viso = arguments[arguments.length - 1]
		if(!ZeT.isb(viso)) viso = false

		return (viso)?(grid.headerCt.getVisibleGridColumns()):
		  (grid.headerCt.getGridColumns())
	},

	/**
	 * Returns index of a grid column by it's daatIndex
	 * configuration property.
	 *
	 * Leading arguments are of extjsf.co(), the grid.
	 */
	columnIByDataInd : function(/* ... dataIndex */)
	{
		var grid = extjsf.co.apply(extjsf, arguments)
		var dati = arguments[arguments.length - 1]
		var cols = ZeT.isa(grid)?(grid):
		  extjsf.u.gridColumns(grid)

		for(var i = 0;(i < cols.length);i++)
			if(cols[i].dataIndex === dati)
				return i
	},

	/**
	 * Same as columnIByDataInd(), but returns
	 * a Column component.
	 */
	columnByDataInd  : function(/* ... dataIndex */)
	{
		var grid = extjsf.co.apply(extjsf, arguments)
		var dati = arguments[arguments.length - 1]
		var cols = ZeT.isa(grid)?(grid):
		  extjsf.u.gridColumns(grid)

		for(var i = 0;(i < cols.length);i++)
			if(cols[i].dataIndex === dati)
				return cols[i]
	},

	/**
	 * Same as columnByDataInd(), but searches by
	 * 'xtype' configuration property of the columns.
	 * Returns the first column matching.
	 */
	columnByType     : function(/* ... xtype */)
	{
		var grid = extjsf.co.apply(extjsf, arguments)
		var type = arguments[arguments.length - 1]
		var cols = ZeT.isa(grid)?(grid):
		  extjsf.u.gridColumns(grid)

		for(var i = 0;(i < cols.length);i++)
			if(cols[i].xtype === type)
				return cols[i]
	},

	/**
	 * Invalidates 'index' property of a grid row model
	 * (record) that stores the internal index of the row.
	 * Used for grids that display row index column.
	 */
	reindexGrid     : function(/* grid | store | array */)
	{
		var grid = extjsf.co.apply(extjsf, arguments)
		if(!grid) grid = arguments[0]

		if(ZeT.isa(grid))
			return ZeT.each(grid, function(i) {i.index = null})

		var store = (grid.isStore === true)?(grid):(grid.getStore())
		store.each(function(i) {i.index = null})
		if(ZeT.isf(grid.getView)) grid.getView().refresh()
	},

	/**
	 * Moves currently selected item of the grid up or
	 * down with optional callback notifier. The delay
	 * timeout is optional and 1000 ms by the default.
	 *
	 * Returns false when no updates took place.
	 */
	moveGridSel     : function(up, grid, d_f, fn)
	{
		var  b = extjsf.bind(grid), g = extjsf.co(grid)
		var st = g.getStore()

		//~: selected items
		var se = g.getSelectionModel().getSelection()
		if(!se || !se.length) return false

		//?: {can't move up}

		if(up && (st.indexOf(se[0]) == 0))
			return false

		//?: {can't move down}
		if(!up && (st.getCount() ==
			          st.indexOf(se[se.length - 1]) + 1))
			return false

		//~: deselect
		g.getSelectionModel().deselectAll()

		//~: move the selected up-down
		ZeT.each(se, function(x)
		{
			var i = st.indexOf(x)

			if( up) ZeT.assert(i     > 0)
			if(!up) ZeT.assert(i + 1 < st.getCount())

			st.removeAt(i)
			if( up) st.insert(i - 1, [x])
			if(!up) st.insert(i + 1, [x])
		})

		//~: select items back
		g.getSelectionModel().select(se)

		//~: update delay
		var dl = ZeT.isn(d_f)?(d_f):(1000)
		fn = ZeT.isf(d_f)?(d_f):ZeT.isf(fn)?(fn):(null)
		if(!fn) return //?: {has no callback}

		//~: update on the server with the delay
		if(!b.updating) b.updating = 1
		var ui = ++b.updating

		ZeT.timeout(dl, function()
		{
			//?: {has no more update requests}
			if(ui === b.updating) fn(grid)
		})
	},

	/**
	 * In the given tree (see extjsf.co()) expands
	 * only paths to the currently selected nodes.
	 */
	showTreeToSel   : function(/* tree */)
	{
		var tree = extjsf.co.apply(extjsf, arguments)
		var sel  = tree.getSelectionModel().getSelection()
		sel = sel.length && sel[0]

		tree.collapseAll(function()
		{
			if(sel) while(sel.parentNode)
			{
				sel.parentNode.expand()
				sel = sel.parentNode
			}
		})
	}
})
