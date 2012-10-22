package com.tverts.system.zservices;

/* com.tverts: support (logging) */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Implementation to test Z-Services basics.
 *
 * @author anton.baukin@gmail.com
 */
public class TestService extends ServiceBase
{
	/* Test Event */

	public static class TestEvent
	       extends      EventBase
	       implements   DelayedEvent
	{
		public static final long serialVersionUID = 0L;


		/* public: TestEvent interface */

		public String getMessage()
		{
			return message;
		}

		public void   setMessage(String message)
		{
			this.message = message;
		}

		public long   getEventTime()
		{
			return eventTime;
		}

		public void   setEventTime(long eventTime)
		{
			this.eventTime = eventTime;
		}


		/* message */

		private String message;
		private long   eventTime;
	}


	/* public: Service interface */

	public void service(Event event)
	{
		if(!(event instanceof TestEvent))
		{
			LU.I(getLog(), "Test Service ", uid(),
			     " got unknown message of type: ", eventType(event)
			);

			return;
		}

		LU.I(getLog(), "Test Service ", uid(), " got ",
		  isBroadcast(event)?("broadcast"):(""), " message: ",
		  ((TestEvent)event).getMessage()
		);
	}


	/* public: TestService interface */

	public String[] depends()
	{
		return depends;
	}

	public void     setDepends(String depends)
	{
		this.depends = SU.s2a(depends);
	}

	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}


	/* private: dependencies */

	private String[] depends;
}