package com.tverts.endure.auth;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;


/**
 * Session of the authenticated user.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthSession extends NumericBase
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


	/* persisted properties */

	private AuthLogin login;
	private String    sessionId;
	private String    sessionKey;

	private Date      createTime;
	private Date      accessTime;
	private Date      closeTime;
	private long      sequence;
}