package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		for(ServletContextListener scl : obtainListeners())
			scl.contextInitialized(sce);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		ListIterator<ServletContextListener> i =
		  obtainListeners().listIterator(obtainListeners().size());

		while(i.hasPrevious())
			i.previous().contextDestroyed(sce);
	}

	/* public: Bean interface */

	/**
	 * A read-only list of the listeners registered.
	 */
	public List<ServletContextListener>
	            getListeners()
	{
		return Collections.unmodifiableList(obtainListeners());
	}

	public void setListeners(List<ServletContextListener> listeners)
	{
		this.listeners = (listeners == null)?(null):
		  (new ArrayList<ServletContextListener>(listeners));
	}

	/* protected: listeners access */

	protected List<ServletContextListener> obtainListeners()
	{
		return (listeners != null)?(listeners):
		  (listeners = createListeners());
	}

	protected List<ServletContextListener> createListeners()
	{
		return new ArrayList<ServletContextListener>(4);
	}

	/* private: the listeners */

	private List<ServletContextListener> listeners;
}