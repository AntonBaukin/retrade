package com.tverts.servlet;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * HTTP request handling support.
 *
 * @author anton.baukin@gmail.com
 */
public class REQ
{
	/* request properties */

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

	public static boolean isLocalhost()
	{
		return isLocalhost(RequestPoint.request());
	}

	/**
	 * Returns the substring from the request URI where
	 * the context path prefix is removed, and the
	 * characters till the path parameters or the query
	 * parameters is left. The string always starts with '/'.
	 */
	public static String  getRequestPath(HttpServletRequest r)
	{
		String uri = r.getRequestURI();
		if(uri.length() == 0) return "/";

		String ctx = r.getContextPath();

		//?: {this request is to the default context}
		if(ctx.length() == 0)
			return (uri.charAt(0) == '/')?(uri):("/" + uri);

		if((uri.charAt(0) != '/') && (ctx.charAt(0) == '/'))
			ctx = ctx.substring(1);

		if((uri.charAt(0) == '/') && (ctx.charAt(0) != '/'))
			uri = uri.substring(1);

		if(!uri.startsWith(ctx))
			return "/";

		uri = uri.substring(ctx.length());
		if(uri.length() == 0) return "/";

		if(uri.charAt(0) != '/')
			uri = "/" + uri;

		return uri;
	}

	public static String  getRequestPath()
	{
		return getRequestPath(RequestPoint.request());
	}

	public static boolean isGunZIPAllowed(HttpServletRequest r)
	{
		return SU.sXs(r.getHeader("Accept-Encoding")).toLowerCase().contains("gzip");
	}

	/**
	 * Adds the parameters to the given URL string
	 * without encoding it.
	 */
	public static String  param(String url, Object... nv)
	{
		StringBuilder x = new StringBuilder(url.length() + 64).append(url);

		//?: {has no '?'}
		if(url.indexOf('?') == -1)
			x.append('?');

		//?: {has parameters}
		if(x.charAt(x.length() - 1) != '?')
			x.append('&');

		//c: for each (name + value) pair
		for(int i = 0;(i < nv.length);i += 2)
		{
			EX.assertx(i + 1 < nv.length);
			if(i != 0) x.append('&');

			//~: parameter name
			EX.assertx(nv[i] instanceof CharSequence);
			x.append(nv[i]);

			//?: {has value}
			String v = (nv[i+1] == null)?(""):(SU.sXs(nv[i+1].toString()));
			if(!v.isEmpty()) x.append('=').append(v);
		}

		return x.toString();
	}
}