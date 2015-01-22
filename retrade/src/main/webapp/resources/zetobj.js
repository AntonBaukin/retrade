/*===============================================================+
 |                                                     zetobj    |
 |   0-ZeT JavaScript Library for Browsers                       |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


// +----: ZeT Strings : -----------------------------------------+

var ZeTS = window.ZeTS = window.ZeTS ||
{
	/**
	 * Returns false for string objects that are not
	 * whitespace-trimmed empty.
	 */
	ises             : function(s)
	{
		return !ZeT.iss(s) || (s.length == 0) || !/\S/.test(s)
	},

	/**
	 * Checks the value, or a property value is a string.
	 * If the final property is not accessible, false
	 * value is returned.
	 *
	 * Empty strings return false value! (Whitespaces are
	 * not trimmed before the check.)
	 */
	i$s              : function(/* object, properties list */)
	{
		var o = arguments[0]

		if(arguments.length == 1)
			return ZeT.iss(o) && !!o.length

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.i$x(o)) return false
		}

		return ZeT.iss(o) && !!o.length
	},

	trim             : function(s)
	{
		return !(s && s.length)?(''):(s.replace(/^\s+|\s+$/g, ''))
	},

	first            : function(s)
	{
		return s.length && s.charAt(0)
	},

	replace          : function(s, a, b)
	{
		return s.split(a).join(b)
	},

	cat              : function( /* various objects */)
	{
		for(var i = 0;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i]) || (arguments[i] === null))
				arguments[i] = ''

		return String.prototype.concat.apply('', arguments)
	},

	cati             : function(index, objs)
	{
		if(!objs || !ZeT.isi(objs.length)) return ''

		for(var i = 0;(i < objs.length);i++)
			if((i < index) || ZeT.isu(objs[i]) || (objs[i] === null))
				objs[i] = ''

		return String.prototype.concat.apply('', objs)
	},

	catif            : function(x /*, various objects */)
	{
		if(!x || ZeT.iss(x) && !x.length) return ''
		arguments[0] = ''

		for(var i = 1;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i]) || (arguments[i] === null))
				arguments[i] = ''

		return String.prototype.concat.apply('', arguments)
	}
}

/**
 * For some browsers that do not have String.endsWith()...
 */
if(!String.prototype.endsWith)
  String.prototype.endsWith = function(s)
{
	if(!s || !s.length) return false

	var i = this.lastIndexOf(s)
	return (i == this.length - s.length)
}


// +----: ZeT Arrays : ------------------------------------------+

