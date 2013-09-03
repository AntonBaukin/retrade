package com.tverts.genesis;

/* standard Java classes */

import java.util.Map;

/* com.tverts: system services */

import com.tverts.system.services.events.ServiceEventBase;


/**
 * {@link GenesisService} sends this event to
 * Main Service after the generation completed.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisDone extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;


	/* public: GenesisDone (bean) interface */

	public GenesisEvent getEvent()
	{
		return event;
	}

	public void setEvent(GenesisEvent event)
	{
		this.event = event;
	}

	public Long getDomain()
	{
		return domain;
	}

	public void setDomain(Long domain)
	{
		this.domain = domain;
	}

	public Map getGenCtx()
	{
		return genCtx;
	}

	public void setGenCtx(Map genCtx)
	{
		this.genCtx = genCtx;
	}


	/* the original genesis event */

	private GenesisEvent event;
	private Long         domain;
	private Map          genCtx;
}