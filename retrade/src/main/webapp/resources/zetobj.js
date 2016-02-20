/*===============================================================+
 |                                                          zet  |
 |                   0-ZeT JavaScript Library                    |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================+
 | Requires [ lodash ]
 +---------------------------------------------------------------*/


// +----: Lodash Library  :--------------------------------------+

/**
 * Variable 'Lo' is used instead of '_'...
 */
var Lo = window._; if(!Lo)
	throw new Error('Lodash library is not defined!')


// +----: ZeT Mini-Core :----------------------------------------+

if(window.ZeT)
	throw new Error('Double defined ZeT library!')

/**
 * The sections of the listing are organized so that each
 * following section may depend on items defined previously.
 * This zero section depends only on Lodash library.
 */
var ZeT = window.ZeT =
{
	keys             : Lo.keys,

	/**
	 * Extends optional object with optional extension.
	 * Assigns only own properties of the object.
	 * Returns the extended object.
	 */
	extend           : function(obj, ext)
	{
		if(!obj) obj = {}
		if(!ext) return obj

		Lo.assign(obj, ext)
		return obj
	},

	/**
	 * Invokes the function given. Optiona arguments
	 * must go before the function-body.
	 */
	scope            : function(/* [parameters] f */)
	{
		var f = arguments[arguments.length - 1]
		if(!Lo.isFunction(f))
			throw new Error('ZeT.scope() got not a function!')

		//?: {has additional arguments}
		for(var a = [], i = 0;(i < arguments.length - 1);i++)
			a.push(arguments[i])

		return (a.length)?(f.apply(this, a)):(f.call(this))
	},

	/**
	 * Directly concatenates to string items of array-like
	 * object starting with the index given.
	 */
	cati             : (function(/* index, array-like */)
	{
		var concat = String.prototype.concat

		function isx(o)
		{
			return (o === null) || (typeof o === 'undefined')
		}

		return function(index, objs)
		{
			if(!objs || !Lo.isInteger(objs.length))
				return ''

			for(var i = 0;(i < objs.length);i++)
				if((i < index) || isx(objs[i]))
					objs[i] = ''

			return concat.apply('', objs)
		}
	})(),

	stack            : function()
	{
		return '' + new Error().stack
	},
}


// +----: ZeT Checks :-------------------------------------------+

ZeT.extend(ZeT,
{
	/**
	 * Strict testing of strings, not so wide as in Lo.isString().
	 *
	 * Note that this check also handles case when string is
	 * coerced to raw object in calls like apply('s', ...)
	 */
	iss              : ZeT.scope(function(/* s */)
	{
		var s2s = String.prototype.toString

		return function(s)
		{
			return (typeof s === 'string') || (s && (s.toString === s2s))
		}
	}),

	/**
	 * Returns false for not a string objects, or for
	 * strings that are whitespace-trimmed empty.
	 */
	ises             : function(s)
	{
		return !ZeT.iss(s) || !s.length || !/\S/.test(s)
	},

	isf              : Lo.isFunction,

	iso              : Lo.isPlainObject,

	isb              : Lo.isBoolean,

	isu              : function(o)
	{
		return (typeof o === 'undefined')
	},

	isx              : function(o)
	{
		return (o === null) || (typeof o === 'undefined')
	},

	isa              : Lo.isArray,

	/**
	 * Test is array-like object. It is an array,
	 * or object that has integer length property,
	 * except string and functions.
	 */
	isax             : function(x)
	{
		return ZeT.isa(x) || (!ZeT.isx(x) &&
		  ZeT.isi(x.length) && !ZeT.iss(x) && !ZeT.isf(x))
	},

	isn              : Lo.isNumber,

	/**
	 * Is integer number.
	 */
	isi              : function(i)
	{
		return ZeT.isn(i) && (i === (i|0))
	},

	/**
	 * Returns true if the argument is defined, not false, 0, not
	 * ws-empty string or empty array, or array-like object having
	 * an item like that. (Up to one level of recursion only!)
	 *
	 * Warning as a sample: if you test agains an array that
	 * contains 0, false, null, undefined, ws-empty string,
	 * or an empty array (just empty) — test fails!
	 */
	test             : ZeT.scope(function(/* x */)
	{
		function notdef(x)
		{
			return (x === null) || (x === false) ||
			  (x === 0) || (typeof x === 'undefined') ||
			  (ZeT.iss(x) && ZeT.ises(x)) ||
			  (ZeT.isa(x) && !x.length)
		}

		return function(x)
		{
			//?: {root check is undefined}
			if(notdef(x)) return false

			//?: {root check is not array-like}
			if(!ZeT.isi(x.length)) return true

			//~: check all the items of array-like are defined
			for(var i = 0;(i < x.length);i++)
				if(notdef(x[i])) return false

			return true
		}
	})
})


