package com.tverts.servlet.filters;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Aggregates all the parameters of {@link Filter}
 * invocation. Also has internal reference to the
 * algorithm of filters invocation cycle.
 *
 * @author anton.baukin@gmail.com
 */
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

	/**
	 * Tells whether the cycle was breaked. Breaking
	 * may not be cancelled.
	 */
	public boolean     isBreaked();

	/**
	 * Breaks the cycle. Has meaning only in
	 * {@link Filter#openFilter(FilterTask)} method.
	 *
	 * If the filter sets or raises an exception,
	 * the cycle is automatically breaked.
	 */
	public void        setBreaked();

	/**
	 * Returns the exception saved (or raised) in the task
	 * by one of the filters of the cycle.
	 */
	public Throwable   getError();

	public void        setError(Throwable error);

	/**
	 * Call this method to continue the cycle from
	 * {@link Filter#openFilter(FilterTask)} without
	 * exiting the method. The call may be done indirectly.
	 *
	 * Allows to nest invocation context to create
	 * transaction scopes and else needs.
	 */
	public void        continueCycle();
}