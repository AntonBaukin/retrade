package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;


/**
 * Extended read-only view on a {@link PriceListEntity}
 * that supports {@link GoodPrice} values.
 *
 * This view is needed when displaying prices of the
 * Good Unit selected in all the Price Lists.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-list")
@XmlType(name = "price-list-view")
public class PriceListView extends CatItemView
{
	public static final long serialVersionUID = 20140806L;


	/* Price List View (bean) */

	public BigDecimal getPrice()
	{
		return price;
	}

	private BigDecimal price;

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}


	/* Initialization */

	public PriceListView init(Object obj)
	{
		if(obj instanceof GoodPrice)
			return initPrice((GoodPrice)obj);

		super.init(obj);
		return this;
	}

	public PriceListView initPrice(GoodPrice gp)
	{
		this.price = gp.getPrice();
		return this;
	}
}