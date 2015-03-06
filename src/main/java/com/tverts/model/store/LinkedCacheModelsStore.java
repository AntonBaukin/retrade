package com.tverts.model.store;

/* Java */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Cache applying Hash Map and linked list of the entries.
 * Prunes the last recently used items.
 *
 * @author anton.baukin@gmail.com.
 */
public class LinkedCacheModelsStore extends ModelsStoreBase
{
	/* public: constructor */

	public LinkedCacheModelsStore(int size)
	{
		EX.assertx(size > 0);
		this.size    = size;
		this.entries = new HashMap<String, LinkedEntry>(size);
		this.pruned  = new HashSet<LinkedEntry>(size / 10);
	}


	/* protected: entries access */

	protected ModelEntry find(String key)
	{
		synchronized(this)
		{
			return entries.get(key);
		}
	}

	protected void       remove(ModelEntry e)
	{
		synchronized(this)
		{
			entries.remove(e.key);
		}
	}

	protected void       save(ModelEntry e)
	{
		if(!(e instanceof LinkedEntry))
			throw EX.ass("Not a Linked Model Entry!");

		synchronized(this)
		{
			entries.put(e.key, (LinkedEntry)e);
		}
	}

	protected ModelEntry newEntry()
	{
		return new LinkedEntry();
	}


	/* public: Linked Model Entity */

	public static class LinkedEntry extends ModelEntry
	{
		public LinkedEntry prev;
		public LinkedEntry next;
	}


	/* protected: the cache state */

	protected final int   size;
	protected LinkedEntry head, tail;

	protected final Map<String, LinkedEntry> entries;
	protected final Set<LinkedEntry>         pruned;
}