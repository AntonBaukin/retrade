package com.tverts.api;

/* standard Java classes */

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Client providing authentication to the group
 * of 'ReTrade' applications over HTTP.
 *
 * Create the client instance, login, then send
 * the requests to the remote system.
 *
 * This requests methods of the class are
 * thread-safe. Authentication ones are not!
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
		this.sequence = new AtomicLong();
		this.random   = createRandom();
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
		if(pi == 4) //<-- http:
		{
			pi  = url.indexOf('/', 7); //<-- after "http://"
			url = url.substring(0, pi) + ":80" + url.substring(pi);
		}

		//-->  http://$S:$P/$C/
		//-->  http://$S:$P/

		int ci = url.indexOf('/', 7);
		if(ci == url.length() - 1)
			url += "/auth/";

		this.url = url;
		return this;
	}

	public Long       getDomain()
	{
		return domain;
	}

	/**
	 * Integer value with the client domain key.
	 */
	public AuthClient initDomain(long domain)
	{
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
			String status = request("close", null);

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
		//~: HTTP GET greet
		String greet = s2s(get(getURL() +
		  "servlet/session?step=greet").toString());

		if(greet == null) throw new IllegalStateException(
		  "Recieved empty greet response from Authentication Server!");

		return greet;
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
		  Rc, Rs, getDomain(), getDomain(), P
		);

		//~: login URL
		String url = String.format(
		  "%sservlet/session?step=login&%s&domain=%d&login=%s&Rc=%s&H=%s",

		  getURL(), greet, getDomain(), getLogin(),
		  bytes2hex(Rc), H
		);

		//!: invoke the server
		String sid = s2es(get(url).toString());

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
		  Rc, Rs, sid, P
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
		return s2s(get(url).toString());
	}


	/* protected: HTTP communications */

	/**
	 * Processor of HTTP POST request and response streams.
	 */
	public static interface PostStreamer
	{
		/* public: PostStreamer interface */

		/**
		 * Tells whether the POST request
		 * would contain request body.
		 */
		public boolean isRequestStream();

		/**
		 * Writes the request content body
		 * to POST to the remote server.
		 */
		public void    writeRequest(OutputStream os)
		  throws Exception;

		public void    readResponse(String contentType, InputStream is)
		  throws Exception;
	}

	/**
	 * Issues HTTP GET request returning the content
	 * object depending on the response MIME type.
	 */
	protected Object   get(String url)
	  throws Exception
	{
		return new URL(url).getContent();
	}

	/**
	 * Issues HTTP POST request with the streaming
	 * processor given.
	 */
	protected void     post(String url, PostStreamer streamer)
	  throws Exception
	{
		Exception         er = null;
		byte[]            bf = new byte[512];
		OutputStream      ou = null;
		InputStream       in = null;
		HttpURLConnection co = (HttpURLConnection)
		  new URL(url).openConnection();

		try
		{
			//~: set connection parameters
			co.setDoOutput(streamer.isRequestStream());
			co.setUseCaches(false);

			//~: set HTTP parameters and headers
			co.setRequestMethod("POST");
			co.setRequestProperty("Content-type", "application/octet-stream");

			//~: write the request body
			if(co.getDoOutput())
			{
				ou = co.getOutputStream();

				//~: write the request
				streamer.writeRequest(ou);

				try
				{
					ou.flush();
					ou.close();
				}
				finally
				{
					ou = null;
				}
			}

			//~: get the response stream
			in = co.getInputStream();

			//?: {HTTP response code is not 200}
			if(co.getResponseCode() != 200)
				throw new IllegalStateException(String.format(
				  "Authentication server at URL [%s] had returned " +
				  "error HTTP status code [%d], message is: [%s]!",

				  url, co.getResponseCode(), s2es(co.getResponseMessage())
				));

			//!: read the server response
			streamer.readResponse(co.getContentType(), in);
		}
		catch(Exception e)
		{
			er = e;
		}
		finally
		{
			//~: close the output stream
			if(ou != null) try
			{
				ou.close();
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}

			//~: close the input stream
			if(in != null) try
			{
				in.close();
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}

			//~: close the connection
			if(co != null) try
			{
				co.disconnect();
			}
			catch(Exception e)
			{
				if(er == null) er = e;
			}
		}

		if(er != null)
			throw er;

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

	private volatile String url;
	private volatile Long   domain;
	private volatile String login;
	private volatile String passhash;


	/* authentication session state */

	private volatile String     sessionId;  //<-- session ID
	private volatile byte[]     sessionKey; //<-- private session key
	private volatile AtomicLong sequence;   //<-- incremented sequence of the requests
	private final Random        random;     //<-- secure random of the client
}