var ZeTA = window.ZeTA = window.ZeTA || {

	/**
	 * Creates a copy of array-like object given.
	 * Optional [begin; end) range allows to copy
	 * a part of the array. Negative values of
	 * the range boundaries are not allowed.
	 */
	copy             : function(a, begin, end)
	{
		//?: {has no range}
		if(ZeT.isu(begin))
			return ZeT.isa(a)?(a.slice()):ZeT.a(a)

		//~: [begin; end)
		ZeT.assert(ZeT.isn(begin))
		ZeT.assert(begin >= 0)
		if(ZeT.isu(end) || (end > a.length)) end = a.length
		ZeT.assert(ZeT.isn(end))
		ZeT.assert(begin <= end)

		//?: has more than 50% items to copy
		if((end - begin)*2 >= a.length)
			return (ZeT.isa(a)?(a):ZeT.a(a)).slice(begin, end)

		//~: manual copy
		var r = new Array(end - begin)
		for(var i = begin, j = 0;(i < end);i++, j++) r[j] = a[i]

		return r
	},

	/**
	 * Has two forms of invocation:
	 *
	 * 0    target array;
	 * 1..  items or arrays to remove.
	 *
	 * this target array;
	 * 0..  items or arrays to remove.
	 *
	 * Removes the items from the target array.
	 * If item is itself an array, recursively
	 * invokes this function.
	 *
	 * Returns the target array.
	 */
	remove           : function()
	{
		var i, j

		//?: {second form}
		if(ZeT.isa(this))
		{
			for(i = 0;(i < arguments.length);i++)
				if(ZeT.isa(arguments[i]))
					ZeTA.remove.apply(this, arguments[i])
				else if((j = this.indexOf(arguments[i])) != -1)
					this.splice(j, 1)

			return this
		}
		//~: first form
		else
		{
			for(i = 1;(i < arguments.length);i++)
				if(ZeT.isa(arguments[i]))
					ZeTA.remove.apply(arguments[0], arguments[i])
				else if((j = arguments[0].indexOf(arguments[i])) != -1)
					arguments[0].splice(j, 1)

			return arguments[0]
		}
	},

	/**
	 * Takes two array-like objects and optional
	 * [begin, end) range from the second one.
	 *
	 * If the first (target) object is an array,
	 * modifies it adding the items from the
	 * second object in the range given.
	 *
	 * If the target object is not an array,
	 * makes it's array-copy, returns it.
	 */
	merge            : function(a, b, begin, end)
	{
		a = ZeT.a(a)

		//?: {has range} make a copy
		if(!ZeT.isu(begin))
			b = ZeTA.copy(b, begin, end)

		//~: push all the items
		Array.prototype.push.apply(a, b)
		return a
	},

	/**
	 * Checks that two objects are array-like and
	 * have the same length and the items each
	 * strictly (===) equals.
	 */
	equals           : function(a, b)
	{
		if(!a || !b) return (a == null) && (a == b)
		if(a === b)  return true

		if(!ZeT.isi(a.length) || !ZeT.isi(b.length))
			return false

		if(a.length != b.length) return false
		for(var l = a.length, i = 0;(i < l);i++)
			if(a[i] !== b[i])
				return false
		return true
	}
}

//!: require browsers to support Array.indexOf()!
if(!Array.prototype.indexOf)
	throw new Error('Your browser has no Array.indexOf()!')


// +----: ZeT Library : -----------------------------------------+

