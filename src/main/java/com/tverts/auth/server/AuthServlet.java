package com.tverts.auth.server;

/* standard Java classes */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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

		if(!(req instanceof HttpServletResponse))
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
	{}

	protected void initAuthProtocol()
	{
		protocolPrototype = new AuthProtocol();
		protocolPrototype.initPrototype(new DbConnect());
	}


	/* shared: protocol prototype */

	private static AuthProtocol protocolPrototype;
}