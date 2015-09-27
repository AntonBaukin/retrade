/*===============================================================+
 | 0-ZeT Library for Nashorn-JsX                        [ 1.0 ]  |
 |                                                               |
 |                        Various Checks                         |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT = JsX.global('ZeT')

ZeT.iss = function(s)
{
	return (typeof s === 'string')
}

ZeT.isf = function(f)
{
	return (typeof f === 'function')
}

ZeT.isb = function(b)
{
	return (typeof b === 'boolean')
}

ZeT.isu = function(o)
{
	return (typeof o === 'undefined')
}

ZeT.isa = Array.isArray

ZeT.isn = function(n)
{
	return Object.prototype.toString.call(n) == '[object Number]'
}

ZeT.isi = function(i)
{
	return (i === +i) && (i === (i|0))
}

ZeT.iso = function(o)
{
	return (o !== null) && (typeof o === 'object')
}

ZeT.isb = function(b)
{
	return (typeof b === 'boolean')
}

ZeT //<-- return this value