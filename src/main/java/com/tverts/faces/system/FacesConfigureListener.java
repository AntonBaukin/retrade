package com.tverts.faces.system;

/* standard Java classes */

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* JavaServer Faces */

import javax.faces.FactoryFinder;
import com.sun.faces.config.ConfigureListener;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


/**
 * Starts Faces configuration.
 *
 * @author anton.baukin@gmail.com
 */
public class   FacesConfigureListener
       extends ServletListenerWrapper
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent event)
	{
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: call original configuration
			super.contextInitialized(event);

			//~: attach to the system class loader
			//hackJSFactoryManager();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}


	/* protected: ServletListenerWrapper interface */

	protected Class getListenerClass()
	{
		return ConfigureListener.class;
	}


	/* private: JSF implementation hack */

//	@SuppressWarnings("unchecked")
//	private void hackJSFactoryManager()
//	  throws Exception
//	{
//		//~: get factories cache
//		Field f = FactoryFinder.class.getDeclaredField("FACTORIES_CACHE");
//		if(f == null) throw new IllegalStateException();
//		f.setAccessible(true);
//		Object fcache = f.get(null); //<-- static field
//		if(fcache == null) throw new IllegalStateException();
//
//		//~: get application map
//		f = fcache.getClass().getDeclaredField("applicationMap");
//		if(f == null) throw new IllegalStateException();
//		f.setAccessible(true);
//		Object amap = f.get(fcache);
//		if(!(amap instanceof Map))
//			throw new IllegalStateException();
//
//		//~: search for factory with parent class loader
//		ClassLoader pcl = SystemClassLoader.INSTANCE.getParent();
//
//		for(Object e : ((Map)amap).entrySet())
//		{
//			Object k = ((Entry)e).getKey();
//			f = k.getClass().getDeclaredField("cl");
//		}
//
//	}
}