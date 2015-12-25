package com.tverts.endure.core;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (core) */

import com.tverts.endure.ActionBuilderXRoot;


/**
 * Actions on Unified Attribute Types.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActAttrType extends ActionBuilderXRoot
{
	/* Action Types */

	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* Action Builder */

	public void buildAction(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, AttrType.class);

		//?: {ensure type present}
		if(ENSURE.equals(actionType(abr)))
		{
			AttrType type = target(abr, AttrType.class);

			//~: save the calculation
			chain(abr).first(new SaveNumericIdentified(task(abr)).
			  setPredicate(x -> null == bean(GetUnity.class).getAttrType(
			    type.getDomain().getPrimaryKey(),
			    type.getAttrType().getPrimaryKey(),
			    type.getName()
			)));

			complete(abr);
		}
	}
}