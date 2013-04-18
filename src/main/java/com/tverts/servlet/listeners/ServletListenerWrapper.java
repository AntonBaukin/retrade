package com.tverts.servlet.listeners;

/* standard Java classes */

import java.lang.reflect.Method;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

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
                      ServletRequestListener,
                      HttpSessionListener,
                      ServletRequestAttributeListener,
                      HttpSessionAttributeListener
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

			//?: {not for context processing}
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

			//?: {not for context processing}
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


	/* public: HttpSessionListener interface */

	public void sessionCreated(HttpSessionEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for session processing}
			if(!iss) return;

			//?: {call by the interface}
			if(sic)
				((HttpSessionListener)listener).sessionCreated(event);
			//!: call by the reflection
			else try
			{
				scm.invoke(listener, event);
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

	public void sessionDestroyed(HttpSessionEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for session processing}
			if(!iss) return;

			//?: {call by the interface}
			if(sic)
				((HttpSessionListener)listener).sessionDestroyed(event);
			//!: call by the reflection
			else try
			{
				sdm.invoke(listener, event);
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


	/* public: ServletRequestAttributeListener interface */

	public void attributeAdded(ServletRequestAttributeEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request attribute processing}
			if(!isra) return;

			//?: {call by the interface}
			if(raic)
				((ServletRequestAttributeListener)listener).attributeAdded(event);
			//!: call by the reflection
			else try
			{
				raam.invoke(listener, event);
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

	public void attributeRemoved(ServletRequestAttributeEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request attribute processing}
			if(!isra) return;

			//?: {call by the interface}
			if(raic)
				((ServletRequestAttributeListener)listener).attributeRemoved(event);
			//!: call by the reflection
			else try
			{
				radm.invoke(listener, event);
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

	public void attributeReplaced(ServletRequestAttributeEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for request attribute processing}
			if(!isra) return;

			//?: {call by the interface}
			if(raic)
				((ServletRequestAttributeListener)listener).attributeReplaced(event);
			//!: call by the reflection
			else try
			{
				rarm.invoke(listener, event);
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


	/* public: HttpSessionAttributeListener interface */

	public void attributeAdded(HttpSessionBindingEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for session attribute processing}
			if(!issa) return;

			//?: {call by the interface}
			if(saic)
				((HttpSessionAttributeListener)listener).attributeAdded(event);
			//!: call by the reflection
			else try
			{
				saam.invoke(listener, event);
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

	public void attributeRemoved(HttpSessionBindingEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for session attribute processing}
			if(!issa) return;

			//?: {call by the interface}
			if(saic)
				((HttpSessionAttributeListener)listener).attributeRemoved(event);
			//!: call by the reflection
			else try
			{
				sadm.invoke(listener, event);
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

	public void attributeReplaced(HttpSessionBindingEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: ensure the listener
			ensureListener();

			//?: {not for session attribute processing}
			if(!issa) return;

			//?: {call by the interface}
			if(saic)
				((HttpSessionAttributeListener)listener).attributeReplaced(event);
			//!: call by the reflection
			else try
			{
				sarm.invoke(listener, event);
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

				isc = (cim != null) & (cdm != null);
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

				isr = (rim != null) & (rdm != null);
			}
			catch(NoSuchMethodException e)
			{}


			//~: detect session abilities
			iss = sic = (listener instanceof HttpSessionListener);

			if(!iss) try
			{
				scm = listener.getClass().
				  getMethod("sessionCreated", HttpSessionEvent.class);

				sdm = listener.getClass().
				  getMethod("sessionDestroyed", HttpSessionEvent.class);

				iss = (scm != null) & (sdm != null);
			}
			catch(NoSuchMethodException e)
			{}


			//~: request attributes abilities
			isra = raic = (listener instanceof ServletRequestAttributeListener);

			if(!isra) try
			{
				raam = listener.getClass().
				  getMethod("attributeAdded", ServletRequestAttributeEvent.class);

				radm = listener.getClass().
				  getMethod("attributeRemoved", ServletRequestAttributeEvent.class);

				rarm = listener.getClass().
				  getMethod("attributeReplaced", ServletRequestAttributeEvent.class);

				isra = (raam != null) & (radm != null) & (rarm != null);
			}
			catch(NoSuchMethodException e)
			{}


			//~: session attributes abilities
			issa = saic = (listener instanceof HttpSessionAttributeListener);

			if(!issa) try
			{
				saam = listener.getClass().
				  getMethod("attributeAdded", HttpSessionBindingEvent.class);

				sadm = listener.getClass().
				  getMethod("attributeRemoved", HttpSessionBindingEvent.class);

				sarm = listener.getClass().
				  getMethod("attributeReplaced", HttpSessionBindingEvent.class);

				issa = (saam != null) & (sadm != null) & (sarm != null);
			}
			catch(NoSuchMethodException e)
			{}
		}
	}


	/* private: listener instance */

	private volatile Object  listener;

	//~: ServletContextListener
	private volatile boolean isc;  //<-- is context listened
	private volatile boolean cic;  //<-- direct interface
	private volatile Method  cim;  //<-- init method
	private volatile Method  cdm;  //<-- destroy method

	//~: ServletRequestListener
	private volatile boolean isr;  //<-- is request listened
	private volatile boolean ric;  //<-- direct interface
	private volatile Method  rim;  //<-- init method
	private volatile Method  rdm;  //<-- destroy method

	//~: HttpSessionListener
	private volatile boolean iss;  //<-- is session listened
	private volatile boolean sic;  //<-- direct interface
	private volatile Method  scm;  //<-- create method
	private volatile Method  sdm;  //<-- destroy method

	//~: ServletRequestAttributeListener
	private volatile boolean isra; //<-- is request attribute listened
	private volatile boolean raic; //<-- direct interface
	private volatile Method  raam; //<-- added method
	private volatile Method  radm; //<-- deleted method
	private volatile Method  rarm; //<-- replaced method


	//~: HttpSessionAttributeListener
	private volatile boolean issa; //<-- is session attribute listened
	private volatile boolean saic; //<-- direct interface
	private volatile Method  saam; //<-- added method
	private volatile Method  sadm; //<-- deleted method
	private volatile Method  sarm; //<-- replaced method
}