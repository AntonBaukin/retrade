package com.tverts.endure.core;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.endure.ActionBuilderXRoot;


/**
 * {@link Domain} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActDomain extends ActionBuilderXRoot
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
	 * Validates the existing (and saved) domain (the target)
	 * that it has all the surrounding instances needed.
	 * (Such as lock objects, aggregated values, and etc.)
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveDomain(abr);

		if(ENSURE.equals(actionType(abr)))
			ensureDomain(abr);
	}


	/* protected: action methods */

	protected void saveDomain(ActionBuildRec abr)
	{
		//?: {target is not a Domain}
		checkTargetClass(abr, Domain.class);

		//NOTE that the actions are added to chain here
		//     as to a stack.

		//~: create domain's areal (is executed last!)
		xnest(abr, ActDomain.ENSURE, target(abr));

		//~: save the domain
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set domain unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void ensureDomain(ActionBuildRec abr)
	{
		//?: {target is not a Domain}
		checkTargetClass(abr, Domain.class);

		//~: ensure domain locks
		ensureDomainLocks(abr);

		complete(abr);
	}

	protected void ensureDomainLocks(ActionBuildRec abr)
	{
		//~: goods & stores lock
		ensureLock(abr, Domains.LOCK_XGOODS);
	}
}