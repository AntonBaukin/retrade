package com.tverts.model.store;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: model */

import com.tverts.model.ModelBean;


/**
 * WARNING! This implementation is for development only!
 * It does not remove the outdated model entries.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SimpleModelStore extends ModelStoreBase
{
	public static final long serialVersionUID = 0L;


	/* public: ModelStore (not Java Bean) interface */

	public ModelBean         removeBean(String key)
	{
		synchronized(entries)
		{
			ModelBeanEntry e = entries.remove(key);
			return (e == null)?(null):(e.getModelBean());
		}
	}

	/* protected: ModelStoreBase interface */

	protected ModelBeanEntry findEntry(String key)
	{
		synchronized(entries)
		{
			return entries.get(key);
		}
	}

	protected ModelBeanEntry saveEntry(ModelBean bean)
	{
		ModelBeanEntry e;

		synchronized(entries)
		{
			//~: get the entry
			e = entries.get(bean.getModelKey());

			//?: {not found it} add new one
			if(e == null)
				entries.put(bean.getModelKey(), e = new ModelBeanEntry(bean));
		}

		return e;
	}


	/* private: the entries map */

	private Map<String, ModelBeanEntry> entries =
	  new HashMap<String, ModelBeanEntry>(7);
}