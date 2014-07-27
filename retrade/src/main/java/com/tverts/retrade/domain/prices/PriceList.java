package com.tverts.retrade.domain.prices;

/* Java */

import java.util.Set;

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
public class      PriceList
       extends    OxCatEntity
       implements OxSearch
{
	/* Object Extraction */

	public com.tverts.api.retrade.goods.PriceList getOx()
	{
		com.tverts.api.retrade.goods.PriceList pl =
		  (com.tverts.api.retrade.goods.PriceList) super.getOx();
		if(pl == null) setOx(pl = new com.tverts.api.retrade.goods.PriceList());
		return pl;
	}

	public void setOx(Object ox)
	{
		EX.assertx(ox instanceof com.tverts.api.retrade.goods.PriceList);
		super.setOx(ox);
	}

	public void setCode(String code)
	{
		super.setCode(code);
	}

	public void setName(String name)
	{
		super.setName(name);
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