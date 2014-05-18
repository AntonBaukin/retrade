package com.tverts.retrade.domain.firm;

/* standard Java classes */

import java.util.Arrays;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.core.ActUnity;
import com.tverts.endure.aggr.ActAggrValue;
import com.tverts.endure.aggr.calc.AggrCalcs;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: system (tx) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.system.tx.SetTxAction;

/* com.tverts: retrade domain (core) */


/**
 * {@link Contractor} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActContractor extends ActionBuilderReTrade
{
	/* action types */

	/**
	 * Send task with this type to save the given contractor
	 * instance (the target) and to create+save all the
	 * related instances.
	 */
	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;

	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* parameters of the action task */

	/**
	 * If this Boolean flag set the firm reference
	 * of the contractor is saved.
	 */
	public static final String SAVE_FIRM  =
	  ActContractor.class.getName() + ": save firm";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveContractor(abr);

		if(UPDATE.equals(actionType(abr)))
			updateContractor(abr);

		if(ENSURE.equals(actionType(abr)))
			ensureContractor(abr);
	}


	/* protected: action methods */

	protected void saveContractor(ActionBuildRec abr)
	{
		//?: {target is not a Contractor}
		checkTargetClass(abr, Contractor.class);

		//~: ensure the contractor
		xnest(abr, ActContractor.ENSURE, target(abr));

		//~: save the firm
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set contractor' unity
		xnest(abr, ActUnity.CREATE, target(abr));

		//?: {save the firm by the flag}
		if(flag(abr, SAVE_FIRM))
			saveFirm(abr);

		complete(abr);
	}

	protected void saveFirm(ActionBuildRec abr)
	{
		Firm firm = target(abr, Contractor.class).getFirm();

		//?: {the firm does not exist}
		if(firm == null) return;

		//?: {it is a test contractor} create test key
		if(firm.getPrimaryKey() == null)
			setPrimaryKey(session(abr), firm, isTestTarget(abr));

		//!: nest the firm save action
		xnest(abr, ActFirm.SAVE, firm);
	}

	protected void updateContractor(ActionBuildRec abr)
	{
		//?: {target is not a Contractor}
		checkTargetClass(abr, Contractor.class);

		//?: {save the firm by the flag}
		if(flag(abr, SAVE_FIRM))
			saveFirm(abr);
		//?: {update the firm}
		else if(target(abr, Contractor.class).getFirm() != null)
			xnest(abr, ActFirm.UPDATE, target(abr, Contractor.class).getFirm());

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void ensureContractor(ActionBuildRec abr)
	{
		//?: {target is not a Contractor}
		checkTargetClass(abr, Contractor.class);

		//~: ensure the aggregated values
		ensureContractorAggrValues(abr);

		complete(abr);
	}

	protected void ensureContractorAggrValues(ActionBuildRec abr)
	{
		//~: ensure debt aggregated value
		buildAggrValue(abr, Contractors.AGGRVAL_CONTRACTOR_DEBT, null,
		  ActAggrValue.CALCS, Arrays.asList(
		    AggrCalcs.AGGR_CALC_VOL_MONTH,
		    AggrCalcs.AGGR_CALC_VOL_WEEK
		));
	}
}