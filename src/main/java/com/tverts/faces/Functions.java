package com.tverts.faces;

/* Java Servlet api */

import javax.servlet.http.HttpServletRequest;

/* com.tverts: servlet */

import com.tverts.servlet.RequestPoint;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Java Server Faces functions.
 *
 * @author anton.baukin@gmail.com
 */
public class Functions
{
	/* public: HTML */

	/**
	 * Wraps the text given into CDATA section.
	 * If argument is {@code null}, writes
	 * '<![CDATA[' string.
	 */
	public static String cdata(Object s)
	{
		if(s == null) return "<![CDATA[";

		String x = s.toString();

		return new StringBuilder(x.length() + 12).
		  append("<![CDATA[").append(x).append("]]>").
		  toString();
	}


	/* public: URLs creators */

	public static String encodePath(String path)
	{
		String        cpath = RequestPoint.request().
		  getContextPath();
		StringBuilder res   = new StringBuilder(
		  cpath.length() + path.length() + 1);

		res.append(cpath);
		if((path.length() != 0) && (path.charAt(0) != '/'))
			res.append('/');
		res.append(path);

		return (path.length() == 0)?(res.toString())
		  :(RequestPoint.response().encodeURL(res.toString()));
	}

	public static String absoluteURL(String path)
	{
		HttpServletRequest request = RequestPoint.request();
		String             scheme  = request.getScheme();
		String             host    = request.getServerName();
		String             cpath   = request.getContextPath();
		int                port    = request.getServerPort();

		if((scheme == null) || "undefined".equalsIgnoreCase(scheme))
			scheme = "http";

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

		return result.toString();
	}


	/* public: Java Script support */

	/**
	 * Escapes string to place into Java Script
	 * source text. Note that XML entities
	 * are not encoded here, and you must
	 * protected XML text properly with CDATA
	 * sections.
	 */
	public static String escapeJSString(Object sobj)
	{
		return SU.escapeJSString(sobj);
	}


	/* public: model views support  */

	public static String genViewId(ModelView v, String name)
	{
		return new StringBuilder(v.getId().length() + name.length() + 1).
		  append(v.getId()).append('_').append(name).
		  toString();
	}
}