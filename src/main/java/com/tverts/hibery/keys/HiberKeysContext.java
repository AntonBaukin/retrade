package com.tverts.hibery.keys;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysContext;

/**
 * COMMENT HiberKeysContext
 *
 * @author anton.baukin@gmail.com
 */
public interface HiberKeysContext extends KeysContext
{
	/* public: HiberKeysContext interface */

	public Session getSession();
}