package com.tverts.secure;

/* standard Java classes */

import java.util.Collection;
import java.util.HashSet;

/* Java Servlet api */

import javax.servlet.http.HttpSession;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import com.tverts.servlet.RequestPoint;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core + auth + secure) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.ActAuthSession;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecKey;

/* com.tverts: secure */

import com.tverts.secure.session.SecSession;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.s2s;


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

	public static SecSession  secSession()
	{
		return EX.assertn( INSTANCE.getSecSession(),
		  "No Secure Session instance is bound to the current thread!"
		);
	}

	public static Long        domain()
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

	public static Long        login()
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

	public static Long        loginOrNull()
	{
		return (Long) secSession().attr(SecSession.ATTR_AUTH_LOGIN);
	}

	public static void        closeSecSession()
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

	public static Domain      loadDomain()
	{
		Domain d = bean(GetDomain.class).getDomain(domain());

		if(d == null) throw EX.state(
		  "Domain pkey [", domain(), "] doesn't exist!"
		);

		return d;
	}

	public static AuthSession loadAuthSession()
	{
		return EX.assertn(bean(GetAuthLogin.class).getAuthSession(
		  (String) secSession().attr(SecSession.ATTR_AUTH_SESSION)),

		  "Auth Session [", secSession().attr(SecSession.ATTR_AUTH_SESSION),
		  "] is not found in the database!"
		);
	}

	public static AuthLogin   loadLogin()
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

	public static boolean     isSystemDomain()
	{
		return SYSTEM_DOMAIN.equals(loadDomain().getCode());
	}


	private static final String SESSION_SYSTEM_ATTR =
	  SecPoint.class.getName() + ": system login: ";

	public static boolean     isSystemLogin()
	{
		//~: inspect the session cache
		HttpSession session = RequestPoint.sessionOrNull();
		if(session != null)
		{
			Long x = (Long) session.getAttribute(SESSION_SYSTEM_ATTR + domain());
			if(x != null) return x.equals(login());
		}

		//~: load the System login
		AuthLogin system = bean(GetAuthLogin.class).getSystemLogin(domain());

		//?: {has session} cache
		if((system != null) && (session != null))
			session.setAttribute(SESSION_SYSTEM_ATTR + domain(), system.getPrimaryKey());

		return (system != null) && system.getPrimaryKey().equals(login());
	}

	public static void        checkSystemDomain()
	{
		if(!isSystemDomain())
			throw EX.state("System Domain only!");
	}

	public static Domain      loadSystemDomain()
	{
		Domain d =  bean(GetDomain.class).getDomain(SYSTEM_DOMAIN);

		if(d == null) throw EX.state("No System Domain present!");
		return d;
	}

	public static Long        clientFirmKey()
	{
		//~ access session
		SecSession s = INSTANCE.getSecSession();
		if(s == null) return null;

		//?: {this person has firm}
		Long r = (Long) s.attr(SecSession.ATTR_CLIENT_FIRM);
		if(r != null) return r;

		//?: {searched the key}
		if(Boolean.TRUE.equals(s.attr(ATTR_CLIENT_FIRM_SEARCHED)))
			return null;

		//~: do search
		AuthLogin l = EX.assertn(loadLogin());
		if((l.getPerson() != null) && (l.getPerson().getFirm() != null))
			s.attr(SecSession.ATTR_CLIENT_FIRM,
			  r = l.getPerson().getFirm().getPrimaryKey());

		//~: mark as searched
		s.attr(ATTR_CLIENT_FIRM_SEARCHED, true);

		return r;
	}

	private static final String ATTR_CLIENT_FIRM_SEARCHED =
	  SecSession.ATTR_CLIENT_FIRM + " [Searched]";


	/* public: SecPoint (security checks) interface */

	/**
	 * Checks whether current user has access
	 * with the key named to the Domain.
	 */
	public static boolean     isSecure(String key)
	{
		return isSystemLogin() || bean(GetSecure.class).
		  isSecure(login(), domain(), SecKeys.secKey(key));
	}

	public static boolean     isSecure(Long target, String key)
	{
		return isSystemLogin() || bean(GetSecure.class).
		  isSecure(login(), target, SecKeys.secKey(key));
	}

	/**
	 * Returns true when at least one of the keys is
	 * allowed, and not simultaneously forbidden.
	 */
	public static boolean     isAnySecure(Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<SecKey>(keys.size());
		for(String key : keys) if((key = s2s(key)) != null)
			skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAnySecure(login(), domain(), skeys);
	}

	public static boolean     isAnySecure(Long target, Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<SecKey>(keys.size());
		for(String key : keys) if((key = s2s(key)) != null)
			skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAnySecure(login(), target, skeys);
	}

	/**
	 * Returns true when all the keys are allowed.
	 */
	public static boolean     isAllSecure(Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<SecKey>(keys.size());
		for(String key : keys) if((key = s2s(key)) != null)
			skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAllSecure(login(), domain(), skeys);
	}

	public static boolean     isAllSecure(Long target, Collection<String> keys)
	{
		if(isSystemLogin()) return true;

		HashSet<SecKey> skeys = new HashSet<SecKey>(keys.size());
		for(String key : keys) if((key = s2s(key)) != null)
			skeys.add(SecKeys.secKey(key));

		return bean(GetSecure.class).
		  isAllSecure(login(), target, skeys);
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