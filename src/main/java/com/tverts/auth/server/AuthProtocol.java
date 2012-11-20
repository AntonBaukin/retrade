package com.tverts.auth.server;

/* standard Java classes */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Implements 'Re Trade' Application
 * authentication protocol.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthProtocol implements Cloneable
{
	/* public: AuthProtocol interface */

	public void   setParameter(String name, String value)
	{
		if(params == null)
			params = new HashMap<String, String>(3);
		params.put(name, value);
	}

	public void   setWriter(Writer writer)
	{
		this.writer = writer;
	}

	public void   setPing(BytesStream ping)
	{
		this.ping = ping;
	}

	public void   setPong(BytesStream pong)
	{
		this.pong = pong;
	}

	public String getPongHash()
	{
		return pongHash;
	}

	public String getError()
	{
		return error;
	}

	public void   setRequest(boolean request)
	{
		this.request = request;
	}

	public void   setReceive(boolean receive)
	{
		this.receive = receive;
	}

	public void   invoke()
	  throws IOException
	{
		if(this.request)
			this.error = request();
		else
			this.error = dispatch();
	}

	/**
	 * Call this method after invoking request-receive
	 * actions and all the response (Pong) data were
	 * sent to the client.
	 *
	 * This delayed commit reduces the probability
	 * of marking execution requests as delivered
	 * on broken in-the-middle connections.
	 */
	public void   commit()
	{
		if(invokeCommit != null)
			invokeCommit.run();
	}

	public void   initPrototype(DbConnect dbcPrototype)
	{
		this.dbConnect = dbcPrototype;
		this.sidpfx    = initSid();
		this.sidsfx    = new AtomicLong();
		this.random    = new AuthRandom();
		this.digest    = new AuthDigest();
		this.xkey      = initSkey(this.random);
	}


	/* public: Cloneable interface */

	public AuthProtocol clone()
	{
		try
		{
			return (AuthProtocol) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/* authentication protocol implementation */

	protected String dispatch()
	  throws IOException
	{
		String step = param("step");

		if("greet".equals(step))
			return stepGreet();

		if("login".equals(step))
			return stepLogin();

		if("touch".equals(step))
			return stepTouch();

		if("close".equals(step))
			return stepClose();

		return "Send authentication step parameter.";
	}

	protected String stepGreet()
	  throws IOException
	{
		long   stime = System.currentTimeMillis();
		byte[] Rs    = random.randomBytes(20);
		byte[] HRs   = digest.sign(stime, xkey, Rs);

		//>> server time
		writer.write("stime=");
		writer.write(Long.toString(stime));

		//>> Rs
		writer.write("&Rs=");
		writer.write(Encodings.bytes2hex(Rs));

		//>> HRs
		writer.write("&HRs=");
		writer.write(Encodings.bytes2hex(HRs));

		return null;
	}

	protected String stepLogin()
	  throws IOException
	{
		//~: parse the domain
		long domain;
		if(param("domain") == null)
			return "Send domain parameter.";

		try
		{
			domain = Long.parseLong(param("domain"));
		}
		catch(NumberFormatException e)
		{
			return "Domain must be a long number.";
		}

		//~: login
		String login = param("login");
		if(login == null)
			return "Send login parameter.";
		if(login.length() > 255)
			return "Login is too long.";

		//~: Rc
		byte[] Rc;
		if(param("Rc") == null)
			return "Send Rc (client random) parameter.";
		Rc = Encodings.hex2bytes(param("Rc").toCharArray());
		if(Rc.length != 20)
			return "Rc (client random) must have 20 bytes.";

		//~: H (main signature)
		String H = param("H");
		if(H == null)
			return "Send H (main signature) parameter.";


		//<: check server time, Rs, HRs

		//~: parse the server time
		long stime;
		if(param("stime") == null)
			return "Send stime (server time) parameter.";

		try
		{
			stime = Long.parseLong(param("stime"));
		}
		catch(NumberFormatException e)
		{
			return "Parameter stime (server time) must be a long number.";
		}

		//~: Rs
		if(param("Rs") == null)
			return "Send Rs parameter.";
		byte[] Rs = Encodings.hex2bytes(param("Rs").toCharArray());
		if(Rs.length != 20)
			return "Rs (server random) must have 20 bytes.";


		//~: HRs
		String HRs = param("HRs");
		if(HRs == null)
			return "Send HRs parameter.";

		//!: validate HRs
		String xHRs = digest.signHex(
		  stime, xkey, Rs
		);

		if(!xHRs.equals(HRs))
			return "Wrong HRs signature.";

		//~: check timeout
		long xtime = stime + AuthConfig.INSTANCE.getAuthTimeout();
		if(xtime < System.currentTimeMillis())
			return "Authentication timed out.";

		//>: check server time, Rs, HRs


		//~: connect to the database
		Boolean   txc = null;
		DbConnect dbc = createDbConnect();
		dbc.connect();

		try
		{
			//<: check the user password

			//HINT: user password is stored in the database
			//  as SHA-1 signature in HEX string.

			String P  = dbc.getPassword(domain, login);
			String xH = digest.signHex(
			  Rc, Rs, param("domain"), login, P.toCharArray()
			);

			if(!xH.equals(H))
				return "Wrong H signature.";

			//>: check the user password

			//!: generate the Session ID
			String sid  = nextSid(dbc);

			//~: create private session key
			String skey = digest.signHex(
			  Rc, Rs, sid, P.toCharArray()
			);

			//!: save session key to the database
			AuthSession session = new AuthSession();

			session.setSessionId(sid);
			session.setDomain(domain);
			session.setLogin(login);
			session.setServerTime(System.currentTimeMillis());
			session.setSessionKey(skey);

			dbc.saveSession(session);

			//~: write the sid to the stream
			writer.write("sid=");
			writer.write(sid);

			txc = true;
		}
		catch(Throwable e)
		{
			txc = false;
			throw new RuntimeException(e);
		}
		finally
		{
			dbc.disconnect(txc);
		}

		return null;
	}

	protected String stepTouch()
	  throws IOException
	{
		//~: sid
		String sid = param("sid");
		if(sid == null)
			return "Send Session ID (sid) parameter.";

		//~: parse sequence number
		long sequence;
		if(param("sequence") == null)
			return "Send sequence number (sequence) parameter.";

		try
		{
			sequence = Long.parseLong(param("sequence"));
		}
		catch(NumberFormatException e)
		{
			return "Parameter sequence (sequence number) must be a long number.";
		}

		//~: H (request signature)
		String H = param("H");
		if(H == null)
			return "Send H (request signature) parameter.";

		//~: connect to the database
		Boolean   txc = null;
		DbConnect dbc = createDbConnect();
		dbc.connect();

		//~: error text
		final String MSG = "Wrong H signature.";

		try
		{
			//~: load the session
			AuthSession session = new AuthSession();
			session.setSessionId(sid);
			dbc.loadSession(session);

			//?: {session is closed / not exists}
			if(session.isClosed())
				return MSG; //!: do not expose actual session state

			if(session.getSessionKey() == null)
				throw new IllegalStateException();

			//~: check the signature
			String xH = digest.signHex(
			  sid, sequence, session.getSessionKey().toCharArray()
			);

			if(!xH.equals(H))
				return MSG;

			//?: {session timed out}
			long stimeout = System.currentTimeMillis() - session.getServerTime();
			if(stimeout > AuthConfig.INSTANCE.getSessionTimeout())
				return MSG;

			//~: check sequence number
			if(sequence <= session.getSequence())
				return MSG;

			//!: update the session sequence
			session.setServerTime(System.currentTimeMillis());
			session.setSequence(sequence);

			dbc.touchSession(session);

			//~: write the success
			writer.write("touched");
			txc = true;
		}
		catch(Throwable e)
		{
			txc = false;
			throw new RuntimeException(e);
		}
		finally
		{
			dbc.disconnect(txc);
		}

		return null;
	}

	protected String stepClose()
	  throws IOException
	{
		//~: sid
		String sid = param("sid");
		if(sid == null)
			return "Send Session ID (sid) parameter.";

		//~: parse sequence number
		long sequence;
		if(param("sequence") == null)
			return "Send sequence number (sequence) parameter.";

		try
		{
			sequence = Long.parseLong(param("sequence"));
		}
		catch(NumberFormatException e)
		{
			return "Parameter sequence (sequence number) must be a long number.";
		}

		//~: H (request signature)
		String H = param("H");
		if(H == null)
			return "Send H (request signature) parameter.";

		//~: connect to the database
		Boolean   txc = null;
		DbConnect dbc = createDbConnect();
		dbc.connect();

		//~: error text
		final String MSG = "Wrong H signature.";

		try
		{
			//~: load the session
			AuthSession session = new AuthSession();
			session.setSessionId(sid);
			dbc.loadSession(session);

			//?: {session is closed / not exists}
			if(session.isClosed())
				return MSG; //!: do not expose actual session state

			if(session.getSessionKey() == null)
				throw new IllegalStateException();

			//~: check the signature
			String xH = digest.signHex(
			  sid, sequence, session.getSessionKey().toCharArray()
			);

			if(!xH.equals(H))
				return MSG;

			//?: {session timed out}
			long stimeout = System.currentTimeMillis() - session.getServerTime();
			if(stimeout > AuthConfig.INSTANCE.getSessionTimeout())
				return MSG;

			//~: check sequence number
			if(sequence <= session.getSequence())
				return MSG;


			//HINT: access time (server time) is not updated on close!

			//!: update the session sequence
			session.setSequence(sequence);
			session.setClosed(true);

			dbc.touchSession(session);
			txc = true;

			//~: write the success
			writer.write("closed");
		}
		catch(Throwable e)
		{
			// txc = false; <-- close is committed always
			throw new RuntimeException(e);
		}
		finally
		{
			dbc.disconnect(txc);
		}

		return null;
	}


	/* requests implementation */

	protected String request()
	{
		//~: sid
		String sid = param("sid");
		if(sid == null)
			return "Send Session ID (sid) parameter.";

		//~: parse sequence number
		long sequence;
		if(param("sequence") == null)
			return "Send sequence number (sequence) parameter.";

		try
		{
			sequence = Long.parseLong(param("sequence"));
		}
		catch(NumberFormatException e)
		{
			return "Parameter sequence (sequence number) must be a long number.";
		}

		//~: H (request signature)
		String H = param("H");
		if(H == null)
			return "Send H (request signature) parameter.";

		//~: connect to the database
		Boolean   txc = null;
		DbConnect dbc = createDbConnect();
		dbc.connect();

		//~: error text
		final String MSG = "Wrong H signature.";

		try
		{
			//~: load the session
			AuthSession session = new AuthSession();
			session.setSessionId(sid);
			dbc.loadSession(session);

			//?: {session is closed / not exists}
			if(session.isClosed())
				return MSG; //!: do not expose actual session state

			if(session.getSessionKey() == null)
				throw new IllegalStateException();

			//~: payload digest
			byte[] pd = null;
			if(ping.length() != 0L)
				pd = digest.sign(ping);

			//~: session key characters (for hash check)
			char[] skey = session.getSessionKey().toCharArray();

			//~: check the signature
			String xH = digest.signHex(
			  sid, sequence, skey,
			  (this.receive)?("receive"):("request"), pd
			);

			if(!xH.equals(H))
				return MSG;

			//?: {session timed out}
			long stimeout = System.currentTimeMillis() - session.getServerTime();
			if(stimeout > AuthConfig.INSTANCE.getSessionTimeout())
				return MSG;

			//~: check sequence number
			if(sequence <= session.getSequence())
				return MSG;


			//!: update the session sequence
			session.setSequence(sequence);
			session.setServerTime(System.currentTimeMillis());

			dbc.touchSession(session);
			txc = true;


			//?: {this is a receive Ping}
			if(this.receive)
			{
				//~: get the client key (from HTTP POST body)
				ByteArrayOutputStream bos =
				  new ByteArrayOutputStream((int) ping.length());
				ping.copy(bos);
				bos.close();

				String clientKey = new String(bos.toByteArray(), "UTF-8");

				//!: do receive
				invokeCommit = dbc.receive(session, clientKey, pong);

				//~: calculate the resulting message digest
				if(pong.length() != 0)
					pongHash = digest.signHex(
					  sid, sequence, skey, digest.sign(pong)
					);
			}
			//!: handle a request Ping
			else
				//!: save the request into the database
				dbc.request(session, ping);
		}
		catch(Throwable e)
		{
			// txc = false; <-- close is committed always
			throw new RuntimeException(e);
		}
		finally
		{
			dbc.disconnect(txc);
		}

		return null;
	}


	/* protected: protocol support */

	protected String    param(String name)
	{
		if(params == null) return null;

		String v = params.get(name);
		if(v == null) return null;

		v = v.trim();
		if(v.length() == 0) v = null;

		return v;
	}

	protected DbConnect createDbConnect()
	{
		return dbConnect.clone();
	}

	protected String    nextSid(DbConnect dbc)
	{
		if(sidpfx == null)
			throw new IllegalStateException();

		return sidpfx + '_' +
		  Long.toHexString(sidsfx.incrementAndGet());
	}

	protected String    initSid()
	{
		//~: connect to the database
		DbConnect dbc = createDbConnect();
		dbc.connect();

		String    sid;
		boolean   xtx = false;

		try
		{
			//~: initialize the database
			dbc.initDatabase();

			//~: generate the next Session ID prefix
			sid = dbc.nextSidPrefix();
			xtx = true;
		}
		finally
		{
			dbc.disconnect(xtx);
		}

		return sid;
	}

	protected byte[]    initSkey(AuthRandom random)
	{
		return random.randomBytes(20);
	}


	/* private: protocol state */

	private Map<String, String> params;
	private Writer              writer;
	private BytesStream         ping;
	private BytesStream         pong;
	private String              pongHash;
	private Runnable            invokeCommit;
	private String              error;
	private boolean             request;
	private boolean             receive;


	/* (prototype state): shared state */

	/**
	 * The prototype database connection.
	 */
	private DbConnect    dbConnect;

	/**
	 * Session Id consists of the two parts:
	 * prefix number taken from the database
	 * sequence, and locally incremented suffix
	 * number separated by '_'.
	 */
	private String       sidpfx;
	private AtomicLong   sidsfx;

	private AuthRandom   random;
	private AuthDigest   digest;

	/**
	 * Random private key used when
	 * sending signed stateless messages.
	 */
	private byte[]       xkey;
}