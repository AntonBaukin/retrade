package com.tverts.servlet.listeners;

/* standard Java classes */

import java.lang.reflect.Method;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


/**
 * Creates the listener defined by it's class
 * (using System class loader).
 *
 * Implements interfaces for the context
 * and the request listeners. Dynamically
 * selects what to invoke actually.
 *
 * Allows to call the listeners not implementing
 * the interfaces (via reflection), but both
 * the methods must be implemented.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServletListenerWrapper
       implements     ServletContextListener,
                      ServletRequestListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request processing}
			if(!isc) return;

			//?: {call by the interface}
			if(cic)
				((ServletContextListener)listener).contextInitialized(event);
			//!: call by the reflection
			else try
			{
				cim.invoke(listener, event);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void contextDestroyed(ServletContextEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request processing}
			if(!isc) return;

			//?: {call by the interface}
			if(cic)
				((ServletContextListener)listener).contextDestroyed(event);
			//!: call by the reflection
			else try
			{
				cdm.invoke(listener, event);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request processing}
			if(!isr) return;

			//?: {call by the interface}
			if(ric)
				((ServletRequestListener)listener).requestInitialized(event);
			//!: call by the reflection
			else try
			{
				rim.invoke(listener, event);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}

	public void requestDestroyed(ServletRequestEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{

			//~: ensure the listener
			ensureListener();

			//?: {not for request processing}
			if(!isr) return;

			//?: {call by the interface}
			if(ric)
				((ServletRequestListener)listener).requestDestroyed(event);
			//!: call by the reflection
			else try
			{
				rdm.invoke(listener, event);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* protected: listener access */

	protected abstract Class getListenerClass();

	protected Object         createListener()
	{
		try
		{
			return getListenerClass().newInstance();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(String.format(
			  "Can't create instance of Servlet Request Listener with " +
			  "class [%s]!", getListenerClass().getName()
			));
		}
	}

	protected void           ensureListener()
	{
		synchronized(this)
		{
			if(listener != null)
				return;

			//~: create the listener
			listener = createListener();

			//~: detect context abilities
			isc = cic = (listener instanceof ServletContextListener);

			if(!isc) try
			{
				cim = listener.getClass().
				  getMethod("contextInitialized", ServletContextEvent.class);

				cdm = listener.getClass().
				  getMethod("contextDestroyed", ServletContextEvent.class);

				isc = (cim != null) && (cdm != null);
			}
			catch(NoSuchMethodException e)
			{}


			//~: detect request abilities
			isr = ric = (listener instanceof ServletRequestListener);

			if(!isr) try
			{
				rim = listener.getClass().
				  getMethod("requestInitialized", ServletRequestEvent.class);

				rdm = listener.getClass().
				  getMethod("requestDestroyed", ServletRequestEvent.class);

				isr = (rim != null) && (rdm != null);
			}
			catch(NoSuchMethodException e)
			{}
		}
	}


	/* private: listener instance */

	private volatile Object  listener;

	//~: ServletContextListener
	private volatile boolean isc; //<-- is context listened
	private volatile boolean cic; //<-- direct interface
	private volatile Method  cim; //<-- init method
	private volatile Method  cdm; //<-- destroy method

	//~: ServletRequestListener
	private volatile boolean isr; //<-- is request listened
	private volatile boolean ric; //<-- direct interface
	private volatile Method  rim; //<-- init method
	private volatile Method  rdm; //<-- destroy method
}