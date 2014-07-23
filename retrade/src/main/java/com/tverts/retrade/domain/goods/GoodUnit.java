package com.tverts.retrade.domain.goods;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.OxCatEntity;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceList;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Catalogue item that links a good name
 * with a {@link MeasureUnit}.
 *
 * Good Unit is a good (type) itself.
 * It is not separated from it's measure.
 *
 * The price of the one unit of the good
 * is stored in {@link GoodPrice} entries
 * related with the {@link PriceList}s
 * of the Domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      GoodUnit
       extends    OxCatEntity
       implements OxSearch
{
	/* Object Extraction */

	public Good getOx()
	{
		Good g = (Good) super.getOx();
		if(g == null) setOx(g = new Good());
		return g;
	}

	public void setOx(Object ox)
	{
		EX.assertx(ox instanceof Good);
		super.setOx(ox);
	}

	public void updateOx()
	{
		super.updateOx();

		Good g = getOx();

		//=: measure
		g.setMeasure((measure == null)?(null):(measure.getPrimaryKey()));

		//:= calc
		g.setCalc((calc == null)?(null):(calc.getPrimaryKey()));
	}


	/* Good Unit */

	public MeasureUnit getMeasure()
	{
		return measure;
	}

	private MeasureUnit measure;

	public void setMeasure(MeasureUnit measure)
	{
		this.measure = measure;
	}

	public GoodCalc getGoodCalc()
	{
		return calc;
	}

	private GoodCalc calc;

	public void setGoodCalc(GoodCalc goodCalc)
	{
		this.calc = goodCalc;
	}

	public String getSortName()
	{
		return (sortName != null)?(sortName):
		  (sortName = SU.sort(this.getName()));
	}

	private String sortName;

	public void setSortName(String sortName)
	{
		this.sortName = sortName;
	}

	public void setName(String name)
	{
		//?: {has name already assigned}
		if((getName() != null) && !getName().equals(name))
			this.sortName = null;

		super.setName(name);
	}
}