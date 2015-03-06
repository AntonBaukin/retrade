package com.tverts.model.store;

/* Java */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * WARNING! This implementation is for development only!
 * It does not remove the outdated model entries.
 *
 * @author anton.baukin@gmail.com
 */
public class SimpleModelStore extends ModelsStoreBase
{
	/* protected: entries access */

	protected ModelEntry find(String key)
	{
		return entries.get(key);
	}

	protected void       remove(ModelEntry e)
	{
		entries.remove(e.key);
	}

	protected void       save(ModelEntry e)
	{
		entries.put(e.key, e);
	}

	private Map<String, ModelEntry> entries =
	  new ConcurrentHashMap<>(101);
}