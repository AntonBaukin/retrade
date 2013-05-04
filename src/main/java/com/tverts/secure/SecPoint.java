package com.tverts.secure;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.ActAuthSession;
import com.tverts.endure.auth.AuthSession;
import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: secure */

import com.tverts.secure.session.SecSession;


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


	/* public: SecPoint (static) interface */

	public static SecSession secSession()
	{
		SecSession secs = INSTANCE.getSecSession();

		if(secs == null) throw new IllegalStateException(
		  "No Secure Session instance is bound to the current thread!");
		return secs;
	}

	public static Long       domain()
	{
		Long domain = (Long) secSession().
		  attr(SecSession.ATTR_DOMAIN_PKEY);

		if(domain == null) throw new IllegalStateException(String.format(
		  "Secure Session for Auth [%s] has no Domain key!",
		  (String) secSession().attr(SecSession.ATTR_AUTH_SESSION)
		));

		return domain;
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

		if(d == null) throw new IllegalStateException(String.format(
		  "Domain pkey [%d] doesn't exist!", domain()
		));

		return d;
	}

	/**
	 * Code of the System Domain.
	 */
	public static final String SYSTEM_DOMAIN = "System";

	public static boolean    isSystemDomain()
	{
		return SYSTEM_DOMAIN.equals(loadDomain().getCode());
	}

	public static void       checkSystemDomain()
	{
		if(!isSystemDomain())
			throw new IllegalStateException("System Domain only!");
	}

	public static Domain     loadSystemDomain()
	{
		Domain d =  bean(GetDomain.class).getDomain(SYSTEM_DOMAIN);

		if(d == null) throw new IllegalStateException(
		  "No System Domain present!");
		return d;
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