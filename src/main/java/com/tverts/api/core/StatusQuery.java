package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlType;


/**
 * Query to get the status of the requests execution
 * within the current authentication session.
 * Also used as a test query.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(namespace = "urn:com.tverts:retrade:core:status-query")
public class StatusQuery
{
	/* public: StatusQuery (bean) interface */

	public Long getFirstResult()
	{
		return firstResult;
	}

	public void setFirstResult(Long firstResult)
	{
		this.firstResult = firstResult;
	}

	public Long getResultsLimit()
	{
		return resultsLimit;
	}

	/**
	 * The limit of the results. Note that server
	 * may restrict this value for the performance
	 * reasons.
	 */
	public void setResultsLimit(Long resultsLimit)
	{
		this.resultsLimit = resultsLimit;
	}

	/**
	 * Set this key to get the status of the particular
	 * Ping request sent with the client key assigned.
	 */
	public String getRequestKey()
	{
		return requestKey;
	}

	public void setRequestKey(String requestKey)
	{
		this.requestKey = requestKey;
	}

	/**
	 * Flag to also return executed (completed)
	 * Ping requests. Unset (false) by default.
	 */
	public Boolean getIncludeExecuted()
	{
		return includeExecuted;
	}

	public void setIncludeExecuted(Boolean includeExecuted)
	{
		this.includeExecuted = includeExecuted;
	}

	/**
	 * Flag to skip executed Ping requests
	 * having Pong responses already delivered.
	 * Unset (false) by default.
	 */
	public Boolean getSkipDelivered()
	{
		return skipDelivered;
	}

	public void setSkipDelivered(Boolean skipDelivered)
	{
		this.skipDelivered = skipDelivered;
	}


	/* query parameters */

	private Long    firstResult;
	private Long    resultsLimit;
	private String  requestKey;
	private Boolean includeExecuted;
	private Boolean skipDelivered;
}