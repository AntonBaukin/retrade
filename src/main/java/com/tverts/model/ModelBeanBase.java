package com.tverts.model;

import com.tverts.support.EX;

/**
 * Implementation base for model beans. Note that
 * this class is also serializable what is not
 * required for model beans as XML mapping is
 * frequently adopted.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ModelBeanBase implements ModelBean
{
	public static final long serialVersionUID = 0L;


	/* Model Bean (main) */

	public String  getModelKey()
	{
		return modelKey;
	}

	private String modelKey;

	public void    setModelKey(String key)
	{
		this.modelKey = key;
	}

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
		if(getDomain() == null) throw new IllegalStateException(
		  "Domain primary key is not defined in the bean model " +
		  getClass().getSimpleName() + "!"
		);

		return getDomain();
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
			throw new IllegalStateException(String.format(
			  "Model bean requested by the key '%s' is not a class checked [%s]!",
			  key, beanClass.getName()
			));

		return (B)mb;
	}
}