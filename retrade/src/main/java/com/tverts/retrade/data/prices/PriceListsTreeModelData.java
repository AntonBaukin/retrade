package com.tverts.retrade.data.prices;

/* standard Java classes */

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

import com.tverts.endure.tree.TreeNodeView;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.GoodPriceView;
import com.tverts.retrade.domain.prices.PriceList;
import com.tverts.retrade.domain.prices.PriceListsTreeModelBean;


/**
 * Model data provider to display the Price Lists Tree
 * and the prices in separated table.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "pricesNumber", "prices", "listsTree"
})
public class PriceListsTreeModelData implements ModelData
{
	/* constructor */

	public PriceListsTreeModelData()
	{}

	public PriceListsTreeModelData(PriceListsTreeModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public PriceListsTreeModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public Long getPricesNumber()
	{
		if(!ModelRequest.isKey("prices"))
			return null;

		//?: {there is no current price list} display nothing
		if(model.getCurrentList() == null)
			return null;

		return bean(GetGoods.class).countGoodPrices(model);
	}

	@XmlElement(name = "good-price")
	@XmlElementWrapper(name = "good-prices")
	@SuppressWarnings("unchecked")
	public List<GoodPriceView> getPrices()
	{
		if(!ModelRequest.isKey("prices"))
			return null;

		//?: {there is no current price list} display nothing
		if(model.getCurrentList() == null)
			return null;

		List sel = bean(GetGoods.class).selectGoodPrices(model);
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new GoodPriceView().init(o));

		return (List<GoodPriceView>) res;
	}

	@XmlElement(name = "node")
	@XmlElementWrapper(name = "tree")
	@SuppressWarnings("unchecked")
	public List<TreeNodeView> getListsTree()
	{
		if(!ModelRequest.isKey("tree"))
			return null;

		List<PriceList>    lists = bean(GetGoods.class).
		  getPriceLists(model.domain());

		List<TreeNodeView> res   = new ArrayList<TreeNodeView>(lists.size());


		for(PriceList l : lists)
		{
			TreeNodeView n = new TreeNodeView();
			res.add(n);

			//~: list key
			n.setObjectKey(l.getPrimaryKey().toString());

			//~: parent key
			if(l.getParent() != null)
				n.setParentKey(l.getParent().getPrimaryKey().toString());

			//~: code
			n.setCode(l.getCode());

			//~: name
			n.setName(l.getName());
		}

		return res;
	}


	/* private: model */

	private PriceListsTreeModelBean model;
}
