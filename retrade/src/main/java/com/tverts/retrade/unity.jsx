var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')
var getUnity = ZeT.bean('GetUnity')

var INFOS =
{
	'ReTrade: Invoice: Buy' : {
		page: '/invoices/info-buy'
	},

	'ReTrade: Invoice: Sell' : {
		page: '/invoices/info-sell',
		box: { widthpt: 620, heightpt: 420 }
	},

	'ReTrade: Invoice: Move' : {
		page: '/invoices/info-move'
	},

	'ReTrade: Invoice: Volume Check' : {
		page: '/invoices/info-volume-check'
	},

	'ReTrade: Sells: Invoice' : {
		page: '/sells/invoice-info'
	},

	'ReTrade: Sells: Sells Session' : {
		page: '/sells/session-info'
	}
}

function get()
{
	//~: load the unity
	var u  = loadInstance()
	if(!u) return
	var ut = ZeT.assertn(u.getUnity().getUnityType())

	//~: response JSON
	var x = { primaryKey: u.getPrimaryKey() }

	//~: type class and name
	x.typeClass = ut.getTypeClass().toString()
	x.typeName  = ut.getTypeName()

	//~: select the info page
	x.info = INFOS[x.typeName]

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
