package com.tverts.model;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This Model Store just wraps else Store
 * to support additional isolation.
 *
 * @author anton.baukin@gmail.com
 */
public class DelegatingModelPoint implements ModelPoint
{
	public static final long serialVersionUID = 0L;


	/* public: DelegatingModelPoint (bean) interface */

	public void       setModelStore(ModelStore store)
	{
		this.modelStore = EX.assertn(store);
	}


	/* public: ModelStore (not Java Bean) interface */

	public ModelBean  addBean(ModelBean bean)
	{
		return modelStore().addBean(bean);
	}

	public ModelBean  removeBean(String key)
	{
		return modelStore().removeBean(key);
	}

	public ModelBean  readBean(String key)
	{
		return modelStore().readBean(key);
	}


	/* protected: delegation support */

	protected ModelStore modelStore()
	{
		//?: {the store model is not assigned}
		return EX.assertn(this.modelStore,
		  "Delegating Model Point has no Model Store strategy installed!"
		);
	}


	/* private: aggregated strategies */

	private ModelStore modelStore;
}