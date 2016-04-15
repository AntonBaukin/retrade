package com.tverts.endure.aggr.calc;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.DelayedInstance;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;


/**
 * This builder of actions is for create and save
 * calculations over the aggregated values.
 *
 * The value may already exist in the database and
 * passed as direct object link, or to be created
 * and saved in the current transaction and passed
 * as {@link DelayedInstance}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActAggrCalc extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType CREATE =
	  new ActionType("create-calc", AggrValue.class);


	/* parameters of the action task */

	/**
	 * This parameter is always required. It defines the
	 * name of the unity type of the calculation, or the type
	 * instance directly.
	 */
	public static final String CALC_TYPE  =
	  ActAggrCalc.class.getName() + ": aggr calc unity type";

	/**
	 * This parameter is required when the target is not
	 * the aggregated value. Assign loaded from the database
	 * {@link AggrValue} reference, or {@link DelayedInstance}
	 * creation strategy.
	 */
	public static final String AGGR_VALUE =
	  ActAggrCalc.class.getName() + ": aggr value";


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(CREATE.equals(actionType(abr)))
			createAggrCalc(abr);
	}


	/* create aggregated value */

	protected void      createAggrCalc(ActionBuildRec abr)
	{
		DelayedInstance c = createBuilder(abr);

		//~: save the value
		chain(abr).first(new SaveNumericIdentified(task(abr), c).
		  setPredicate(c));

		//~: set aggr value unity (is executed first!)
		xnest(abr, ActUnity.CREATE, c,
		  ActUnity.UNITY_TYPE, calcType(abr));

		complete(abr);
	}

	protected AggrValue aggrValue(ActionBuildRec abr)
	{
		AggrValue v = null;
		Object    p = param(abr, AGGR_VALUE);
		if(p == null) p = target(abr);

		if(p instanceof DelayedInstance)
			p = ((DelayedInstance)p).getInstance();
		if(p instanceof AggrValue)
			v = (AggrValue)p;

		if(v == null) throw new IllegalStateException(
		  "Action Builder for Aggregated Calculations requires valid " +
		  "Aggregation Value instance or DelayedInstance had created it!");

		return v;
	}

	protected UnityType calcType(ActionBuildRec abr)
	{
		Object p = param(abr, CALC_TYPE);

		if(p instanceof String)
			p = UnityTypes.unityType(AggrCalc.class, (String)p);

		if(!(p instanceof UnityType)) throw new IllegalStateException(
		  "Action Builder for Aggregated Calculations requires " +
		  "Unity Type of the calculation to create!");

		return (UnityType)p;
	}


	/* delayed creator of aggregation calculation */

	protected DelayedInstance createBuilder(ActionBuildRec abr)
	{
		return new CreateAggrCalc(abr);
	}

	public class CreateAggrCalc implements DelayedInstance
	{
		/* public: constructor */

		public CreateAggrCalc(ActionBuildRec abr)
		{
			this.abr = abr;
		}


		/* public: DelayedInstance interface */

		public AggrCalc createInstance(Action action)
		{
			//~: call predicate as side-effect of test loading
			evalPredicate(null);

			//?: {already created | loaded}
			if(calc != null)
				return calc;

			calc = new AggrCalc();

			//~: assign the primary key
			setPrimaryKey(action.getContext().getActionTx(), calc,
			  isTestInstance(aggrValue()));

			//~: assign the aggregated value
			calc.setAggrValue(aggrValue());

			return calc;
		}

		public AggrCalc getInstance()
		{
			return calc;
		}


		/* public: Predicate interface */

		public boolean  evalPredicate(Object ctx)
		{
			if(notexists != null) return notexists;

			this.calc = bean(GetAggrValue.class).
			  getAggrCalc(aggrValue(), calcType(abr));

			return this.notexists = (this.calc == null);
		}


		/* protected: support routines */

		protected AggrValue aggrValue()
		{
			return (val != null)?(val):
			  (val = ActAggrCalc.this.aggrValue(abr));
		}


		/* protected: action build record */

		protected final ActionBuildRec abr;


		/* protected: the value created */

		protected AggrValue val;
		protected AggrCalc  calc;
		protected Boolean   notexists;
	}
}