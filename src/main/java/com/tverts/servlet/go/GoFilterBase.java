package com.tverts.servlet.go;

/* Java Servlet */

import javax.servlet.RequestDispatcher;

/* com.tverts: servlet */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Implementation base for go-filter that
 * just replaces the URI of the request
 * with some else URI, and then does
 * forwarding.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GoFilterBase extends FilterBase
{
	/* Filter Base */

	public void openFilter(FilterTask task)
	{
		//~: request the target page
		String page = getGoPage(task);

		//?: {got it} go to
		if(!SU.sXe(page))
			goPage(task, page);
	}

	public void closeFilter(FilterTask task)
	{}


	/* protected: go-operation */

	/**
	 * Returns the page to go, or null if this filter
	 * doesn't know the source path. Note that the
	 * context path is excluded from it.
	 */
	protected String getGoPage(String path)
	{
		return null;
	}

	protected String getGoPage(FilterTask task)
	{
		return getGoPage(task.getRequest().getRequestURI().
		  substring(task.getRequest().getContextPath().length())
		);
	}

	protected void   goPage(FilterTask task, String page)
	{
		try
		{
			//~: finish go-filtering
			task.doBreak();

			//~: create request dispatcher
			RequestDispatcher d = EX.assertn(
			  task.getRequest().getRequestDispatcher(page),
			  "Can not go to the page [", page, "]!", page
			);

			//?: {include request}
			if(FilterStage.INCLUDE.equals(task.getFilterStage()))
				d.include(task.getRequest(), task.getResponse());
			//!: else -> forward
			else
				d.forward(task.getRequest(), task.getResponse());
		}
		catch(Throwable e)
		{
			throw EX.wrap(EX.xrt(e));
		}
	}
}