var ZeT   = JsX.once('zet/app.js')
var ZeTS  = JsX.once('zet/strings.js')
var GoodS = JsX.once('./Goods.js')
var GenGs = JsX.once('domain/goods/GenGoodAttrTypes')


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
	var type = ZeT.assertn(ZeT.bean('GetUnity').
	  getAttrType(model.objectKey))

	//sec: same domain
	ZeT.sec.checkSameDomain(type)

	//?: {attribute is system}
	if(type.isSystem())
		return updateSystemAttr(type, model)

	return updateAttr(type, model)
}

function updateSystemAttr(atype, model)
{
	var ga = ZeT.s2o(atype.getOx().getObject())

	//?: {must have a list of values}
	ZeT.assert(ZeT.isa(ga.values))

	//~: validate values
	var vs = validateListValues(ga.type, model)
	if(!ZeTS.ises(vs)) return vs

	//=: values
	ga.values = model.values
	atype.getOx().setObject(ZeT.o2s(ga))

	//~: local name (is sent as name)
	if(!ZeTS.ises(model.name))
	{
		ga.nameLo = model.name
		atype.setNameLo(ga.nameLo)
		atype.getOx().setNameLo(ga.nameLo)
	}

	//!: update ox-object
	atype.updateOx()
}

function insertAttr(model)
{
	var ActionsPoint = Java.type('com.tverts.actions.ActionsPoint')
	var ActionType   = Java.type('com.tverts.actions.ActionType')

	//?: {is system}, may not be
	ZeT.assert(!model.system)

	//?: {has values}
	if(ZeT.isa(model.values))
	{
		//~: validate them
		var vs = validateListValues(model.type, model)
		if(!ZeTS.ises(vs)) return vs
	}

	//~: create Attribute Type instance
	var type = GenGs.assignGoodAttr(model)

	//=: current domain
	type.setDomain(ZeT.sec.loadDomain())

	//!: insert the type
	ActionsPoint.actionRun(ActionType.ENSURE, type)
}

function validateListValues(type, model)
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
			v = GoodS.validateGoodAttrValue(type, v)
		}
		catch(e)
		{
			return ZeTS.cat('Неверно значение списка [', x.value, ']: ', e)
		}

		//~: convert back to string
		x.value = '' + v
	}
}

function updateAttr(type, model)
{
	var ActionsPoint = Java.type('com.tverts.actions.ActionsPoint')
	var ActionType   = Java.type('com.tverts.actions.ActionType')

	//?: {is system}, may not be
	ZeT.assert(!model.system)
	ZeT.assert(!type.isSystem())

	//?: {has values}
	if(ZeT.isa(model.values))
	{
		//~: validate them
		var vs = validateListValues(model.type, model)
		if(!ZeTS.ises(vs)) return vs
	}

	//~: assign Attribute Type instance
	GenGs.assignGoodAttr(model, type)

	//!: update the type
	ActionsPoint.actionRun(ActionType.UPDATE, type)
}