package com.tverts.jsx.pages;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.PickFilter;
import com.tverts.servlet.go.GoDispatch;
import com.tverts.servlet.go.GoPageFilterBase;

/* com.tverts: scripting */

import com.tverts.jsx.JsX;


/**
 * Forwards go-requests processing to {@link JsServlet}.
 *
 * @author anton.baukin@gmail.com.
 */
@PickFilter(order = { 5015 })
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