// +----: ZeT Asserts :------------------------------------------+

ZeT.extend(ZeT,
{
	/**
	 * Returns exception concatenating the optional
	 * arguments into string message. The stack is
	 * appended as string after the new line.
	 */
	ass              : function(/* messages */)
	{
		var m = ZeT.cati(0, arguments)
		var x = ZeT.stack()

		//?: {has message}
		if(!ZeT.ises(m)) x = m.concat('\n', x)

		//!: return error to throw later
		return new Error(x)
	},

	/**
	 * First argument of assertion tested with ZeT.test().
	 * The following optional arguments are the message
	 * components concatenated to string.
	 *
	 * The function returns the test argument.
	 */
	assert           : function(test /* messages */)
	{
		if(ZeT.test(test)) return test

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'Assertion failed!'

		throw ZeT.ass(m)
	},

	/**
	 * Checks that given object is not null, or undefined.
	 */
	assertn          : function(obj /* messages */)
	{
		if(!ZeT.isx(obj)) return obj

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'The object is undefined or null!'

		throw ZeT.ass(m)
	},

	/**
	 * Tests the the given object is a function
	 * and returns it back.
	 */
	assertf          : function(f /* messages */)
	{
		if(ZeT.isf(f)) return f

		var m = ZeTS.cati(1, arguments)
		if(ZeT.ises(m)) m = 'A function is required!'

		throw ZeT.ass(m)
	},

	/**
	 * Tests that the first argument is a string
	 * that is not whitespace-empty. Returns it.
	 */
	asserts          : function(str /* messages */)
	{
		if(!ZeT.ises(str)) return str

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'Not a whitespace-empty string is required!'

		throw ZeT.ass(m)
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
		if(ZeT.ises(m)) m = 'Not an empty array is required!'

		throw ZeT.ass(m)
	}
})


// +----: ZeT Basics :-------------------------------------------+

