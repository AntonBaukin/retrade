package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * As a {@link ServletRequestListener} class this
 * class must be registered in 'web.xml' file.
 * But only one time: in Servlet 2.5 it is not
 * possible to assign init parameters to listeners,
 * and this class is a singleton even when
 * several instances are created.
 *
 * Each instance of this class shares the same
 * root listener defined as the point:
 * {@link ServletRequestListenerPoint}
 *
 * To register different listeners you must
 * create subclasses and refer other point.
 *
 * @see {@link ServletRequestListenerBean}
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      ServletRequestListenerBridge
       implements ServletRequestListener
{
	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent sre)
	{
		getListenerPoint().requestInitialized(sre);
	}

	public void requestDestroyed(ServletRequestEvent sre)
	{
		getListenerPoint().requestDestroyed(sre);
	}

	/* protected: access point */

	protected ServletRequestListener getListenerPoint()
	{
		return ServletRequestListenerPoint.getInstance();
	}
}