package com.tverts.support.fmt;

/* standard Java classes  */

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Universal formatting context.
 *
 * @author anton.baukin@gmail.com
 */
@SuppressWarnings("unchecked")
public class FmtCtx
{
	/* public: FmtCtx interface */

	public Object     obj()
	{
		return object;
	}

	public FmtCtx     obj(Object obj)
	{
		this.object = obj;
		return this;
	}

	public Object     get(Object key)
	{
		return (params == null)?(null):(params.get(key));
	}

	public FmtCtx     put(Object key, Object val)
	{
		if(params == null)
			params = new HashMap(3);

		if(val == null)
			params.remove(key);
		else
			params.put(key, val);

		return this;
	}

	public boolean    is(Object flag)
	{
		return (flags == null)?(false):(flags.contains(flag));
	}

	/**
	 * Sets the flag.
	 */
	public FmtCtx     set(Object flag)
	{
		if(flags == null)
			flags = new HashSet(4);

		//?: {composite flag}
		if(flag instanceof Object[])
		{
			for(Object f : (Object[]) flag)
				this.set(f);
			return this;
		}

		flags.add(flag);
		return this;
	}

	/**
	 * Clears the flag.
	 */
	public FmtCtx     clear(Object flag)
	{
		if(flags != null)
			flags.remove(flag);
		return this;
	}

	public Collection keys()
	{
		return (params != null)?(params.keySet()):
		  Collections.emptySet();
	}

	public Collection flags()
	{
		return (flags != null)?(flags):
		  Collections.emptySet();
	}


	/* private: context state */

	private Object object;
	private Map    params;
	private Set flags;
}