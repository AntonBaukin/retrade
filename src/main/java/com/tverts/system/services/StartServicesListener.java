package com.tverts.system.services;

/* standard Java classes */

import java.util.List;

/* com.tverts: system services */

import com.tverts.system.Service;
import com.tverts.system.ServicesPoint;

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

			//!!!: not thrown
			//throw error;
		}
	}

	/* protected: logging */

	protected void   logServicesFound(List<Service> services)
	{
		if(!getLog().isInfoEnabled()) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to start the active services present: \n[");
		ServicesPoint.appendActiveServicesList(sb, services);
		sb.append(']');

		getLog().info(sb.toString());
	}

	protected void   logServicesStopping(List<Service> services)
	{
		if(!getLog().isInfoEnabled()) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to stop the active services present: \n[");
		ServicesPoint.appendActiveServicesList(sb, services);
		sb.append(']');

		getLog().info(sb.toString());
	}

	protected void   logServiceInitOpen(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "starting system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "started system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(ServiceInitError error)
	{
		getLog().error(String.format(
		  "error occured when starting system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}

	protected void   logServiceFreeOpen(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "stopping system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "stopped system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(ServiceInitError error)
	{
		getLog().error(String.format(
		  "error occured when stopping system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}
}