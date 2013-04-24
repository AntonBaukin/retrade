package com.tverts.servlet.filters;

/* standard Java classes */

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Go-Dispatcher for one go-filter instance
 * that caches whether the page was successfully
 * dispatched the request.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GoDisperCached extends GoDisperBase
{
	/* public: Go-Dispatcher interface */

	public String    isGoRequest(FilterTask task)
	{
		String res = super.isGoRequest(task);

		//?: {not a go-request}
		if(res == null) return null;

		//?: {not certain} lookup
		if(res.charAt(0) == '?')
		{
			String x = get(task);

			//?: {not known}
			if(x == null) return res;

			//?: {not that}
			if(x.isEmpty())
				return null;

			return x;
		}

		return res;
	}

	public boolean   dispatch(FilterTask task, String page)
	{
		//~: do dispatch
		boolean res = super.dispatch(task, page);

		//~: cache
		put(task, (res)?(page):(null));

		return res;
	}


	/* public: Go-Dispatcher Cached (bean) interface */

	public int       getSize()
	{
		return size;
	}

	public void      setSize(int size)
	{
		if(size <= 0) throw new IllegalArgumentException();
		this.size = size;
	}


	/* protected: caching */

	@SuppressWarnings("unchecked")
	protected String get(FilterTask task)
	{
		String p = task.getRequest().getRequestURI();
		Object x;

		synchronized(this)
		{
			//~: get the cache map
			Map m = (Map)((cache == null)?(null):(cache.get()));
			if(m == null) return null;

			//~: lookup
			x = m.get(p);
		}

		//?: {not found}
		if(x == null) return null;

		//?: {not that}
		if(Boolean.FALSE.equals(x))
			return "";

		//!: found
		return (String)x;
	}

	@SuppressWarnings("unchecked")
	protected void   put(FilterTask task, String page)
	{
		String p = task.getRequest().getRequestURI();

		synchronized(this)
		{
			//~: get the cache map
			Map m = (Map)((cache == null)?(null):(cache.get()));
			if(m == null)
				cache = new SoftReference(m = new HashMap(getSize()));

			//?: {got the limit}
			if(m.size() > getSize())
				m.remove(m.keySet().iterator().next());

			//~: put
			m.put(p, (page != null)?(page):Boolean.FALSE);
		}
	}


	/* private: the cache map */

	private SoftReference cache;
	private int           size = 101;
}