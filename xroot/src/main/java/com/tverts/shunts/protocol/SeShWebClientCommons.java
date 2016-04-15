package com.tverts.shunts.protocol;

/* Java */

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* Servlet */

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/* com.tverts: shunts protocol */

import com.tverts.shunts.protocol.SeShProtocolBase.SeShConnectionFailed;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShServletFailure;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShSystemFailure;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.streams.Base64Decoder;
import com.tverts.support.streams.Base64Encoder;
import com.tverts.support.streams.BytesStream;


/**
 * Implements HTTP conversation with this server.
 * Support class for {@link SeShProtocolWebBase}.
 *
 * This particular implementation utilizes
 * Apache HTTP Commons component library.
 * It supports the conversation using cookies.
 *
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
		EX.assertn(port);
		EX.assertn(request);

		//0: set request URL
		String url = EX.asserts(createURL(port));

		//1: encode the request
		byte[] payload; try
		{
			payload = createRequestBody(request);
		}
		catch(Throwable e)
		{
			throw new SeShProtocolError(e, "Error occured during ",
			  "the writing and encoding of Self-Shunt Request!");
		}

		//2: create the connection
		HttpURLConnection connection = null;
		InputStream       input      = null;
		try
		{
			try
			{
				//~: open the connection
				connection = (HttpURLConnection)(new URL(url).openConnection());

				//~: set the headers
				setRequestHeaders(connection);
			}
			catch(Throwable e)
			{
				throw new SeShProtocolError(e, "Error occured during creation of ",
				  "HTTP connection to the local Self-Shunt servlet!");
			}

			//3: send the request
			try
			{
				sendRequestPayload(connection, payload);
			}
			catch(java.net.ConnectException e)
			{
				throw new SeShConnectionFailed(e);
			}
			catch(Throwable e)
			{
				throw new SeShServletFailure(e, "Error occured during ",
				  "executing the Self-Shunt Request!");
			}

			if(breaked) throw new InterruptedException();

			//4: handle the response status
			try
			{
				input = handleResponseStatus(connection);
			}
			catch(Throwable e)
			{
				throw new SeShServletFailure(e, "Error occured during ",
				  "receiving the Self-Shunt Response!");
			}

			//5: decode shunt response
			SeShResponse result; try
			{
				result = readResponse(connection, input);
			}
			catch(SeShProtocolError e)
			{
				throw e;
			}
			catch(Exception e)
			{
				throw new SeShProtocolError(e, "Error occured during reading ",
				  "the Self Shunt Response instance from the HTTP stream!");
			}

			//5: {the response contains system error} throw it
			if(result.getSystemError() != null)
				throw new SeShSystemFailure(result);

			return result;
		}
		finally
		{
			try
			{
				if(input != null) try
				{
					input.close();
				}
				catch(Throwable e)
				{}
			}
			finally
			{
				if(connection != null)
					connection.disconnect();
			}
		}
	}


	/* protected: request and response processing */

	protected void          setRequestHeaders(HttpURLConnection c)
	  throws Exception
	{
		c.setRequestMethod("POST");
		c.setDoOutput(true);
		c.setUseCaches(false);

		//~: connection timeout
		if(getConnTimeout() > 0)
			c.setConnectTimeout((int) getConnTimeout());

		//~: socket timeout
		if(getSoTimeout() > 0)
			c.setReadTimeout((int) getSoTimeout());

		//Content-Type
		c.setRequestProperty("Content-Type", "application/octet-stream");

		//Content-Encoding
		c.setRequestProperty("Content-Encoding", "base64");
	}

	protected byte[]        createRequestBody(SeShRequest request)
	  throws Exception
	{
		//~: create encoding streams
		BytesStream        bos = new BytesStream().setNotCloseNext(true);
		ObjectOutputStream oos = new ObjectOutputStream(new Base64Encoder(bos));

		//!: write the object
		try
		{
			oos.writeObject(request);
			oos.close();

			return bos.bytes();
		}
		finally
		{
			bos.closeAlways();
		}
	}

	protected void          sendRequestPayload(HttpURLConnection c, byte[] p)
	  throws Exception
	{
		//~: Content-Length
		c.setRequestProperty("Content-Length", Integer.toString(p.length));

		//~: write the request body
		try(OutputStream o = c.getOutputStream())
		{
			o.write(p);
			o.flush();
			o.close();
		}
	}

	protected InputStream   handleResponseStatus(HttpURLConnection c)
	  throws Exception
	{
		//~: get the response stream
		InputStream input = c.getInputStream();

		//?: {got good status}
		if(c.getResponseCode() == SC_OK)
			return input;

		//?: {service is temporary unavailable}
		if(c.getResponseCode() == SC_SERVICE_UNAVAILABLE)
			throw new SeShConnectionFailed();

		//!: raise servlet failure
		throw new SeShServletFailure(
		  "Self Shunt response has bad HTTP status code: [",
		  c.getResponseCode(), "], status text: \n",
		  c.getResponseMessage());
	}

	protected SeShResponse  readResponse(HttpURLConnection c, InputStream s)
	  throws Exception
	{
		//~: check Content-Type header
		checkResponseContentType(c);

		//~: check Content-Encoding header
		checkResponseEncoding(c);

		//~: read the response object
		Object o; try
		{
			//~: open the content stream
			s = openContentStream(c, s);

			//~: and read the object
			o = new ObjectInputStream(s).readObject();
		}
		catch(Exception e)
		{
			throw new SeShServletFailure(e,
			  "Error occured while reading SeShResponse from the stream!");
		}

		//?: {the result object is undefined}
		if(o == null)
			throw new SeShServletFailure("SeShResponse is not defined!");

		//?: {wrong object type}
		if(!(o instanceof SeShResponse)) throw new SeShServletFailure(
		  "Self Shunt HTTP response instance has wrong Java class type: ",
		  LU.cls(o), "]!");

		return (SeShResponse)o;
	}

	protected InputStream   openContentStream(HttpURLConnection c, InputStream s)
	  throws Exception
	{
		String enc = getTransferEncoding(c);

		//?: {has Base64 transfer encoding}
		if("base64".equals(enc))
			return new Base64Decoder(s);

		return s;
	}

	protected void          checkResponseContentType(HttpURLConnection c)
	  throws Exception
	{
		String ct = getContentType(c);

		//?: {content type is not defined}
		if(ct == null) throw new SeShServletFailure(
		  "Self Shunt HTTP response has no Content-Type provided!");

		//?: {wrong content type}
		if(!"application/octet-stream".equals(ct))
			throw new SeShServletFailure("Self Shunt HTTP response has not ",
			  "supported  Content-Type: [", ct, "]!");
	}

	/**
	 * Finds the 'Content-Type' from the request given.
	 * The result is turned to lower case.
	 */
	protected String        getContentType(HttpURLConnection c)
	{
		String ct = SU.s2s(c.getHeaderField("Content-Type"));

		if(ct != null)
		{
			int i = ct.indexOf(';');
			if(i != -1) ct = SU.s2s(ct.substring(0, i));
		}

		return (ct == null)?(null):(ct.toLowerCase());
	}

	protected void          checkResponseEncoding(HttpURLConnection c)
	  throws Exception
	{
		String te = getTransferEncoding(c);

		//?: {has Base64 encoding}
		if("base64".equals(te))
			return;

		//?: {has unknown transfer encoding}
		if(te != null) throw new SeShServletFailure(
		  "Self Shunt HTTP response has the Content-Encoding ",
		  "header with unsupported value: [", te, "]!");
	}

	/**
	 * Finds the 'Content-Encoding'
	 * from the request given. The result is
	 * turned to lower case.
	 */
	protected String        getTransferEncoding(HttpURLConnection c)
	{
		String enc = SU.s2s(c.getHeaderField("Content-Encoding"));
		return (enc == null)?(null):(enc.toLowerCase());
	}
}