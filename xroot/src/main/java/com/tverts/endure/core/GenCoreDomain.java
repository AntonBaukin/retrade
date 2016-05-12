package com.tverts.endure.core;

/* Java */

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

/* com.tverts: api */

import com.tverts.api.clients.Computer;

/* com.tverts: endure (auth, messages, persons) */

import com.tverts.endure.auth.Auth;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.ComputerEntity;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.msg.GetMsg;
import com.tverts.endure.msg.Msg;
import com.tverts.endure.msg.MsgBoxObj;
import com.tverts.endure.person.Persons;

/* com.tverts: support */

import com.tverts.support.EX;
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

	private String domainCode;

	public void      setDomainCode(String v)
	{
		this.domainCode = EX.asserts(v);
	}

	@Param(required = true)
	public String    getDomainName()
	{
		return domainName;
	}

	private String domainName;

	public void      setDomainName(String v)
	{
		this.domainName = EX.asserts(v);
	}

	@Param(required = true)
	public boolean   isTestDomain()
	{
		return testDomain;
	}

	private boolean testDomain;

	public void      setTestDomain(boolean testDomain)
	{
		this.testDomain = testDomain;
	}

	@Param(required = true)
	public String    getSystemPassword()
	{
		return systemPassword;
	}

	private String systemPassword = "password";

	public void      setSystemPassword(String v)
	{
		this.systemPassword = EX.asserts(v);
	}

	@Param(descr = "Types of the messages links for the Domain source " +
	  "separated by semi-colons")
	public String getSystemMessages()
	{
		return systemMessages;
	}

	private String systemMessages;

	public void setSystemMessages(String systemMessages)
	{
		this.systemMessages = systemMessages;
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
		ComputerEntity c = createComputer(ctx);

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

	protected ComputerEntity createComputer(GenCtx ctx)
	{
		ComputerEntity ce = new ComputerEntity();
		Computer       c  = ce.getOx();

		//=: domain
		ce.setDomain(ctx.get(Domain.class));

		//=: code
		c.setCode(Auth.SYSTEM_USER);

		//=: name
		c.setName("System User");

		//=: remarks
		c.setRemarks("Computer user automatically created " +
		  "for System login of each Domain.");

		//!: do save
		ce.updateOx();
		actionRun(ActionType.SAVE, ce);

		return ce;
	}

	protected AuthLogin createSystemLogin(GenCtx ctx, ComputerEntity c)
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

		//~: subscribe to the domain messages
		loginMessages(sys);

		return sys;
	}

	protected void      setPassword(GenCtx ctx, AuthLogin sys)
	{
		//?: {has no password configured}
		EX.asserts(getSystemPassword());

		//~: access SHA-1 digest
		MessageDigest d = ctx.get(MessageDigest.class);

		if(d == null) try
		{
			ctx.set(MessageDigest.class, d =
			  MessageDigest.getInstance("SHA-1"));
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}

		//~: encode & set
		sys.setPasshash(Auth.passwordHash(d, getSystemPassword()));
	}

	protected void      loginMessages(AuthLogin sys)
	{
		String sm = getSystemMessages();
		if(SU.sXe(sm)) return;

		//~: separate by '\n'
		String[] types = SU.s2a(sm);
		EX.asserte(types);

		//~: take the messages bos for the system user
		MsgBoxObj mb = bean(GetMsg.class).msgBox(sys.getPrimaryKey());

		//~: create the links
		for(String type : types)
			Msg.link(mb, sys, type);
	}
}