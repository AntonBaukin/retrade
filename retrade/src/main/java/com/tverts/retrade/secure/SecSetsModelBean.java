package com.tverts.retrade.secure;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade data */

import com.tverts.retrade.data.settings.SecSetsModelData;


/**
 * Model of viewing Secure Sets.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "secure-sets-model")
public class SecSetsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: SecRulesModelBean (bean) interface */

	public String getAblesModel()
	{
		return ablesModel;
	}

	public void setAblesModel(String ablesModel)
	{
		this.ablesModel = ablesModel;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new SecSetsModelData(this);
	}


	/* private: view attributes */

	private String   ablesModel;
}