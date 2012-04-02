package com.tverts.genesis;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Spring framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: system tx */

import com.tverts.system.tx.TxPoint;

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
       implements GenesisSphereReference, Runnable
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
	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: nest the context
		ctx = nestContext(ctx);

		//~: generate in the own context
		try
		{
			logGenerateBefore(ctx);
			doGenDispTx(ctx);
			logGenerateSuccess(ctx);
		}
		catch(GenesisError e)
		{
			throw e;
		}
	}


	/* public: Runnable interface */

	/**
	 * Invokes all the genesis phases: condition
	 * check, generation and the cleanup.
	 *
	 * Any exception raised within the task is thrown
	 * out after the rollback task had been executed.
	 */
	public void run()
	{
		try
		{
			this.generate(null);
		}
		catch(GenesisError e)
		{
			throw new RuntimeException(String.format(
			  "Unhandled error occured in Genesis %s!", logsig()), e);
		}
	}


	/* public: GenesisSphereReference interface */

	public List<GenesisSphere> dereferObjects()
	{
		return Collections.singletonList(this);
	}


	/* public: GenesisSphere (bean) interface */

	public Long getSeed()
	{
		return seed;
	}

	public void setSeed(Long seed)
	{
		this.seed = seed;
	}


	/* protected: invocation protocol */

	protected GenCtx   nestContext(GenCtx outer)
	{
		if(outer != null)
			return outer.stack(this);

		GenCtxBase res = new GenCtxBase(this);

		//~: random seed
		if(getSeed() != null)
			res.setGen(getSeed());

		return res;
	}

	protected void     doGenDispTx(GenCtx ctx)
	  throws GenesisError
	{
		if(ctx.getOuter() == null)
			doGenTx(ctx);
		else
			doGen(ctx);
	}

	@Transactional
	protected void     doGenTx(GenCtx ctx)
	  throws GenesisError
	{
		//~: push default transaction context
		TxPoint.getInstance().setTxContext();

		try
		{
			doGen(ctx);
		}
		finally
		{
			//!: pop transaction context
			TxPoint.getInstance().setTxContext(null);
		}
	}
	
	protected void     doGen(GenCtx ctx)
	  throws GenesisError
	{
		//~: obtain genesis units
		List<Genesis> gens = reference.dereferObjects();
		if((gens == null) || gens.isEmpty()) return;

		//~: invoke the genesis units
		for(Genesis gen : gens) try
		{
			//!: clone the original (prototype) unit
			gen = gen.clone();

			//!: invoke generation
			logGenGenerateBefore(ctx, gen);
			gen.generate(ctx);
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


	/* private: genesis reference */

	private GenesisReference reference;


	/* private: sphere parameters */

	private Long seed;
}