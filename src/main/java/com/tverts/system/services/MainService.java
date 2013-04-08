package com.tverts.system.services;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: service events */

import com.tverts.system.services.events.EventRun;
import com.tverts.system.services.events.EventRunReference;


/**
 * Main Service is always a Singletone.
 * It must be register din the system by
 * it's fixed name, or the system would
 * not start.
 *
 * Main Service aggregated a list of
 * {@link EventRun} strategies to handle
 * the events.
 *
 * Main Service is the first to receive
 * broadcast events, and is the first
 * service initialized.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      MainService
       implements Service, ServiceReference
{
	/**
	 * The fixed name of Main service.
	 */
	public static final String NAME = "Main";


	/* public: Service interface */

	public String   uid()
	{
		return NAME;
	}

	public String[] depends()
	{
		return new String[0];
	}

	/**
	 * On the initialization Main Service
	 * collects all the {@link EventRun}
	 * strategies referred. This list
	 * may not be changed further.
	 */
	public void     init(Servicer servicer)
	{
		this.runs = dereferHandlers();
	}

	public void     service(Event event)
	{
		for(EventRun run : runs)
			if(run.run(event))
				break;
	}


	/* public: ServiceReference interface */

	public List<Service> dereferObjects()
	{
		return Collections.<Service> singletonList(this);
	}


	/* public: MainService (bean) interface */

	public EventRunReference getReference()
	{
		return reference;
	}

	public void setReference(EventRunReference reference)
	{
		this.reference = reference;
	}


	/* protected: support */

	protected EventRun[] dereferHandlers()
	{
		if(reference == null)
			return new EventRun[0];

		List<EventRun> res = reference.
		  dereferObjects();
		return res.toArray(new EventRun[res.size()]);
	}


	/* private: event handlers reference */

	private EventRunReference reference;
	private EventRun[]        runs;
}
