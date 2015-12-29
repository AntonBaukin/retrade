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

import com.tverts.api.core.JString;
import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: endure (core) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;

/* com.tverts: retrade domain */

import com.tverts.retrade.domain.goods.GoodAttrsModelBean;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides Good attribute types or values.
 * Values are returned when concrete Good
 * Unit is defined in the model.
 *
 * @author anton.baukin@gmail.com.
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
		List<GoodAttr> res = new ArrayList<>();

		//~: select the attribute types
		List<AttrType> ats = bean(GetUnity.class).
		  getAttrTypes(model.domain(), Goods.typeGoodAttr().getPrimaryKey());

		for(AttrType at : ats)
		{
			GoodAttr a = new GoodAttr();
			res.add(a);

			//=: primary key
			a.setPkey(at.getPrimaryKey());

			//=: name
			a.setName(at.getName());
			a.setNameLo(at.getNameLo());

			//=: system
			a.setSystem(at.isSystem());

			//=: object
			EX.assertx(at.getOx() instanceof JString);
			a.setObject(((JString) at.getOx()).getJson());
		}

		//?: {model good is not defined} attribute types only
		if(model.getGoodUnit() == null)
			return res;

		return res;
	}



	/* private: model */

	private GoodAttrsModelBean model;
}