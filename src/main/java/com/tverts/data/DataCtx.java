package com.tverts.data;

/* Java */

import java.io.Serializable;
import java.util.Date;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.ReportRequest;


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

	public Long    getLogin()
	{
		return login;
	}

	public void    setLogin(Long login)
	{
		this.login = login;
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
	 * Additional parameters given as
	 * a serializable Java Bean.
	 */
	public Object  getParams()
	{
		return params;
	}

	public void    setParams(Object params)
	{
		this.params = params;
	}


	/* public: initialization */

	/**
	 * Initializes the data context Domain and
	 * the Login from the current web request.
	 */
	public DataCtx init()
	{
		//~: domain
		this.domain = SecPoint.domain();

		//~: login
		this.login = SecPoint.login();

		//~: current time
		this.requestTime = new Date();

		return this;
	}

	public DataCtx init(ReportRequest r, Object params)
	{
		//~: domain
		this.domain = r.getDomain().getPrimaryKey();

		//~: login
		this.login = r.getOwner().getPrimaryKey();

		//~: request time
		this.requestTime = r.getTime();

		//~: parameter model
		this.params = params;

		return this;
	}


	/* private: the context data */

	private Long   domain;
	private Long   login;
	private Date   requestTime;
	private Object params;
}