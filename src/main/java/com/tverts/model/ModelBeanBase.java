package com.tverts.model;

/* Java */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;


/**
 * Implementation base for model beans.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelBeanBase
       implements     ModelBean, Externalizable
{
	/* Model Bean (main) */

	public String  getModelKey()
	{
		return modelKey;
	}

	public void    setModelKey(String key)
	{
		this.modelKey = key;
	}

	public boolean isActive()
	{
		return active;
	}

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

	public void      setDomain(Long domain)
	{
		this.domain = domain;
	}

	public Class<? extends ModelData>
	                 getDataClass()
	{
		return dataClass;
	}

	public void      setDataClass(Class<? extends ModelData> dataClass)
	{
		this.dataClass = dataClass;
	}


	/* protected: support interface */

	protected ModelBean readModelBean(String key)
	{
		return ModelAccessPoint.model().readBean(key);
	}

	@SuppressWarnings("unchecked")
	protected <B extends ModelBean> B
	                    readModelBean(String key, Class<B> beanClass)
	{
		ModelBean mb = readModelBean(key);

		if((mb != null) && (beanClass != null) &&
		   !beanClass.isAssignableFrom(mb.getClass())
		  )
			throw EX.state("Model bean requested by the key [", key,
			  "] is not a class checked [", beanClass.getName(), "]!");

		return (B)mb;
	}


	/* private: encapsulated data */

	private String  modelKey;
	private Long    domain;
	private boolean active = true;
	private Class<? extends ModelData>
	                dataClass;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		IO.str(o, modelKey);
		o.writeLong(domain);
		o.writeBoolean(active);
		IO.cls(o, dataClass);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		modelKey  = IO.str(i);
		domain    = i.readLong();
		active    = i.readBoolean();

		dataClass = IO.cls(i);
		EX.assertx((dataClass == null) || ModelData.class.isAssignableFrom(dataClass));
	}
}