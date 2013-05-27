package com.tverts.event;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

import com.tverts.endure.NumericIdentity;


/**
 * System lifecycle Event implementation base.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EventBase implements Event
{
	/* public: constructors */

	public EventBase(Object target)
	{
		this.target = target;
	}


	/* public: Event interface */

	public Object  target()
	{
		return this.target;
	}

	@SuppressWarnings("unchecked")
	public Object  get(Object key)
	{
		return (params == null)?(null):(params.get(key));
	}

	@SuppressWarnings("unchecked")
	public Object  put(Object key, Object val)
	{
		if(params == null)
			params = new HashMap(3);
		return params.put(key, val);
	}

	public int     cycle()
	{
		return this.cycle;
	}

	public void    recycle()
	{
		this.recycled = true;
	}

	public boolean isRecycled()
	{
		return this.recycled;
	}

	public void    commit()
	{
		if(isRecycled())
			cycle++;
		this.recycled = false;
	}

	public String  logText()
	{
		StringBuilder s = new StringBuilder(32);

		//~: event class name
		s.append('[').
		  append(this.getClass().getSimpleName()).
		//~: target class name
		  append("] on target [").
		  append(target().getClass().getSimpleName()).
		  append(']');

		//~: target primary key
		if(target() instanceof NumericIdentity)
			s.append(" pkey [").
			  append(((NumericIdentity)target()).getPrimaryKey()).
			  append(']');

		return s.toString();
	}


	/* private: event state */

	private Object  target;
	private Map     params;
	private int     cycle;
	private boolean recycled;
}