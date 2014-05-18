package com.tverts.retrade.domain.selset;

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


/**
 * Model data provider for the table of all the
 * Logins of the domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "selSetSize", "selSet"})
public class SelSetModelData implements ModelData
{
	/* public: constructors */

	public SelSetModelData()
	{}

	public SelSetModelData(SelSetModelBean model)
	{
		this.model = model;
	}


	/* public: SelSetModelData (bean) interface */

	@XmlElement
	public SelSetModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getSelSetSize()
	{
		return bean(GetSelSet.class).
		  countSelSetSize(getModel());
	}

	@XmlElement(name = "item")
	@XmlElementWrapper(name = "selset")
	public List<SelItemView> getSelSet()
	{
		List<SelItem> items = bean(GetSelSet.class).
		  selectSelSetItems(getModel());

		List<SelItemView> res =
		  new ArrayList<SelItemView>(items.size());

		for(SelItem item : items)
			res.add(new SelItemView().init(item));
		return res;
	}


	/* private: model */

	private SelSetModelBean model;
}