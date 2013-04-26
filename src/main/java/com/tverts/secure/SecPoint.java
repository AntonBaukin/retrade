package com.tverts.secure;

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