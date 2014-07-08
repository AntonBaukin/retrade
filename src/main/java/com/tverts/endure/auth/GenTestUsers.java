package com.tverts.endure.auth;

/* standard Java classes */

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: events */

import com.tverts.event.EventPoint;

/* com.tverts: secure */

import com.tverts.secure.force.AskSecForceEvent;

/* com.tverts: endure api */

import com.tverts.api.clients.Person;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.person.Persons;
import com.tverts.endure.person.PersonEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Reads 'GenTestUsers.xml' file with the users
 * of the Test Domain. Creates the instances.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestUsers extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void    generate(GenCtx ctx)
	  throws GenesisError
	{
		Object url = EX.assertn(
		  getClass().getResource("GenTestUsers.xml"),
		  "No GenTestUsers.xml file found!"
		);

		try
		{
			new ReadTestUsers(ctx).process(url.toString());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}
	}


	/* protected: generation callback */

	protected void    generate(GenCtx ctx, GenState s)
	  throws GenesisError
	{
		EX.assertn(s.login, "Person or computer has no login!");

		EX.assertx(
		  ((s.computer != null) && (s.person == null)) ||
		  ((s.computer == null) && (s.person != null))
		);

		//?: {take login from computer code}
		if((s.computer != null) && (s.login.code == null))
			s.login.code = EX.asserts(s.computer.getCode());

		if(s.computer != null)
			if(saveComputer(ctx, s))
				saveLogin(ctx, s);

		if(s.person != null)
			if(savePerson(ctx, s))
				saveLogin(ctx, s);
	}

	protected boolean saveComputer(GenCtx ctx, GenState gs)
	  throws GenesisError
	{
		Computer c = bean(GetAuthLogin.class).getComputer(
		  ctx.get(Domain.class).getPrimaryKey(),
		  gs.computer.getCode()
		);

		if(c != null)
		{
			LU.I(log(ctx), logsig(), " computer with code [",
			  gs.computer.getCode(), "] already exists");

			return false;
		}

		gs.computerEntity = c = new Computer();

		c.setDomain(ctx.get(Domain.class));
		c.setCode(gs.computer.getCode());
		c.setName(gs.computer.getName());
		c.setComment(gs.computer.getComment());

		actionRun(ActionType.SAVE, c);

		LU.I(log(ctx), logsig(), " created computer with code [",
		  c.getCode(), "] with login [", gs.login.code, "]"
		);

		return true;
	}

	protected boolean savePerson(GenCtx ctx, GenState gs)
	  throws GenesisError
	{
		PersonEntity pe = bean(GetAuthLogin.class).getPersonByLogin(
		  ctx.get(Domain.class).getPrimaryKey(),
		  gs.login.code
		);

		if(pe != null)
		{
			LU.I(log(ctx), logsig(), " person ", Persons.name(pe),
			 " with login [", gs.login.code, "] already exists");

			return false;
		}

		gs.personEntity = pe = new PersonEntity();

		//=: domain
		pe.setDomain(ctx.get(Domain.class));

		//=: person data
		pe.setOx(gs.person);

		//!: save the person
		actionRun(ActionType.SAVE, pe);

		LU.I(log(ctx), logsig(), " created person ",
		  Persons.name(pe), " with login [" + gs.login.code + "]"
		);

		return true;
	}

	protected void    saveLogin(GenCtx ctx, GenState s)
	  throws GenesisError
	{
		AuthLogin     l = new AuthLogin();
		MessageDigest d = ctx.get(MessageDigest.class);
		String        p;

		//~: encode the password
		try
		{
			if(d == null) ctx.set(MessageDigest.class, d =
			  MessageDigest.getInstance("SHA-1"));

			p = Auth.passwordHash(d, s.login.password);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//~: domain
		l.setDomain(ctx.get(Domain.class));

		//~: code
		l.setCode(s.login.code);

		//~: password hash
		l.setPasshash(p);

		//~: computer
		if(s.computerEntity != null)
			l.setComputer(s.computerEntity);

		//~: person
		if(s.personEntity != null)
			l.setPerson(s.personEntity);


		//!: do save the login
		actionRun(ActionType.SAVE, l);

		//~: create secure instances
		secure(ctx, s, l);
	}

	protected void    secure(GenCtx ctx, GenState s, AuthLogin l)
	  throws GenesisError
	{
		//?: {there is no security entries}
		if(s.secures == null) return;

		//c: create ask force event
		for(Secure se : s.secures)
			EventPoint.react( //<-- !: send it
			  new AskSecForceEvent(l).
			  setForce(se.force)
			);
	}


	/* protected: xml handler states */

	protected static class Login
	{
		public String code;
		public String password;
	}

	protected static class Secure
	{
		public String force;
	}

	protected static class GenState
	{
		public Login  login;
		public Person person;
		public com.tverts.api.clients.Computer computer;
		public PersonEntity personEntity;
		public Computer computerEntity;
		public List<Secure> secures;
	}


	/* protected: xml handler */

	protected class ReadTestUsers extends SaxProcessor<GenState>
	{
		public ReadTestUsers(GenCtx ctx)
		{
			this.ctx = ctx;
			this.collectTags = true;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: <person> | <computer>
			if(istag(1, "person", "computer"))
			{
				event().state(new GenState());

				//?: <person>
				if(istag("person"))
					state().person = new com.tverts.api.clients.Person();
				//?: <computer>
				else if(istag("computer"))
					state().computer = new com.tverts.api.clients.Computer();
			}

			//?: (<person> | <computer>) <login>
			else if(istag(2, "login"))
				state(1).login = new Login();
		}

		protected void open()
		{
			//~: (<computer> | <person>) <login>
			if(istag(2, "login"))
			{
				state(1).login.code = attr("code");
				state(1).login.password = EX.asserts(
				  attr("password"), "Login password is undefined!"
				);
			}

			//~: (<computer> | <person>) <secure>
			else if(istag(2, "secure"))
			{
				//~: force attribute
				String force = EX.asserts(attr("force"),
				  "Gen Test Users, login [", state(1).login.code,
				  "] has <secure force = '?'> undefined!"
				);

				//~: create secure entry
				Secure e = new Secure();
				e.force = force;

				if(state(1).secures == null)
					state(1).secures = new ArrayList<Secure>(4);
				state(1).secures.add(e);
			}
		}

		protected void close()
		{
			//?: <computer>
			if(istag(1, "computer"))
				requireFillClearTags(state().computer, "code", "name");

			//?: <person>
			else if(istag(1, "person"))
				requireFillClearTags(state().person, "last-name", "first-name");

			//?: {<computer> | <person>}
			if(level(1)) try
			{
				generate(ctx, state());
			}
			catch(Throwable e)
			{
				throw EX.wrap(e);
			}
		}


		/* genesis context */

		private GenCtx ctx;
	}
}