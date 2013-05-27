package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: support */

import com.tverts.support.LU;

/* com.tverts: system (tx + services) */

import com.tverts.system.tx.TxPoint;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServicesPoint;


/**
 * This strict implementation of the Z-Services
 * Queue listening bean just unwraps the message
 * and send the event into Services System without
 * additional synchronization.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      PlainEventsListenerBean
       implements EventsListenerBean
{
	/* public: EventsListenerBean interface */

	public void takeEventMessage(Message msg)
	{
		try
		{
			//~: extract the event
			Event event = extractEvent(msg);

			//~: process the event
			process(event);
		}
		catch(Throwable e)
		{
			//!: ignore this error
		}
	}


	/* protected: message handling */

	protected Event extractEvent(Message msg)
	  throws Exception
	{
		return JMSProtocol.getInstance().event(msg);
	}

	@Transactional(rollbackFor = Throwable.class)
	protected void  process(Event event)
	  throws Throwable
	{
		//~: push default transaction context
		TxPoint.getInstance().setTxContext();

		try
		{
			//!: directly send the event to the service
			ServicesPoint.system().service(event);
		}
		finally
		{
			//!: pop transaction context
			TxPoint.getInstance().setTxContext(null);
		}
	}

	protected void  rollbackMessage(Throwable error, Message msg)
	{
		LU.E(getLog(), error, " error occurred when processing " +
		  " message of Z-Serivices Queue in the listener! ",
		  logMessage(msg), '.');

		//!: nevertheless acknowledge the message
		commitMessage(msg);
	}

	protected void  commitMessage(Message msg)
	{
		try
		{
			msg.acknowledge();
		}
		catch(Throwable e)
		{
			LU.E(getLog(), e, " error occurred when committing " +
			  " message of Z-Serivices Queue in the listener! ",
			  logMessage(msg), '.');
		}
	}



	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}

	protected String logMessage(Message msg)
	{
		try
		{
			return String.format(
			  "JMS message for service '%s' with event type [%s]",
			  JMSProtocol.getInstance().readService(msg),
			  JMSProtocol.getInstance().readEventType(msg)
			);
		}
		catch(Throwable e)
		{
			return "UNKNOWN message format";
		}
	}
}