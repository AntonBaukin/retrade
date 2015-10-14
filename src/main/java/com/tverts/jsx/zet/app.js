/*===============================================================+
 | 0-ZeT Library for Nashorn-JsX                        [ 1.0 ]  |
 |                                                               |
 |                   Java Application Bindings                   |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT  = JsX.once('./basics.js')
var ZeTS = JsX.once('./strings.js')


// +----: print(): ---------------------------------------------->

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


ZeT //<-- return this value