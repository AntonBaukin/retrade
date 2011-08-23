package com.tverts.system.mp;

/* standard Java classes */

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/* com.tverts: multiprocessing */

import com.tverts.mp.WaitPoint;

/* com.tverts: services */

import com.tverts.system.Service;
import com.tverts.system.ServiceReference;
import com.tverts.system.ServicesPoint;
import com.tverts.system.mp.ServiceSynchBus.ServiceSynchBusListener;


/**
 * Serives synchronization on the wait point
 * {@link WaitServicesPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public class ServiceWaitSynch
       implements ServiceSynch, ServiceSynchBusListener
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
		//~: notify the local wait point
		this.waitPoint.serviceCompleted(service);


		//~: notify the synch bus
		final ServiceSynchBus synchBus = this.synchBus;

		if(synchBus != null)
			synchBus.serviceCompleted(service);
	}


	/* public: ServiceSynchBusListener interface */

	public void onServiceCompleted(Service service)
	{
		this.waitPoint.serviceCompleted(service);
	}


	/* public: ServiceWaitSynch bean interface */

	public void setServices(ServiceReference services)
	{
		this.waitPoint.setServices(services);
	}

	/**
	 * Updates the synch bus of the point. This point
	 * is also registered as a listener on the bus.
	 */
	public void setSynchBus(ServiceSynchBus bus)
	{
		final ServiceSynchBus synchBus = this.synchBus;

		//~: unregister from the old bus
		if(synchBus != null)
			synchBus.removeListener(this);

		this.synchBus = bus;

		//~: register in the new bus
		if(bus != null)
			bus.connectListener(this);
	}


	/* public: Object interface */

	public String toString()
	{
		return this.waitPoint.toString();
	}


	/* protected: the wait point */

	protected final WaitServicesPoint  waitPoint =
	  new WaitServicesPoint();


	/* protected: the synchronization bus */

	protected volatile ServiceSynchBus synchBus;


	/* protected: wait point adaptation for the services */

	/**
	 * The synchronization primitive the services activation order.
	 *
	 * If a serive is not an active service it is NOT automatically
	 * removed from the list of the services to wait.
	 *
	 * It is assumed the active service would notify the waiting services
	 * when it's behaviour is in some phase: for example, when it had done
	 * all the active work.
	 */
	protected static class WaitServicesPoint
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
		 * Returns {@code true} when wait request was successful,
		 * and all the services are completed.
		 */
		public boolean waitOnPoint(Service service, long timeout)
		  throws InterruptedException
		{
			return this.waitPoint.waitOnPointTimeout(timeout, service);
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
				sb.append(ServicesPoint.logsig(service));
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
}