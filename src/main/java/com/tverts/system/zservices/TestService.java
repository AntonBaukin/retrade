package com.tverts.system.zservices;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: z-services */

import com.tverts.system.zservices.events.EventBase;
import com.tverts.system.zservices.events.SystemReady;

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
		if     (event instanceof TestEvent)
			serviceTest((TestEvent)event);
		else if(event instanceof SystemReady)
			serviceReady((SystemReady)event);
		else
			serviceOther(event);
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

	public void     setMessages(String messages)
	{
		this.messages = SU.s2a(messages);
	}


	/* protected: servicing */

	protected void serviceTest(TestEvent event)
	{
		LU.I(getLog(), "Test Service ", uid(), " got ",
		  isBroadcasted(event)?("broadcast "):(""), "message: ",
		  event.getMessage()
		);

		sendNextMsg();
	}

	protected void serviceReady(SystemReady event)
	{
		LU.I(getLog(), "Test Service ", uid(), " got ready event...");

		sendNextMsg();
	}

	protected void serviceOther(Event event)
	{
		LU.I(getLog(), "Test Service ", uid(),
		  " got unknown message of type: ", type(event)
		);
	}

	protected void sendNextMsg()
	{
		if((messages == null) || (msgsend >= messages.length))
			return;

		sendMsg(messages[msgsend++]);
	}

	@Transactional
	protected void sendMsg(String msg)
	{
		if(SU.sXe(msg)) return;

		//~: cause the transaction to begin
		txSession().createQuery(
		  "select count(*) from UnityType"
		).uniqueResult();

		String s = "";
		String m = msg;
		int    a = msg.indexOf('@');

		if(a != -1)
		{
			m = msg.substring(0, a).trim();
			s = msg.substring(a + 1).trim();
		}

		if(SU.sXe(m)) return;
		if(SU.sXe(s)) s = null;


		TestEvent e = new TestEvent();

		e.setService(s);
		e.setMessage(String.format(
		  "%s > %s: %s", uid(), (s == null)?("*"):(s), m
		));

		servicer().send(e);
	}


	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}


	/* dependencies & messages */

	private String[] depends;
	private String[] messages;
	private int      msgsend;
}