package com.tverts.retrade.exec.api.sells;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.SellsDesk;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Creates and saves new Sells Session from
 * the given API object.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertSellsSession extends InsertEntityBase
{
	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof com.tverts.api.retrade.sells.SellsSession);
	}

	protected Long    insert(Object source)
	{
		com.tverts.api.retrade.sells.SellsSession    s =
		  (com.tverts.api.retrade.sells.SellsSession) source;

		com.tverts.retrade.domain.sells.SellsSession d =
		  new com.tverts.retrade.domain.sells.SellsSession();

		//~: domain
		d.setDomain(domain());

		//~: code
		d.setCode(s.getCode());

		//~: open time
		d.setTime(s.getTime());

		//~: close time
		d.setCloseTime(s.getCloseTime());

		//~: pay desk must be empty
		EX.assertx(s.getPayDesk() == null);

		//~: sells desk
		d.setSellsDesk(loadSellsDesk(s));

		//~: payment desk of that sells desk
		d.setPayDesk(d.getSellsDesk().getPayDesk());


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, d);

		return d.getPrimaryKey();
	}


	/* protected: insert support */

	protected SellsDesk loadSellsDesk(com.tverts.api.retrade.sells.SellsSession s)
	{
		EX.assertn(s.getSellsDesk(), "Sells Session x-key [", s.getXkey(),
		  "] has Sells Desk p-key undefined!"
		);

		SellsDesk sd = EX.assertn(
		  bean(GetSells.class).getSellsDesk(s.getSellsDesk()),

		  "Sells Session x-key [", s.getXkey(),
		  "] refers unknown Sells Desk p-key [",
		  s.getSellsDesk(), "]!"
		);

		//sec: check the domain
		checkDomain(sd);

		return sd;
	}
}