package com.tverts.retrade.exec.api.goods;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Measure;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;


/**
 * Dumps {@link MeasureUnit} as {@link Measure}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpMeasures extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		MeasureUnit u = (MeasureUnit)src;
		Measure     m = new Measure();

		m.setPkey(u.getPrimaryKey());
		m.setTx(u.getTxn());
		m.setCode(u.getCode());
		m.setName(u.getName());

		m.setClassCode(u.getClassCode());
		m.setClassUnit(u.getClassUnit());
		m.setFractional(u.isFractional());

		return m;
	}

	protected Class  getUnityClass()
	{
		return MeasureUnit.class;
	}

	protected Class  getEntityClass()
	{
		return Measure.class;
	}
}