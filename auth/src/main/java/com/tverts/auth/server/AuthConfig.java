package com.tverts.auth.server;

/* Java */

import javax.sql.DataSource;

/* Java Messaging */

import javax.jms.ConnectionFactory;
import javax.jms.Queue;


/**
 * Stores configuration parameters of the
 * authentication server.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthConfig
{
	/* AuthConfig Singleton */

	public static final AuthConfig INSTANCE =
	  new AuthConfig();


	/* public: access configuration */

	/**
	 * DataSource of the authentication database.
	 */
	public DataSource getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public ConnectionFactory getConnectionFactory()
	{
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory)
	{
		this.connectionFactory = connectionFactory;
	}

	public Queue getExecRequestQueue()
	{
		return execRequestQueue;
	}

	public void setExecRequestQueue(Queue execRequestQueue)
	{
		this.execRequestQueue = execRequestQueue;
	}

	/**
	 * Authentication timeout is a maximum pause
	 * between the protocol steps invocation.
	 */
	public long getAuthTimeout()
	{
		return authTimeout;
	}

	public void setAuthTimeout(long authTimeout)
	{
		this.authTimeout = authTimeout;
	}

	public long getSessionTimeout()
	{
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout)
	{
		this.sessionTimeout = sessionTimeout;
	}


	/* authentication parameters */

	private DataSource        dataSource;
	private ConnectionFactory connectionFactory;
	private Queue             execRequestQueue;

	private long       authTimeout    = 60 * 1000L;          //<-- 1 minute
	private long       sessionTimeout = 4 * 60 * 60 * 1000L; //<-- 4 hours
}