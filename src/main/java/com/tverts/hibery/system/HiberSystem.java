package com.tverts.hibery.system;

/* standard Java classes */

import java.sql.Connection;
import java.sql.SQLException;

/* Hibernate Persistence Layer */

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * Provides access to the lower implementation levels
 * of Hibernate infrastructure of the application.
 *
 * @author anton.baukin@gmail.com
 */
public class HiberSystem
{
	/* public: Singleton */

	public static HiberSystem getInstance()
	{
		return INSTANCE;
	}

	private static final HiberSystem INSTANCE =
	  new HiberSystem();

	protected HiberSystem()
	{}

	/* public: HiberSystem interface */

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory     = sf;
		this.connectionProvider = null;

		//?: {this factory provides implementation interface}
		if(sf instanceof SessionFactoryImplementor)
		{
			//~: remember the JDBC connections provider

			this.connectionProvider =
			  (((SessionFactoryImplementor)sf).getConnectionProvider());
		}
	}

	public void setConfiguration(Configuration config)
	{
		this.configuration = config;
	}

	/* public: access hibernate configuration */

	public Configuration        getConfiguration()
	{
		return configuration;
	}

	public Dialect              getDialect()
	{
		if(!(this.sessionFactory instanceof SessionFactoryImplementor))
			return null;

		return ((SessionFactoryImplementor)this.sessionFactory).getDialect();
	}

	public static Dialect       dialect()
	{
		Dialect dialect = HiberSystem.getInstance().getDialect();

		if(dialect == null) throw new IllegalStateException(
		  "Hibernate Dialect is not defined!");

		return dialect;
	}

	public static Configuration config()
	{
		Configuration config = HiberSystem.getInstance().
		  getConfiguration();

		if(config == null) throw new IllegalStateException();
		return config;
	}

	/* public: access connections provider */

	public ConnectionProvider getConnectionProvider()
	{
		return connectionProvider;
	}

	public Connection         openConnection()
	  throws SQLException
	{
		ConnectionProvider cp = getConnectionProvider();

		if(cp == null) throw new IllegalStateException();
		return cp.getConnection();
	}

	public void               closeConnection(Connection connection)
	  throws SQLException
	{
		if(connection.isClosed())
			return;

		ConnectionProvider cp = getConnectionProvider();

		if(cp == null) throw new IllegalStateException();
		cp.closeConnection(connection);
	}

	/* private: hibernate system points */

	private SessionFactory     sessionFactory;
	private Configuration      configuration;
	private ConnectionProvider connectionProvider;
}