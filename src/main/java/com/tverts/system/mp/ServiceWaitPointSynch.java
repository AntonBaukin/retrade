package com.tverts.system.mp;

/* com.tverts: services */

import com.tverts.system.Service;
import com.tverts.system.ServiceReference;


/**
 * Serives synchronization on the wait point
 * {@link WaitServicesPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public class      ServiceWaitPointSynch
       implements ServiceSynch
{
	/* public: ServiceSynch interface */

	/**
	 * Waits till the services of the point are completed.
	 *
	 * The service argument is not ignored: if the calling
	 * service is within the waiting resources, it is
	 * excluded (for this call only) from on-wait check.
	 * (Else the service itself would be blocked.)
	 */
	public void waitServiceMayStart(Service service)
	  throws InterruptedException
	{
		this.waitPoint.waitOnPoint(service);
	}

	public void serviceCompleted(Service service)
	{
		this.waitPoint.serviceCompleted(service);
	}


	/* public: ServiceWaitPointSynch bean interface */

	public void setServices(ServiceReference services)
	{
		this.waitPoint.setServices(services);
	}


	/* public: Object interface */

	public String toString()
	{
		return this.waitPoint.toString();
	}


	/* private: the wait point */

	private final WaitServicesPoint waitPoint =
	  new WaitServicesPoint();
}