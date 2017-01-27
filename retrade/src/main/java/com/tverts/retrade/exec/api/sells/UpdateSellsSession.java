package com.tverts.retrade.exec.api.sells;

/* standard Java classes */

import java.util.HashSet;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;
import com.tverts.hibery.system.events.LoadReadOnly;
import com.tverts.hibery.system.events.OnHiberEvent;

/* com.tverts: tx */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (sells + stores) */

import com.tverts.retrade.domain.sells.ActSellsSession;
import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.GoodSell;
import com.tverts.retrade.domain.sells.SellReceipt;
import com.tverts.retrade.domain.sells.SellsDesk;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Updates Sells Session in two modes: easy, when
 * some of the attributes are changed; and hard,
 * when the re-fixing is done with all the derived
 * entities recalculated.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateSellsSession extends UpdateEntityBase
{
	protected boolean      isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof com.tverts.api.retrade.sells.SellsSession);
	}

	protected Class        getUnityClass(Holder holder)
	{
		return com.tverts.retrade.domain.sells.SellsSession.class;
	}

	protected void         update(Object entity, Object source)
	{
		com.tverts.api.retrade.sells.SellsSession    s =
		  (com.tverts.api.retrade.sells.SellsSession) source;

		com.tverts.retrade.domain.sells.SellsSession d =
		  (com.tverts.retrade.domain.sells.SellsSession) entity;

		//?: {items were updated} update session on own tx
		if(s.isItemsUpdated())
			updateSession(d.getPrimaryKey(), s);
		//~: just update session entity
		else
		{
			updateSessionTx(s, d);
			return;
		}

		//~: find all the stores
		TradeStore[] stores = findStores(d);

		//!: clear the session (do nothing there)
		session().clear();

		//!: update action for each store
		for(TradeStore ts : stores)
			updateSession(d.getPrimaryKey(), ts.getPrimaryKey());
	}

	/**
	 * TODO set AggrValue as read-only after asynchronous aggregation...
	 */
	protected void         setupTxContext(Tx tx)
	{
		tx.set(OnHiberEvent.class, new LoadReadOnly(
		  com.tverts.endure.Unity.class,
		  com.tverts.endure.UnityType.class,
		  //com.tverts.endure.aggr.AggrValue.class,
		  com.tverts.retrade.domain.goods.MeasureUnit.class,
		  com.tverts.retrade.domain.goods.GoodUnit.class,
		  com.tverts.retrade.domain.goods.CalcPart.class,
		  com.tverts.retrade.domain.goods.GoodCalc.class,
		  com.tverts.retrade.domain.sells.SellReceipt.class
		));
	}


	/* protected: update support */

	protected SellsDesk    loadSellsDesk
	  (com.tverts.api.retrade.sells.SellsSession s)
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

	protected String       debugPrintSession
	  (com.tverts.retrade.domain.sells.SellsSession d, TradeStore ts)
	{
		Set<Long> goods = new HashSet<Long>(101);
		int positions = 0; for(SellReceipt r : d.getReceipts())
		{
			positions += r.getGoods().size();
			for(GoodSell g : r.getGoods())
				if(ts.equals(g.getStore()))
					goods.add(g.getGoodUnit().getPrimaryKey());
		}

		return SU.cats("updated Sells Session [", d.getPrimaryKey(),
		  "]+[", d.getCode(), "] in Store [", ts.getPrimaryKey(),
		  "] having [", d.getReceipts().size(),
		  "] receipts with total positions [", positions,
		  "] and unique goods [", goods.size(),
		  "]. Session size [", HiberSystem.debugContextSize(), "]"
		);
	}

	protected TradeStore[] findStores
	  (com.tverts.retrade.domain.sells.SellsSession d)
	{
		Set<TradeStore> res = new HashSet<TradeStore>(7);

		for(SellReceipt sr : d.getReceipts())
			for(GoodSell gs : sr.getGoods())
				res.add(gs.getStore());

		return res.toArray(new TradeStore[res.size()]);
	}

	protected void         updateSession
	  (final Long dpk, final com.tverts.api.retrade.sells.SellsSession s)
	{
		bean(TxBean.class).setNew().execute(new Runnable()
		{
			public void run()
			{
				com.tverts.retrade.domain.sells.SellsSession d =
				  bean(GetSells.class).getSession(dpk);

				updateSessionTx(s, d);
			}
		});
	}

	protected void         updateSession(final Long dpk, final Long tspk)
	{
		bean(TxBean.class).setNew().execute(new Runnable()
		{
			public void run()
			{
				long td = System.currentTimeMillis();
   			setupTxContext(TxPoint.txContext());

				com.tverts.retrade.domain.sells.SellsSession d =
				  bean(GetSells.class).getSession(dpk);

				TradeStore ts = bean(GetTradeStore.class).getTradeStore(tspk);

				ActionsPoint.actionRun(ActionType.UPDATE, d,
				  ActSellsSession.PARAM_STORE, ts,
				  ActionsPoint.SYNCH_AGGR, true
				);

				if(LU.isD(LU.LOGT)) LU.D(LU.LOGT,
				  debugPrintSession(d, ts), " in ", LU.td(td));
			}
		});
	}

	protected void         updateSessionTx
	  (
	    com.tverts.api.retrade.sells.SellsSession    s,
 	    com.tverts.retrade.domain.sells.SellsSession d
	  )
	{
		//~: code
		if(s.getCode() != null)
			d.setCode(s.getCode());

		//~: open time
		if(s.getTime() != null)
			d.setTime(s.getTime());

		//~: close time
		if(s.getCloseTime() != null)
			d.setCloseTime(s.getCloseTime());

		//?: {items are not updated}
		if(!s.isItemsUpdated())
		{
			//~: sells desk must be the same
			if(s.getSellsDesk() != null) EX.assertx(
			  CMP.eq(d.getSellsDesk().getPrimaryKey(), s.getSellsDesk()),
			  "Sells Session p-key [", d.getPrimaryKey(),
			  "] has Sells Desk updated what is allowed with items updating only!"
			);

			//~: update transaction number
			TxPoint.txn(tx(), d);

			//HINT: this is easy update with, returning...
			return;
		}

		//?: {sells desk was changed}
		if(s.getSellsDesk() != null)
			if(!CMP.eq(d.getSellsDesk().getPrimaryKey(), s.getSellsDesk()))
		{
			//~: sells desk
			d.setSellsDesk(loadSellsDesk(s));

			//~: payment desk of that sells desk
			d.setPayDesk(d.getSellsDesk().getPayDesk());
		}
	}
}