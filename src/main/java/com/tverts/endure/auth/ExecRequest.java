package com.tverts.endure.auth;

/* standard Java classes */

import java.util.Date;


/**
 * Request to the query execution service.
 * Created by authentication application.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ExecRequest
{
	/* public: ExecRequest (bean) interface */

	/**
	 * Primary key of the request assigned by
	 * the authentication application had placed
	 * the request from 'pkeys_exec_request' sequence.
	 */
	public Long     getPrimaryKey()
	{
		return primaryKey;
	}

	public void     setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Long     getDomain()
	{
		return domain;
	}

	public void     setDomain(Long domain)
	{
		this.domain = domain;
	}

	/**
	 * Copy of the primary key of the {@link AuthSession}
	 * this request belongs to. All requests of the same
	 * session are executed in the order of primary keys
	 * increase and strictly sequentially.
	 */
	public String   getSessionId()
	{
		return sessionId;
	}

	public void     setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	/**
	 * The optional key assigned by the client.
	 * Requests without a key are removed from
	 * the database on the delivery. Requests
	 * having the key stay until the session
	 * is closed, or their reservation time
	 * is expired.
	 */
	public String   getClientKey()
	{
		return clientKey;
	}

	public void     setClientKey(String clientKey)
	{
		this.clientKey = clientKey;
	}

	/**
	 * The request Ping object as UTF-8 XML text.
	 * Must be always assigned.
	 */
	public byte[]   getRequest()
	{
		return request;
	}

	public void     setRequest(byte[] request)
	{
		this.request = request;
	}

	/**
	 * The response Pong object as UTF-8 XML text.
	 * Assigned when the request processing is
	 * complete, and there is no error.
	 */
	public byte[]   getResponse()
	{
		return response;
	}

	public void     setResponse(byte[] response)
	{
		this.response = response;
	}

	/**
	 * Timestamp of the request.
	 */
	public Date     getRequestTime()
	{
		return requestTime;
	}

	public void     setRequestTime(Date requestTime)
	{
		this.requestTime = requestTime;
	}

	/**
	 * Timestamp of the response. Assigned on the
	 * execution complete.
	 */
	public Date     getResponseTime()
	{
		return responseTime;
	}

	public void     setResponseTime(Date responseTime)
	{
		this.responseTime = responseTime;
	}

	public boolean  isExecuted()
	{
		return executed;
	}

	public void     setExecuted(boolean executed)
	{
		this.executed = executed;
	}

	public boolean  isDelivered()
	{
		return delivered;
	}

	public void     setDelivered(boolean delivered)
	{
		this.delivered = delivered;
	}


	/* persisted properties */

	private Long    primaryKey;
	private Long    domain;
	private String  sessionId;
	private String  clientKey;
	private byte[]  request;
	private byte[]  response;
	private Date    requestTime;
	private Date    responseTime;
	private boolean executed;
	private boolean delivered;
}