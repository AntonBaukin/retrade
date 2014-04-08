package com.tverts.servlet;

/* Java */

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.beanOrNull;

/* com.tverts: secure */

import com.tverts.secure.ForbiddenException;

/* com.tverts: objects */

import com.tverts.objects.BinarySource;
import com.tverts.objects.BinarySource.Biny;

/* com.tverts: model */

import com.tverts.model.ModelAccessPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * Download servlet supports two strategies:
 *
 *  · model bean referred with 'model' parameter;
 *  · Spring Bean referred with 'bean' parameter.
 *
 * Each variant requires the object instance to
 * implement {@link Download} or {@link BinarySource}
 * interfaces.
 *
 * For model bean the it is checked the owner of
 * the model is the same user issuing the request.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class DownloadServlet extends GenericServlet
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

		//?: {not a GET request}
		if(!"GET".equalsIgnoreCase(req.getMethod()))
		{
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		//~: the target instance to provide the data
		Object provider = null;
		String param;

		//?: {provider as a model}
		if((param = SU.s2s(req.getParameter("model"))) != null)
		{
			//~: access the model  <-- this is secure access
			provider = EX.assertn(
			  ModelAccessPoint.model().readBean(param),
			  "Model Bean [", param, "] is not found!"
			);

			//?: {not supported}
			EX.assertx((provider instanceof Download) | (provider instanceof BinarySource),
			  "Model Bean [", param, "] is not a supported binary data provider!"
			);
		}
		//?: {provider as a bean}
		else if((param = SU.s2s(req.getParameter("bean"))) != null)
		{
			//~: access the spring bean
			provider = EX.assertn( beanOrNull(param),
			  "Spring Bean [", param, "] is not found!"
			);

			//?: {not supported}
			EX.assertx((provider instanceof Download) | (provider instanceof BinarySource),
			  "Spring Bean [", param, "] is not a supported binary data provider!"
			);
		}

		//~: invoke the provider
		invoke(req, res, provider);
	}


	/* protected: accessing binary data */

	protected void  invoke
	  (HttpServletRequest req, HttpServletResponse res, Object provider)
	  throws java.io.IOException, ServletException
	{
		//?: {download provider}
		if(provider instanceof Download)
			((Download)provider).download(req, res);
		//?: {binary source provider}
		else if(provider instanceof BinarySource)
		{
			//~: create binary source context
			BinyHTTP biny = new BinyHTTP(req, res);

			//~: generate the data
			try
			{
				((BinarySource)provider).download(biny);
			}
			catch(ForbiddenException e)
			{
				res.sendError(HttpServletResponse.SC_FORBIDDEN, EX.e2en(e));
				return;
			}

			//~: feed the data
			biny.feed();
		}
		//~: has no provider
		else res.sendError(HttpServletResponse.SC_BAD_REQUEST,
		  "Either Model Bean, nor Spring Bean binary data " +
		  "providers are not specified!"
		);
	}


	/* protected static: Binary Source Context */

	protected static class BinyHTTP implements Biny
	{
		/* public: constructor */

		public BinyHTTP(HttpServletRequest req, HttpServletResponse res)
		{
			this.req     = req;
			this.res     = res;
			this.binary  = new BytesStream().setNotClose(true);
			this.headers = new HashMap<String, String>(7);
		}


		/* public: Binary Data Context */

		public OutputStream stream(int flags)
		  throws IOException
		{
			EX.assertx(stream == null, "Binary Stream is already opened!");
			this.stream = binary;

			//?: {use deflation}
			if(((flags & DEFLATE) != 0) && REQ.isGunZIPAllowed(req))
			{
				this.stream = new GZIPOutputStream(stream);
				headers.put("Content-Encoding", "gzip");
			}

			return this.stream;
		}

		public String       get(String param)
		{
			String res = req.getParameter(param);

			//?: {has no such a parameter} take a header
			return (res != null)?(res):SU.s2s(req.getHeader(param));
		}

		public String       set(String param, String value)
		{
			EX.asserts(param);

			if(value == null)
				return headers.remove(param);
			else
				return headers.put(param, value);
		}

		public void         status(Object status)
		{
			this.status = status;
		}


		/* public: Binary Data Context for HTTP */

		public BinyHTTP     setDownload(boolean download)
		{
			this.download = download;
			return this;
		}

		public void         feed()
		  throws IOException, ServletException
		{
			//~: set the headers
			for(Entry<String, String> e : headers.entrySet())
				if(!SU.sXe(e.getValue()))
					res.setHeader(e.getKey(), e.getValue());

			//?: {has status code}
			if(status instanceof Integer)
				res.setStatus((Integer) status);

			//~: content type
			String ct = SU.s2s(headers.get("Content-Type"));
			if(ct == null) ct = "application/octet-stream";
			res.setContentType(ct);

			//~: close the deflation stream
			stream.close(); //<-- binary stream is not closed

			//~: content length
			res.setContentLength((int) binary.length());

			//~: content disposition
			if(download && (binary.length() != 0L))
				if(SU.sXe(headers.get("Content-Disposition")))
					res.setHeader("Content-Disposition", "attachment;");

			//~: copy to the output stream
			try
			{
				if(binary.length() != 0L)
					binary.copy(res.getOutputStream());
			}
			finally
			{
				binary.setNotClose(false);
				binary.setNotCloseNext(false);
				binary.close();
			}
		}


		/* protected: request and response */

		protected final HttpServletRequest  req;
		protected final HttpServletResponse res;


		/* protected: configuration */

		protected boolean download = true;


		/* protected: context state */

		protected final BytesStream         binary;
		protected OutputStream              stream;
		protected final Map<String, String> headers;
		protected Object                    status;
	}
}