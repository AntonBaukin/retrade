package com.tverts.objects;

/* standard Java classes */

import java.io.Serializable;


/**
 * This special version of object access strategy
 * caches the instance accessed up to 256 milliseconds.
 * This allows not to ask for the instance in short periods.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      ObjectAccessTimedCache<O>
       implements ObjectAccess<O>, Serializable
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
		this.instance = instance;
		this.factory  = factory;
		updateTimestamp();
	}

	protected ObjectAccessTimedCache()
	{}


	/* public: ObjectAccess interface */

	public O                  accessObject()
	{
		checkTimeout();

		//?: {has no instance yet} access it
		if(instance != null)
			prolongateTimestamp();
		else
		{
			instance = getFactory().accessObject();
			updateTimestamp();
		}

		return instance;
	}


	/* public: ObjectAccessTimedCache interface */

	public ObjectAccess<O>    getFactory()
	{
		if(factory != null)
			return factory;

		if((factory = obtainFactory()) == null)
			throw new IllegalStateException(
			  "ObjectAccessTimedCache has access factory undefined!");

		return factory;
	}

	public long               getTimestamp()
	{
		return timestamp;
	}

	public void               updateTimestamp()
	{
		this.timestamp = timestamp();
	}

	public void               prolongateTimestamp()
	{
		this.timestamp = timestamp();
	}


	/* protected: extend points */

	protected void            checkTimeout()
	{
		if(timestamp != timestamp())
			this.instance = null;
	}

	protected O               getInstance()
	{
		return instance;
	}

	protected void            setInstance(O instance)
	{
		this.instance = instance;
	}

	protected ObjectAccess<O> obtainFactory()
	{
		return null;
	}

	protected long            timestamp()
	{
		return System.currentTimeMillis() & ~0xFFL;
	}


	/* private: the cache state */

	private transient O     instance;
	private ObjectAccess<O> factory;
	private long            timestamp;
}