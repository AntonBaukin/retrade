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

import com.tverts.endure.secure.SecAbleView;

/* com.tverts: retrade secure */

import com.tverts.retrade.secure.GetSecureViews;
import com.tverts.retrade.secure.SecAblesModelBean;


/**
 * Data for {@link SecAblesModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data") @XmlType(propOrder = {
  "model", "secAblesNumber", "secAbles"
})
public class SecAblesModelData implements ModelData
{
	/* public: constructors */

	public SecAblesModelData()
	{}

	public SecAblesModelData(SecAblesModelBean model)
	{
		this.model = model;
	}

	
	/* public: SecAblesModelData (bean) interface */

	@XmlElement
	public SecAblesModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getSecAblesNumber()
	{
		return bean(GetSecureViews.class).countAbles(getModel());
	}

	@XmlElement(name = "able")
	@XmlElementWrapper(name = "sec-ables")
	public List<SecAbleView> getSecAbles()
	{
		return bean(GetSecureViews.class).selectAbles(getModel());
	}


	/* the model */

	private SecAblesModelBean model;
}
