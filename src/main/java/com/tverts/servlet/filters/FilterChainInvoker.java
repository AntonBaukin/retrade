package com.tverts.servlet.filters;

/* Java Servlet api */

import javax.servlet.FilterChain;

/**
 * Terminal filter that invokes Servlet
 * filter chain thus continuing request
 * processing.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class FilterChainInvoker implements Filter
{
	/* public: constructor */

	public FilterChainInvoker(FilterChain filterChain)
	{
		if(filterChain == null)
			throw new IllegalArgumentException();
		this.filterChain = filterChain;
	}


	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		try
		{
			filterChain.doFilter(
			  task.getRequest(), task.getResponse());
		}
		catch(Throwable e)
		{
			task.setError(e);
			task.doBreak();
		}
	}

	public void closeFilter(FilterTask task)
	{}


	/* protected: the chain */

	protected final FilterChain filterChain;
}