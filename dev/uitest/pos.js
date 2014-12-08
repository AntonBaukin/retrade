/*===============================================================+
 |                                                               |
 |  Restera™ POS Terminal UI Script                              |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT = window.ZeT = window.ZeT || {

// +----: String Routines: --------------------------------------+

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

	join             : function(s, a)
	{
		if(!ZeT.isa(a)) return null

		var x = []; for(var i = 0;(i < a.length);i++)
			if(!ZeT.ises(a[i])) x.push(a[i])

		return (a.length)?(x.join(s)):(null)
	},

	cat              : function( /* various objects */)
	{
		for(var i = 0;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i])) arguments[i] = ''

		return String.prototype.concat.apply('', arguments)
	},

	cati             : function(index, objs)
	{
		if(!objs || !ZeT.isn(objs.length)) return ''

		for(var i = 0;(i < objs.length);i++)
			if((i < index) || ZeT.isu(objs[i])) objs[i] = ''

		return String.prototype.concat.apply('', objs)
	},

	catif            : function(x /*, various objects */)
	{
		if(!x || ZeT.iss(x) && !x.length) return ''
		arguments[0] = ''

		for(var i = 1;(i < arguments.length);i++)
			if(ZeT.isu(arguments[i])) arguments[i] = ''

		return String.prototype.concat.apply('', arguments)
	},



// +----: Test Functions: ---------------------------------------+

	iss              : function(s)
	{
		return (typeof s === 'string')
	},

	/**
	 * Returns false for string objects that are not
	 * whitespace-trimmed empty.
	 */
	ises             : function(s)
	{
		return !ZeT.iss(s) || (s.length == 0) || !/\S/.test(s)
	},

	isf              : function(f)
	{
		return (typeof f === 'function')
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
		return (Object.prototype.toString.call(n) == '[object Number]')
	},


// +----: Assertion & Debug Routines: ---------------------------+

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

		var m = ZeT.cati(1, arguments);
		if(ZeT.ises(m)) m = 'Assertion failed!'

		throw m
	},

	/**
	 * Checks that given object is not
	 * null, or undefined.
	 */
	assertn          : function(obj /* messages */)
	{
		if((obj !== null) && !ZeT.isu(obj))
			return obj

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'The object is undefined or null!'

		throw m
	},

	/**
	 * Tests the the given object is a not-empty array.
	 */
	asserta          : function(array /* messages */)
	{
		if(ZeT.isa(array) && array.length)
			return array

		var m = ZeT.cati(1, arguments);
		if(ZeT.ises(m)) m = 'A non-empty array is required!'

		throw m
	},

	log              : function (/* objects */)
	{
		if(!ZeT._console_) ZeT._console_ = console && ZeT.isf(console.log)
		if(!ZeT._console_) return

		var msg = String.prototype.concat.apply('', arguments);
		if(!ZeT.ises(msg)) console.log(msg)
	}
};

var POS = window.POS = window.POS || {

	inspectData      : function()
	{
		var D = ZeT.asserta(window.GOODS, 'Нет файла с данными товаров!')

		//~: create the Tree
		var T = POS.GoodsTree = { roots: [] }

		//~: error codes
		var E = [];

		//c: inspect all the items
		for(var i = 0;(i < D.length);i++)
		{
			var d = D[i];

			//?: {has no code}
			if(ZeT.ises(d.code))
			{
				ZeT.log('Позиция без кода [', i + 1, '], имя [', d.name, ']')
				E[0] = 'есть позиция без кода'
			}

			//?: {has no name}
			if(ZeT.ises(d.name))
			{
				ZeT.log('Позиция без имени [', i + 1, '], код [', d.code, ']')
				E[1] = 'есть позиция без имени'
			}

			//?: {has no parent}
			if((d.parent == '0') || ZeT.ises(d.parent))
				d.parent = null

			if(!d.parent) if(!T.root) T.roots.push(d); else
			{
				ZeT.log('Позиция без имени [', i + 1, '], код [',
				  d.code, '], имя [', d.name, ']')
				E[2] = 'есть несколько корневых элементов'
			}

			//~: map item by the code
			T[d.code] = d

			//?: {has no index}
			if(!ZeT.isn(d.index))
			{
				ZeT.log('Позиция без индекса [', i + 1, '], код [',
				  d.code, '], имя [', d.name, ']')
				E[3] = 'есть позиция без индкса порядка дочернего елемента'
			}

			//?: {has no folder-item flag}
			if((d.folder !== true) && (d.folder !== false))
			{
				ZeT.log('Позиция без folder [', i + 1, '], код [',
				  d.code, '], имя [', d.name, ']')
				E[4] = 'есть позиция без признака элемент-или-каталог'
			}

			//?: {is folder}
			if(d.folder)
			{
				//?: {has price}
				if(!ZeT.isu(d.price))
				{
					ZeT.log('Каталог имеет цену [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[5] = 'есть каталог с указанной ценой'
				}

				//?: {has measure}
				if(!ZeT.isu(d.measure))
				{
					ZeT.log('Каталог имеет ед. измерения [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[6] = 'есть каталог с ед. измерения'
				}
			}
			//~: is item
			else
			{
				//?: {has no price}
				if(!ZeT.isn(d.price))
				{
					ZeT.log('Товар не имеет цены [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[7] = 'есть товар без цены'
				}

				//?: {has no measure}
				if(ZeT.ises(d.measure))
				{
					ZeT.log('Товар без ед. измерения [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[8] = 'есть товар без ед. измерения'
				}
			}
			//{folder: true, name: "БЕГЕМОТ", code: "002462", index: 1, visual: "ffff80"},
			//{folder: true, name: "АКЦИИ", code: "004205", parent: "002462", index: 5, visual: "ffff80"},
			//{folder: false, name: "Нож столовый", code: "003539", parent: "003506", index: 3, visual: "bba4de", measure: "шт", price: 10000},
		}

		//?: {has error}
		var error = ZeT.join('; ', E)
		if(error) throw ZeT.cat('При проверке данных найдены ошибки: ', error, '!');
	}

};