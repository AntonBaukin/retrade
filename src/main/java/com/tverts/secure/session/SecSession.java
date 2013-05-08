package com.tverts.secure.session;

/* standard Java classes */

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: endure (authentication) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;


/**
 * Object that is saved in web session
 * to link it with the authentication login.
 *
 * @author anton.baukin@gmail.com
 */
public class SecSession implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* attributes names */

	/**
	 * Attribute of (String) primary key of the
	 * {@link AuthSession} related to this
	 * secured access session.
	 */
	public static String ATTR_AUTH_SESSION =
	  "SecSession : Auth Session";

	/**
	 * Attribute of (Long) primary key of the
	 * {@link AuthLogin} bound to this
	 * secured access session.
	 */
	public static String ATTR_AUTH_LOGIN   =
	  "SecSession : Auth Session";

	/**
	 * Attribute of (Long) key of the Domain
	 * to log in. Without a domain login is impossible.
	 */
	public static String ATTR_DOMAIN_PKEY  =
	  "SecSession : Domain key";

	/**
	 * Java time (Long) this session was created.
	 */
	public static String ATTR_CREATE_TIME  =
	  "SecSession : create time";

	/**
	 * Java time (Long) this session will expire,
	 * if won't be touched.
	 */
	public static String ATTR_EXPIRE_TIME  =
	  "SecSession : expire time";

	/**
	 * Java time (Long) this session was touched.
	 */
	public static String ATTR_TOUCH_TIME   =
	  "SecSession : touch time";

	/**
	 * Optional parameter (Long) to Expire Strategy
	 * defining the maximum session life time
	 * in milliseconds.
	 */
	public static String ATTR_LIFE_TIME    =
	  "SecSession : life time";

	/**
	 * Tells that the session is manually closed.
	 */
	public static String ATTR_CLOSED       =
	  "SecSession : closed";


	/* public: constructor */

	@SuppressWarnings("unchecked")
	public SecSession()
	{
		this.attrs = Collections.synchronizedMap(new HashMap());
	}


	/* public: Expire Strategy (attributes) interface */

	@SuppressWarnings("unchecked")
	public Serializable   attr(Serializable key)
	{
		return (Serializable) attrs.get(key);
	}

	@SuppressWarnings("unchecked")
	public Serializable   attr(Serializable key, Serializable val)
	{
		return (Serializable) attrs.put(key, val);
	}

	/**
	 * Returns copy of the keys of the session.
	 */
	@SuppressWarnings("unchecked")
	public Serializable[] attrs()
	{
		return (Serializable[]) attrs.keySet().
		  toArray(new Serializable[attrs.size()]);
	}


	/* public: Expire Strategy (strategies) interface */

	public ExpireStrategy getExpireStrategy()
	{
		return expireStrategy;
	}

	public SecSession     setExpireStrategy(ExpireStrategy s)
	{
		this.expireStrategy = s;
		return this;
	}


	/* authentication attributes */

	@SuppressWarnings("unchecked")
	private Map            attrs;


	/* authentication strategies */

	private ExpireStrategy expireStrategy;
}