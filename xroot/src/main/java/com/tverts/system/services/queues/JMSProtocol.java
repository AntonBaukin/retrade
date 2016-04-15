package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.Event;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Implements protocol of encapsulating Z-Services
 * Events into JMS Messages.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class JMSProtocol
{
	/* JMSProtocol Singleton */

	public static JMSProtocol getInstance()
	{
		return INSTANCE;
	}

	public static final JMSProtocol INSTANCE =
	  new JMSProtocol();

	protected JMSProtocol()
	{}


	/* protocol constants */

	public static final String SERVICE   =
	  "com_tverts_zservices_ServiceUID";

	public static final String BROADCAST =
	  "com_tverts_zservices_IsBroadcast";

	/**
	 * Active MQ property for a delayed event.
	 */
	public static final String EXECTIME  =
	  "AMQ_SCHEDULED_DELAY";


	/**
	 * When application server starts, it saves the timestamp
	 * and assigns it to each event sent. When it executes
	 * a request it allows only the request with the same
	 * marker. This technique rejects the delayed events
	 * sent from the previous runs.
	 */
	public static final String MARKER    =
	  "com_tverts_zservices_Marker";

	public static final long   MARKER_TS =
	  System.currentTimeMillis();


	/* public: protocol implementation */

	public Message message(Session session, Event event)
	  throws Exception
	{
		ObjectMessage msg = session.createObjectMessage(event);

		//~: service name | broadcast
		if(SU.sXe(event.getService()))
			msg.setBooleanProperty(BROADCAST, true);
		else
			msg.setStringProperty(SERVICE, event.getService());

		//~: service marker
		msg.setLongProperty(MARKER, MARKER_TS);

		//~: execution time
		if(event instanceof DelayedEvent)
		{
			long et = ((DelayedEvent) event).getEventTime();
			long td = et - System.currentTimeMillis();

			if(td > 0L)
				msg.setLongProperty("AMQ_SCHEDULED_DELAY", td);
		}

		return msg;
	}

	public Event   event(Message msg)
	  throws Exception
	{
		final String ERR = "Unknown Z-Service Event encapsulation format!";

		if(!(msg instanceof ObjectMessage))
			throw EX.state(ERR);

		//?: {has no marker | marker differs}
		if(!msg.propertyExists(MARKER) || (msg.getLongProperty(MARKER) != MARKER_TS))
			return null;

		//~: take the object
		Object res = ((ObjectMessage) msg).getObject();

		//?: {is not an event}
		if(!(res instanceof Event))
			throw EX.state(ERR);

		return (Event)res;
	}

	public String  readService(Message msg)
	  throws Exception
	{
		return msg.getStringProperty(SERVICE);
	}
}