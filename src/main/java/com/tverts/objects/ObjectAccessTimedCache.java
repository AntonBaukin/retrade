package com.tverts.objects;

/* standard Java classes */

import java.io.Serializable;


/**
 * This special version of object access strategy
 * caches the instance accessed up to 250 milliseconds.
 * This allows not to ask for the instance in short periods.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class ObjectAccessTimedCache<O>
       implements  ObjectAccess<O>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public ObjectAccessTimedCache(ObjectAccess<O> factory)
	{
		if(factory == null) throw new IllegalArgumentException();
		this.factory = factory;
	}

	public ObjectAccessTimedCache(O instance, ObjectAccess<O> factory)
	{
		this.instance  = instance;
		this.factory   = factory;
		this.timestamp = timestamp();
	}


	/* public: ObjectAccess interface */

	public O                accessObject()
	{
		checkTimeout();

		//?: {has no instance yet} access it
		if(instance != null)
			this.timestamp = timestamp(); //<-- just prolong usage
		else if(factory != null)
		{
			instance = factory.accessObject();
			this.timestamp = timestamp();
		}
		else throw new IllegalStateException(
		  "ObjectAccessTimedCache has access factory undefined!");

		return instance;
	}


	/* public: ObjectAccessTimedCache interface */

	public ObjectAccess<O>  getFactory()
	{
		return factory;
	}

	public long             getTimestamp()
	{
		return timestamp;
	}

	public void             prolongate()
	{
		this.timestamp = timestamp();
	}


	/* private: simple things */

	private void            checkTimeout()
	{
		if(timestamp() - timestamp > 250L)
			this.instance = null;
	}

	private long            timestamp()
	{
		return System.currentTimeMillis();
	}


	/* private: the cache state */

	private transient O     instance;
	private ObjectAccess<O> factory;
	private long            timestamp;
}