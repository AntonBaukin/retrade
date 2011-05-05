package com.tverts.hibery.keys;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysContext;
import com.tverts.endure.keys.KeysGenerator;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import org.hibernate.id.IdentifierGenerator;

/**
 * TODO comment HiberKeysGeneratorBridge
 *
 * @author anton.baukin@gmail.com
 */
public final class HiberKeysGeneratorBridge
       implements  KeysGenerator
{
	/* public: constructor */

	public HiberKeysGeneratorBridge(IdentifierGenerator generator)
	{
		if(generator == null) throw new IllegalArgumentException();
		this.generator = generator;
	}

	/* public: KeysGenerator interface */

	public Object createPrimaryKey(KeysContext context)
	{
		Session session = null;

		if(context.getSavedInstance() == null)
			throw new IllegalArgumentException();

		if(context instanceof HiberKeysContext)
			session = ((HiberKeysContext)context).getSession();

		if(session == null)
			session = HiberPoint.session();

		if(!(session instanceof SessionImplementor))
			throw new IllegalStateException();

		return generator.generate(
		  (SessionImplementor)session, context.getSavedInstance());
	}

	/* private: hibernate generator */

	private IdentifierGenerator generator;
}