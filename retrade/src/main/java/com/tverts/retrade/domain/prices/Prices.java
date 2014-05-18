package com.tverts.retrade.domain.prices;

/* standard Java classes */

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

import static com.tverts.support.DU.date2str;


/**
 * Collects some constants and static routines
 * for dealing with goods prices.
 *
 * @author anton.baukin@gmail.com
 */
public class Prices
{
	/* action types */

	public static final ActionType ACT_FIX_PRICES =
	  ActReprice.ACT_FIX_PRICES;


	/* action builder parameters */

	/**
	 * Send this parameter when fixing price change document
	 * {@link RepriceDoc} to assign the time of fix operation.
	 * If not set, current system time is taken.
	 */
	public static final String CHANGE_TIME  =
	  ActReprice.CHANGE_TIME;

	public static final String REPRICE_EDIT =
	  ActReprice.REPRICE_EDIT;


	/* types of entities */

	/**
	 * Unity Type of {@link PriceList}.
	 */
	public static final String TYPE_PRICE_LIST    =
	  "ReTrade: Prices: Price List";


	/**
	 * Unity Type name for {@link RepriceDoc} that is
	 * the the prices change document. Note that currently
	 * it does not implement United, but is primary generated
	 * to support it if needed.
	 */
	public static final String TYPE_REPRICE_DOC   =
	  "ReTrade: Goods: Price Change Document";


	/* public static: price lists */

	public static String getPriceListFullName(PriceList pl)
	{
		if(pl == null) return null;
		return String.format("№%s (%s)",
		  pl.getCode(), pl.getName());
	}


	/* public static: price change documents */

	public static String createRepriceDocCode(Date day, int index)
	{
		return String.format("ИЦ-%s/%d", date2str(day), index);
	}

	public static String createRepriceDocCode(Domain domain)
	{
		return String.format("ИЦ-%d", bean(IncValues.class).
		  txIncValue(domain, UnityTypes.unityType(
		    RepriceDoc.class, TYPE_REPRICE_DOC), "code", 1));
	}
}
