package com.tverts.servlet;

/* Java Servlet api */

import javax.servlet.http.HttpServletResponse;

/**
 * Provides access to a restricted subset of
 * HTTP response object.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class ResponseWrapper
{
	/* public: constructor */

	public ResponseWrapper(HttpServletResponse response)
	{
		this.response = response;
	}

	/* public: Request subset */

	public String  encodeURL(String url)
	{
		return getResponse().encodeURL(url);
	}

	public String  encodeRedirectURL(String url)
	{
		return getResponse().encodeRedirectURL(url);
	}

	public boolean containsHeader(String name)
	{
		return getResponse().containsHeader(name);
	}

	public String  getContentType()
	{
		return getResponse().getContentType();
	}

	public String  getCharacterEncoding()
	{
		return getResponse().getCharacterEncoding();
	}

	public java.util.Locale
	               getLocale()
	{
		return getResponse().getLocale();
	}

	/* protected: response access */

	protected final HttpServletResponse getResponse()
	{
		return response;
	}

	/* private: the response */

	private HttpServletResponse response;
}