package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.services.events.ServiceEventBase;


/**
 * Event sent by tasks execution service to
 * the execution planning service after the
 * task is completed (independently on an error).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ExecDoneEvent extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;


	/* public: ExecDoneEvent interface */

	public ExecRunEvent getRunEvent()
	{
		return runEvent;
	}

	public void         setRunEvent(ExecRunEvent runEvent)
	{
		this.runEvent = runEvent;
	}


	/* the task primary key */

	private ExecRunEvent runEvent;
}