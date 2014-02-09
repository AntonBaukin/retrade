package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;

/* com.tverts: support */

import com.tverts.support.LU;

/* com.tverts: (services + spring + tx) */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServicesPoint;
import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


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

	public void takeEventMessage(final Message msg)
	{
		try
		{
			//~: extract the event
			final Event event = extractEvent(msg);

			//~: execute it
			bean(TxBean.class).execute(new Runnable()
			{
				public void run()
				{
					try
					{
						//~: process the event
						process(event);
					}
					catch(Throwable e)
					{
						throw EX.wrap(e);
					}
				}
			});

			//~: commit the message
			commitMessage(msg);
		}
		catch(Throwable e)
		{
			//~: rollback the message
			rollbackMessage(e, msg);
		}
	}


	/* protected: message handling */

	protected Event extractEvent(Message msg)
	  throws Exception
	{
		return JMSProtocol.getInstance().event(msg);
	}

	protected void  process(Event event)
	  throws Throwable
	{
		ServicesPoint.system().service(event);
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
			LU.E(getLog(), e, "error occurred when committing",
			  " message of Z-Serivices Queue in the listener! ",
			  logMessage(msg));
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
			return SU.cats(
			  "JMS message for service [", JMSProtocol.getInstance().readService(msg),
			  "] with event type [", JMSProtocol.getInstance().readEventType(msg), "]"
			);
		}
		catch(Throwable e)
		{
			return "UNKNOWN message format";
		}
	}
}