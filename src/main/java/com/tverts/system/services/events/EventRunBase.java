package com.tverts.system.services.events;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.MainService;
import com.tverts.system.services.Servicer;
import com.tverts.system.services.ServicesPoint;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Implementation base for Event
 * processing strategy.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EventRunBase
       implements     EventRun, EventRunReference
{
	/* public: EventRunReference interface */

	public List<EventRun> dereferObjects()
	{
		return Collections.<EventRun> singletonList(this);
	}


	/* protected: events handling support */

	protected Servicer servicer()
	{
		return ServicesPoint.system();
	}

	/**
	 * Returns name of the Main Service.
	 */
	protected String   uid()
	{
		return MainService.NAME;
	}

	protected void     send(Event event)
	{
		if(event instanceof ServiceEventBase)
			((ServiceEventBase)event).setSourceService(uid());

		servicer().send(event);
	}

	protected void     send(String suid, EventBase event)
	{
		if((suid != null) && SU.sXe(event.getService()))
			((EventBase)event).setService(suid);

		send(event);
	}

	/**
	 * Sends event to the Main Service.
	 */
	protected void     self(EventBase event)
	{
		send(uid(), event);
	}

	protected void     broadcast(Event event)
	{
		if(!SU.sXe(event.getService()))
			if(event instanceof EventBase)
				((EventBase)event).setService(null);

		send(event);
	}

	protected Class    type(Event event)
	{
		return (event.getEventType() != null)
		  ?(event.getEventType()):(event.getClass());
	}

	protected boolean  broadcasted(Event event)
	{
		return SU.sXe(event.getService());
	}

	protected boolean  mine(Event event)
	{
		return this.uid().equals(event.getService());
	}


	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}

	protected String getServicesLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}

	protected String logsig()
	{
		return this.getClass().getSimpleName();
	}
}