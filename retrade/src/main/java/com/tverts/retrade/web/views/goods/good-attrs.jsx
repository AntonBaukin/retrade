var ZeT   = JsX.once('zet/app.js')
var ZeTS  = JsX.once('zet/strings.js')
var GoodS = JsX.once('./Goods.js')

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

	//~: process the model
	var error = processModel(model)

	//?: {has error}
	if(!ZeTS.ises(error))
	{
		response.setStatus(400)
		return print(error)
	}

	print(model.name)
}

function processModel(model)
{
	//?: {is creating}
	if(ZeT.isx(model.objectKey))
		return insertAttr(model)

	//~: object key
	if(ZeT.iss(model.objectKey))
		model.objectKey = parseInt(model.objectKey)
	ZeT.assert(ZeT.isi(model.objectKey))

	//~: load the attribute
	var atype = ZeT.assertn(ZeT.bean('GetUnity').
	  getAttrType(model.objectKey))

	//?: {attribute is system}
	if(atype.isSystem())
		return editSystemAttr(atype, model)

	return editAttr(atype, model)
}

function editSystemAttr(atype, model)
{
	var ga = ZeT.s2o(atype.getOx().getObject())

	//?: {must have a list of values}
	ZeT.assert(ZeT.isa(ga.values))

	//~: validate values
	var vs = validateListValues(ga, model)
	if(!ZeTS.ises(vs)) return vs

	//=: values
	ga.values = model.values
	atype.getOx().setObject(ZeT.o2s(ga))

	//!: update ox-object
	atype.updateOx()
}

function editAttr(atype, model)
{

}

function insertAttr(model)
{

}

function validateListValues(ga, model)
{
	ZeT.assert(ZeT.isa(model.values))
	for(var i = 0;(i < model.values.length);i++)
	{
		//~: item and it's value
		var x = model.values[i]
		ZeT.assert(ZeT.iso(x))
		var v = ZeT.asserts(x.value)

		//~: temporary convert it to proper type
		try
		{
			v = GoodS.validateGoodAttrValue(ga.type, v)
		}
		catch(e)
		{
			return ZeTS.cat('Неверно значение списка [', x.value, ']: ', e)
		}

		//~: convert back to string
		x.value = '' + v
	}
}