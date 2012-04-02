package com.tverts.genesis;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * Straight implementation of {@link GenCtx}.
 *
 * @author anton.baukin@gmail.com
 */
public class GenCtxBase implements GenCtx, Cloneable
{
	/* public: constructor */

	public GenCtxBase(Genesis owner)
	{
		this(owner, null);
	}

	public GenCtxBase(Genesis owner, GenCtx outer)
	{
		this.owner = owner;
		this.outer   = outer;
	}


	/* public: GenCtx interface */

	public Genesis getOwner()
	{
		return owner;
	}

	public GenCtx  getOuter()
	{
		return outer;
	}

	public Random  gen()
	{
		return null;
	}

	public GenCtx  stack(Genesis owner)
	{
		GenCtxBase res;

		//~: clone the context
		try
		{
			res = (GenCtxBase)clone();

			//~: owner
			res.owner = owner;

			//~: outer
			res.outer = this;
		}
		catch(CloneNotSupportedException e)
		{
			res = new GenCtxBase(owner, this);
		}
		
		if(gen == null)    //<-- create it to share
			gen = new Random();

		//~: share gen
		res.gen = gen;

		if(params == null) //<-- create them to share
			params = new HashMap(7);

		//~: share the parameters
		res.params = params;

		//~: log
		res.log = log;

		return res;
	}

	public Set     params()
	{
		return (params != null)?(params.keySet()):
		  Collections.emptySet();
	}

	public Object  get(Object p)
	{
		return (params == null)?(null):(params.get(p));
	}

	@SuppressWarnings("unchecked")
	public Object  set(Object p, Object v)
	{
		if(params == null)
			params = new HashMap(7);
		if(v == null)
			return params.remove(p);
		return params.put(p, v);
	}

	public String  log()
	{
		if(log == null)
			log = Genesis.class.getName();
		return log;
	}


	/* public: GenCtxBase interface */
	
	public GenCtxBase setGen(Random gen)
	{
		this.gen = gen;
		return this;
	}

	public GenCtxBase setGen(Long seed)
	{
		this.gen = (seed == null)?(new Random()):(new Random(seed));
		return this;
	}

	public GenCtxBase setLog(String log)
	{
		this.log = log;
		return this;
	}


	/* private: context state */

	private Genesis owner;
	private GenCtx  outer;
	private Random  gen;
	private Map     params;
	private String  log;
}