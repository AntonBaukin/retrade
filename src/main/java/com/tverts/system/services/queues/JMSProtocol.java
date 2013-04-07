package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.Event;

/* com.tverts: support */

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

	private static final JMSProtocol INSTANCE =
	  new JMSProtocol();

	protected JMSProtocol()
	{}


	/* protocol constants */

	public static final String SERVICE   =
	  "com_tverts_zservices_serviceUID";

	public static final String BROADCAST =
	  "com_tverts_zservices_isBroadcast";

	public static final String EVENTYPE  =
	  "com_tverts_zservices_eventType";

	public static final String EXECTIME  =
	  "com_tverts_zservices_executionTime";


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

		//~: event type
		if(event.getEventType() == null)
			msg.setStringProperty(EVENTYPE, event.getClass().getName());
		else
			msg.setStringProperty(EVENTYPE, event.getEventType().getName());

		//~: execution time
		if(event instanceof DelayedEvent)
		{
			long et = ((DelayedEvent)event).getEventTime();

			if(et > System.currentTimeMillis())
			{
				msg.setLongProperty(EXECTIME, et);

				//~: HornetQ support
				msg.setLongProperty("_HQ_SCHED_DELIVERY", et);
			}
		}

		return msg;
	}

	public Event   event(Message msg)
	  throws Exception
	{
		final String ERR = "Unknown Z-Service Event encapsulation format!";

		if(!(msg instanceof ObjectMessage))
			throw new IllegalStateException(ERR);

		Object res = ((ObjectMessage)msg).getObject();

		if(!(res instanceof Event))
			throw new IllegalStateException(ERR);
		return (Event)res;
	}

	public String  readService(Message msg)
	  throws Exception
	{
		return msg.getStringProperty(SERVICE);
	}

	public String  readEventType(Message msg)
	  throws Exception
	{
		return msg.getStringProperty(EVENTYPE);
	}
}