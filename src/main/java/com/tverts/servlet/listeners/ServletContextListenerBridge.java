package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
		getListenerPoint().contextInitialized(sce);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		getListenerPoint().contextDestroyed(sce);
	}

	/* protected: access point */

	protected ServletContextListener getListenerPoint()
	{
		return ServletContextListenerPoint.getInstance();
	}
}