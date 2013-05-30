package com.tverts.secure;

/* Java Servlet api */

import javax.servlet.http.HttpSession;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import com.tverts.endure.secure.GetSecure;
import com.tverts.servlet.RequestPoint;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.ActAuthSession;
import com.tverts.endure.auth.Auth;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: secure */

import com.tverts.secure.session.SecSession;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Access various security related objects
 * bound to the pending request, or web session.
 *
 * @author anton.baukin@gmail.com
 */
public final class SecPoint
{
	/* Singletone */

	public static final SecPoint INSTANCE =
	  new SecPoint();

	public static SecPoint getInstance()
	{
		return INSTANCE;
	}


	/* security keys */

	public static final String KEY_VIEW   = "view";
	public static final String KEY_EDIT   = "edit";
	public static final String KEY_DELETE = "delete";


	/* log destinations */

	public static final String LOG_SEC    =
	  "com.tverts.secure";


	/* public: SecPoint (static) interface */

	public static SecSession secSession()
	{
		SecSession secs = INSTANCE.getSecSession();

		if(secs == null) throw EX.state(
		  "No Secure Session instance is bound to the current thread!");
		return secs;
	}

	public static Long       domain()
	{
		Long domain = (Long) secSession().
		  attr(SecSession.ATTR_DOMAIN_PKEY);

		if(domain == null) throw EX.state(
		  "Secure Session for Auth [",
		  secSession().attr(SecSession.ATTR_AUTH_SESSION),
		  "] has no Domain key!"
		);

		return domain;
	}

	public static Long       login()
	{
		Long login = (Long) secSession().
		  attr(SecSession.ATTR_AUTH_LOGIN);

		if(login == null) throw EX.state(
		  "Secure Session for Auth [",
		  secSession().attr(SecSession.ATTR_AUTH_SESSION),
		  "] has no Login key!"
		);

		return login;
	}

	public static Long       loginOrNull()
	{
		return (Long) secSession().attr(SecSession.ATTR_AUTH_LOGIN);
	}

	public static void       closeSecSession()
	{
		//~: set the closed attribute
		secSession().attr(SecSession.ATTR_CLOSED, true);

		//~: execute Auth Login close action
		AuthSession session = bean(GetAuthLogin.class).
		  getAuthSession((String) secSession().attr(SecSession.ATTR_AUTH_SESSION));

		//?: {session exists and is not closed yet}
		if((session != null) && (session.getCloseTime() == null))
			actionRun(ActAuthSession.CLOSE, session);
	}

	public static Domain     loadDomain()
	{
		Domain d = bean(GetDomain.class).getDomain(domain());

		if(d == null) throw EX.state(
		  "Domain pkey [", domain(), "] doesn't exist!"
		);

		return d;
	}

	public static AuthLogin  loadLogin()
	{
		AuthLogin l = bean(GetAuthLogin.class).getLogin(login());

		if(l == null) throw EX.state(
		  "Auth Login pkey [", login(), "] doesn't exist!"
		);

		return l;
	}

	/**
	 * Code of the System Domain.
	 */
	public static final String SYSTEM_DOMAIN = "System";

	public static boolean    isSystemDomain()
	{
		return SYSTEM_DOMAIN.equals(loadDomain().getCode());
	}


	private static final String SESSION_SYSTEM_ATTR =
	  SecPoint.class.getName() + ": system login";

	public static boolean    isSystemLogin()
	{
		//~: inspect the session cache
		HttpSession session = RequestPoint.sessionOrNull();
		if(session != null)
		{
			Boolean x = (Boolean) session.getAttribute(SESSION_SYSTEM_ATTR);
			if(x != null) return x;
		}

		//~: check the System name
		boolean res = Auth.SYSTEM_USER.equals(loadLogin().getCode());;

		//?: {has session} cache
		if(session != null)
			session.setAttribute(SESSION_SYSTEM_ATTR, res);

		return res;
	}

	public static void       checkSystemDomain()
	{
		if(!isSystemDomain())
			throw EX.state("System Domain only!");
	}

	public static Domain     loadSystemDomain()
	{
		Domain d =  bean(GetDomain.class).getDomain(SYSTEM_DOMAIN);

		if(d == null) throw EX.state("No System Domain present!");
		return d;
	}


	/* public: SecPoint (security checks) interface */

	/**
	 * Checks whether current user has access
	 * with the key named to the Domain.
	 */
	public static boolean    isSecure(String key)
	{
		return isSystemLogin() || bean(GetSecure.class).
		  isSecure(login(), domain(), SecKeys.secKey(key));
	}


	/* public: SecPoint (access) interface */

	public SecSession getSecSession()
	{
		return session.get();
	}

	public void       setSecSession(SecSession secs)
	{
		if(secs != null)
			this.session.set(secs);
		else
			this.session.remove();
	}


	/* private: thread local state */

	private ThreadLocal<SecSession> session =
	  new ThreadLocal<SecSession>();
}