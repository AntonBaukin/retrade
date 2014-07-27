package com.tverts.retrade.domain.prices;

/* Java */

import java.util.Set;

/* tverts.com: api */

import com.tverts.api.retrade.goods.PriceList;

/* tverts.com: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.OxCatEntity;

/* tverts.com: support */

import com.tverts.support.EX;


/**
 * Collection of prices for the goods.
 *
 * @author anton.baukin@gmail.com
 */
public class      PriceListEntity
       extends    OxCatEntity
       implements OxSearch
{
	/* Object Extraction */

	public PriceList getOx()
	{
		PriceList pl = (PriceList) super.getOx();
		if(pl == null) setOx(pl = new PriceList());
		return pl;
	}

	public void setOx(Object ox)
	{
		EX.assertx(ox instanceof PriceList);
		super.setOx(ox);
	}


	/* Price List */

	public Set<GoodPrice> getItems()
	{
		return items;
	}

	private Set<GoodPrice> items;

	public void setItems(Set<GoodPrice> items)
	{
		this.items = items;
	}
}