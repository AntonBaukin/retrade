package com.tverts.servlet.go;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterTask;


/**
 * Forwards the request to JSP page
 * if such exists. Tries to forward
 * initial go-requests.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GoServerPages extends GoPageFilterBase
{
	/* protected: GoFilterBase interface */

	protected boolean isExactURI(String uri)
	{
		return uri.endsWith(".jsp") || uri.endsWith(".jspf");
	}

	protected boolean varForward(FilterTask task, String page)
	{
		//~: try page
		if(runForward(task, page + ".jsp"))
			return true;

		//~: try fragment
		return runForward(task, page + ".jspf");
	}
}