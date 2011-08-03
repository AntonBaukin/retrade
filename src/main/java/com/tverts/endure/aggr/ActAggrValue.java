package com.tverts.endure.aggr;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: spring */

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

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

/* com.tverts: support */

import static com.tverts.support.OU.assignable;


/**
 * This builder of actions is for create and save
 * aggregated value ({@link AggrValue) instances.
 *
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


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(CREATE.equals(actionType(abr)))
			createAggrItem(abr);
	}


	/* create aggregated value */

	protected void      createAggrItem(ActionBuildRec abr)
	{
		Predicate       p = createExistsPredicate(abr);
		DelayedInstance v = createAggrValue(abr);

		//~: save the value
		chain(abr).first(new SaveNumericIdentified(task(abr), v).
		  setPredicate(p));

		//~: set aggr value unity (is executed first!)
		xnest(abr, ActUnity.CREATE, v, PREDICATE, p,
		  ActUnity.UNITY_TYPE, getValueType(abr));

		complete(abr);
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

	protected DelayedInstance createAggrValue(ActionBuildRec abr)
	{
		return new CreateAggrValue(abr);
	}

	protected class CreateAggrValue implements DelayedInstance
	{
		/* public: constructor */

		public CreateAggrValue(ActionBuildRec abr)
		{
			this.abr = abr;
		}


		/* public: DelayedInstance interface */

		public NumericIdentity createInstance(Action action)
		{
			if(val != null) return val;

			Unity o = getOwner(abr);
			Long  s = getSelector(abr);

			val = new AggrValue();

			//~: assign the primary key of test instances
			if(o.getPrimaryKey() < 0L)
				setPrimaryKey(action.getContext().getActionTx(), val, true);

			//~: assign the owner
			val.setOwner(o);

			//~: assign the selector ID
			val.setSelectorID(s);

			return val;
		}


		/* protected: action build record */

		protected final ActionBuildRec abr;

		/* protected: the value created */

		protected AggrValue            val;
	}


	/* create aggregated value predicate */

	protected Predicate createExistsPredicate(ActionBuildRec abr)
	{
		return new IsValueExist(abr);
	}

	protected class IsValueExist implements Predicate
	{
		/* public: constructor */

		public IsValueExist(ActionBuildRec abr)
		{
			this.abr = abr;
		}

		/* public: Predicate interface */

		public boolean evalPredicate(Object ctx)
		{
			if(notexists != null) return notexists;

			Unity     o = getOwner(abr);
			UnityType t = getValueType(abr);
			Long      s = getSelector(abr);

			return notexists = (bean(GetAggrValue.class).getAggrValue(o, t, s) == null);
		}


		/* protected: action build record */

		protected final ActionBuildRec abr;


		/* protected: check state */

		protected Boolean              notexists;
	}
}