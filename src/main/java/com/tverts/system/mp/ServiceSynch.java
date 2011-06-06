package com.tverts.system.mp;

/* com.tverts: services */

import com.tverts.system.Service;


/**
 * The point of synchronization the services activation order.
 *
 * @author anton.baukin@gmail.com
 */
public interface ServiceSynch
{
	/* public: ServiceSynch interface */

	/**
	 * Each service connected to synch point must
	 * invoke this method before starting it's
	 * actual activity.
	 */
	public void waitServiceMayStart(Service service)
	  throws InterruptedException;

	/**
	 * Each service connected to synch point must
	 * invoke this method after it's actiity is done.
	 *
	 * Depending on the service design the time when
	 * it is completed may vary. Some services do the
	 * work and finish activity, some do process the
	 * initial tasks and then wait for the new tasks.
	 * The latter tell that they are completed when
	 * the initial tasks are done.
	 *
	 * Services that repeatedly handle the incoming
	 * tasks and has no initial tasks may tell that
	 * they are completed in the very beginning.
	 * It is not an error to tell this after each
	 * request is handled.
	 */
	public void serviceCompleted(Service service);
}