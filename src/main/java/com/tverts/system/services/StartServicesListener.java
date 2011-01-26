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

		LU.I(getLog(), sb.toString());
	}

	protected void   logServicesStopping(List<Service> services)
	{
		if(!LU.isI(getLog())) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to stop the active services present: \n[");
		ServicesPoint.appendActiveServicesList(sb, services);
		sb.append(']');

		LU.I(getLog(), sb.toString());
	}

	protected void   logServiceInitOpen(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "starting system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "started system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(ServiceInitError error)
	{
		LU.E(getLog(), String.format(
		  "error occured when starting system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}

	protected void   logServiceFreeOpen(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "stopping system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "stopped system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(ServiceInitError error)
	{
		LU.E(getLog(), String.format(
		  "error occured when stopping system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}

	protected void   logServiceWaitOpen(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "waiting system service '%s' to stop...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceWaitClose(Service service)
	{
		if(!LU.isD(getLog())) return;

		LU.D(getLog(), String.format(
		  "system service '%s' threads are stopped!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceWaitClose(ServiceInitError error)
	{
		LU.E(getLog(), String.format(
		  "error occured when waiting system service '%s' to stop!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}
}