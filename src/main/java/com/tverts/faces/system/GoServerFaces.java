package com.tverts.faces.system;

/* com.tverts: servlets */

import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.GoPageFilterBase;


/**
 * Forwards the request to xhtml page if such exists.
 * Tries to forward initial go-requests.
 *
 * @author anton.baukin@gmail.com
 */
public class GoServerFaces extends GoPageFilterBase
{
	/* protected: GoFilterBase interface */

	protected boolean isExactURI(String uri)
	{
		return uri.endsWith(".xhtml");
	}

	protected boolean varForward(FilterTask task, String page)
	{
		return runForward(task, page + ".xhtml");
	}
}