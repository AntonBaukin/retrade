package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextListener;


/**
 * Creates the listener defined by it's class
 * (using System class loader).
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ContextListenerWrapper
       extends        ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		//?: {repeated call?}
		if(this.listener != null)
			throw new IllegalStateException();

		//~: create the listener
		this.listener = createListener();

		//~: initialize
		this.listener.contextInitialized(getEvent());
	}

	protected void destroy()
	{
		//?: {lost init instance} create again
		if(this.listener == null)
			this.listener = createListener();

		//~: destroy
		try
		{
			this.listener.contextDestroyed(getEvent());
		}
		finally
		{
			this.listener = null;
		}
	}


	/* protected: listener access */

	protected abstract Class<? extends ServletContextListener>
	                                 getListenerClass();

	protected ServletContextListener createListener()
	{
		try
		{
			return getListenerClass().newInstance();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(String.format(
			  "Can't create instance of Servlet Context Listener with " +
			  "class [%s]!", getListenerClass().getName()
			));
		}
	}


	/* private: listener instance */

	private ServletContextListener listener;
}