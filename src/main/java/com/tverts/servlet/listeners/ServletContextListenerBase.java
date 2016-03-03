package com.tverts.servlet.listeners;

/* Java */

import java.util.Collections;
import java.util.List;

/* Java Servlet */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;


/**
 * Combines listener with self referencing object.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServletContextListenerBase
       implements     ServletContextListener
{
	/* Servlet Context Listener */

	public void contextInitialized(ServletContextEvent sce)
	{
		try
		{
			this.event = sce;
			this.init();
		}
		finally
		{
			this.event = null;
		}
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		try
		{
			this.event = sce;
			this.destroy();
		}
		finally
		{
			this.event = null;
		}
	}


	/* protected: listening */

	protected abstract void       init();

	protected void                destroy()
	{}

	protected ServletContextEvent getEvent()
	{
		return this.event;
	}


	/* private: servlet context event */

	private ServletContextEvent event;
}