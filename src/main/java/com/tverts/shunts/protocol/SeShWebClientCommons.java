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

import com.tverts.shunts.protocol.SeShProtocolBase.SeShSystemFailure;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import static org.apache.commons.httpclient.params.HttpClientParams.*;

/* com.tverts: shunts protocol */

import com.tverts.shunts.protocol.SeShProtocolBase.SeShConnectionFailed;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShServletFailure;

/* com.tverts: support streams */

import com.tverts.support.streams.Base64Decoder;
import com.tverts.support.streams.Base64Encoder;

/**
 * Implements HTTP conversation with this server.
 * Support class for {@link SeShProtocolWeb}.
 *
 * This particular implementation utilizes
 * Apache HTTP Commons component library.
 * It supports the conversation using qookies.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShWebClientCommons
       extends SeShWebClientBase
{
	/* protected: connection issues) */

	protected SeShResponse  sendRequest (
	                    String      port,
	                    SeShRequest request
	                  )
	  throws SeShProtocolError, InterruptedException
	{
		if(port    == null) throw new IllegalArgumentException();
		if(request == null) throw new IllegalArgumentException();

		//0: set request URL
		String     url  = createURL(port);
		if(url == null) throw new IllegalStateException();

		//1: create POST request
		PostMethod post = new PostMethod(url);
		setRequestHeaders(post);

		try
		{
			post.setRequestEntity(createRequestBody(request));
		}
		catch(Exception e)
		{
			throw new SeShProtocolError(
			  "error occured when serializing Self Shunt Request " +
			  "into the body of the POST HTTP request!", e);
		}

		//2: send the HTTP request
		try
		{
			getClient().executeMethod(post);
		}
		catch(java.net.ConnectException e)
		{
			throw new SeShConnectionFailed();
		}
		catch(Exception e)
		{
			throw new SeShServletFailure(String.format(
			  "error occured when send POST HTTP request to the" +
			  "server by the URL: '%s'!", url), e);
		}

		if(breaked) throw new InterruptedException();

		//3: handle the response status
		handleResponseStatus(post);

		SeShResponse res;

		//4: decode shunt response
		try
		{
			res = readResponse(post);
		}
		catch(SeShProtocolError e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SeShProtocolError(
			  "error occured when deserializing Self Shunt " +
			  "Response instance from the HTTP stream!", e);
		}

		//5: {the response contains system error} throw it
		if(res.getSystemError() != null)
			throw new SeShSystemFailure(res);

		return res;
	}

	/* protected: request coding and decoding  */

	protected RequestEntity createRequestBody (
	                          SeShRequest request
	                        )
	  throws Exception
	{
		//create encoding streams
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		ObjectOutputStream    oos = new ObjectOutputStream(
		  new Base64Encoder(bos, 4));

		//!: write the object
		oos.writeObject(request);
		oos.close();

		//create the POST body entry
		return new ByteArrayRequestEntity(
		  bos.toByteArray(), "application/octet-stream");
	}

	protected void          setRequestHeaders(PostMethod post)
	{
		//Content-Type
		post.addRequestHeader(
		  "Content-Type",
		  "application/octet-stream"
		);

		//Content-Transfer-Encoding
		post.addRequestHeader(
		  "Content-Transfer-Encoding",
		  "base64"
		);
	}

	protected SeShResponse  readResponse(HttpMethod req)
	  throws Exception
	{
		String            ctp = getContentType(req);

		//?: {content type is not defined}
		if(ctp == null) throw new SeShServletFailure(
		  "Self Shunt HTTP response has no Content-Type provided!");

		//?: {wrong content type}
		if(!"application/octet-stream".equals(ctp))
			throw new SeShServletFailure(
			  "Self Shunt HTTP response has the Content-Type with " +
			  "not supported value!");

		InputStream       ins = req.getResponseBodyAsStream();
		String            ten = getTransferEncoding(req);

		//?: {has Base64 encoding}
		if("base64".equals(ten))
			ins = new Base64Decoder(ins);
		//?: {has unknown transfer encoding}
		else if(ten != null) throw new SeShServletFailure(String.format(
		  "Self Shunt HTTP response has the Content-Transfer-Encoding " +
		  "header with unsupported value: '%s'", ten));

		//read the response object
		ObjectInputStream ois = new ObjectInputStream(ins);
		Object            res = ois.readObject();

		ois.close();

		//?: {the result object is undefined}
		if(res == null) throw new SeShServletFailure(
		  "Self Shunt HTTP response is not defined!");

		//?: {wrong object type}
		if(!(res instanceof SeShResponse))
			throw new SeShServletFailure(
			  "Self Shunt HTTP response instance has wrong type!");

		return (SeShResponse)res;
	}

	/**
	 * Finds the 'Content-Type' from the request given.
	 * The result is turned to lower case.
	 */
	protected String        getContentType(HttpMethod req)
	{
		Header hdr = req.getResponseHeader("Content-Type");
		if(hdr == null) return null;

		String val = hdr.getValue();
		if(val != null) val = val.toLowerCase();

		int    ich = (val == null)?(-1):(val.indexOf(';'));
		return (ich != -1)?(val.substring(0, ich)):(val);
	}

	/**
	 * Finds the 'Content-Transfer-Encoding'
	 * from the request given.
	 *
	 * The result is turned to lower case.
	 */
	protected String        getTransferEncoding(HttpMethod req)
	{
		Header hdr = req.getResponseHeader(
		  "Content-Transfer-Encoding");
		if(hdr == null) return null;

		String val = hdr.getValue();
		return (val == null)?(null):(val.toLowerCase());
	}

	protected void          handleResponseStatus(HttpMethod req)
	  throws SeShProtocolError
	{
		//?: {got good status}
		if(req.getStatusCode() == SC_OK)
			return;

		//?: {service is temporary unavailable}
		if(req.getStatusCode() == SC_SERVICE_UNAVAILABLE)
			throw new SeShConnectionFailed();

		//!: raise servlet failure
		throw new SeShServletFailure(String.format(
		  "Self Shunt response has bad HTTP status code %d, status text: \n%s",
		  req.getStatusCode(), req.getStatusText()));

	}

	/* protected: HttpClient creation */

	protected HttpClient       getClient()
	{
		return (client != null)?(client)
		  :(client = createClient());
	}

	protected HttpClient       createClient()
	{
		return new HttpClient(buildClientParams());
	}

	protected HttpClientParams buildClientParams()
	{
		HttpClientParams p = new HttpClientParams();

		//URI encoding
		p.setParameter(
		  HTTP_URI_CHARSET,           "UTF-8");

		//content encoding
		p.setParameter(
		  HTTP_CONTENT_CHARSET,       "UTF-8");

		//TCP SO_TIMEOUT
		p.setIntParameter(
		  SO_TIMEOUT,                 getSoTimeout());

		//connection timeout
		p.setLongParameter(
		  CONNECTION_MANAGER_TIMEOUT, getConnTimeout());

		//redirects
		p.setBooleanParameter(
		  ALLOW_CIRCULAR_REDIRECTS,   false);

		p.setBooleanParameter(
		  REJECT_RELATIVE_REDIRECT,   false);

		p.setIntParameter(
		  MAX_REDIRECTS,              2);

		return p;
	}

	/* private: HTTP client */

	private HttpClient client;
}