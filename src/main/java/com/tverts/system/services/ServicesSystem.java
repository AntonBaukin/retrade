package com.tverts.system.services;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* com.tverts: support (logging) */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Implementation of Z-Services subsystem.
 *
 * @author anton.baukin@gmail.com
 */
public class ServicesSystem implements Servicer
{
	/* public: ServicesSystem interface */

	/**
	 * This method registers and initializes all the services.
	 * It must be invoked before any service to be used!
	 */
	public void init()
	{
		LU.I(getLog(), "starting System Z-Services...");

		try
		{
			lock().writeLock().lock();

			//0: build the services map
			this.services = buildServicesMap();

			//1: order the services
			this.ordered  = orderServices();

			LU.I(getLog(), "  Z-Services order: ",
			  SU.scat(", ", (Object[])this.ordered));

			//2: init the services
			for(String suid : this.ordered)
			{
				this.services.get(suid).init(this);
				LU.I(getLog(), "  initialized service: ", suid);
			}

			LU.I(getLog(), "all Z-Services initialized!");
		}
		finally
		{
			lock().writeLock().unlock();
		}
	}

	/**
	 * Services the event. May be invoked directly by
	 * the client components, but intended for system
	 * usages. Service messages (events) executor
	 * invokes this method.
	 *
	 * Consider sending the events {@link #send(Event)}
	 * rather than process them directly!
	 */
	public void service(Event event)
	{
		if(event == null)
			return;

		try
		{
			lock().readLock().lock();


			//?: {broadcast the event}
			if(SU.sXe(event.getService()))
				broadcast(event);
			else
			{
				Service service = getServicesMap().
				  get(event.getService());

				if(service == null)
					LU.E(getLog(), eventTypeLog(event),
					  " refers unknown service '%s'!",
					  event.getService());

				//~: invoke the service
				if(service != null)
					invoke(service, event);
			}
		}
		catch(Throwable e)
		{
			LU.E(getLog(), e);
		}
		finally
		{
			lock().readLock().unlock();
		}
	}


	/* public: Servicer interface */

	public Service  service(String suid)
	{
		try
		{
			lock().readLock().lock();
			return getServicesMap().get(suid);
		}
		finally
		{
			lock().readLock().unlock();
		}
	}

	public Service  xservice(String suid)
	{
		Service service = service(suid);

		if(service == null)
			throw new IllegalStateException(String.format(
			  "Service ID with ID '%s' does not exist!", suid
			));

		return service;
	}

	public void     send(Event event)
	{
		if(getMessenger() == null) throw new IllegalStateException(
		  "Z-Services System has no Service Messager configured!");

		getMessenger().sendEvent(event);
	}

	public String[] services()
	{
		try
		{
			lock().readLock().lock();
			return this.ordered;
		}
		finally
		{
			lock().readLock().unlock();
		}
	}


	/* public: ServicesSystem (bean) interface */

	public ServiceMessenger getMessenger()
	{
		return messenger;
	}

	public void setMessenger(ServiceMessenger messenger)
	{
		this.messenger = messenger;
	}

	public ServiceReference getReference()
	{
		return reference;
	}

	public void setReference(ServiceReference reference)
	{
		this.reference = reference;
	}


	/* protected: servicing */

	protected void broadcast(Event event)
	{
		for(String suid : services()) try
		{
			getServicesMap().get(suid).service(event);
		}
		catch(Throwable e)
		{
			logError(suid, e, event);
		}
	}

	protected void invoke(Service service, Event event)
	{
		try
		{
			service.service(event);
		}
		catch(Throwable e)
		{
			logError(service.uid(), e, event);
		}
	}

	protected void logError(String suid, Throwable e, Event event)
	{
		LU.E(getLog(), e, " error in service '", suid,
		  "' while processing ", eventTypeLog(event), '!');
	}


	/* protected: system implementation */

	protected Map<String, Service> getServicesMap()
	{
		if(services == null) throw new IllegalStateException(
		  "Z-Services System was not initialized!"
		);

		return services;
	}

	protected Map<String, Service> buildServicesMap()
	{
		Map<String, Service> map = allocateServicesMap();

		if(getReference() == null)
			return map;

		List<Service> services = getReference().
		  dereferObjects();

		for(Service service : services)
			if(!map.containsKey(service.uid()))
				map.put(service.uid(), service);
			//!: service with duplicate UID
			else throw new IllegalStateException(String.format(
			  "Error during Z-Services System build: service '%s' is " +
			  "already registered!", service.uid()
			));

		return map;
	}

	protected String[]             orderServices()
	{
		ArrayList<String>        result =
		  new ArrayList<String>(getServicesMap().size());

		Map<String, Set<String>> depmap =
		  new HashMap<String, Set<String>>(getServicesMap().size());

		//~: process independent services
		for(Service service : getServicesMap().values())
		{
			String[] depends = service.depends();

			//?: {the service is independent}
			if(depends == null)
			{
				result.add(service.uid());
				continue;
			}

			//~: check the services do present
			for(String depend : depends)
				if(!getServicesMap().containsKey(depend))
					throw new IllegalArgumentException(String.format(
					  "Error during Z-Services System build: service '%s' depends" +
					  " on not existing service '%s'!", service.uid(), depend
					));

			//~: add to the dependency map
			depmap.put(service.uid(),
			  new HashSet<String>(Arrays.asList(depends)));
		}

		final Map<String, Set<String>> closures =
		  new HashMap<String, Set<String>>(depmap.size());

		//~: check dependencies closure
		for(String suid : depmap.keySet())
		{
			HashSet<String> inspects =
			  new HashSet<String>(depmap.get(suid));

			HashSet<String> closure  =
			  new HashSet<String>(inspects.size());

			while(!inspects.isEmpty())
			{
				String iuid = inspects.iterator().next();
				inspects.remove(iuid);

				if(closure.contains(iuid))
					continue;
				closure.add(iuid);

				if(iuid.equals(suid))
					throw new IllegalStateException(String.format(
					  "Error during Z-Services System build: service '%s' " +
					  "has cyclyc dependency on itself via '%s' service!",
					  suid, iuid
					));

				if(depmap.containsKey(iuid))
					inspects.addAll(depmap.get(iuid));
			}

			closures.put(suid, closure);
		}

		//~: sort by the dependencies
		ArrayList<String> sorded =
		  new ArrayList<String>(closures.keySet());

		Collections.sort(sorded, new Comparator<String>()
		{
			public int compare(String suidL, String suidR)
			{
				//?: {L dep on R} L after R
				if(closures.get(suidL).contains(suidR))
					return +1;

				//?: {R dep on L} R after L
				if(closures.get(suidR).contains(suidL))
					return -1;

				//~: they are not depend on each other
				return 0;
			}
		});


		result.addAll(sorded);
		return result.toArray(new String[result.size()]);
	}

	protected Map<String, Service> allocateServicesMap()
	{
		return new LinkedHashMap<String, Service>(11);
	}

	protected final ReadWriteLock  lock()
	{
		return lock;
	}

	protected String               eventTypeLog(Event event)
	{
		return (event == null)?("event UNDEFINED"):
		  (event.getEventType() == null)
		    ?String.format("event class %s", event.getClass().getName())
		    :String.format("event class %s type %s",
		      event.getClass().getName(), event.getEventType().getName());
	}


	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_BOOT;
	}


	/* the system state */

	private final ReadWriteLock  lock =
	  new ReentrantReadWriteLock();

	private Map<String, Service> services;
	private String[]             ordered;

	private ServiceMessenger     messenger;
	private ServiceReference     reference;
}