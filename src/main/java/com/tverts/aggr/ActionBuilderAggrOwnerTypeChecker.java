package com.tverts.aggr;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderTypeChecker;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.aggr.AggrValue;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Works as the base class, but is for aggregated
 * values only. The class of the target must be
 * {@link AggrValue}. The type name of the value
 * may be tested by
 *
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   ActionBuilderAggrOwnerTypeChecker
       extends ActionBuilderTypeChecker
{
	/* public: ActionBuilderAggrOwnerTypeChecker (bean) interface */

	/**
	 * The Unity Type name of the aggregated value.
	 * This parameter is not obligatory, but used
	 * in the most cases.
	 */
	public String getAggrType()
	{
		return aggrType;
	}

	public void   setAggrType(String aggrType)
	{
		this.aggrType = s2s(aggrType);
	}


	/* protected: ActionBuilderTypeChecker (check) interface */

	protected boolean   isActionBuildPossible(ActionBuildRec abr)
	{
		return AggrValue.class.equals(targetClass(abr)) &&
		  super.isActionBuildPossible(abr);
	}

	protected boolean   isActionTargetMatch(ActionBuildRec abr)
	{
		//?: {has aggregated value' type to check}
		if(getAggrType() != null)
		{
			UnityType ut = findAggrValueUnityType(abr);

			//?: {has wrong type} not our case
			if((ut == null) || !getTypeName().equals(ut.getTypeName()))
				return false;
		}

		//~: continue check the owner...
		return super.isActionTargetMatch(abr);
	}

	protected boolean   isActionTargetClassMatch(ActionBuildRec abr)
	{
		UnityType ut = findTargetUnityType(abr);
		return (ut != null) && getTypeClass().equals(ut.getTypeClass());
	}

	protected UnityType findAggrValueUnityType(ActionBuildRec abr)
	{
		return super.findTargetUnityType(abr);
	}

	protected UnityType findTargetUnityType(ActionBuildRec abr)
	{
		Unity unity = target(abr, AggrValue.class).getOwner();
		return (unity == null)?(null):(unity.getUnityType());
	}


	/* private: checker parameters */

	private String aggrType;
}