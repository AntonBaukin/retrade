package com.tverts.retrade.exec.api.invoices;

/* Java */

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (*) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.SellGood;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: api */

import com.tverts.api.retrade.document.Buy;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.goods.GoodSell;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Implementation base for executor to insert new
 * {@link Invoice}s of various Unity Types from
 * the given API {@link Buy} instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class InsertInvoiceBase extends InsertEntityBase
{
	/* protected: abstraction interface */

	protected abstract String      getInvoiceTypeName();

	protected abstract InvoiceData createInvoiceData();

	protected abstract InvGood     createGood();


	/* protected: InsertEntityBase interface */

	protected Long        insert(Object source)
	{
		BuySell b = (BuySell) source;
		Invoice i = new Invoice();

		//~: primary key
		setPrimaryKey(session(), i, isTestInstance(domain()));

		//~: domain
		i.setDomain(domain());

		//~: code
		i.setCode(b.getCode());

		//~: timestamp
		i.setInvoiceDate(b.getTime());

		//~: remarks
		i.setRemarks(b.getRemarks());

		//~: create the data
		createData(i, b);

		//~: create the state
		createState(i, b);

		//~: do save
		save(i, b);

		//~: fix the invoice
		fixInvoice(i, b);

		//!: flush the session
		//HiberPoint.flush(session(), HiberPoint.CLEAR);

		return i.getPrimaryKey();
	}


	/* protected: save operation support */

	protected void        save(Invoice i, BuySell bs)
	{
		ActionsPoint.actionRun(ActionType.SAVE, i, createSaveParams(i, bs));
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

	protected UnityType   getInvoiceType()
	{
		return UnityTypes.unityType(Invoice.class, getInvoiceTypeName());
	}

	protected String      getInvoiceOrderType()
	{
		return Invoices.OTYPE_INV_BUYSELL;
	}

	protected UnityType   getInvoiceStateType()
	{
		return Invoices.typeInvoiceStateEdited();
	}

	@SuppressWarnings("unchecked")
	protected Map         createSaveParams(Invoice i, BuySell bs)
	{
		Map p = new HashMap(11);

		//~: invoice type
		p.put(Invoices.INVOICE_TYPE, getInvoiceType());

		//?: {has no order type}
		if(getInvoiceOrderType() == null)
			p.put(Invoices.ORDER_NOT, true);
		else
			p.put(Invoices.ORDER_TYPE, getInvoiceOrderType());

		//~: state type
		p.put(Invoices.INVOICE_STATE_TYPE, getInvoiceStateType());

		//~: the contractor
		assignContractor(bs, p);

		return p;
	}

	@SuppressWarnings("unchecked")
	protected void        assignContractor(BuySell bs, Map params)
	{
		//~: load the contractor
		params.put(Invoices.INVOICE_CONTRACTOR,
		  loadContractor(bs.getXkey(), bs.getXContractor(), bs.getContractor()));
	}


	/* protected: state and data creation */

	protected void        createData(Invoice i, BuySell bs)
	{
		InvoiceData d = createInvoiceData();

		//~: invoice + data
		i.setInvoiceData(d);
		d.setInvoice(i);

		//~: assign the store
		assignStore(d, bs);

		//~: copy the goods
		copyGoods(d, bs);
	}

	protected void        createState(Invoice i, BuySell bs)
	{
		InvoiceState s = new InvoiceState();

		//~: invoice + state
		i.setInvoiceState(s);
		s.setInvoice(i);
	}

	protected void        assignStore(InvoiceData d, BuySell bs)
	{
		//~: set the destination store
		d.setStore(loadStore(bs.getXkey(), bs.getXStore(), bs.getStore()));
	}


	/* protected: goods copying */

	@SuppressWarnings("unchecked")
	protected void copyGoods(InvoiceData d, BuySell bs)
	{
		//c: for all the goods of the income
		for(GoodSell gs : bs.getGoods())
		{
			InvGood ig = createGood();

			//~: primary key
			setPrimaryKey(session(), ig,
			  isTestInstance(d.getInvoice()));

			//~: assign the good
			copyGood(bs, ig, gs);

			//!: add it
			((List<InvGood>)d.getGoods()).add(ig);
		}
	}

	protected void copyGood(BuySell bs, InvGood ig, GoodSell gs)
	{
		//~: good unit
		ig.setGoodUnit(loadGoodUnit(
		  bs.getXkey(), gs.getXGood(), gs.getGood()));

		//~: volume
		assignGoodVolume(bs.getXkey(), ig, gs);

		//?: {buy invoice}
		if(ig instanceof BuyGood)
			copyGood(bs, (BuyGood)ig, gs);

		//?: {sell invoice}
		if(ig instanceof SellGood)
			copyGood(bs, (SellGood)ig, gs);
	}

	protected void copyGood(BuySell bs, BuyGood ig, GoodSell gs)
	{
		//~: good cost
		ig.setCost(copyGoodCost(bs.getXkey(), gs));
	}

	protected void copyGood(BuySell bs, SellGood ig, GoodSell gs)
	{
		//~: good cost
		ig.setCost(copyGoodCost(bs.getXkey(), gs));

		//~: price list
		assignPriceList(bs.getXkey(), ig, gs);
	}

	protected void        assignGoodVolume(String ixkey, InvGood ig, GoodSell gs)
	{
		//?: {volume is undefined}
		EX.assertn(gs.getVolume(),

		  "Invoice to insert xkey [", ixkey, "] good xkey [", gs.getXkey(),
		  "] pkey [", gs.getPkey(), "] has no volume value!"
		);

		//?: {volume has wrong value}
		try
		{
			if(!CMP.grZero(gs.getVolume()))
				throw new IllegalArgumentException();

			//~: check the scale
			gs.setVolume(gs.getVolume().
			  setScale(3, BigDecimal.ROUND_UNNECESSARY));
		}
		catch(Exception e)
		{
			throw EX.arg("Invoice to insert xkey [", ixkey,
			  "] good xkey [", gs.getXkey(), "] pkey [", gs.getPkey(),
			  "] has has wrong volume value!"
			);
		}

		//~: set the volume (& check the scale)
		ig.setVolume(gs.getVolume());
	}

	protected BigDecimal  copyGoodCost(String ixkey, GoodSell gs)
	{
		BigDecimal cost = gs.getCost();

		//?: {cost is undefined}
		EX.assertn(cost, "Invoice to insert xkey [", ixkey, "] good xkey [",
		  gs.getXGood(), "] pkey [", gs.getGood(), "] has no (volume) cost value!"
		);

		//?: {cost has wrong value}
		try
		{
			//HINT: zero cost is assumed as 'gift': volume correction
			//  made via fake buy operations, not good, but exists...
			EX.assertx(CMP.greZero(cost));

			//~: check the scale
			cost = cost.setScale(5, BigDecimal.ROUND_UNNECESSARY);
		}
		catch(Exception e)
		{
			throw EX.arg("Invoice to insert xkey [", ixkey,
			  "] good xkey [", gs.getXkey(), "] pkey [", gs.getPkey(),
			  "] has has wrong (volume) cost value!"
			);
		}

		//~: set the volume (& check the scale)
		return cost;
	}

	protected void        assignPriceList(String ixkey, SellGood ig, GoodSell gs)
	{
		//?: {good is not sold by the price list}
		if(gs.getList() == null)
			return;

		//=: load the price list
		ig.setPriceList(loadPriceList(
		  ixkey, gs.getXkey(), gs.getXList(), gs.getList()));
	}


	/* protected: entities loading */

	protected TradeStore  loadStore(String ixkey, String sxkey, Long pk)
	{
		EX.assertn(pk, "Invoice to insert xkey [", ixkey,
		 "] has no Trade Store reference!"
		);

		//~: load the store
		TradeStore ts = bean(GetTradeStore.class).
		  getTradeStore(pk);

		EX.assertn(ts, "Invoice to insert xkey [", ixkey,
		  "] refers Trade Store xkey [", sxkey, "] pkey [", pk,
		  "] that doesn't exist!"
		);

		//sec: check the domain
		checkDomain(ts);

		return ts;
	}

	protected Contractor  loadContractor(String ixkey, String cxkey, Long pk)
	{
		EX.assertn(pk, "Invoice to insert xkey [", ixkey,
		 "] has no Contractor reference!"
		);

		//~: load the contractor
		Contractor co = bean(GetContractor.class).
		  getContractor(pk);

		EX.assertn(co, "Invoice to insert xkey [", ixkey,
		  "] refers Contractor xkey [", cxkey, "] pkey [", pk,
		  "] that doesn't exist!"
		);

		//sec: check the domain
		checkDomain(co);

		return co;
	}

	protected GoodUnit    loadGoodUnit(String ixkey, String gxkey, Long pk)
	{
		EX.assertn(pk, "Invoice to insert xkey [", ixkey,
		 "] has no Good Unit xkey [", gxkey, "] reference!"
		);

		//~: load the contractor
		GoodUnit gu = bean(GetGoods.class).getGoodUnit(pk);

		EX.assertn(gu, "Invoice to insert xkey [", ixkey,
		  "] refers Good Unit xkey [", gxkey, "] pkey [", pk,
		  "] that doesn't exist!"
		);

		//sec: check the domain
		checkDomain(gu);

		return gu;
	}

	protected PriceListEntity loadPriceList
	  (String ixkey, String gxkey, String pxkey, Long pk)
	{
		EX.assertn(pk, "Invoice to insert xkey [", ixkey,
		 "] has no Price List reference!"
		);

		//~: load the store
		PriceListEntity pl = bean(GetPrices.class).getPriceList(pk);

		EX.assertn(pl, "Invoice to insert xkey [", ixkey,
		  "] refers Price List xkey [", gxkey, "] pkey [", pk,
		  "] that doesn't exist!"
		);

		//sec: check the domain
		checkDomain(pl);

		return pl;
	}
}