package com.tverts.retrade.data.settings;

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

import com.tverts.endure.secure.SecRuleView;

/* com.tverts: retrade secure */

import com.tverts.retrade.secure.GetSecureViews;
import com.tverts.retrade.secure.SecRulesModelBean;


/**
 * Data for {@link SecRulesModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data") @XmlType(propOrder = {
  "model", "secRulesNumber", "secRules"
})
public class SecRulesModelData implements ModelData
{
	/* public: constructors */

	public SecRulesModelData()
	{}

	public SecRulesModelData(SecRulesModelBean model)
	{
		this.model = model;
	}


	/* public: SecRulesModelData (bean) interface */

	@XmlElement
	public SecRulesModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getSecRulesNumber()
	{
		return bean(GetSecureViews.class).countRules(getModel());
	}

	@XmlElement(name = "rule")
	@XmlElementWrapper(name = "sec-rules")
	public List<SecRuleView> getSecRules()
	{
		return bean(GetSecureViews.class).selectRules(getModel());
	}


	/* the model */

	private SecRulesModelBean model;
}