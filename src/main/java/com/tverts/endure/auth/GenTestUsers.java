package com.tverts.endure.auth;

/* Java */

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.endure.msg.Messages;
import com.tverts.endure.person.FirmEntity;
import com.tverts.endure.person.GetFirm;
import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: events */

import com.tverts.event.EventPoint;

/* com.tverts: secure */

import com.tverts.secure.force.AskSecForceEvent;

/* com.tverts: api */

import com.tverts.api.clients.Computer;
import com.tverts.api.clients.Person;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.person.Persons;
import com.tverts.endure.person.PersonEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
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
		ComputerEntity ce = bean(GetAuthLogin.class).getComputer(
		  ctx.get(Domain.class).getPrimaryKey(),
		  gs.computer.getCode()
		);

		if(ce != null)
		{
			LU.I(log(ctx), logsig(), " computer with code [",
			  gs.computer.getCode(), "] already exists");

			return false;
		}

		gs.computerEntity = ce = new ComputerEntity();

		//=: domain
		ce.setDomain(ctx.get(Domain.class));

		//=: computer instance
		ce.setOx(gs.computer);

		//!: save
		actionRun(ActionType.SAVE, ce);

		LU.I(log(ctx), logsig(), " created computer with code [",
		  ce.getCode(), "] with login [", gs.login.code, "]"
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

		//?: {has firm code}
		if(!SU.sXe(gs.person.getXFirmKey()))
			assignFirm(pe);

		//!: save the person
		pe.updateOx();
		actionRun(ActionType.SAVE, pe);

		LU.I(log(ctx), logsig(), " created person ",
		  Persons.name(pe), " with login [" + gs.login.code + "]"
		);

		return true;
	}

	protected void    assignFirm(PersonEntity pe)
	{
		FirmEntity fe = EX.assertn(bean(GetFirm.class).
		  getFirm(pe.getDomain(), pe.getOx().getXFirmKey()),

		  "Firm entity with code [", pe.getOx().getXFirmKey(),
		  "] not found in Domain [", pe.getDomain().getPrimaryKey(), "]!"
		);

		//=: firm entity
		pe.setFirm(fe);

		//~: clear the firm x-key (it's code)
		pe.getOx().setXFirmKey(null);
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

		//~: create message links
		messages(ctx, s, l);
	}

	protected void    secure(GenCtx ctx, GenState s, AuthLogin l)
	  throws GenesisError
	{
		//?: {there is no security entries}
		if(s.secures == null) return;

		//c: create ask force event
		for(String force : s.secures)
			EventPoint.react( //<-- !: send it
			  new AskSecForceEvent(l).setForce(force)
			);
	}

	protected void    messages(GenCtx ctx, GenState s, AuthLogin l)
	  throws GenesisError
	{
		//?: {there is no messages linked}
		if(s.messages == null) return;

		//c: create message links
		for(String type : s.messages)
			Messages.link(Messages.box(l.getPrimaryKey()),
			  ctx.get(Domain.class).getPrimaryKey(), type);
	}


	/* protected: XML Handler States */

	protected static class Login
	{
		public String code;
		public String password;
	}

	protected static class GenState
	{
		public Login          login;
		public Person         person;
		public Computer       computer;
		public PersonEntity   personEntity;
		public ComputerEntity computerEntity;
		public List<String>   secures;
		public List<String>   messages;
	}


	/* protected: XML Handler */

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
			//?: <person>
			if(istag(1, "person"))
			{
				event().state(new GenState());
				state().person = new com.tverts.api.clients.Person();
			}

			//?: <computer>
			else if(istag(1, "computer"))
			{
				event().state(new GenState());
				state().computer = new com.tverts.api.clients.Computer();
			}

			//?: (<person> | <computer>) <login>
			else if(istag(1, "person", "login") || istag(1, "computer", "login"))
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

				//~: add secure force entry
				if(state(1).secures == null)
					state(1).secures = new ArrayList<>(4);
				state(1).secures.add(force);
			}

			//~: (<computer> | <person>) <messages>
			else if(istag(2, "messages"))
			{
				//~: message link type
				String type = EX.asserts(attr("type"),
				  "Gen Test Users, login [", state(1).login.code,
				  "] has <messages type = '?'> undefined!"
				);

				//~: add message type
				if(state(1).messages == null)
					state(1).messages = new ArrayList<>(4);
				state(1).messages.add(type);
			}
		}

		protected void close()
		{
			//?: <computer>
			if(istag(1, "computer"))
				requireFillClearTags(state().computer, true, "code", "name");

			//?: <person>
			else if(istag(1, "person"))
				requireFillClearTags(state().person, true, "last-name", "first-name");

			//?: {<computer> | <person>}
			if(islevel(1)) try
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