package com.tverts.endure.auth;

/* standard Java classes */

import java.util.Date;


/**
 * Session of the authenticated user.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthSession
{
	/* public: Session (bean) properties */

	public AuthLogin getLogin()
	{
		return login;
	}

	public void      setLogin(AuthLogin login)
	{
		this.login = login;
	}

	/**
	 * The primary key of the session record.
	 * Sessions are created by the authentication
	 * application.
	 */
	public String    getSessionId()
	{
		return sessionId;
	}

	public void      setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String    getSessionKey()
	{
		return sessionKey;
	}

	public void      setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	/**
	 * Server-generated random 20 bytes as HEX-string.
	 * Stored in the database not to allow sending
	 * repeated login requests with the same Rs.
	 */
	public String    getServerRandom()
	{
		return serverRandom;
	}

	public void      setServerRandom(String serverRandom)
	{
		this.serverRandom = serverRandom;
	}

	public Date      getCreateTime()
	{
		return createTime;
	}

	public void      setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date      getAccessTime()
	{
		return accessTime;
	}

	public void      setAccessTime(Date accessTime)
	{
		this.accessTime = accessTime;
	}

	public Date      getCloseTime()
	{
		return closeTime;
	}

	public void      setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public long      getSequence()
	{
		return sequence;
	}

	public void      setSequence(long sequence)
	{
		this.sequence = sequence;
	}

	/**
	 * Bind key allows client to transfer security
	 * token from Authentication server to Web
	 * one. Server and client do calculate this
	 * private bind key separately. After the first
	 * usage, the key must be removed from the database
	 */
	public String    getBind()
	{
		return bind;
	}

	public void      setBind(String bind)
	{
		this.bind = bind;
	}


	/* public: Object interface */

	public boolean   equals(Object o)
	{
		if(this == o)
			return true;

		if(!this.getClass().equals(o.getClass()))
			return false;

		String k0 = this.getSessionId();
		String k1 = ((AuthSession)o).getSessionId();

		return (k0 != null) && k0.equals(k1);
	}

	public int       hashCode()
	{
		String k0 = this.getSessionId();
		return (k0 == null)?(0):(k0.hashCode());
	}

	public String    toString()
	{
		return String.format("Auth Session [%s]", getSessionId());
	}


	/* persisted properties */

	private AuthLogin login;
	private String    sessionId;
	private String    sessionKey;
	private String    serverRandom;

	private Date      createTime;
	private Date      accessTime;
	private Date      closeTime;
	private long      sequence;
	private String    bind;
}