package com.tverts.system.zservices;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* com.tverts: system services (events) */

import com.tverts.system.zservices.events.EventBase;
import com.tverts.system.zservices.events.ServiceEventBase;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Z-Service implementation base.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServiceBase
       implements     Service, ServiceReference
{
	/* public: Service interface */

	public String   uid()
	{
		if(this.UID == null) throw new IllegalStateException(
		  "Service UID was not assigned!");

		return this.UID;
	}

	public String[] depends()
	{
		return null;
	}

	public void     init(Servicer servicer)
	{
		this.servicer = servicer;
	}


	/* public: ServiceReference interface */

	public List<Service> dereferObjects()
	{
		return Collections.<Service> singletonList(this);
	}


	/* public: ServiceBase (bean) interface */

	public String getUID()
	{
		return UID;
	}

	public void   setUID(String UID)
	{
		this.UID = SU.s2s(UID);
	}


	/* protected: service implementation base */

	protected Servicer servicer()
	{
		if(this.servicer == null)
			throw new IllegalStateException(String.format(
			  "Service '%s' was not initialized by the Service System!",
			  getUID()
			));

		return this.servicer;
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
	 * Sends event to this service (self-call).
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

	protected boolean  isBroadcasted(Event event)
	{
		return SU.sXe(event.getService());
	}


	/* service state */

	private String   UID;
	private Servicer servicer;
}