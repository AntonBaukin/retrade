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

/* com.tverts: endure (core + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.person.Person;
import com.tverts.endure.person.Persons;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import static com.tverts.support.SU.cats;
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
		Object url = getClass().getResource("GenTestUsers.xml");
		if(url == null) throw new GenesisError(this, ctx,
		  new IllegalStateException("No 'GenTestUsers.xml' file found!"));

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
		if((s.computer == null) && (s.person == null))
			throw new IllegalStateException(
			  "Set <computer> or <person> tag in 'GenTestUsers.xml' file!");

		if(s.login == null) throw new IllegalStateException(
		  "Set <login> into <computer>, <person> " +
		  "tags in 'GenTestUsers.xml' file!");

		if((s.login.code == null) && (s.computer != null))
			s.login.code = s.computer.code;

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
		StateComputer s = gs.computer;
		Computer      c = bean(GetAuthLogin.class).
		  getComputer(ctx.get(Domain.class).getPrimaryKey(), s.code);

		if(c != null)
		{
			LU.I(log(ctx), logsig(), " found computer with code '",
			  s.code, "' already existing in the database.");

			return false;
		}

		s.computer = c = new Computer();

		c.setDomain(ctx.get(Domain.class));
		c.setCode(s.code);
		c.setName(s.name);
		c.setComment(s.comment);

		actionRun(ActionType.SAVE, c);

		LU.I(log(ctx), logsig(), " created computer with code '",
		  s.code, "' ", (gs.login == null)?("without login."):
		    (" with login '" + gs.login.code + "'.")
		);

		return true;
	}

	protected boolean savePerson(GenCtx ctx, GenState gs)
	  throws GenesisError
	{
		StatePerson s = gs.person;
		Person      p = (gs.login == null)?(null):
		  bean(GetAuthLogin.class).getPersonByLogin(
		    ctx.get(Domain.class).getPrimaryKey(), gs.login.code);

		if(p != null)
		{
			LU.I(log(ctx), logsig(), " found person '", Persons.name(p),
			 "' with login [", gs.login.code,
			 "] already existing in the database.");

			return false;
		}

		s.person = p = new Person();

		p.setDomain(ctx.get(Domain.class));
		p.setLastName(s.lastName);
		p.setFirstName(s.firstName);
		p.setMiddleName(s.middleName);
		p.setGender(s.gender);
		p.setEmail(s.email);
		p.setPhoneMob(s.phoneMob);
		p.setPhoneWork(s.phoneWork);

		actionRun(ActionType.SAVE, p);

		LU.I(log(ctx), logsig(), " created person '", Persons.name(p),
		  (gs.login == null)?("without login."):
		    ("' with login [" + gs.login.code + "].")
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
		if(s.computer != null)
			l.setComputer(s.computer.computer);

		//~: person
		if(s.person != null)
			l.setPerson(s.person.person);


		//!: do save the login
		actionRun(ActionType.SAVE, l);

		//~: flush the session
		session().flush();

		//~: create secure instances
		secure(ctx, s, l);
	}

	protected void    secure(GenCtx ctx, GenState s, AuthLogin l)
	  throws GenesisError
	{
		//?: {there is no security entries}
		if(s.secEntries == null) return;

		//c: create ask force event
		for(SecEntry se : s.secEntries)
			EventPoint.react( //<-- !: send it
			  new AskSecForceEvent(l).
			  setForce(se.force)
			);
	}


	/* protected: xml handler states */

	protected static class StateLogin
	{
		public String code;
		public String password;
	}

	protected static class StateComputer
	{
		public String code;
		public String name;
		public String comment;

		public Computer computer;
	}

	protected static class StatePerson
	{
		public String    lastName;
		public String    firstName;
		public String    middleName;
		public Character gender;
		public String    email;
		public String    phoneMob;
		public String    phoneWork;

		public Person    person;
	}

	protected static class SecEntry
	{
		public String force;
	}

	protected static class GenState
	{
		public StateLogin     login;
		public StateComputer  computer;
		public StatePerson    person;
		public List<SecEntry> secEntries;
	}



	/* protected: xml handler */

	protected class   ReadTestUsers
	          extends SaxProcessor<GenState>
	{
		public ReadTestUsers(GenCtx ctx)
		{
			this.ctx = ctx;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			if(level() == 1)
				event().state(new GenState());

			if((level() == 2) && "login".equals(event().tag()))
				state(1).login = new StateLogin();
		}

		protected void open()
		{
			//~: <computer>
			if((level() == 1) && event().tag("computer"))
			{
				state().computer = new StateComputer();

				state().computer.code = event().attr("code");
				state().computer.name = event().attr("name");
			}

			//~: <person>
			if((level() == 1) && event().tag("person"))
			{
				state().person = new StatePerson();

				state().person.lastName   = event().attr("last-name");
				state().person.middleName = event().attr("middle-name");
				state().person.firstName  = event().attr("first-name");

				String gender             = event().attr("gender");
				state().person.gender     = (gender == null)?(null):
				  (gender.length() == 1)?(gender.charAt(0)):(null);

				state().person.email      = event().attr("email");
				state().person.phoneMob   = event().attr("mobile-phone");
				state().person.phoneWork  = event().attr("work-phone");
			}

			//~: (<computer> | <person>) <login>
			if((level() == 2) && event().tag("login"))
			{
				state(1).login.code = event().attr("code");
				state(1).login.password = event().attr("password");
			}

			//~: (<computer> | <person>) <secure>
			if((level() == 2) && event().tag("secure"))
			{
				//~: force attribute
				String force = event().attr("force");

				if(force == null) throw new IllegalStateException(cats(
				  "Gen Test Users, login [", state(1).login.code,
				  "] has <secure force = '?'> undefined!"
				));

				//~: create secure entry
				SecEntry e = new SecEntry();
				e.force = force;

				if(state(1).secEntries == null)
					state(1).secEntries = new ArrayList<SecEntry>(4);
				state(1).secEntries.add(e);
			}
		}

		protected void close()
		{
			if((level() == 2) && "comment".equals(event().tag()))
				if(state(1).computer != null)
					state(1).computer.comment = event().text().trim();

			if((level() == 1) && (state() != null)) try
			{
				generate(ctx, state());
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}


		/* genesis context */

		private GenCtx ctx;
	}
}