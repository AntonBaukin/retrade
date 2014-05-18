/*===============================================================+
 |                                                     zetobj    |
 |   Zero ZeT  Java Script Library                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


var ZeT = window.ZeT = window.ZeT || {

// +----: Global Definitions: -----------------------------------+

	define           : function(name, object)
	{
		if(!ZeT.iss(name) || !name.length)
			throw 'ZeT difinitions are for string names only!';

		var o;

		if(!window.ZeT$Global)
			window.ZeT$Global = {};

		if(o = window.ZeT$Global[name])
			return o;

		window.ZeT$Global[name] = object;
		return object;
	},

	defined          : function(name)
	{
		var g = window.ZeT$Global;
		return g && g[name];
	},

	init             : function(name, func)
	{
		if(!ZeT.iss(name) || !name.length)
			throw 'ZeT initialization are for string names only!';

		if(!ZeT.isf(func))
			throw 'function required for ZeT initialization!';

		if(!window.ZeT$Init)
			window.ZeT$Init = {};

		if(window.ZeT$Init[name] === true)
			return this;

		window.ZeT$Init[name] = true;
		func()
		return this;
	},

	delayed          : function(obj)
	{
		if(ZeT.isf(obj) && (obj.ZeT$delay === true))
			obj = obj();
		return obj;
	},

	delayedProp      : function(obj, prop)
	{
		if(!obj) return obj;

		if(!prop)
		{
			for(var p in obj) if(p)
				this.delayedProp(obj, p)
			return;
		}

		var val = obj[prop];

		if(ZeT.isf(val) && (val.ZeT$delay === true))
			obj[prop] = val();
		return this;
	},

	isDelayed        : function(obj_or_val, prop)
	{
		if(prop) obj_or_val = obj_or_val && obj_or_val[prop];
		if(!obj_or_val) return false;
		return (obj_or_val.ZeT$delay === true);
	},

	delay            : function(func)
	{
		if(!ZeT.isf(func))
			throw 'Zet.delay can not delay not a function!';

		func.ZeT$delay = true;
		return func;
	},

	defineDelay      : function(name, func)
	{
		return this.define(name, this.delay(func));
	},


// +----: Object Programming: -----------------------------------+

	extend           : function(obj, ext)
	{
		if(!obj) obj = {};
		if(ext) for(var p in ext)
			obj[p] = ext[p];
		return obj;
	},

	/**
	 * Prototype JS: Class.create().
	 */
	createClass      : function()
	{
		return ZeT$Impl.Class.create.
		  apply(ZeT$Impl.Class, arguments);
	},

	/**
	 * First argument if the definition name.
	 * The class is not created again if it is
	 * already defined.
	 *
	 * Analogue in Prototype JS: Class.create().
	 */
	defineClass      : function()
	{
		var name  = arguments[0];
		if(!ZeT.iss(name))
			throw 'ZeT difinitions are for string names only!';

		var c1ass = ZeT.defined(name);
		if(c1ass) return c1ass;

		var parcl = arguments[1];
		if(ZeT.iss(parcl)) parcl = ZeT.defined(parcl);
		var args  = ZeT$Impl.update_args([parcl], arguments, 2);

		return ZeT.define(name, ZeT.createClass.apply(ZeT, args));
	},

	/**
	 * Creates instance of the defined class given.
	 *
	 * 0   definition key name or class object;
	 * 1.. passed to class constructor.
	 */
	createInstance   : function()
	{
		var c1ass = arguments[0];

		if(ZeT.iss(c1ass)) c1ass = ZeT.defined(c1ass);
		if(!ZeT.isf(c1ass)) throw 'Can not create instance of not a Class';

		var args = ZeT$Impl.update_args([], arguments, 1);
		var res  = ZeT.extend({}, c1ass.prototype);
		c1ass.prototype.constructor.apply(res, args)

		return res;
	},

	/**
	 * Creates named instance of the class defined.
	 * If instance with this name exists, returns it instead.
	 *
	 * 0   string (unique) key name of instance;
	 * 1.. passed to createInstance().
	 */
	defineInstance   : function()
	{
		var name  = arguments[0];
		if(!ZeT.iss(name))
			throw 'ZeT difinitions are for string names only!';

		var res   = ZeT.defined(name);
		if(res) return res;

		var args = ZeT$Impl.update_args([], arguments, 1);
		res = ZeT.createInstance.apply(this, args);

		return ZeT.define(name, res);
	},

	/**
	 * Defines instance of temporary class.
	 *
	 * 0   string (unique) key name of instance;
	 *
	 * 1   [optional] definition key name or class object
	 *     for parent class of the temporary one;
	 *
	 * 2   the body of the class;
	 *
	 * 3.. [optional] arguments of the class constructor
	 *     to create temporary instance.
	 */
	singleInstance   : function()
	{
		var name  = arguments[0];
		if(!ZeT.iss(name))
			throw 'ZeT difinitions are for string names only!';

		var res   = ZeT.defined(name);
		if(res) return res;

		var pcls  = arguments[1];
		if(ZeT.iss(pcls))
		{
			pcls = ZeT.defined(pcls);
			if(!ZeT.isf(pcls)) throw 'Can not create instance of not a Class';
		}

		//~: arguments of class create invocation
		var cargs = ZeT.isf(pcls)?([pcls, arguments[2]]):([arguments[1]]);

		//~: create the class
		var c1ass = ZeT.createClass.apply(ZeT, cargs);

		//~: create an instance
		var args = ZeT$Impl.update_args([c1ass], arguments, ZeT.isf(pcls)?(3):(2));
		return ZeT.define(name, ZeT.createInstance.apply(ZeT, args));
	},

	/**
	 * Prototype JS: Class.addMethods().
	 */
	extendClass      : function(c1ass, methods)
	{
		if(ZeT.iss(c1ass)) c1ass = ZeT.defined(c1ass);
		return ZeT$Impl.Class.extend.
		  call(ZeT$Impl.Class, c1ass, methods);
	},


