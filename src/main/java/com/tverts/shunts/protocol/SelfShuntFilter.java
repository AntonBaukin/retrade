package com.tverts.shunts.protocol;

/* Java */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/* tverts.com: servlet (filters) */

import com.tverts.servlet.REQ;
import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;

/* tverts.com: shunt (service) */

import com.tverts.shunts.service.SelfShuntService;

/* tverts.com: support (utils, streams) */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.streams.Base64Decoder;
import com.tverts.support.streams.Base64Encoder;


/**
 * Over the comments the shunt processing servlet
 * is sometimes named. But there is no such a
 * servlet. Instead there is this filter.
 *
 * Filter detects that the request to the system
 * is a shunt request by checking the request path.
 *
 * The filter reads the serialized request objects,
 * delegates the request to the service this filter
 * is related with, then sends back the serialized
 * response object.
 *
 * Note that the filter is able to work with one
 * shunt service instance only. If you need more
 * HTTP handling (web) services (what is rare),
 * create another filter instance mapped by
 * differ servlet path.
 *
 * As it is must be, the filter instance is
 * thread-safe (reentable).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SelfShuntFilter
       extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		//?: {this request is not a shunt request} skip it
		if(isShuntRequest(task)) try
		{
			task.setBreaked();
			handleShuntRequest(task);
		}
		catch(Throwable e)
		{
			task.setError(e);
		}
	}

	public void closeFilter(FilterTask task)
	{}


	/* public: parameters of the filter */

	/**
	 * Refers a Self Shunt Service this filter
	 * works with. Has no default value.
	 */
	public SelfShuntService getService()
	{
		return service;
	}

	public void setService(SelfShuntService service)
	{
		this.service = EX.assertn(service);
	}

	/**
	 * The name of the "servlet". This servlet (or JSP)
	 * doesn't exist actually, but there must be some
	 * server path name to be handled by this filer.
	 */
	public static final String DEF_SERVLET   =
	  "/go/self-shunt";

	/**
	 * The virtual path to the shunt processing servlet.
	 * Note that there is no such a servlet, but this filter.
	 *
	 * The path must be coherent with the web protocol
	 * of the service this filter is attached to.
	 */
	public String getShuntServlet()
	{
		return shuntServlet;
	}

	public void   setShuntServlet(String shuntServlet)
	{
		EX.asserts(shuntServlet);

		if(!shuntServlet.startsWith("/"))
			shuntServlet = "/" + shuntServlet;

		this.shuntServlet = shuntServlet;
	}


	/* protected: request handling */

	protected void    handleShuntRequest(FilterTask task)
	  throws Throwable
	{
		SelfShuntService service  = getService();
		SeShRequest      request  = null;
		SeShResponse     response = null;
		Throwable        error    = null;

		//?: {has no service configured}
		if(service == null)
			error = new IllegalStateException(
			  "Self Shunt Servlet has no Shunt service defined!");

		//0: read the request
		if(error == null) try
		{
			request = readRequest(task);
		}
		catch(Throwable e)
		{
			error = e;
		}

		//1: execute the request in the service
		if(request != null) try
		{
			response = service.executeRequest(request);
		}
		catch(Throwable e)
		{
			error = e;
		}

		//?: {has no response}
		if((request == null) && (error == null))
			error = new IllegalStateException(
			  "Self Shunt Servlet got null response from the " +
			  "Self Shunt Service it is connected to");

		//2: {has an error} create the response with error
		if(error != null)
		{
			response = new SeShResponseBase(request);
			((SeShResponseBase)response).setSystemError(error);
		}

		//3: write the response to the servlet result
		writeResponse(task, response);
	}


	/* protected: filter request issues */

	/**
	 * Answers yes when the servlet path of the
	 * request equals to the configured servlet path,
	 * and the content type is 'application/octet-stream'.
	 */
	protected boolean isShuntRequest(FilterTask task)
	{
		return FilterStage.REQUEST.equals(task.getFilterStage()) &&
		  isShuntPath(task) && isShuntContentType(task);
	}

	protected boolean isShuntPath(FilterTask task)
	{
		return getShuntServlet().equals(
		  REQ.getRequestPath(task.getRequest()));
	}

	protected boolean isShuntContentType(FilterTask task)
	{
		return "application/octet-stream".equals(getContentType(task));
	}

	/**
	 * Finds the 'Content-Type' from the request given.
	 * The result is turned to lower case.
	 */
	protected String  getContentType(FilterTask task)
	{
		String res = SU.s2s(task.getRequest().getContentType());
		return (res == null)?(null):(res.toLowerCase());
	}

	/**
	 * Finds the 'Content-Encoding'
	 * from the request given.
	 *
	 * The result is turned to lower case.
	 */
	protected String  getTransferEncoding(FilterTask task)
	{
		String res = SU.s2s(task.getRequest().
		  getHeader("Content-Encoding"));
		return (res == null)?(null):(res.toLowerCase());
	}

	/* protected: request and response streaming */

	/**
	 * Reads the request from the body of HTTP POST
	 * request. Supports BASE64 encoding. Does not
	 * allow to return {@code null} instance.
	 */
	protected SeShRequest readRequest(FilterTask task)
	  throws Exception
	{
		//?: {unsupported transfer encoding}
		String ten = getTransferEncoding(task);
		if((ten != null) && !ten.equals("base64"))
			throw EX.arg("Self Shunt Servlet got the request with Content-",
			  "Transfer-Encoding header having unsupported value: [", ten, "]!");

		//?: {has BASE64 encoding} unwrap it
		InputStream ins = task.getRequest().getInputStream();
		if(ten != null)
			ins = new Base64Decoder(ins);

		//!: read the object
		Object res;
		try(ObjectInputStream ois = new ObjectInputStream(ins))
		{
			res = ois.readObject();
		}

		//?: {the object is not of a request class}
		if(!(res instanceof SeShRequest))
			throw EX.state("Self Shunt Servlet got the request object of the class [",
			  LU.cls(res), "] that is not a Self Shunt Request class!");

		return (SeShRequest)res;
	}

	/**
	 * Serializes the shunt response instance to the
	 * servlet output stream. Supports BASE64 encoding
	 * that is set when the request came with this
	 * transfer encoding header.
	 */
	protected void        writeResponse(FilterTask task, SeShResponse response)
	  throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		OutputStream          ous = bos;
		ObjectOutputStream    oos;

		//0: set the headers
		setResponseHeaders(task);

		//?: {has BASE64 encoding in the request} wrap with it
		if("base64".equals(getTransferEncoding(task)))
			ous = new Base64Encoder(ous);

		//!: write the response object
		oos = new ObjectOutputStream(ous);
		oos.writeObject(response);
		oos.close();

		//!: write the bytes actually
		task.getResponse().getOutputStream().
		  write(bos.toByteArray());
	}

	protected void        setResponseHeaders(FilterTask task)
	{
		//the content type
		task.getResponse().setContentType("application/octet-stream");

		//?: {has BASE64 encoding in the request}
		if("base64".equals(getTransferEncoding(task)))
			task.getResponse().setHeader(
			  "Content-Encoding", "base64");
	}


	/* private: parameters of the filter */

	private SelfShuntService service;
	private String           shuntServlet = DEF_SERVLET;
}