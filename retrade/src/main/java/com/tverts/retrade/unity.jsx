var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')
var getUnity = ZeT.bean('GetUnity')

var INFOS =
{
	'ReTrade: Invoice: Buy'          : '/invoices/info-buy',
	'ReTrade: Invoice: Sell'         : '/invoices/info-sell',
	'ReTrade: Invoice: Move'         : '/invoices/info-move',
	'ReTrade: Invoice: Volume Check' : '/invoices/info-volume-check',
	'ReTrade: Sells: Invoice'        : '/sells/invoice-info',
	'ReTrade: Sells: Sells Session'  : '/sells/session-info'
}

function get()
{
	//~: load the unity
	var u  = loadInstance()
	if(!u) return
	var ut = ZeT.assertn(u.getUnity().getUnityType())

	//~: response JSON
	var x = {}

	//~: type class and name
	x.typeClass = ut.getTypeClass().toString()
	x.typeName  = ut.getTypeName()

	//~: select the info page
	x.infoPage  = INFOS[x.typeName]

	//~: write the result JSON
	response.setContentType('application/json')
	print(ZeT.o2s(x))
}

function loadInstance()
{
	//~: take the unity id
	var id = ZeTS.trim(params.entity)
	if(!ZeTS.ises(id)) id = parseInt(id)
	if(!ZeT.isi(id))
	{
		response.sendError(400, 'Unity primary key is not defined!')
		return null
	}

	//~: load the object
	var u = getUnity.getUnited(id)
	if(!u)
	{
		response.sendError(404, ZeTS.cat(
		  'Unity with primary key [', id, '] is not found!'))
		return null
	}

	//sec: {not the same domain}
	if(!ZeT.sec.isSameDomain(u))
	{
		response.sendError(403, ZeTS.cat(
		  'Unity with primary key [', id,
		  '] is not in your database domain!'))
		return null
	}

	return u
}
