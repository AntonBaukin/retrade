package com.tverts.secure.session;

/* Java */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* com.tverts: endure (authentication) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthSession;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.SU;


/**
 * Object that is saved in web session
 * to link it with the authentication login.
 *
 * @author anton.baukin@gmail.com
 */
public class SecSession implements Externalizable
{
	/* Attributes Names */

	/**
	 * Attribute of (String) primary key of the
	 * {@link AuthSession} related to this
	 * secured access session.
	 */
	public static String ATTR_AUTH_SESSION =
	  "SecSession: Auth Session";

	/**
	 * Attribute of (Long) primary key of the
	 * {@link AuthLogin} bound to this
	 * secured access session.
	 */
	public static String ATTR_AUTH_LOGIN   =
	  "SecSession: Auth Login";

	/**
	 * Primary key with the Firm Entity
	 * the user refers to. Note that only
	 * external clients do have firms
	 * defined, but not regular users.
	 */
	public static String ATTR_CLIENT_FIRM  =
	  "SecSession: Client Firm";

	/**
	 * Attribute of (Long) key of the Domain
	 * to log in. Without a domain login is impossible.
	 */
	public static String ATTR_DOMAIN_PKEY  =
	  "SecSession: Domain key";

	/**
	 * Java time (Long) this session will expire,
	 * if won't be touched.
	 */
	public static String ATTR_EXPIRE_TIME  =
	  "SecSession: expire time";

	/**
	 * Java time (Long) this session was touched.
	 */
	public static String ATTR_TOUCH_TIME   =
	  "SecSession: touch time";

	/**
	 * Optional parameter (Long) to Expire Strategy
	 * defining the maximum session life time
	 * in milliseconds.
	 */
	public static String ATTR_LIFE_TIME    =
	  "SecSession: life time";

	/**
	 * Tells that the session is manually closed.
	 */
	public static String ATTR_CLOSED       =
	  "SecSession: closed";


	/* public: constructor */

	public SecSession()
	{
		this.attrs = new ConcurrentHashMap<String, String>(7);
	}

	/**
	 * Creates Secure Session from the database record
	 * provided without expiration strategy check.
	 *
	 * This constructor is used for background processes,
	 * and secure expiration may cause a deny of service
	 * for delayed events and requests.
	 */
	public SecSession(AuthSession a)
	{
		this();

		//~: auth session key
		attr(ATTR_AUTH_SESSION, a.getSessionId());

		//~: login key
		attr(ATTR_AUTH_LOGIN, a.getLogin().getPrimaryKey().toString());

		//?: {has client firm}
		if(a.getLogin().getPerson() != null)
			if(a.getLogin().getPerson().getFirm() != null)
				attr(ATTR_CLIENT_FIRM,
				  a.getLogin().getPerson().getFirm().getPrimaryKey().toString());

		//~: domain key
		attr(ATTR_DOMAIN_PKEY, a.getLogin().getDomain().getPrimaryKey().toString());

		//~: touch time
		attr(ATTR_TOUCH_TIME, Long.toString(a.getAccessTime().getTime()));
	}


	/* Sec Session Attributes*/

	public String attr(String  key)
	{
		return (attrs == null)?(null):(attrs.get(key));
	}

	public String attr(String  key, String  val)
	{
		EX.asserts(key);
		return attrs.put(key, val);
	}

	protected final Map<String, String> attrs;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		//~: copy the attributes in a flat list
		ArrayList<String> kvs = new ArrayList<String>(2*attrs.size());
		for(String k : attrs.keySet())
		{
			String v = SU.s2s(attrs.get(k));
			if(v == null) continue;
			kvs.add(k); kvs.add(v);
		}

		//>: the number of records
		o.writeInt(kvs.size());

		//>: write all the lines
		for(String s : kvs)
			IO.str(o, s);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		//<: the number of records
		int s = i.readInt();
		EX.assertx(s%2 == 0);

		//~: add them to the map
		for(int j = 0;(j < s);j += 2)
		{
			String k = EX.asserts(IO.str(i));
			String v = IO.str(i);
			attrs.put(k, v);
		}
	}
}