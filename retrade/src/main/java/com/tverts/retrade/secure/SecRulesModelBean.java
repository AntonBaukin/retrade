package com.tverts.retrade.secure;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

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
public class SecRulesModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: SecRulesModelBean (bean) interface */

	public String[] getSearchNames()
	{
		return searchNames;
	}

	public void setSearchNames(String[] searchNames)
	{
		this.searchNames = searchNames;
	}

	public String getSelSet()
	{
		return selSet;
	}

	public void setSelSet(String selSet)
	{
		this.selSet = selSet;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new SecRulesModelData(this);
	}


	/* private: model attributes */

	private String[] searchNames;
	private String   selSet;
}