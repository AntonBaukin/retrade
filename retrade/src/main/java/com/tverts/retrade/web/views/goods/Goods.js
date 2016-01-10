var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

function getGoodFoldersTree(domain)
{
	var Goods = Java.type('com.tverts.retrade.domain.goods.Goods')
	var get   = ZeT.bean('GetTree')

	//~: get the domain primary key
	if(!domain) domain = ZeT.sec.domain()

	//~: load the goods tree of the domain
	var tree = get.getDomain(domain, Goods.TYPE_GOODS_TREE, null)

	//~: select the folders
	var folders = get.selectFolders(tree);

	//~: build tree from the flat list
	var roots = [], map = {}

	function fetch(pk)
	{
		var x = map[pk]
		if(!x) map[pk] = x = { objectKey: pk, leaf: true }
		return x
	}

	ZeT.each(folders, function(f)
	{
		//~: fetch item by the key
		var x = fetch(f.getPrimaryKey())

		x.code = f.getCode()
		x.name = x.text = f.getName()
		x.cmp  = ZeT.SU.oxtext(x.name)

		//?: {is not root itself}
		if(f.getParent())
		{
			var p = fetch(f.getParent().getPrimaryKey())
			p.leaf = false; if(!p.children) p.children = []
			p.children.push(x)
			x.parentKey = p.objectKey
		}
		else //<-- is a root
			roots.push(x)
	})

	function cmp(a, b)
	{
		return a.cmp.localeCompare(b.cmp)
	}

	function sort(abc)
	{
		ZeT.assert(ZeT.isa(abc))
		abc.sort(cmp)
		if(ZeT.isa(abc.children))
			sort(abc.children)
	}

	//~: encode the tree roots
	return ZeT.o2s(roots)
}

function assignGoodAttributes(gu, g, values)
{
	var Map   = Java.type('java.util.HashMap')
	var Void  = Java.type('java.lang.Void')
	var Goods = Java.type('com.tverts.retrade.domain.goods.Goods')
	var get   = ZeT.bean('GetUnity')

	//~: check the attributes
	ZeT.asserts(values)
	values = ZeT.s2o(values)
	ZeT.assert(ZeT.isa(values))

	//~: load existing attribute types
	var types = get.getAttrTypes(gu.getDomain().getPrimaryKey(),
	  Goods.typeGoodAttr().getPrimaryKey())

	//~: map them by the keys
	var k2t = {}; ZeT.each(types, function(t){
		k2t['' + t.getPrimaryKey()] = t
	})

	//~: resulting attributes
	var attrs = new Map()
	g.setAttrs(attrs)

	//~: first, mark all attributes to remove
	ZeT.each(types, function(t){
		//attrs.put(t.getName(), Void.class)
	})

	//c: decode and validate each value given
	ZeT.each(values, function(xv)
	{
		//~: find the type by the key
		ZeT.asserts(xv.pkey)
		var attrType = ZeT.assertn(k2t[xv.pkey])

		//~: requested value
		var v = validateGoodAttrValue(attrType, xv.value)

		//?: {the value is incorrect}
		ZeT.assert(!ZeT.isu(v))

		attrs.put(attrType.getName(), v)
	})
}

function validateGoodAttrValue(attrType, v)
{
	//?: {value is undefined}
	if(ZeT.isx(v)) return null

	//?: {value is empty string}
	if(ZeT.iss(v) && ZeTS.ises(v)) return null

	//?: {value is empty array}
	if(ZeT.isa(v) && !v.length) return null

	var AttrType = Java.type('com.tverts.endure.core.AttrType')
	var GoodAttr = Java.type('com.tverts.api.retrade.goods.GoodAttr')

	if(attrType instanceof AttrType)
		attrType = attrType.getOx()

	if(attrType instanceof GoodAttr)
	{
		ZeT.asserts(attrType.getObject())
		attrType = ZeT.s2o(attrType.getObject())
		ZeT.assert(ZeT.iso(attrType))
		attrType = ZeT.asserts(attrType.type)
	}

	//?: {couldn't find the type name}
	ZeT.asserts(attrType)

	//?: {value is an array}
	if(ZeT.isa(v))
	{
		var List = Java.type('java.util.ArrayList')
		var list = new List()

		ZeT.each(v, function(x)
		{
			//?: {sub-arrays are not allowed}
			ZeT.assert(!ZeT.isa(x))

			x = validateGoodAttrValue(attrType, x)

			//?: {has no value}
			if(x === null) return

			//?: {illegal value}
			ZeT.assert(!ZeT.isu(x))

			list.add(x)
		})

		return list
	}

	//?: {value must be string-encoded}
	ZeT.asserts(v)

	//?: {string}
	if(attrType == 'string')
		return ZeT.asserts(v)

	//?: {integer}
	if(attrType == 'integer')
		return Java.type('java.lang.Long').parseLong(v)

	//?: {decimal}
	if(attrType == 'decimal')
		return ZeT.jdecimal(v)

	//?: {volume}
	if(attrType == 'volume')
		return ZeT.jdecimal(v)

	throw ZeT.ass('Unknown type: ', attrType)
}