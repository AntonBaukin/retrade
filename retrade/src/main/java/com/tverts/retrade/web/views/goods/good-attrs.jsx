var ZeT = JsX.once('zet/app.js')

function post()
{
	response.setContentType('text/plain;charset=UTF-8')

	//~: decode model parameter
	var model = ZeT.asserts(params.model)
	ZeT.assert(ZeT.iso(model = ZeT.s2o(model)))

	//?: {has no name}
	ZeT.asserts(model.name)

	//?: {has no type}
	ZeT.asserts(model.type)

	print(model.name)
}