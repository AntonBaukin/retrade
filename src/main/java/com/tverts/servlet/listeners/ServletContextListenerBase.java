package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;

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
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			this.event = sce;
			this.init();
			this.event = null;
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			this.event = sce;
			this.destroy();
			this.event = null;
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
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