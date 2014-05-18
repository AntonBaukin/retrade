package com.tverts.retrade.exec.api.goods;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.CalcPart;
import com.tverts.retrade.domain.goods.GoodCalc;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.CalcItem;

/* com.tverts: execution (api) */

import com.tverts.api.core.DumpEntities;
import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link GoodCalc} as {@link Calc}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpCalcs extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		GoodCalc s = (GoodCalc)src;
		Calc     d = new Calc();

		//~: primary key
		d.setPkey(s.getPrimaryKey());

		//~: tx-number
		d.setTx(s.getTxn());

		//~: time (as open time)
		d.setTime(s.getOpenTime());

		//~: good
		d.setGood(s.getGoodUnit().getPrimaryKey());

		//~: report as fixed document
		d.setFixed(true);

		//c: for the parts
		for(CalcPart p : s.getParts())
		{
			CalcItem i = new CalcItem();
			d.getItems().add(i);

			//~: good
			i.setGood(p.getGoodUnit().getPrimaryKey());

			//~: volume
			i.setVolume(p.getVolume());
		}

		return d;
	}

	protected Class  getUnityClass()
	{
		return GoodCalc.class;
	}

	protected Class  getEntityClass()
	{
		return Calc.class;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected int    getDumpLimit(DumpEntities de)
	{
		return 16;
	}

	protected void   restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		//~: restrict through the good unit
		qb.getClauseWhere().addPart(
		  "e.goodUnit.domain = :domain"
		).
		  param("domain", tx().getDomain());
	}

	protected void   restrictDump(QueryBuilder qb, DumpEntities de)
	{
		//HINT: we do not include calculations for the derived goods!
		qb.getClauseWhere().addPart(
		  "e.superGood is null"
		);
	}
}