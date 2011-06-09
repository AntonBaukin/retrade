package com.tverts.endure.core;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.action;

/* com.tverts: genesis */

import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;


/**
 * Generates test domain with the name 'Test Domain'
 * and negative promary key. There is only one test
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

	private static final GenTestDomain INSTANCE =
	  new GenTestDomain();

	protected GenTestDomain()
	{}


	/* public: access test domain */

	/**
	 * Returns the test domain, or raises {@link IllegalStateException}
	 * if it is not created/discovered.
	 */
	public static Domain testDomain()
	{
		Domain res = getInstance().getTestDomain();

		if(res == null) throw new IllegalStateException(
		  "Test Domain is not discovered (generated)!"
		);

		return res;
	}

	public Domain        getTestDomain()
	{
		return testDomain;
	}


	/* public: Genesis interface */

	public Runnable      generate()
	  throws GenesisError
	{
		//~: create test domain if it does not exist yet
		createTestDomain();

		//~: ensure the domain related entities exists
		ensureTestDomain();

		return null;
	}


	/* protected: test domain generation & verification*/

	protected void createTestDomain()
	{
		//~: search for the test domain
		this.testDomain = bean(GetDomain.class).getTestDomain();

		//?: {it exists} nothing to do
		if(this.testDomain != null) return;

		//~: create and save new instance
		this.testDomain = new Domain();
		setPrimaryKey(session(), this.testDomain, true);
		this.testDomain.setName("Test Domain");

		//!: do save
		action(ActDomain.SAVE, this.testDomain).run();
	}

	protected void ensureTestDomain()
	{
		//TODO ensure test domain
		//action(ActDomain.ENSURE, this.testDomain).run();
	}


	/* protected: test domain reference */

	private Domain testDomain;
}