// +----: Function Tricks: --------------------------------------+

	/**
	 * Prototype JS: Function.bind().
	 */
	fbind            : function(f, context)
	{
		if((arguments.length < 3) && ZeT.isu(arguments[1]))
			return f;

		var args = Array.prototype.slice.call(arguments, 2);
		var func = function()
		{
			var a = ZeT$Impl.merge_args(args, arguments);
			return f.apply(context, a);
		}

		func.ZeT$fbind = true;
		return func;
	},

	fbinda           : function(f, context, args)
	{
		return function()
		{
			return f.apply(context, ZeT.a(args));
		}
	},

	/**
	 * Prototype JS: Function.wrap().
	 *
	 * Here 'f' argument is the function being wrapped.
	 * When the resulting function is called, 'wrapper'
	 * function is invoked with the first argument is
	 * the original 'f' function. (It may be called in
	 * the wrapper implementation to achieve wrapping.)
	 */
	fwrap            : function(f, wrapper)
	{
		return function()
		{
			var a = ZeT$Impl.update_args([ZeT.fbind(f, this)], arguments);
			return wrapper.apply(this, a);
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
	 * The result of the last call is returned as is.
	 */
	pipe             : function(/* functions */)
	{
		var fn = ZeT$Impl.selects_funcs(arguments);

		if(!fn.length) throw 'ZeT.pipe: ' +
		  'pipe functions are not defined';

		if(fn.length == 1) return fn[0];

		return function()
		{
			var r = ZeT.a(arguments);

			for(var i = 0;(i < fn.length);i++)
			{
				if(!ZeT.isa(r)) r = [r];
				r = fn[i].apply(this, r);
				if(ZeT.i$x(r)) return r;
			}
			return r;
		}
	},

	/**
	 * Shorthand for setTimeout() function that
	 * takes the function given and optionally binds
	 * it with the context and the arguments array given.
	 */
	timeout          : function(tm, fn, self, args)
	{
		if(!ZeT.isn(tm) || (tm < 0))
			throw 'ZeT.timeout([tm]): illegal timeout!';
		if(!ZeT.isf(fn))
			throw 'ZeT.timeout([fn]): not a function!';

		if(self) fn = ZeT.fbinda(fn, self, args);
		setTimeout(fn, tm)
		return this;
	},

	timeouted        : function()
	{
		var args = arguments;
		return function()
		{
			ZeT.timeout.apply(ZeT, args)
		}
	},


// +----: Test Functions: ---------------------------------------+

	iss              : function(s)
	{
		return (typeof s === 'string');
	},

	isf              : function(f)
	{
		if(ZeT$Impl.is_node_list_func())
			return (Object.prototype.toString.call(f) === '[object Function]');

		return (typeof f === 'function');
	},

	isb              : function(b)
	{
		return (typeof b === 'boolean');
	},

	isu              : function(o)
	{
		return (typeof o === 'undefined');
	},

	isa              : ('isArray' in Array)?
	  (Array.isArray):function(a)
	{
		return (Object.prototype.toString.call(a) === '[object Array]');
	},

	isn              : function(n)
	{
		return Object.prototype.toString.call(n) == '[object Number]';
	},

	isi              : function(i)
	{
		return (i === +i) && (i === (i|0));
	},


// +----: Helper Functions: -------------------------------------+

	a                : function(a)
	{
		if(ZeT.isu(a) || (a === null))
			return [];

		if(ZeT.isa(a)) return a;
		if(ZeT.iss(a)) return [a];

		if(ZeT.isf(a.toArray))
			return a.toArray();

		if(ZeT.isu(a.length))
			return [a];

		var l = a.length || 0, r = new Array(l);
		for(var i = 0;(i < l);i++)
			r[i] = a[i];
		return r;
	},

	keys             : function(o)
	{
		if(ZeT.isf(Object.keys))
			return Object.keys(o);

		var q, r = [];

		q = ZeT.isf(o.hasOwnProperty) && o.hasOwnProperty;
		q = q || (ZeT.isf(Object.prototype.hasOwnProperty) &&
		   Object.prototype.hasOwnProperty);
		q = q || function() {return true;};

		for(var p in o) if(q.call(o, p))
			r.push(p);

		return r;
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
		if(test) return test;

		var m = ZeTS.cati(1, arguments);
		if(ZeTS.ises(m)) m = 'Assertion failed!';

		throw m;
	},

	/**
	 * Evaluates the script given in the function body.
	 */
	xeval            : function (script)
	{
		if(ZeTS.ises(script)) return;
		eval('((function(){'.concat(script, '})())'))
	},


// +----: Debug Logging: ----------------------------------------+

	log              : function (/* strings */)
	{
		var msg = String.prototype.concat.apply('', arguments);
		if(ZeTS.ises(msg)) return;

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
		var o = arguments[0];
		var r = ZeT.isu(o) || (o === null);

		//?: {not need to check further}
		if(r || (arguments.length == 1)) return !!r;

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.isu(o) || (o === null)) return true;
		}

		return false;
	},

	/**
	 * Returns true when the flag is undefined or
	 * is not set (===) to false.
	 */
	i$xtrue           : function(/* object, properties list */)
	{
		var o = arguments[0];
		var u = ZeT.i$x(o);

		//?: {not need to check further}
		if(u || (arguments.length == 1))
			return u || !(o === false);

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.i$x(o)) return true;
		}

		return (o !== false);
	},

	/**
	 * Returns true when the flag is undefined or
	 * is not set (===) to true.
	 */
	i$xfalse          : function(/* object, properties list */)
	{
		var o = arguments[0];
		var u = ZeT.i$x(o);

		//?: {not need to check further}
		if(u || (arguments.length == 1))
			return u || !(o === true);

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.i$x(o)) return true;
		}

		return (o !== true);
	},

	/**
	 * Checks the value, or a property value is a function.
	 * If the final property is not accessible, false
	 * value is returned.
	 */
	i$f               : function(/* object, properties list */)
	{
		var o = arguments[0];

		if(arguments.length == 1)
			return ZeT.isf(o);

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.i$x(o)) return false;
		}

		return ZeT.isf(o);
	},

	/**
	 * Checks the value, or a property value is an array.
	 * If the final property is not accessible, false
	 * value is returned.
	 */
	i$a               : function(/* object, properties list */)
	{
		var o = arguments[0];

		if(arguments.length == 1)
			return ZeT.isa(o);

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.i$x(o)) return false;
		}

		return ZeT.isa(o);
	}
}


