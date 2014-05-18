package com.tverts.retrade.exec.api.invoices;

/* standard Java classes */

import java.util.ArrayList;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: retrade domain (firms + payments + store) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.SellGood;
import com.tverts.retrade.domain.payment.GetInvoiceBill;


/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade api */

import com.tverts.api.retrade.document.Buy;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.document.Move;
import com.tverts.api.retrade.document.Sell;
import com.tverts.api.retrade.goods.GoodSell;

/* com.tverts: execution (api) */

import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps Byt, Sell, and Move Invoices.
 *
 * The Unity Type must be defined!
 *
 * @author anton.baukin@gmail.com
 */
public class DumpInvoices extends EntitiesDumperBase
{
	/* protected: EntitiesDumperBase interface */

	protected Object   createApiEntity(Object src)
	{
		if(Invoices.TYPE_INVOICE_BUY.equals(getUnityType()))
			return createBuyInvoice((Invoice)src);

		if(Invoices.TYPE_INVOICE_SELL.equals(getUnityType()))
			return createSellInvoice((Invoice)src);

		if(Invoices.TYPE_INVOICE_MOVE.equals(getUnityType()))
			return createMoveInvoice((Invoice)src);

		throw EX.state();
	}

	protected Class    getUnityClass()
	{
		return Invoice.class;
	}

	protected Class    getEntityClass()
	{
		if(Invoices.TYPE_INVOICE_BUY.equals(getUnityType()))
			return Buy.class;

		if(Invoices.TYPE_INVOICE_SELL.equals(getUnityType()))
			return Sell.class;

		if(Invoices.TYPE_INVOICE_MOVE.equals(getUnityType()))
			return Move.class;

		throw EX.state();
	}


	/* protected: entities creation */

	protected Object   createBuyInvoice(Invoice i)
	{
		return init(new Buy(), i);
	}

	protected Object   createSellInvoice(Invoice i)
	{
		return init(new Sell(), i);
	}

	protected Object   createMoveInvoice(Invoice i)
	{
		return init(new Move(), i);
	}

	protected Object   init(BuySell bs, Invoice i)
	{
		//~: primary key
		bs.setPkey(i.getPrimaryKey());

		//~: transaction number
		bs.setTx(i.getTxn());

		//~: code
		bs.setCode(i.getCode());

		//~: fixed?
		bs.setFixed(Invoices.isInvoiceFixed(i));

		//~: document time
		bs.setTime(i.getInvoiceDate());

		//~: remarks
		bs.setRemarks(i.getRemarks());

		//~: init the contractor
		initContractor(bs, i);

		//~: init the stores
		initStores(bs, i);

		//~: init the goods
		initGoods(bs, i);

		return bs;
	}

	protected void     initContractor(BuySell bs, Invoice i)
	{
		//?: {move invoice (has no contractor)}
		if(bs instanceof Move)
			return;

		//~: load the contractor
		Contractor c = bean(GetInvoiceBill.class).
		  getInvoiceBillContractor(i);

		if(c != null)
			bs.setContractor(c.getPrimaryKey());
	}

	protected void     initStores(BuySell bs, Invoice i)
	{
		//~: the main (destination) store
		bs.setStore((i.getInvoiceData()).
		  getStore().getPrimaryKey());

		//?: {move invoice} the source store
		if(bs instanceof Move)
			((Move)bs).setSource(((MoveData)i.getInvoiceData()).
			  getSourceStore().getPrimaryKey());
	}

	protected void     initGoods(BuySell bs, Invoice i)
	{
		//~: create destination goods list
		bs.setGoods(new ArrayList<GoodSell>(
		  i.getInvoiceData().getGoods().size()
		));

		//c: for all invoice source goods
		for(InvGood ig : i.getInvoiceData().getGoods())
			bs.getGoods().add(copyGood(ig));
	}

	protected GoodSell copyGood(InvGood ig)
	{
		GoodSell gs = new GoodSell();

		//~: good
		gs.setGood(ig.getGoodUnit().getPrimaryKey());

		//~: good volume
		gs.setVolume(ig.getVolume());

		//?: {buy good}
		if(ig instanceof BuyGood)
			return this.copyGood((BuyGood)ig, gs);

		//?: {sell good}
		if(ig instanceof SellGood)
			return this.copyGood((SellGood)ig, gs);

		return gs;
	}

	protected GoodSell copyGood(BuyGood ig, GoodSell gs)
	{
		//~: good cost
		gs.setCost(ig.getCost());

		return gs;
	}

	protected GoodSell copyGood(SellGood ig, GoodSell gs)
	{
		//~: good cost
		gs.setCost(ig.getCost());

		//~: good price
		if(ig.getPrice() != null)
			gs.setList(ig.getPrice().getPriceList().getPrimaryKey());

		//HINT: we do not copy actual price as it
		//  may be not the same as at the document time!

		return gs;
	}
}