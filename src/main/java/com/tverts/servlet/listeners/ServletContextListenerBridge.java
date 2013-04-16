package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


/**
 * As a {@link ServletContextListener} class this
 * class must be registered in 'web.xml' file.
 * But only one time: in Servlet 2.5 it is not
 * possible to assign init parameters to listeners,
 * and this class is a singleton even when
 * several instances are created.
 *
 * Each instance of this class shares the same
 * root listener defined as the point:
 * {@link ServletContextListenerPoint}
 *
 * To register different listeners you must
 * create subclasses and refer other point.
 *
 *
 * @see {@link ServletContextListenerBean}
 *
 * @author anton.baukin@gmail.com
 */
public class      ServletContextListenerBridge
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			getListenerPoint().contextInitialized(sce);
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
			getListenerPoint().contextDestroyed(sce);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* protected: access point */

	protected ServletContextListener getListenerPoint()
	{
		return ServletContextListenerPoint.getInstance();
	}
}