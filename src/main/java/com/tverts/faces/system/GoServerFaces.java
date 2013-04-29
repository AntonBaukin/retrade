package com.tverts.faces.system;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;

/* com.tverts: servlets */

import com.tverts.servlet.filters.FilterTask;
import com.tverts.servlet.filters.GoPageFilterBase;


/**
 * Invokes JavaServer Faces listeners
 * {@link FacesConfigureListener}, and
 * {@link WebappLifecycleListener}.
 *
 * Forwards the request to xhtml page
 * if such exists. Tries to forward
 * initial go-requests.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GoServerFaces extends GoPageFilterBase
{
	/* public: Filter interface */

	public void       closeFilter(FilterTask task)
	{
		//?: {close faces scope}
		if(isScopeMatch(task, false))
			scopeFaces(task, false);
	}


	/* protected: GoFilterBase interface */

	protected boolean isExactURI(String uri)
	{
		return uri.endsWith(".xhtml");
	}

	protected boolean varForward(FilterTask task, String page)
	{
		return runForward(task, page + ".xhtml");
	}

	protected void    runExactMatch(FilterTask task)
	{
		//?: {open faces scope}
		if(isScopeMatch(task, true))
			scopeFaces(task, true);
	}

	protected boolean runForward(FilterTask task, String page)
	{
		//?: {open faces scope}
		if(isScopeMatch(task, true))
			scopeFaces(task, true);

		return super.runForward(task, page);
	}


	/* protected: faces listeners invocation */

	protected static final String ATTR_OPENED   =
	  GoServerFaces.class.getName() + ": opened";

	protected boolean isScopeMatch(FilterTask task, boolean open)
	{
		Boolean opened = this.opened.get();

		//?: {open when not opened}
		if(open && !Boolean.TRUE.equals(opened))
		{
			this.opened.set(Boolean.TRUE);
			task.getRequest().setAttribute(ATTR_OPENED, true);

			return true;
		}

		//?: {close when opened in this request}
		if(!open && Boolean.TRUE.equals(opened))
			if(Boolean.TRUE.equals(task.getRequest().getAttribute(ATTR_OPENED)))
			{
				this.opened.remove();
				return true;
			}

		return false;
	}

	protected void    scopeFaces(FilterTask task, boolean open)
	{
		//~: create event
		ServletRequestEvent event = new ServletRequestEvent(
		  task.getRequest().getServletContext(),
		  task.getRequest()
		);

		if(open)
		{
			//~: invoke configure listener
			FacesConfigureListener.INSTANCE.
			  requestInitializeCall(event);

			//~: invoke lifecycle listener
			WebappLifecycleListener.INSTANCE.
			  requestInitializeCall(event);
		}
		else
		{
			//~: invoke configure listener
			FacesConfigureListener.INSTANCE.
			  requestDestroyCall(event);

			//~: invoke lifecycle listener
			WebappLifecycleListener.INSTANCE.
			  requestDestroyCall(event);
		}
	}


	/* filter thread state */

	/**
	 * Thread local flag of open-close operations.
	 */
	protected final ThreadLocal<Boolean> opened =
	  new ThreadLocal<Boolean>();
}