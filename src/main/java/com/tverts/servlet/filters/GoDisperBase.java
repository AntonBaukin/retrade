package com.tverts.servlet.filters;

/* standard Java classes */

import java.io.File;

/* Java Servlet api */

import javax.servlet.RequestDispatcher;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Implementation base for go-dispatchers.
 *
 * @author anton.baukin@gmail.com
 */
public class GoDisperBase implements GoDisper
{
	/* public: Go-Dispatcher interface */

	public String     isGoRequest(FilterTask task)
	{
		String page = task.getRequest().getRequestURI();

		//?: {not a go-request}
		if(!page.startsWith(getGoPrefix(task)))
			return null;

		return preparePage(page, task);
	}

	public boolean    dispatch(FilterTask task, String page)
	{
		//?: {the page doesn't exist}
		if(!isPageExists(page, task))
			return false;

		try
		{
			//~: create request dispatcher
			RequestDispatcher d = task.getRequest().
			  getRequestDispatcher(page);

			if(d == null)
				return false;

			//~: set cache options
			cacheControl(task);

			//!: forward
			d.forward(task.getRequest(), task.getResponse());
		}
		catch(Throwable e)
		{
			throw new RuntimeException(EX.xrt(e));
		}

		return true;
	}


	/* protected: dispatching support */

	protected String  getGoPrefix(FilterTask task)
	{
		if(goPrefix == null)
			goPrefix = task.getRequest().getContextPath() + "/go/";
		return goPrefix;
	}

	protected String  preparePage(String page, FilterTask task)
	{
		StringBuilder p = new StringBuilder(page.length() + 1);
		String        g = getGoPrefix(task);

		p.append('?').append(page);
		p.delete(1, (g.charAt(0) == '/')?(g.length()):(g.length() + 1));
		if(p.charAt(1) != '/') p.insert(1, '/');

		return p.toString();
	}

	protected boolean isPageExists(String page, FilterTask task)
	{
		//~: get the path in the file system
		String path = task.getRequest().getServletContext().getRealPath(page);
		if(path == null) return false;

		//~: check the file exists
		File   file = new File(path);
		return file.exists() && file.isFile();
	}

	protected void    cacheControl(FilterTask task)
	{
		if(!task.getResponse().containsHeader("Cache-Control"))
		{
			task.getResponse().addHeader("Cache-Control", "no-cache, max-age=0");
			task.getResponse().addHeader("Expires", "0");
		}
	}


	/* private: filter state */

	private volatile String goPrefix;
}