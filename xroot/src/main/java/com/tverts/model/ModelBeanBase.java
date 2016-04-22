package com.tverts.model;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * Implementation base for model beans.
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(propOrder = {"modelKey", "domain"})
public abstract class ModelBeanBase implements ModelBean
{
	/* Model Bean (main) */

	@XmlElement(name = "model-key")
	public String  getModelKey()
	{
		return modelKey;
	}

	private String modelKey;

	public void    setModelKey(String key)
	{
		this.modelKey = key;
	}

	@XmlTransient
	public boolean isActive()
	{
		return active;
	}

	private boolean active = true;

	public void    setActive(boolean active)
	{
		this.active = active;
	}


	/* Model Bean (data access) */

	@SuppressWarnings("unchecked")
	public ModelData modelData()
	{
		if(dataClass == null)
			return null;

		try
		{
			Class cls = this.getClass();

			while(cls != null)
			{
				//?: {has a constructor of that type}
				try
				{
					return (ModelData) dataClass.getConstructor(cls).newInstance(this);
				}
				catch(NoSuchMethodException e)
				{} //<-- ignore this error

				//~: lookup in the interfaces
				for(Class ifs : cls.getInterfaces()) try
				{
					return (ModelData) dataClass.getConstructor(ifs).newInstance(this);
				}
				catch(NoSuchMethodException e)
				{} //<-- ignore this error
			}

			//~: not found any specific constructor, invoke the default
			try
			{
				return (ModelData) dataClass.getConstructor().newInstance();
			}
			catch(NoSuchMethodException e)
			{
				//!: unable to create an instance
				throw EX.state("No constructor available!");
			}
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Unable to create Data Model of class [",
			  dataClass.toString(), "]!");
		}
	}

	public Long      domain()
	{
		return EX.assertn(getDomain(),
		  "Domain primary key is not defined in the bean model of class [",
		  getClass().getName(), "]!"
		);
	}

	public Long      getDomain()
	{
		return domain;
	}

	private Long domain;

	public void      setDomain(Long domain)
	{
		this.domain = domain;
	}

	@XmlTransient
	public Class<? extends ModelData> getDataClass()
	{
		return dataClass;
	}

	private Class<? extends ModelData> dataClass;

	public void setDataClass(Class<? extends ModelData> dataClass)
	{
		this.dataClass = dataClass;
	}


	/* protected: support interface */

	protected ModelBean readModelBean(String key)
	{
		return ModelsAccessPoint.modelsStore().read(key);
	}

	@SuppressWarnings("unchecked")
	protected <B extends ModelBean> B readModelBean(String key, Class<B> beanClass)
	{
		ModelBean mb = readModelBean(key);

		if((mb != null) && (beanClass != null) &&
		   !beanClass.isAssignableFrom(mb.getClass())
		  )
			throw EX.state("Model bean requested by the key [", key,
			  "] is not a class checked [", beanClass.getName(), "]!");

		return (B)mb;
	}


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		IO.str(o, modelKey);
		IO.longer(o, domain);
		o.writeBoolean(active);
		IO.cls(o, dataClass);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		modelKey  = IO.str(i);
		domain    = IO.longer(i);
		active    = i.readBoolean();

		dataClass = IO.cls(i);
		EX.assertx((dataClass == null) || ModelData.class.isAssignableFrom(dataClass));
	}
}