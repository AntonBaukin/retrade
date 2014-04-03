package com.tverts.data;

/* Java */

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.secure.session.SecSession;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * The context of accessing the data.
 *
 * @author anton.baukin@gmail.com.
 */
@SuppressWarnings("unchecked")
public class DataCtx implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: Data Context (bean) interface */

	/**
	 * The target domain of the data selection.
	 * Equals to the domain of the issuing user.
	 * May also be a system domain.
	 */
	public Long    getDomain()
	{
		return domain;
	}

	public void    setDomain(Long domain)
	{
		this.domain = domain;
	}

	public Date    getRequestTime()
	{
		return requestTime;
	}

	public void    setRequestTime(Date requestTime)
	{
		this.requestTime = requestTime;
	}

	/**
	 * Returns the authentication Session ID
	 * of the user issuing the request.
	 * Undefined for the system requests.
	 */
	public String  getSecSession()
	{
		return secSession;
	}

	public void    setSecSession(String secSession)
	{
		this.secSession = secSession;
	}

	public Map     getParams()
	{
		return params;
	}

	public void    setParams(Map params)
	{
		this.params = params;
	}


	/* public: support interface */

	public Object  param(Object key)
	{
		return (params == null)?(null):(params.get(key));
	}

	public DataCtx param(Serializable key, Serializable val)
	{
		EX.assertn(key);

		if(params == null)
			params = new HashMap(5);

		if(val == null)
			params.remove(key);
		else
			params.put(key, val);

		if(params.isEmpty())
			params = null;

		return this;
	}


	/* public: initialization */

	/**
	 * Initializes the data context Domain and
	 * the Secure Session from the data of
	 * currently pending web request.
	 */
	public DataCtx init()
	{
		//~: domain
		this.domain = SecPoint.domain();

		//~: current time
		this.requestTime = new Date();

		//~: secure session
		this.secSession = EX.asserts(
		  (String) SecPoint.secSession().attr(SecSession.ATTR_AUTH_SESSION));

		return this;
	}


	/* private: the context data */

	private Long   domain;
	private Date   requestTime;
	private String secSession;
	private Map    params;
}