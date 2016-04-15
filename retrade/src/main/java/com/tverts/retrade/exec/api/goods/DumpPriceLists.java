package com.tverts.retrade.exec.api.goods;

/* com.tverts: retrade api */

import com.tverts.api.retrade.prices.PriceList;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.Prices;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link PriceListEntity}s
 * as {@link PriceList}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpPriceLists extends EntitiesDumperBase
{
	@SuppressWarnings("unchecked")
	protected Object createApiEntity(Object src)
	{
		PriceListEntity s =
		  (PriceListEntity)src;

		PriceList       d = new PriceList();

		//~: set catalogue item properties
		d.setPkey(s.getPrimaryKey());
		d.setTx(s.getTxn());
		d.setCode(s.getCode());
		d.setName(s.getName());

		return d;
	}

	protected Class  getUnityClass()
	{
		return PriceListEntity.class;
	}

	protected Class  getEntityClass()
	{
		return PriceList.class;
	}

	public String    getUnityType()
	{
		return Prices.TYPE_PRICE_LIST;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}
}