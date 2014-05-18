package com.tverts.retrade.domain.goods;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceList;


/**
 * Dictionary item that links a good name
 * with a measure unit {@link MeasureUnit}.
 *
 * Good Unit is a good (type) itself.
 * It is not separated from the measure.
 *
 * The price of the one unit of the good
 * is stored in {@link GoodPrice} entries
 * related with the {@link PriceList}s
 * of the Domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GoodUnit extends Entity implements CatItem
{
	/* public: GoodUnit bean interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void   setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCode()
	{
		return code;
	}

	public void   setCode(String code)
	{
		this.code = code;
	}

	public MeasureUnit getMeasure()
	{
		return measure;
	}

	public void   setMeasure(MeasureUnit measure)
	{
		this.measure = measure;
	}

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		this.name = name;
		this.nameLower = null;
	}

	public String getNameLower()
	{
		return (nameLower != null)?(nameLower):
		  (name == null)?(null):(nameLower = name.toLowerCase());
	}

	public void   setNameLower(String nameLower)
	{
		this.nameLower = nameLower;
	}

	public GoodCalc getGoodCalc()
	{
		return goodCalc;
	}

	public void setGoodCalc(GoodCalc goodCalc)
	{
		this.goodCalc = goodCalc;
	}


	/* persisted attributes */

	private Domain         domain;
	private String         code;
	private MeasureUnit    measure;
	private String         name;
	private String         nameLower;
	private GoodCalc       goodCalc;
}