var ZeT$Impl = ZeT.define('ZeT$Impl', {

	/**
	 * Prototype JS library: object Class.
	 */
	Class             : (function()
	{
		function Subclass() {}

		function create()
		{
			var parent = null, props = ZeT.a(arguments);

			if(ZeT.isf(props[0]))
				parent = props.shift();

			function Class()
			{
				if(ZeT.isf(this.init))
					this.init.apply(this, arguments)
			}

			Class.superclass = parent;
			Class.subclasses = [];

			if(parent)
			{
				Subclass.prototype = parent.prototype;
				Class.prototype    = new Subclass();

				if(!parent.subclasses)
					parent.subclasses = [];
				parent.subclasses.push(Class)
			}

			for(var i = 0, length = props.length;(i < length);i++)
				ZeT$Impl.Class.extend(Class, props[i])

			Class.prototype.constructor = Class;
			return Class;
		}

		//HINT: this flag indicates that 'toString' method
		// is not listed in the properties (keys) list
		var IS_DONTENUM_BUGGY = (function()
		{
			for(var p in { toString: 1 })
			{
				if(p === 'toString') return false;
			}

			return true;
		})()

		function extend(c1ass, source)
		{
			//HINT: this appeals to the outer Class
			var parent = c1ass.superclass && c1ass.superclass.prototype;
			var props  = ZeT.keys(source);

			if(IS_DONTENUM_BUGGY)
			{
				//HINT: this checks whether the method is overwritten.
				//  If so, manually adds the method name to the keys.
				if(source.toString != Object.prototype.toString)
					props.push("toString");

				if(source.valueOf != Object.prototype.valueOf)
					props.push("valueOf");
			}

			for(var i = 0, length = props.length;(i < length);i++)
			{
				var p = props[i], v = source[p];

				if(!parent || !ZeT.isf(v))
				{
					c1ass.prototype[p] = v;
					continue;
				}

				var a = ZeT$Impl.argument_names(v);

				if(a[0] === '$super')
				{
					var method = v;

					//?: {parent has no such a method}
					if(!ZeT.isf(parent[p]))
						throw 'Can not extend class with $super argument as ' +
						  'the parent class does not define method [' + p + ']';

					v = ZeT.fwrap((function(m)
					{
						return function()
						{
							return parent[m].apply(this, arguments);
						}

					})(p), method);

					v.valueOf  = ZeT.fbind(method.valueOf,  method);
					v.toString = ZeT.fbind(method.toString, method);
				}

				c1ass.prototype[p] = v;
			}

			return c1ass;
		}

		return {
			create  : create,
			extend  : extend
		};
	})(),

	is_node_list_func : function()
	{
		var isnlf = ZeT$Impl._is_node_list_func_;

		if(ZeT.isb(isnlf))     return isnlf;
		if(ZeT.isu(document))  return null;

		ZeT$Impl._is_node_list_func_ = isnlf =
		  (typeof document.getElementsByTagName('body') === 'function');

		return isnlf;
	},

	/**
	 * Prototype JS library:
	 *   Function.argumentNames().
	 */
	argument_names    : function(f)
	{
		var names = f.toString().
		  match(/^[\s\(]*function[^(]*\(([^)]*)\)/)[1].
		  replace(/\/\/.*?[\r\n]|\/\*(?:.|[\r\n])*?\*\//g, '').
		  replace(/\s+/g, '').split(',');

		return ((names.length == 1) && !names[0])?([]):(names);
	},

	update_args       : function(array, args, start)
	{
		var length = array.length;

		if(!start) start = 0;
		for(var i = 0, l = args.length - start;(i < l);i++)
			array[length + i] = args[start + i];
		return array;
	},

	merge_args        : function(array, args)
	{
		array = Array.prototype.slice.call(array, 0);
		return ZeT$Impl.update_args(array, args);
	},

	selects_funcs     : function(args)
	{
		var res = [];

		for(var i = 0;(i < args.length);i++)
			if(ZeT.isf(args[i])) res.push(args[i])
		return res;
	}
})


var ZeTS = ZeT.define('ZeTS',
{
	/**
	 * Returns false for string objects that are not
	 * whitespace-trimmed empty.
	 */
	ises             : function(s)
	{
		return !ZeT.iss(s) || (s.length == 0) || !/\S/.test(s);
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
		var o = arguments[0];

		if(arguments.length == 1)
			return ZeT.iss(o) && !!o.length;

		var a = ZeT.a(arguments); a.shift()

		while(a.length)
		{
			o = o[a.shift()]; //<-- access the next property
			if(ZeT.i$x(o)) return false;
		}

		return ZeT.iss(o) && !!o.length;
	},

	trim             : function(s)
	{
		return !(s && s.length)?(''):
		  s.replace(/^\s+|\s+$/g, '');
	},

	first            : function(s)
	{
		return s.length && s.charAt(0);
	},

	replace          : function(s, a, b)
	{
		return s.split(a).join(b);
	},

	cat              : function( /* various objects */)
	{
		for(var i = 0;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i])) arguments[i] = '';

		return String.prototype.concat.apply('', arguments);
	},

	cati             : function(index, objs)
	{
		if(!objs || !ZeT.isn(objs.length)) return '';

		for(var i = 0;(i < objs.length);i++)
			if((i < index) || ZeT.isu(objs[i])) objs[i] = '';

		return String.prototype.concat.apply('', objs);
	},

	catif            : function(x /*, various objects */)
	{
		if(!x || ZeT.iss(x) && !x.length) return '';
		arguments[0] = '';

		for(var i = 1;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i])) arguments[i] = '';

		return String.prototype.concat.apply('', arguments);
	}
})

