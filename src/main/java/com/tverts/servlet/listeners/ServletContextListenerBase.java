package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextListener;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;

/**
 * Combines listener with self referencing object.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServletContextListenerBase
       implements     ServletContextListener,
                      ObjectsReference<ServletContextListener>
{
	/* public: ObjectsReference interface */

	public List<ServletContextListener> dereferObjects()
	{
		return Collections.<ServletContextListener>singletonList(this);
	}


	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		this.event = sce;
		this.init();
		this.event = null;
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		this.event = sce;
		this.destroy();
		this.event = null;
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