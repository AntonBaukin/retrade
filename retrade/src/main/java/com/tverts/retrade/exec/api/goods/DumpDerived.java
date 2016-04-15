package com.tverts.retrade.exec.api.goods;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade api */

import com.tverts.api.core.DumpEntities;
import com.tverts.api.retrade.goods.DerivedGood;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link GoodUnit} with active Calculation
 * of a derived good as a {@link DerivedGood}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpDerived extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		GoodUnit     u = (GoodUnit)src;
		DerivedGood  g = new DerivedGood();

		g.setPkey(u.getPrimaryKey());
		g.setTx(u.getTxn());
		g.setCode(u.getCode());
		g.setName(u.getName());
		g.setMeasure(u.getMeasure().getPrimaryKey());

		EX.assertn(u.getGoodCalc());
		g.setSuperGood(u.getGoodCalc().getSuperGood().getPrimaryKey());
		g.setSemiReady(u.getGoodCalc().isSemiReady());
		g.setSubCode(u.getGoodCalc().getOx().getSubCode());
		g.setSubVolume(u.getGoodCalc().getOx().getSubVolume());

		return g;
	}

	protected Class  getUnityClass()
	{
		return GoodUnit.class;
	}

	protected Class  getEntityClass()
	{
		return DerivedGood.class;
	}

	public String    getUnityType()
	{
		return Goods.TYPE_GOOD_UNIT;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected void   restrictDump(QueryBuilder qb, DumpEntities de)
	{
		//~: take only goods with active calculations of derived
		qb.getClauseWhere().addPart(
		  "e.goodCalc.superGood is not null"
		);
	}
}