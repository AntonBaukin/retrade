package com.tverts.retrade.domain.goods;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: endure (core) */

import com.tverts.endure.OxSearch;
import com.tverts.endure.core.OxCatEntity;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceListEntity;

/* com.tverts: support */

import com.tverts.support.CMP;
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
 * related with the {@link PriceListEntity}s
 * of the Domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GoodUnit extends OxCatEntity implements OxSearch
{
	/* Object Extraction */

	public Good   getOx()
	{
		EX.assertn(getPrimaryKey());
		Good g = (Good) super.getOx();

		//?: {is a sub-good}
		if(isSubGood())
			return EX.assertn(g);
		//?: {create initial good}
		else if(g == null)
			setOx(g = new Good());

		return g;
	}

	public void   setOx(Object ox)
	{
		EX.assertx(ox instanceof Good);
		EX.assertn(getPrimaryKey());

		super.setOx(ox);
	}

	public void   updateOx()
	{
		Good g = getOx();

		//=: update own group
		this.group = g.getGroup();

		//?: {is a sub-good} nothing else
		if(isSubGood())
			return;

		//=: ox-good measure
		g.setMeasure((measure == null)?(null):(measure.getPrimaryKey()));

		//=: ox-good calculation key
		g.setCalc((calc == null)?(null):(calc.getPrimaryKey()));

		super.updateOx();
	}

	public String getOxSearch()
	{
		Good g = getOx();
		return SU.catx(g.getCode(), group, g.getName());
	}


	/* Good Unit */

	public boolean isSubGood()
	{
		return (getUnity() != null) && !CMP.eq(getUnity(), this);
	}

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
		if(CMP.eq(getName(), name)) return;

		this.sortName = null;
		super.setName(name);
	}

	public String getGroup()
	{
		return group;
	}

	private String group;

	public void setGroup(String group)
	{
		this.group = group;
	}


	/* protected: sub-goods support */

	public void    setUnityTxn()
	{
		if(!isSubGood())
			super.setUnityTxn();
	}

	protected void setUnityKey()
	{
		EX.assertn(getPrimaryKey());
		if(!isSubGood())
			super.setUnityKey();
	}

	protected void setUnityOx()
	{
		if(!isSubGood())
			super.setUnityOx();
	}
}