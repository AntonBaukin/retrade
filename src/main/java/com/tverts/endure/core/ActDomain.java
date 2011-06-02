package com.tverts.endure.core;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderWithTxBase;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SavePrimaryIdentified;


/**
 * Domain processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActDomain extends ActionBuilderWithTxBase
{
	/* action types */

	/**
	 * Send task with this type to save the given domain
	 * instance (the target) and to create+save all the
	 * related instances.
	 */
	public static final ActionType SAVE   =
	  ActionType.SAVE;

	/**
	 * Used while the system is under development.
	 * Validates the existing (and saved) domain (the target)
	 * that it has all the surrounding instances needed.
	 * (Such as lock objects, aggregated values, and etc.)
	 */
	public static final ActionType ENSURE =
	  new ActionType("endure.core.Domain: ensure", Domain.class);


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
		{
			saveDomain(abr);
			return;
		}

		if(ENSURE.equals(actionType(abr)))
			ensureDomain(abr);
	}

	/* protected: action methods */

	protected void saveDomain(ActionBuildRec abr)
	{
		//?: {target is not a Domain}
		checkTargetClass(abr, Domain.class);

		//NOTE that the actions are added to chain here
		//     as to the stack.

		//~: save the domain
		chain(abr).first(new SavePrimaryIdentified(task(abr)));

		//~: set domain unity
		xnest(abr, ActUnity.CREATE, target(abr));
	}

	protected void ensureDomain(ActionBuildRec abr)
	{
		//?: {target is not a Domain}
		checkTargetClass(abr, Domain.class);

		//TODO ensure Domain complete

		complete(abr);
	}
}