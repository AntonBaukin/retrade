package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

/* com.tverts: support */

import com.tverts.support.LO;
import com.tverts.support.LU;

/**
 * Sphere is a collection of {@link Genesis} instances,
 * a {@link Runnable} and a {@link Genesis} instance itself.
 *
 * Sphere defines the scope of genesis units. Within this
 * scope the units may accumulate (generate) the data not
 * deleting them, then invoke the self shunts or do else
 * things, and after that delete the data at once.
 *
 * @author anton.baukin@gmail.com
 */
public class      GenesisSphere
       extends    GenesisBase
       implements Genesis, Runnable
{
	/* public: constructor */

	protected GenesisSphere(GenesisReference reference)
	{
		this.reference = reference;
		this.setCondition(createPredicate());
	}

	/* public: Genesis interface */

	/**
	 * Invokes all the {@link Genesis} units accessed by the
	 * {@link GenesisReference} given.
	 *
	 * Note that this call collects the cleanup tasks in the
	 * opposite order: the cleanup of the last unit invoked
	 * would be called the first.
	 */
	public Runnable  generate()
	  throws GenesisError
	{
		List<Genesis>  gens  =
		  (reference == null)?(null):(reference.dereferObjects());

		//?: {has no genesis units}
		if((gens == null) || gens.isEmpty())
			return null;

		List<Runnable> tasks = new ArrayList<Runnable>(gens.size());
		Runnable       task;
		Predicate      cond;

		//~: invoke the genesis units
		for(Genesis gen : gens) try
		{
			cond = gen.getCondition();

			//?: {Genesis condition failed} skip this unit
			if((cond != null) && !cond.evalPredicate(gen))
			{
				logGenConditionFailed(gen);
				continue;
			}

			logGenGenerateBefore(gen);
			task = gen.generate();
			logGenGenerateSuccess(gen);

			if(task != null)
				tasks.add(task);
		}
		catch(GenesisError e)
		{
			//?: {has rollback task} add it
			if(e.getRollbackTask() != null)
				tasks.add(e.getRollbackTask());

			logGenGenerateError(e);
			throw new GenesisError(e.getCause(), gen,
			  createCleanups(tasks));
		}
		catch(Throwable e)
		{
			logGenGenerateError(gen, e);
			throw new GenesisError(e, gen,
			  createCleanups(tasks));
		}

		return createCleanups(tasks);
	}

	/* public: Runnable interface */

	/**
	 * Invokes all the genesis phases: condition
	 * check, generation and the cleanup.
	 *
	 * Any exception raised within the task is thrown
	 * out after the rollback task had been executed.
	 */
	public void      run()
	  throws RuntimeException
	{
		//?: {the condition is false} exit now
		if(!isAllowed())
		{
			logRunNotAllowed();
			return;
		}

		Runnable  cleanup = null;
		Throwable error   = null;

		//~: invoke the generation
		try
		{
			logRunGenerateBefore();
			cleanup = this.generate();
			logRunGenerateSuccess();
		}
		catch(GenesisError e)
		{
			cleanup = e.getRollbackTask();
			error   = e.getCause();

			logRunGenerateError(e);
		}
		catch(Throwable e)
		{
			error = e;
			logRunGenerateError(e);
		}

		//?: {has cleanup task} invoke it
		if(cleanup != null) try
		{
			logRunCleanupBefore(cleanup);
			cleanup.run();
			logRunCleanupSuccess();
		}
		catch(Throwable e)
		{
			if(error == null)
				error = e;

			logRunCleanupError(e);
		}

		//?: {has an error} throw it out
		if(error != null)
			throw new RuntimeException(String.format(
			  "unhandled error occured while invoking %s!",
			  logsig()), error);
	}

	/* protected: cleanup composite */

	/**
	 * Creates the {@link Runnable} tasks that invokes all
	 * the tasks of the list provided in the reversed order.
	 *
	 * While invoking delegates the exception handling to
	 * {@link #handleCleanupInvokeError(Runnable, Throwable)}.
	 */
	protected Runnable  createCleanups(List<Runnable> tasks)
	{
		return ((tasks == null) || tasks.isEmpty())?(null):
		  (new CleanupsInvoker(tasks));
	}

	/**
	 * Processes the cleanup error and tells whether
	 * to continue the cleanup {@code true}, or to
	 * break the cleanups invocation cycle.
	 */
	protected boolean   handleCleanupInvokeError
	  (Runnable task, Throwable e)
	{
		logCleanupInvokeError(task, e);
		return true;
	}

	protected class CleanupsInvoker implements Runnable
	{
		/* public: constructor */

		public CleanupsInvoker(List<Runnable> tasks)
		{
			this.tasks = tasks.toArray(new Runnable[tasks.size()]);
		}

		/* public: Runnable interface */

		public void run()
		{
			for(Runnable task : tasks) try
			{
				task.run();
			}
			catch(Throwable e)
			{
				try
				{
					//?: {do break the cleanup cycle}
					if(!handleCleanupInvokeError(task, e))
						break;
				}
				catch(Throwable ee)
				{
					//!: ignore this error

					logCleanupInvokeError(e);
				}
			}
		}

		/* protected: tasks iterator */

		protected final Runnable[] tasks;
	}

	/* protected: or predicate */

	protected Predicate createPredicate()
	{
		return new OrPredicate();
	}

	protected class OrPredicate implements Predicate
	{
		/* public: Predicate interface */

		public boolean evalPredicate(Object ctx)
		{
			List<Genesis> gens = (reference == null)?(null):
			  (reference.dereferObjects());

			//?: {has no genesis units}
			if((gens == null) || gens.isEmpty())
				return false;

			//~: ask the unit's conditions
			for(Genesis gen : gens)
			{
				Predicate cond = gen.getCondition();

				if((cond == null) || cond.evalPredicate(gen))
					return true;
			}

			return false;
		}
	}

	/* protected: logging */

	protected String logsig(String lang)
	{
		//?: {has the default name} return plain variant
		if(getClass().getSimpleName().equalsIgnoreCase(getName()))
			return (LO.LANG_RU.equals(lang))?
			  ("Сфера генезиса"):("Genesis Sphere");

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

	protected void   logRunNotAllowed()
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(), " [run]: ",
		  "the Predicate DID NOT allow to run this genesis unit!" );
	}

	protected void   logRunGenerateBefore()
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(), " [run]: ",
		  "is about to run generate procedure...");
	}

	protected void   logRunGenerateSuccess()
	{
		if(!LU.isI(getLog())) return;

		LU.T(getLog(), logsig(), " [run]: ",
		  "had successfully passed the generation!");
	}

	protected void   logRunGenerateError(GenesisError e)
	{
		LU.E(getLog(), e.getCause(), logsig(e.getGenesis()), " [run]: ",
		  "ERROR occured while generating! ");
	}

	protected void   logRunGenerateError(Throwable e)
	{
		LU.E(getLog(), e, logsig(), " [run]: ",
		  "ERROR occured while generating! ");
	}

	protected void   logRunCleanupBefore(Runnable cleanup)
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(), " [run]: ",
		  "is about to run the cleanup procedure [",
		  cleanup.getClass().getSimpleName(), "]...");
	}

	protected void   logRunCleanupSuccess()
	{
		if(!LU.isI(getLog())) return;

		LU.I(getLog(), logsig(), " [run]: ",
		     "cleanup succeed!");
	}

	protected void   logRunCleanupError(Throwable e)
	{
		LU.E(getLog(), e, logsig(), " [run]: ",
		  "ERROR occured while doing cleanup! ");
	}

	protected void   logCleanupInvokeError(Runnable task, Throwable e)
	{
		LU.E(getLog(), e, logsig(), " [run]: ",
		  "ERROR occured while invoking cleanup strategy of ",
		  "the aggregated Genesis Unit. The task is: ",
		  (task == null)?("UNDEFINED"):(task.getClass().getSimpleName()));
	}

	protected void   logCleanupInvokeError(Throwable e)
	{
		LU.E(getLog(), e, logsig(), " [run]: ",
		  "ERROR occured while invoking cleanup strategy of ",
		  "the aggregated Genesis Unit.");
	}

	protected void   logGenConditionFailed(Genesis g)
	{
		if(!LU.isI(getLog())) return;

		LU.T(getLog(), logsig(g), " [gen]: ",
		  "SKIP this genesis by condition.");
	}

	protected void   logGenGenerateBefore(Genesis g)
	{
		if(!LU.isT(getLog())) return;

		LU.T(getLog(), logsig(g), " [gen]: ",
		  "is about to run generate procedure...");
	}

	protected void   logGenGenerateSuccess(Genesis g)
	{
		if(!LU.isI(getLog())) return;

		LU.T(getLog(), logsig(g), " [gen]: ",
		  "had successfully PASSED the generation!");
	}

	protected void   logGenGenerateError(GenesisError e)
	{
		LU.E(getLog(), e.getCause(), logsig(e.getGenesis()), " [run]: ",
		  "ERROR occured while generating! ");
	}

	protected void   logGenGenerateError(Genesis g, Throwable e)
	{
		LU.E(getLog(), e.getCause(), logsig(g), " [run]: ",
		  "ERROR occured while generating! ");
	}

	/* protected: genesis reference */

	protected GenesisReference reference;
}