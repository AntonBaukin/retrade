package com.tverts.auth.server;

/* Java */

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

/* com.tverts: auth support */

import com.tverts.auth.server.support.BytesStream;
import com.tverts.auth.server.support.EX;


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
			throw EX.arg();

		if(!(res instanceof HttpServletResponse))
			throw EX.arg();

		this.service((HttpServletRequest)req, (HttpServletResponse)res);
	}

	public void service(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException
	{
		String  path = req.getRequestURI();
		boolean get  = "GET".equalsIgnoreCase(req.getMethod());
		boolean post = "POST".equalsIgnoreCase(req.getMethod());


		//?: {authentication session request}
		if(get && path.endsWith("/session"))
			session(req, res);
		//?: {request object processing}
		else if(post && path.endsWith("/request"))
			request(req, res, false);
		//?: {receive object processing}
		else if(post && path.endsWith("/receive"))
			request(req, res, true);
		else
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}


	/* protected: servicing */

	protected void session(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException
	{
		//~: create auth protocol instance
		AuthProtocol protocol = AuthInit.getProtocolPrototype().clone();

		//~: assign income parameters
		for(Enumeration<String> i = req.getParameterNames();(i.hasMoreElements());)
		{
			String p = i.nextElement();
			protocol.setParameter(p, req.getParameter(p));
		}

		//~: create output writer
		BytesStream stream = new BytesStream();
		Writer      writer = new OutputStreamWriter(stream, "ASCII");
		protocol.setWriter(writer);

		//!: invoke the protocol
		protocol.invoke();

		//~: set prevent caching headers
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		res.setHeader("Pragma", "no-cache");

		//?: {has protocol error}
		if(protocol.getError() != null)
		{
			stream.close();
			res.sendError(HttpServletResponse.SC_FORBIDDEN, protocol.getError());
			return;
		}

		//~: get resulting bytes
		writer.flush();

		//~: content type (plain text)
		res.setHeader("Content-Type", "text/plain;charset=US-ASCII");
		res.setIntHeader("Content-Length", (int) stream.length());

		//~: write the response on success
		try
		{
			stream.copy(res.getOutputStream());
		}
		finally
		{
			stream.close();
		}
	}

	protected void request(HttpServletRequest req, HttpServletResponse res, boolean recieve)
	  throws ServletException, IOException
	{
		//~: create auth protocol instance
		AuthProtocol protocol = AuthInit.getProtocolPrototype().clone();

		//~: assign income parameters
		for(Enumeration<String> i = req.getParameterNames();(i.hasMoreElements());)
		{
			String p = i.nextElement();
			protocol.setParameter(p, req.getParameter(p));
		}

		BytesStream is = null;
		BytesStream os = null;

		try
		{
			//~: set request processing mode
			protocol.setRequest(true);
			protocol.setReceive(recieve);

			//~: read the input object
			is = new BytesStream();
			is.write(req.getInputStream());
			protocol.setPing(is);

			//~: create output stream
			os = new BytesStream();
			protocol.setPong(os);

			//!: invoke the protocol
			protocol.invoke();

			//~: set prevent caching headers
			res.setHeader("Cache-Control", "no-cache, max-age=0");
			res.setHeader("Expires", "0");

			//?: {has protocol error}
			if(protocol.getError() != null)
			{
				res.sendError(HttpServletResponse.SC_FORBIDDEN, protocol.getError());
				return;
			}

			//~: content type (bytes)
			res.setHeader("Content-Type",   "application/octet-stream");
			res.setHeader("Content-Length", Long.toString(os.length()));

			if((protocol.getPongHash() != null) && (os.length() != 0L))
				res.setHeader("Auth-Digest", protocol.getPongHash());

			//!: copy the content to the output
			os.copy(res.getOutputStream());
			res.getOutputStream().flush();
		}
		finally
		{
			if(is != null)
				is.close();

			if(os != null)
				os.close();
		}

		//HINT: we commit the invocation to the database
		//  after the data were sent to the client.

		protocol.commit(); //<-- separate transaction!
	}
}