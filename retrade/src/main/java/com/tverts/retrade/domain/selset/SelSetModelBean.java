package com.tverts.retrade.domain.selset;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;


/**
 * Model Bean of current Selection Set.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class SelSetModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: SelSetModelBean (bean) interface */

	public Long getLogin()
	{
		return login;
	}

	public void setLogin(Long login)
	{
		this.login = login;
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
		return new SelSetModelData(this);
	}


	/* selection set model */

	private Long   login;
	private String selSet;
}