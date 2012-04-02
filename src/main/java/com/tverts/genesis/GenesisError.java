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

	public GenesisError(Throwable cause, Genesis genesis)
	{
		this(null, cause, genesis);
	}

	public GenesisError(String msg, Throwable cause, Genesis genesis)
	{
		super(msg, cause);
		this.genesis  = genesis;
	}


	/* public: GenesisError interface */

	public Genesis  getGenesis()
	{
		return genesis;
	}


	/* private: the Genesis unit reference */

	private Genesis genesis;
}