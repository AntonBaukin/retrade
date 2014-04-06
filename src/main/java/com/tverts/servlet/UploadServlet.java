package com.tverts.servlet;

/* Java */

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/* Java Servlets */

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Apache File Upload Library */

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Closeable;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.beanOrNull;

/* com.tverts: servlets */

import com.tverts.servlet.Upload.UploadCtx;

/* com.tverts: model */

import com.tverts.model.ModelAccessPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Upload servlet supports two strategies:
 *
 *  · model bean referred with 'model' parameter;
 *  · Spring Bean referred with 'bean' parameter.
 *
 * Each variant requires the object instance to
 * implement {@link Upload} interface.
 *
 * For model bean the it is checked the owner of
 * the model is the same user issuing the request.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class UploadServlet extends GenericServlet
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

		//?: {not a POST request}
		if(!"POST".equalsIgnoreCase(req.getMethod()))
		{
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		//~: the target instance to provide the data
		Object upload = null;
		String param;

		//?: {upload as a model}
		if((param = SU.s2s(req.getParameter("model"))) != null)
		{
			//~: access the model  <-- this is secure access
			upload = EX.assertn(
			  ModelAccessPoint.model().readBean(param),
			  "Model Bean [", param, "] is not found!"
			);

			//?: {not supported}
			if(!(upload instanceof Upload)) throw EX.ass("Model Bean [",
			  param, "] is not a supported file upload strategy!");

		}
		//?: {upload as a bean}
		else if((param = SU.s2s(req.getParameter("bean"))) != null)
		{
			//~: access the spring bean
			upload = EX.assertn(beanOrNull(param),
			  "Spring Bean [", param, "] is not found!"
			);

			//?: {not supported}
			if(!(upload instanceof Upload)) throw EX.ass("Spring Bean [",
			  param, "] is not a supported file upload strategy!");
		}

		//~: invoke the upload strategy
		invoke(req, (Upload)upload);
	}

	protected void  invoke(HttpServletRequest req, Upload upload)
	  throws java.io.IOException, ServletException
	{
		//?: {this is a multi-part request}
		if(ServletFileUpload.isMultipartContent(req))
		{
			//~: create the context
			UploadMultipart ctx = new UploadMultipart(req);

			//c: for all the files
			try
			{
				while(ctx.nextFile())
					upload.upload(ctx);
			}
			catch(FileUploadException e)
			{
				throw new ServletException(e);
			}
		}
		//~: this is a plain body POST
		else
		{
			//~: create the context
			UploadBody ctx = new UploadBody(req);

			//~: invoke the strategy
			upload.upload(ctx);
		}

		//!: commit the strategy
		upload.commit();
	}


	/* upload contexts implementation */

	protected static class UploadBody implements UploadCtx
	{
		/* public: constructor */

		public UploadBody(HttpServletRequest request)
		{
			this.request = request;
		}


		/* public: Upload Context */

		public Set<String> paramsNames()
		{
			return request.getParameterMap().keySet();
		}

		public Set<String> headersNames()
		{
			if(headers != null)
				return headers;

			headers = new HashSet<String>(11);
			for(Enumeration<String> i = request.getHeaderNames();(i.hasMoreElements());)
				headers.add(i.nextElement());

			return headers;
		}

		public String      param(String name)
		{
			return request.getParameter(name);
		}

		public String[]    params(String name)
		{
			return request.getParameterValues(name);
		}

		public String      header(String name)
		{
			return request.getHeader(name);
		}

		public boolean     isLast()
		{
			return true;
		}

		public InputStream stream()
		  throws IOException
		{
			return (stream != null)?(stream):(stream = request.getInputStream());
		}

		public String      contentType()
		{
			return request.getContentType();
		}

		public String      fileName()
		{
			return null;
		}


		/* the request & the internal state */

		protected final HttpServletRequest request;
		protected Set<String>              headers;
		protected InputStream              stream;
	}

	protected static class UploadMultipart extends UploadBody
	{
		/* public: constructor */

		public UploadMultipart(HttpServletRequest request)
		{
			super(request);
		}


		/* public: control interface */

		public boolean nextFile()
		  throws IOException, FileUploadException
		{
			//~: clear stream reference
			if(stream instanceof Closeable) try
			{
				if(!((Closeable)stream).isClosed())
					stream.close();
			}
			finally
			{
				stream = null;
				this.file = null;
			}

			//?: {no iterator is opened yet}
			if(iterator == null)
				iterator = UPLOAD.getItemIterator(request);

			//~: seek for the next file item
			while(iterator.hasNext())
			{
				//~: open the next file
				this.file = iterator.next();

				//?: {it is a field} skip it
				if(this.file.isFormField())
					continue;

				return true;
			}

			return false;
		}


		/* public: Upload Context */

		public boolean     isLast()
		{
			try
			{
				return !EX.assertn(this.iterator).hasNext();
			}
			catch(Exception e)
			{
				throw EX.wrap(e);
			}
		}

		public InputStream stream()
		  throws IOException
		{
			if(stream != null) return stream;
			return stream = EX.assertn(this.file).openStream();
		}

		public String      contentType()
		{
			return SU.s2s(EX.assertn(this.file).getContentType());
		}

		public String      fileName()
		{
			return SU.s2s(EX.assertn(this.file).getName());
		}


		/* the state of the strategy */

		protected static final ServletFileUpload UPLOAD =
		  new ServletFileUpload();

		protected FileItemIterator iterator;
		protected FileItemStream   file;
	}
}