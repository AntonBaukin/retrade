var ZeT = JsX.once('zet/app.js')

/**
 * For the given Good Unit that may not be a sub-good
 * returns JSON-encoded array of measures info for
 * that good and all it's sub-goods.
 */
function encodeGoodMeasures(goodUnit)
{
	//?: {not a sub-good is given}
	ZeT.assert(goodUnit && (goodUnit.getSuperGood() == null))

	//~: select all the goods
	var result = [], subs = ZeT.bean('GetGoods').
	  getSubGoods(goodUnit.getPrimaryKey())

	function encode(gu)
	{
		//~: take ox-good, ox-measure
		var x = { objectKey: gu.getPrimaryKey() }
		x.good    = ZeT.jo2o(gu.getOxOwn())
		x.measure = ZeT.jo2o(gu.getMeasure().getOx())

		//?: {is a sub-good}
		if(gu.getSuperGood())
			x.calc = ZeT.jo2o(gu.getGoodCalc().getOx())

		result.push(x)
	}

	encode(goodUnit)
	ZeT.each(subs, encode)

	return ZeT.o2s(result)
}