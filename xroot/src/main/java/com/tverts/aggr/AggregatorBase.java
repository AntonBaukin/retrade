package com.tverts.aggr;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

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
import com.tverts.support.SU;


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


	/* Aggregator */

	public void aggregate(AggrJob job)
	{
		AggrStruct struct;

		//?: {this aggregator can handle the job} invoke it
		if(isJobSupported(job))
			//?: {processed aggregation} mark the job complete
			if(aggregate(struct = createStruct(job)))
			{
				//~: touch the aggregated value instance
				touchAggrValue(struct);

				//~: update the owner of the aggregated value
				updateAggrOwner(struct);

				//!: mark the job as completed
				job.complete(true);
			}
	}


	/* Aggregator Base (bean) */

	public AggrCalcReference getCalculators()
	{
		return calculators;
	}

	private AggrCalcReference calculators;

	public void setCalculators(AggrCalcReference calculators)
	{
		this.calculators = calculators;
	}


	/* protected: aggregation */

	protected abstract void aggregateTask(AggrStruct struct)
	  throws Throwable;

	/**
	 * Aggregates the job running the tasks separately.
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
			//~: assign the error to the job
			struct.job.error(struct.task, EX.print(e));

			//~: raise aggregation error
			throw new AggrJobError(e, struct);
		}

		return true;
	}

	protected void          calculations(AggrStruct struct)
	{
		//~: obtain the calculations strategies
		List<AggrCalculator> acs =
		  (getCalculators() == null)?(null):
		  getCalculators().dereferObjects();

		//?: {there is no calculators}
		if((acs == null) || acs.isEmpty())
			return;

		//~: get the related calculated values
		if(struct.calcs == null)
			struct.calcs = bean(GetAggrValue.class).
			  getAggrCalcs(aggrValue(struct));

		//?: {there is no calculations}
		if(struct.calcs == null || struct.calcs.isEmpty())
			return;

		//~: invoke strategies on the related calculations
		for(AggrCalc calc : struct.calcs)
			for(AggrCalculator ac : acs)
				ac.calculate(struct.calc(calc));

		struct.calc(null); //<-- clear
	}

	protected void          touchAggrValue(AggrStruct struct)
	{
		TxPoint.txn(struct.job.aggrTx(), struct.job.aggrValue());
	}

	protected void          updateAggrOwner(AggrStruct struct)
	{}

	protected void          checkAggrJob(AggrStruct struct)
	{
		//?: {the aggregation value is not defined}
		EX.assertn(struct.job.aggrValue(),
		  "Aggregated Value is not defined in the job!");

		//?: {the transaction context is not defined}
		EX.assertn(struct.job.aggrTx(),
		  "Aggregation Tx Context is not defined!");
	}

	protected AggrStruct    createStruct(AggrJob job)
	{
		return new AggrStruct(job);
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
		  new HashSet<>(Arrays.asList(tasks)));
	}

	private Set<Class> supportedTasks = Collections.emptySet();

	protected Set<Class>    getSupportedTasks()
	{
		return this.supportedTasks;
	}


	/* Aggregation Structure */

	public static class AggrStruct
	{
		/* public: constructor */

		public AggrStruct(AggrJob aggrJob)
		{
			this.job = aggrJob;
		}

		public final AggrJob job;


		/* Aggregation Structure (assign) */

		public AggrTask task;

		/**
		 * Sets the new task. Clears all the objects
		 * related to the previous one.
		 */
		public AggrStruct     task(AggrTask task)
		{
			if(this.task == task)
				return this;

			//HINT: calculations relate to the value, not task

			this.task  = task;
			this.items = null;

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

		protected List<AggrItem> items;

		public AggrStruct     items(Collection<AggrItem> items)
		{
			this.items = (items == null)?(null):
			  (new ArrayList<>(items));

			return this;
		}

		public AggrStruct     items(AggrItem... items)
		{
			this.items = (items.length == 0)?(null):
			  (new ArrayList<>(Arrays.asList(items)));

			return this;
		}

		/**
		 * Assigns the calculation for temporary needs.
		 */
		public AggrStruct     calc(AggrCalc calc)
		{
			this.calc = calc;
			return this;
		}

		public AggrCalc calc;

		public List<AggrCalc> calcs;
	}


	/* protected: access tx-context & session */

	protected Tx      tx(AggrStruct struct)
	{
		return EX.assertn(struct.job.aggrTx(),
		  "Aggregator is not bound to any Transactional Context!");
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session session(AggrStruct struct)
	{
		return EX.assertn(
		  tx(struct).getSessionFactory().getCurrentSession(),
		  "Aggregator got undefined Hibernate session!");
	}


	/* protected: querying */

	protected Query Q(AggrStruct struct, String hql, Object... replaces)
	{
		return HiberPoint.query(session(struct), hql, replaces);
	}


	/* protected: helper functions */

	protected AggrValue aggrValue(AggrStruct struct)
	{
		return struct.job.aggrValue();
	}

	protected String    getLog()
	{
		return this.getClass().getName();
	}

	protected String    logsig()
	{
		return this.getClass().getSimpleName();
	}

	protected String    logsig(AggrStruct struct)
	{
		if(struct.job == null)
			return SU.cats(logsig(), " [aggr job undefined]");

		if(struct.job.aggrValue() == null)
			return SU.cats(logsig(), " [aggr value undefined] ",
			  logsig(struct.task));

		return SU.cats(logsig(), "[aggr value ",
		  struct.job.aggrValue().getPrimaryKey(),
		  "] ", logsig(struct.task));
	}

	protected String    logsig(AggrTask task)
	{
		if(task == null)
			return "[aggr task undefined]";

		return SU.cats(
		  "[aggr task ", task.getClass().getSimpleName(),
		  " for source ", (task.getSourceClass() == null)?
		  ("undefined"):(task.getSourceClass().getSimpleName()),
		  " [", task.getSource(), "]"
		);
	}
}