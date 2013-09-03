package com.tverts.genesis;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* Spring framework */

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.Param;

/* com.tverts: support */

import com.tverts.support.LO;
import com.tverts.support.LU;


/**
 * Sphere is a composite of {@link Genesis} instances,
 * a {@link Runnable} and a {@link Genesis} instance itself.
 *
 * Sphere creates own transactional context only when
 * it is not nested in outer composite. (The outer
 * genesis context is undefined.)
 *
 * Note that the genesis context of the sphere
 * is shared between all the invocations.
 *
 * @author anton.baukin@gmail.com
 */
public class      GenesisSphere
       extends    GenesisBase
       implements GenesisSphereReference
{
	/* public: constructor */

	public GenesisSphere(GenesisReference reference)
	{
		if(reference == null)
			throw new IllegalArgumentException();

		this.reference = reference;
	}


	/* public: Genesis interface */

	/**
	 * Invokes all the {@link Genesis} units accessed
	 * by the {@link GenesisReference} constructed with.
	 */
	public void          generate(GenCtx ctx)
	  throws GenesisError
	{
		//!: can't invoke on the prototype
		if(this.objects == null)
			throw new IllegalStateException(String.format(
			  "Can't invoke Genesis Sphere [%s] as it is a prototype!",
			  this.getName()
			));

		//~: nest the context
		ctx = nestContext(ctx);

		//~: generate in the own context
		logGenerateBefore(ctx);
		doGenDispTx(ctx);
		logGenerateSuccess(ctx);
	}

	public GenesisSphere clone()
	{
		GenesisSphere sphere = (GenesisSphere)super.clone();

		//~: clear the references for the clone
		sphere.reference = null;

		//~: aggregated Genesis items
		Genesis[] objects = this.objects;

		//?: {this is a prototype}
		if(this.reference != null)
		{
			List<Genesis> gens =
			  this.reference.dereferObjects();

			if(gens != null)
				objects = gens.toArray(new Genesis[gens.size()]);
		}

		if(objects == null)
			objects = new Genesis[0];

		//~: clone the items
		for(int i = 0;(i < objects.length);i++)
			objects[i] = objects[i].clone();

		//~: assign them to the result
		sphere.objects = objects;

		return sphere;
	}

	public void          parameters(List<ObjectParam> params)
	{
		//~: add this sphere own parameters
		super.parameters(params);

		List<Genesis> gens = null;

		//?: {this is a clone}
		if(this.objects != null)
			gens = Arrays.asList(this.objects);
		else if(this.reference != null)
			gens = this.reference.dereferObjects();

		//~: collect the parameters of the nested instances
		if(gens != null) for(Genesis g : gens)
			addNestedParameters(params, g);
	}


	/* public: GenesisSphereReference interface */

	public List<GenesisSphere> dereferObjects()
	{
		return Collections.singletonList(this);
	}


	/* public: GenesisSphere (bean) interface */

	@Param
	public Long    getSeed()
	{
		return seed;
	}

	public void    setSeed(Long seed)
	{
		this.seed = seed;
	}

	/**
	 * Tells this Genesis Sphere to create own
	 * transaction even if an external exists.
	 *
	 * By default is is true!
	 */
	@Param
	public boolean isOwnTx()
	{
		return ownTx;
	}

	public void    setOwnTx(boolean ownTx)
	{
		this.ownTx = ownTx;
	}


	/* protected: invocation protocol */

	protected GenCtx   nestContext(GenCtx outer)
	{
		GenCtx res = (outer != null)?(outer.stack(this)):(new GenCtxBase(this));

		//~: random seed
		if((getSeed() != null) && (res instanceof GenCtxBase))
			((GenCtxBase)res).setGen(getSeed());

		return res;
	}

	protected void     doGenDispTx(GenCtx ctx)
	  throws GenesisError
	{
		//?: {need to create own transaction}
		if((ctx.getOuter() == null) || isOwnTx())
			doGenTx(ctx);
		else
			doGen(ctx);
	}

	@Transactional(rollbackFor = Throwable.class,
	  propagation = Propagation.REQUIRES_NEW)
	protected void     doGenTx(GenCtx ctx)
	  throws GenesisError
	{
		//~: remember the external session
		Session session = ctx.session();

		//~: push default tx-context for this new transaction
		TxPoint.getInstance().setTxContext();

		try
		{
			//~: bind the session to the context
			if(ctx instanceof GenCtxBase)
				((GenCtxBase)ctx).setSession(TxPoint.txSession());

			//~: check Hibernate session
			checkCtxSession(ctx);

			//~: invoke the generation
			doGen(ctx);
		}
		finally
		{
			//!: pop transaction context
			TxPoint.getInstance().setTxContext(null);

			//~: restore the original session of the context
			if(ctx instanceof GenCtxBase)
				//?: {there was differ external session}
				if(session != ctx.session())
					((GenCtxBase)ctx).setSession(session);
				else
					((GenCtxBase)ctx).setSession(null);
		}
	}
	
	protected void     doGen(GenCtx ctx)
	  throws GenesisError
	{
		//~: invoke the genesis units
		for(Genesis gen : this.objects) try
		{
			//!: clone the original (prototype) unit
			gen = gen.clone();

			//!: invoke generation
			logGenGenerateBefore(ctx, gen);

			//~: generate
			gen.generate(ctx);

			//~: flush the session
			ctx.session().flush();

			logGenGenerateSuccess(ctx, gen);
		}
		catch(GenesisError e)
		{
			logGenGenerateError(e.getCtx(), e.getGenesis(), e.getCause());
			throw e;
		}
		catch(Throwable e)
		{
			logGenGenerateError(ctx, gen, e);
			throw new GenesisError(e, gen, ctx);
		}
	}

	protected void     checkCtxSession(GenCtx ctx)
	{
		if(ctx.session() == null)
			throw new IllegalStateException(
			  "Genesis Sphere got Context without Hibernate Session bount!");

		if(TxPoint.txSession() != ctx.session())
			throw new IllegalStateException(
			  "Genesis Sphere has two Hibernate Sessions messed " +
			  "in the Generation Context Vs globally accessed one!");
	}


	/* protected: logging */

	protected String logsig(String lang)
	{
		return String.format((LO.LANG_RU.equals(lang))?
		  ("Сфера генезиса '%s'"):("Genesis Sphere '%s'"),
		  getName());
	}

	protected String logsig(Genesis g)
	{
		if(g instanceof GenesisSphere)
			return ((GenesisSphere)g).logsig();

		if(g instanceof GenesisBase)
			return ((GenesisBase)g).logsig();

		return String.format("Genesis '%s'", g.getName());
	}


	/* protected: logging (Runnable) */

	protected void   logGenerateBefore(GenCtx ctx)
	{
		if(!LU.isT(log(ctx))) return;

		if(ctx.getOuter() == null)
			LU.T(log(ctx), logsig(), " opening sphere in own Tx...");
		else
			LU.T(log(ctx), logsig(), " opening sphere in outer Tx...");
	}

	protected void   logGenerateSuccess(GenCtx ctx)
	{
		if(!LU.isI(log(ctx))) return;

		LU.T(log(ctx), logsig(), " closed sphere with success!");
	}

	protected void   logGenGenerateBefore(GenCtx ctx, Genesis g)
	{
		if(!LU.isT(log(ctx))) return;

		LU.T(log(ctx), logsig(), " starting ", logsig(g));
	}

	protected void   logGenGenerateSuccess(GenCtx ctx, Genesis g)
	{
		if(!LU.isI(log(ctx))) return;

		LU.T(log(ctx), logsig(), " success on ", logsig(g), "!");
	}

	protected void   logGenGenerateError(GenCtx ctx, Genesis g, Throwable e)
	{
		LU.E(log(ctx), e, logsig(), " error with ", logsig(g), "!");
	}

	/* private: sphere parameters */

	private Long    seed;
	private boolean ownTx = true;

	/* private: genesis reference */

	//!: for the prototype only
	private GenesisReference reference;

	//!: for cloned sphere only
	private Genesis[]        objects;
}