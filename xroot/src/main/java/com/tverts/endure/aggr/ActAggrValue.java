package com.tverts.endure.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: endure (aggregation calculations) */

import com.tverts.endure.aggr.calc.ActAggrCalc;
import com.tverts.endure.aggr.calc.AggrCalc;

/* com.tverts: support */

import static com.tverts.support.OU.assignable;


/**
 * This builder of actions is for create and save
 * aggregated value ({@link AggrValue) instances.
 *
 * @author anton.baukin@gmail.com
 */
public class ActAggrValue extends ActionBuilderXRoot
{
	/* action types */

	/**
	 * Send task with this type to create and save the
	 * aggregated value of the type defined by
	 * {@link #VALUE_TYPE} parameter.
	 *
	 * The target of the task must be a {@link United}
	 * or a {@link Unity} instance of the owner of
	 * the aggregated value.
	 *
	 * Note, that when executing, the actions do check
	 * whether there aggregated value with the same
	 * triple of owner, type, and optional selector
	 * already exists. If so, no duplicate instance
	 * is created!
	 */
	public static final ActionType CREATE =
	  new ActionType("create", AggrValue.class);


	/* parameters of the action task */

	/**
	 * This parameter is always required. It defines the
	 * name of the unity type of the value, or the type
	 * instance directly.
	 */
	public static final String VALUE_TYPE =
	  ActAggrValue.class.getName() + ": aggr value unity type";

	/**
	 * By default the owner of the aggregated value
	 * is being created is the target of the builder task.
	 *
	 * This parameter defines a {@link United} or a
	 * {@link Unity} explicitly and overwrites the default.
	 */
	public static final String OWNER      =
	  ActAggrValue.class.getName() + ": aggr value owner";

	/**
	 * This parameter defined optional selector
	 * {@link NumericIdentity} instance.
	 */
	public static final String SELECTOR   =
	  ActAggrValue.class.getName() + ": aggr value selector";

	/**
	 * Create Aggregation Calculations over this aggregated
	 * value sending this parameter. It is a single string,
	 * or an array, or a collection of strings with Unity
	 * Type names (class {@link AggrCalc}) of the calculations
	 * to create.
	 *
	 * The calculations are created on demand.
	 */
	public static final String CALCS      =
	  ActAggrValue.class.getName() + ": aggregation calculations";


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(CREATE.equals(actionType(abr)))
			createAggrValue(abr);
	}


	/* create aggregated value */

	protected void      createAggrValue(ActionBuildRec abr)
	{
		DelayedInstance v = createBuilder(abr);

		//~: create and save the calculations
		if(param(abr, CALCS) != null)
			createAggrCalcs(abr, v);

		//~: save the value
		chain(abr).first(new SaveNumericIdentified(task(abr), v).
		  setPredicate(v));

		//~: set aggr value unity (is executed first!)
		xnest(abr, ActUnity.CREATE, v,
		  ActUnity.UNITY_TYPE, getValueType(abr));

		complete(abr);
	}

	protected void      createAggrCalcs(ActionBuildRec abr, DelayedInstance v)
	{
		Object calcs = param(abr, CALCS);

		if(calcs instanceof Object[])
			calcs = Arrays.asList((Object[])calcs);
		else if(!(calcs instanceof Collection))
			calcs = Collections.singletonList(calcs);

		//~: delegate the creation
		for(Object calc : (Collection)calcs)
			xnest(abr, ActAggrCalc.CREATE, v, ActAggrCalc.CALC_TYPE, calc);
	}

	protected Unity     getOwner(ActionBuildRec abr)
	{
		//~: get the owner from the parameter
		Object owner = param(abr, OWNER);

		//?: {parameter is not defined} take the target
		if(owner == null)
			owner = targetOrNull(abr);

		//?: {given as a United}
		if(owner instanceof United)
			owner = ((United)owner).getUnity();

		//?: {not a unity} exception
		assignable(owner, Unity.class);

		return (Unity)owner;
	}

	protected Long      getSelector(ActionBuildRec abr)
	{
		//~: get the selector from the parameter
		Object selector = param(abr, SELECTOR);

		//?: {selector parameter does not exist}
		if(selector == null) return null;

		//~: assert it's type
		assignable(selector, NumericIdentity.class);

		return ((NumericIdentity)selector).getPrimaryKey();
	}

	protected UnityType getValueType(ActionBuildRec abr)
	{
		Object    ptype  = param(abr, VALUE_TYPE);
		UnityType result = null;

		//?: {the type is defined by the name}
		if(ptype instanceof String)
			result = UnityTypes.unityType(AggrValue.class, ptype.toString());

		//?: {the type is given directly}
		if(ptype instanceof UnityType)
			result = (UnityType)ptype;

		//!: return the unique type: this actually does not work
		//   as more than one type name is registered for AggValue.
		return (result != null)?(result):
		  (UnityTypes.unityType(AggrValue.class));
	}


	/* delayed creator of aggregated value */

	protected DelayedInstance createBuilder(ActionBuildRec abr)
	{
		return new CreateAggrValue(abr);
	}

	public class CreateAggrValue implements DelayedInstance
	{
		/* public: constructor */

		public CreateAggrValue(ActionBuildRec abr)
		{
			this.abr = abr;
		}


		/* public: DelayedInstance interface */

		public AggrValue createInstance(Action action)
		{
			//~: call predicate as side-effect of test loading
			evalPredicate(null);

			//?: {already created | loaded}
			if(val != null)
				return val;

			Unity o = getOwner(abr);
			Long  s = getSelector(abr);

			val = new AggrValue();

			//~: assign the primary key
			setPrimaryKey(action.getContext().getActionTx(), val,
			  isTestInstance(o));

			//~: assign the owner
			val.setOwner(o);

			//~: assign the selector key
			val.setSelectorKey(s);

			return val;
		}

		public AggrValue getInstance()
		{
			return val;
		}


		/* public: Predicate interface */

		public boolean evalPredicate(Object ctx)
		{
			if(notexists != null) return notexists;

			Long      o = getOwner(abr).getPrimaryKey();
			UnityType t = getValueType(abr);
			Long      s = getSelector(abr);

			this.val = bean(GetAggrValue.class).getAggrValue(o, t, s);
			return this.notexists = (this.val == null);
		}


		/* protected: action build record */

		protected final ActionBuildRec abr;


		/* protected: the value created */

		protected AggrValue val;
		protected Boolean   notexists;
	}
}