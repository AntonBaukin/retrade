package com.tverts.model.store;

/* Java */

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Models backend for test purposes.
 *
 * @author anton.baukin@gmail.com.
 */
public class ModelsMemoryBackend extends ModelsBackendBase
{
	/* Models Backend */

	public void find(ModelEntry e)
	{
		Save s; if((s = store.get(e.key)) != null)
		{
			e.domain = s.domain;
			e.login  = s.login;
			e.bean   = restore(s.bytes);
		}
	}

	public void store(Collection<ModelEntry> es)
	{
		for(ModelEntry e : es)
			store.put(e.key, new Save(e, store(e.bean)));
	}

	public void remove(ModelEntry e)
	{
		store.remove(e.key);
	}

	protected final Map<String, Save> store =
	  new ConcurrentHashMap<String, Save>(101);

	protected class Save
	{
		Long   domain;
		Long   login;
		byte[] bytes;

		public Save(ModelEntry e, byte[] bytes)
		{
			this.domain = e.domain;
			this.login  = e.login;
			this.bytes  = bytes;
		}
	}
}