var ZeT = window.ZeT = window.ZeT || {

// +----: Global Definitions: ----------------------------------->

	define           : function(name, object)
	{
		ZeT.asserts(name, 'ZeT difinitions are for string names only!')

		if(!window.ZeT$Global)
			window.ZeT$Global = {}

		var o; if(o = window.ZeT$Global[name])
			return o

		window.ZeT$Global[name] = object
		return object
	},

	defined          : function(name)
	{
		ZeT.asserts(name, 'ZeT difinitions are for string names only!')
		return window.ZeT$Global && window.ZeT$Global[name]
	},

	init             : function(name, init)
	{
		ZeT.asserts(name, 'ZeT initializations are for string names only!')
		ZeT.assert(ZeT.isf(init), 'Function is required for ZeT initialization!')

		if(!window.ZeT$Init)
			window.ZeT$Init = {}

		if(!ZeT.isu(window.ZeT$Init[name]))
			return window.ZeT$Init[name]

		//!: invoke the initializer
		window.ZeT$Init[name] = func() || true

		return window.ZeT$Init[name]
	},

	delayed          : function(obj)
	{
		if(ZeT.isf(obj) && (obj.ZeT$delay === true))
			obj = obj()

		return obj
	},

	delayedProp      : function(obj, prop)
	{
		if(!obj) return obj

		//?: {process the given property}
		var val; if(prop)
		{
			val = obj[prop]
			if(ZeT.isf(val) && (val.ZeT$delay === true))
				obj[prop] = val()
		}
		//~: process the whole object
		else for(var p in obj)
			if(p && ZeT.isf(val = obj[p]) && (val.ZeT$delay === true))
				obj[prop] = val()

		return obj
	},

	isDelayed        : function(obj_or_val, prop)
	{
		//?: {property is specified}
		if(prop && obj_or_val)
			obj_or_val = obj_or_val[prop]

		return !!obj_or_val && (obj_or_val.ZeT$delay === true)
	},

	delay            : function(f)
	{
		ZeT.assert(ZeT.isf(f), 'Zet.delay() may not delay not a function!')
		f.ZeT$delay = true
		return f
	},

	defineDelay      : function(name, func)
	{
		return this.define(name, this.delay(func))
	},


// +----: Object Programming: ----------------------------------->

	extend           : function(obj, ext)
	{
		if(!obj) obj = {}
		if(!ext) return obj

		//~: copy all the keys existing
		var keys = ZeT.keys(ext)
		for(var i = 0;(i < keys.length);i++)
			obj[keys[i]] = ext[keys[i]]

		return obj
	},

	/**
	 * ZeT.define() class by the name.
	 *
	 * Second and third arguments are
	 * passed to ZeT.createClass().
	 *
	 * Second argument may be a Class
	 * instance (or plain function), or
	 * a string name of else definition.
	 */
	defineClass      : function()
	{
		//~: access the class already defined
		var cls = ZeT.defined(arguments[0])
		if(cls) return cls

		//~: parent class is a definition
		cls = arguments[1]
		if(ZeT.iss(cls)) cls = ZeT.assertn(ZeT.defined(cls),
		  'Parent class definition name [', cls, '] is not found!')

		//~: create a class
		return ZeT.define(arguments[0],
		  ZeT.Class.call(ZeT.Class, cls, arguments[2]))
	},

	/**
	 * Creates instance of the defined class given.
	 *
	 * 0   definition key name or Class object;
	 * 1.. passed to class constructor.
	 */
	createInstance   : function()
	{
		//~: access class definition
		var cls = arguments[0]
		if(ZeT.iss(cls)) cls = ZeT.defined(cls)
		ZeT.assert(ZeT.isf(cls) && (cls.ZeT$Class === true),
		 'Can not create instance of not a Class!')

		//~: remove 0-argument (definition name)
		var args = ZeTA.copy(arguments, 1)
		return cls.create.apply(cls, args)
	},

	/**
	 * ZeT.define() instance of the class given.
	 * If instance with this name exists, returns it instead.
	 *
	 * 0   string (unique) key name of instance;
	 * 1.. passed to createInstance().
	 */
	defineInstance   : function()
	{
		//~: lookup it is already defined
		var res = ZeT.defined(arguments[0])
		if(res) return res

		//~: remove 0-argument (definition name)
		var args = ZeTA.copy(arguments, 1)
		res = ZeT.createInstance.apply(this, args)

		//~: define it
		return ZeT.define(arguments[0], res)
	},

	/**
	 * Defines instance of a temporary (anonymous) class.
	 *
	 * 0   string (unique) key name of instance;
	 *
	 * 1   [optional] definition key name, or Class,
	 *     or plain function to be the parent class
	 *     of the temporary one;
	 *
	 * 2   the body of the class;
	 *
	 * 3.. [optional] arguments of the class constructor
	 *     to create temporary instance.
	 */
	singleInstance   : function()
	{
		//~: lookup it is already defined
		var res = ZeT.defined(arguments[0])
		if(res) return res

		//~: access the parent class defined
		var pcls = arguments[1]; if(ZeT.iss(pcls))
		{
			pcls = ZeT.defined(pcls); ZeT.assert(ZeT.isf(pcls),
			  'Can not create instance of not a Class or function!')
		}

		//~: arguments of class create invocation
		var cargs = ZeT.isf(pcls)?([pcls, arguments[2]]):([arguments[1]])

		//~: create the anonymous class
		var cls = ZeT.Class.apply(ZeT.Class, cargs)

		//~: copy constructor arguments
		var args = ZeTA.merge([cls], arguments, ZeT.isf(pcls)?(3):(2))

		//~: create and define the instance
		var obj = ZeT.createInstance.apply(ZeT, args)
		return ZeT.define(arguments[0], obj)
	},


// +----: Function Helpers: ------------------------------------->

	/**
	 * Returns a function having 'this' assigned to 'that'
	 * argument and the following arguments passed as
	 * the first arguments of each call.
	 *
	 * 0   [required] a function;
	 * 1   [required] 'this' context tu use;
	 * 2.. [optional] first and the following arguments.
	 */
	fbind            : function(f, that)
	{
		//?: {has function and the context}
		ZeT.assert(ZeT.isf(f))
		ZeT.assertn(that)

		//~: copy the arguments
		var args = ZeTA.copy(arguments, 2)

		return function()
		{
			var a = ZeTA.merge(ZeTA.copy(args), arguments)
			return f.apply(that, a)
		}
	},

	/**
	 * Works as ZeT.fbind(), but takes additional
	 * arguments as a copy of array-like object given.
	 */
	fbinda           : function(f, that, args)
	{
		//?: {has function and the context}
		ZeT.assert(ZeT.isf(f))
		ZeT.assertn(that)

		//~: copy the arguments
		args = ZeTA.copy(args)

		return function()
		{
			return f.apply(that, args)
		}
	},

	/**
	 * Creates a function that sequentially calls the
	 * functions given as the arguments.
	 *
	 * All the functions share the same call context.
	 * The arguments of the pipe call are given to the
	 * first function. The result is then given as the
	 * arguments of the next call as arguments array!
	 * So when the result is an array, and you want it
	 * to come as the first argument only, but is not
	 * split into the arguments, wrap it in array.
	 *
	 * Warning: if intermediate result is undefined
	 * or null, the pipe processing is stopped!
	 *
	 * The result of the last call is returned as is.
	 */
	pipe             : function(/* functions */)
	{
		var fn = []; ZeT.each(arguments, function()
		{
			ZeT.assert(ZeT.isf(this)); fn.push(this)
		})

		//?: {has just one item in the pipe}
		ZeT.assert(fn.length, 'ZeT.pipe() functions are not defined!')
		if(fn.length == 1) return fn[0]

		return function()
		{
			var r = arguments //<-- intermediate result

			for(var i = 0;(i < fn.length);i++)
			{
				//?: {previous results are not an array}
				if(!ZeT.isa(r)) r = [r] //<-- wrap for apply

				//~: invoke the i-th function of the pipe
				r = fn[i].apply(this, r)

				//?: {has no result}
				if(ZeT.isu(r) || (r === null))
					return r
			}

			return r
		}
	},

	/**
	 * Shorthand for setTimeout() function that takes
	 * the function given and optionally binds it with
	 * this-context and the arguments array given.
	 *
	 * Returns the argument function, or the bound one.
	 */
	timeout          : function(tm, f, that, args)
	{
		ZeT.assert(ZeT.isn(tm) && (tm >= 0), 'ZeT.timeout(): illegal timeout!')
		ZeT.assert(ZeT.isf(f), 'ZeT.timeout(): not a function!')

		//?: {do bind}
		if(that) f = ZeT.fbinda(f, that, args)

		setTimeout(f, tm)
		return f
	},

	/**
	 * Returns function that on-call activates timeout
	 * for the function given as the first argument.
	 * Arguments are the same as in ZeT.timeout().
	 */
	timeouted        : function()
	{
		var args = arguments

		return function()
		{
			return ZeT.timeout.apply(ZeT, args)
		}
	},


// +----: Test Functions: --------------------------------------->

	iss              : function(s)
	{
		return (typeof s === 'string')
	},

	isf              : function(f)
	{
		return (typeof f === 'function')
	},

	isb              : function(b)
	{
		return (typeof b === 'boolean')
	},

	isu              : function(o)
	{
		return (typeof o === 'undefined')
	},

	isa              : ('isArray' in Array)?(Array.isArray):function(a)
	{
		return (Object.prototype.toString.call(a) === '[object Array]')
	},

	isn              : function(n)
	{
		return (n === +n)
	},

	isi              : function(i)
	{
		return (i === +i) && (i === (i|0))
	},


// +----: Helper Functions: ------------------------------------->

	/**
	 * Takes any array-like object and returns true array.
	 * If source object is an array, return it.
	 *
	 * Array-like objects do have integer length property
	 * and values by the integer keys [0; length).
	 *
	 * If object given is not an array, wraps it to array.
	 * Undefined or null value produces empty array.
	 *
	 * If source object has toArray() method, that method
	 * is invoked with this-context is the object.
	 */
	a                : function(a)
	{
		if(ZeT.isa(a)) return a
		if(ZeT.isu(a) || (a === null)) return []
		if(ZeT.iss(a)) return [a]

		if(ZeT.isf(a.toArray))
		{
			a = a.toArray()
			ZeT.assert(ZeT.isa(a), 'ZeT.a(): .toArray() returned not an array!')
			return a
		}

		//~: manually copy the items
		var l = a.length; if(!ZeT.isi(l)) return [a]
		var r = new Array(l)
		for(var i = 0;(i < l);i++) r[i] = a[i]

		return r
	},

	keys             : function(o)
	{
		if(ZeT.isf(Object.keys))
			return Object.keys(o)

		//~: find proper property checker
		var q, r = [];
		if(ZeT.isf(o.hasOwnProperty)) q = o.hasOwnProperty
		else if(ZeT.isf(Object.prototype.hasOwnProperty))
			q = Object.prototype.hasOwnProperty
		else
			q = function(){ return true; }

		//c: for each object key
		for(var p in o) if(q.call(o, p))
			r.push(p)

		return r
	},

	/**
	 * First argument of assertion is an expression
	 * evaluated as extended if-check. The following
	 * optional arguments are the message components
	 * concatenated to string.
	 *
	 * The function returns the test value given.
	 */
	assert           : function(test /* messages */)
	{
		if(test) return test

		var m = ZeTS.cati(1, arguments)
		if(ZeTS.ises(m)) m = 'Assertion failed!'

		throw new Error(m)
	},

	/**
	 * Checks that given object is not
	 * null, or undefined.
	 */
	assertn          : function(obj /* messages */)
	{
		if((obj !== null) && !ZeT.isu(obj))
			return obj

		var m = ZeTS.cati(1, arguments)
		if(ZeTS.ises(m)) m = 'The object is undefined or null!'

		throw new Error(m)
	},

	/**
	 * Tests the the given object is a not-empty array
	 * and returns it back.
	 */
	asserta          : function(array /* messages */)
	{
		if(ZeT.isa(array) && array.length)
			return array

		var m = ZeTS.cati(1, arguments)
		if(ZeTS.ises(m)) m = 'A non-empty array is required!'

		throw new Error(m)
	},

	/**
	 * Tests that the first argument is a string
	 * that is not whitespace-empty. Returns it.
	 */
	asserts          : function(str /* messages */)
	{
		if(!ZeTS.ises(str))
			return str

		var m = ZeTS.cati(1, arguments)
		if(ZeTS.ises(m)) m = 'A not whitespace-empty string is required!'

		throw new Error(m)
	},

	/**
	 * Evaluates the script given in the function body.
	 */
	xeval            : function(script)
	{
		if(ZeTS.ises(script)) return
		eval('((function(){'.concat(script, '})())'))
	},

	/**
	 * Takes array-like object and invokes the
	 * function given on each item. Function
	 * receives arguments: [0] is the item,
	 * [1] is the item index.
	 *
	 * This-context of the function call
	 * is also the item iterated.
	 *
	 * If call on some item returns false, iteration
	 * is breaked and that stop-index is returned.
	 */
	each             : function(a, f)
	{
		ZeT.assert(a && ZeT.isn(a.length))
		ZeT.assert(ZeT.isf(f))

		for(var i = 0;(i < a.length);i++)
			if(f.call(a[i], a[i], i) === false)
				return i
		return undefined
	},


// +----: Debug Logging: ----------------------------------------+

	log              : function (/* strings */)
	{
		var msg = String.prototype.concat.apply('', arguments)
		if(ZeTS.ises(msg)) return

		if(ZeT.i$f(console, 'log'))
			console.log(msg)
	},


// +----: Te$t Functions: ---------------------------------------+

	/**
	 * Checks the value is undefined or null.
	 *
	 * The following (optional) arguments define the keys in the
	 * sequence of property access of the object.
	 */
	i$x               : function(/* object, properties list */)
	{
		var o = arguments[0]
		var r = ZeT.isu(o) || (o === null)

		//?: {not need to check further}
		if(r || (arguments.length == 1)) return !!r

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.isu(o) || (o === null)) return true
		}

		return false
	},

	/**
	 * Returns true when the flag is undefined or
	 * is not set (===) to false.
	 */
	i$xtrue           : function(/* object, properties list */)
	{
		var o = arguments[0]
		var u = ZeT.i$x(o)

		//?: {not need to check further}
		if(u || (arguments.length == 1))
			return u || !(o === false)

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.i$x(o)) return true
		}

		return (o !== false)
	},

	/**
	 * Returns true when the flag is undefined or
	 * is not set (===) to true.
	 */
	i$xfalse          : function(/* object, properties list */)
	{
		var o = arguments[0]
		var u = ZeT.i$x(o)

		//?: {not need to check further}
		if(u || (arguments.length == 1))
			return u || !(o === true)

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.i$x(o)) return true
		}

		return (o !== true)
	},

	/**
	 * Checks the value, or a property value is a function.
	 * If the final property is not accessible, false
	 * value is returned.
	 */
	i$f               : function(/* object, properties list */)
	{
		var o = arguments[0]

		if(arguments.length == 1)
			return ZeT.isf(o)

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.i$x(o)) return false
		}

		return ZeT.isf(o)
	},

	/**
	 * Checks the value, or a property value is an array.
	 * If the final property is not accessible, false
	 * value is returned.
	 */
	i$a               : function(/* object, properties list */)
	{
		var o = arguments[0]

		if(arguments.length == 1)
			return ZeT.isa(o)

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()] //<-- access the next property
			if(ZeT.i$x(o)) return false
		}

		return ZeT.isa(o)
	}
}


