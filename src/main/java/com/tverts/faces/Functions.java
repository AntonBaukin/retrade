package com.tverts.faces;

/* abaukin.com */

import com.tverts.servlet.RequestPoint;
import com.tverts.support.SU;

/**
 * Java Server Faces functions.
 *
 * @author anton baukin (abaukin@mail.ru)
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
		  append("<![CDATA[").
		  append(x).
		  append("]]>").
		  toString();
	}

	/* public: URLs creators */

	public static String addContextPath(String path)
	{
		String        cpath = RequestPoint.request().
		  getContextPath();
		StringBuilder res   = new StringBuilder(
		  cpath.length() + path.length() + 1);

		res.append(cpath);
		if((path.length() != 0) && (path.charAt(0) != '/'))
			res.append('/');
		res.append(path);

		//TODO addContextPath() encode URL after filters
//		return (path.length() == 0)?(res.toString())
//		  :(RequestPoint.response().encodeURL(res.toString()));
		return res.toString();
	}

	public static String encodeURL(String url)
	{
		return RequestPoint.response().encodeURL(url);
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
}