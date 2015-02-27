package com.tverts.retrade.domain.firm;

/* Java */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core, persons) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.IncValues;
import com.tverts.endure.person.FirmEntity;

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
	/* Unity Types  */

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


	/* Contractors Routines */

	public static String getContractorName(Contractor c)
	{
		return (c == null)?(null):String.format(
		  "№%s, %s", c.getCode(), c.getName()
		);
	}

	public static String createContractorCode(Domain d)
	{
		return SU.cats("КО-", bean(IncValues.class).
		  txIncValue(d, contractorType(), "code", 1));
	}


	/* Unity Types Related */

	public static UnityType contractorType()
	{
		return UnityTypes.unityType(
		  Contractor.class, TYPE_CONTRACTOR);
	}

	public static UnityType firmType()
	{
		return UnityTypes.unityType(
		  FirmEntity.class, TYPE_FIRM);
	}

	public static UnityType aggrTypeContractorDebt()
	{
		return UnityTypes.unityType(
		  AggrValue.class, AGGRVAL_CONTRACTOR_DEBT);
	}


	/* Support */

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