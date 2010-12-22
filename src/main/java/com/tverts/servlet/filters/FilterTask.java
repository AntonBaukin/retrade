package com.tverts.servlet.filters;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FilterTask
{
	/* public: FilterTask interface */

	public FilterStage getFilterStage();

	public HttpServletRequest
	                   getRequest();

	public void        setRequest(HttpServletRequest request);

	public HttpServletResponse
	                   getResponse();

	public void        setResponse(HttpServletResponse response);

	public boolean     isBreaked();

	public void        setBreaked();

	public Throwable   getError();

	public void        setError(Throwable error);

	public void        continueCycle();
}