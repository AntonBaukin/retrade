package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

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
public class MeasureUnitView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String getClassCode()
	{
		return classCode;
	}

	public void setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	public BigDecimal getClassUnit()
	{
		return classUnit;
	}

	public void setClassUnit(BigDecimal classUnit)
	{
		this.classUnit = classUnit;
	}

	public boolean isFractional()
	{
		return fractional;
	}

	public void setFractional(boolean fractional)
	{
		this.fractional = fractional;
	}


	/* public: init interface */

	public MeasureUnitView init(CatItem ci)
	{
		MeasureUnit mu = (MeasureUnit) ci;
		Measure      m = mu.getOx();

		classCode  = m.getClassCode();
		classUnit  = m.getClassUnit();
		fractional = m.isFractional();

		return (MeasureUnitView) super.init(ci);
	}


	/* measure attributes */

	private String     classCode;
	private BigDecimal classUnit;
	private boolean    fractional = true;
}
