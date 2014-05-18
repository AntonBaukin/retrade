package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;


/**
 * Model to create or edit a {@link GoodUnit}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class GoodModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public GoodUnitView getView()
	{
		return view;
	}

	public void setView(GoodUnitView view)
	{
		this.view = view;
	}

	public GoodCalcView getCalc()
	{
		return calc;
	}

	public void setCalc(GoodCalcView calc)
	{
		this.calc = calc;
	}

	public GoodCalcView getHistoryCalc()
	{
		return historyCalc;
	}

	public void setHistoryCalc(GoodCalcView historyCalc)
	{
		this.historyCalc = historyCalc;
	}

	public GoodCalcView getEditCalc()
	{
		return editCalc;
	}

	public void setEditCalc(GoodCalcView editCalc)
	{
		this.editCalc = editCalc;
	}

	public Long getGoodsFolder()
	{
		return goodsFolder;
	}

	public void setGoodsFolder(Long goodsFolder)
	{
		this.goodsFolder = goodsFolder;
	}

	public boolean isSelSetAble()
	{
		return selSetAble;
	}

	public void setSelSetAble(boolean selSetAble)
	{
		this.selSetAble = selSetAble;
	}

	public boolean isCalcUpdated()
	{
		return calcUpdated;
	}

	public void setCalcUpdated(boolean calcUpdated)
	{
		this.calcUpdated = calcUpdated;
	}


	/* the good view */

	private GoodUnitView view;
	private GoodCalcView calc;
	private GoodCalcView historyCalc;
	private GoodCalcView editCalc;
	private Long         goodsFolder;
	private boolean      selSetAble;
	private boolean      calcUpdated;
}