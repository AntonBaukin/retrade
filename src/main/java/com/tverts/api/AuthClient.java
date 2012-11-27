package com.tverts.api;

/* standard Java classes */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/* Java API for XML Binding */

import javax.xml.bind.JAXBContext;



/**
 * Client providing authentication to the group
 * of 'ReTrade' applications over HTTP.
 *
 * Create the client instance, login, then send
 * the requests to the remote system.
 *
 * The authentication client is not thread safe
 * by the nature of the protocol: each request
 * has the incremented sequence number private
 * to the session, and the server denies the
 * requests with the number lower then the
 * previously received valid request.
 *
 * If you send several requests in concurrent,
 * they may reach the server not in the order
 * of creating requests.
 *
 * You have to implement requests queueing
 * for multi-threaded applications or just
 * block the concurrent requests manually.
 * This implementation has no internal locks.
 *
 * Authentication protocol does not require
 * a secured connection to login. The request
 * sent has message digest, but are not encrypted.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AuthClient
{
	/* public: constructor */

	public AuthClient()
	{
		this.sequence    = new AtomicLong();
		this.random      = createRandom();
		this.jaxbContext = createJAXBContext();
	}


	/* authentication error */

	public static class AuthError extends RuntimeException
	{
		/* public: constructor */

		public AuthError(String message)
		{
			super(message);
		}

		public AuthError(Throwable cause, String message)
		{
			super(message, cause);
		}

		public AuthError(Throwable cause)
		{
			super(cause);
		}


		/* public: AuthClient interface */

		public AuthClient getAuthClient()
		{
			return authClient;
		}

		public AuthError  setAuthClient(AuthClient authClient)
		{
			this.authClient = authClient;
			return this;
		}

		public String     getAuthStep()
		{
			return authStep;
		}

		public AuthError  setAuthStep(String authStep)
		{
			this.authStep = authStep;
			return this;
		}


		/* private: error info */

		private AuthClient authClient;
		private String     authStep;
	}


	/* public: initialization */

	public String     getURL()
	{
		return url;
	}

	/**
	 * HTTP URL to access the authentication application.
	 * The format is the following:
	 *
	 *   [http://]$S[:$P][/$C]
	 *
	 * $S  is server host name (or ip);
	 * $P  optional port number (80);
	 * $C  optional application path (/auth/).
	 */
	public AuthClient initURL(String url)
	{
		if((url = s2s(url)) == null)
			throw new IllegalArgumentException();

		if(!url.startsWith("http://"))
			url = "http://" + url;
		if(!url.endsWith("/"))
			url += "/";

		//-->  http://$S:$P/$C/
		//-->  http://$S:$P/
		//-->  http://$S/

		//~: insert port number
		int pi = url.indexOf(':', 7);
		if(pi == -1) //<-- http:
		{
			pi  = url.indexOf('/', 7); //<-- after "http://"
			url = url.substring(0, pi) + ":80" + url.substring(pi);
		}

		//-->  http://$S:$P/$C/
		//-->  http://$S:$P/

		int ci = url.indexOf('/', 7);
		if(ci == url.length() - 1)
			url += "auth/";

		this.url = url;
		return this;
	}

	public String     getDomain()
	{
		return domain;
	}

	/**
	 * Integer value with the client domain key.
	 */
	public AuthClient initDomain(String domain)
	{
		if((domain = s2s(domain)) == null)
			throw new IllegalArgumentException();

		this.domain = domain;
		return this;
	}

	public String     getLogin()
	{
		return login;
	}

	public AuthClient initLogin(String login)
	{
		if((login = s2s(login)) == null)
			throw new IllegalArgumentException();

		this.login = login;
		return this;
	}

	/**
	 * Returns SHA-1 hash of the password string
	 * (converted to UTF-8 bytes) as HEX encoded text.
	 */
	public String     getPasshash()
	{
		return passhash;
	}

	/**
	 * Does not store the password directly,
	 * but as a hash only.
	 */
	public AuthClient initPassword(String password)
	{
		if((password == null) || password.isEmpty())
			throw new IllegalArgumentException();

		try
		{
			this.passhash = digestHex(
			  (Object)password.getBytes("UTF-8"));
		}
		catch(Exception e)
		{
			throw new AuthError(e,
			  "Error occured when creating password hash!");
		}

		return this;
	}

	public String     getSessionId()
	{
		return sessionId;
	}

	public Integer    getTimeout()
	{
		return timeout;
	}

	/**
	 * Optional parameter of socket timeout
	 * in milliseconds.
	 */
	public AuthClient initTimeout(Integer timeout)
	{
		this.timeout = timeout;
		return this;
	}


	/* public: authentication */

	/**
	 * Issues login of the configured user.
	 * A Authentication Session is created
	 * on the server side at this type.
	 *
	 * The session may be closed explicitly,
	 * or it would be closed by the server
	 * on the session timeout.
	 *
	 * This method is not thread-safe.
	 * It may be invoked only once if the
	 * login was successful.
	 */
	public void login()
	  throws AuthError
	{
		//~: check the parameters
		String err = checkBeforeLogin();
		if(err != null)
			throw new AuthError(err).setAuthClient(this).
			  setAuthStep("validate");

		//~: issue greet step
		String greet;

		try
		{
			greet = stepGreet();
		}
		catch(AuthError e)
		{
			throw e.setAuthClient(this).setAuthStep("greet");
		}
		catch(Exception e)
		{
			throw new AuthError(e).
			  setAuthClient(this).setAuthStep("greet");
		}

		//~: issue login step
		try
		{
			stepLogin(greet);
		}
		catch(AuthError e)
		{
			throw e.setAuthClient(this).setAuthStep("login");
		}
		catch(Exception e)
		{
			throw new AuthError(e).
			  setAuthClient(this).setAuthStep("login");
		}

		//~: issue touch step (the check)
		try
		{
			String status = request("touch", null);

			if(!"touched".equals(status))
				throw new AuthError(String.format(
				  "Authentication Session [%s] is invalid " +
				  "or was unexpectably closed!", getSessionId()
				));
		}
		catch(AuthError e)
		{
			throw e.setAuthClient(this).setAuthStep("touch");
		}
		catch(Exception e)
		{
			throw new AuthError(e).
			  setAuthClient(this).setAuthStep("touch");
		}
	}

	/**
	 * Explicitly close the authentication session.
	 * This client may not be used again.
	 *
	 * This method is not thread-safe.
	 */
	public void close()
	  throws AuthError
	{
		//~: check the session exists
		String err = checkSession();
		if(err != null)
			throw new AuthError(err).setAuthClient(this).
			  setAuthStep("validate");

		//~: issue close step (the check)
		try
		{
			String status = request("close", null);

			if(!"closed".equals(status))
				throw new AuthError(String.format(
				  "Authentication Session [%s] was not closed!",
				  getSessionId()
				));
		}
		catch(AuthError e)
		{
			throw e.setAuthClient(this).setAuthStep("close");
		}
		catch(Exception e)
		{
			throw new AuthError(e).
			  setAuthClient(this).setAuthStep("touch");
		}
	}

	/**
	 * Touches the session record stored by the server.
	 * Prevents the session expiration of clients issuing
	 * requests with long pauses (hours).
	 */
	public void touch()
	  throws AuthError
	{
		//~: check the session exists
		String err = checkSession();
		if(err != null)
			throw new AuthError(err).setAuthClient(this).
			  setAuthStep("validate");

		//~: issue close step (the check)
		try
		{
			String status = request("touch", null);

			if(!"touched".equals(status))
				throw new AuthError(String.format(
				  "Authentication Session [%s] was not touched!",
				  getSessionId()
				));
		}
		catch(AuthError e)
		{
			throw e.setAuthClient(this).setAuthStep("touch");
		}
		catch(Exception e)
		{
			throw new AuthError(e).
			  setAuthClient(this).setAuthStep("touch");
		}
	}


	/* public: requests processing */

	/**
	 * Sends authenticated request to the server.
	 *
	 * Server-generated errors are returned in
	 * the resulting {@link Pong} object, unlike
	 * the {@link #xsend(Ping)} method.
	 *
	 * Note that not a receiving Pings would produce
	 * {@code null} Ping results in the most cases.
	 */
	public Pong send(Ping ping)
	  throws AuthError
	{
		//~: check the session exists
		String err = checkSession();
		if(err != null)
			throw new AuthError(err).setAuthClient(this).
			  setAuthStep("validate");

		//~: Ping type: request | receive
		boolean receive = (ping.getRequest() == null);

		//~: request type
		if(!receive)
			ping.setType(ping.getRequest().getClass().getName());


		//<: create POST payload

		byte[] payload;

		//?: {a requesting ping} write the Ping object as XML
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
			OutputStreamWriter    osw = new OutputStreamWriter(bos, "UTF-8");

			//~: write the object to the stream
			getJAXBContext().createMarshaller().
			  marshal(ping, osw); //!: write the Ping itself

			//~: get the result bytes
			osw.flush();  osw.close();
			payload = bos.toByteArray();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(
			  "Error occured while writing Ping object into XML!", e);
		}

		//>: create POST payload


		//!: create the URL
		StringBuilder url = new StringBuilder(getURL().length() + 24).
		  append(getURL());

		//?: {a receiving ping}
		if(receive)
			url.append("servlet/receive");
		else
			url.append("servlet/request");

		//!: send the request
		try
		{
			payload = post(url.toString(),
			  receive?("receive"):("request"), payload);
		}
		catch(Exception e)
		{
			throw new IllegalStateException(
			  "Error occured while executing " +
			  "POST HTTP request to the server!", e
			);
		}

		//?: {has no bytes}
		if((payload != null) && (payload.length == 0))
			payload = null;


		//?: {the response is empty} no resulting Pong
		if(payload == null)
			return null;

		//<: xml to object
		Object pong;

		try
		{
			InputStreamReader isr = new InputStreamReader(
			  new ByteArrayInputStream(payload), "UTF-8");

			pong = getJAXBContext().createUnmarshaller().unmarshal(
			  new InputStreamReader(new ByteArrayInputStream(payload), "UTF-8"));
		}
		catch(Exception e)
		{
			throw new IllegalStateException(
			  "Error occured while reading Pong object from XML!", e);
		}
		//>: xml to object

		if(!(pong instanceof Pong))
			throw new IllegalStateException(
			  "Server had returned not a Pong instance!");

		return (Pong)pong;
	}

	/**
	 * Sends authenticated request to the server.
	 *
	 * Server-side processing errors are reported
	 * as {@link PongError} exceptions.
	 */
	public Pong xsend(Ping ping)
	  throws AuthError, PongError
	{
		Pong pong;

		try
		{
			pong = this.send(ping);
		}
		catch(AuthError e)
		{
			throw e;
		}
		catch(RuntimeException e)
		{
			throw new PongError(e, ping);
		}

		if((pong != null) && (pong.getError() != null))
			throw new PongError(ping, pong);
		return pong;
	}


	/* protected: authentication parts */

	protected String checkBeforeLogin()
	{
		if(getURL() == null)
			return "Initialize URL parameter!";

		if(getDomain() == null)
			return "Initialize Domain parameter!";

		if(getLogin() == null)
			return "Initialize Login parameter!";

		if(getPasshash() == null)
			return "Initialize Password parameter!";

		if(getSessionId() != null)
			return "Already logged in! (Want to resuse client?)";

		return null;
	}

	protected String checkSession()
	{
		if(getSessionId() == null)
			return "Not authenticated (logged in)!";

		return null;
	}

	/**
	 * Send 'greet' request to the server.
	 * Returns the server-generated code.
	 * Decodes Rs (server random) bytes.
	 */
	protected String stepGreet()
	  throws Exception
	{
		return gets(getURL() + "servlet/session?step=greet");
	}

	protected void   stepLogin(String greet)
	  throws Exception
	{
		byte[] Rs = null;

		//<: decode Rs bytes

		int Rs_b = greet.indexOf("Rs=");
		int Rs_e = (Rs_b == -1)?(-2):(greet.indexOf('&', Rs_b));
		if(Rs_e == -1) Rs_e = greet.length();

		if((Rs_b >= 0) && (Rs_e > Rs_b))
		{
			String Rs_s = s2s(greet.substring(Rs_b + 3, Rs_e));

			if(Rs_s != null)
				Rs = hex2bytes(Rs_s);
		}

		if((Rs == null) || (Rs.length != 20))
			throw new IllegalStateException(
			  "Greet response recieved from Authentication Server " +
			  "contains no valid Rs part!");

		//>: decode Rs bytes


		//~: client generated secured random bytes
		byte[] Rc  = randomBytes(new byte[20]);

		//~: password hash bytes
		byte[] P   = hex2bytes(passhash);

		//!: calculate the checksum
		String H   = digestHex(
		  Rc, Rs, getDomain(), getLogin(), P
		);

		//~: login URL
		String url = String.format(
		  "%sservlet/session?step=login&%s&domain=%s&login=%s&Rc=%s&H=%s",

		  getURL(), greet, getDomain(), getLogin(),
		  new String(bytes2hex(Rc)), H
		);

		//!: invoke the server
		String sid = s2es(gets(url));

		//~: decode Session ID
		if(sid.startsWith("sid="))
			sessionId = s2s(sid.substring("sid=".length()));

		//?: {wrong sid response}
		if(sessionId == null)
			throw new IllegalStateException(
			  "Greet response recieved from Authentication Server " +
			  "contains no valid Session ID. Authentication had failed!");


		//!: calculate the private session key
		sessionKey = digest(
		  Rc, Rs, sessionId, P
		);
	}

	/**
	 * Requests the session servlet after successful
	 * session open (login).
	 */
	protected String request(String step, String params)
	  throws Exception
	{
		//~: take next sequence number
		long seqnum = sequence.incrementAndGet();

		//!: calculate the checksum
		String H    = digestHex(
		  sessionId, seqnum, sessionKey
		);

		//~: operation (step) URL
		String url = String.format(
		  "%sservlet/session?step=%s&sid=%s&sequence=%d&H=%s%s",

		  getURL(), step, sessionId, seqnum, H,
		  (params == null)?(""):("&" + params)
		);

		//!: issue the request
		return s2s(gets(url));
	}


	/* protected: HTTP communications */

	/**
	 * Issues HTTP GET request returning the content
	 * object depending on the response MIME type.
	 */
	protected Object   get(String url)
	  throws Exception
	{
		return new URL(url).getContent();
	}

	protected String   gets(String url)
	  throws Exception
	{
		Exception   er = null;
		InputStream is = (InputStream) get(url);
		String      re = null;

		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
			byte[]                buf = new byte[64];

			for(int sz;((sz = is.read(buf)) > 0);)
				bos.write(buf, 0, sz);
			bos.close();

			re = new String(bos.toByteArray(), "UTF-8");
		}
		catch(Exception e)
		{
			er = e;
		}
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}
		}

		if(er != null) throw er;
		return re;
	}

	/**
	 * Issues HTTP POST request with the optional
	 * request bytes written as the request body.
	 *
	 * The bytes returned from the server are
	 * the result of the call.
	 */
	protected byte[]   post(String url, String opcode, byte[] request)
	  throws Exception
	{
		//~: take next sequence number
		long   seqnum = sequence.incrementAndGet();

		byte[] digest = digest((Object) request);

		//!: calculate the checksum
		String H      = digestHex(
		  sessionId, seqnum, sessionKey, opcode, digest
		);


		//<: update the URL
		{
			StringBuilder s = new StringBuilder(url.length() + 48).
			  append(url);

			if(url.indexOf('?') == -1) s.append('?');
			else s.append('&');

			//~: session id
			s.append("sid=").append(sessionId);

			//~: sequence number
			s.append("&sequence=").append(seqnum);

			//~: checksum value
			s.append("&H=").append(H);

			url = s.toString();
		}
		//>: update the URL


		//<: communicate with the server

		byte[]            result  = null;
		Exception         error   = null;
		OutputStream      ostream = null;
		InputStream       istream = null;
		HttpURLConnection connect = (HttpURLConnection)
		  new URL(url).openConnection();

		try
		{
			//~: set HTTP + connection parameters
			connect.setRequestMethod("POST");
			connect.setDoOutput(request != null);
			connect.setUseCaches(false);
			if(getTimeout() != null)
				connect.setConnectTimeout(getTimeout());

			//?: {has request content}
			if(request != null)
			{
				//~: set the HTTP headers
				connect.setRequestProperty("Content-Type",   "application/octet-stream");
				connect.setRequestProperty("Content-Length", Integer.toString(request.length));

				//~: write the request body
				ostream = connect.getOutputStream();
				ostream.write(request);

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
			}

			//~: get the response stream
			istream = connect.getInputStream();

			//?: {HTTP response code is not 200}
			if(connect.getResponseCode() != 200)
				throw new IllegalStateException(String.format(
				  "Authentication server at URL [%s] had returned " +
				  "error HTTP status code [%d], message is: [%s]!",

				  url, connect.getResponseCode(), s2es(connect.getResponseMessage())
				));

			//~: read the server response
			ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
			byte[]                buf = new byte[256];

			for(int sz;((sz = istream.read(buf)) > 0);)
				bos.write(buf, 0, sz);
			bos.close();
			result = bos.toByteArray();

			//~: validate the response
			if(result.length != 0)
			{
				String authDigest = connect.getHeaderField("Auth-Digest");

				if(authDigest == null)
					throw new IllegalStateException(
					  "Server response has no Auth-Digest header!");

				String xAD = digestHex(
				  sessionId, seqnum, sessionKey, digest((Object) result)
				);

				if(!xAD.equals(authDigest))
					throw new IllegalStateException(
					  "Detected Auth-Digest mismatch in the server response!");
			}
		}
		catch(Exception e)
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

		if(error != null)
			throw error;

		//>: communicate with the server

		return result;
	}


	/* protected: JAXB support */

	protected JAXBContext getJAXBContext()
	{
		return this.jaxbContext;
	}

	protected JAXBContext createJAXBContext()
	{
		try
		{
			//<: read list file (with packages names)

			URL list = AuthClient.class.getResource("jaxb.list");
			if(list == null) throw new IllegalStateException(
			  "Can't find jaxb.list file!");

			StringBuilder  sb = new StringBuilder(1024);
			BufferedReader br = new BufferedReader(
			  new InputStreamReader(list.openStream(), "UTF-8"), 256);
			String         s;

			while((s = br.readLine()) != null)
				if((s = s2s(s)) != null)
					sb.append((sb.length() == 0)?(""):(":")).append(s);
			br.close();

			//>: read list file


			//!: create the context
			return JAXBContext.newInstance(sb.toString());
		}
		catch(Exception e)
		{
			throw new IllegalStateException(
			  "Can't create AuthClient (API) JAXB Context!", e);
		}
	}


	/* protected: support utilities */

	protected Random   createRandom()
	{
		return new SecureRandom();
	}

	protected byte[]   randomBytes(byte[] a)
	{
		synchronized(random)
		{
			random.nextBytes(a);
		}

		return a;
	}

	protected String   s2s(String s)
	{
		return (s == null)?(null):
		  ((s = s.trim()).length() == 0)?(null):(s);
	}

	protected String   s2es(String s)
	{
		return (s == null)?(""):(s.trim());
	}

	private static char[] BYTES2HEX =
	  "0123456789ABCDEF".toCharArray();

	protected char[]   bytes2hex(byte[] a)
	{
		if(a == null) return null;

		char[] c = new char[a.length * 2];

		for(int i = 0, j = 0;(i < a.length);i++, j += 2)
		{
			int b = a[i];

			//HINT: higher comes first!

			c[j    ] = BYTES2HEX[ (b & 0xF0) >> 4 ];
			c[j + 1] = BYTES2HEX[ (b & 0x0F)      ];
		}

		return c;
	}

	protected byte[]   digest(Object... values)
	{
		MessageDigest digest;
		byte[]        longs  = null;

		try
		{
			digest = MessageDigest.getInstance("SHA-1");
		}
		catch(Exception e)
		{
			throw new AuthError(e,
			  "Can't get SHA-1 message digest!");
		}

		for(Object v : values)
		{
			if(v instanceof Long)
			{
				if(longs == null)
					longs = new byte[8];

				long l = (Long)v;
				for(int i = 0;(i < 8);i++)
					longs[i] = (byte)( (l >> 8*i) & 0xFF );

				digest.update(longs);
				continue;
			}

			if(v instanceof byte[])
			{
				digest.update((byte[]) v);
				continue;
			}

			if(v instanceof CharSequence) try
			{
				digest.update(v.toString().getBytes("UTF-8"));
				continue;
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}

			throw new IllegalArgumentException();
		}

		return digest.digest();
	}

	protected String   digestHex(Object... objs)
	{
		return new String(bytes2hex(digest(objs)));
	}

	protected byte[]   hex2bytes(CharSequence c)
	{
		byte[] a = new byte[c.length() / 2];
		int    l = 0, h = 16;

		for(int i = 0;(i < c.length());i++)
		{
			int x = (int)c.charAt(i);

			//?: {not ASCII character}
			if((x & 0xFF00) != 0)
				continue;

			int b = HEX2BYTES[x];

			//?: {not a HEX character}
			if(b == 16)
				continue;

			//HINT: higher comes first!

			//?: {higher is not set yet}
			if(h == 16)
			{
				h = b;
				continue;
			}

			//HINT: 'b' is a lower part here...
			a[l++] = (byte)(b | (h << 4));
			h = 16;
		}

		//?: {resulting array is longer}
		if(l != a.length)
		{
			byte[] a2 = new byte[l];
			System.arraycopy(a, 0, a2, 0, l);
			a = a2;
		}

		return a;
	}

	private static byte[] HEX2BYTES =
	  new byte[256]; //<-- values are: 0 .. 15, 16

	static
	{
		char[] hex = "0123456789abcdef".toCharArray();
		char[] HEX = "0123456789ABCDEF".toCharArray();


		for(int i = 0;(i < 256);i++)
			HEX2BYTES[i] = 16;

		for(byte j = 0;(j < 16);j++)
		{
			HEX2BYTES[((int)hex[j]) & 0xFF] = j;
			HEX2BYTES[((int)HEX[j]) & 0xFF] = j;
		}
	}


	/* private: initialization parameters */

	private volatile String     url;
	private volatile String     domain;
	private volatile String     login;
	private volatile String     passhash;
	private volatile Integer    timeout;


	/* authentication session state */

	private volatile String     sessionId;  //<-- session ID
	private volatile byte[]     sessionKey; //<-- private session key
	private volatile AtomicLong sequence;   //<-- incremented sequence of the requests
	private final Random        random;     //<-- secure random of the client


	/* authentication session state */

	private volatile JAXBContext jaxbContext;
}