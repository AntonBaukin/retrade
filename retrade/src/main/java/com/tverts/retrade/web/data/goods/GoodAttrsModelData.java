package com.tverts.retrade.web.data.goods;

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

/* com.tverts: api (core, retrade) */

import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: endure (core) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;

/* com.tverts: retrade domain */

import com.tverts.retrade.domain.goods.GoodAttrsModelBean;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: support */


/**
 * Provides Good attribute types or values.
 * Values are returned when concrete Good
 * Unit is defined in the model.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = { "model", "goodAttrs" })
public class GoodAttrsModelData implements ModelData
{
	/* public: constructors */

	public GoodAttrsModelData()
	{}

	public GoodAttrsModelData(GoodAttrsModelBean model)
	{
		this.model = model;
	}


	/* Goods Model Data */

	@XmlElement
	public GoodAttrsModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "good-attr")
	@XmlElementWrapper(name = "good-attrs")
	@SuppressWarnings("unchecked")
	public List<GoodAttr> getGoodAttrs()
	{
		//~: select the attribute types
		List<AttrType> ats = bean(GetUnity.class).
		  getAttrTypes(model.domain(), Goods.typeGoodAttr().getPrimaryKey());

		//~: get attribute type objects
		List<GoodAttr> res = new ArrayList<>(ats.size());
		for(AttrType at : ats) res.add((GoodAttr) at.getOx());

		//~: set local names
		for(GoodAttr ga : res)
			if(ga.getNameLo() == null)
				ga.setNameLo(ga.getName());

		return res;
	}


	/* private: model */

	private GoodAttrsModelBean model;
}