var ZeT   = JsX.once('zet/app.js')
var ZeTS  = JsX.once('zet/strings.js')

function post()
{
	var GoodsFilter = Java.type('com.tverts.retrade.domain.goods.GoodsFilter')
	var SelItem     = Java.type('com.tverts.retrade.domain.selset.SelItem')
	var get         = ZeT.bean('GetSelSet')

	//~: decode filter model
	var filter = ZeT.asserts(params.filter)
	ZeT.assert(ZeT.iso(filter = ZeT.s2jo(GoodsFilter, filter)))

	//?: {filter is null}
	if(filter.isNull())
		return response.setStatus(400)

	//~: get the selection set name
	var selset = ZeT.assertn(params.selset)

	//~: load it
	selset = ZeT.assertn(get.getSelSet(selset))

	//<: create filter item

	var item = new SelItem()

	//=: primary key
	ZeT.HiberPoint.setPrimaryKey(ZeT.tx.txSession(), item)

	//=: selection set
	item.setSelSet(selset)

	//=: ox-class
	item.setOxClass('GoodsFilter')

	//=: ox-filter
	item.setOx(filter)

	//!: persist it
	ZeT.tx.txSession().save(item)

	//>: create filter item

	//~: write the response
	response.setContentType('text/plain;charset=UTF-8')
	print(item.getPrimaryKey())
}