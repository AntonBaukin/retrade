package com.tverts.auth.server;

/* standard Java classes */

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/* JUnit library */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Tests {@link AuthProtocol} implementation
 * with in-memory {@link DbConnect}.
 *
 * @author anton.baukin@gmail.com
 */
public class TestAuthProtocol
{
	/* test entry points */

	@org.junit.Test
	public void testStepGreet()
	  throws Exception
	{
		new TestAuthClient().greet();
	}

	@org.junit.Test
	public void testStepLogin()
	  throws Exception
	{
		TestAuthClient client = new TestAuthClient();

		client.setLogin("second");
		client.setPassword("pass@second");

		client.greet();
		client.login();
	}

	@org.junit.Test
	public void testStepTouch()
	  throws Exception
	{
		TestAuthClient client = new TestAuthClient();

		client.setLogin("first");
		client.setPassword("pass-first");

		client.greet();
		client.login();
		client.touch();
	}

	@org.junit.Test
	public void testStepClose()
	  throws Exception
	{
		TestAuthClient client = new TestAuthClient();

		client.setLogin("third");
		client.setPassword("pass:third");

		client.greet();
		client.login();
		client.touch();
		client.close();

		//~: test closed session
		AuthError error = null;
		try
		{
			client.touch();
		}
		catch(AuthError e)
		{
			error = e;
		}
		assertNotNull(error);
	}

	@org.junit.Test
	public void testWrongPassword()
	  throws Exception
	{
		TestAuthClient client = new TestAuthClient();

		client.setLogin("third");
		client.setPassword("pass!third");

		client.greet();

		AuthError error = null;
		try
		{
			client.login();
		}
		catch(AuthError e)
		{
			error = e;
		}
		assertNotNull(error);
	}


	/* protected: authentication protocol */

	private static AuthProtocol protocolPrototype;

	private static AuthDigest   authDigest =
	  new AuthDigest();

	private static AuthRandom   authRandom =
	  new AuthRandom();

	protected static class AuthError extends RuntimeException
	{
		public AuthError(String message)
		{
			super(message);
		}
	}

