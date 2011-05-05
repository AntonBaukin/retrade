package com.tverts.hibery;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;

/* Hibernate Persistence Layer */

import org.hibernate.connection.ConnectionProvider;

/**
 * TODO comment ConnectPoint
 *
 * @author anton.baukin@gmail.com
 */
public class ConnectPoint
{
	/* public: Singleton */

	public static ConnectPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ConnectPoint INSTANCE =
	  new ConnectPoint();

	protected ConnectPoint()
	{}

	/* public: ConnectPoint interface */

	public ConnectionProvider getProvider()
	{
		return provider;
	}

	public void               setProvider(ConnectionProvider provider)
	{
		this.provider = provider;
	}

	public Connection         open()
	  throws SQLException
	{
		ConnectionProvider cp = getProvider();

		if(cp == null) throw new IllegalStateException();
		return cp.getConnection();
	}

	public void               close(Connection connection)
	  throws SQLException
	{
		if(connection.isClosed())
			return;

		ConnectionProvider cp = getProvider();

		if(cp == null) throw new IllegalStateException();
		cp.closeConnection(connection);
	}

	/* private: the state of the point */

	private volatile ConnectionProvider provider;
}