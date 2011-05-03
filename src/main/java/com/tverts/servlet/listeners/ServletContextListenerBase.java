package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletContextListener;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;

/**
 * Combines listener with self referencing object.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServletContextListenerBase
       implements     ServletContextListener,
                      ObjectsReference<ServletContextListener>
{
	/* public: ObjectsReference interface */

	public List<ServletContextListener> dereferObjects()
	{
		return Collections.<ServletContextListener>singletonList(this);
	}
}