package com.tverts.model;

/* standard Java classes */

import java.util.Date;


/**
 * COMMENT DelegatingModelPoint
 *
 * @author anton.baukin@gmail.com
 */
public class DelegatingModelPoint implements ModelPoint
{
	public static final long serialVersionUID = 0L;


	/* public: DelegatingModelPoint (bean) interface */

	public ModelStore getModelStore()
	{
		return modelStore;
	}

	public void       setModelStore(ModelStore modelStore)
	{
		this.modelStore = modelStore;
	}


	/* public: ModelStore (not Java Bean) interface */

	public ModelBean findBean(String key)
	{
		return modelStore().findBean(key);
	}

	public ModelBean addBean(ModelBean bean)
	{
		return modelStore().addBean(bean);
	}

	public ModelBean removeBean(String key)
	{
		return modelStore().removeBean(key);
	}

	public ModelBean readBean(String key)
	{
		return modelStore().readBean(key);
	}

	public Date      accessReadTime(String key)
	{
		return modelStore().accessReadTime(key);
	}

	public Long      accessLogin(String key)
	{
		return modelStore().accessLogin(key);
	}


	/* protected: delegation support */

	protected ModelStore modelStore()
	{
		//?: {the store model is not assigned} illegal state
		if(this.modelStore == null) throw new IllegalStateException(
		  "Delegating Model Point has no Model Store strategy installed!");

		return this.modelStore;
	}


	/* private: aggregated strategies */

	private ModelStore modelStore;
}