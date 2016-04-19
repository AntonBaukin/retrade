package com.tverts.faces.system;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.PickFilter;
import com.tverts.servlet.go.GoPageFilterBase;


/**
 * Forwards the request to xhtml page if such exists.
 * Tries to forward initial go-requests.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 5005 })
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