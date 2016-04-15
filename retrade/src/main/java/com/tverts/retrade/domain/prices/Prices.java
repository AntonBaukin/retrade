package com.tverts.retrade.domain.prices;

/* Java */

import java.util.Date;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.IncValues;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Collects some constants and static routines
 * for dealing with goods prices.
 *
 * @author anton.baukin@gmail.com
 */
public class Prices
{
	/* Action Types */

	public static final ActionType ACT_FIX_PRICES =
	  ActReprice.ACT_FIX_PRICES;


	/* Action Builder Parameters */

	/**
	 * Send this parameter when fixing price change document
	 * {@link RepriceDoc} to assign the time of fix operation.
	 * If not set, current system time is taken.
	 */
	public static final String CHANGE_TIME  =
	  ActReprice.CHANGE_TIME;

	public static final String REPRICE_EDIT =
	  ActReprice.REPRICE_EDIT;


	/* Unity Types of Entities */

	/**
	 * Unity Type of {@link PriceListEntity}.
	 */
	public static final String TYPE_PRICE_LIST   =
	  "ReTrade: Prices: Price List";


	/**
	 * Unity Type name for {@link RepriceDoc} that is
	 * the the prices change document. Note that currently
	 * it does not implement United, but is primary generated
	 * to support it if needed.
	 */
	public static final String TYPE_REPRICE_DOC  =
	  "ReTrade: Goods: Price Change Document";

	/**
	 * Price Change Document with changes for the
	 * contractors having Lists association changed.
	 */
	public static final String TYPE_FIRM_REPRICE =
	  "ReTrade: Goods: Firm Price Change Document";


	/* Routines for Price Lists */

	public static String getPriceListFullName(PriceListEntity pl)
	{
		return (pl == null)?(null):
		  String.format("%s; %s", pl.getCode(), pl.getName());
	}


	/* Routines for Price Change Documents */

	public static String createRepriceDocCode(Date day, int index)
	{
		return String.format("ИЦ-%s/%d", DU.date2str(day), index);
	}

	public static String createRepriceDocCode(Domain domain)
	{
		return String.format("ИЦ-%d", bean(IncValues.class).
		  txIncValue(domain, UnityTypes.unityType(
		    RepriceDoc.class, TYPE_REPRICE_DOC), "code", 1));
	}

	public static String createRepriceDocCode(PriceListEntity pl)
	{
		return String.format("ИЦ.ИСХ-%s", EX.asserts(pl.getCode()));
	}

	public static String createRepriceDocFirmsCode(Domain domain)
	{
		return String.format("ИЦ.КОН-%d", bean(IncValues.class).
		  txIncValue(domain, UnityTypes.unityType(
		    RepriceDoc.class, TYPE_FIRM_REPRICE), "code", 1));
	}
}