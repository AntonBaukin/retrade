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
	 * Returns the test domain, or raises {@link IllegalStateException}
	 * if it is not created/discovered.
	 */
	public static Domain testDomain()
	{
		Domain res = getInstance().getTestDomain();

		if(res == null) throw new IllegalStateException(
		  "Test Domain is not discovered or generated!"
		);

		return res;
	}

	public Domain        getTestDomain()
	{
		return testDomain;
	}


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: create test domain if it does not exist yet
		createTestDomain(ctx);
	}


	/* protected: test domain generation & verification */

	protected void  setTestDomain(Domain testDomain)
	{
		getInstance().testDomain = this.testDomain = testDomain;
	}

	protected void  createTestDomain(GenCtx ctx)
	{
		//~: search for the test domain
		Domain d = bean(GetDomain.class).getTestDomain();
		setTestDomain(d);

		//?: {it exists} nothing to do
		if(d != null) return;

		//~: create and save new instance
		setTestDomain(d = new Domain());
		setPrimaryKey(session(), d, true);
		d.setName("Test Domain");

		//!: do save
		actionRun(ActionType.SAVE, d);

		//~: log success
		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " had created Test Domain with PK = ", d.getPrimaryKey());
	}


	/* protected: test domain reference */

	private Domain testDomain;
}