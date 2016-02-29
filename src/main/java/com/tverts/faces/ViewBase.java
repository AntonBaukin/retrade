package com.tverts.faces;

/* com.tverts: servlet */

import com.tverts.servlet.RequestPoint;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Abstraction of a Faces View (back bean).
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class ViewBase
{
	/* Phony Fields */

	public boolean  isPhonyBoolTrue()
	{
		return true;
	}

	public void     setPhonyBoolTrue(boolean phonyBoolTrue)
	{}

	public boolean  isPhonyBoolFalse()
	{
		return false;
	}

	public void     setPhonyBoolFalse(boolean phonyBoolFalse)
	{}

	public String   getPhonyString()
	{
		return "";
	}

	public void     setPhonyString(String s)
	{}


	/* Request Access */

	public String   getParam(String name)
	{
		return SU.s2s(RequestPoint.param(name));
	}

	public String[] getParams(String name)
	{
		return RequestPoint.params(name);
	}

	/**
	 * Returns web context related URL (starts with '/')
	 * to the page current request was issued for.
	 * It is already URL-encoded.
	 */
	public String   getRequestURI()
	{
		return RequestPoint.response().encodeURL(
		  RequestPoint.request(0).getRequestURI());
	}
}