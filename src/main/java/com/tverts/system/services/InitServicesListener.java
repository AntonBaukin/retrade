package com.tverts.system.services;

/* standard Java classes */

import java.util.List;
import java.util.ListIterator;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* Apache Log4J */

import org.apache.log4j.Logger;

/* com.tverts: webapp listeners */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: system services */

import com.tverts.system.Service;
import com.tverts.system.ServicesPoint;

/**
 * Handles the first phase of the services bootstrap:
 * service initialization and free.
 *
 * Note that the services are freed in the reverse order
 * according to the start.
 *
 * The list of the services is accessed through the
 * point and not held between the start and stop.
 * But changing the list of the services is strongly
 * not recommended!
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   InitServicesListener
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	/**
	 * Initializes the services configured in {@link ServicesPoint}.
	 *
	 * @throws ServiceInitError
	 */
	public void contextInitialized(ServletContextEvent sce)
	{
		List<Service> services = ServicesPoint.getInstance().dereferObjects();

		logServicesFound(services);
		initServices(services);
	}

	/**
	 * Frees the services in the reversed order.
	 *
	 * @throws ServiceInitError
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
		List<Service> services = ServicesPoint.getInstance().dereferObjects();

		logServicesStopping(services);
		freeServices(services);
	}

	/* protected: init stage */

	/**
	 * When an error occures, the init cycle is breaked with
	 * {@link ServiceInitError} exception.
	 *
	 * @throws ServiceInitError
	 */
	protected void   initServices(List<Service> services)
	{
		for(Service service : services)
			initService(service);
	}

	protected void   initService(Service service)
	{
		try
		{
			logServiceInitOpen(service);
			service.initService();
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
	 * Note that the services are freed in the reversed order!
	 * When an error occures, the free cycle is NOT breaked.
	 */
	protected void   freeServices(List<Service> services)
	{
		ListIterator<Service> i =
		  services.listIterator(services.size());

		while(i.hasPrevious())
			freeService(i.previous());
	}

	protected void   freeService(Service service)
	{
		try
		{
			logServiceFreeOpen(service);
			service.freeService();
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

	protected Logger getLog()
	{
		return ServicesPoint.LOG_SERVICE_BOOT;
	}

	protected void   logServicesFound(List<Service> services)
	{
		if(!getLog().isInfoEnabled()) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the following services are registered in the system: \n[");
		ServicesPoint.appendServicesList(sb, services);
		sb.append(']');

		getLog().info(sb.toString());
	}

	protected void   logServicesStopping(List<Service> services)
	{
		if(!getLog().isInfoEnabled()) return;

		StringBuilder sb = new StringBuilder(128);

		sb.append("the system is going to free all the services present: \n[");
		ServicesPoint.appendServicesList(sb, services);
		sb.append(']');

		getLog().info(sb.toString());
	}

	protected void   logServiceInitOpen(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "initializing system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "initialized system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceInitClose(ServiceInitError error)
	{
		getLog().error(String.format(
		  "error occured when initializing system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}

	protected void   logServiceFreeOpen(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "destroying system service '%s'...",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(Service service)
	{
		if(!getLog().isDebugEnabled()) return;

		getLog().debug(String.format(
		  "destroyed system service '%s'!",
		  service.getServiceInfo().getServiceName()));
	}

	protected void   logServiceFreeClose(ServiceInitError error)
	{
		getLog().error(String.format(
		  "error occured when destroying system service '%s'!",
		  error.getService().getServiceInfo().getServiceName()),
		  error);
	}
}