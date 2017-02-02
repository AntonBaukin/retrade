package com.tverts.aggr.calc;

/* Hibernate Persistence Layer */

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import com.tverts.system.tx.Tx;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorBase.AggrStruct;
import com.tverts.endure.aggr.calc.AggrCalc;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTask;
import com.tverts.endure.aggr.AggrTaskBase;
import com.tverts.endure.aggr.AggrValue;

/* com.tverts: objects */

import com.tverts.objects.StringsReference;


/**
 * Implementation base for {@link AggrCalculator}.
 *
 * Note that the calculator is firmly attached to
 * the concrete implementation of the Aggregator.
 * It may support a subset of the aggregator tasks,
 * but may not error on the unsupported one.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrCalcBase
       implements     AggrCalculator, AggrCalcReference
{
	/* public: AggrCalcBase (bean) interface */

	public StringsReference getCalcTypes()
	{
		return calcTypes;
	}

	public void setCalcTypes(StringsReference calcTypes)
	{
		this.calcTypes = calcTypes;
	}


	/* public: AggrCalculator interface */

	public void calculate(AggrStruct struct)
	{
		if(isCalc(struct))
		{
			//~: do the calculations
			calc(struct);

			//~: touch the calculation instance
			touchCalc(struct);
		}
	}


	/* public: AggrCalcReference interface */

	public List<AggrCalculator> dereferObjects()
	{
		return Collections.<AggrCalculator> singletonList(this);
	}


	/* protected: calculations */

	protected abstract void calc(AggrStruct struct);

	protected boolean       isCalc(AggrStruct struct)
	{
		return calcTypesSet().contains(
		  aggrCalc(struct).getUnity().getUnityType().getTypeName());
	}

	protected Set<String>   calcTypesSet()
	{
		if(calcTypesSet != null)
			return calcTypesSet;

		if(getCalcTypes() == null)
			calcTypesSet = Collections.emptySet();
		else
		{
			List<CharSequence> strs = getCalcTypes().dereferObjects();
			Set<String>        set  = new HashSet<String>(strs.size());

			for(CharSequence str : strs)
				set.add(str.toString());
			calcTypesSet = set;
		}

		return calcTypesSet;
	}

	protected void          touchCalc(AggrStruct struct)
	{
		TxPoint.txn(tx(struct), struct.calc);
	}


	/* protected: access request instances */

	protected AggrValue aggrValue(AggrStruct struct)
	{
		return struct.job.aggrValue();
	}

	protected AggrTask  aggrTask(AggrStruct struct)
	{
		return struct.task;
	}

	protected AggrCalc  aggrCalc(AggrStruct struct)
	{
		if(struct.calc == null) throw new IllegalStateException();
		return struct.calc;
	}

	protected Object    param(AggrStruct struct, String name)
	{
		AggrTask task = aggrTask(struct);
		return (!(task instanceof AggrTaskBase))?(null):
		  ((AggrTaskBase)task).param(name);
	}

	protected <T> T     param(AggrStruct struct, String name, Class<T> c1ass)
	{
		AggrTask task = aggrTask(struct);
		return (!(task instanceof AggrTaskBase))?(null):
		  ((AggrTaskBase)task).param(name, c1ass);
	}


	/* protected: access transaction context & Hibernate session */

	protected Tx tx(AggrStruct struct)
	{
		Tx tx = struct.job.aggrTx();

		if(tx == null) throw new IllegalStateException(
		  "Aggregation Calculator is not bound to any Transactional Context!"
		);

		return tx;
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session   session(AggrStruct struct)
	{
		SessionFactory f = tx(struct).getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "Aggregation Calculator got undefined Hibernate session (or factroy)!");

		return s;
	}

	protected Query     Q(AggrStruct struct, String hql, Object... replaces)
	{
		return HiberPoint.query(session(struct), hql, replaces);
	}


	/* protected: logging */

	protected String    getLog()
	{
		return this.getClass().getName();
	}


	/* private: parameters of the strategy */

	private StringsReference     calcTypes;
	private volatile Set<String> calcTypesSet;
}