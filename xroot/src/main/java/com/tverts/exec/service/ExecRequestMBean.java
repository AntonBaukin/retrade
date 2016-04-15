package com.tverts.exec.service;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.MessageListener;

/* com.tverts: services */

import com.tverts.system.services.ServicesPoint;


/**
 * Listens for Execution Notifications Queue and
 * redirects to the Execution Plan Service.
 *
 * @author anton.baukin@gmail.com.
 */
public class ExecRequestMBean implements MessageListener
{
	/* public: MessageListener interface */

	public static final String REQUEST_ID_KEY =
	  "com_tverts_exec_requestID";

	public void onMessage(Message msg)
	{
		Long pk = null; try
		{
			if(msg.propertyExists(REQUEST_ID_KEY))
				pk = msg.getLongProperty(REQUEST_ID_KEY);
		}
		catch(Throwable e)
		{
			//~: omit this exception
			return;
		}

		//?: {ho primary key is given} do nothing
		if(pk == null)
			return;

		//~: send execution plan event
		ServicesPoint.send(
		  ExecPlanServiceBase.SERVICE_NAME,
		  new ExecPlanEvent(pk)
		);
	}
}