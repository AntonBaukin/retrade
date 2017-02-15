package com.tverts.retrade.exec.api.invoices;

/* Java */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import static com.tverts.hibery.HiberPoint.isTestPrimaryKey;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (*) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: api */

import com.tverts.api.retrade.document.Buy;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.goods.GoodSell;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Implementation base for executor to update
 * {@link Invoice}s of various Unity Types from
 * the given API {@link Buy} instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UpdateInvoiceBase extends UpdateEntityBase
{
	/* protected: abstraction interface */

	protected abstract String      getInvoiceTypeName();

	/* protected: UpdateEntityBase interface */

	protected Class   getUnityClass(Holder holder)
	{
		return Invoice.class;
	}

	protected void    update(Object entity, Object source)
	{
		Invoice i  = (Invoice) entity;
		BuySell bs = (BuySell) source;

		//~: check the invoice
		checkInvoice(i);

		//~: unfix invoice
		unfixInvoice(i);

		//~: create invoice edit view
		InvoiceEdit ie = createInvoiceEdit(i, bs);

		//!: update the invoice
		ActionsPoint.actionRun(ActionType.UPDATE, i,
		  Invoices.INVOICE_EDIT, ie);

		//~: fix the invoice
		fixInvoice(i, bs);

		//!: flush the session
		HiberPoint.flush(session(), HiberPoint.CLEAR);
	}


	/* protected: invoice processing */

	protected UnityType   getInvoiceType()
	{
		return UnityTypes.unityType(
		  Invoice.class, getInvoiceTypeName());
	}

	protected void        checkInvoice(Invoice i)
	{
		//?: {wrong invoice type}
		EX.assertx(
		  getInvoiceType().equals(i.getUnity().getUnityType()),

		  "For Invoice [", i.getPrimaryKey(), "] required ",
		  getInvoiceType().toString(), ", but got ",
		  i.getUnity().getUnityType().toString(), '!'
		);
	}

	protected void        unfixInvoice(Invoice i)
	{
		//?: {the invoice is not actually fixed}
		if(!Invoices.isInvoiceFixed(i))
			return;

		//!: execute edit action
		ActionsPoint.actionRun(Invoices.ACT_EDIT, i);
	}

	protected void        fixInvoice(Invoice i, BuySell bs)
	{
		//?: {the invoice is not fixed}
		if(!bs.isFixed()) return;

		//!: flush the session
		//HiberPoint.flush(session());

		//!: execute fix action
		ActionsPoint.actionRun(Invoices.ACT_FIX, i);
	}

	protected InvoiceEdit createInvoiceEdit(Invoice i, BuySell bs)
	{
		InvoiceEdit ie = new InvoiceEdit().init(i);

		//~: code
		ie.setInvoiceCode(bs.getCode());

		//~: edit date (and the order)
		ie.setEditDate(bs.getTime());

		//~: assign the contractor
		assignContractor(ie, bs);

		//~: assign the store
		assignStore(ie, bs);

		//~: merge the goods
		mergeGoodsEdit(ie, bs);

		return ie;
	}

	protected void        assignContractor(InvoiceEdit ie, BuySell bs)
	{
		//~: check & assign the contractor
		ie.setContractor(loadContractor(
		  ie.objectKey(), bs.getContractor()).
		  getPrimaryKey());
	}

	protected void        assignStore(InvoiceEdit ie, BuySell bs)
	{
		//~: check & assign the store
		ie.setTradeStore(loadStore(
		  ie.objectKey(), bs.getStore()).
		  getPrimaryKey());
	}

	protected void        mergeGoodsEdit(InvoiceEdit ie, BuySell bs)
	{
		//~: map the positions of good units of old list
		Map<Long, Integer> oldp =
		  new HashMap<Long, Integer>(bs.getGoods().size());
		for(int i = 0;(i < ie.getGoods().size());i++)
			oldp.put(ie.getGoods().get(i).getGoodUnit(), i);

		//~: form new edit lists
		List<InvoiceGoodView> res =
		  new ArrayList<InvoiceGoodView>(oldp.size());

		for(GoodSell gs : bs.getGoods())
		{
			InvoiceGoodView ge;

			//~: {that good unit exists}
			if(oldp.containsKey(gs.getPkey()))
				ge = ie.getGoods().get(oldp.get(gs.getPkey()));
			//~: create the new good edit
			else
			{
				ge = new InvoiceGoodView();

				//~: generate primary key
				BuyGood ig = new BuyGood();
				setPrimaryKey(session(), ig,
				  isTestPrimaryKey(ie.objectKey()));

				ge.setObjectKey(ig.getPrimaryKey());

				//~: good unit
				ge.init(loadGoodUnit(ie.objectKey(), gs.getPkey()));
			}

			//~: assign the sell
			ge.setGoodVolume(gs.getVolume());
			assignGoodCost(gs, ge);

			//~: assign the price list
			assignPriceList(gs, ge, ie);

			//!: add the good
			res.add(ge);
		}

		//~: set the resulting list
		ie.setGoods(res);
	}

	/**
	 * HINT: only sell operations may be
	 *   related to a Price List...
	 */
	protected void        assignPriceList
	  (GoodSell gs, InvoiceGoodView ge, InvoiceEdit ie)
	{}

	protected void        assignGoodCost(GoodSell gs, InvoiceGoodView ge)
	{
		ge.setVolumeCost(gs.getCost());
	}

	protected TradeStore  loadStore(Long ipk, Long pk)
	{
		EX.assertn(pk, "External Invoice [", ipk,
		  "] has no Trade Store reference!"
		);

		//~: load the store
		TradeStore ts = bean(GetTradeStore.class).
		  getTradeStore(pk);

		EX.assertn(ts, "External Invoice [", ipk,
		  "] refers Trade Store [", pk, "] that does not exist!"
		);

		//sec: check the domain
		checkDomain(ts);

		return ts;
	}

	protected Contractor  loadContractor(Long ipk, Long pk)
	{
		EX.assertn(pk, "External Invoice [", ipk,
		  "] has no Contractor reference!"
		);

		//~: load the store
		Contractor co = bean(GetContractor.class).getContractor(pk);

		EX.assertn(co, "External Invoice [", ipk,
		  "] refers Contractor [", pk, "] that does not exist!"
		);

		//sec: check the domain
		checkDomain(co);

		return co;
	}

	protected GoodUnit    loadGoodUnit(Long ipk, Long pk)
	{
		EX.assertn(pk, "External Invoice [", ipk,
		  "] has no Good Unit reference!"
		);

		//~: load the store
		GoodUnit gu = bean(GetGoods.class).getGoodUnit(pk);

		EX.assertn(gu, "External Invoice [", ipk,
		  "] refers Good Unit [", pk, "] that does not exist!"
		);

		//sec: check the domain
		checkDomain(gu);

		return gu;
	}

	protected PriceListEntity loadPriceList(Long ipk, Long pk)
	{
		EX.assertn(pk, "External Invoice [", ipk,
		  "] has no Price List reference!"
		);

		//~: load the store
		PriceListEntity pl = bean(GetPrices.class).getPriceList(pk);

		EX.assertn(pl, "External Invoice [", ipk,
		  "] refers Price List [", pk, "] that does not exist!"
		);

		//sec: check the domain
		checkDomain(pl);

		return pl;
	}
}