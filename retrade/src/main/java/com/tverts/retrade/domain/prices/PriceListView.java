package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;


/**
 * Extended read-only view on a {@link PriceListEntity}
 * that supports {@link GoodPrice} values.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-list")
public class PriceListView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* public: PriceListView (bean) interface */

	public BigDecimal getPrice()
	{
		return price;
	}

	private BigDecimal price;

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}


	/* public: initialization interface */

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