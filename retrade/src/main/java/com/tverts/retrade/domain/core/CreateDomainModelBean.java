package com.tverts.retrade.domain.core;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: objects */

import com.tverts.objects.ObjectParamView;


/**
 * Model bean to create (generate) new Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(propOrder = {
  "testDomain", "logParam", "params"
})
public class CreateDomainModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: CreateDomainModelBean (bean) interface */

	@XmlElement(name = "test-domain")
	public Boolean isTestDomain()
	{
		return (testDomain)?(Boolean.TRUE):(null);
	}

	public void setTestDomain(Boolean v)
	{
		this.testDomain = Boolean.TRUE.equals(v);
	}

	@XmlElement(name = "log-param")
	public String getLogParam()
	{
		return logParam;
	}

	public void setLogParam(String logParam)
	{
		this.logParam = logParam;
	}

	@XmlElement(name = "object-param")
	@XmlElementWrapper(name = "object-params")
	public ObjectParamView[] getParams()
	{
		return params;
	}

	public void setParams(ObjectParamView[] params)
	{
		if(params == null) throw new IllegalArgumentException();
		this.params = params;
	}


	/* state of the model */

	private boolean           testDomain;
	private String            logParam;
	private ObjectParamView[] params = new ObjectParamView[0];
}