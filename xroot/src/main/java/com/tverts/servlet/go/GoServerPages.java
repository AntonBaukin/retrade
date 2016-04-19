package com.tverts.servlet.go;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.PickFilter;
import org.springframework.stereotype.Component;


/**
 * Forwards the request to JSP page if such exists.
 * Tries to forward initial go-requests.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 5010 })
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