/*===============================================================+
 | 0-ZeT Library for Nashorn-JsX                        [ 1.0 ]  |
 |                                                               |
 |                   Java Application Bindings                   |
 |                                                               |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT = JsX.once('./console.js')


// +----: print(): ---------------------------------------------->

var ZeT_Console_out = ZeT.Console.out

/**
 * Overwrites Nashorn print() with Console implementation.
 */
//function print(/* various objects */)
//{
//	//ZeT_Console_out.println.apply(ZeT_Console_out, arguments)
//}