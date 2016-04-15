package com.tverts.retrade.domain.sells;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.ActPaymentBase;


/**
 * Actions builder for {@link SellsPay}s.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSellsPay extends ActPaymentBase
{
	/* protected: builder support */

	protected UnityType getPaymentType(ActionBuildRec abr)
	{
		return UnityTypes.unityType(SellsPay.class);
	}
}