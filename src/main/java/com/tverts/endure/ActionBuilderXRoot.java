package com.tverts.endure;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderWithTxBase;

/* com.tverts: ensure (core + aggr) */

import com.tverts.endure.aggr.ActAggrValue;
import com.tverts.endure.aggr.AggrValue;


/**
 * Extended action builder that provides additional
 * support for entities defined in XRoot module.
 *
 * Each module has it's own variant of such a builder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderXRoot
       extends        ActionBuilderWithTxBase
{
	/* parameters of the actions */

	/**
	 * Send this parameter to do aggregation synchronous.
	 *
	 * WARNING! Be careful with this feature! Synchronous
	 *   aggregation overpasses the aggregation services,
	 *   and is not blocked / synchronized. Also, the
	 *   module issuing this action must have proper
	 *   registered.
	 */
	public static final String SYNCH_AGGR =
	  ActionBuilderXRoot.class.getName() + ": synchronous aggregation";


	/* protected: aggregated values support */

	/**
	 * Creates the {@link AggrValue} of the given type
	 * for the target {@link United} instance.
	 *
	 * Note that the action created first checks whether
	 * the aggregation value of such a type already exists,
	 * and does not create one if it is so.
	 *
	 * Selector argument is optional.
	 *
	 * The owner of the aggregated value is the
	 * target of the build record. It must be
	 * {@link United} or {@link Unity} instance.
	 */
	protected void    buildAggrValue (
	                    ActionBuildRec  abr,
	                    String          aggrTypeName,
	                    NumericIdentity selector
	                  )
	{
		xnest(abr, ActAggrValue.CREATE, target(abr),

		  ActAggrValue.VALUE_TYPE, aggrTypeName,
		  ActAggrValue.SELECTOR,   selector
		);
	}

	/**
	 * Tells whether the aggregation request must be issued
	 * as synchronous. See {@link #SYNCH_AGGR} parameter,
	 * by default it is false.
	 *
	 * This parameter is checked recursively for the nested
	 * tasks. You do not need to pass is into the each
	 * nested task until you want to overwrite it.
	 */
	protected boolean isAggrSynch(ActionBuildRec abr)
	{
		return flagRecursive(abr, SYNCH_AGGR);
	}
}