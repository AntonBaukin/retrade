package com.tverts.event;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;


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


	/* private: event state */

	private Object  target;
	private Map     params;
	private int     cycle;
	private boolean recycled;
}