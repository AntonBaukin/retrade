package com.tverts.system.services;

/* standard Java classes */

import java.util.List;
import java.util.ListIterator;

/* com.tverts: system services */

import com.tverts.system.Service;
import com.tverts.system.ServicesPoint;

/* com.tverts: support */

import com.tverts.support.LU;

/**
 * Handles the second phase of the services bootstrap:
 * service start and stop.
 *
 * Note that the services are stopped in the reverse order
 * according to the start.
 *
 * Implementation issue: as this class is inherited from
 * {@link InitServicesListener}, operation 'init' means
 * 'start', and 'free' means 'stop'.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   StartServicesListener
       extends InitServicesListener
{
	/* protected: start stage */

	protected void initService(Service service)
	{
		//?: {the service is not active} skip it
		if(!service.getServiceInfo().isActiveService())
			return;

		try
		{
			logServiceInitOpen(service);
			service.startService();
			logServiceInitClose(service);
		}
		catch(Throwable e)
		{
			ServiceInitError error = new ServiceInitError(service, e);

			logServiceInitClose(error);
			throw error;
		}
	}

	/**
	 * After stopping the services this method would
	 * wait until their current activity to stop.
	 */
	protected void freeServices(List<Service> services)
	{
		super.freeServices(services);

		try
		{
			waitServices(services);
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected void freeService(Service service)
	{
		//?: {the service is not active} skip it
		if(!service.getServiceInfo().isActiveService())
			return;

		try
		{
			logServiceFreeOpen(service);
			service.stopService();
			logServiceFreeClose(service);
		}
		catch(Throwable e)
		{
			ServiceInitError error = new ServiceInitError(service, e);

			logServiceFreeClose(error);

			//NOTE: that possible error is not thrown out of here.
			//      Else, the rest of the services would not be stopped.
		}
	}

	protected void waitServices(List<Service> services)
	  throws InterruptedException
	{
		ListIterator<Service> i =
		  services.listIterator(services.size());

		while(i.hasPrevious())
			waitService(i.previous());
	}

	protected void waitService(Service service)
	  throws InterruptedException
	{
		//?: {the service is not active} skip it
		if(!service.getServiceInfo().isActiveService())
			return;

		try
		{
			logServiceWaitOpen(service);
			service.waitService();
			logServiceWaitClose(service);
		}
		catch(InterruptedException e)
		{
			//HINT: we just waiting, and were interrupted, so,
			//      jump out without hesitation...

			throw e;
		}
		catch(Throwable e)
		{
			ServiceInitError error = new ServiceInitError(service, e);
			logServiceWaitClose(error);
		}
	}

	/* protected: logging */

	protected void   logServicesFound(List<Service> services)
	{
		if(!LU.isI(getLog())) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to start the active services present: \n[");
		ServicesPoint.appendActiveServicesList(sb, services);
		sb.append(']');

		LU.I(getLog(), sb);
	}

	protected void   logServicesStopping(List<Service> services)
	{
		if(!LU.isI(getLog())) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to stop the active services present: \n[");
		ServicesPoint.appendActiveServicesList(sb, services);
		sb.append(']');

		LU.I(getLog(), sb);
	}

	protected void   logServiceInitOpen(Service s)
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), "starting ", logsig(s), "...");
	}

	protected void   logServiceInitClose(Service s)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), "started ", logsig(s), "!");
	}

	protected void   logServiceInitClose(ServiceInitError e)
	{
		LU.E(getLog(), e,
		  "error occured when starting ",
		  logsig(e.getService()), "!");
	}

	protected void   logServiceFreeOpen(Service s)
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), "stopping ", logsig(s), "...");
	}

	protected void   logServiceFreeClose(Service s)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), "stopped ", logsig(s), "!");
	}

	protected void   logServiceFreeClose(ServiceInitError e)
	{
		LU.E(getLog(), e,
		  "error occured when stopping ",
		  logsig(e.getService()), "!");
	}

	protected void   logServiceWaitOpen(Service s)
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), "waiting ", logsig(s), " to stop...");
	}

	protected void   logServiceWaitClose(Service s)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), logsig(s), " threads are stopped!");
	}

	protected void   logServiceWaitClose(ServiceInitError e)
	{
		LU.E(getLog(), e,
		  "error occured when waiting ",
		  logsig(e.getService()), " to stop!");
	}
}