package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.zservices.events.ServiceEventBase;


/**
 * Event to execute the task.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecRunEvent extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;


	/* public: ExecRunEvent interface */

	/**
	 * Primary key of the task.
	 */
	public long getTaskKey()
	{
		return taskKey;
	}

	public void setTaskKey(long taskKey)
	{
		this.taskKey = taskKey;
	}


	/* the task primary key */

	private long taskKey;
}