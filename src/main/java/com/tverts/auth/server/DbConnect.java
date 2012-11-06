package com.tverts.auth.server;

/* standard Java classes */

import java.sql.Connection;
import javax.sql.DataSource;


/**
 * Data Access Strategy controlling
 * own connection and transaction
 * to the authentication database.
 *
 * The initial strategy object is a
 * prototype instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DbConnect implements Cloneable
{
	/* public: constructors */

	public DbConnect(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public DbConnect()
	{
		this(AuthConfig.INSTANCE.getDataSource());
	}


	/* public: DbConnect (connection) interface */

	public void connect()
	{
		try
		{
			connection = dataSource.getConnection();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void disconnect(Boolean commit)
	{
		if(connection != null) try
		{
			if(Boolean.TRUE.equals(commit))
				connection.commit();

			if(Boolean.FALSE.equals(commit))
				connection.rollback();

			connection.close();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			connection = null;
		}
	}


	/* public: DbConnect (data access) interface */

	public String nextSidPrefix()
	{
		return null;
	}

	public String getPassword(Long domain, String login)
	{
		return null;
	}

	public void   loadSession(AuthSession session)
	{}

	public void   saveSession(AuthSession session)
	{}

	public void   touchSession(AuthSession session)
	{}


	/* public: Cloneable interface */

	public DbConnect clone()
	{
		try
		{
			DbConnect dbc  = (DbConnect) super.clone();
			dbc.connection = null;

			return dbc;
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/* (prototype state): the data source */

	private DataSource dataSource;


	/* (instance state): the connection */

	private Connection connection;
}