// +----: ZeT.Class: --------------------------------------------+

/**
 * Creates Class instance. The arguments are:
 *
 * 0  [optional] parent Class instance;
 * 1  [optional] body object with the Class methods.
 *
 * The parent Class may be of ZeT implementation: each such
 * instance is marked with (Class.ZeT$Class = true).
 *
 * It is allowed the parent Class to be a general Function.
 * As for ZeT Class inheritance, Function.prototype will be
 * the parent [[Prototype]] of Class.prototype. To call Function
 * (as a constructor) from Class initialization method (see
 * Class.initializer() method), use the same $superApply() or
 * $superCall() runtime-added methods.
 *
 * The body object may contain not only the methods, but properties
 * of other types: they are 'static' members of the prototype of
 * the instances created.
 *
 * The returned Class instance is a Function having the following
 * instance methods:
 *
 * · create(...) : new instance of Class
 *
 *   creates an instance of the Class. Takes any number of arguments
 *   that are passed as-is to the initialization method.
 *
 * · extend({body} | [{body}]) : this Class
 *
 *   adds the methods (and the properties) of the body (or array of
 *   bodies) given to the prototype of the Class. Note that the methods
 *   (as references) are copied wrapped, and adding methods (or fields)
 *   to the body object after extending has no effect.
 *
 * · addMethod(name, f) : this Class
 *
 *   adds the method given to the prototype of Class. Note that the
 *   function given is wrapped to provide $-objects at the call time.
 *
 * · initializer([names]) : this Class
 *
 *   give an array of names (or single name) with the body' initialization
 *   method. Default names are: 'initialize', 'init', and 'constructor'.
 *   Only the first method found in the instance is called.
 *
 *   Note that constructor() is always defined when plain Function was
 *   inherited Hence, 'constructor' must be the last in the list, or you
 *   have to implement constructor() as the initializing method.
 *
 * The instances created as a Class has the following properties and methods:
 *
 * · $class  it's Class instance.
 *
 * · $plain  equals to a plain Function when it is the root of hierarchy;
 *
 * · $callSuper(), $applySuper()
 *
 *   these functions are available only within a method call.
 *   They invoke the method with the same name defined in the
 *   ancestor classes hierarchy.
 *
 * · $callContext
 *
 *   as $callSuper(), available only within a method call.
 *   It contains the following properties:
 *
 *   · name:  the name of the method (currently invoked);
 *
 *   · wrapped:  the original method added to Class (and wrapped);
 *
 *   · method: method is being invoked (i.e., the wrapper);
 *
 *   · callSuper, applySuper:  functions that are assigned
 *     as $- to object when invoking a method;
 *
 *   · superFallback: function to invoke within $call-,
 *     $applySuper() when super method was not found.
 *
 *    Note that $callContext object is shared between the calls
 *    of the body method wrapped! (Each function in the method
 *    hierarchy still has it's own instance.)
 */
