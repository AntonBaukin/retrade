package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;


/**
 * Good of a Buy Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class BuyGood extends InvGood
{
	/* public: bean interface */

	/**
	 * The cost of the whole volume.
	 */
	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCost(BigDecimal c)
	{
		if((c != null) && (c.scale() != 10))
			c = c.setScale(10);

		this.cost = c;
	}


	/* public: InvGood (bean) interface */

	public BuyData getData()
	{
		return (BuyData) super.getData();
	}

	public void setData(BuyData data)
	{
		super.setData(data);
	}


	/* persisted attributes */

	private BigDecimal cost;
}