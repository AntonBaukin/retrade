package com.tverts.hibery.keys;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;

/* com.tverts: endure keys */

import com.tverts.endure.keys.KeysContext;
import com.tverts.endure.keys.KeysGenerator;

/* com.tverts: hibery */

import org.hibernate.id.IdentifierGenerator;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;


/**
 * COMMENT HiberKeysGeneratorBridge
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
			session = txSession();

		if(!(session instanceof SessionImplementor))
			throw new IllegalStateException();

		return generator.generate(
		  (SessionImplementor)session, context.getSavedInstance());
	}

	/* private: hibernate generator */

	private IdentifierGenerator generator;
}