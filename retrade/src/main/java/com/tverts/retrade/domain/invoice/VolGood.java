package com.tverts.retrade.domain.invoice;

/**
 * Plain persisted entity of Invoice Good.
 *
 * @author anton.baukin@gmail.com
 */
public class VolGood extends InvGood
{
	/* public: InvGood (bean) interface */

	public VolData getData()
	{
		return (VolData) super.getData();
	}

	public void setData(VolData data)
	{
		super.setData(data);
	}
}