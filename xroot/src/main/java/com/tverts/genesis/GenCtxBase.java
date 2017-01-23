package com.tverts.genesis;

/* standard Java classes */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Straight implementation of {@link GenCtx}.
 *
 * @author anton.baukin@gmail.com
 */
public class GenCtxBase implements GenCtx
{
	/* public: constructor */

	/**
	 * Creates root genesis context.
	 * Installed by Genesis Service.
	 */
	public GenCtxBase()
	{}

	public GenCtxBase(Genesis owner)
	{
		this(owner, null);
	}

	public GenCtxBase(Genesis owner, GenCtx outer)
	{
		this.owner = owner;
		this.outer = outer;
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
		return (gen != null)?(gen):(gen = createGen(null));
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

		//~: create parameters to share
		if(params == null)
			params = new HashMap(17);

		//~: create exported to share
		if(exported == null)
			exported = new HashSet(3);

		//~: share the parameters & exported
		res.params   = params;
		res.exported = exported;

		//~: log
		res.log = log;

		return res;
	}

	public Set     params()
	{
		return (params != null)?(params.keySet()):
		  Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	public Set     exported()
	{
		return (exported != null)?(exported):
		  (exported = new HashSet());
	}

	public Object  get(Object p)
	{
		return (params == null)?(null):(params.get(p));
	}

	@SuppressWarnings("unchecked")
	public <T> T   get(Object p, Class<T> cls)
	{
		Object v = get(p);

		if(v == null) throw new IllegalStateException(
		  "Parameter [" + p + "] is not defined!");

		if(!cls.isAssignableFrom(v.getClass()))
			throw new IllegalStateException(
		  "Parameter [" + p + "] is not a " + cls.getName() + "!");
		
		return (T) v;
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

	@SuppressWarnings("unchecked")
	public <T> T   get(Class<T> cls)
	{
		if(cls == null)
			throw new IllegalArgumentException();
		if(params == null)
			return null;

		Object res = params.get(cls);
			if((res != null) && !cls.isAssignableFrom(res.getClass()))
			throw new IllegalStateException();

		return (T) res;
	}

	@SuppressWarnings("unchecked")
	public <T> T   set(T obj)
	{
		if(obj == null)
			throw new IllegalArgumentException();

		Class cls = HiberSystem.getInstance().findActualClass(obj);
		return (T) this.set(cls, obj);
	}

	@SuppressWarnings("unchecked")
	public void    export(Object key)
	{
		if(exported == null)
			exported = new HashSet(3);
		exported.add(key);
	}

	public String  log()
	{
		if(log == null)
			log = GenesisPoint.LOG_GENESIS;
		return log;
	}

	public Session session()
	{
		Session res = (session != null)?(session):
		  (outer == null)?(null):(outer.session());

		if(res == null)
			res = txSession();
		return res;
	}


	/* public: GenCtxBase interface */
	
	public GenCtxBase setGen(Random gen)
	{
		this.gen = gen;
		return this;
	}

	public GenCtxBase setGen(Long seed)
	{
		this.gen = createGen(seed);
		return this;
	}

	public GenCtxBase setLog(String log)
	{
		this.log = log;
		return this;
	}

	public GenCtxBase setSession(Session session)
	{
		this.session = session;
		return this;
	}


	/* protected: support */

	protected Random  createGen(Long seed)
	{
		if(seed == null)
			seed = System.currentTimeMillis();

		LU.I(log(), "using seed = ", seed);
		return new Random(seed);
	}


	/* private: context state */

	private Genesis owner;
	private GenCtx  outer;
	private Random  gen;
	private Map     params;
	private Set     exported;
	private String  log;
	private Session session;
}