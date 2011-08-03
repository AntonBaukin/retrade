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
	protected void buildAggrValue (
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
}