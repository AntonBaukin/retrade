package com.tverts.hibery.sql;

/* Java */

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.xml.SaxProcessor;



/**
 * Supports loading and storing database queries
 * from XML files names as the Get-strategies
 * with '.query.xml' suffix added.
 *
 * Cache supports hierarchy of Get-strategies
 * and look up in the parent when doesn't find
 * the query id requested.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class QueryCache
{
	/* Cache Registry */

	public static QueryCache cache(Class<?> get)
	{
		EX.assertn(get);

		//?: {query base class} has it no
		if(GetObjectBase.class.equals(get))
			return null;

		//?: {object base class} has it no
		if(Object.class.equals(get))
			return null;

		//~: get with create on first demand
		return CACHES.computeIfAbsent(get, QueryCache::new);
	}

	private static final ConcurrentMap<Class<?>, QueryCache>
	  CACHES = new ConcurrentHashMap<>(17);

	protected QueryCache(Class<?> get)
	{
		this.parent = QueryCache.cache(get.getSuperclass());
		this.file   = get.getResource(get.getSimpleName() + ".query.xml");

		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.readLock  = rwl.readLock();
		this.writeLock = rwl.writeLock();
	}


	/* Query Cache */

	public String q(String id)
	{
		EX.asserts(id);
		readLock.lock();

		try
		{
			//?: {no loaded yet} do load
			if(queries == null)
			{
				readLock.unlock();
				writeLock.lock();

				try
				{
					if(queries == null)
						this.load();
				}
				finally
				{
					writeLock.unlock();
					readLock.lock();
				}
			}

			//~: lookup in the mapping
			String q = queries.get(id);
			return (q != null)?(q):(parent == null)?(null):parent.q(id);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void   load()
	{
		writeLock.lock();

		try
		{
			//?: {do reload}
			if(queries != null)
			{
				queries = null;

				if(parent != null)
					parent.load();
			}

			queries = new HashMap<>(17);

			//?: {has no file} do nothing
			if(file == null) return;

			//~: invoke the reader
			try
			{
				new QueriesReader(queries).process(file.toString());
			}
			catch(Throwable e)
			{
				throw EX.wrap(e, "Error while processing ",
				  "queries file [", file, "]!");
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}


	/* Queries Reader */

	public static class QueriesReader extends SaxProcessor<Object>
	{
		public QueriesReader(Map<String, String> queries)
		{
			this.queries = EX.assertn(queries);
		}

		public final Map<String, String> queries;


		/* SAX Processor */

		protected void createState()
		{}

		protected void open()
		{}

		protected void close()
		{
			//?: <query>
			if(istag(1, "query"))
			{
				String id = EX.asserts(attr("id"));
				String  q = EX.assertn(event().text().trim());

				queries.put(id, q);
			}
		}
	}


	/* private: the state of the cache */

	private final QueryCache     parent;
	private final URL            file;
	private Map<String, String>  queries;
	private final Lock           readLock;
	private final Lock           writeLock;
}