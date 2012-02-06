package com.tverts.endure;

/* standard Java classes */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/* com.tverts: support */

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;


/**
 * {@link UnityTypes} is a collection of {@link UnityType}
 * instances of the system level. User-defined types are
 * not placed here.
 *
 * The system types are registered in the configuration and
 * automatically created during the startup.
 *
 * This singleton point does not contain the configuration
 * specific entries, the runtime ones only.
 *
 * All the instances here are detached from the session
 * they are originally loaded (created).
 *
 * As system types are loaded during the startup and may
 * not be deleted, the registry interface contains
 * read-only methods.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class UnityTypes
{
	/* public: Singleton */

	public static UnityTypes getInstance()
	{
		return INSTANCE;
	}

	private static final UnityTypes INSTANCE =
	  new UnityTypes();

	protected UnityTypes()
	{
		ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

		this.readLock  = rwlock.readLock();
		this.writeLock = rwlock.writeLock();

		this.entries   = createEntriesMap();
	}


	/* public: UnityTypes interface */

	public UnityType  getType(Class typeClass, String typeName)
	{
		if(typeClass == null)
			throw new IllegalArgumentException();

		if((typeName = s2s(typeName)) == null)
			throw new IllegalArgumentException();

		//~> (read lock
		readLock.lock();

		UnityType res   = null;
		Entry     entry = entries.get(typeClass);

		if(entry != null)
			res = entry.getUnityType(typeName);

		//~> read lock)
		readLock.unlock();

		return res;
	}

	public UnityType  getDistinctType(Class typeClass)
	{
		if(typeClass == null)
			throw new IllegalArgumentException();

		//~> (read lock
		readLock.lock();

		UnityType res   = null;
		Entry     entry = entries.get(typeClass);

		if(entry != null)
			res = entry.getDistinctUnityType();

		//~> read lock)
		readLock.unlock();

		return res;
	}

	public Set<Class> getTypesClasses()
	{
		//~> (read lock
		readLock.lock();

		Set<Class> res = new HashSet<Class>(entries.keySet());

		//~> read lock)
		readLock.unlock();

		return res;
	}

	public Map<String, UnityType>
	                  getTypes(Class typeClass)
	{
		if(typeClass == null)
			throw new IllegalArgumentException();

		//~> (read lock
		readLock.lock();

		Map<String, UnityType> res   = null;
		Entry                  entry = entries.get(typeClass);

		if(entry != null)
			res = entry.getTypes();

		//~> read lock)
		readLock.unlock();

		return (res != null)?(res):(new HashMap<String, UnityType>(0));
	}

	public void       setType
	  (Class typeClass, String typeName, UnityType unityType)
	{
		if(typeClass == null)
			throw new IllegalArgumentException();

		if((typeName = s2s(typeName)) == null)
			throw new IllegalArgumentException();

		//~> (write lock
		writeLock.lock();

		Entry entry = entries.get(typeClass);

		if(entry == null)
			entries.put(typeClass, entry = new Entry(typeClass));

		entry.setUnityType(typeName, unityType);

		if(entry.isEmpty())
			entries.remove(typeClass);

		//~> write lock)
		writeLock.unlock();
	}

	public void       setTypes
	  (Class typeClass, Map<String, UnityType> types)
	{
		if(typeClass == null)
			throw new IllegalArgumentException();

		//~> (write lock
		writeLock.lock();

		Entry entry = entries.get(typeClass);

		if(entry == null)
			entries.put(typeClass, entry = new Entry(typeClass));

		//?: {has no entries defined} just remove them
		if((types == null) || types.isEmpty())
			entries.remove(typeClass);
		else
			entry.rewrite(types); //<-- rewrite all entries

		if(entry.isEmpty())
			entries.remove(typeClass);

		//~> write lock)
		writeLock.unlock();
	}


	/* public: UnityTypes simplifications */

	public static UnityType unityType(Class typeClass, String typeName)
	{
		UnityType res = getInstance().getType(typeClass, typeName);

		if(res == null) throw new IllegalStateException(String.format(
		  "No system Unity Type registered for class [%s] having name [%s]!",
		  typeClass.getName(), typeName
		));

		return res;
	}

	public static UnityType unityType(Class typeClass)
	{
		UnityType res = getInstance().getDistinctType(typeClass);

		if(res == null) throw new IllegalStateException(String.format(
		  "No distinct system Unity Type registered for class [%s]!",
		  typeClass.getName()
		));

		return res;
	}


	/* protected: registry entry */

	protected static class Entry
	{
		/* public: constructor */

		public Entry(Class unityClass)
		{
			this.unityClass = unityClass;
			this.unityTypes = createTypesMap();
		}

		/* public: entry interface */

		public Class     getUnityClass()
		{
			return unityClass;
		}

		public boolean   isEmpty()
		{
			return unityTypes.isEmpty();
		}

		public UnityType getUnityType(String typeName)
		{
			return unityTypes.get(typeName);
		}

		public UnityType getDistinctUnityType()
		{
			return (unityTypes.size() != 1)?(null):
			  (unityTypes.entrySet().iterator().next().getValue());
		}

		public Map<String, UnityType>
		                 getTypes()
		{
			return new HashMap<String, UnityType>(unityTypes);
		}

		public void      setUnityType(String typeName, UnityType unityType)
		{
			if(unityType == null)
				unityTypes.remove(typeName);
			else
				unityTypes.put(typeName, unityType);
		}

		public void      rewrite(Map<String, UnityType> types)
		{
			unityTypes.clear();
			unityTypes.putAll(types);

			//~: remove undefined types
			for(Iterator<Map.Entry<String, UnityType>> ei =
			     unityTypes.entrySet().iterator();(ei.hasNext());)
			{
				Map.Entry<String, UnityType> e = ei.next();

				if(sXe(e.getKey()) || (e.getValue() == null))
					ei.remove();
			}
		}

		/* public: Object interface */

		public boolean   equals(Object o)
		{
			return (this == o) ||
			  unityClass.equals(((Entry)o).unityClass);
		}

		public int       hashCode()
		{
			return unityClass.hashCode();
		}

		/* protected: entry internals */

		protected Map<String, UnityType>
		                 createTypesMap()
		{
			return new HashMap<String, UnityType>(3);
		}

		/* protected: entry mapping */

		protected final Class                  unityClass;
		protected final Map<String, UnityType> unityTypes;
	}

	/* protected: registry entries support */

	protected Map<Class, Entry> createEntriesMap()
	{
		return new HashMap<Class, Entry>(31);
	}

	/* protected: the entries */

	protected final Map<Class, Entry> entries;

	/* private: locks */

	protected final Lock readLock;
	protected final Lock writeLock;
}