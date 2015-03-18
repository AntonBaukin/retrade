package com.tverts.retrade.exec.api.goods;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps {@link GoodUnit} as {@link Good}.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpGoods extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		GoodUnit u = (GoodUnit)src;
		Good     g = new Good();

		Goods.init(u, g);
		return g;
	}

	protected Class  getUnityClass()
	{
		return GoodUnit.class;
	}

	protected Class  getEntityClass()
	{
		return Good.class;
	}

	public String    getUnityType()
	{
		return Goods.TYPE_GOOD_UNIT;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}
}