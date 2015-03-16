package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

/* com.tverts: (spring + system) */

import static com.tverts.spring.SpringPoint.bean;
import static com.tverts.system.JTAPoint.jta;
import com.tverts.system.tx.TxBean;

/* com.tverts: services */

import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceMessenger;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Sends Z-Services Events as JMS messages
 * to the named queue.
 *
 * @author anton.baukin@gmail.com
 */
public class JMSMessenger implements ServiceMessenger
{
	/* public: ServiceMessager interface */

	public void sendEvent(final Event event)
	{
		EX.assertn(event);

		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				Throwable  error   = null;
				Connection conn    = obtainConnection();
				Session    session = null;

				try
				{
					//~: create JMS session
					session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);

					//!: send the message
					send(session, event);
				}
				catch(Throwable e)
				{
					error = e;
				}
				finally
				{
					//~: close the session
					if(session != null) try
					{
						session.close();
					}
					catch(Throwable e)
					{
						if(error == null) error = e;
					}

					//~: close the connection
					try
					{
						conn.close();
					}
					catch(Throwable e)
					{
						if(error == null) error = e;
					}
				}

				//?: {has error}
				if(error != null) throw EX.wrap(error,
				  "Error occurred while sending JMS Message ",
				  "into Z-Services Bus Queue!");
			}
		});
	}


	/* public: JMSMessenger (bean) interface */

	public String getQueueName()
	{
		return queueName;
	}

	public void   setQueueName(String queueName)
	{
		this.queueName = queueName;
	}

	public String getConnectionFactory()
	{
		return connectionFactory;
	}

	public void   setConnectionFactory(String connectionFactory)
	{
		this.connectionFactory = connectionFactory;
	}


	/* protected: messaging */

	protected Queue      locateQueue()
	{
		try
		{
			return (Queue)jta().namingContext().
			  lookup(getQueueName());
		}
		catch(Exception e)
		{
			throw EX.state("Can't locate Z-Services Queue by the JNDI name [",
			  getQueueName(), "]!");
		}
	}

	protected Connection obtainConnection()
	{
		ConnectionFactory cf;

		//~: lookup the connection factory
		try
		{
			cf = (ConnectionFactory)jta().namingContext().
			  lookup(getConnectionFactory());
		}
		catch(Exception e)
		{
			throw EX.state("Can't locate Z-Services JMS Connection Factory ",
			  "by the JNDI name [", getConnectionFactory(), "]!");
		}

		//~: get the connection
		try
		{
			return cf.createConnection();
		}
		catch(Exception e)
		{
			throw EX.state("Can't obtain JMS Connection from Factory ",
			  "by the JNDI name [", getConnectionFactory(), "]!");
		}
	}

	protected void       send(Session session, Event event)
	  throws Throwable
	{
		//~: create the message by the protocol
		Message         m = JMSProtocol.INSTANCE.message(session, event);
		MessageProducer p = session.createProducer(locateQueue());
		Throwable       x = null;

		//~: send the message
		try
		{
			p.send(m);
		}
		catch(Throwable e)
		{
			x = e;
		}
		finally
		{
			try
			{
				p.close();
			}
			catch(Throwable e)
			{
				if(x == null) x = e;
			}
		}
	}


	/* messenger properties */

	private String queueName;
	private String connectionFactory;
}