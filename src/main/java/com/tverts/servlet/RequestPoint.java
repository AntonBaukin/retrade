package com.tverts.servlet;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;


/* Java Servlet api */

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Provides access to HTTP request environment.
 *
 * @author anton.baukin@gmail.com
 */
public class RequestPoint
{
	/* Singletone */

	public static final RequestPoint INSTANCE =
	  new RequestPoint();

	public static RequestPoint getInstance()
	{
		return INSTANCE;
	}


	/* static: RequestPoint (read) interface */

	public static final String NO_WEB_ERR =
	  "The thread is not bound to the HTTP request!";

	public static ServletContext     context()
	{
		if(INSTANCE.getContext() == null)
			throw new IllegalStateException(NO_WEB_ERR);
		return INSTANCE.getContext();
	}

	/**
	 * Returns the number of requests are in the stack.
	 */
	public static int                requests()
	{
		List<HttpServletRequest> r = INSTANCE.getRequests();
		return (r == null)?(0):(r.size());
	}

	/**
	 * Returns the number of responses are in the stack.
	 */
	public static int                responses()
	{
		List<ResponseWrapper> r = INSTANCE.getResponses();
		return (r == null)?(0):(r.size());
	}

	/**
	 * Returns the request on the top of the stack.
	 */
	public static HttpServletRequest request()
	{
		List<HttpServletRequest> r = INSTANCE.getRequests();

		if((r == null) || r.isEmpty())
			throw new IllegalStateException(NO_WEB_ERR);
		return r.get(r.size() - 1);
	}

	/**
	 * Returns the request in the given position
	 * of the stack. Index 0 is root request.
	 */
	public static HttpServletRequest request(int i)
	{
		List<HttpServletRequest> r = INSTANCE.getRequests();

		if((r == null) || r.isEmpty())
			throw new IllegalStateException(NO_WEB_ERR);
		return r.get(i);
	}

	public static HttpSession        session()
	{
		return request().getSession(false);
	}

	/**
	 * Returns the response wrapper
	 * on the top of the stack.
	 */
	public static ResponseWrapper    response()
	{
		List<ResponseWrapper> r = INSTANCE.getResponses();

		if((r == null) || r.isEmpty())
			throw new IllegalStateException(NO_WEB_ERR);
		return r.get(r.size() - 1);
	}

	/**
	 * Returns the request in the given position
	 * of the stack. Index 0 is root request.
	 */
	public static ResponseWrapper    response(int i)
	{
		List<ResponseWrapper> r = INSTANCE.getResponses();

		if((r == null) || r.isEmpty())
			throw new IllegalStateException(NO_WEB_ERR);
		return r.get(i);
	}


	/* public: RequestPoint (instance read) interface  */

	public ServletContext            getContext()
	{
		return context;
	}

	public List<HttpServletRequest>  getRequests()
	{
		return this.requests.get();
	}

	public List<ResponseWrapper>     getResponses()
	{
		return this.responses.get();
	}


	/* public: RequestPoint (instance write) interface  */

	/**
	 * Note that is not allowed to change the context
	 * instance directly. Set {@code null} link before.
	 */
	public void setContext(ServletContext ctx)
	{
		if((context != null) && (ctx != null))
			throw new IllegalStateException();
		context = ctx;
	}

	/**
	 * Puts the request into the stack.
	 * To pop request pass null.
	 */
	public void setRequest(HttpServletRequest request)
	{
		List<HttpServletRequest> requests = this.requests.get();

		//?: {pop request}
		if(request == null)
		{
			//?: {the stack is empty}
			if((requests == null) || requests.isEmpty())
				throw new IllegalStateException(NO_WEB_ERR);

			//!: pop
			requests.remove(requests.size() - 1);

			//?: {no more left} clear the local
			if(requests.isEmpty())
				this.requests.remove();
		}
		//!: put request
		else
		{
			//?: {the stack is empty}
			if(requests == null)
				this.requests.set(requests = new ArrayList<HttpServletRequest>(4));

			//!: put the request
			requests.add(request);
		}
	}

	/**
	 * Puts the response (wrapped) into the stack.
	 * To pop response pass null.
	 */
	public void setResponse(HttpServletResponse response)
	{
		List<ResponseWrapper> responses = this.responses.get();

		//?: {pop response}
		if(response == null)
		{
			//?: {the stack is empty}
			if((responses == null) || responses.isEmpty())
				throw new IllegalStateException(NO_WEB_ERR);

			//!: pop
			responses.remove(responses.size() - 1);

			//?: {no more left} clear the local
			if(responses.isEmpty())
				this.responses.remove();
		}
		//!: put response
		else
		{
			//?: {the stack is empty}
			if(responses == null)
				this.responses.set(responses = new ArrayList<ResponseWrapper>(4));

			//!: put the response wrapper
			responses.add(new ResponseWrapper(response));
		}
	}


	/* public static: utilities */

	public static String formAbsoluteURL(String path, boolean copy_get_query)
	{
		HttpServletRequest request = RequestPoint.request();
		String             scheme  = request.getScheme();
		String             host    = request.getServerName();
		String             cpath   = request.getContextPath();
		int                port    = request.getServerPort();

		if(scheme == null) scheme = "http";
		if(!"http".equals(scheme) && !"https".equals(scheme))
			scheme = request.isSecure()?("https"):("http");

		StringBuilder      result  = new StringBuilder(
		  scheme.length() + host.length() +
		  cpath.length()  + path.length() + 8
		);

		//<: deal with the ports

		if("http".equalsIgnoreCase(scheme)  && (port == 80 ))
			port = 0;

		if("https".equalsIgnoreCase(scheme) && (port == 443))
			port = 0;

		//>: deal with the ports

		//~: scheme
		result.append(scheme).append("://");

		//~: host
		result.append(host);

		//~: port
		if(port != 0)
			result.append(':').append(port);

		//~: context path
		result.append(cpath);

		//~: path
		if((path.length() != 0) && (path.charAt(0) != '/'))
			result.append('/');
		result.append(path);

		//~: add GET parameters
		if(copy_get_query)
		{
			String params = request.getQueryString();
			if(params != null)
				result.append('?').append(params);
		}

		return result.toString();
	}


	/* private: requests thread local */

	private ServletContext context;

	private ThreadLocal<List<HttpServletRequest>> requests  =
	  new ThreadLocal<List<HttpServletRequest>>();

	private ThreadLocal<List<ResponseWrapper>>    responses =
	  new ThreadLocal<List<ResponseWrapper>>();
}