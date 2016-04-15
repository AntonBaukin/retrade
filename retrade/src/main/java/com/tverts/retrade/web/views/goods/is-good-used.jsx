var ZeT = JsX.once('zet/app.js')

function get()
{
	response.setContentType('text/plain;charset=UTF-8')

	//~: decode the entity
	ZeT.asserts(params.entity)
	var pk = parseInt(params.entity)
	ZeT.assert(ZeT.isi(pk))

	//~: check the result
	print(ZeT.bean('GetGoods').isGoodUsed(pk, false))
}