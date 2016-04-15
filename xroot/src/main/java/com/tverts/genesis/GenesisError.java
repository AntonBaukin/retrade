package com.tverts.genesis;

/**
 * This exception is raised while invoking
 * {@link Genesis} units. It allows to access
 * the cleanup tasks to invoke to rollback
 * the modifications generated.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisError extends Exception
{
	/* public: constructors */

	public GenesisError(Throwable cause, Genesis genesis, GenCtx ctx)
	{
		super(cause);

		this.genesis = genesis;
		this.ctx     = ctx;
	}

	public GenesisError(Genesis genesis, GenCtx ctx, Throwable cause)
	{
		super(cause);

		this.genesis = genesis;
		this.ctx     = ctx;
	}


	/* public: GenesisError interface */

	public Genesis  getGenesis()
	{
		return genesis;
	}

	public GenCtx   getCtx()
	{
		return ctx;
	}


	/* private: the Genesis unit reference */

	private transient Genesis genesis;
	private transient GenCtx  ctx;
}