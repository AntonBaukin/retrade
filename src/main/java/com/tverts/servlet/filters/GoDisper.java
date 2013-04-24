package com.tverts.servlet.filters;

/**
 * Dispatcher for go-requests.
 *
 * @author anton.baukin@gmail.com
 */
public interface GoDisper
{
	/* public: Go-Dispatcher interface */

	/**
	 * Tells whether the task given is actually
	 * a go-request for the filter given.
	 *
	 * Note that dispatcher may answer when it is not
	 * certain, and only dispatch operation would
	 * actually tell the dispatching took place.
	 *
	 * Returns null string, if this request is not
	 * for the filter given, or not a go-request.
	 *
	 * Returns string starting with '?' if the filter must
	 * try to form the actual page name from the result
	 * string (removing leading '?').
	 *
	 * Else, returns not empty string with exact match of
	 * the page to dispatch.
	 */
	public String  isGoRequest(FilterTask task);

	/**
	 * Tries dispatch
	 */
	public boolean dispatch(FilterTask task, String page);
}