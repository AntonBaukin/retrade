package com.tverts.exec.api.system;

/* standard Java classes */

import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: api */

import com.tverts.api.core.Okey;
import com.tverts.api.system.SelfShuntStart;
import com.tverts.api.system.SelfShuntResults;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: exec */

import com.tverts.exec.ExecError;
import com.tverts.exec.ExecutorBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: system services + shunts  */

import com.tverts.system.services.ServicesPoint;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.service.SelfShuntGroupsEvent;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Executes Self-Shunt requests made by System user.
 *
 * @author anton.baukin@gmail.com
 */
public class ExecSelfShunt extends ExecutorBase
{
	public Object execute(Object request)
	  throws ExecError
	{
		//?: {start shunting request}
		if(request instanceof SelfShuntStart)
		{
			checkSecure();

			((SelfShuntStart)request).setLogKey(
			  startSelfShunt(((SelfShuntStart)request).getGroups())
			);

			return request;
		}

		//?: {shunting results request}
		if(request instanceof SelfShuntResults)
		{
			checkSecure();

			((SelfShuntResults)request).setResults(resultSelfShunt(
			  ((SelfShuntResults)request).getLogKey()
			));

			return request;
		}

		return null;
	}


	/* protected: execution */

	protected void   checkSecure()
	{
		//?: {not a system user}
		if(!SecPoint.isSystemLogin())
			throw EX.forbid("Only System user is allowed to self-shunt!");
	}

	protected String startSelfShunt(Set<String> groups)
	{
		EX.asserte(groups, "Self-Shunt groups must be provided!");

		//~: create shunt event
		SelfShuntGroupsEvent event = new SelfShuntGroupsEvent();

		//~: the domain to shunt
		event.setDomain(domain().getPrimaryKey());

		//~: groups
		event.getGroups().addAll(groups);

		//!: read-only flag is always set
		event.setReadonly(true);

		//~: log parameter
		String log = "Log " + DU.timefull2str(null);
		event.setLogParam(log);

		//!: send event for background execution
		ServicesPoint.send(
		  SelfShuntPoint.getInstance().getService().uid(),
		  event
		);

		return log;
	}

	protected String resultSelfShunt(String key)
	{
		EX.assertn(key = SU.s2s(key), "Self-Shunt lok key must be provided!");

		Property log = bean(GetProps.class).get(domain(), "Self-Shunt", key);
		return (log == null)?(null):(bean(GetProps.class).string(log));
	}
}