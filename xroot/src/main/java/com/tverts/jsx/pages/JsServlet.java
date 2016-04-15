package com.tverts.jsx.pages;

/* Java */

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* com.tverts: jsx */

import com.tverts.jsx.JsCtx;
import com.tverts.jsx.JsX;

/* com.tverts: servlet */

import com.tverts.servlet.REQ;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * This servlet forwards the processing to
 * JavaScript file having '.jsx' extension.
 *
 * It prepares the execution context and invokes
 * {@link JsX#apply(String, String, JsCtx, Object...)}
 * having the first argument the script requested,
 * second argument the name of the HTTP method
 * in the lower case (get, post, put, and else),
 * and the call arguments are Servlet request
 * and response objects.
 *
 * Servlet input stream is wrapped with Reader
 * in the encoding of the body request. Do
 * not call it in the case of binary stream.
 * To access the input Reader in the script
 * call {@code JsX.in()}.
 *
 * Response may be written directly from the
 * Servlet response object, or via wrapped Writer.
 * To access it call {@code JsX.out()}.
 *
 * Any error text written to error Writer via
 * {@code JsX.err()} has priority over the
 * ordinary output text and responded with
 * server internal error 500 status.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class JsServlet extends GenericServlet
{
	/* public: Generic Servlet */

	public void service(ServletRequest xreq, ServletResponse xres)
	  throws java.io.IOException, ServletException
	{
		if(!(xreq instanceof HttpServletRequest))
			throw EX.state();

		if(!(xres instanceof HttpServletResponse))
			throw EX.state();

		HttpServletRequest  req = (HttpServletRequest)xreq;
		HttpServletResponse res = (HttpServletResponse)xres;

		//~: resolve the page requested
		String script = resolveScript(req);
		if(script == null)
		{
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		//~: create the context
		JsCtx ctx = createContext(req, res);

		//!: invoke the script
		try
		{
			JsX.apply(script, req.getMethod().toLowerCase(), ctx);
		}
		catch(Throwable e)
		{
			//~: print the error to the proper stream
			try
			{
				PrintWriter w = new PrintWriter(ctx.getStreams().getError());
				e.printStackTrace(w);
				w.flush();
			}
			catch(Throwable e2)
			{
				throw new ServletException(e2);
			}
		}

		//~: deliver the results
		try
		{
			//~: flush the streams
			ctx.getStreams().flush();

			//?: {have error text} send error
			BytesStream err = ctx.getStreams().getErrorBytes();
			if((err != null) && (err.length() != 0L))
			{
				//~: stratus, type, length
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setContentType("text/plain;charset=UTF-8");
				res.setContentLength((int) err.length());

				//!: write the bytes
				err.copy(res.getOutputStream());
				return;
			}

			//?: {have output text}
			BytesStream out = ctx.getStreams().getOutputBytes();
			if((out != null) && (out.length() != 0L))
			{
				//~: content length
				res.setContentLength((int) out.length());

				//!: write the bytes
				out.copy(res.getOutputStream());
			}
		}
		finally
		{
			//~: close the context
			ctx.close();
		}
	}


	/* protected: script execution */

	protected String  resolveScript(HttpServletRequest req)
	{
		String uri = req.getRequestURI();
		EX.assertx(uri.endsWith(".jsx"));

		//?: {has context path}
		String cp = req.getContextPath();
		if(uri.startsWith(cp))
			uri = uri.substring(cp.length());

		//?: {script is not found}
		if(!JsX.INSTANCE.exists(uri))
			return null;

		return uri;
	}

	protected JsCtx   createContext(HttpServletRequest req, HttpServletResponse res)
	  throws java.io.IOException, ServletException
	{
		JsCtx ctx = new JsCtx();

		//~: copy the request parameters into variable
		Map<String, Object> params = new HashMap<String, Object>();
		params.putAll(req.getParameterMap());
		ctx.put("params", params);

		//~: replace string arrays
		for(Map.Entry<String, Object> e : params.entrySet())
			if(e.getValue() instanceof String[])
				if(((String[])e.getValue()).length == 1)
					e.setValue(((String[])e.getValue())[0]);
				else
					e.setValue(Arrays.asList((String[])e.getValue()));

		//~: assign the streams
		assignInputStream(ctx, req);

		//~: default output and error streams
		ctx.getStreams().output().error();

		//=: request variable
		ctx.put("request", req);

		//=: response variable
		ctx.put("response", res);

		return ctx;
	}

	@SuppressWarnings("unchecked")
	protected void    assignInputStream(JsCtx ctx, HttpServletRequest req)
	  throws java.io.IOException, ServletException
	{
		String ct = req.getHeader("Content-Type");
		String en = null;

		//?: {has no content type} assume empty
		if(ct == null)
			return; //<-- the default is empty stream
		else
			ct = ct.toLowerCase();

		//?: {content type has parameters}
		if(ct.indexOf(';') != -1)
		{
			String[] xct = SU.s2a(ct, ';');
			EX.asserte(xct);

			//[0]: content type
			ct = EX.asserts(SU.s2s(xct[0]));

			//~: search for the charset
			for(int i = 1;(i < xct.length);i++)
			{
				String s = SU.s2s(xct[0]);
				if(s == null) continue;

				final String CS = "chartset=";
				if(s.startsWith(CS))
					en = SU.s2s(s.substring(CS.length()));
			}
		}

		//?: {no encoding defined} assume UTF-8
		if(en == null)
			en = "UTF-8";
		else
			en = en.toUpperCase();

		//=: content type variable
		ctx.put("contentType", ct);

		//?: {input stream is text}
		if(isTextContent(ct))
		{
			//~: use the request input stream
			ctx.getStreams().input(new InputStreamReader(
			  req.getInputStream(), en));

			return;
		}

		//?: {decode body parameters}
		if("application/x-www-form-urlencoded".equals(ct))
		{
			REQ.decodeBodyParams(
			  new InputStreamReader(req.getInputStream(), en),
			  en, (Map<String, Object>) ctx.get("params")
			);

			return;
		}

		//~: assign input stream variable
		ctx.put("stream", req.getInputStream());
	}

	protected boolean isTextContent(String ct)
	{
		return ct.startsWith("text/")          ||
		  "application/javascript".equals(ct)  ||
		  "application/json".equals(ct);
	}
}