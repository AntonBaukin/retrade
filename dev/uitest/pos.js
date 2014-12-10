/*===============================================================+
 |                                                               |
 |  Restera™ POS Terminal UI Script                              |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT = window.ZeT = window.ZeT || {

// +----: Object Routines : -------------------------------------+

	keys             : function(o)
	{
		if((ZeT._Object_keys_ !== true) && (ZeT._Object_keys_ !== false))
			ZeT._Object_keys_ = ZeT.isf(Object.keys)

		if(ZeT._Object_keys_)
			return Object.keys(o)

		var q = ZeT._hasop_; if(!q)
		{
			q = ZeT.isf(o.hasOwnProperty) && o.hasOwnProperty
			q = q || (ZeT.isf(Object.prototype.hasOwnProperty) &&
			  Object.prototype.hasOwnProperty)
			ZeT._hasop_ = q = q || function() { return true }
		}

		var r = []; for(var p in o) if(q.call(o, p))
			r.push(p)

		return r
	},


// +----: String Routines : -------------------------------------+

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


// +----: Array Routines : --------------------------------------+

	each             : function(a, f)
	{
		if(a) for(var i = 0;(i < a.length);i++)
			if(f(a[i], i) === false)
				return a[i]
		return undefined
	},


// +----: Test Functions : --------------------------------------+

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

	isa              : ('isArray' in Array)?(Array.isArray):
	function(a)
	{
		return (Object.prototype.toString.call(a) === '[object Array]')
	},

	isn              : function(n)
	{
		return (Object.prototype.toString.call(n) == '[object Number]')
	},


// +----: Assertion & Debug Routines : --------------------------+

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

		var m = ZeT.cati(1, arguments)
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

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'A non-empty array is required!'

		throw m
	},

	/**
	 * Test that a string is given, and it is not
	 * a whitespace-only string.
	 */
	asserts          : function(string /* messages */)
	{
		if(!ZeT.ises(string))
			return string

		var m = ZeT.cati(1, arguments)
		if(ZeT.ises(m)) m = 'A not-whitespace-empty string is required!'

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

// +----: Public Routines : -------------------------------------+

	readGoodsData    : function()
	{
		var D = ZeT.asserta(window.GOODS, 'Нет файла с данными товаров!')

		//~: create the Tree
		var T = POS.GoodsTree = { roots: [] }

		//~: visuals
		var V = POS.GoodsVisuals = {}

		//~: error codes
		var E = []

		//c: for each good item
		for(var i = 0;(i < D.length);i++)
		{
			var d = D[i]

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

			//?: code is reused
			if(T[d.code])
			{
				ZeT.log('Позиция [', i + 1, '], код [', d.code, '], имя [',
				  d.name, '] дублирует код [', d.code, ']')
				E[3] = 'есть позиция дубликатом кода'
			}

			//~: map item by the code
			T[d.code] = d

			//?: {has no index}
			if(!ZeT.isn(d.index))
			{
				ZeT.log('Позиция без индекса [', i + 1, '], код [',
				  d.code, '], имя [', d.name, ']')
				E[4] = 'есть позиция без индкса порядка дочернего елемента'
			}

			//?: {has no folder-item flag}
			if((d.folder !== true) && (d.folder !== false))
			{
				ZeT.log('Позиция без folder [', i + 1, '], код [',
				  d.code, '], имя [', d.name, ']')
				E[5] = 'есть позиция без признака элемент-или-каталог'
			}

			//?: {is folder}
			if(d.folder)
			{
				//?: {has price}
				if(!ZeT.isu(d.price))
				{
					ZeT.log('Каталог имеет цену [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[6] = 'есть каталог с указанной ценой'
				}

				//?: {has measure}
				if(!ZeT.isu(d.measure))
				{
					ZeT.log('Каталог имеет ед. измерения [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[7] = 'есть каталог с ед. измерения'
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
					E[8] = 'есть товар без цены'
				}

				//?: {has no measure}
				if(ZeT.ises(d.measure))
				{
					ZeT.log('Товар без ед. измерения [', i + 1, '], код [',
					  d.code, '], имя [', d.name, ']')
					E[9] = 'есть товар без ед. измерения'
				}
			}

			//?: {has visual}
			if(!ZeT.ises(d.visual))
			{
				//?: {color code}
				if(d.visual.match(/[a-fA-F0-9]+/))
					d.visual = d.visual.toUpperCase()
				V[d.visual] = {}
			}
		}

		//?: {has error}
		var error = ZeT.join('; ', E)
		if(error) throw ZeT.cat('При проверке данных найдены ошибки: ', error, '!')

		//~: log visuals
		ZeT.log('Goods visuals: ', ZeT.join(', ', ZeT.keys(V)))
	},

	buildGoodsTree   : function()
	{
		//~: goods data & tree
		var D = ZeT.asserta(window.GOODS)
		var T = ZeT.assertn(POS.GoodsTree)
		var E = [] //<-- error codes

		//c: for each good item having parent
		for(var i = 0;(i < D.length);i++)
		{
			var d = D[i], p = T[d.parent]
			if(ZeT.isu(p)) continue

			//?: {parent not found}
			if(!p)
			{
				ZeT.log('Позиция [', i + 1, '], код [', d.code, '], имя [',
				  d.name, '] с ненайденным родителем [', d.parent, ']')
				E[0] = 'есть позиция с ненайденным родителем'
			}

			//?: {parent is not a folder}
			if(!p && !p.folder)
			{
				ZeT.log('Позиция [', i + 1, '], код [', d.code, '], имя [',
				  d.name, '] имеет родителем товар [', d.parent, ']')
				E[1] = 'есть позиция с родителем-товаром, а не папкой'
			}

			//~: refer as in the child
			if(!p.children) p.children = []
			p.children.push(d)

			//?: {this is a folder}
			if(d.folder)
			{
				if(!p.subfolders) p.subfolders = []
				p.subfolders.push(d)
			}
		}

		//?: {has error}
		var error = ZeT.join('; ', E)
		if(error) throw ZeT.cat('При проверке данных найдены ошибки: ', error, '!')

		//~: items index compare
		function cmp(l, r)
		{
			return (l.index < r.index)?(-1):(l.index == r.index)?(0):(+1)
		}

		//c: for each good item having children
		for(i = 0;(i < D.length);i++)
		{
			if(D[i].children)
				D[i].children.sort(cmp)
			if(D[i].subfolders)
				D[i].subfolders.sort(cmp)
		}

		//~: sort the roots
		ZeT.asserta(T.roots).sort(cmp)
	},

	/**
	 * Repaints the goods folders with optional
	 * selected folder specified (also, by it's
	 * code in the data file).
	 */
	drawGoodsFolders : function(selected)
	{
		var T = ZeT.assertn(POS.GoodsTree)
		ZeT.asserta(T.roots)

		//~: hide the content
		POS._gs_fds_hide()

		//?: {display selected | single folder}
		if(selected || (T.roots.length == 1))
			POS._gs_folder(selected || T.roots[0])
		//~: display root folders
		else
			ZeT.each(T.roots, function(f){ POS._gs_fd_draw(f) })

		//~: resize the folders area
		POS._gs_fds_resize()
	},


// +----: Goods Folders Routines : ------------------------------+

	_gs_folder       : function(f)
	{
		var T = ZeT.assertn(POS.GoodsTree)
		if(ZeT.iss(f)) f = ZeT.assertn( T[f],
		  'Folder [', f, '] is not found in the goods tree!')
		ZeT.assert(f.folder)

		//?: {has multiple roots} draw the root
		var M = POS.GoodsRootFolder
		if(M && (M.alwaysPresent || (T.roots.length > 1)))
			POS._gs_fd_draw(M)

		//~: collect the parent nodes
		var ps = [], x = f; while(x.parent)
		{
			x = ZeT.assertn(T[x.parent])
			ps.splice(0, 0, x)
		}

		//~: draw them
		ZeT.each(ps, function(x){
			x.tempClasses = 'path'
			POS._gs_fd_draw(x)
		})

		//~: draw target folder
		f.tempClasses = 'selected'

		//?: {folder has sub-folders}
		if(f.subfolders)
		{
			POS._gs_fd_draw(f)

			//~: draw them
			ZeT.each(f.subfolders, function(x){ POS._gs_fd_draw(x) })
		}
		//~: draw folder within the siblings
		else
			ZeT.each(ZeT.asserta(T[f.parent].subfolders),
			  function(x){ POS._gs_fd_draw(x) })
	},

	_gs_fds_hide     : function()
	{
		POS._gs_folder_xline = null
		$('#pos-main-area-folders .pos-folders-line').hide()
	},

	_gs_fds_resize   : function()
	{
		//~: find the last line displayed
		var C  = POS.GoodsFolders; if(!C) return
		var ls = $('#pos-main-area-folders .pos-folders-line:visible')
		if(!ls || !ls.length) return

		//~: choose the line
		var l = $((ZeT.isn(C.maxLines) && (ls.length > C.maxLines))?
		  (ls[C.maxLines]):(ls[ls.length - 1]))

		//~: target areas
		var F = $('#pos-main-area-folders-ext')
		var f = $('#pos-main-area-folders-ext div')
		var G = $('#pos-main-area-goods-ext')
		var g = $('#pos-main-area-goods-ext div')

		//~: resize delta
		var d = f.innerHeight() - l.position().top - l.outerHeight()

		F.innerHeight(F.innerHeight() - d)
		G.innerHeight(G.innerHeight() + d)
	},

	/**
	 * Draws the next goods folder item
	 * placed in proper line position.
	 */
	_gs_fd_draw      : function(f)
	{
		//~: access current line
		var line = POS._gs_folder_xline; if(!line) line =
		  POS._gs_folder_xline = POS._gs_fd_line()

		//~: add item to this line
		var item = POS._gs_fd_line_item(line)

		//?: {line has no more space} add a new line
		if(!item) {
			line = POS._gs_folder_xline = POS._gs_fd_line()
			item = ZeT.assertn(POS._gs_fd_line_item(line))
		}

		//~: initialize the item
		POS._gs_fd_item(item, f)
	},

	/**
	 * Reuses or appends new goods folder line.
	 */
	_gs_fd_line      : function()
	{
		//~: search for the first hidden line
		var line = $('#pos-main-area-folders .pos-folders-line:hidden')

		//?: {found it} hide items
		if(line.length)
		{
			line = $(line[0])
			line.data('i', 0)
			line.find('.pos-folders-item').hide()
			line.find('.pos-folders-item-sep').hide().removeClass('sep-justify')
			return line.show()
		}

		//~: all the lines
		var L = $('#pos-main-area-folders .pos-folders-line')

		//~: create new line
		line = $("<tr></tr>").addClass('pos-folders-line').
		  addClass((L.length%2 == 0)?('even'):('odd'))

		//~: the empty list of items
		line.data('i', 0)
		line.data('items', [])

		//~: append it
		$('#pos-main-area-folders').append(line)
		return line
	},

	/**
	 * Returns the next available goods folder
	 * item within the line. Inserts new items
	 * on the demand. If line has no more space,
	 * returns null.
	 */
	_gs_fd_line_item : function(line)
	{
		//~: take existing item
		var i = line.data('i'), a = line.data('items')
		if(i < a.length)
		{
			line.data('i', i+1)
			a[i].prev().show()
			return a[i].show()
		}

		//?: {has no more space}
		var m = POS._gs_folder_line_max
		if(m && i >= m)
		{
			line.find('.pos-folders-item-sep').addClass('sep-justify')
			return null
		}

		//~: create new item & add it
		var item = $('#pos-goods-folder-item-template').
		  clone().attr('id', null).
		  addClass((a.length%2 == 0)?('even'):('odd'))

		//~: append point
		line.append(item)

		//?: {line has no space}
		if(!POS._gs_folder_line_max)
		{
			var outer = $('#pos-main-area-folders-ext > div')
			if(outer.width() < item.position().left + item.outerWidth())
			{
				item.remove()
				POS._gs_folder_line_max = a.length
				return null
			}
		}

		//?: {not first item} add separator
		if(i) item.before($('<td><div></div></td>').addClass('pos-folders-item-sep'))

		//~: increment the position
		line.data('i', i+1)

		//~: append to the items
		a.push(item)

		//~: on click
		item.click(POS._gs_fd_click)

		return item.css('visibility', '')
	},

	/**
	 * Initializes goods folder item with
	 * the given model element.
	 */
	_gs_fd_item      : function(item, f)
	{
		item.data('model', f)

		//?: {has classes to remove}
		if(item.data('classes-to-remove'))
		{
			item.removeClass(item.data('classes-to-remove'))
			item.data('classes-to-remove', null)
		}

		//~: folder name in inner div
		item.find('div').text(f.name)

		//?: {folder has children}
		item.toggleClass('subfolders', !!f.subfolders)

		//?: {has special class}
		var c2r = ''; if(ZeT.iss(f.styleClasses))
		{
			c2r += f.styleClasses
			item.addClass(f.styleClasses)
		}

		//?: {has temp class}
		if(ZeT.iss(f.tempClasses))
		{
			c2r += ' ' + f.tempClasses
			item.addClass(f.tempClasses)
			f.tempClasses = null
		}

		if(!ZeT.ises(c2r))
			item.data('classes-to-remove', c2r)
	},

	_gs_fd_click     : function(e)
	{
		//~: access folder model
		var m = $(e.delegateTarget).data('model')
		if(!m) return

		//?: {root folder}
		if(POS.GoodsRootFolder == m)
		   POS.drawGoodsFolders()
		//~: draw regular folder
		else
			POS.drawGoodsFolders(ZeT.asserts(m.code))
	}


// +----: Goods Routines : --------------------------------------+

}