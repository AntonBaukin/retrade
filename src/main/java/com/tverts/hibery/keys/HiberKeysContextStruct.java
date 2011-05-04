package com.tverts.hibery.keys;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysContextStruct;

public class      HiberKeysContextStruct
       extends    KeysContextStruct
       implements HiberKeysContext
{
	/* public: constructor */

	public HiberKeysContextStruct(Class keysClass)
	{
		super(keysClass);
	}

	/* public: HiberKeysContext interface */

	public Session getSession()
	{
		return session;
	}

	/* public: HiberKeysContextStruct interface */

	public HiberKeysContextStruct setSession(Session session)
	{

		this.session = session;
		return this;
	}

	public HiberKeysContextStruct setSavedInstance(Object instance)
	{
		super.setSavedInstance(instance);
		return this;
	}

	/* private: Hibernate session reference */

	private Session session;
}