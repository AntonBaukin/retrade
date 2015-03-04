package com.tverts.model.store;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.EX;


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
		ModelEntry e; synchronized(entries)
		{
			e = entries.get(key);
		}

		//?: {found it not}
		if(e == null)
			return (delegate == null)?(null):(delegate.find(key));
		else
			return (delegate == null)?(e):(delegate.found(e));
	}

	protected void       remove(ModelEntry e)
	{
		synchronized(entries)
		{
			entries.remove(e.key);
		}

		if(delegate != null)
			delegate.remove(e);
	}

	protected void       save(ModelEntry e)
	{
		EX.asserts(e.key);
		EX.assertn(e.bean);

		if(delegate != null)
			e = delegate.save(e);

		synchronized(entries)
		{
			entries.put(e.key, e);
		}
	}

	private Map<String, ModelEntry> entries =
	  new HashMap<String, ModelEntry>(17);
}