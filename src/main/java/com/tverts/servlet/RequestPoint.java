package com.tverts.servlet;

/* Java Servlet api */

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Provides access to the pending HTTP request environment.
 *
 * @author anton.baukin@gmail.com
 */
public class RequestPoint
{
	/* public static: RequestPoint interface  */

	public static ServletContext
	                   context()
	{
		if(context == null)
			throw new IllegalStateException(NO_WEB_ERR);
		return context;
	}

	/**
	 * Note that is not allowed to change the context
	 * instance directly. Set {@code null} link before.
	 */
	public static void setContext(ServletContext ctx)
	{
		if((context != null) && (ctx != null))
			throw new IllegalStateException();
		context = ctx;
	}

	public static HttpSession
	                   session()
	{
		return request().getSession();
	}

	/**
	 * Returns the root request. Raises exception if
	 * the thread is not bound to a the pending request.
	 */
	public static HttpServletRequest
	                   request()
	  throws IllegalStateException
	{
		HttpServletRequest res = request.get();

		if(res == null)
			throw new IllegalStateException(NO_WEB_ERR);
		return res;
	}

	public static HttpServletRequest
	                   requestOrNull()
	{
		return request.get();
	}

	public static void setRootRequest(HttpServletRequest request)
	{
		if(request != null)
			RequestPoint.request.set(request);
		else
			RequestPoint.request.remove();
	}

	protected static String NO_WEB_ERR =
	  "The thread is not bound to the HTTP request!";

	/**
	 * Returns wrapper object to HTTP response object
	 * of current HTTP request.
	 */
	public static ResponseWrapper
	                   response()
	{
		ResponseWrapper res = response.get();

		if(res == null)
			throw new IllegalStateException(NO_WEB_ERR);
		return res;
	}

	public static ResponseWrapper
	                   responseOrNull()
	{
		return response.get();
	}

	public static void setResponse(HttpServletResponse response)
	{
		if(response != null)
			RequestPoint.response.set(new ResponseWrapper(response));
		else
			RequestPoint.response.remove();
	}

	/* private: requests thread local */

	private static ServletContext                  context;

	private static ThreadLocal<HttpServletRequest> request =
	  new ThreadLocal<HttpServletRequest>();

	private static ThreadLocal<ResponseWrapper>    response =
	  new ThreadLocal<ResponseWrapper>();
}