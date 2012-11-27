package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.zservices.events.ServiceDelayedEventBase;


/**
 * Event for subclass of {@link ExecPlanServiceBase}
 * to plan execution of the tasks stored in the database.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecPlanEvent extends ServiceDelayedEventBase
{
	public static final long serialVersionUID = 0L;
}