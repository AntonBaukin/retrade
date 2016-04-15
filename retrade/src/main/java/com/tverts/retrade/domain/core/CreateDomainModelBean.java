package com.tverts.retrade.domain.core;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: objects */

import com.tverts.objects.ObjectParamView;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * Model bean to create (generate) new Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "create-domain-model", propOrder = {
  "testDomain", "logParam", "params"
})
public class CreateDomainModelBean extends ModelBeanBase
{
	/* Create Domain Model  */

	@XmlElement(name = "test-domain")
	public Boolean getTestDomain()
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
		this.params = EX.assertn(params);
	}


	/* private: encapsulated data */

	private boolean           testDomain;
	private String            logParam;
	private ObjectParamView[] params = new ObjectParamView[0];


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		o.writeBoolean(testDomain);
		IO.str(o, logParam);
		IO.obj(o, params);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		testDomain = i.readBoolean();
		logParam   = IO.str(i);
		params     = IO.obj(i, ObjectParamView[].class);
	}
}