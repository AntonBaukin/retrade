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

		//?: {is a sub-good}
		if(getSuperGood() != null)
			return getSuperGood().getOx();

		//?: {create initial good}
		Good g = (Good) super.getOx();
		if(g == null)
			super.setOx(g = Goods.initOx(this));

		return g;
	}

	/**
	 * For ordinary goods this is the same ox-object.
	 * For sub-goods this is distinct instance not
	 * shared with it's super good.
	 */
	public Good   getOxOwn()
	{
		//?: {this is not a sub-good}
		if(getSuperGood() == null)
			return this.getOx();

		//?: {create initial good}
		Good g = (Good) super.getOx();
		if(g == null)
			super.setOx(g = Goods.initOx(this));

		return g;
	}

	public void   setOx(Object ox)
	{
		EX.assertx(ox instanceof Good);
		EX.assertn(getPrimaryKey());

		//?: {allow for owner good only}
		EX.assertx(getSuperGood() == null);
		super.setOx(ox);
	}

	public void   setOxOwn(Object ox)
	{
		EX.assertx(ox instanceof Good);

		//?: {allow for sub-good only}
		EX.assertn(getSuperGood());
		super.setOx(ox);
	}

	public void   updateOx()
	{
		//?: {is a sub-good} nothing else
		if(getSuperGood() != null)
		{
			Good g = getOxOwn();

			//=: is service flag
			g.setService(this.service);

			return;
		}

		Good g = getOx();

		//=: ox-good measure
		g.setMeasure((measure == null)?(null):(measure.getPrimaryKey()));

		//=: ox-good calculation key
		g.setCalc((calc == null)?(null):(calc.getPrimaryKey()));

		//=: is service flag
		this.service = g.isService();

		//=: visibility flags
		this.visibleBuy = g.isVisibleBuy();
		this.visibleSell = g.isVisibleSell();
		this.visibleLists = g.isVisibleLists();
		this.visibleReports = g.isVisibleReports();

		super.updateOx();
	}

	public void   updateOxOwn()
	{
		Good g = getOxOwn();

		//=: ox-good measure
		g.setMeasure((measure == null)?(null):(measure.getPrimaryKey()));

		//=: ox-good calculation key
		g.setCalc((calc == null)?(null):(calc.getPrimaryKey()));

		//=: visibility flags
		this.visibleBuy = g.isVisibleBuy();
		this.visibleSell = g.isVisibleSell();
		this.visibleLists = g.isVisibleLists();
		this.visibleReports = g.isVisibleReports();

		super.updateOx();
	}


	/* Good Unit */

	public GoodUnit getSuperGood()
	{
		return superGood;
	}

	private GoodUnit superGood;

	public void setSuperGood(GoodUnit superGood)
	{
		this.superGood = superGood;
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

	public boolean isService()
	{
		return service;
	}

	private boolean service;

	public void setService(boolean service)
	{
		this.service = service;
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


	/* Visibility Flags */

	public boolean isVisibleSell()
	{
		return visibleSell;
	}

	private boolean visibleSell = true;

	public void setVisibleSell(boolean visibleSell)
	{
		this.visibleSell = visibleSell;
	}

	public boolean isVisibleBuy()
	{
		return visibleBuy;
	}

	private boolean visibleBuy = true;

	public void setVisibleBuy(boolean visibleBuy)
	{
		this.visibleBuy = visibleBuy;
	}

	public boolean isVisibleLists()
	{
		return visibleLists;
	}

	private boolean visibleLists = true;

	public void setVisibleLists(boolean visibleLists)
	{
		this.visibleLists = visibleLists;
	}

	public boolean isVisibleReports()
	{
		return visibleReports;
	}

	private boolean visibleReports = true;

	public void setVisibleReports(boolean visibleReports)
	{
		this.visibleReports = visibleReports;
	}
}