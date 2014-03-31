package com.tverts.data;

/* Java */

import java.io.Serializable;
import java.util.Date;


/**
 * The context of accessing the data.
 *
 * @author anton.baukin@gmail.com.
 */
public class DataCtx implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: Data Context */

	/**
	 * The target domain of the data selection.
	 * Equals to the domain of the issuing user.
	 * May also be a system domain.
	 */
	public Long getDomain()
	{
		return domain;
	}

	public void setDomain(Long domain)
	{
		this.domain = domain;
	}

	public Date getRequestTime()
	{
		return requestTime;
	}

	public void setRequestTime(Date requestTime)
	{
		this.requestTime = requestTime;
	}

	/**
	 * Returns the authentication Session ID
	 * of the user issuing the request.
	 * Undefined for the system requests.
	 */
	public String getSessionID()
	{
		return sessionID;
	}

	public void setSessionID(String sessionID)
	{
		this.sessionID = sessionID;
	}


	/* private: the context data */

	private Long   domain;
	private Date   requestTime;
	private String sessionID;
}