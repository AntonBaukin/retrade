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

	//?: {not JsX}
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

function testBasicsObject()
{
	var ZeT = JsX.include('zet/basics.js')

	//?: {not JsX}
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
	var ZeT = JsX.include('zet/basics.js')

	// ...
}