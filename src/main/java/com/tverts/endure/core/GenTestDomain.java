package com.tverts.endure.core;

/* standard Java classe */

import java.util.List;

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


/**
 * Generates test domain with the name 'Test Domain'
 * and negative primary key. There is only one test
 * domain in the system.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestDomain extends GenesisHiberPartBase
{
	/* public: Singleton */

	public static GenTestDomain getInstance()
	{
		return INSTANCE;
	}

	private static volatile GenTestDomain INSTANCE;

	protected GenTestDomain()
	{
		synchronized(GenTestDomain.class)
		{
			if(INSTANCE != null)
				throw new IllegalStateException();
			INSTANCE = this;
		}
	}


	/* public: access test domain */

	/**
	 * Returns the primary test domain, or raises
	 * {@link IllegalStateException} if it is not
	 * created or discovered.
	 */
	public static Domain testDomain()
	{
		Domain res = getInstance().getTestDomain();

		if(res == null) throw new IllegalStateException(
		  "Primary test Domain is not discovered or generated!"
		);

		return res;
	}

	/**
	 * Returns the empty test domain, or raises
	 * {@link IllegalStateException}
	 * if it is not created/discovered.
	 */
	public static Domain emptyDomain()
	{
		Domain res = getInstance().getEmptyDomain();

		if(res == null) throw new IllegalStateException(
		  "Empty test Domain is not discovered or generated!"
		);

		return res;
	}

	public Domain        getTestDomain()
	{
		return testDomain;
	}

	public Domain        getEmptyDomain()
	{
		return emptyDomain;
	}


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		List<Domain> domains = bean(GetDomain.class).
		  getTestDomains();

		final String NAME0 = "Тестовый домен";
		final String NAMEE = "Пустой домен";

		//~: test (primary) domain
		Domain d = findDomain(ctx, domains, NAME0);

		if(d != null)
			setTestDomain(d);
		else
			setTestDomain(createTestDomain(ctx, NAME0));

		//~: empty domain
		d = findDomain(ctx, domains, NAMEE);

		if(d != null)
			setEmptyDomain(d);
		else
			setEmptyDomain(createTestDomain(ctx, NAMEE));
	}


	/* protected: test domain generation & verification */

	protected void   setTestDomain(Domain testDomain)
	{
		getInstance().testDomain = this.testDomain = testDomain;
	}

	protected void  setEmptyDomain(Domain emptyDomain)
	{
		this.emptyDomain = emptyDomain;
	}

	protected Domain findDomain(GenCtx ctx, List<Domain> domains, String name)
	{
		Domain r = null;

		for(Domain d : domains)
			if(name.equals(d.getName()))
			{
				r = d;
				break;
			}

		if(r != null) if(LU.isI(log(ctx)))
			LU.I(log(ctx), logsig(),
			     " found Test Domain '", name, "', key = ",
			     r.getPrimaryKey()
			);

		return r;
	}

	protected Domain createTestDomain(GenCtx ctx, String name)
	{
		//~: create and save new instance
		Domain d = new Domain();
		setPrimaryKey(session(), d, true);
		d.setName(name);

		//!: do save
		actionRun(ActionType.SAVE, d);

		//~: log success
		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " created Test Domain '", name, "', key = ",
		  d.getPrimaryKey()
		);

		return d;
	}


	/* protected: test domain reference */

	private Domain testDomain;
	private Domain emptyDomain;
}