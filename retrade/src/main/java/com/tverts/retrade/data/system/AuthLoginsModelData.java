package com.tverts.retrade.data.system;

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

/* com.tverts: endure + retrade domain (auth) */

import com.tverts.endure.auth.AuthLoginView;
import com.tverts.retrade.domain.auth.AuthLoginsModelBean;
import com.tverts.retrade.domain.auth.GetAuthLoginsViews;


/**
 * Model data provider for the table of all the
 * Logins of the domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "loginsNumber", "logins"})
public class AuthLoginsModelData implements ModelData
{
	/* public: constructors */

	public AuthLoginsModelData()
	{}

	public AuthLoginsModelData(AuthLoginsModelBean model)
	{
		this.model = model;
	}


	/* public: LoginsModelData (bean) interface */

	@XmlElement
	public AuthLoginsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getLoginsNumber()
	{
		return bean(GetAuthLoginsViews.class).
		  countLogins(getModel());
	}

	@XmlElement(name = "login")
	@XmlElementWrapper(name = "logins")
	public List<AuthLoginView> getLogins()
	{
		return bean(GetAuthLoginsViews.class).
		  selectLoginViews(getModel());
	}


	/* private: model */

	private AuthLoginsModelBean model;
}