package com.tverts.api.core;

/* standard Java classes */

import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.LimitedQuery;


/**
 * Query to get the status of the requests execution
 * within the current authentication session.
 * Also used as a test query.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "select-pings-states")
public class SelectPingsStates implements LimitedQuery
{
	/* public: GetExecTasks (bean) interface */

	@XmlAttribute(name = "first-result")
	public Long getFirstResult()
	{
		return firstResult;
	}

	public void setFirstResult(Long firstResult)
	{
		this.firstResult = firstResult;
	}

	@XmlAttribute(name = "results-limit")
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
	@XmlElement(name = "request-key")
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
	@XmlAttribute(name = "include-executed")
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
	@XmlAttribute(name = "skip-delivered")
	public Boolean getSkipDelivered()
	{
		return skipDelivered;
	}

	public void setSkipDelivered(Boolean skipDelivered)
	{
		this.skipDelivered = skipDelivered;
	}

	@XmlElement(name = "ping-state")
	@XmlElementWrapper(name = "result")
	public List<PingState> getPings()
	{
		return pings;
	}

	public void setPings(List<PingState> pings)
	{
		this.pings = pings;
	}


	/* query parameters */

	private Long    firstResult;
	private Long    resultsLimit;
	private String  requestKey;
	private Boolean includeExecuted;
	private Boolean skipDelivered;


	/* the result */

	private List<PingState> pings;
}