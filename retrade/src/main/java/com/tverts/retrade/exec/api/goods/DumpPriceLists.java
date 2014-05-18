package com.tverts.retrade.exec.api.goods;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.Prices;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link com.tverts.retrade.domain.prices.PriceList}s
 * as {@link com.tverts.api.retrade.goods.PriceList}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpPriceLists extends EntitiesDumperBase
{
	@SuppressWarnings("unchecked")
	protected Object createApiEntity(Object src)
	{
		com.tverts.retrade.domain.prices.PriceList s =
		  (com.tverts.retrade.domain.prices.PriceList)src;

		com.tverts.api.retrade.goods.PriceList     d =
		  new com.tverts.api.retrade.goods.PriceList();

		//~: set catalogue item properties
		d.setPkey(s.getPrimaryKey());
		d.setTx(s.getTxn());
		d.setCode(s.getCode());
		d.setName(s.getName());

		//~: parent price list
		if(s.getParent() != null)
			d.setParent(s.getParent().getPrimaryKey());

		return d;
	}

	protected Class  getUnityClass()
	{
		return com.tverts.retrade.domain.prices.PriceList.class;
	}

	protected Class  getEntityClass()
	{
		return com.tverts.api.retrade.goods.PriceList.class;
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