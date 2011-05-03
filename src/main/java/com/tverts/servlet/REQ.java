package com.tverts.servlet;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP request handling support.
 *
 * @author anton.baukin@gmail.com
 */
public class REQ
{
	/* public: find request properties */

	/**
	 * Tells whether the request comes from the local host.
	 */
	public static boolean isLocalhost(HttpServletRequest r)
	{
		String adr = r.getRemoteAddr();

		return
		  "127.0.0.1".equals(adr)                || //<-- IPv4, a
		  "::1".equals(adr)                      || //<-- IPv6, a
		  "0:0:0:0:0:0:0:1".equals(adr)          || //<-- IPv6, b
		  adr.matches("127(.\\d\\d?\\d?){3,3}");    //<-- IPv4, b
	}

	/**
	 * Returns the substring from the request URI where
	 * the context path prefix is removed, and the
	 * characters till the path parameters or the query
	 * parameters is left. The string always starts with '/'.
	 */
	public static  String getRequestPath(HttpServletRequest r)
	{
		String uri = r.getRequestURI();
		if(uri.length() == 0) return "/";

		String ctx = r.getContextPath();

		//?: {this request is to the default context}
		if(ctx.length() == 0)
			return (uri.charAt(0) == '/')?(uri):
			  new StringBuilder(uri.length() + 1).
			    append('/').append(uri).toString();

		if((uri.charAt(0) != '/') && (ctx.charAt(0) == '/'))
			ctx = ctx.substring(1);

		if((uri.charAt(0) == '/') && (ctx.charAt(0) != '/'))
			uri = uri.substring(1);

		if(!uri.startsWith(ctx))
			return "/";

		uri = uri.substring(ctx.length());
		if(uri.length() == 0) return "/";

		if(uri.charAt(0) != '/')
			uri = new StringBuilder(uri.length() + 1).
			  append('/').append(uri).toString();

		return uri;
	}
}