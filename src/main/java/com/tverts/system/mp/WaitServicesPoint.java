package com.tverts.system.mp;

/* standard Java classes */

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/* com.tverts: services */

import com.tverts.system.Service;
import com.tverts.system.ServiceReference;
import com.tverts.system.services.ServiceBase;

/* com.tverts: multiprocessing */

import com.tverts.mp.WaitPoint;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * The synchronization primitive the services activation order.
 *
 * If a serive is not an active service it is NOT automatically
 * removed from the list of the services to wait.
 *
 * It is assumed the active service would notify the waiting services
 * when it's behaviour is in some phase: for example, when it had done
 * all the active work.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class WaitServicesPoint
{
	/* public: WaitServicesPoint interface */

	/**
	 * Waits till all the services of the point are completed.
	 */
	public void    waitOnPoint(Service service)
	  throws InterruptedException
	{
		this.waitPoint.waitOnPoint(service);
	}

	/**
	 * Waits till all the services of the point are completed
	 * with the timeout (milliseconds) given. If the timeout
	 * is zero the call is not blocking.
	 *
	 * Returns {@code true} when wait request was successfull,
	 * and all the services are completed.
	 */
	public boolean waitOnPoint(Service service, long timeout)
	  throws InterruptedException
	{
		return this.waitPoint.waitOnPoint(timeout, service);
	}

	/**
	 * Tells that the service is completed. Once the service
	 * is marked it may not be unmarked back.
	 */
	public void    serviceCompleted(Service service)
	{
		this.waitPoint.releaseResources(service);
	}


	/* public: WaitServicesPoint bean interface */

	/**
	 * Privies the reference to the services. Not that
	 * dereference is done right in this call.
	 */
	@SuppressWarnings("unchecked")
	public void    setServices(ServiceReference services)
	{
		HashSet<Service> resources =
		  new HashSet<Service>(services.dereferObjects());

		this.waitPoint = new WaitPoint(resources);
		this.waitInfo  = this.toString(resources);
	}


	/* public: Object interface */

	public String    toString()
	{
		return this.waitInfo;
	}

	protected String toString(Collection<Service> services)
	{
		StringBuilder sb = new StringBuilder(156);
		boolean       fs = true;

		sb.append("wait services point on [");

		for(Service service : services)
		{
			if(!fs) sb.append("; "); fs = false;

			if(service instanceof ServiceBase)
				sb.append(((ServiceBase)service).logsig());
			else
				sb.append(OU.sig(service));
		}

		sb.append(']');

		return sb.toString();
	}


	/* private: the wait point */

	private volatile WaitPoint waitPoint =
	  new WaitPoint(Collections.emptySet());

	private String             waitInfo  =
	  "wait services point on []";
}