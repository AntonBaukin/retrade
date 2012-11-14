package com.tverts.auth.server;

/* standard Java classes */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import javax.sql.DataSource;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Java Naming */

import javax.naming.InitialContext;


/**
 * Servlet invoking 'Re Trade' Application
 * authentication protocol {@link AuthProtocol}.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthServlet extends GenericServlet
{
	/* public: GenericServlet interface */

	public void service(ServletRequest req, ServletResponse res)
	  throws ServletException, IOException
	{
		if(!(req instanceof HttpServletRequest))
			throw new IllegalStateException();

		if(!(res instanceof HttpServletResponse))
			throw new IllegalStateException();

		this.service((HttpServletRequest)req, (HttpServletResponse)res);
	}

	public void service(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException
	{
		if(!"get".equalsIgnoreCase(req.getMethod()))
		{
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		//~: create auth protocol instance
		AuthProtocol protocol = protocolPrototype.clone();

		//~: assign income parameters
		for(Enumeration<String> i = req.getParameterNames();(i.hasMoreElements());)
		{
			String p = i.nextElement();
			protocol.setParameter(p, req.getParameter(p));
		}

		//~: create output writer
		ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
		Writer writer = new OutputStreamWriter(bos, "ASCII");
		protocol.setWriter(writer);

		//!: invoke the protocol
		protocol.invoke();

		//~: set prevent caching headers
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		res.setHeader("Pragma", "no-cache");

		//?: {has protocol error}
		if(protocol.getError() != null)
		{
			res.sendError(HttpServletResponse.SC_FORBIDDEN, protocol.getError());
			return;
		}

		//~: get resulting bytes
		writer.flush(); writer.close();
		byte[] buffer = bos.toByteArray();

		//~: content type (plain text)
		res.setHeader("Content-Type", "text/plain;charset=US-ASCII");
		res.setIntHeader("Content-Length", buffer.length);

		//~: write the response on success
		res.getOutputStream().write(buffer);
	}

	public void init()
	  throws ServletException
	{
		//~: read configuration parameters
		initAuthConfig();

		//~: create the protocol prototype
		initAuthProtocol();
	}


	/* protected: initialization */

	protected void initAuthConfig()
	{
		//~: configure the data source
		configDatasource();

		//~: config authentication timeout
		configAuthTimeout();

		//~: config session timeout
		configSessionTimeout();
	}

	protected void configDatasource()
	{
		String name = getInitParameter("datasource-name");
		if(name == null) throw new IllegalStateException(
		  "The required parameter 'datasource-name' is not configured " +
		  "in the web.xml, AuthServlet section!");

		try
		{
			InitialContext ctx = new InitialContext();
			DataSource     ds  = (DataSource) ctx.lookup(name);

			AuthConfig.INSTANCE.setDataSource(ds);
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured during AuthServlet initialization when " +
			  "accessing ReTrade Database datasource!", e);
		}
	}

	protected void configAuthTimeout()
	{
		String p = getInitParameter("auth-timeout");
		if(p == null) return;

		AuthConfig.INSTANCE.
		  setAuthTimeout(1000L*Long.parseLong(p));
	}

	protected void configSessionTimeout()
	{
		String p = getInitParameter("session-timeout");
		if(p == null) return;

		AuthConfig.INSTANCE.
		  setSessionTimeout(1000L*Long.parseLong(p));
	}

	protected void initAuthProtocol()
	{
		protocolPrototype = new AuthProtocol();
		protocolPrototype.initPrototype(new DbConnect());
	}


	/* shared: protocol prototype */

	private static AuthProtocol protocolPrototype;
}