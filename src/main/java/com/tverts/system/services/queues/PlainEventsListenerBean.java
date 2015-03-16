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
	/* Events Listener Bean */

	public void     takeEventMessage(final Message msg)
	{
		try
		{
			//~: extract the event
			final Event event = extractEvent(msg);

			//~: execute it
			if(event != null) bean(TxBean.class).execute(new Runnable()
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
		return JMSProtocol.INSTANCE.event(msg);
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
		return (log != null)?(log):
		  (log = LU.LB(ServicesPoint.LOG_SERVICE_MAIN, getClass()));
	}

	private String log;

	protected String logMessage(Message msg)
	{
		try
		{
			return SU.cats( "JMS message for Service [",
			  JMSProtocol.INSTANCE.readService(msg), "]"
			);
		}
		catch(Throwable e)
		{
			return "UNKNOWN message format";
		}
	}
}