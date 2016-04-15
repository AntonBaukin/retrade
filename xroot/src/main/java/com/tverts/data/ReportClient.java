package com.tverts.data;

/* Java */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * Client that sends requests to the
 * reports merging server.
 *
 * This is a prototype Spring Bean.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class ReportClient
{
	/* public: initialization */

	/**
	 * The gun-zipped report template file.
	 */
	public ReportClient setReport(byte[] report)
	{
		this.report = report;
		return this;
	}

	/**
	 * The XML data file. (Not gun-zipped.)
	 */
	public ReportClient setData(BytesStream data)
	{
		this.data = data;
		return this;
	}

	/**
	 * The resulting report file, not gun-zipped.
	 */
	public ReportClient setResult(BytesStream result)
	{
		this.result = result;
		return this;
	}

	public ReportClient setFormat(ReportFormat format)
	{
		EX.assertx(
		  ReportFormat.PDF.equals(format) ||
		  ReportFormat.XLS.equals(format)
		);

		this.format = format;
		return this;
	}


	/* public: bean configuration */

	public void setHost(String host)
	{
		this.host = EX.assertn(SU.s2s(host));
	}

	public void setPort(int port)
	{
		EX.assertx(port > 0);
		this.port = port;
	}

	public void setPath(String path)
	{
		this.path = SU.s2s(path);
	}

	public void setSchema(String schema)
	{
		this.schema = SU.s2s(schema);
	}

	/**
	 * Optional parameter of socket timeout
	 * in milliseconds.
	 */
	public void setTimeout(Integer timeout)
	{
		EX.assertx((timeout == null) || (timeout > 0));
		this.timeout = timeout;
	}


	/* public: communication */

	public void    request()
	  throws IOException
	{
		EX.assertx(EX.assertn(report).length   != 0L);
		EX.assertx(EX.assertn(data).length()   != 0L);
		EX.assertx(EX.assertn(result).length() == 0L);

		HttpURLConnection connect = (HttpURLConnection)
		  new URL(getURL()).openConnection();
		OutputStream      ostream = null;
		InputStream       istream = null;
		Throwable         error   = null;

		try
		{
			//~: set HTTP + connection parameters
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			connect.setUseCaches(false);
			if(timeout != null)
				connect.setConnectTimeout(timeout);

			//~: multi-part boundary
			String boundary = genMultipartBoundary();
			connect.setRequestProperty("Content-Type",
			  "multipart/form-data; boundary=" + boundary);

			//~: simulate writing the content (without the data)
			BytesStream sim = new BytesStream();
			writeMultipart(sim, boundary, true); sim.close();
			connect.setRequestProperty("Content-Length", Long.toString(
			  sim.length() + report.length + data.length()
			));

			//~: write the request body
			ostream = connect.getOutputStream();
			writeMultipart(ostream, boundary, false);

			//~: close the output stream
			try
			{
				ostream.flush();
				ostream.close();
			}
			finally
			{
				ostream = null;
			}

			//~: connect to the server
			connect.connect();

			//~: response code & content type
			this.serverCode  = connect.getResponseCode();
			this.contentType = SU.sXs(SU.s2s(connect.getHeaderField("Content-Type")));

			//~: get the resulting stream
			if(this.serverCode == 200)
				istream = connect.getInputStream();
			else
				istream = connect.getErrorStream();

			//~: pump the data
			result.write(istream);

			//?: {has error text}
			if((serverCode != 200) && contentType.startsWith("text/"))
				if(result.length() != 0L) try
				{
					this.errorText = SU.s2s(new String(result.bytes(), "UTF-8"));
					result.erase(); //<-- clear the buffer
				}
				catch(Throwable e)
				{
					//~: ignore error here
				}

			if((serverCode != 200) && (errorText == null))
				errorText =  connect.getResponseMessage();
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			//~: close the output stream
			if(ostream != null) try
			{
				ostream.close();
			}
			catch(Exception e)
			{
				if(error == null) error = e;
			}

			//~: close the input stream
			if(istream != null) try
			{
				istream.close();
			}
			catch(Exception e)
			{
				if(error == null) error = e;
			}

			//~: close the connection
			if(connect != null) try
			{
				connect.disconnect();
			}
			catch(Exception e)
			{
				if(error == null) error = e;
			}
		}

		if(error instanceof IOException)
			throw (IOException) error;
		else if(error != null)
			throw EX.wrap(error);
	}

	/**
	 * Just closes all the streams configured.
	 */
	public void    close()
	{
		data.close();
		result.close();
	}

	public String  getURL()
	{
		StringBuilder s = new StringBuilder(64);

		//~: schema
		s.append((schema != null)?(schema):("http"));
		s.append("://");

		//~: host
		s.append(EX.assertn(SU.s2s(host)));

		//~: port
		if(port != 80)
			s.append(':').append(port);

		//~: path
		if(path != null)
		{
			if((SU.last(s) != '/') && (SU.first(path) != '/'))
				s.append('/');
			s.append(path);
		}

		//?: {has '?'}
		if(s.indexOf("?") == -1)
		{
			if(SU.last(s) == '/')
				s.delete(s.length() - 1, s.length());
			s.append('?');
		}

		//~: format parameter
		EX.assertn(format);
		if(SU.last(s) != '?') s.append('&');
		s.append("format=").append(format.name());

		return s.toString();
	}

	public boolean isSucceeded()
	{
		return serverCode == 200;
	}

	public int     getServerCode()
	{
		return serverCode;
	}

	public String  getContentType()
	{
		return contentType;
	}

	public String  getErrorText()
	{
		return errorText;
	}


	/* protected: communications */

	protected String genMultipartBoundary()
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(report);
			data.digest(md);

			return new String(SU.bytes2hex(md.digest()));
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
	}

	protected void   writeMultipart(OutputStream os, String boundary, boolean simulate)
	  throws IOException
	{
		final byte[] N = new byte[]{ 0x0D, 0x0A };
		final byte[] H = "--".getBytes("ASCII");
		final byte[] B = boundary.getBytes("ASCII");

		final byte[] R = ("Content-Disposition: form-data; " +
		  "name=\"report\"").getBytes("ASCII");

		final byte[] D = ("Content-Disposition: form-data; " +
		  "name=\"xml-data\"").getBytes("ASCII");

		final byte[] G = "Content-Type: application/x-gzip".getBytes("ASCII");

		//~: report section boundary
		os.write(N); os.write(H); os.write(B); os.write(N);

		//~: report content disposition + type
		os.write(R); os.write(N); os.write(G); os.write(N);

		//~: report content
		os.write(N); if(!simulate)
			os.write(report);

		//~: data section boundary
		os.write(N); os.write(H); os.write(B); os.write(N);

		//~: data content disposition + type
		os.write(D); os.write(N); os.write(G); os.write(N);

		//~: data content
		os.write(N); if(!simulate)
			data.copy(os);

		//~: closing
		os.write(N); os.write(H); os.write(B); os.write(H); os.write(N);
	}


	/* protected: the data */

	protected byte[]      report;
	protected BytesStream data;
	protected BytesStream result;


	/* protected: request */

	protected ReportFormat format;
	protected String       host   = "localhost";
	protected int          port   = 8080;
	protected String       path   = "birt/report";
	protected String       schema = "http";
	protected Integer      timeout;


	/* protected: communication state */

	protected int          serverCode;
	protected String       contentType;
	protected String       errorText;
}