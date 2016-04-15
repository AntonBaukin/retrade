package com.tverts.retrade.domain.goods;

/* Java */

import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;


/**
 * View of {@link MeasureUnit} instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "measure-unit")
@XmlType(name = "measure-unit-view")
public class MeasureUnitView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* Measure Unit View */

	public String getClassCode()
	{
		return classCode;
	}

	private String classCode;

	public void setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	public BigDecimal getClassUnit()
	{
		return classUnit;
	}

	private BigDecimal classUnit;

	public void setClassUnit(BigDecimal classUnit)
	{
		this.classUnit = classUnit;
	}

	public boolean isFractional()
	{
		return fractional;
	}

	private boolean fractional = true;

	public void setFractional(boolean fractional)
	{
		this.fractional = fractional;
	}


	/* Initialization */

	public MeasureUnitView init(CatItem ci)
	{
		MeasureUnit mu = (MeasureUnit) ci;
		Measure      m = mu.getOx();

		classCode  = m.getClassCode();
		classUnit  = m.getClassUnit();
		fractional = m.isFractional();

		return (MeasureUnitView) super.init(ci);
	}
}
