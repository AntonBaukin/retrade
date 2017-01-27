package com.tverts.auth.server;

/* Java */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.sql.DataSource;

/* Java Servlets */

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* Java Naming */

import javax.naming.InitialContext;

/* Java Messaging */

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/* com.tverts: auth support */

import com.tverts.auth.server.support.EX;
import com.tverts.auth.server.support.SU;


/**
 * Initializes the authentication application.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthInit implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		//~: init naming context
		initNamingContext();

		//~: read configuration parameters
		initAuthConfig(sce.getServletContext());

		//~: create the protocol prototype
		initAuthProtocol(sce.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}


	/* public static: AuthInit prototype */

	public static AuthProtocol getProtocolPrototype()
	{
		return protocolPrototype;
	}


	/* protected: initialization */

	@SuppressWarnings("unchecked")
	protected void initNamingContext()
	{
		try
		{
			//?: {has jndi.properties}
			InputStream jp = Thread.currentThread().getContextClassLoader().
			  getResourceAsStream("jndi.properties");

			Hashtable env = null; if(jp != null) try
			{
				Properties p = new Properties();
				p.load(jp);
				env = new Hashtable(p);
			}
			finally
			{
				jp.close();
			}

			//~: open the context
			namingContext = new InitialContext(env);
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured during Auth Server ",
			  "initialization when creating the Naming Context!");
		}
	}

	protected void initAuthConfig(ServletContext ctx)
	{
		//~: configure the data source
		initDataSource(ctx);

		//~: configure the queue
		initConnFactory(ctx);
		initNotifyQueue(ctx);

		//~: config authentication timeout
		configAuthTimeout(ctx);

		//~: config session timeout
		configSessionTimeout(ctx);
	}

	protected void initDataSource(ServletContext ctx)
	{
		String name = EX.asserts(
		  ctx.getInitParameter("retrade.auth.datasource"),

		  "The required context parameter 'retrade.auth.datasource'",
		  " is not configured in the web.xml!"
		);

		try
		{
			AuthConfig.INSTANCE.setDataSource(EX.assertn(
			  (DataSource) namingContext.lookup(name),
			  "No Data Source found!"
			));
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured during Auth Server ",
			  "initialization when looking up the Data Source!");
		}
	}

	protected void initConnFactory(ServletContext ctx)
	{
		String name = EX.asserts(
		  ctx.getInitParameter("retrade.auth.jms-conn-factory"),

		  "The required context parameter 'retrade.auth.jms-conn-factory'",
		  " is not configured in the web.xml!"
		);

		try
		{
			AuthConfig.INSTANCE.setConnectionFactory(EX.assertn(
				 (ConnectionFactory)namingContext.lookup(name),
				 "No JMS Connection Factory found!"
			  )
			);
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured during Auth Server initialization ",
			  "when looking up the JMS Connection Factory!");
		}
	}

	protected void initNotifyQueue(ServletContext ctx)
	{
		String name = EX.asserts(
		  ctx.getInitParameter("retrade.auth.exec-queue"),

		  "The required context parameter 'retrade.auth.exec-queue'",
		  " is not configured in the web.xml!"
		);

		try
		{
			AuthConfig.INSTANCE.setExecRequestQueue(EX.assertn(
				 (Queue)namingContext.lookup(name),
				 "No ReTrade Execute Notifications Queue found!"
			  )
			);
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured during Auth Server initialization ",
			  "when looking up the ReTrade Execute Notifications Queue!");
		}
	}

	protected void configAuthTimeout(ServletContext ctx)
	{
		String p = SU.s2s(ctx.getInitParameter("retrade.auth.auth-timeout"));
		if(p != null) AuthConfig.INSTANCE.
		  setAuthTimeout(Long.parseLong(p)*1000);
	}

	protected void configSessionTimeout(ServletContext ctx)
	{
		String p = SU.s2s(ctx.getInitParameter("retrade.auth.session-timeout"));
		if(p != null) AuthConfig.INSTANCE.
		  setSessionTimeout(Long.parseLong(p)*1000);
	}

	protected void initAuthProtocol(ServletContext ctx)
	{
		protocolPrototype = new AuthProtocol();
		protocolPrototype.initPrototype(new Connect());
	}


	/* shared: protocol prototype */

	private static InitialContext namingContext;
	private static AuthProtocol   protocolPrototype;
}