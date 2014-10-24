package com.tverts.retrade.domain.goods;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model to create or edit a {@link GoodUnit}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "good-model")
public class GoodModelBean extends ModelBeanBase
{
	/* Good Model Bean */

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


	/* private: encapsulated data */

	private GoodUnitView view;
	private GoodCalcView calc;
	private GoodCalcView historyCalc;
	private GoodCalcView editCalc;
	private Long         goodsFolder;
	private boolean      selSetAble;
	private boolean      calcUpdated;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.obj(o, view);
		IO.obj(o, calc);
		IO.obj(o, historyCalc);
		IO.obj(o, editCalc);

		o.writeLong(goodsFolder);
		o.writeBoolean(selSetAble);
		o.writeBoolean(calcUpdated);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		view        = IO.obj(i, GoodUnitView.class);
		calc        = IO.obj(i, GoodCalcView.class);
		historyCalc = IO.obj(i, GoodCalcView.class);
		editCalc    = IO.obj(i, GoodCalcView.class);

		goodsFolder = i.readLong();
		selSetAble  = i.readBoolean();
		calcUpdated = i.readBoolean();
	}
}