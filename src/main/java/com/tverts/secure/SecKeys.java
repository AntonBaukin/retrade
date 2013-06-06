package com.tverts.secure;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (secure) */

import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecKey;

/* com.tverts: support */

import static com.tverts.support.SU.sXe;


/**
 * Storage of security keys.
 * Implementation is thread-safe.
 *
 * @author anton.baukin@gmail.com
 */
public class SecKeys
{
	/* Singleton */

	public static final SecKeys INSTANCE =
	  new SecKeys();

	public static SecKeys getInstance()
	{
		return INSTANCE;
	}


	/* public: static interface */

	public static SecKey secKey(String name)
	{
		SecKey key = INSTANCE.key(name);

		if(key == null)
			throw new IllegalStateException(String.format(
			  "No Secure Key [%s] exists!", name));

		return key;
	}


	/* public: SecKeys interface */

	/**
	 * Returns Hibernate attached or detached
	 * Secure Key instance.
	 *
	 * If there is no key in the local storage,
	 * searches in the database if transaction
	 * is available.
	 */
	public SecKey key(String name)
	{
		if(sXe(name))
			throw new IllegalArgumentException();

		SecKey key = cache.get(name);
		if(key != null) return key;

		//?: {has no database access}
		if(TxPoint.getInstance().getTxContext() == null)
			return null;

		key = bean(GetSecure.class).getSecKey(name);
		if(key != null) cache.put(name, key);

		return key;
	}

	/**
	 * Adds the key primary key
	 * to the local cache.
	 */
	public void   cache(SecKey key)
	{
		if(key == null) throw new IllegalArgumentException();
		if(sXe(key.getName())) throw new IllegalArgumentException();
		if(key.getPrimaryKey() == null) throw new IllegalArgumentException();

		cache.put(key.getName(), key);
	}


	/* the cache */

	private Map<String, SecKey> cache =
	  Collections.synchronizedMap(new HashMap<String, SecKey>(101));
}