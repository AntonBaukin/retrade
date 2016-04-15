package com.tverts.retrade.domain.payment;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;


/**
 * Actions builder for {@link Settling}s.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSettling extends ActPaymentBase
{
	/* protected: builder support */

	protected UnityType getPaymentType(ActionBuildRec abr)
	{
		return UnityTypes.unityType(Settling.class);
	}

	/* protected: aggregation */

	protected void      aggrPayIts(ActionBuildRec abr)
	{
		super.aggrPayIts(abr);

		//~: aggregate on the contractor payment
		aggrPayIt(abr, target(abr, Settling.class).getPayFirm());
	}
}