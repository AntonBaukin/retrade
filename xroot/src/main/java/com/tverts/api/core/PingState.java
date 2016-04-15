package com.tverts.api.core;

/* standard Java classes */

import java.util.Date;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.support.TimestampAdapter;


/**
 * The state of the execution request (ping)
 * previously sent by the user within his domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "ping-state")
public class PingState
{
	@XmlAttribute(name = "domain", required = true)
	public long getDomain()
	{
		return domain;
	}

	public void setDomain(long domain)
	{
		this.domain = domain;
	}

	@XmlAttribute(name = "login-key", required = true)
	public long getLoginKey()
	{
		return loginKey;
	}

	public void setLoginKey(long loginKey)
	{
		this.loginKey = loginKey;
	}

	@XmlAttribute(name = "session-id", required = true)
	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	@XmlAttribute(name = "executed", required = true)
	public boolean isExecuted()
	{
		return executed;
	}

	public void setExecuted(boolean executed)
	{
		this.executed = executed;
	}

	@XmlAttribute(name = "delivered", required = true)
	public boolean isDelivered()
	{
		return delivered;
	}

	public void setDelivered(boolean delivered)
	{
		this.delivered = delivered;
	}

	@XmlAttribute(name = "txn")
	public Long getTxn()
	{
		return txn;
	}

	public void setTxn(Long txn)
	{
		this.txn = txn;
	}

	@XmlElement(name = "login", required = true)
	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	@XmlElement(name = "request-time", required = true)
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getRequestTime()
	{
		return requestTime;
	}

	public void setRequestTime(Date requestTime)
	{
		this.requestTime = requestTime;
	}

	@XmlElement(name = "response-time")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getResponseTime()
	{
		return responseTime;
	}

	public void setResponseTime(Date responseTime)
	{
		this.responseTime = responseTime;
	}

	@XmlElement(name = "exec-begin")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getExecBegin()
	{
		return execBegin;
	}

	public void setExecBegin(Date execBegin)
	{
		this.execBegin = execBegin;
	}

	@XmlElement(name = "exec-done")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getExecDone()
	{
		return execDone;
	}

	public void setExecDone(Date execDone)
	{
		this.execDone = execDone;
	}


	/* attributes */

	private long    domain;
	private long    loginKey;
	private String  sessionId;
	private boolean executed;
	private boolean delivered;
	private Long    txn;
	private String  login;
	private Date    requestTime;
	private Date    responseTime;
	private Date    execBegin;
	private Date    execDone;
}