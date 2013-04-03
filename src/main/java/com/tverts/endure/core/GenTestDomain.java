package com.tverts.endure.core;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Generates test domain with the configured code
 * and the name. System test domain (created by
 * default by Genesis Service) has code 'Test'.
 *
 * Note that this genesis unit is always invoked
 * the first. It saves the domain in the context
 * as {@link GenCtx#set(Object)}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestDomain extends GenesisHiberPartBase
{
	/* public: configuration parameters */

	public String getDomainCode()
	{
		return domainCode;
	}

	public void   setDomainCode(String v)
	{
		if(SU.sXe(v)) throw new IllegalArgumentException();
		this.domainCode = SU.s2s(v);
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void   setDomainName(String v)
	{
		if(SU.sXe(v)) throw new IllegalArgumentException();
		this.domainName = SU.s2s(v);
	}


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: find domain by the code
		Domain d = findDomain(ctx);

		if(d != null)
			setTestDomain(ctx, d);
		else
			setTestDomain(ctx, createTestDomain(ctx));
	}


	/* protected: test domain generation & verification */

	protected void   setTestDomain(GenCtx ctx, Domain domain)
	{
		ctx.set(domain);
	}

	protected Domain findDomain(GenCtx ctx)
	{
		Domain d = bean(GetDomain.class).
		  getDomain(getDomainCode());
		if(d == null) return null;

		LU.I(log(ctx), logsig(),
		  " found test Domain pkey [", d.getPrimaryKey(),
		  "] code [", d.getCode(), "] name [", d.getName(), "]"
		);

		return d;
	}

	protected Domain createTestDomain(GenCtx ctx)
	{
		//~: create and save new instance
		Domain d = new Domain();
		setPrimaryKey(session(), d, true);

		//~: code
		d.setCode(getDomainCode());

		//~: name
		d.setName(getDomainName());

		//!: do save
		actionRun(ActionType.SAVE, d);

		//~: log success
		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " created test Domain pkey [", d.getPrimaryKey(),
		  "] code [", d.getCode(), "], name [", d.getName(), "]"
		);

		return d;
	}


	/* private: generation parameters */

	private String domainCode;
	private String domainName;
}