var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

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
		var x = {}

		//~: take ox-good, ox-measure
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

/**
 * Applies sub-goods (measures) edit request.
 */
function post()
{
	var model  = ZeT.s2o(ZeT.asserts(params.measures))
	var result = applyGoodMeasures(model)

	//?: {validation had failed}
	if(!ZeTS.ises(result))
	{
		response.setContentType('text/plain;charset=UTF-8')
		response.setStatus(400)
		return print(result)
	}

	//~: print back the measures
	response.setContentType('application/json;charset=UTF-8')
	print(encodeGoodMeasures(result))
}

function applyGoodMeasures(measures)
{
	var get = ZeT.bean('GetGoods')

	ZeT.assert(ZeT.isa(measures) && measures.length)

	//~: load the super good
	ZeT.assert(ZeT.isi(measures[0].good.pkey))
	var gu = ZeT.assertn(get.getGoodUnit(measures[0].good.pkey))

	//sec: good of the same domain
	ZeT.assert(ZeT.sec.isSameDomain(gu))

	//~: select sub-goods
	var subs = get.getSubGoods(gu.getPrimaryKey())
	var subm = {}; ZeT.each(subs, function(sg){ subm[sg.getPrimaryKey()] = sg })

	//~: validate overall relations
	var valid = checkGoodMeasures(gu, subm, measures)
	if(!ZeTS.ises(valid)) return valid

	//~: apply changes to the super ox-good
	applyGoodOx(gu, measures[0])
	gu.updateOx()

	//~: update existing sub-goods
	ZeT.each(measures, function(m, i)
	{
		if(i == 0) return //<-- that is super-good

		var sg = subm[m.good.pkey]
		if(!sg) return

		//~: apply changes to the sub-ox-good
		applyGoodOx(sg, m)
		sg.updateOxOwn()

		//?: {has measure the same}
		if(sg.getMeasure().getPrimaryKey() == m.good.measure)
			return

		//~: load the measure
		var mu = get.getMeasureUnit(m.good.measure)

		//sec: measure of the same domain
		ZeT.assert(ZeT.sec.isSameDomain(mu))

		//=: assign measure of the unit
		sg.setMeasure(mu)
	})

	//return 'Test message: ' + gu.getCode()
	return gu
}

function checkGoodMeasures(gu, subm, measures)
{
	var get = ZeT.bean('GetGoods')

	//?: {all goods are unchecked from the lists}
	var valid; ZeT.each(measures, function(m)
	{
		if(m.good['visible-lists'] === true)
			{ valid = true; return false }
	})

	if(!valid) return 'Хотя бы одна единица измерения ' +
	  'товара должна быть отображена в списках!'

	//~: find goods with updated measures
	ZeT.each(measures, function(m, i)
	{
		if(i == 0) return //<-- that is super-good

		var sg = subm[m.good.pkey]
		if(!sg) return

		//?: {has measure the same}
		if(sg.getMeasure().getPrimaryKey() == m.good.measure)
			return

		//~: detect sub-good usage
		var usage = get.isGoodUsed(sg.getPrimaryKey(), false)
		if(usage == 0) return

		valid = ZeTS.cat('Невозможно изменить единицу измерения "',
		  sg.getMeasure().getCode(), '", поскольку товар с ней ',
		  goodUsageDescr(usage), '!')

		return false
	})

	if(ZeT.iss(valid)) return valid
}

function applyGoodOx(gu, m)
{
	var g = gu.getOxOwn()

	//=: visibility flags
	g.setVisibleBuy(m.good['visible-buy'])
	g.setVisibleSell(m.good['visible-sell'])
	g.setVisibleLists(m.good['visible-lists'])
	g.setVisibleReports(m.good['visible-reports'])

	//=: net weight
	g.setNetWeight(v2d(m.good['net-weight']))

	//=: gross weight
	g.setGrossWeight(v2d(m.good['gross-weight']))

	//=: bar code
	g.setBarCode(m.good['bar-code'])

	//?: {sub-good}
	if(gu.getSuperGood())
	{
		//~: volume coefficient
		ZeT.assertn(m.calc)
		var vo = v2d(m.calc['sub-volume'])
		ZeT.assertn(vo)

		//~: put in the calculation
		ZeT.assertn(gu.getGoodCalc())
		gu.getGoodCalc().getOx().setSubVolume(vo)
		gu.getGoodCalc().updateOx()
	}

	function v2d(v)
	{
		var d = ZeT.assertn(ZeT.jdecimal(v))
		if(ZeT.isx(d)) return null
		ZeT.assert(ZeT.CMP.grZero(d) && (d.scale() < 4))
		if(d.scale() == 0) d = d.setScale(1)
		return d
	}
}

function goodUsageDescr(flags)
{
	var s = '', p

	function add(mask, prefix, txt)
	{
		if(!(flags & mask)) return
		if(s.length) s += ', '
		if(p !== prefix) s += prefix + ' '
		p = prefix; s += txt
	}

	add(1, 'имеет', 'несколько ед. измерения')
	add(2, 'используется', 'в формулах')
	add(4, 'находится в', 'пр.-листах')
	add(8, 'находится в', 'накладных закупки')
	add(16, 'находится в', 'накладных продажи')
	add(32, 'находится в', 'накладных перемещения')
	add(64, 'находится в', 'документах инвентаризации')
	add(128, 'стал', 'промежуточным товаром в накладных')

	return s
}