ZeT.Class = ZeT.Class || function()
{
	//~: initialization methods lookup array
	var inits = ['initialize', 'init', 'constructor']

	//!: the Class instance to return
	function Class()
	{
		//c: process the initialize names list
		for(var i = 0;(i < inits.length);i++)
		{
			var m = this[inits[i]]
			if(!ZeT.isf(m)) continue

			//?: {this is a root Function constructor} skip it for now
			if(Class.$plain && (m === Class.$plain.prototype.constructor))
				continue

			//?: {this is Object constructor}
			if(m === Object.prototype.constructor) continue

			//~: install fallback for plain Function root
			if(Class.$plain && m.$callContext)
				m.$callContext.superFallback = Class.$plain

			//!: call the initializer
			return m.apply(this, arguments)
		}

		//HINT: we found no initialization method in the body...

		//?: {has hierarchy root Function} invoke it as a fallback
		if(Class.$plain)
			Class.$plain.apply(this, arguments)
	}

	//:: Class.$super
	Class.$super = ZeT.isf(arguments[0])?(arguments[0]):(null)

	//:: Class.$plain
	if(Class.$super) Class.$plain = (Class.$super.ZeT$Class === true)?
	  (Class.$super.$plain):(Class.$super)

	//?: {has parent class} use it as a prototype
	if(!Class.$super) Class.prototype = {}; else (function()
	{
		function U() {}
		U.prototype = Class.$super.prototype
		Class.prototype = new U()
	})()

	//:: Class.create()
	Class.create = function()
	{
		var args = arguments

		function C()
		{
			Class.apply(this, args)
		}

		C.prototype = Class.prototype
		return new C()
	}

	function createCallContext(name, f)
	{
		return { name: name, wrapped : f,

			assign  : function(that)
			{
				//:: this.$callContext
				that.$callContext = this

				//:: this.$callSuper
				that.$callSuper  = this.callSuper

				//:: this.$applySuper
				that.$applySuper = this.applySuper
			},

			revoke  : function(that)
			{
				delete that.$callContext
				delete that.$callSuper
				delete that.$applySuper
			}
		}
	}

	//:: Class.addMethod()
	Class.addMethod = function(name, f)
	{
		//~: find super method and invalidate it's cache marker
		var sx, sm = Class.$super && Class.$super.prototype[name]
		if(ZeT.isf(sm)) sm.$cacheMarker = sx = {}
			else sm = undefined

		function accessSuper(that)
		{
			//?: {has super method & the marker is actual}
			if(sm && (sm.$cacheMarker === sx))
				return sm

			//~: find it
			sm = Class.$super && Class.$super.prototype[name]
			if(ZeT.isf(sm)) sx = sm.$cacheMarker; else
			{
				sm = undefined

				//?: {has fallback call provided}
				var fb = that.$callContext.superFallback
				if(fb) return fb

				throw new Error('$super method (' + name + ') not found!')
			}

			return sm
		}

		//~: invalidate cache marker of existing method
		(function()
		{
			var m = Class.prototype[name]
			if(ZeT.isf(m)) m.$cacheMarker = {}
		})()

		//~: wrap the method
		function Method()
		{
			//HINT: when method is invoked recursively,
			//  it has the same call context

			var x = this.$callContext //<-- current call context
			var a = !x || (x.method !== Method) //?: is it changed

			try
			{
				//?: {new call context must be assigned}
				if(a) Method.$callContext.assign(this)

				//!: invoke the function is being wrapped
				return f.apply(this, arguments)
			}
			finally
			{
				//?: {has new call context assigned}
				if(a) try
				{
					this.$callContext.revoke(this)
				}
				finally
				{
					//?: {has external context} return to it
					if(x) x.assign(this)
				}
			}
		}

		//~: assign wrapper to the prototype
		Class.prototype[name] = Method

		//:: Class.[Method].$callContext
		Method.$callContext = createCallContext(name, f)
		Method.$callContext.method = Method

		//:: Class.[Method].$callSuper
		Method.$callContext.callSuper = function()
		{
			return accessSuper(this).apply(this, arguments)
		}

		//:: Class.[Method].$applySuper
		Method.$callContext.applySuper = function(args)
		{
			return accessSuper(this).apply(this, args)
		}

		return Class
	}

	//:: Class.extend()
	Class.extend = function(body)
	{
		if(!body) return Class
		if(!ZeT.isa(body)) body = [body]

		for(var j = 0;(j < body.length);j++)
		{
			var b = body[j], k, v, ks = ZeT.keys(b), p = Class.prototype
			for(var i = 0;(i < ks.length);i++)
			{
				k = ks[i]; v = b[k]
				if(!ZeT.isf(v)) p[k] = v; else
					Class.addMethod(k, v)
			}
		}

		return Class
	}

	//~: extend with the body given
	Class.extend((Class.$super)?(arguments[1]):(arguments[0]))

	//:: Class.initializer()
	Class.initializer = function(a)
	{
		if(a && !ZeT.isa(a)) a = [a]
		if(ZeT.isa(a) && a.length)
			inits = a
		return Class
	}

	//:: this.$class
	Class.prototype.$class = Class

	//~: mark as a Class instance
	Class.ZeT$Class = true

	return Class
}


