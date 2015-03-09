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
public class      LinkedCacheModelsStore
       extends    ModelsStoreBase
       implements CachingModelsStore
{
	/* public: constructor */

	public LinkedCacheModelsStore(int size)
	{
		EX.assertx(size > 0);
		this.size    = size;
		this.entries = new HashMap<String, LinkedEntry>(size);
		this.pruned  = new HashSet<LinkedEntry>(size / 10);
	}


	/* Models Store Base */

	public void setDelegate(Delegate delegate)
	{
		EX.assertx(delegate == null || delegate instanceof CachingDelegate);
		super.setDelegate(delegate);
	}


	/* Caching Models Store */

	public void copyPruned(Map<ModelEntry, Integer> items)
	{

	}

	public void copyAll(Map<ModelEntry, Integer> items)
	{

	}

	public void commitSaved(Map<ModelEntry, Integer> items, boolean remove)
	{

	}


	/* protected: entries access */

	protected ModelEntry find(String key)
	{
		synchronized(this)
		{
			//?: {found it not}
			LinkedEntry e = entries.get(key);
			if(e == null) return null;

			//?: {this entry was pruned}
			if(pruned.contains(e))
			{
				//~: make it effective back
				pruned.remove(e);

				//~: place as the head
				placeAsHead(e);
			}
			//~: just move to be head
			else
				moveToHead(e);

			return e;
		}
	}

	protected void       remove(ModelEntry x)
	{
		synchronized(this)
		{
			//?: {removed it not}
			LinkedEntry e = entries.remove(x.key);
			EX.assertn(e);

			//?: {this entry was pruned}
			if(pruned.contains(e))
				pruned.remove(e);
			//~: remove from the list
			else
				unlink(e);
		}
	}

	protected void       save(ModelEntry e)
	{
		if(!(e instanceof LinkedEntry))
			throw EX.ass("Not a Linked Model Entry!");

		int s, p = 0; synchronized(this)
		{
			entries.put(e.key, (LinkedEntry)e);

			//~: place it as a head
			placeAsHead((LinkedEntry)e);

			//?: {has overflow}
			if((s = entries.size()) > this.size)
			{
				//~: unlink the tail
				LinkedEntry x = EX.assertn(this.tail);
				unlink(x);

				//~: move it to pruned
				pruned.add(x);
				p = pruned.size();
			}
		}

		//?: {has overflow} notify the delegate
		if((s > this.size) && (delegate != null))
			((CachingDelegate) delegate).overflow(p);
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


	/* protected: list operations */

	protected void moveToHead(LinkedEntry e)
	{
		EX.assertn(this.head);
		if(head == e) return;

		//~: unlink, then place
		unlink(e);
		placeAsHead(e);
	}

	protected void placeAsHead(LinkedEntry e)
	{
		EX.assertx(e.prev == null);
		EX.assertx(e.next == null);

		//?: {head is undefined}
		if(head == null)
		{
			EX.assertx(tail == null);
			head = tail = e;
		}
		else
		{
			e.next = head;
			EX.assertx(e.prev == null);
			head.prev = e;
			head = e;
		}
	}

	protected void unlink(LinkedEntry e)
	{
		//?: {it is head}
		if(head == e)
			head = e.next;

		//?: {it is tail}
		if(tail == e)
			tail = e.prev;

		//?: {has previous}
		if(e.prev != null)
			e.prev.next = e.next;

		//?: {has next}
		if(e.next != null)
			e.next.prev = e.prev;

		e.prev = e.next = null;
	}


	/* protected: the cache state */

	protected final int   size;
	protected LinkedEntry head, tail;

	protected final Map<String, LinkedEntry> entries;
	protected final Set<LinkedEntry>         pruned;
}