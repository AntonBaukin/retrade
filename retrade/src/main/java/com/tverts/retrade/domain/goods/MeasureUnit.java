package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.cats.CatItemBase;


/**
 * Dictionary item naming a measure unit of
 * the goods volume.
 *
 * @author anton.baukin@gmail.com
 */
public class MeasureUnit extends CatItemBase
{
	/* public: MeasureUnit (bean) interface */

	/**
	 * Tells that the volume measured by this unit
	 * contains fractional part.
	 */
	public boolean     isFractional()
	{
		return fractional;
	}

	public void        setFractional(boolean fractional)
	{
		this.fractional = fractional;
	}

	public String      getClassCode()
	{
		return classCode;
	}

	public void        setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	public BigDecimal  getClassUnit()
	{
		return classUnit;
	}

	public void        setClassUnit(BigDecimal cu)
	{
		if((cu != null) && (cu.scale() != 8))
			cu = cu.setScale(8);

		this.classUnit = cu;
	}


	/* persisted attributes */

	private String     classCode;
	private BigDecimal classUnit;
	private boolean    fractional = true;
}