package com.tverts.retrade.exec.api.sells;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.Sells;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps Sells Sessions in the same named API objects.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpSellsSessions extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		com.tverts.retrade.domain.sells.SellsSession s =
		  (com.tverts.retrade.domain.sells.SellsSession)src;

		com.tverts.api.retrade.sells.SellsSession    d =
		  new com.tverts.api.retrade.sells.SellsSession();

		//~: primary key
		d.setPkey(s.getPrimaryKey());

		//~: tx-number
		d.setTx(s.getTxn());

		//~: code
		d.setCode(s.getCode());

		//~: open time
		d.setTime(s.getTime());

		//~: close time
		d.setCloseTime(s.getCloseTime());

		//~: sells desk
		d.setSellsDesk(s.getSellsDesk().getPrimaryKey());

		//~: payment desk
		d.setPayDesk(s.getPayDesk().getPrimaryKey());

		//~: remarks
		d.setRemarks(s.getRemarks());

		//~: income
		d.setIncome(s.getActualIncome());

		//!: sells sessions are always fixed
		d.setFixed(true);

		return d;
	}

	protected Class  getUnityClass()
	{
		return com.tverts.retrade.domain.sells.SellsSession.class;
	}

	protected Class  getEntityClass()
	{
		return com.tverts.api.retrade.sells.SellsSession.class;
	}

	public String    getUnityType()
	{
		return Sells.TYPE_SELLS_SESS;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}
}