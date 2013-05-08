package com.tverts.model;

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


	/* public: ModelBean (Java Bean) interface */

	public String       getModelKey()
	{
		return modelKey;
	}

	public void         setModelKey(String key)
	{
		this.modelKey = key;
	}

	public boolean      isActive()
	{
		return active;
	}

	public void         setActive(boolean active)
	{
		this.active = active;
	}


	/* public: ModelBean (data access) interface */

	public ModelData    modelData()
	{
		return null;
	}

	public Long         domain()
	{
		if(getDomain() == null) throw new IllegalStateException(
		  "Domain primary key is not defined in the bean model " +
		  getClass().getSimpleName() + "!"
		);

		return getDomain();
	}

	public Long         getDomain()
	{
		return domain;
	}

	public void         setDomain(Long domain)
	{
		this.domain = domain;
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


	/* private: attributes */

	private String  modelKey;
	private Long    domain;
	private boolean active = true;
}