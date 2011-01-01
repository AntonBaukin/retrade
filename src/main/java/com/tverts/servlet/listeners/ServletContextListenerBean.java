package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.List;
import java.util.ListIterator;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: objects */

import com.tverts.objects.FixedObjectsRedirector;

/**
 * Class implements a composite listener.
 * It is also a Spring Bean.
 *
 * When initializing ServletContext, the listeners 
 * of all the composites (a tree of listeners)
 * are invoked in-depth.
 *
 * When deinitializing the context the listeners 
 * are invoked in the reversed order.
 *
 * This class is not thread-safe. As a part of
 * initialization structure, it must be invoked
 * only within the thread starting web application.
 *
 * @author anton baukin (abaukin@mail.ru) 
 */
public class      ServletContextListenerBean
       extends    FixedObjectsRedirector<ServletContextListener>
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		for(ServletContextListener scl : dereferObjects())
			scl.contextInitialized(sce);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		List<ServletContextListener>         l =
		  dereferObjects();
		ListIterator<ServletContextListener> i =
		  l.listIterator(l.size());

		while(i.hasPrevious())
			i.previous().contextDestroyed(sce);
	}
}