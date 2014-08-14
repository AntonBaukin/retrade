package com.tverts.retrade.data.goods;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeFolder;
import com.tverts.endure.tree.TreeNodeView;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;
import com.tverts.retrade.domain.goods.GoodUnitView;


/**
 * Model data provider to display the Goods Tree.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "goodsNumber", "goods", "goodsTree"
})
public class GoodsTreeModelData implements ModelData
{
	/* public: constructors */

	public GoodsTreeModelData()
	{}

	public GoodsTreeModelData(GoodsTreeModelBean model)
	{
		this.model = model;
	}


	/* public: GoodsModelData (bean) interface */

	@XmlElement
	public GoodsTreeModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public Integer getGoodsNumber()
	{
		return (!isGoodsRequest())?(null):
		  bean(GetGoods.class).countGoodUnits(getModel());
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodUnitView> getGoods()
	{
		if(!isGoodsRequest()) return null;

		List sel = bean(GetGoods.class).
		  selectGoodUnits(getModel());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new GoodUnitView().init(o));

		return res;
	}

	@XmlElement(name = "node")
	@XmlElementWrapper(name = "tree")
	@SuppressWarnings("unchecked")
	public List<TreeNodeView> getGoodsTree()
	{
		if(!isTreeRequest()) return null;

		GetTree            get = bean(GetTree.class);
		List<TreeFolder>   fds = get.selectFolders(
		  get.getDomain(model.getTreeDomain()));

		List<TreeNodeView> res =
		  new ArrayList<TreeNodeView>(fds.size());

		for(TreeFolder f : fds)
			res.add(new TreeNodeView().init(f));

		return res;
	}


	/* protected: data access support */

	protected boolean isTreeRequest()
	{
		return ModelRequest.isKey("tree");
	}

	protected boolean isGoodsRequest()
	{
		return ModelRequest.isKey("goods");
	}


	/* private: model */

	private GoodsTreeModelBean model;
}