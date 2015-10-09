package com.tverts.jsx.pages;

/* com.tverts: servlets */

import com.tverts.jsx.JsX;
import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.GoDispatch;
import com.tverts.servlet.filters.GoPageFilterBase;


/**
 * Forwards go-requests processing to {@link JsServlet}.
 *
 * @author anton.baukin@gmail.com.
 */
public class JsGoFilter extends GoPageFilterBase
{
	/* protected: GoFilterBase interface */

	protected boolean isExactURI(String uri)
	{
		return uri.endsWith(".jsx");
	}

	protected boolean varForward(FilterTask task, String page)
	{
		return runForward(task, page + ".jsx");
	}

	protected void    initGoDispatch(GoDispatch request)
	{
		request.setExists(this::isJsExists);
	}

	protected boolean isJsExists(GoDispatch request)
	{
		return JsX.INSTANCE.exists(request.page);
	}
}