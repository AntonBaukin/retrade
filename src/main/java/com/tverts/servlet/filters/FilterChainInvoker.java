package com.tverts.servlet.filters;

/* Java Servlet api */

import javax.servlet.FilterChain;

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
			task.setBreaked();
		}
	}

	public void closeFilter(FilterTask task)
	{}

	/* protected: the chain */

	protected final FilterChain filterChain;
}