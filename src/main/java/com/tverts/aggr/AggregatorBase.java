package com.tverts.aggr;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionOrNullRun;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItem;
import com.tverts.endure.aggr.AggrTask;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;
import com.tverts.endure.aggr.calc.AggrCalc;

/* com.tverts: aggregation calculations  */

import com.tverts.aggr.calc.AggrCalcReference;
import com.tverts.aggr.calc.AggrCalculator;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Basic level of {@link Aggregator} implementation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggregatorBase
       implements     Aggregator, AggregatorReference
{
	/* public: AggregatorReference interface */

	public List<Aggregator> dereferObjects()
	{
		return Collections.<Aggregator> singletonList(this);
	}


	/* public: Aggregator interface */

	public void aggregate(AggrJob job)
	{
		AggrStruct struct;

		//?: {this aggregator can handle the job} invoke it
		if(isJobSupported(job))
			//?: {processed aggregation} mark the job complete
			if(aggregate(struct = createStruct(job)))
			{
				//~: update the owner of the aggregated value
				updateAggrOwner(struct);

				//!: mark the job as completed
				job.complete(true);
			}
	}


	/* public: AggregatorBase (bean) interface */

	public AggrCalcReference getCalculators()
	{
		return calculators;
	}

	public void setCalculators(AggrCalcReference calculators)
	{
		this.calculators = calculators;
	}


	/* protected: aggregation */

	protected abstract void aggregateTask(AggrStruct struct)
	  throws Throwable;

	/**
	 * Aggregates al the job running the tasks separately.
	 * Returns true if the task was completed.
	 */
	protected boolean       aggregate(AggrStruct struct)
	{
		//~: check the validity of the aggregation job
		checkAggrJob(struct);

		//~: run all the tasks separately
		for(int i = 0;(i < struct.job.size());i++) try
		{
			//?: {got empty task} skip it
			if(struct.job.task(i) == null)
				continue;

			//!: aggregate the task given
			aggregateTask(struct.task(struct.job.task(i)));

			//~: do the calculations
			calculations(struct);
		}
		catch(Throwable e)
		{
			Boolean r = handleTaskError(struct, e);

			//?: {the task is ordered to stop}
			if(r != null)
				return r;

			throw new AggrJobError(e, struct);
		}

		return true;
	}

	protected void          calculations(AggrStruct struct)
	{
		//~: obtain the calculations strategies
		List<AggrCalculator> acs = (getCalculators() == null)?(null):
		  getCalculators().dereferObjects();
		if((acs == null) || acs.isEmpty()) return;


		//~: get the related calculated values
		if(struct.calcs == null)
			struct.calcs = bean(GetAggrValue.class).
			  getAggrCalcs(aggrValue(struct));

		if(struct.calcs == null)
			struct.calcs = Collections.emptyList();
		if(struct.calcs.isEmpty()) return;


		//~: invoke strategies on the related calculations
		for(AggrCalc calc : struct.calcs)
			for(AggrCalculator ac : acs)
				ac.calculate(struct.calc(calc));
	}

	protected void          updateAggrOwner(AggrStruct struct)
	{
		actionOrNullRun(ActionType.REVIEW,
		  struct.job.aggrValue().getOwner(),
		  ActionType.REVIEWSRC, struct.job.aggrValue()
		);
	}

	protected void          checkAggrJob(AggrStruct struct)
	{
		//?: {the aggregation value is not defined}
		if(struct.job.aggrValue() == null)
			throw new IllegalStateException(
			  "Aggregated Value is not defined in the job!");

		//?: {the transaction context is not defined}
		if(struct.job.aggrTx() == null)
			throw new IllegalStateException(
			  "Aggregation Tx Context is not defined!");
	}

	protected AggrStruct    createStruct(AggrJob job)
	{
		return new AggrStruct(job);
	}

	/**
	 * Handles the error occurred while aggregating. If the result is
	 * undefined, the job processor will ignore it and continue with
	 * the next task.
	 *
	 * The defined result is returned as the result of the job processor
	 * {@link #aggregate(AggrStruct)}.
	 *
	 * Note that false result means the it is possible that some else
	 * aggregator will take this job from it's beginning task, and this
	 * aggregator must undo all the previous work.
	 */
	protected Boolean       handleTaskError(AggrStruct struct, Throwable e)
	{
		struct.job.error(struct.task, EX.print(e));
		return null;
	}

	protected boolean       isJobSupported(AggrJob job)
	{
		return getSupportedTasks().containsAll(job.tasksClasses());
	}

	/**
	 * Invoke this method from an aggregator constructor to define
	 * the set of aggregation tasks classes that aggregator supports.
	 */
	protected void          setSupportedTasks(Class... tasks)
	{
		this.supportedTasks = Collections.unmodifiableSet(
		  new HashSet<Class>(Arrays.asList(tasks)));
	}

	protected Set<Class>    getSupportedTasks()
	{
		return this.supportedTasks;
	}


	/* public: aggregation structure */

	public class AggrStruct
	{
		/* public: constructor */

		public AggrStruct(AggrJob aggrJob)
		{
			this.job = aggrJob;
		}


		/* public: assigners */

		public AggrTask       task()
		{
			return this.task;
		}

		/**
		 * Sets the new task. Clears all the objects
		 * related to the previous one.
		 */
		public AggrStruct     task(AggrTask task)
		{
			if(this.task == task) return this;

			this.task  = task;
			this.items = null;

			//HINT: calculations relate to the value, not task

			return this;
		}

		/**
		 * Returns the list of items affected by the task.
		 */
		public List<AggrItem> items()
		{
			return (this.items != null)?(items):
			   Collections.<AggrItem> emptyList();
		}

		public AggrStruct     items(Collection<AggrItem> items)
		{
			this.items = (items == null)?(null):
			  (new ArrayList<AggrItem>(items));

			return this;
		}

		public AggrStruct     items(AggrItem... items)
		{
			this.items = (items.length == 0)?(null):
			  (new ArrayList<AggrItem>(Arrays.asList(items)));

			return this;
		}

		public AggrCalc       calc()
		{
			return this.calc;
		}

		public AggrStruct     calc(AggrCalc calc)
		{
			this.calc = calc;
			return this;
		}


		/* public: structure fields */

		public final AggrJob  job;
		public AggrTask       task;
		public List<AggrItem> items;
		public AggrCalc       calc;
		public List<AggrCalc> calcs;
	}


	/* protected: access transaction context & Hibernate session */

	protected AggrTx  tx(AggrStruct struct)
	{
		AggrTx tx = struct.job.aggrTx();

		if(tx == null) throw new IllegalStateException(
		  "Aggregator is not bound to any Transactional Context!"
		);

		return tx;
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session session(AggrStruct struct)
	{
		SessionFactory f = tx(struct).getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "Aggregator got undefined Hibernate session (or factroy)!");

		return s;
	}


	/* protected: Hibernate querying */

	protected Query   Q(AggrStruct struct, String hql, Object... replaces)
	{
		return HiberPoint.query(session(struct), hql, replaces);
	}


	/* protected: helper functions */

	protected AggrValue aggrValue(AggrStruct struct)
	{
		return struct.job.aggrValue();
	}

	protected String    logsig()
	{
		return this.getClass().getSimpleName();
	}

	protected String    logsig(AggrStruct struct)
	{
		if(struct.job == null)
			return String.format("%s, Aggregation job undefined!", logsig());

		if(struct.job.aggrValue() == null)
			return String.format(
			  "%s, Aggregated Value undefined, %s.",
			  logsig(), logsig(struct.task));

		return String.format(
		  "%s, Aggregated Value [%d], %s",

		  logsig(), struct.job.aggrValue().getPrimaryKey(),
		  logsig(struct.task)
		);
	}

	protected String    logsig(AggrTask task)
	{
		if(task == null)
			return "Aggregation Task undefined";

		return String.format(
		  "Aggregation Task %s for source %s [%s]!",

		  task.getClass().getSimpleName(),

		  (task.getSourceClass() == null)?("Undefined")
		    :(task.getSourceClass().getSimpleName()),

		  (task.getSourceKey() == null)?("?"):
		    (task.getSourceKey().toString())
		);
	}


	/* private: aggregator support data */

	private Set<Class>        supportedTasks = Collections.emptySet();
	private AggrCalcReference calculators;
}