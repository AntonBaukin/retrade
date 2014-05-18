package com.tverts.retrade.domain.firm;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;


/**
 * Collects some constants and static routines
 * for dealing with {@link Contractor}s.
 *
 * @author anton.baukin@gmail.com
 */
public class Contractors
{
	/* unity types  */

	public static final String TYPE_CONTRACTOR         =
	  "ReTrade: Firms: Contractor";

	public static final String TYPE_FIRM               =
	  "ReTrade: Firms: Firm";

	/**
	 * Aggregation value of summary contractor debt.
	 *
	 * Positive value is the amount of money the
	 * contractor must pay. Negative is the amount
	 * to pay to the contractor.
	 *
	 * This value is attached to {@link Contractor}.
	 */
	public static final String AGGRVAL_CONTRACTOR_DEBT =
	  "ReTrade: Aggr Value: Contractor Debt";


	/* contractors routines */

	public static String getContractorName(Contractor c)
	{
		return (c == null)?(null):String.format(
		  "№%s, %s", c.getCode(), c.getName()
		);
	}


	/* public static: unity types related */

	public static UnityType aggrTypeContractorDebt()
	{
		return UnityTypes.unityType(
		  AggrValue.class, AGGRVAL_CONTRACTOR_DEBT);
	}
}