package com.tverts.endure.core;

/* standard Java classes */

import java.security.MessageDigest;

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

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (auth + persons) */

import com.tverts.endure.auth.Auth;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.Computer;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.person.Persons;

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
public class GenCoreDomain extends GenesisHiberPartBase
{
	/* public: configuration parameters */

	@Param(required = true)
	public String    getDomainCode()
	{
		return domainCode;
	}

	public void      setDomainCode(String v)
	{
		if(SU.sXe(v)) throw new IllegalArgumentException();
		this.domainCode = SU.s2s(v);
	}

	@Param(required = true)
	public String    getDomainName()
	{
		return domainName;
	}

	public void      setDomainName(String v)
	{
		if(SU.sXe(v)) throw new IllegalArgumentException();
		this.domainName = SU.s2s(v);
	}

	@Param(required = true)
	public String    getSystemPassword()
	{
		return systemPassword;
	}

	public void      setSystemPassword(String v)
	{
		if(SU.sXe(v)) throw new IllegalArgumentException();
		this.systemPassword = SU.s2s(v);
	}

	@Param(required = true)
	public boolean   isTestDomain()
	{
		return testDomain;
	}

	public void      setTestDomain(boolean testDomain)
	{
		this.testDomain = testDomain;
	}


	/* public: Genesis interface */

	public void      generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: find or create the domain
		ensureDomain(ctx);

		//~: find or create System user
		ensureSystemUser(ctx);
	}


	/* protected: domain generation */

	protected void   ensureDomain(GenCtx ctx)
	{
		//~: find domain by the code
		Domain d = bean(GetDomain.class).
		  getDomain(getDomainCode());

		//?: {domain already exists}
		if(d != null)
		{
			ctx.set(d);

			LU.I(log(ctx), logsig(),
			  " found Domain pkey [", d.getPrimaryKey(),
			  "] code [", d.getCode(), "] name [", d.getName(), "]"
			);

			return;
		}

		//~: create new domain
		ctx.set(d = createNewDomain(ctx));

		LU.I(log(ctx), logsig(), " created ",
		  isTestDomain()?("test "):(""),
		  "Domain pkey [", d.getPrimaryKey(),
		  "] code [", d.getCode(), "], name [", d.getName(), "]"
		);
	}

	protected Domain createNewDomain(GenCtx ctx)
	{
		//~: create and save new instance
		Domain d = new Domain();
		setPrimaryKey(session(), d, isTestDomain());

		//~: code
		d.setCode(getDomainCode());

		//~: name
		d.setName(getDomainName());


		//!: do save
		actionRun(ActionType.SAVE, d);

		return d;
	}


	/* protected: System user generation */

	protected void      ensureSystemUser(GenCtx ctx)
	{
		//~: find System login
		AuthLogin sys = bean(GetAuthLogin.class).
		  getSystemLogin(ctx.get(Domain.class).getPrimaryKey());

		//?: {login exists for computer}
		if((sys != null) && (sys.getComputer() != null))
		{
			LU.I(log(ctx), logsig(),
			  " found System login for Computer code [",
			  sys.getComputer().getCode(), "] pkey [",
			  sys.getComputer().getPrimaryKey(), "]"
			);

			return;
		}

		//?: {login exists for computer}
		if((sys != null) && (sys.getPerson() != null))
		{
			LU.I(log(ctx), logsig(),
			  " found System login for Person name [",
			  Persons.name(sys.getPerson()), "] pkey [",
			  sys.getPerson().getPrimaryKey(), "]"
			);

			return;
		}

		//~: create default computer
		Computer c = createComputer(ctx);

		//?: {login exists}
		if(sys != null)
		{
			//~: assign the computer
			sys.setComputer(c);

			LU.I(log(ctx), logsig(), " assigned generated Computer pkey [",
			  c.getPrimaryKey(), "] to existing System login"
			);

			return;
		}

		//~: create system login
		sys = createSystemLogin(ctx, c);

		LU.I(log(ctx), logsig(), " generated System login pkey [",
		  sys.getPrimaryKey(), "] with generated Computer pkey [",
		  c.getPrimaryKey(), "]"
		);
	}

	protected Computer  createComputer(GenCtx ctx)
	{
		Computer c = new Computer();

		//~: domain
		c.setDomain(ctx.get(Domain.class));

		//~: code
		c.setCode(Auth.SYSTEM_USER);

		//~: name
		c.setName("System User");

		//~: comment
		c.setComment("Computer user automatically created " +
		  "for System login of each Domain.");


		//!: do save
		actionRun(ActionType.SAVE, c);

		return c;
	}

	protected AuthLogin createSystemLogin(GenCtx ctx, Computer c)
	{
		AuthLogin sys = new AuthLogin();

		//~: domain
		sys.setDomain(ctx.get(Domain.class));

		//~: code
		sys.setCode(Auth.SYSTEM_USER);

		//~: password
		setPassword(ctx, sys);

		//~: computer
		sys.setComputer(c);


		//!: do save
		actionRun(ActionType.SAVE, sys);

		return sys;
	}

	protected void      setPassword(GenCtx ctx, AuthLogin sys)
	{
		//?: {has no password configured}
		if(SU.sXe(getSystemPassword()))
			throw new IllegalStateException();

		//~: access SHA-1 digest
		MessageDigest d = ctx.get(MessageDigest.class);

		if(d == null) try
		{
			ctx.set(MessageDigest.class, d =
			  MessageDigest.getInstance("SHA-1"));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//~: encode & set
		sys.setPasshash(
		  Auth.passwordHash(d, getSystemPassword()));
	}


	/* private: generation parameters */

	private String  domainCode;
	private String  domainName;
	private String  systemPassword = "password";
	private boolean testDomain;
}