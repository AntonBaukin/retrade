package com.tverts.retrade.domain.goods;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.OxCatItemBase;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Dictionary item naming a measure unit of
 * the goods volume.
 *
 * @author anton.baukin@gmail.com
 */
public class MeasureUnit extends OxCatItemBase
{
	public Measure getOx()
	{
		Measure m = (Measure) super.getOx();
		if(m == null)
		{
			setOx(m = new Measure());

			//!: fractional by the default
			m.setFractional(true);
		}

		return m;
	}

	public void    setOx(Object ox)
	{
		EX.assertx(ox instanceof Measure);
		super.setOx(ox);
	}

	public String  createOxSearch()
	{
		Measure m = getOx();

		return SU.catx(
		  m.getCode(), m.getName(),
		  m.getClassCode()
		);
	}
}