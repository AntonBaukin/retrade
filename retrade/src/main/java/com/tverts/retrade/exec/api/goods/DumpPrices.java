package com.tverts.retrade.exec.api.goods;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.PriceItem;

/* com.tverts: execution (api) */

import com.tverts.api.core.DumpEntities;
import com.tverts.exec.api.EntitiesDumperBase;


/**
 * Dumps {@link GoodPrice}s as {@link PriceItem}s.
 *
 * @author anton.baukin@gmail.com.
 */
public class DumpPrices extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		GoodPrice p = (GoodPrice) src;
		PriceItem i = new PriceItem();

		i.setPkey(p.getPrimaryKey());
		i.setGood(p.getGoodUnit().getPrimaryKey());
		i.setList(p.getPriceList().getPrimaryKey());
		i.setPrice(p.getPrice());

		return i;
	}

	protected Class  getUnityClass()
	{
		return GoodPrice.class;
	}

	protected Class  getEntityClass()
	{
		return PriceItem.class;
	}

	protected void   restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		//~: restrict by the price list domain
		qb.getClauseWhere().addPart(
		  "e.priceList.domain = :domain"
		).
		  param("domain", tx().getDomain());
	}
}