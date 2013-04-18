package com.tverts.faces.system;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;

/* com.tverts: servlets */

import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;


/**
 * Invokes JavaServer Faces listeners
 * {@link FacesConfigureListener}, and
 * {@link WebappLifecycleListener}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class FacesRequestListenerFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		//~: create event
		ServletRequestEvent event = new ServletRequestEvent(
		  task.getRequest().getServletContext(),
		  task.getRequest()
		);

		//~: invoke configure listener
		FacesConfigureListener.INSTANCE.
		  requestInitializeCall(event);

		//~: invoke lifecycle listener
		WebappLifecycleListener.INSTANCE.
		  requestInitializeCall(event);
	}

	public void closeFilter(FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		//~: create event
		ServletRequestEvent event = new ServletRequestEvent(
		  task.getRequest().getServletContext(),
		  task.getRequest()
		);

		//~: invoke configure listener
		FacesConfigureListener.INSTANCE.
		  requestDestroyCall(event);

		//~: invoke lifecycle listener
		WebappLifecycleListener.INSTANCE.
		  requestDestroyCall(event);
	}
}