ZeT.extend(ZeT,
{
	/**
	 * Defines global object in the window scope.
	 * If object is arguments is undefined, returns
	 * previously defined instance.
	 *
	 * When name has '.', they are used to trace
	 * the intermediate objects from the window top.
	 * 'ZeT.S' means that 'S' object is nested into
	 * 'ZeT' that is in the window (global) scope.
	 * Each intermediate object must exist.
	 *
	 * The same object is never defined twice.
	 * The initially defined object is returned.
	 */
	define           : function(name, object)
	{
		ZeT.asserts(name, 'ZeT difinitions are for string names only!')
		var scope = window, xname = name, i = name.indexOf('.')

		//~: trace the scope
		while(i != -1)
		{
			var n = name.substring(0, i)
			name = name.substring(i + 1)

			//?: {has empty name parts}
			ZeT.asserts(n, 'Empty intermediate name in ZeT.define(', xname, ')!')
			ZeT.asserts(name, 'Empty terminating name in ZeT.define(', xname, ')!')

			//?: {has the scope undefined}
			ZeT.assertn(scope = scope[n], 'Undefined intermediate scope object ',
			  'in ZeT.define(', xname, ') at [', n, ']!')

			i = name.indexOf('.')
		}

		//?: {target exists in the scope}
		var o = scope[name]
		if(!ZeT.isx(o)) return o

		//~: assign the target
		if(!ZeT.isx(object))
			scope[name] = object

		return object
	},

	/**
	 * Takes any array-like object and returns true array.
	 * If source object is an array, returns it as-is.
	 *
	 * Array-like objects do have integer length property
	 * and values by the integer keys [0; length).
	 *
	 * Note that strings are not treated as array-like.
	 * ZeT.a('...') returns ['...']. The same for functions.
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
		if(ZeT.iss(a) || ZeT.isf(a)) return [a]

		if(ZeT.isf(a.toArray))
		{
			ZeT.assert(ZeT.isa(a = a.toArray()),
			  'ZeT.a(): .toArray() returned not an array!')
			return a
		}

		//~: manually copy the items
		var l = a.length; if(!ZeT.isi(l)) return [a]
		var r = new Array(l)
		for(var i = 0;(i < l);i++) r[i] = a[i]

		return r
	}
})


// +----: ZeT Strings  :-----------------------------------------+

var ZeTS = ZeT.define('ZeT.S',
{
	iss              : ZeT.iss,

	ises             : ZeT.ises,

	/**
	 * Trims side white spaces of the string given.
	 * Returns empty string as a fallback.
	 */
	trim             : function(s)
	{
		return (!ZeT.iss(s) || !s.length)?(''):(s.replace(/^\s+|\s+$/g, ''))
	},

	first            : function(s)
	{
		return (ZeT.iss(s) && s.length)?(s.charAt(0)):(undefined)
	},

	starts           : Lo.startsWith,

	ends             : Lo.endsWith,

	/**
	 * Replaces plain string with else plain string.
	 */
	replace          : function(s, a, b)
	{
		return s.split(a).join(b)
	},

	cati             : ZeT.cati,

	/**
	 * Directly concatenates given objects into a string.
	 */
	cat              : function(/* various objects */)
	{
		return ZeTS.cati(0, arguments)
	},

	/**
	 * Concatenates trailing objects if the first one
	 * passes ZeT.test().
	 */
	catif            : function(x /* various objects */)
	{
		return ZeT.test(x)?ZeTS.cati(1, arguments):('')
	},

	/**
	 * Shortcut for ZeTS.catif() that checks all the arguments.
	 */
	catifall         : function(/* various objects */)
	{
		return ZeT.test(arguments)?ZeTS.cati(0, arguments):('')
	},

	/**
	 * Concatenates the objects with the separator given
	 * as the first argument, or as 'this' context object.
	 * Note that arrays are processed deeply.
	 */
	catsep           : function(/* sep, various objects */)
	{
		var x, b = 1, s = '', sep = arguments[0]

		//?: {invoked with string 'this'}
		if(ZeT.iss(this)) { b = 0; sep = this }

		//c: for each argument
		for(var i = b;(i < arguments.length);i++)
		{
			if(ZeT.isx(x = arguments[i]))
				continue

			if(!ZeT.iss(x))
			{
				//?: {is an array}
				if(ZeT.isa(x))
					x = ZeTS.catsep.apply(sep, x)
				//?: {toString()}
				else if(ZeT.isf(x.toString))
					x = x.toString()
				else
					x = Lo.toString(x)
			}

			//?: {empty string}
			if(ZeT.ises(x))
				continue

			if(s.length) s += sep
			s += x
		}

		return s
	},

	/**
	 * Invokes callback for each sub-string in the string.
	 * If callback returns false, breaks. Optional separator
	 * (defaults to /\s+/) argument to String.split().
	 */
	each             : function(/* [sep], s, f */)
	{
		var sep, s, f

		ZeT.scope(arguments.length, arguments, function(l, a)
		{
			ZeT.assert(l == 2 || l == 3)
			if(l == 2) { sep = /\s+/; s = a[0]; f = a[1] }
			else { sep = a[0]; s = a[1]; f = a[2] }
		})

		ZeT.assert(ZeT.iss(s))
		ZeT.assertf(f)

		s = s.split(sep)
		for(var i = 0;(i < s.length);i++)
			if(s[i].length)
				if(f(s[i]) === false)
					return this

		return this
	}
})


