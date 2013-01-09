package com.tverts.api.term;

/* standard Java classes */

import java.util.Date;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Session of a sell terminal.
 */
@XmlType(name = "terminal-session")
public class Session
{
	/**
	 * Operator primary key provided by the
	 * source database.
	 */
	@XmlAttribute(name = "operator-key", required = true)
	public long getOperator()
	{
		return operator;
	}

	public void setOperator(long operator)
	{
		this.operator = operator;
	}

	/**
	 * Terminal primary key provided by the
	 * source database and configured on
	 * the terminal computer.
	 */
	@XmlAttribute(name = "terminal-key", required = true)
	public long getTerminal()
	{
		return terminal;
	}

	public void setTerminal(long terminal)
	{
		this.terminal = terminal;
	}

	/**
	 * Key of the session generated by the
	 * terminal. Unique within the terminal.
	 */
	@XmlElement(name = "session-key", required = true)
	public String getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	@XmlElement(name = "begin-time", required = true)
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getBeginTime()
	{
		return beginTime;
	}

	public void setBeginTime(Date beginTime)
	{
		this.beginTime = beginTime;
	}

	/**
	 * The terminal session end time. Set when
	 * operator has closed the session.
	 */
	@XmlElement(name = "end-time")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}


	/* Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		Session session = (Session)o;
		return (terminal == session.terminal) &&
		  sessionKey.equals(session.sessionKey);
	}

	public int hashCode()
	{
		int result = (int)(terminal ^ (terminal >>> 32));
		result = 31*result + sessionKey.hashCode();
		return result;
	}


	/* attributes */

	private long   operator;
	private long   terminal;
	private String sessionKey;
	private Date   beginTime;
	private Date   endTime;
}