package com.tverts.auth.server;

/* com.tverts: auth support */

import com.tverts.auth.server.support.EX;


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

	public String getRs()
	{
		return Rs;
	}

	public void setRs(String rs)
	{
		Rs = rs;
	}

	public String getBind()
	{
		return bind;
	}

	public void setBind(String bind)
	{
		this.bind = bind;
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
			throw EX.wrap(e);
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
		session.setRs(getRs());
		session.setBind(getBind());
	}


	/* session properties */

	private String  sessionId;
	private boolean closed;
	private long    domain;
	private String  login;
	private long    serverTime;
	private String  sessionKey;
	private long    sequence;
	private String  Rs;
	private String  bind;
}