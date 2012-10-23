package com.tverts.system.zservices.queues;

/* Java Messaging */

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: system */

import static com.tverts.system.JTAPoint.jta;

/* com.tverts: services */

import com.tverts.system.zservices.Event;
import com.tverts.system.zservices.ServiceMessenger;


/**
 * Sends Z-Services Events as JMS messages
 * to the named queue.
 *
 * @author anton.baukin@gmail.com
 */
public class JMSMessenger implements ServiceMessenger
{
	/* public: ServiceMessager interface */

	@Transactional
	public void sendEvent(Event event)
	{
		if(event == null)
			throw new IllegalArgumentException();

		//~: obtain the connection
		Connection conn  = obtainConnection();
		Throwable  error = null;

		try
		{
			//~: create JMS session
			Session session = conn.createSession(
			  true, Session.AUTO_ACKNOWLEDGE);

			//!: send the message
			send(session, event);

			//NOTE: that in JTA-XA transactions we can't commit sessions!
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
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
		if(error != null) throw new RuntimeException(
		  "Error occurred while sending JMS Message " +
		  "into Z-Services Bus Queue!", error);
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
			throw new IllegalStateException(String.format(
			  "Can't locate Z-Services Queue by the JNDI name [%s]!",
			  getQueueName()
			));
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
			throw new IllegalStateException(String.format(
			  "Can't locate Z-Services JMS Connection Factory " +
			  "by the JNDI name [%s]!", getConnectionFactory()
			));
		}

		//~: get the connection
		try
		{
			return cf.createConnection();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(String.format(
			  "Can't obtain JMS Connection from Factory " +
			  "by the JNDI name [%s]!", getConnectionFactory()
			));
		}
	}

	protected void       send(Session session, Event event)
	  throws Exception
	{
		//~: create the message by the protocol
		Message msg = JMSProtocol.getInstance().
		  encapsulate(session, event);

		//!: send the message
		session.createProducer(locateQueue()).
		  send(msg);
	}


	/* messenger properties */

	private String queueName;
	private String connectionFactory;
}