package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * Class implements a composite listener.
 * It is also a Spring Bean.
 *
 * When initializing a request, the listeners 
 * of all the composites (a tree of listeners)
 * are invoked in-depth.
 *
 * When deinitializing the request the listeners 
 * are invoked in the reversed order.
 *
 * This class is thread-safe, and all the listeners
 * of the composite must also be thread-safe.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      ServletRequestListenerBean
       implements ServletRequestListener
{
	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent sre)
	{
		for(ServletRequestListener scl : obtainListeners())
			scl.requestInitialized(sre);
	}

	public void requestDestroyed(ServletRequestEvent sre)
	{
		ListIterator<ServletRequestListener> i =
		  obtainListeners().listIterator(obtainListeners().size());

		while(i.hasPrevious())
			i.previous().requestDestroyed(sre);
	}

	/* public: Bean interface */

	/**
	 * A read-only list of the listeners registered.
	 */
	public List<ServletRequestListener>
	            getListeners()
	{
		return Collections.unmodifiableList(obtainListeners());
	}

	public void setListeners(List<ServletRequestListener> listeners)
	{
		this.listeners = (listeners == null)?(null):
		  (new ArrayList<ServletRequestListener>(listeners));
	}

	/* protected: listeners access */

	protected List<ServletRequestListener> obtainListeners()
	{
		return (listeners != null)?(listeners):
		  (listeners = createListeners());
	}

	protected List<ServletRequestListener> createListeners()
	{
		return new ArrayList<ServletRequestListener>(4);
	}

	/* private: the listeners */

	private List<ServletRequestListener> listeners;
}