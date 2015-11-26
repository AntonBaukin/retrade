package com.tverts.retrade.domain.goods;

/* Java */

import java.math.BigDecimal;
import java.math.RoundingMode;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Domains;
import com.tverts.endure.core.IncValues;

/* com.tverts: aggregation */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade api (goods) */

import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.Measure;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Collects some constants and static routines
 * for dealing with goods {@link GoodUnit}.
 *
 * @author anton.baukin@gmail.com
 */
public class Goods
{
	/* Types of Entities */

	public static final String TYPE_GOOD_UNIT    =
	  "ReTrade: Goods: Good Unit";

	public static final String TYPE_GOODS_TREE   =
	  "ReTrade: Goods: Tree";

	public static final String TYPE_GOODS_FOLDER =
	  "ReTrade: Goods: Tree Folder";

	/* Types of Locks */

	/**
	 * @see {@link Domains#LOCK_XGOODS}
	 */
	public static final String LOCK_XGOODS       =
	  Domains.LOCK_XGOODS;


	/* Types of Aggregated Values */

	/**
	 * Unity type of the Good Unit volume in a Trade Store.
	 * The owner of the Aggregated Value is s Store,
	 * selector is a Good Unit.
	 */
	public static final String AGGRVAL_STORE_VOLUME    =
	  "ReTrade: Aggr Value: Good Volume in Trade Store";

	/**
	 * Unity type of the rest cost of the Good Unit.
	 * Global for the Domain; has no selector.
	 */
	public static final String AGGRVAL_GOOD_REST_COST  =
	  "ReTrade: Aggr Value: Good Rest Cost";

	/**
	 * This special calculation provides feedback from
	 * fixed history items of aggregation to the related
	 * Volume Check Documents (implemented as Invoices).
	 */
	public static final String AGGR_CALC_VOL_CHECK     =
	  "Aggr Calc: Volume: Volume Check";


	/* Support Routines */

	public static String     getGoodUnitName(GoodUnit gu)
	{
		return (gu == null)?(null):(gu.getName());
	}

	public static String     getStoreFullName(TradeStore store)
	{
		if(store == null) return null;
		return String.format("%s; %s",
		  store.getCode(), store.getName());
	}

	public static String     genNextGoodCode(Domain domain)
	{
		long code = bean(IncValues.class).txIncValue(
		  domain, UnityTypes.unityType(
		  GoodUnit.class, TYPE_GOOD_UNIT), "code", 1
		);

		return SU.lenum(4, code);
	}

	public static boolean    equals(GoodUnit a, GoodUnit b)
	{
		return (a == b) || ((a != null) && (b != null) &&
		  (a.getPrimaryKey() != null) &&
		  a.getPrimaryKey().equals(b.getPrimaryKey()));
	}

	public static boolean    equals(GoodUnit a, Long b)
	{
		return (a != null) && (b != null) &&
		  (a.getPrimaryKey() != null) && a.getPrimaryKey().equals(b);
	}

	/**
	 * Takes two measures. If they have the same class code,
	 * returns the coefficient of 1-unit of b-measure from
	 * 1-unit of a-measure.
	 */
	public static BigDecimal coerce(MeasureUnit a, MeasureUnit b)
	{
		Measure ax = a.getOx();
		Measure bx = b.getOx();
		String  cc = ax.getClassCode();

		//?: {has class codes differ}
		if((cc == null) || !cc.equals(bx.getClassCode()))
			return null;

		//~: the volumes
		BigDecimal av = ax.getClassUnit();
		BigDecimal bv = bx.getClassUnit();
		if(av == null) av = BigDecimal.ONE;
		if(bv == null) bv = BigDecimal.ONE;

		//~: transition coefficient
		return bv.divide(av, 10, RoundingMode.HALF_EVEN).
		  stripTrailingZeros();
	}

	/**
	 * Ordinary good has sub-code equals to it's code.
	 * Sub-goods have their measure' code added.
	 */
	public static String     subCode(GoodUnit gu)
	{
		return !gu.isSubGood()?(gu.getCode()):
		  SU.cats(gu.getCode(), " ÷ ", gu.getMeasure().getCode());
	}


	/* Unity Types of Aggregated Values */

	public static UnityType aggrTypeRestCost()
	{
		return UnityTypes.unityType(
		  AggrValue.class, AGGRVAL_GOOD_REST_COST);
	}

	public static UnityType aggrTypeStoreVolume()
	{
		return UnityTypes.unityType(
		  AggrValue.class, AGGRVAL_STORE_VOLUME);
	}


	/* Aggregated Values Routines */

	public static BigDecimal aggrValueRestCost(AggrValue cost)
	{
		if(cost == null) return null;

		BigDecimal z = cost.getAggrValue();
		BigDecimal w = cost.getAggrDenom();

		if((z != null) && (BigDecimal.ZERO.compareTo(z) == 0))
			z = w = null;

		if((w != null) && (BigDecimal.ZERO.compareTo(w) == 0))
			z = w = null;

		if((z == null) || (w == null))
			return null;

		//HINT: aggregated value z = w * p, where
		// w is (denominator) aggregated rest(!) volume,
		// and p is the rest cost.
		//
		// Note that w is not the whole volume available!

		return z.divide(w, 5, RoundingMode.HALF_EVEN);
	}


	/* Special Checks */

	public static boolean    canBuyGood(GoodUnit gu)
	{
		return (gu.getGoodCalc() == null) || gu.getGoodCalc().isSemiReady();
	}


	/* Initialization and Copying */

	public static void init(GoodUnit gu, Good g)
	{
		//=: primary key
		g.setPkey(gu.getPrimaryKey());

		//=: code
		g.setCode(gu.getCode());

		//=: name
		g.setName(gu.getName());

		//=: group
		g.setGroup(gu.getGroup());

		//=: tx-number
		g.setTx(gu.getTxn());

		//=: measure unit
		if(gu.getMeasure() != null)
			g.setMeasure(gu.getMeasure().getPrimaryKey());

		//=: calculation
		if(gu.getGoodCalc() != null)
			g.setCalc(gu.getGoodCalc().getPrimaryKey());
	}

	public static void copySub(GoodUnit gu, GoodUnit sub)
	{
		//?: {has no keys}
		EX.assertn(gu.getPrimaryKey());
		EX.assertn(sub.getPrimaryKey());

		//?: {have differ measures}
		EX.assertn(gu.getMeasure());
		EX.assertn(sub.getMeasure());
		EX.assertx(!CMP.eq(gu.getMeasure(), sub.getMeasure()));

		//?: {not a good owner}
		EX.assertx(!gu.isSubGood());

		//?: {share the same unity}
		if(sub.getUnity() == null)
			sub.setUnity(gu.getUnity());
		else
			EX.assertx(CMP.eq(gu.getUnity(), sub.getUnity()));

		//=: domain
		if(sub.getDomain() == null)
			sub.setDomain(gu.getDomain());
		else
			EX.assertx(CMP.eq(gu.getDomain(), sub.getDomain()));

		//=: tx-number
		sub.setTxn(gu);
		sub.setTxn(gu.getUnity());

		//=: code
		sub.setCode(gu.getCode());

		//=: name
		sub.setName(gu.getName());
		sub.setSortName(gu.getSortName());

		//=: group
		sub.setGroup(gu.getGroup());
	}
}