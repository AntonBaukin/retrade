package com.tverts.retrade.secure;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade data */

import com.tverts.retrade.data.settings.SecRulesModelData;


/**
 * Model of viewing Secure Rules.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "secure-rules-model")
public class SecRulesModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new SecRulesModelData(this);
	}
}