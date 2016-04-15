package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.services.events.ServiceEventBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Event for subclass of {@link ExecPlanServiceBase}
 * to plan execution of the tasks stored in the database.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecPlanEvent extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public ExecPlanEvent()
	{}

	public ExecPlanEvent(long requestKey)
	{
		EX.assertx(requestKey > 0L);
		this.requestKey = requestKey;
	}


	/* public: bean interface */

	public long getRequestKey()
	{
		return requestKey;
	}

	public void setRequestKey(long requestKey)
	{
		this.requestKey = requestKey;
	}


	/* private: the request primary key hint */

	private long requestKey;
}