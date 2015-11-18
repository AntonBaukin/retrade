/*===============================================================+
 | 0-ZeT Library for Nashorn-JsX                        [ 1.0 ]  |
 |                                                               |
 |                   Java Application Bindings                   |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT  = JsX.once('./basics.js')
var ZeTS = JsX.once('./strings.js')


// +----: print() : --------------------------------------------->

var _original_print_
if(ZeT.isu(_original_print_))
	_original_print_ = print

/**
 * Overwrites Nashorn print() with Console implementation.
 */
var print = function(/* various objects */)
{
	var s = ZeTS.cat.apply(ZeTS, arguments)
	if(s.length) _original_print_(s)
}


// +----: ZeT Extensions : -------------------------------------->

ZeT.extend(ZeT,
{
	/**
	 * String utilities.
	 */
	SU               : Java.type('com.tverts.support.SU'),

	/**
	 * Java Date utilities.
	 */
	DU               : Java.type('com.tverts.support.DU'),

	/**
	 * Logging utilities.
	 */
	LU               : Java.type('com.tverts.support.LU'),

	/**
	 * Exceptions and assertions.
	 */
	EX               : Java.type('com.tverts.support.EX'),

	/**
	 * Input-output.
	 */
	IO               : Java.type('com.tverts.support.IO'),

	sec              : Java.type('com.tverts.secure.SecPoint'),

	tx               : Java.type('com.tverts.system.tx.TxPoint'),

	/**
	 * Returns bean registered in Spring
	 */
	bean             : function(name)
	{
		ZeT.asserts(name)

		//?: {starts with capital}
		var x = ZeTS.first(name)
		if(x.toUpperCase() == x)
			name = x.toLowerCase() + name.substring(1)

		return ZeT.SpringPoint.bean(name)
	},

	SpringPoint      : Java.type('com.tverts.spring.SpringPoint'),

	jss              : function(s)
	{
		return ZeT.SU.jss(s)
	},

	html             : function(s)
	{
		return ZeT.SU.escapeXML(s)
	},

	/**
	 * Converts given object to JSON formatted string.
	 */
	o2s              : function(o)
	{
		return JSON.stringify(o)
	},

	s2o              : function(s)
	{
		ZeT.asserts(s)
		return JSON.parse(s)
	},

	/**
	 * Creates Java array of the given type.
	 */
	jarray           : function(type, length)
	{
		ZeT.assert(ZeT.isi(length) && (length >= 0))
		if(ZeT.iss(type)) type = Java.type(type)
		ZeT.assertn(type.class)
		return java.lang.reflect.Array.newInstance(type.class, length)
	}
}) //<-- return this value
