package com.tverts.retrade.domain.prices;

/* Java */

import java.util.Set;

/* tverts.com: api */

import com.tverts.api.retrade.prices.PriceList;

/* tverts.com: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.Remarkable;
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
       implements OxSearch, Remarkable
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

	public String getRemarks()
	{
		return getOx().getComment();
	}

	public void setRemarks(String remarks)
	{
		getOx().setComment(remarks);
	}

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