// +----: ZeT Arrays  :------------------------------------------+

var ZeTA = ZeT.define('ZeT.A',
{

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

		//?: {end is undefined}
		if(ZeT.isu(end) || (end > a.length))
			end = a.length

		//~: asserts on [begin; end)
		ZeT.assert(ZeT.isi(begin) && ZeT.isi(end))
		ZeT.assert((begin >= 0) && (begin <= end))

		//?: {is an array exactly}
		if(ZeT.isa(a)) return a.slice(begin, end)

		//~: manual copy
		var r = new Array(end - begin)
		for(var i = begin;(i < end);i++)
			r[i - begin] = a[i]
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
	 * Items are checked agains map-key equality
	 * (put it to map, then check it is there).
	 * Undefined and null items are supported.
	 *
	 * Returns the target array.
	 */
	remove           : ZeT.scope(function()
	{
		var u = {}, n = {}

		function collect(m, a)
		{
			if(ZeT.isu(a)) return m[u] = true
			if(a === null) return m[n] = true

			if(!ZeT.isax(a))
				return m[a] = true

			for(var i = 0;(i < a.length);i++)
				collect(m, a[i])
		}

		function test(m, x)
		{
			if(ZeT.isu(x)) x = u
			if(x === null) x = n
			return (m[x] === true)
		}

		return function()
		{
			var a, m = {}, i = 1, r = []

			//~: select the variant
			if(!ZeT.isa(this)) a = arguments[0]
			else { a = this; i = 0 }

			//~: collect the keys
			for(;(i < arguments.length);i++)
				collect(m, arguments[i])

			//console.log(m)

			//~: scan for ranged splicing
			for(i = 0;(i < a.length);i++)
				if(test(m, a[i]))
				{
					//~: scan for the range
					for(var j = i + 1;(j < a.length);j++)
						if(!test(m, a[j])) break;

					r.push(i)
					r.push(j - i)
					i = j //<-- advance
				}

			//~: back splicing
			for(var i = r.length - 2;(i >= 0);i -= 2)
				a.splice(r[i], r[i+1])

			return a //<-- target array
		}
	}),

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
	concat           : function(a, b, begin, end)
	{
		a = ZeT.a(a)

		//?: {has range} make a copy
		if(ZeT.isu(begin)) b = ZeT.a(b); else
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
	eq               : function(a, b)
	{
		if(a === b) return true
		if(ZeT.isx(a) || ZeT.isx(b))
			return (ZeT.isx(a) == ZeT.isx(b))

		//?: {not array-like}
		if(!ZeT.isi(a.length) || !ZeT.isi(b.length))
			return false

		//?: {length differ}
		var l = a.length
		if(l != b.length)
			return false

		for(var i = 0;(i < l);i++)
			if(a[i] !== b[i])
				return false
		return true
	}
})


// +----: ZeT Library  :-----------------------------------------+

var ZeT = window.ZeT = window.ZeT || {

// +----: Global Definitions :----------------------------------->

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

	/**
	 * Returns the object defined. If object is given,
	 * creates deep clone of the instance and extends
	 * it deeply with the properties of that object.
	 */
	defined          : function(name /*, obj*/)
	{
		ZeT.asserts(name, 'ZeT difinitions are for string names only!')
		var obj = window.ZeT$Global && window.ZeT$Global[name]

		if(ZeT.iso(obj) && ZeT.iso(arguments[1]))
		{
			obj = ZeT.deepClone(obj)
			obj = ZeT.deepExtend(obj, arguments[1])
		}

		return ZeT.delayedProp(obj)
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
		window.ZeT$Init[name] = init() || true

		return window.ZeT$Init[name]
	},

	delayed          : function(obj)
	{
		if(ZeT.isf(obj) && (obj.ZeT$delay === true))
			obj = obj()
		return obj
	},

	/**
	 * If property is defined, resolves that delayed
	 * property. If not, deeply resolves the object.
	 */
	delayedProp      : function(obj, prop)
	{
		if(!obj) return obj

		//?: {process the whole object}
		if(ZeT.isu(prop) || (prop === null))
		{
			for(var p in obj)
				this.delayedProp(obj, ZeT.assertn(p))
		}
		//~: or just the property given
		else
		{
			var val = obj[prop]
			if(ZeT.isf(val) && (val.ZeT$delay === true))
				obj[prop] = val()
		}

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


// +----: Object Programming :----------------------------------->

	/**
	 * Makes shallow copy of the source with
	 * optional extension provided.
	 */
	clone            : function(src, ext)
	{
		return ZeT.extend(ZeT.extend(null, src), ext)
	},

	/**
	 * Allows to clone deeply object with prototype support.
	 * It directly copies fields of this types: numbers, booleans,
	 * functions, not a plain objects. Arrays are copied deeply.
	 */
	deepClone        : function(obj)
	{
		//?: {undefined, null, false, zero}
		if(!obj) return obj

		//?: {is string} copy it
		if(ZeT.iss(obj)) return '' + obj

		//?: {is an array}
		var i, res; if(ZeT.isa(obj))
		{
			res = new Array(obj.length)
			for(i = 0;(i < obj.length);i++)
				res[i] = ZeT.deepClone(obj[i])
			return res
		}

		//?: {not a plain object}
		if(!ZeT.iso(obj)) return obj; else

		//~: create instance with same prototype
		(function()
		{
			function U() {}
			U.prototype = Object.getPrototypeOf(obj)
			res = new U()
		})()

		//~: extend
		var keys = ZeT.keys(obj)
		for(i = 0;(i < keys.length);i++)
			res[keys[i]] = ZeT.deepClone(obj[keys[i]])

		return res
	},

	/**
	 * Takes object and copies all the fields from the source
	 * when the same fields are undefined (note that nulls are
	 * not undefined). If field is a plain object, extends
	 * it deeply. Note that arrays are not merged.
	 * A deep clone of a field value is assigned.
	 */
	deepExtend       : function(obj, src)
	{
		if(!src) return obj
		if(!obj) obj = {}

		//?: {not an object}
		ZeT.assert(ZeT.iso(obj), 'ZeT.deepExtend(): not an object! ', obj)

		var k, keys = ZeT.keys(src)
		for(var i = 0;(i < keys.length);i++)
			//?: {field is undefined}
			if(ZeT.isu(obj[k = keys[i]]))
				obj[k] = ZeT.deepClone(src[k])
			//?: {extend nested object}
			else if(ZeT.iso(obj[k]))
				ZeT.deepExtend(obj[k], src[k])

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
		var args = ZeTA.concat([cls], arguments, ZeT.isf(pcls)?(3):(2))

		//~: create and define the instance
		var obj = ZeT.createInstance.apply(ZeT, args)
		return ZeT.define(arguments[0], obj)
	},

	/**
	 * Creates anonymous sub-class of the class given by it's
	 * Class instance or the definition name, extends it with
	 * the body methods given and passes the optional arguments
	 * to the instance constructor.
	 *
	 * The first variant of the arguments is:
	 *
	 * 0) Class or definition key; 1) arguments array;
	 * 2) sub-class definition body.
	 *
	 * The second variant is:
	 *
	 * 0) Class or definition key; 1) sub-class definition
	 * body; 2..) arguments list.
	 */
	hiddenInstance   : function()
	{
		var cls = arguments[0]

		//?: {parent class is a definition name}
		if(ZeT.iss(cls)) cls = ZeT.assertn(ZeT.defined(cls),
		  'No definition is bound by the name [', cls, ']!')

		ZeT.assert( ZeT.isf(cls), //?: {not a Class instance}
		  'Can not create instance of not a Class or function!')

		//~: take the body
		var args, body = arguments[1]
		if(ZeT.isa(body))
		{
			args = body
			body = arguments[2]
		}

		ZeT.assert(ZeT.iso(body), //?: {body is not an object}
		  'Anonymous class body is not an object!')

		//~: create the anonymous class
		var cls  = ZeT.Class.call(ZeT.Class, cls, body)

		//~: copy constructor arguments
		if(args) args = ZeTA.concat([cls], args)
		else     args = ZeTA.concat([cls], arguments, 2)

		//~: create the instance
		return ZeT.createInstance.apply(ZeT, args)
	},

	/**
	 * Extends the class (also, by it's definition name)
	 * with the body-methods given.
	 */
	extendClass      : function(cls, ext)
	{
		//~: access defined class
		if(ZeT.iss(cls)) cls = ZeT.defined(cls)
		ZeT.assert(cls.ZeT$Class === true, 'A Class is required to be extended!')

		//c: extend for each key
		ZeT.assertn(ext, 'Class extention is not given!')
		ZeT.each(ZeT.keys(ext), function(key)
		{
			if(ZeT.iss(key)) cls.addMethod(key, ext[key])
		})

		return cls
	},


// +----: Function Helpers :------------------------------------->

	/**
	 * Returns a function having 'this' assigned to 'that'
	 * argument and the following arguments passed as
	 * the first arguments of each call.
	 *
	 * 0   [required] a function;
	 * 1   [required] 'this' context to use;
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
			var a = ZeTA.concat(ZeTA.copy(args), arguments)
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
			var a = ZeTA.concat(ZeTA.copy(args), arguments)
			return f.apply(that, a)
		}
	},

	/**
	 * Universal variant of ZeT.fbind().
	 * Second argument may be this context.
	 * Else arguments are index (0-based)
	 * followed by the value.
	 */
	fbindu           : function(f /*, [this], (i, arg)* */)
	{
		//?: {has function and the context}
		ZeT.assert(ZeT.isf(f))

		var that = arguments[1], iarg = []

		//?: {with this-context}
		var i = 1; if(arguments.length%2 == 0) i = 2; else
			that = undefined

		//~: copy following arguments
		while(i < arguments.length)
		{
			ZeT.assert(ZeT.isi(arguments[i]))
			ZeT.assert(arguments[i] >= 0)
			iarg.push(arguments[i])
			ZeT.assert(i + 1 < arguments.length)
			iarg.push(arguments[i+1])
			i += 2
		}

		return function()
		{
			var a = ZeT.a(arguments)
			for(i = 0;(i < iarg.length);i += 2)
				a.splice(iarg[i], 0, iarg[i+1])

			return f.apply(ZeT.isu(that)?(this):(that), a)
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
			var r = ZeT.a(arguments) //<-- intermediate result

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


// +----: Helper Functions :------------------------------------->

	/**
	 * Trailing argument must be a function that
	 * is invoked only when all leading arguments
	 * are defined (not ZeT.isx). Boolean values
	 * are treated as well: false value stops call.
	 *
	 * Returns null when callback was not invoked,
	 * or the result of the function call.
	 */
	scopeif          : function(/* args, f */)
	{
		var a = ZeT.a(arguments)
		ZeT.assert(arguments.length)

		var f = a.pop()
		ZeT.assert(ZeT.isf(f))

		for(var i = 0;(i < a.length);i++)
			if(ZeT.isx(a[i]) || (a[i] === false))
				return null

		return f.apply(this, a)
	},

	get              : function(/* object, properties list */)
	{
		var o = arguments[0]

		for(var i = 1;(i < arguments.length);i++)
		{
			var k = ZeT.assertn(arguments[i],
			  'ZeT.get() key at [', i, '] is undefined!')

			if(ZeT.isu(o = o[k]) || (o === null))
				return undefined
		}

		return o
	},

	/**
	 * Evaluates the script given in a function body.
	 */
	xeval            : function(script)
	{
		if(ZeT.ises(script)) return undefined
		return eval('((function(){'.concat(script, '})())'))
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
		if(!a) return undefined
		ZeT.assert(ZeT.isi(a.length))
		ZeT.assert(ZeT.isf(f))

		for(var i = 0;(i < a.length);i++)
			if(f.call(a[i], a[i], i) === false)
				return i
		return a.length
	},

	collect          : function(a, f)
	{
		//?: {collect a property}
		var p; if(ZeT.iss(p = f)) f = function(x) { return x[p] }
		ZeT.assert(ZeT.isf(f))

		var r = []; ZeT.each(a, function(x, i)
		{
			x = f.call(x, x, i)
			if(!ZeT.isu(x)) r.push(x)
		})

		return r
	},


// +----: Assertions :------------------------------------------->


// +----: Debug Logging :----------------------------------------+

	/**
	 * Logs the values provided and returns the value:
	 *
	 * · if there are no arguments, all are undefined,
	 *   or ws-empty strings, returns undefined;
	 *
	 * · the last argument being not a string;
	 *
	 * · the last not empty string.
	 */
	log              : function (/* objects */)
	{
		var j = 0, a = ZeT.a(arguments)

		//~: result --> last not a string
		var r; for(var i = a.length - 1;(i >= 0);i--)
			if(!ZeT.i$x(a[i]) && !ZeT.iss(a[i]))
				{ r = a[i]; break }

		//~: result --> last not a ws-empty string
		if(!r) for(i = a.length - 1;(i >= 0);i--)
			if(!ZeT.ises(a[i]))
				{ r = a[i]; break }

		function pack(i)
		{
			if(j + 1 >= i) return i

			for(var x = '', k = j;(k < i);k++)
				if(!ZeT.isu(a[k]) && (a[k] !== null))
					x += a[k]

			a[j] = x; a.splice(j + 1, i - j - 1)
			return j
		}

		function ise(x)
		{
			return ZeT.isu(x) || (x == null) ||
			  (ZeT.iss(x) && ZeT.ises(x))
		}

		for(i = 0;(i < a.length);i++)
			if(ZeT.isxlog(a[i]))
			{
				i = pack(i)
				j = i + 1
			}

		pack(a.length)

		var empty = true
		for(j = 0;(j < a.length);j++)
			if(!ise(a[j])) { empty = false; break }

		if(!empty) console.log.apply(console, a)
		return r
	},

	isxlog           : function(o)
	{
		if(!o) return false

		//?: {is plain object}
		if(typeof o === 'object') return true

		//?: {is an element}
		if(o.nodeType === 1) return true

		return false
	},


// +----: Te$t Functions :---------------------------------------+

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
	},



	/**
	 * Converts given object to JSON formatted string.
	 */
	o2s              : function(o)
	{
		return JSON.stringify(o)
	},

	/**
	 * Reverse to ZeT.o2s().
	 */
	s2o              : function(s)
	{
		return (ZeT.isx(s) || ZeT.ises(s))?(null):JSON.parse(s)
	}
}


// +----: ZeT.Class :--------------------------------------------+

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
 * · static : empty Object
 *
 *   use this object to store data shared for every instance
 *   of this Class.
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

	//:: Class.static
	Class.static = {}

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


// +----: ZeT XML  :---------------------------------------------+

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