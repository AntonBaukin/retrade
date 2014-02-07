package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.List;
import java.util.ListIterator;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/* com.tverts: objects */

import com.tverts.objects.FixedObjectsRedirector;

/**
 * Class implements a composite listener.
 * It is also a Spring Bean.
 *
 * When initializing a request, the listeners 
 * of all the composites (a tree of listeners)
 * are invoked in-depth.
 *
 * When de-initializing the request the listeners
 * are invoked in the reversed order.
 *
 * This class is thread-safe, and all the listeners
 * of the composite must also be thread-safe.
 *
 * @author anton.baukin@gmail.com
 */
public class      ServletRequestListenerBean
       extends    FixedObjectsRedirector<ServletRequestListener>
       implements ServletRequestListener
{
	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent sre)
	{
		for(ServletRequestListener scl : dereferObjects())
			scl.requestInitialized(sre);
	}

	public void requestDestroyed(ServletRequestEvent sre)
	{
		List<ServletRequestListener>         l =
		  dereferObjects();
		ListIterator<ServletRequestListener> i =
		  l.listIterator(l.size());

		while(i.hasPrevious())
			i.previous().requestDestroyed(sre);
	}
}