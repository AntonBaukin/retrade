package com.tverts.genesis;

/**
 * This exception is raised while invoking
 * {@link Genesis} units. It allows to access
 * the cleanup tasks to invoke to rollback
 * the modifications generated.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class GenesisError extends Exception
{
	/* public: constructor */

	public GenesisError (
	         Throwable cause,
	         Genesis   genesis,
	         Runnable  rollbackTask
	       )
	{
		super(cause);
		this.genesis = genesis;
		this.rollbackTask = rollbackTask;
	}

	/* public: GenesisError interface */

	public Genesis  getGenesis()
	{
		return genesis;
	}

	public Runnable getRollbackTask()
	{
		return rollbackTask;
	}

	/* private: rollback task */

	private Genesis  genesis;
	private Runnable rollbackTask;
}