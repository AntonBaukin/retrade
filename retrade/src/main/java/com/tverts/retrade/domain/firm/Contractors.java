package com.tverts.retrade.domain.firm;

/* Java */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* tverts.com: support */

import com.tverts.support.SU;


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
	  "ReTrade: Contractor";

	public static final String TYPE_FIRM               =
	  "Core: Firm";

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


	/* support */

	public static class CompareContractors
	       implements   Comparator<Contractor>
	{
		public static final CompareContractors INSTANCE =
		  new CompareContractors();


		/* Comparator */

		public int compare(Contractor l, Contractor r)
		{
			return SU.sXs(l.getNameProc()).
			  compareTo(SU.sXs(r.getNameProc()));
		}
	}

	public static void sort(List<Contractor> cs)
	{
		Collections.sort(cs, CompareContractors.INSTANCE);
	}
}