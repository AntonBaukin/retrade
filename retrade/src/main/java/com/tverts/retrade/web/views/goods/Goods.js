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