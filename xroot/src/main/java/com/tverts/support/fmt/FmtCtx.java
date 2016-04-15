package com.tverts.support.fmt;

/* standard Java classes  */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
		//?: {no flags}
		if(flags == null) return false;

		//?: {scalar flag}
		if(!(flag instanceof Object[]))
			return flags.contains(flag);

		//~: composite flag
		for(Object f : (Object[]) flag)
			if(!this.is(f))
				return false;

		return true;
	}

	/**
	 * Sets the flag.
	 */
	public FmtCtx     set(Object flag)
	{
		if(flags == null)
			flags = new ArrayList(4);

		//?: {this flag is already }
		int i = flags.indexOf(flag);
		if(i != -1) return this;
		flags.add(flag);

		//?: {composite flag}
		if(flag instanceof Object[])
			for(Object f : (Object[]) flag)
				this.set(f);

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
	private List   flags;
}