/**
 * For some browsers that do not have
 * String.endsWith()...
 */
if(!ZeT.isf(String.prototype.endsWith))
  String.prototype.endsWith = function(s)
{
	if(!s || !s.length) return false;

	var i = this.lastIndexOf(s);
	return (i == this.length - s.length);
}


var ZeTA = ZeT.define('ZeTA',
{
	copy             : function(a)
	{
		return ZeT.isa(a)?(a.slice()):ZeT.a(a);
	},

	remove           : function()
	{
		var a, s, o, j;

		if(ZeT.isa(this)) {a = this; s = 0;}
		else {a = arguments[0]; s = 1;}

		for(var i = s;(i < arguments.length);i++)
			if(ZeT.isa(o = arguments[i]))
				ZeTA.remove.apply(a, o)
			else if((j = a.indexOf(o)) != -1)
				a.splice(j, 1)

		return a;
	},

	merge            : function(a, b)
	{
		var r = ZeTA.copy(a); b = ZeT.a(b);

		for(var i = 0;(i < b.length);i++)
			r.push(b[i])
		return r;
	}
})

/**
 * For some browsers that do not have
 * Array.indexOf()...
 */
if(!ZeT.isf(Array.prototype.indexOf))
  Array.prototype.indexOf = function(o)
{
	for(var l = this.length, i = 0;(l);i++, l--)
		if(this[i] == 0) return i;
	return -1;
}


var ZeTX = ZeT.define('ZeT XML Support',
{
	nodes            : function(xml, name)
	{
		if(!xml) return xml;
		if(!ZeT.isf(xml.getElementsByTagName)) return undefined;
		return xml.getElementsByTagName(name) || [];
	},

	node             : function(xml, name)
	{
		if(!xml) return xml;
		var res = ZeTX.nodes(xml, name);
		return (res && res.length)?(res[0]):(null);
	},

	attr             : function(node, attr)
	{
		return node && ZeT.isf(node.getAttribute) &&
		  node.getAttribute(attr);
	},

	/**
	 * Returns the text values of the node immediate
	 * children with text and CDATA types.
	 */
	text             : function(node)
	{
		if(!node) return node;

		//?: {text || cdata}
		if((node.nodeType === 3) || (node.nodeType === 4))
			return node.nodeValue;

		var val, res = [];

		if(node.nodeType !== 1) return undefined;
		node = node.firstChild;

		while(node)
		{
			//?: {text || cdata}
			if((node.nodeType === 3) || (node.nodeType === 4))
				val = node.nodeValue;
			if(ZeT.iss(val)) res.push(val)

			node = node.nextSibling;
		}

		return String.prototype.concat.apply('', res);
	}
})