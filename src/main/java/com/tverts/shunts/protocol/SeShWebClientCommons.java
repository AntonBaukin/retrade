package com.tverts.shunts.protocol;

/* standard Java classes */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/* Servlet api */

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/* Apache HTTP Component (client) */

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import static org.apache.http.params.CoreConnectionPNames.*;
import static org.apache.http.params.CoreProtocolPNames.*;

/* com.tverts: shunts protocol */

import com.tverts.shunts.protocol.SeShProtocolBase.SeShConnectionFailed;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShServletFailure;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShSystemFailure;

/* com.tverts: support streams */

import com.tverts.support.streams.Base64Decoder;
import com.tverts.support.streams.Base64Encoder;


/**
 * Implements HTTP conversation with this server.
 * Support class for {@link SeShProtocolWebBase}.
 *
 * This particular implementation utilizes
 * Apache HTTP Commons component library.
 * It supports the conversation using qookies.
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShWebClientCommons
       extends SeShWebClientBase
{
	/* protected: connection issues */

	protected SeShResponse  sendRequest(String port, SeShRequest request)
	  throws SeShProtocolError, InterruptedException
	{
		if(port    == null) throw new IllegalArgumentException();
		if(request == null) throw new IllegalArgumentException();

		//0: set request URL
		String   url  = createURL(port);
		if(url == null) throw new IllegalStateException();

		//1: create POST request
		HttpPost req = new HttpPost(url);
		setRequestHeaders(req);

		try
		{
			req.setEntity(createRequestBody(request));
		}
		catch(Exception e)
		{
			throw new SeShProtocolError(
			  "Error occured during teh encoding Self Shunt Request " +
			  "into the body of the POST HTTP request!", e);
		}

		//2: send the HTTP request
		HttpResponse res;

		try
		{
			res = getClient().execute(req);
		}
		catch(java.net.ConnectException e)
		{
			throw new SeShConnectionFailed();
		}
		catch(Exception e)
		{
			throw new SeShServletFailure(String.format(
			  "Error occured when send POST HTTP request to the" +
			  "server by the URL: '%s'!", url), e);
		}

		if(breaked) throw new InterruptedException();

		//3: handle the response status
		handleResponseStatus(res);

		SeShResponse result;

		//4: decode shunt response
		try
		{
			result = readResponse(res);
		}
		catch(SeShProtocolError e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SeShProtocolError(
			  "Error occured during the reading Self Shunt " +
			  "Response instance from the HTTP stream!", e);
		}

		//5: {the response contains system error} throw it
		if(result.getSystemError() != null)
			throw new SeShSystemFailure(result);

		return result;
	}

	/* protected: request and response processing */

	protected HttpEntity    createRequestBody(SeShRequest request)
	  throws Exception
	{
		//create encoding streams
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		ObjectOutputStream    oos = new ObjectOutputStream(
		  new Base64Encoder(bos, 4));

		//!: write the object
		oos.writeObject(request);
		oos.close();

		//~: create the entity
		ByteArrayEntity       res =
		  new ByteArrayEntity(bos.toByteArray());

		res.setContentType("application/octet-stream");
		return res;
	}

	protected void          setRequestHeaders(HttpMessage msg)
	{
		//Content-Type
		msg.addHeader(
		  "Content-Type",
		  "application/octet-stream"
		);

		//Content-Transfer-Encoding
		msg.addHeader(
		  "Content-Transfer-Encoding",
		  "base64"
		);
	}

	protected SeShResponse  readResponse(HttpResponse res)
	  throws Exception
	{
		//~: check Content-Type header
		checkResponseContentType(res);

		//~: check Content-Transfer-Encoding header
		checkResponseEncoding(res);

		//~: open the content stream
		InputStream       ins = openContentStream(res);
		ObjectInputStream ois;
		Exception         err = null;
		Object            obj = null;

		//~read the response object
		try
		{
			ois = new ObjectInputStream(ins);
			obj = ois.readObject();
			ins = ois; //<-- prepare for close
		}
		catch(Exception e)
		{
			err = e;
		}
		finally
		{
			try
			{
				ins.close();
			}
			catch(Exception e)
			{
				if(err == null) err = e;
			}
		}

		//?: {has stream error}
		if(err != null) throw new SeShServletFailure(
		  "Error occured while reading SeShResponse from the stream!", err);

		//?: {the result object is undefined}
		if(obj == null) throw new SeShServletFailure(
		  "SeShResponse is not defined!");

		//?: {wrong object type}
		if(!(obj instanceof SeShResponse)) throw new SeShServletFailure(
		  "Self Shunt HTTP response instance has wrong Java class type!");

		return (SeShResponse)obj;
	}

	protected InputStream   openContentStream(HttpResponse res)
	  throws Exception
	{
		InputStream ins;

		//~: open the response' entity' content
		try
		{
			ins = res.getEntity().getContent();
			if(ins == null) throw new NullPointerException();
		}
		catch(Exception e)
		{
			throw new SeShServletFailure(
			  "Can't read the content data from the HTTP response!", e);
		}

		//?: {has Base64 transfer encoding} wrap with the decoder
		if("base64".equals(getTransferEncoding(res)))
			ins = new Base64Decoder(ins);

		return ins;
	}

	protected void          checkResponseContentType(HttpResponse res)
	  throws Exception
	{
		String ct = getContentType(res);

		//?: {content type is not defined}
		if(ct == null) throw new SeShServletFailure(
		  "Self Shunt HTTP response has no Content-Type provided!");

		//?: {wrong content type}
		if(!"application/octet-stream".equals(ct)) throw new SeShServletFailure(
		  "Self Shunt HTTP response has the Content-Type with " +
		  "not supported value!");
	}

	/**
	 * Finds the 'Content-Type' from the request given.
	 * The result is turned to lower case.
	 */
	protected String        getContentType(HttpMessage msg)
	{
		Header hdr = msg.getFirstHeader(
		  "Content-Type");
		if(hdr == null) return null;

		String val = hdr.getValue();
		if(val != null) val = val.toLowerCase();

		int    ich = (val == null)?(-1):(val.indexOf(';'));
		return (ich != -1)?(val.substring(0, ich)):(val);
	}

	protected void          checkResponseEncoding(HttpResponse res)
	  throws Exception
	{
		String te = getTransferEncoding(res);

		//?: {has Base64 encoding}
		if("base64".equals(te))
			return;

		//?: {has unknown transfer encoding}
		if(te != null) throw new SeShServletFailure(String.format(
		  "Self Shunt HTTP response has the Content-Transfer-Encoding " +
		  "header with unsupported value: '%s'", te));
	}

	/**
	 * Finds the 'Content-Transfer-Encoding'
	 * from the request given.
	 *
	 * The result is turned to lower case.
	 */
	protected String        getTransferEncoding(HttpMessage msg)
	{
		Header hdr = msg.getFirstHeader(
		  "Content-Transfer-Encoding");
		if(hdr == null) return null;

		String val = hdr.getValue();
		return (val == null)?(null):(val.toLowerCase());
	}

	protected void          handleResponseStatus(HttpResponse res)
	  throws SeShProtocolError
	{
		//?: {got good status}
		if(res.getStatusLine().getStatusCode() == SC_OK)
			return;

		//?: {service is temporary unavailable}
		if(res.getStatusLine().getStatusCode() == SC_SERVICE_UNAVAILABLE)
			throw new SeShConnectionFailed();

		//!: raise servlet failure
		throw new SeShServletFailure(String.format(
		  "Self Shunt response has bad HTTP status code %d, status text: \n%s",

		  res.getStatusLine().getStatusCode(),
		  res.getStatusLine().getReasonPhrase()));
	}

	/* protected: HttpClient creation */

	protected HttpClient    getClient()
	{
		return (client != null)?(client)
		  :(client = createClient());
	}

	protected HttpClient    createClient()
	{
		return new DefaultHttpClient(buildClientParams());
	}

	protected HttpParams    buildClientParams()
	{
		HttpParams p = new BasicHttpParams();

		//headers encoding
		p.setParameter(
		  HTTP_ELEMENT_CHARSET,       "UTF-8");

		//content encoding
		p.setParameter(
		  HTTP_CONTENT_CHARSET,       "UTF-8");

		//TCP SO_TIMEOUT
		p.setIntParameter(
		  SO_TIMEOUT,                 getSoTimeout());

		//connection timeout
		p.setIntParameter(
		  CONNECTION_TIMEOUT,         getConnTimeout());

		return p;
	}

	/* private: HTTP client */

	private HttpClient client;
}