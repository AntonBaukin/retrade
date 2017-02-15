package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionsCollection.SaveViewBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core + accounts) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.account.PayBank;
import com.tverts.retrade.domain.account.PayCash;

/* com.tverts: retrade domain (documents + invoices) */

import com.tverts.retrade.domain.doc.DocumentView;
import com.tverts.retrade.domain.doc.GetDocumentView;
import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.ResGood;
import com.tverts.retrade.domain.invoice.SellGood;

/* com.tverts: retrade domain (goods + stores) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.store.StoreGood;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Actions builder to process sells operations.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSellsSession extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* action parameters */

	/**
	 * Set this parameter to update Sells Session for the
	 * Trade Store specified only.
	 *
	 * This reduces the session size.
	 */
	public static final String PARAM_STORE =
	  ActSellsSession.class.getName() + ": trade store";


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveSellsSession(abr);

		if(UPDATE.equals(actionType(abr)))
			updateSellsSession(abr);
	}


	/* protected: action methods */

	protected void saveSellsSession(ActionBuildRec abr)
	{
		//?: {target is not an Sells Session}
		checkTargetClass(abr, SellsSession.class);

		//~: do calculations
		calcPayOrder(target(abr, SellsSession.class));

		//~: save the views
		saveSessionDocumentView(abr);

		//~: create the payments
		createSellsPayments(abr);

		//~: create the invoices
		createInvoices(abr);

		//~: save the session
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the sells session unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getSessionType()
		);

		complete(abr);
	}

	protected void updateSellsSession(ActionBuildRec abr)
	{
		//?: {target is not an Sells Session}
		checkTargetClass(abr, SellsSession.class);

		//~: update the views
		updateSessionDocumentView(abr);

		//~: update the payments
		updateSellsPayments(abr);

		//~: update the invoices
		updateInvoices(abr);

		complete(abr);
	}


	/* protected: build support */

	protected UnityType    getSessionType()
	{
		return Sells.typeSellsSession();
	}

	protected void         calcPayOrder(SellsSession s)
	{
		//~: total income from all sell operations
		s.setTotalIncome(Sells.calcReceiptsIncome(s));
	}

	protected TradeStore[] findStores(ActionBuildRec abr, SellsSession s)
	{
		//?: {for distinct store only}
		TradeStore ts = param(abr, PARAM_STORE, TradeStore.class);
		if(ts != null) return new TradeStore[] { ts };

		//~: select all the stores
		Set<TradeStore> res = new HashSet<TradeStore>(7);

		for(SellReceipt sr : s.getReceipts())
			for(GoodSell gs : sr.getGoods())
				res.add(gs.getStore());

		return res.toArray(new TradeStore[res.size()]);
	}


	/* protected: sells invoices creation */

	protected void     createInvoices(ActionBuildRec abr)
	{
		//~: create sells invoices
		createSellsInvoices(abr);
	}

	protected void     createSellsInvoices(ActionBuildRec abr)
	{
		SellsSession s = target(abr, SellsSession.class);

		//c: for each trade store
		for(TradeStore ts : findStores(abr, s))
		{
			//~: create sells invoice
			Invoice i = createSellsInvoice(s, ts);

			//!: save it
			saveSellsInvoice(abr, i);
		}
	}

	protected Invoice  createSellsInvoice(SellsSession s, TradeStore ts)
	{
		Invoice i = new Invoice();

		//~: domain
		i.setDomain(s.getDomain());

		//~: code
		i.setCode(genSellsInvoiceCode(s, ts));

		//~: timestamp
		i.setInvoiceDate(s.getTime());

		//~: invoice type
		i.setInvoiceType(Sells.typeSellsInvoice());

		//~: invoice data
		SellsData d = new SellsData();

		//~: data + invoice
		d.setInvoice(i);
		i.setInvoiceData(d);

		//~: data store
		d.setStore(ts);

		//~: data sells session
		d.setSession(s);

		//~: initialize the sells data
		initSellsInvoiceData(s, ts, d);

		//~: create the invoice state
		createSellsInvoiceState(ts, i);

		return i;
	}

	protected void     saveSellsInvoice(ActionBuildRec abr, Invoice i)
	{
		xnest(abr, ActionType.SAVE, i,
		  Invoices.ORDER_TYPE, Invoices.OTYPE_INV_BUYSELL,
		  Invoices.INVOICE_STATE_TYPE, Invoices.typeInvoiceStateFixed()
		);
	}

	protected String   genSellsInvoiceCode(SellsSession s, TradeStore ts)
	{
		return SU.cats(
		  "КПРО/", s.getCode(), "/", ts.getCode()
		);
	}

	protected void     initSellsInvoiceData(SellsSession s, TradeStore ts, SellsData d)
	{
		//~: aggregated volumes by the good units
		HashMap<GoodUnit, BigDecimal> vols  =
		  new HashMap<GoodUnit, BigDecimal>(17);
		HashMap<GoodUnit, BigDecimal> costs =
		  new HashMap<GoodUnit, BigDecimal>(17);

		//c: find all the goods for that store
		for(SellReceipt sr : s.getReceipts())
			for(GoodSell g : sr.getGoods()) if(ts.equals(g.getStore()))
			{
				BigDecimal av = vols.get(g.getGoodUnit());
				BigDecimal ac = costs.get(g.getGoodUnit());

				av = (av == null)?(g.getVolume()):(av.add(g.getVolume()));
				ac = (ac == null)?(g.getCost()):(ac.add(g.getCost()));

				vols.put(g.getGoodUnit(), av);
				costs.put(g.getGoodUnit(), ac);
			}

		//~: sort the goods by the names
		ArrayList<GoodUnit> gus = new ArrayList<GoodUnit>(vols.keySet());
		Collections.sort(gus, new Comparator<GoodUnit>()
		{
			public int compare(GoodUnit a, GoodUnit b)
			{
				EX.asserts(a.getName());
				EX.asserts(b.getName());

				return a.getName().compareToIgnoreCase(b.getName());
			}
		});

		//~: create sell goods
		d.setGoods(new ArrayList<SellGood>(gus.size()));
		for(GoodUnit gu : gus)
		{
			SellGood sg = new SellGood();
			d.getGoods().add(sg);

			//~: data
			sg.setData(d);

			//~: good unit
			sg.setGoodUnit(gu);

			//~: volume
			sg.setVolume(vols.get(gu).setScale(3));

			//~: cost
			sg.setCost(costs.get(gu).setScale(5));
		}

		//~: calculate the resulting goods
		d.setResGoods(bean(GetInvoice.class).calcInvoice(d));
	}

	protected void     createSellsInvoiceState(TradeStore ts, Invoice i)
	{
		SellsData         d = (SellsData) i.getInvoiceData();

		//~: create invoice state fixed
		InvoiceStateFixed s = new InvoiceStateFixed();

		//~: invoice + state
		s.setInvoice(i);
		i.setInvoiceState(s);

		//~: create the goods list
		s.setStoreGoods(new ArrayList<StoreGood>(d.getResGoods().size()));
		for(ResGood g : d.getResGoods())
		{
			StoreGood sg = new StoreGood();
			s.getStoreGoods().add(sg);

			//~: state
			sg.setInvoiceState(s);

			//~: store
			sg.setStore(ts);

			//~: good unit
			sg.setGoodUnit(g.getGoodUnit());

			//~: volume negative (as it is for sells)
			sg.setVolumeNegative(g.getVolume());

			//?: {this good is transient} balance the volume
			if(g.getGoodCalc() != null)
				sg.setVolumePositive(g.getVolume());
		}
	}


	/* protected: sells invoices update */

	protected void     updateInvoices(ActionBuildRec abr)
	{
		//~: update sells invoices
		updateSellsInvoices(abr);
	}

	protected void     updateSellsInvoices(ActionBuildRec abr)
	{
		SellsSession             s = target(abr, SellsSession.class);
		Map<TradeStore, Invoice> m = new HashMap<TradeStore, Invoice>(7);

		//c: for each trade store
		for(TradeStore ts : findStores(abr, s))
		{
			//~: create new sells invoice
			Invoice i = createSellsInvoice(s, ts);

			m.put(ts, i);
		}

		//~: load existing sells invoices
		List<Invoice> e = bean(GetSells.class).getSellsInvoices(s);

		//~: merge invoices of the same stores
		for(Invoice now : e)
		{
			//~: take the source invoice, if provided
			Invoice src = m.get(now.getInvoiceData().getStore());
			if(src == null) continue;

			//~: add merge action
			chain(abr).first(new MergeSellsInvoice(task(abr), src, now));

			//~: un-map that store
			m.remove(now.getInvoiceData().getStore());
		}

		//~: save invoices for missed stores
		for(Invoice i : m.values())
			saveSellsInvoice(abr, i);
	}


	/* protected: sells payments creation */

	protected void     createSellsPayments(ActionBuildRec abr)
	{
		SellsSession s = target(abr, SellsSession.class);

		//~: create and save cash payment
		SellsPay     c = createCashPayment(s);
		if(c != null) saveSellsPayment(abr, c);

		//~: create and save bank payment
		SellsPay     b = createBankPayment(s);
		if(b != null) saveSellsPayment(abr, b);
	}

	protected SellsPay createCashPayment(SellsSession s)
	{
		BigDecimal c = BigDecimal.ZERO;

		//c: take all the receipts
		for(SellReceipt sr : s.getReceipts())
		{
			SellPayOp op = EX.assertn(sr.payOp());

			if(op.getPayCash() != null)
				c = c.add(op.getPayCash());
		}

		//?: {has cash payment}
		return CMP.eqZero(c)?(null):(createCashPayment(s, c));
	}

	protected SellsPay createCashPayment(SellsSession s, BigDecimal income)
	{
		//~: create & init the payment
		SellsPay p = new SellsPay();
		initSellsPayment(s, p, income);

		//~: code
		p.setCode(genSellsPaymentCode(s, "НАЛ"));

		//~: bank payment way
		p.setPaySelf(EX.assertn(
		  s.getPayDesk().getPayCash(),

		  "Sells Session is connected to Payment Desk [",
		  s.getPayDesk().getPrimaryKey(),
		  "] having no Cash (Self) Payment Way!"
		));

		return p;
	}

	protected SellsPay createBankPayment(SellsSession s)
	{
		BigDecimal b = BigDecimal.ZERO;

		//c: take all the receipts
		for(SellReceipt sr : s.getReceipts())
		{
			SellPayOp op = EX.assertn(sr.payOp());

			if(op.getPayBank() != null)
				b = b.add(op.getPayBank());
		}

		//?: {has bank payment}
		return CMP.eqZero(b)?(null):(createBankPayment(s, b));
	}

	protected SellsPay createBankPayment(SellsSession s, BigDecimal income)
	{
		//~: create & init the payment
		SellsPay p = new SellsPay();
		initSellsPayment(s, p, income);

		//~: code
		p.setCode(genSellsPaymentCode(s, "БАНК"));

		//~: bank payment way
		p.setPaySelf(EX.assertn(
		  s.getPayDesk().getPayBank(),

		  "Sells Session is connected to Payment Desk [",
		  s.getPayDesk().getPrimaryKey(),
		  "] having no Bank (Self) Payment Way!"
		));

		return p;
	}

	protected void     saveSellsPayment(ActionBuildRec abr, SellsPay pay)
	{
		//~: save the payment with auto-ordering by the timestamp
		xnest(abr, ActionType.SAVE, pay,
		  ActSellsPay.ORDER_AUTO, true
		);
	}

	protected void     initSellsPayment(SellsSession s, SellsPay pay, BigDecimal income)
	{
		//~: domain
		pay.setDomain(s.getDomain());

		//~: payment order
		pay.setPayOrder(s);

		//~: time (of the session open)
		pay.setTime(s.getTime());

		//~: income
		pay.setIncome(income.setScale(2, BigDecimal.ROUND_UP));
	}

	protected String   genSellsPaymentCode(SellsSession s, String op)
	{
		return SU.cats("КПРО/", s.getCode(), SU.catif(op, '/', op));
	}

	protected void     updateSellsPayments(ActionBuildRec abr)
	{
		SellsSession s = target(abr, SellsSession.class);

		//~: search for the existing payments
		SellsPay cnow = null;
		SellsPay bnow = null;

		for(SellsPay p : bean(GetSells.class).getSellsPayments(s))
		{
			Class cls = HiberPoint.type(p.getPaySelf().getPayWay());

			//?: {cash payment}
			if(PayCash.class.isAssignableFrom(cls))
				cnow = p;

			//?: {bank payment}
			if(PayBank.class.isAssignableFrom(cls))
				bnow = p;
		}

		//~: create cash and bank payments
		SellsPay csrc = createCashPayment(s);
		SellsPay bsrc = createBankPayment(s);

		//?: {has cash payment} update it
		if(cnow != null)
			chain(abr).first(new UpdateSellsPay(task(abr), EX.assertn(csrc), cnow));
		//~: save it
		else if(csrc != null)
			saveSellsPayment(abr, csrc);

		//?: {has bank payment} update it
		if(bnow != null)
			chain(abr).first(new UpdateSellsPay(task(abr), EX.assertn(bsrc), bnow));
		//~: save it
		else if(bsrc != null)
			saveSellsPayment(abr, bsrc);
	}


	/* protected: sells session view */

	protected void     saveSessionDocumentView(ActionBuildRec abr)
	{
		SellsSession s = target(abr, SellsSession.class);
		DocumentView v = new DocumentView();

		//~: document code
		v.setDocCode(s.getCode());

		//~: document date
		v.setDocDate(s.getTime());

		//~: document cost
		v.setDocCost(s.getTotalIncome());

		//!: do save it
		chain(abr).first(new SaveViewBase(task(abr), v, s));
	}

	protected void     updateSessionDocumentView(ActionBuildRec abr)
	{
		chain(abr).first(new UpdateSessionDocumentView(task(abr)));
	}


	/* protected: merge & update actions */

	protected static class MergeSellsInvoice extends ActionWithTxBase
	{
		/* public: constructor */

		public MergeSellsInvoice(ActionTask task, Invoice src, Invoice now)
		{
			super(task);
			this.src = src;
			this.now = now;
		}


		/* public: Action interface */

		public Invoice getResult()
		{
			return now;
		}


		/* protected: ActionBase interface */

		protected void execute()
		{
			throw EX.unop();
		}


		/* private: source and destination invoices */

		private Invoice src;
		private Invoice now;
	}


	protected static class UpdateSellsPay extends ActionWithTxBase
	{
		/* public: constructor */

		public UpdateSellsPay(ActionTask task, SellsPay src, SellsPay now)
		{
			super(task);
			this.src = src;
			this.now = now;
		}


		/* public: Action interface */

		public SellsPay getResult()
		{
			return now;
		}


		/* protected: ActionBase interface */

		protected void execute()
		{
			//~: update income only
			now.setIncome(src.getIncome());
		}


		/* private: source and destination invoices */

		private SellsPay src;
		private SellsPay now;
	}


	protected class UpdateSessionDocumentView extends ActionWithTxBase
	{
		/* public: constructor */

		public UpdateSessionDocumentView(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public SellsSession getResult()
		{
			return target(SellsSession.class);
		}


		/* protected: ActionBase interface */

		protected void execute()
		{
			SellsSession s = target(SellsSession.class);
			DocumentView v = bean(GetDocumentView.class).
			  findDocumentView(s.getPrimaryKey());

			EX.assertn(v, "Sells Session [", s.getPrimaryKey(),
			  "] has no Document View!");

			//~: document code
			v.setDocCode(s.getCode());

			//~: document date
			v.setDocDate(s.getTime());

			//!: recalculate the payment order income
			calcPayOrder(s);

			//~: document cost
			v.setDocCost(s.getTotalIncome());
		}
	}
}