// +----: ZeT XML : ---------------------------------------------+

var ZeTX = ZeT.define('ZeT XML Support',
{
	nodes            : function(xml, name)
	{
		if(!xml) return xml
		if(!ZeT.isf(xml.getElementsByTagName)) return undefined
		return xml.getElementsByTagName(name) || []
	},

	node             : function(xml, name)
	{
		if(!xml) return xml
		var res = ZeTX.nodes(xml, name)
		return (res && res.length)?(res[0]):(null)
	},

	attr             : function(node, attr)
	{
		return node && ZeT.isf(node.getAttribute) &&
		  node.getAttribute(attr)
	},

	/**
	 * Returns the text values of the node immediate
	 * children with text and CDATA types.
	 */
	text             : function(node)
	{
		if(!node) return node

		//?: {text || cdata}
		if((node.nodeType === 3) || (node.nodeType === 4))
			return node.nodeValue

		var val, res = []

		if(node.nodeType !== 1) return undefined
		node = node.firstChild

		while(node)
		{
			//?: {text || cdata}
			if((node.nodeType === 3) || (node.nodeType === 4))
				val = node.nodeValue
			if(ZeT.iss(val)) res.push(val)

			node = node.nextSibling
		}

		return String.prototype.concat.apply('', res)
	}
})