package com.tverts.endure.auth;

/* standard Java classes */

import java.security.MessageDigest;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.person.Person;
import static com.tverts.endure.core.GenTestDomain.testDomain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Reads 'GenUsers.xml' file with the users
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
		Object url = getClass().getResource("GenUsers.xml");
		if(url == null) throw new GenesisError(this, ctx,
		  new IllegalStateException("No 'GenUsers.xml' file found!"));

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
			  "Set <computer> or <person> tag in 'GenUsers.xml' file!");

		if(s.login == null) throw new IllegalStateException(
		  "Set <login> into <computer>, <person> " +
		  "tags in 'GenUsers.xml' file!");

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
		  getComputer(testDomain().getPrimaryKey(), s.code);
		if(c != null) return false;

		s.computer = c = new Computer();

		c.setDomain(testDomain());
		c.setCode(s.code);
		c.setName(s.name);
		c.setComment(s.comment);

		actionRun(ActionType.SAVE, c);
		return true;
	}

	protected boolean savePerson(GenCtx ctx, GenState gs)
	  throws GenesisError
	{
		StatePerson s = gs.person;
		Person      p = bean(GetAuthLogin.class).
		  getPersonByLogin(testDomain().getPrimaryKey(), gs.login.code);
		if(p != null) return false;

		s.person = p = new Person();

		p.setDomain(testDomain());
		p.setLastName(s.lastName);
		p.setFirstName(s.firstName);
		p.setMiddleName(s.middleName);
		p.setGender(s.gender);

		actionRun(ActionType.SAVE, p);
		return true;
	}

	protected void    saveLogin(GenCtx ctx, GenState s)
	  throws GenesisError
	{
		AuthLogin     l = new AuthLogin();
		MessageDigest d = (MessageDigest) ctx.get(MessageDigest.class);
		String        p;

		try
		{
			if(d == null) ctx.set(MessageDigest.class, d =
			  MessageDigest.getInstance("SHA-1"));

			d.reset();
			p = new String(SU.bytes2hex(
			  d.digest(s.login.password.getBytes("UTF-8"))
			));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		l.setDomain(testDomain());
		l.setCode(s.login.code);
		l.setPasshash(p);

		if(s.computer != null)
			l.setComputer(s.computer.computer);
		if(s.person != null)
			l.setPerson(s.person.person);

		actionRun(ActionType.SAVE, l);
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

		public Person    person;
	}

	protected static class GenState
	{
		public StateLogin    login;
		public StateComputer computer;
		public StatePerson   person;
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
			if((level() == 1) && "computer".equals(event().tag()))
			{
				state().computer = new StateComputer();

				state().computer.code = event().attr("code");
				state().computer.name = event().attr("name");
			}

			if((level() == 1) && "person".equals(event().tag()))
			{
				state().person = new StatePerson();

				state().person.lastName   = event().attr("last-name");
				state().person.middleName = event().attr("middle-name");
				state().person.firstName  = event().attr("first-name");

				String gender             = event().attr("gender");
				state().person.gender     = (gender == null)?(null):
				  (gender.length() == 1)?(gender.charAt(0)):(null);
			}

			if((level() == 2) && "login".equals(event().tag()))
			{
				state(1).login.code = event().attr("code");
				state(1).login.password = event().attr("password");
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