package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;

/* com.tverts: support */

import com.tverts.support.fmt.FmtCtx;


/**
 * Extended read-only view on a {@link PriceList}
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

	public void       setPrice(BigDecimal price)
	{
		this.price = price;
	}

	@XmlElement
	public String     getParents()
	{
		return parents;
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

	public CatItemView   init(CatItem ci)
	{
		//?: {price list} format the name
		if(ci instanceof PriceList)
			this.parents = PriceListFmt.INSTANCE.parents(
			  new FmtCtx().obj(ci).set(PriceListFmt.LONGER)
			);

		return super.init(ci);
	}



	/* private: view attributes */

	private BigDecimal price;
	private String     parents;
}