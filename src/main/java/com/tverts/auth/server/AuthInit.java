package com.tverts.auth.server;

/* standard Java classes */

import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

/* Java Servlets */

import javax.servlet.ServletContextListener;

/* Java Naming */

import javax.naming.InitialContext;


/**
 * Initializes the authentication application.
 *
 * @author anton.baukin@gmail.com.
 */
public class AuthInit implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
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

	protected void initAuthConfig(ServletContext ctx)
	{
		//~: configure the data source
		configDatasource(ctx);

		//~: config authentication timeout
		configAuthTimeout(ctx);

		//~: config session timeout
		configSessionTimeout(ctx);
	}

	@SuppressWarnings("unchecked")
	protected void configDatasource(ServletContext ctx)
	{
		String name = ctx.getInitParameter("retrade.auth.datasource");
		if(name == null) throw new IllegalStateException(
		  "The required context parameter 'retrade.auth.datasource'" +
		  " is not configured in the web.xml!");

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

			//~: open the context & lookup
			InitialContext ic = new InitialContext(env);
			DataSource     ds = (DataSource) ic.lookup(name);

			AuthConfig.INSTANCE.setDataSource(ds);
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured during AuthServlet initialization when " +
			  "accessing ReTrade Database datasource!", e);
		}
	}

	protected void configAuthTimeout(ServletContext ctx)
	{
		String p = ctx.getInitParameter("retrade.auth.auth-timeout");
		if(p == null) return;

		AuthConfig.INSTANCE.setAuthTimeout(Long.parseLong(p)*1000);
	}

	protected void configSessionTimeout(ServletContext ctx)
	{
		String p = ctx.getInitParameter("retrade.auth.session-timeout");
		if(p == null) return;

		AuthConfig.INSTANCE.setSessionTimeout(Long.parseLong(p)*1000);
	}

	protected void initAuthProtocol(ServletContext ctx)
	{
		protocolPrototype = new AuthProtocol();
		protocolPrototype.initPrototype(new DbConnect());
	}


	/* shared: protocol prototype */

	private static AuthProtocol protocolPrototype;
}