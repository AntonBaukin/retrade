package com.tverts.auth.server;

/**
 * Class with the state of the authentication session.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthSession implements Cloneable, java.io.Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: AuthSession (bean) properties */

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public void setClosed(boolean closed)
	{
		this.closed = closed;
	}

	public long getDomain()
	{
		return domain;
	}

	public void setDomain(long domain)
	{
		this.domain = domain;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public long getServerTime()
	{
		return serverTime;
	}

	public void setServerTime(long serverTime)
	{
		this.serverTime = serverTime;
	}

	public String getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	public long getSequence()
	{
		return sequence;
	}

	public void setSequence(long sequence)
	{
		this.sequence = sequence;
	}


	/* public: Cloneable interface */

	protected AuthSession clone()
	{
		try
		{
			return (AuthSession) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}

	protected void copyTo(AuthSession session)
	{
		session.setSessionId(getSessionId());
		session.setClosed(isClosed());
		session.setDomain(getDomain());
		session.setLogin(getLogin());
		session.setServerTime(getServerTime());
		session.setSessionKey(getSessionKey());
		session.setSequence(getSequence());
	}


	/* session properties */

	private String  sessionId;
	private boolean closed;
	private long    domain;
	private String  login;
	private long    serverTime;
	private String  sessionKey;
	private long    sequence;
}