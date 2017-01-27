package com.tverts.retrade.exec.api.sells;

/* standard Java classes */

import java.util.Date;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;
import com.tverts.retrade.exec.api.accounts.EnsureDefaults;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.PayDesk;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Inserts or updates given Sells Desk.
 *
 * Creates default Account, Payment Ways for
 * cash and bank operations, and Pay Desk.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InsertSellsDesk extends InsertEntityBase
{
	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof com.tverts.api.retrade.sells.SellsDesk);
	}

	protected Long    insert(Object source)
	{
		com.tverts.retrade.domain.sells.SellsDesk d = null;

		com.tverts.api.retrade.sells.SellsDesk    s =
		  (com.tverts.api.retrade.sells.SellsDesk) source;

		//~: try to load the existing desk
		if(s.getPkey() != null)
			d = EX.assertn(
			  bean(GetSells.class).getSellsDesk(s.getPkey()),
			  "Sells Desk p-key [", s.getPkey(), "] not found!"
			);

		//?: {found it} update
		if(d != null)
		{
			//~: code
			d.setCode(s.getCode());

			//~: name
			d.setName(s.getName());

			//~: remarks
			d.setRemarks(s.getRemarks());

			//~: update transaction number
			TxPoint.txn(tx(), d);

			return d.getPrimaryKey();
		}

		//~: create new sells desk
		d = new com.tverts.retrade.domain.sells.SellsDesk();

		//~: domain
		d.setDomain(domain());

		//~: code
		d.setCode(s.getCode());

		//~: name
		d.setName(s.getName());

		//~: remarks
		d.setRemarks(s.getRemarks());

		//~: default payment desk
		d.setPayDesk(defaultPayDesk());


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, d);

		return d.getPrimaryKey();
	}


	/* protected: insert support */

	protected static final String DEFAULT = "По умолчанию";

	protected PayDesk defaultPayDesk()
	{
		//~: load all existing desks
		List<PayDesk> desks = bean(GetSells.class).
		  getPayDesks(domain().getPrimaryKey());

		//~: search for the default name
		for(PayDesk d : desks)
			if(DEFAULT.equals(d.getName()))
				return d;

		//~: create the default
		PayDesk d = new PayDesk();

		//~: domain
		d.setDomain(domain());

		//~: name
		d.setName(DEFAULT);

		//~: remarks
		d.setRemarks(SU.cats(
		  "Платёжный терминал по умолчанию. ",
		  "Общий для всех касс. ",
		  "(Создан при импорте данных.)"
		));

		//~: open date
		d.setOpenDate(new Date(0L));

		//~: ensure default accounts strategy
		EnsureDefaults ea = new EnsureDefaults(domain(), null);
		ea.ensure();

		//~: default cash payment way
		d.setPayCash(ea.getPaySelfCash());

		//~: default banks payment way
		d.setPayBank(ea.getPaySelfBank());


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, d);

		return d;
	}
}