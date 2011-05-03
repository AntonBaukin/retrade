package com.tverts.servlet.listeners;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Java Servlet api */

import javax.servlet.ServletRequestListener;

/* com.tverts: objects */

import com.tverts.objects.ObjectsReference;

/**
 * Combines listener with self referencing object.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServletRequestListenerBase
       implements     ServletRequestListener,
                      ObjectsReference<ServletRequestListener>
{
	/* public: ObjectsReference interface */

	public List<ServletRequestListener> dereferObjects()
	{
		return Collections.<ServletRequestListener>singletonList(this);
	}
}