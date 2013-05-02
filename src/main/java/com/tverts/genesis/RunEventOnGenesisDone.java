package com.tverts.genesis;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.events.EventBase;
import com.tverts.system.services.events.EventRunDelegate;

/* com.tverts: self-shunt service */

import com.tverts.shunts.service.SelfShuntEvent;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Responds on {@link GenesisDone} event
 * by sending the event configured.
 *
 * @author anton.baukin@gmail.com
 */
public class   RunEventOnGenesisDone
       extends EventRunDelegate
{
	/* protected: delegating */

	protected boolean   isThatEvent(Event event)
	{
		if(!(event instanceof GenesisDone))
			return false;

		//?: {test domain is not defined} apply to all
		if(getTestDomain() == null)
			return true;

		//?: {genesis has no domain} skip it
		if(((GenesisDone)event).getDomain() == null)
			return false;

		//~: load that domain
		Domain d = bean(GetDomain.class).
		  getDomain(((GenesisDone)event).getDomain());

		//?: {not found it} skip this (error)
		if(d == null) return false;

		//?: {not that domain}
		return getTestDomain().equals(d.getCode());
	}

	protected EventBase updateEvent(EventBase res, Event event)
	{
		if(res instanceof SelfShuntEvent)
			((SelfShuntEvent)res).setDomain(((GenesisDone)event).getDomain());

		return res;
	}


	/* public: RunEventOnGenesisDone (bean) interface */

	public String getTestDomain()
	{
		return testDomain;
	}

	public void setTestDomain(String testDomain)
	{
		this.testDomain = s2s(testDomain);
	}


	/* configuration */

	private String testDomain;
}