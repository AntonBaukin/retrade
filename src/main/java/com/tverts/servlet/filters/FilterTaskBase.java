package com.tverts.servlet.filters;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Basic properties of a {@link FilterTask}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class FilterTaskBase implements FilterTask
{
	/* public: constructor */

	protected FilterTaskBase(FilterStage filterStage)
	{
		this.filterStage = filterStage;
	}

	/* public: FilterTask interface */

	public FilterStage getFilterStage()
	{
		return filterStage;
	}

	public HttpServletRequest
	                   getRequest()
	{
		return request;
	}

	public void        setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletResponse
	                   getResponse()
	{
		return response;
	}

	public void        setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public Throwable   getError()
	{
		return error;
	}

	public void        setError(Throwable error)
	{
		this.error = error;
	}

	public boolean     isBreaked()
	{
		return breaked;
	}

	public void        setBreaked()
	{
		this.breaked = true;
	}

	/* private: the state of the task */

	private FilterStage         filterStage;
	private HttpServletRequest  request;
	private HttpServletResponse response;
	private Throwable           error;
	private boolean             breaked;
}