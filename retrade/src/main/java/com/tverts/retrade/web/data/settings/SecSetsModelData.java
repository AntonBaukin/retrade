package com.tverts.retrade.web.data.settings;

/* standard Java classes */

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

/* com.tverts: secure */

import com.tverts.endure.secure.SecSetView;

/* com.tverts: retrade secure */

import com.tverts.retrade.secure.GetSecureViews;
import com.tverts.retrade.secure.SecSetsModelBean;

/* com.tverts: support */


/**
 * Data for {@link SecSetsModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data") @XmlType(propOrder = {
  "model", "secSetsNumber", "secSets"
})
public class SecSetsModelData implements ModelData
{
	/* public: constructors */

	public SecSetsModelData()
	{}

	public SecSetsModelData(SecSetsModelBean model)
	{
		this.model = model;
	}


	/* public: SecRulesModelData (bean) interface */

	@XmlElement
	public SecSetsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getSecSetsNumber()
	{
		return bean(GetSecureViews.class).countSets(getModel());
	}

	@XmlElement(name = "set")
	@XmlElementWrapper(name = "sec-sets")
	public List<SecSetView> getSecSets()
	{
		return bean(GetSecureViews.class).selectSets(getModel());
	}


	/* the model */

	private SecSetsModelBean model;
}