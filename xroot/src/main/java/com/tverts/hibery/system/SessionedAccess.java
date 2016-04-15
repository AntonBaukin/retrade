package com.tverts.hibery.system;

/* standard Java classes */

import java.io.Serializable;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;


/**
 * This object access strategy checks whether
 * the instance cached is still in the session
 * of the {@link TxPoint}. If not so, the
 * instance is accesses again via the factory.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SessionedAccess<O>
       implements ObjectAccess<O>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public SessionedAccess(ObjectAccess<O> factory)
	{
		this(null, factory);
	}

	public SessionedAccess(O instance, ObjectAccess<O> factory)
	{
		if(factory == null) throw new IllegalArgumentException();
		this.instance = instance;
		this.factory  = factory;
	}



	/* public: ObjectAccess interface */

	public O accessObject()
	{
		return instance = ensureInstance(instance);
	}


	/* protected: support interface */

	protected O ensureInstance(O instance)
	{
		if(instance == null)
			return accessInstance();

		if(!TxPoint.txSession().contains(instance))
			return accessInstance();

		return instance;
	}

	protected O accessInstance()
	{
		return factory.accessObject();
	}


	/* protected: the access state */

	protected final ObjectAccess<O> factory;
	private transient O             instance;
}