	protected static String invokeProtocol(Map<String, String> params)
	{
		AuthProtocol protocol = createAuthProtocol();
		StringWriter writer   = new StringWriter(128);

		for(String param : params.keySet())
			protocol.setParameter(param, params.get(param));
		protocol.setWriter(writer);

		try
		{
			protocol.invoke();
			writer.flush(); writer.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		if(protocol.getError() != null)
			throw new AuthError(protocol.getError());
		return writer.getBuffer().toString();
	}

	protected static AuthProtocol createAuthProtocol()
	{
		if(protocolPrototype == null)
			protocolPrototype = initAuthProtocol();
		return protocolPrototype.clone();
	}

	protected static AuthProtocol initAuthProtocol()
	{
		AuthProtocol protocol = new AuthProtocol();
		protocol.initPrototype(createDbConnect());
		return protocol;
	}

	protected static DbConnect    createDbConnect()
	{
		TestDbConnect dbc = new TestDbConnect();

		dbc.addLogin("first", "pass-first");
		dbc.addLogin("second", "pass@second");
		dbc.addLogin("third", "pass:third");

		return dbc;
	}


	/* test database connection */

	protected static class TestDbConnect extends DbConnect
	{
		/* public: DbConnect (connection) interface */

		public void connect()
		{}

		public void disconnect(Boolean commit)
		{
			if(Boolean.TRUE.equals(commit))
				if(txsession != null)
					sessions.put(txsession.getSessionId(), txsession);
		}


		/* public: DbConnect (data access) interface */

		public String nextSidPrefix()
		{
			return Long.toHexString(sid++);
		}

		public String getPassword(Long domain, String login)
		{
			if(domain != -1L)
				return null;
			return passwords.get(login);
		}

		public void   loadSession(AuthSession session)
		{
			assertNotNull(session.getSessionId());

			AuthSession s = (txsession != null)?(txsession):
			  (txsession = sessions.get(session.getSessionId()));

			if(s == null)
			{
				session.setClosed(true);
				return;
			}

			s.copyTo(session);
		}

		public void   saveSession(AuthSession session)
		{
			assertNotNull(session.getSessionId());
			assertNotSame(0L, session.getDomain());
			assertNotNull(session.getLogin());
			assertNotSame(0L, session.getServerTime());
			assertNotNull(session.getSessionKey());
			assertEquals(0L, session.getSequence());

			txsession = session.clone();
		}

		public void   touchSession(AuthSession session)
		{
			assertNotNull(session.getSessionId());
			if(txsession == null)
				txsession = sessions.get(session.getSessionId());

			assertNotNull(txsession);
			assertTrue(session.getSequence() > txsession.getSequence());
			assertTrue(session.getServerTime() >= txsession.getServerTime());

			txsession.setClosed(session.isClosed());
			txsession.setSequence(session.getSequence());
			txsession.setServerTime(session.getServerTime());
		}


		/* public: TestDbConnect interface */

		public void addLogin(String login, String pass)
		{
			passwords.put(login, authDigest.signHex(pass));
		}


		/* private: shared state (database) */

		private static long sid = 1L;

		private static Map<String, String> passwords =
		  new HashMap<String, String>(3);

		private static Map<String, AuthSession> sessions =
		  new HashMap<String, AuthSession>(3);


		/* private: private state (connection) */

		private AuthSession txsession;
	}


	/* test authentication client */

	protected static class TestAuthClient
	{
		/* public: TestAuthClient interface */


		public void setLogin(String login)
		{
			this.login = login;
		}

		public void setPassword(String password)
		{
			this.passhash = authDigest.
			  signHex(password);
		}

		public void greet()
		{
			String text = null;

			try
			{
				Map<String, String> params =
				  new HashMap<String, String>(1);

				params.put("step", "greet");
				text = invokeProtocol(params);
			}
			catch(AuthError e)
			{
				error = e.getMessage();
			}
			assertNull(error);
			assertNotNull(text);

			String[] lines = text.split("&");
			assertNotSame(0, lines.length);

			for(String pline : lines)
			{
				String[] param = pline.split("=");
				assertEquals(2, param.length);

				if(greet == null)
					greet = new HashMap<String, String>(3);
				greet.put(param[0], param[1]);
			}

			assertNotNull(greet);
			assertNotSame(0, greet.size());
		}

		public void login()
		{
			HashMap<String, String> params =
			  new HashMap<String, String>(7);

			//~: domain
			params.put("domain", "-1");

			//~: login
			assertNotNull(login);
			params.put("login", login);

			//~: Rc
			Rc = authRandom.randomBytes(20);
			params.put("Rc", new String(Encodings.bytes2hex(Rc)));

			//~: greet parameters
			assertNotNull(greet);
			params.putAll(greet);

			//~: get Rs from greet
			assertNotNull(greet.get("Rs"));
			byte[] Rs = Encodings.hex2bytes(
			  greet.get("Rs").toCharArray());
			assertEquals(20, Rs.length);

			//~: calculate H
			assertNotNull(passhash);
			String H = authDigest.signHex(
			  Rc, Rs, "-1", login, passhash.toCharArray()
			);

			params.put("H", H);


			//!: invoke the login step
			params.put("step", "login");
			String sidstr = invokeProtocol(params);
			assertTrue(sidstr.startsWith("sid="));
			assertEquals(-1, sidstr.indexOf('&'));

			//~: save sid value
			sid = sidstr.substring("sid=".length()).trim();
			assertNotSame(0, sid.length());

			//~: calc session key
			sessionKey = authDigest.signHex(
			  Rc, Rs, sid, passhash.toCharArray()
			);
		}

		public void touch()
		{
			HashMap<String, String> params =
			  new HashMap<String, String>(5);

			//~: session id
			assertNotNull(sid);
			params.put("sid", sid);

			//~: sequence
			params.put("sequence", Long.toString(++sequence));

			//~: main signature

			assertNotNull(sessionKey);
			String H = authDigest.signHex(
			  sid, sequence, sessionKey.toCharArray()
			);

			params.put("H", H);

			//!: invoke the touch step
			params.put("step", "touch");
			String status = invokeProtocol(params);
			assertEquals("touched", status);
		}

		public void close()
		{
			HashMap<String, String> params =
			  new HashMap<String, String>(5);

			//~: session id
			assertNotNull(sid);
			params.put("sid", sid);

			//~: sequence
			params.put("sequence", Long.toString(++sequence));

			//~: main signature

			assertNotNull(sessionKey);
			String H = authDigest.signHex(
			  sid, sequence, sessionKey.toCharArray()
			);

			params.put("H", H);

			//!: invoke the touch step
			params.put("step", "close");
			String status = invokeProtocol(params);
			assertEquals("closed", status);
		}


		/* the client state */

		private String error;

		private String login;
		private String passhash;
		private byte[] Rc;
		private String sid;
		private String sessionKey;
		private long   sequence;

		private Map<String, String> greet;
	}
}