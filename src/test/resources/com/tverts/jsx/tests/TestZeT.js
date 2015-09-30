/*===============================================================+
 | 0-ZeT Library for Nashorn-JsX                        [ 1.0 ]  |
 |                                                               |
 |                          Test Cases                           |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

function helloWorld()
{
	print('Hello, World!')
}

function testChecks()
{
	var ZeT = JsX.include('zet/asserts.js')

	//?: {not ZeT}
	if(ZeT !== JsX.global('ZeT'))
		throw new Error('asserts.js returned not ZeT!')

	//--> is strings

	ZeT.assert(ZeT.iss(''))
	ZeT.assert(ZeT.iss('abc'))
	ZeT.assert(ZeT.iss('1.0'))
	ZeT.assert(!ZeT.iss(true))
	ZeT.assert(!ZeT.iss({}))
	ZeT.assert(!ZeT.iss(1.0))
	ZeT.assert(!ZeT.iss(null))
	ZeT.assert(!ZeT.iss(undefined))

	//--> is functions

	ZeT.assert(ZeT.isf(helloWorld))
	ZeT.assert(!ZeT.isf(true))
	ZeT.assert(!ZeT.isf(''))
	ZeT.assert(!ZeT.isf({}))
	ZeT.assert(!ZeT.isf(1.0))
	ZeT.assert(!ZeT.isf(null))
	ZeT.assert(!ZeT.isf(undefined))

	//--> is boolean

	ZeT.assert(ZeT.isb(true))
	ZeT.assert(ZeT.isb(false))
	ZeT.assert(ZeT.isb(true && false))
	ZeT.assert(!ZeT.isb(helloWorld))
	ZeT.assert(!ZeT.isb(''))
	ZeT.assert(!ZeT.isb({}))
	ZeT.assert(!ZeT.isb(1.0))
	ZeT.assert(!ZeT.isb(null))
	ZeT.assert(!ZeT.isb(undefined))

	//--> is undefined

	ZeT.assert(ZeT.isu(undefined))
	ZeT.assert(!ZeT.isu(null))
	ZeT.assert(!ZeT.isu(true))
	ZeT.assert(!ZeT.isu(helloWorld))
	ZeT.assert(!ZeT.isu(''))
	ZeT.assert(!ZeT.isu({}))
	ZeT.assert(!ZeT.isu(1.0))

	//--> is array

	ZeT.assert(ZeT.isa([]))
	ZeT.assert(ZeT.isa([1, 2, 3]))
	ZeT.assert(ZeT.isa(new Array(10)))
	ZeT.assert(!ZeT.isa(null))
	ZeT.assert(!ZeT.isa(undefined))
	ZeT.assert(!ZeT.isa(true))
	ZeT.assert(!ZeT.isa(helloWorld))
	ZeT.assert(!ZeT.isa(''))
	ZeT.assert(!ZeT.isa({}))
	ZeT.assert(!ZeT.isa(1.0))

	//--> is numbers

	ZeT.assert(ZeT.isn(0))
	ZeT.assert(ZeT.isi(0))
	ZeT.assert(ZeT.isn(1.1))
	ZeT.assert(!ZeT.isn({}))
	ZeT.assert(!ZeT.isn('1'))
	ZeT.assert(ZeT.isi(101))
	ZeT.assert(!ZeT.isi(10.1))
	ZeT.assert(!ZeT.isi('10'))
	ZeT.assert(!ZeT.isi({}))

	//--> is object

	ZeT.assert(ZeT.iso({}))
	ZeT.assert(ZeT.iso({a: 'a', b: true, c: {}}))
	ZeT.assert(ZeT.iso(new Object()))
	ZeT.assert(!ZeT.iso(null))
	ZeT.assert(!ZeT.iso(undefined))
	ZeT.assert(!ZeT.iso(true))
	ZeT.assert(!ZeT.iso(helloWorld))
	ZeT.assert(!ZeT.iso('abc'))
	ZeT.assert(!ZeT.iso([]))
	ZeT.assert(!ZeT.iso(1.0))
	ZeT.assert(!ZeT.iso(0))
	ZeT.assert(!ZeT.iso(10))
}

function testAsserts()
{
	var ZeT = JsX.include('zet/asserts.js')

	function test(a, args)
	{
		if(!ZeT.isa(args))
			args = [ args ]

		try
		{
			a.apply(this, args)
		}
		catch(e)
		{
			return
		}

		throw new Error('Assertion ' + a +
		  ' didn\'t throw error on [' + args + ']!')
	}

	//--> assert

	test(ZeT.assert, false)
	test(ZeT.assert, null)
	test(ZeT.assert, undefined)
	test(ZeT.assert, 0)
	test(ZeT.assert, '')
	ZeT.assert(true)
	ZeT.assert(1)
	ZeT.assert(helloWorld)

	//--> assert not null

	test(ZeT.assertn, null)
	test(ZeT.assertn, undefined)
	ZeT.assertn(true)
	ZeT.assertn(0)
	ZeT.assertn('')
	ZeT.assertn(helloWorld)

	//--> assert not empty array

	test(ZeT.asserta, null)
	test(ZeT.asserta, undefined)
	test(ZeT.asserta, [])
	test(ZeT.asserta, new Array(4))
	test(ZeT.asserta, {0: 'a', 1: 1.0})
	ZeT.asserta([null])
	ZeT.asserta([0, 1])

	//--> assert not whitespace-empty string

	test(ZeT.asserts, null)
	test(ZeT.asserts, undefined)
	test(ZeT.asserts, '')
	test(ZeT.asserts, '  ')
	test(ZeT.asserts, ' \t  \r\n ')
	ZeT.asserts('abc')
	ZeT.asserts('ab c ')
	ZeT.asserts('ab c \n')
}

function testArrays()
{
	var ZeT  = JsX.include('zet/asserts.js')
	var ZeTA = JsX.include('zet/arrays.js')

	//?: {not ZeT arrays}
	if(ZeTA !== JsX.global('ZeTA'))
		throw new Error('arrays.js returned not ZeTA!')

	//--> equals

	ZeT.assert(ZeTA.eq([], []))
	ZeT.assert(ZeTA.eq([1, 2, 3], [1, 2, 3]))
	ZeT.assert(!ZeTA.eq([2, 1, 3], [1, 2, 3]))
	ZeT.assert(ZeTA.eq('abc', "abc"))
	ZeT.assert(!ZeTA.eq('abc', "aBc"))
	ZeT.assert(ZeTA.eq('abc', ['a', 'b', 'c']))

	//--> array-like

	var A = { length: 3, 0: 'a', 1: 'b', 2: 'c' }
	ZeT.assert(ZeT.isa(ZeTA.a(A)))
	ZeT.assert(ZeTA.eq(ZeTA.a(A), ['a', 'b', 'c']))

	//--> copy

	var B = [0, 1, 2, 3, 4, 5]
	ZeT.assert(ZeTA.copy(B) !== B)
	ZeT.assert(ZeTA.eq(ZeTA.copy(B), B))
	ZeT.assert(ZeTA.eq(ZeTA.copy(B, 3), [3, 4, 5]))
	ZeT.assert(ZeTA.eq(ZeTA.copy(B, 2, 6), [2, 3, 4, 5]))
	ZeT.assert(ZeTA.eq(ZeTA.copy(B, 2, 4), [2, 3]))

	//--> delete

	ZeT.assert(ZeTA.eq(B, ZeTA.del(ZeTA.copy(B))))
	ZeT.assert(ZeTA.eq([1, 5], ZeTA.del(ZeTA.copy(B), 0, 2, 3, 4)))
	ZeT.assert(ZeTA.eq([1, 5], ZeTA.del(ZeTA.copy(B), [0, 2, 3, 4])))
	ZeT.assert(ZeTA.eq([1, 5], ZeTA.del(ZeTA.copy(B), [0, 2], [3, 4])))
	ZeT.assert(ZeTA.eq([0, 1, 5], ZeTA.del.call(ZeTA.copy(B), 2, 3, 4)))
	ZeT.assert(ZeTA.eq([0, 1, 5], ZeTA.del.call(ZeTA.copy(B), [2], [3, 4])))

	//--> merge

	ZeT.assert(ZeTA.eq([], ZeTA.concat([], [])))
	ZeT.assert(ZeTA.eq(B, ZeTA.concat([0, 1, 2], [3, 4, 5])))
	ZeT.assert(ZeTA.eq(B, ZeTA.concat([0, 1], B, 2)))
	ZeT.assert(ZeTA.eq([0, 1, 3, 4], ZeTA.concat([0, 1], B, 3, 5)))
}

function testBasicsObject()
{
	var ZeT = JsX.include('zet/basics.js')

	//?: {not ZeT}
	if(ZeT !== JsX.global('ZeT'))
		throw new Error('basics.js returned not ZeT!')

	function ks(obj)
	{
		return ZeT.keys(obj).join('')
	}

	//--> keys

	ZeT.assert('a2c' == ['a', 2, 'c'].join(''))
	ZeT.assert('abc' == ks({ a: 'a', b: 2, c: "c" }))

	//--> extend

	var A = { a: 'a', b: 'b' }
	ZeT.assert('ab' == ks(A))

	var B = ZeT.extend(A, { c: 'x', d: 'y' })
	ZeT.assert(A === B)
	ZeT.assert('abcd' == ks(A))
	ZeT.assert(A.a === 'a' && A.d === 'y')

	//--> deep clone

	function Xu(obj)
	{
		ZeT.extend(this, obj)
	}

	ZeT.extend(Xu.prototype, { x: 1, y: 2 })

	A = new Xu({ a: 'a', b: { c: 'c', d: 'd' }})
	ZeT.assert('ab' == ks(A))
	ZeT.assert('cd' == ks(A.b))
	ZeT.assert(Object.getPrototypeOf(A).x === 1)
	ZeT.assert(A.x === 1 && A.y === 2)

	B = ZeT.deepClone(A)
	ZeT.assert(A != B)
	ZeT.assert('ab' == ks(B))
	ZeT.assert(A.b != B.b)
	ZeT.assert('cd' == ks(B.b))
	ZeT.assert(B.b.c === 'c' && B.b.d === 'd')
	ZeT.assert(B.x === 1 && B.y === 2)

	//--> deep extend

	var C = ZeT.deepExtend(B, { x: 'x', b: { c: 1, e: { f: 'f', g: 'g' }}})

	ZeT.assert(C === B)
	ZeT.assert(B.x === 1)
	ZeT.assert('ab' == ks(B))
	ZeT.assert(B.b.c === 'c')
	ZeT.assert('cde' == ks(B.b))
	ZeT.assert('fg' == ks(B.b.e))
	ZeT.assert(B.b.e.f === 'f' && B.b.e.g === 'g')
}

function testBasicsFunction()
{
	var ZeT  = JsX.include('zet/basics.js')
	var ZeTA = JsX.global('ZeTA')

	function xyz()
	{
		var r = ZeT.isn(this)?(this):(0)

		for(var i = 0;(i < arguments.length);i++)
			if(ZeT.isn(arguments[i]))
				r += ((i%2 == 0)?(+1):(-1)) * arguments[i]

		return r
	}

	ZeT.assert(4 == xyz.call(1, 2, 3, 4))

	//--> function bind

	var f = ZeT.fbind(xyz, -3)
	ZeT.assert(0 == f.call(100, 2, 3, 4))

	f = ZeT.fbind(xyz, -3, 2, 3)
	ZeT.assert(0 == f.call(100, 4))

	//--> function bind array

	f = ZeT.fbinda(xyz, -3)
	ZeT.assert(0 == f.call(100, 2, 3, 4))

	f = ZeT.fbinda(xyz, -3, [2, 3])
	ZeT.assert(0 == f.call(100, 4))

	//--> function bind universal

	f = ZeT.fbindu(xyz, -3)
	ZeT.assert(0 == f.call(100, 2, 3, 4))

	f = ZeT.fbindu(xyz, -3, 0, 2, 2, 4)
	ZeT.assert(0 == f.call(100, 3))
	ZeT.assert(1 == f.call(100, 3, 10, 11))

	//--> functions pipe

	function sum()
	{
		ZeT.assert(ZeT.isn(this))
		var a = ZeT.a(arguments)
		for(var i = 0;(i < a.length);i++)
		{
			ZeT.assert(ZeT.isn(a[i]))
			a[i] += this
		}
		return a
	}

	ZeT.assert(ZeTA.eq([11, 12, 13], sum.call(10, 1, 2, 3)))

	function mul()
	{
		ZeT.assert(ZeT.isn(this))
		var a = ZeT.a(arguments)
		for(var i = 0;(i < a.length);i++)
		{
			ZeT.assert(ZeT.isn(a[i]))
			a[i] *= this
		}
		return a
	}

	ZeT.assert(ZeTA.eq([3, 6, 9], mul.call(3, 1, 2, 3)))

	function neg()
	{
		var a = ZeT.a(arguments)
		for(var i = 0;(i < a.length);i++)
		{
			ZeT.assert(ZeT.isn(a[i]))
			a[i] = -a[i]
		}
		return a
	}

	ZeT.assert(ZeTA.eq([-1, 2, -3], neg(1, -2, 3)))

	f = ZeT.pipe(sum)
	ZeT.assert(ZeTA.eq([11, 12, 13], f.call(10, 1, 2, 3)))

	f = ZeT.pipe(sum, mul, neg)
	ZeT.assert(ZeTA.eq([+2, 0, -2], f.call(2, -3, -2, -1)))
}

function testBasicsHelper()
{
	var ZeT  = JsX.include('zet/basics.js')
	var ZeTA = JsX.global('ZeTA')

	//--> each

	ZeT.each(['0', '1', '2', '3'], function(x, i)
	{
		//WARNING: here this somehow becomes an object!
		ZeT.assert(x == this)
		ZeT.assert(parseInt(x) === i)
	})

	//--> evaluate in function

	ZeT.assert('abc' === ZeT.xeval("return 'abc'"))
	ZeT.assert(3 === ZeT.xeval("return 1 + 2"))

	//--> collect property

	var A = [ { x: 'a' }, { x: 2 }, { x: 'b' } ]
	ZeT.assert(ZeTA.eq(['a', 2, 'b'], ZeT.collect(A, 'x')))

	//--> collect function

	ZeT.assert(ZeTA.eq(['a', 'b'], ZeT.collect(A, function(x, i)
	{
		ZeT.assert(x == this)
		ZeT.assert(ZeT.isi(i))

		if(!ZeT.iss(x.x)) return undefined
		return x.x
	})))
}