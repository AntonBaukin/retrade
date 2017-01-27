package com.tverts.retrade.exec.api.sells;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.Sells;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps Sells Desks in teh same named API objects.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpSellsDesks extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		com.tverts.retrade.domain.sells.SellsDesk s =
		  (com.tverts.retrade.domain.sells.SellsDesk)src;

		com.tverts.api.retrade.sells.SellsDesk    d =
		  new com.tverts.api.retrade.sells.SellsDesk();

		//~: primary key
		d.setPkey(s.getPrimaryKey());

		//~: tx-number
		d.setTx(s.getTxn());

		//~: code
		d.setCode(s.getCode());

		//~: name
		d.setName(s.getName());

		//~: payment desk
		d.setPayDesk(s.getPayDesk().getPrimaryKey());

		//~: remarks
		d.setRemarks(s.getRemarks());

		return d;
	}

	protected Class  getUnityClass()
	{
		return com.tverts.retrade.domain.sells.SellsDesk.class;
	}

	protected Class  getEntityClass()
	{
		return com.tverts.api.retrade.sells.SellsDesk.class;
	}

	public String    getUnityType()
	{
		return Sells.TYPE_SELLS_DESK;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}
}
