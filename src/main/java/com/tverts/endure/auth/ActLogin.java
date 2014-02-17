package com.tverts.endure.auth;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.flush;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;
import com.tverts.endure.person.ActPerson;
import com.tverts.endure.person.Person;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Builder for actions related to the
 * authentication and the logins.
 *
 * @author anton.baukin@gmail.com
 */
public class ActLogin extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE     =
	  ActionType.SAVE;

	public static final ActionType CLOSE    =
	  new ActionType(AuthLogin.class, "close");

	/**
	 * Update password (and login code).
	 */
	public static final ActionType PASSWORD =
	  new ActionType(AuthLogin.class, "password");

	/**
	 * Update (or create) Person of the Login.
	 * If Login already refers a Computer,
	 * the latter would not be removed.
	 */
	public static final ActionType PERSON   =
	  new ActionType(AuthLogin.class, "person");


	/* parameters */

	/**
	 * New login code (optional) of action
	 * {@link #PASSWORD}.
	 */
	public static final String PARAM_LOGIN    =
	  ActLogin.class.getName() + ": login";

	/**
	 * New password hash of action {@link #PASSWORD}.
	 */
	public static final String PARAM_PASSHASH =
	  ActLogin.class.getName() + ": password";

	/**
	 * @see {@link ActPerson#PARAM_PERSON}
	 */
	public static final String PARAM_PERSON   =
	  ActPerson.PARAM_PERSON;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveLogin(abr);

		if(CLOSE.equals(actionType(abr)))
			closeLogin(abr);

		if(PASSWORD.equals(actionType(abr)))
			changeLoginPassword(abr);

		if(PERSON.equals(actionType(abr)))
			updatePerson(abr);
	}


	/* protected: action methods */

	protected void saveLogin(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		//~: set create time
		AuthLogin l = target(abr, AuthLogin.class);
		if(l.getCreateTime() == null)
			l.setCreateTime(new java.util.Date());

		//~: send created event
		reactCreated(abr);

		//~: save the login
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set login unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getUnityType());

		complete(abr);
	}

	protected void closeLogin(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		AuthLogin login = target(abr, AuthLogin.class);

		//?: {the login is already closed}
		if(login.getCloseTime() != null) throw EX.state(
		  "Authentication Login [", login.getPrimaryKey(),
		  "] '", login.getCode(), "' is already closed!"
		);

		//~: close the login
		chain(abr).first(new CloseAuthLoginAction(task(abr)));

		complete(abr);
	}

	protected void changeLoginPassword(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		//~: change the login, or password
		chain(abr).first(new ChangeLoginPasswordAction(task(abr)).
		  setLoginCode(param(abr, PARAM_LOGIN, String.class)).
		  setPasshash(param(abr, PARAM_PASSHASH, String.class))
		);

		complete(abr);
	}

	protected void updatePerson(ActionBuildRec abr)
	{
		//?: {target is not a login}
		checkTargetClass(abr, AuthLogin.class);

		AuthLogin login = target(abr, AuthLogin.class);

		//?: {login has person} update it
		if(login.getPerson() != null)
			xnest(abr, ActPerson.UPDATE, login.getPerson(),
			  PARAM_PERSON, param(abr, PARAM_PERSON)
			);
		//!: create new person
		else
		{
			Person p = new Person();

			//~: domain of the login
			p.setDomain(login.getDomain());

			//2: update the person
			xnest(abr, ActPerson.UPDATE, p,
			 PARAM_PERSON, param(abr, PARAM_PERSON)
			);

			//1: assign the person
			chain(abr).first(new AssignLoginPersonAction(task(abr)).setPerson(p));

			//0: save the person
			xnest(abr, ActPerson.SAVE, p);
		}

		complete(abr);
	}


	/* protected: actions build support */

	protected UnityType getUnityType()
	{
		return UnityTypes.unityType(
		  AuthLogin.class, Auth.TYPE_LOGIN);
	}


	/* login close action */

	public static class CloseAuthLoginAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public CloseAuthLoginAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public AuthLogin getResult()
		{
			return target(AuthLogin.class);
		}

		protected void   execute()
		  throws Throwable
		{
			AuthLogin login = target(AuthLogin.class);

			//~: set close time
			login.setCloseTime(new java.util.Date());

			//~: update the login code
			login.setCode(String.format(
			  "%s (closed at %s)", login.getCode(),
			  DU.datetime2str(login.getCloseTime())
			));
		}
	}


	/* set login password action */

	public static class ChangeLoginPasswordAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public ChangeLoginPasswordAction(ActionTask task)
		{
			super(task);
		}


		/* public: UpdateLoginPassword (bean) interface */

		public ChangeLoginPasswordAction setLoginCode(String loginCode)
		{
			this.loginCode = loginCode;
			return this;
		}

		public ChangeLoginPasswordAction setPasshash(String passhash)
		{
			this.passhash = passhash;
			return this;
		}


		/* public: Action interface */

		public AuthLogin getResult()
		{
			return target(AuthLogin.class);
		}

		protected void   execute()
		  throws Throwable
		{
			AuthLogin login = target(AuthLogin.class);

			//~: login code
			if(loginCode != null)
				login.setCode(loginCode);

			//~: password hash
			if(passhash != null)
				login.setPasshash(passhash);
		}


		/* protected: login & password */

		protected String loginCode;
		protected String passhash;
	}


	/* assign person action */

	public static class AssignLoginPersonAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public AssignLoginPersonAction(ActionTask task)
		{
			super(task);
		}


		/* public: AssignLoginPersonAction (bean) interface */

		public AssignLoginPersonAction setPerson(Person person)
		{
			this.person = person;
			return this;
		}


		/* public: Action interface */

		public AuthLogin getResult()
		{
			return target(AuthLogin.class);
		}

		protected void   execute()
		  throws Throwable
		{
			AuthLogin login = target(AuthLogin.class);

			//?: {login has person} illegal state
			if(login.getPerson() != null) throw EX.state(
			  "Auth Login [", login.getPrimaryKey(),
			  "] already has a Person assigned!"
			);

			login.setPerson(person);
			flush(session());
		}


		/* the person */

		private Person person;
	}
}