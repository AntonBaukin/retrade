package com.tverts.retrade.web.data.goods;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.SimpleModelBean;
import com.tverts.model.SimpleModelData;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.Goods;


/**
 * Collects and returns the ordered list of
 * the distinct groups of the Domain Good Units.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodGroups"})
public class GoodGroupsModelData implements SimpleModelData
{
	public static final long serialVersionUID = 20150223L;


	/* public: constructors */

	public GoodGroupsModelData()
	{}

	public GoodGroupsModelData(SimpleModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	public SimpleModelBean getModel()
	{
		return model;
	}

	public void setModel(SimpleModelBean model)
	{
		this.model = model;
	}

	@XmlElement(name = "cat-item")
	@XmlElementWrapper(name = "cat-items")
	public List<CatItemView> getGoodGroups()
	{
		List<String> gs = bean(GetGoods.class).
		  getAttrStrings(getModel().domain(), Goods.AT_GROUP);
		Collections.sort(gs, String::compareToIgnoreCase);

		List<CatItemView> res = new ArrayList<>(gs.size());
		for(String g : gs)
		{
			CatItemView ci = new CatItemView();
			ci.setCode(g);
			res.add(ci);
		}

		return res;
	}


	/* private: model */

	private transient SimpleModelBean model;
}