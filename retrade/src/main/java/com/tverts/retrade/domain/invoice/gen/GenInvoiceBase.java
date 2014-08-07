package com.tverts.retrade.domain.invoice.gen;

/* standard Java class */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.DaysGenDisp;
import com.tverts.genesis.DaysGenPart;
import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade domain (goods + prices + trade stores) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Generates one instance of {@link Invoice} class.
 * Supports ONLY {@link DaysGenDisp} generators.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GenInvoiceBase
       extends        GenesisHiberPartBase
       implements     DaysGenPart
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		Invoice invoice = new Invoice();

		//~: set test primary key
		setPrimaryKey(session(), invoice, true);

		//~: assign test domain
		invoice.setDomain(ctx.get(Domain.class));

		//~: invoice timestamp
		invoice.setInvoiceDate(
		  ctx.get(DaysGenDisp.TIME, Date.class));

		//HINT: the code is generated in the Data creation.

		//~: generate invoice data
		genData(ctx, invoice);

		//~: create the state of the invoice
		genState(ctx, invoice);

		//~: save it
		save(ctx, invoice);
	}


	/* public: DaysGenPart interface */

	public boolean      isDayClear(GenCtx ctx)
	{
		return super.isGenDispDayClear(ctx, getDayMarkType());
	}

	public void         markDayGenerated(GenCtx ctx)
	{
		super.markGenDispDay(ctx, getDayMarkType());
	}

	protected UnityType getDayMarkType()
	{
		return UnityTypes.unityType(Invoice.class, getInvoiceType());
	}


	/* public: GenInvoiceBase (bean) interface */

	public String getInvoiceType()
	{
		return invoiceType;
	}

	public void   setInvoiceType(String invoiceType)
	{
		if((invoiceType = s2s(invoiceType)) == null)
			throw new IllegalArgumentException();

		this.invoiceType = invoiceType;
	}

	public String getOrderType()
	{
		return orderType;
	}

	public void   setOrderType(String orderType)
	{
		this.orderType = orderType;
	}

	@Param
	public int    getGoodsMax()
	{
		return goodsMax;
	}

	public void   setGoodsMax(int v)
	{
		EX.assertx(v > 1);
		this.goodsMax = v;
	}

	@Param
	public int    getVolumeMin()
	{
		return volumeMin;
	}

	public void   setVolumeMin(int v)
	{
		EX.assertx(v > 0);
		this.volumeMin = v;
	}

	@Param
	public int    getVolumeMax()
	{
		return volumeMax;
	}

	public void   setVolumeMax(int v)
	{
		EX.assertx(v > 0);
		this.volumeMax = v;
	}


	/* protected: abstraction interface */

	protected abstract InvoiceData createInvoiceData(GenCtx ctx);

	protected abstract InvGood     createGood();

	protected abstract void        assignGood(GenCtx ctx, InvGood good);


	/* protected: invoice generation */

	protected UnityType     invoiceType()
	{
		return UnityTypes.unityType(Invoice.class, getInvoiceType());
	}

	protected void          genData(GenCtx ctx, Invoice invoice)
	{
		InvoiceData data = createInvoiceData(ctx);
		invoice.setInvoiceData(data);

		//~: data invoice
		data.setInvoice(invoice);

		//~: trade store
		assignStore(ctx, data);

		//~: generate invoice code
		invoice.setCode(genInvoiceCode(ctx, invoice));

		//~: generate the goods
		genGoods(ctx, invoice.getInvoiceData());

		//~: generate resulting (altered) goods
		genResGoods(ctx, invoice.getInvoiceData());
	}

	protected void          genState(GenCtx ctx, Invoice invoice)
	  throws GenesisError
	{
		//~: create the instance
		InvoiceState state = new InvoiceState();

		//~: state primary key
		setPrimaryKey(session(), state, true);

		//~: assign the state
		invoice.setInvoiceState(state);
	}

	protected UnityType     stateType()
	{
		return Invoices.typeInvoiceStateEdited();
	}

	protected void          assignStore(GenCtx ctx, InvoiceData data)
	{
		TradeStore[] tss = ctx.get(TradeStore[].class);
		data.setStore(tss[ctx.gen().nextInt(tss.length)]);
	}

	protected String        genInvoiceCode(GenCtx ctx, Invoice invoice)
	{
		return Invoices.createInvoiceCode(
		  invoiceType(), ctx.get(DaysGenDisp.DAY, Date.class),
		  ctx.get(DaysGenDisp.DAYI, Integer.class),
		  invoice.getInvoiceData()
		);
	}

	protected void          save(GenCtx ctx, Invoice invoice)
	{
		//~: create save parameters
		HashMap ps = new HashMap(7);
		createSaveParams(ctx, invoice, ps);

		//!: run standard save action
		actionRun(Invoices.ACT_SAVE, invoice, ps);

		//~: flush & clean the session
		HiberPoint.flush(session(), HiberPoint.CLEAR);
	}

	@SuppressWarnings("unchecked")
	protected void          createSaveParams(GenCtx ctx, Invoice invoice, Map ps)
	{
		//~: invoice type
		ps.put(Invoices.INVOICE_TYPE, invoiceType());

		//?: {has no order type}
		if(sXe(getOrderType()))
			ps.put(Invoices.ORDER_NOT, true);
		else
			ps.put(Invoices.ORDER_TYPE, getOrderType());

		//~: invoice state type
		ps.put(Invoices.INVOICE_STATE_TYPE, stateType());

		//~: test contractor
		Contractor co = selectTestContractor(ctx, invoice);
		if(co != null) ps.put(Invoices.INVOICE_CONTRACTOR, co);
	}

	private BigDecimal      genDecimal(GenCtx ctx, int min, int max, int frmax)
	{
		return new BigDecimal(
		  String.valueOf(min + ctx.gen().nextInt(max - min)) +
		  '.' + ctx.gen().nextInt(frmax)
		);
	}

	protected Contractor    selectTestContractor(GenCtx ctx, Invoice invoice)
	{
		GetContractor gc = bean(GetContractor.class);
		int           cc = gc.countTestContractors(invoice.getDomain());
		EX.assertx(cc != 0, "No test Contractors are found in the Domain!");

		return EX.assertn(gc.getTestContractor(
		  invoice.getDomain(), ctx.gen().nextInt(cc)));
	}

	protected static final String CTX_GOOD_PRICES_PREFIX =
	  GenInvoiceBase.class.getName() + ": good prices: ";

	protected GoodPrice     selectGoodPrice(GenCtx ctx, InvGood g)
	{
		Long        gu = g.getGoodUnit().getPrimaryKey();
		GoodPrice[] ps = (GoodPrice[]) ctx.get(CTX_GOOD_PRICES_PREFIX + gu);

		//?: select the prices
		if(ps == null)
		{
			List<GoodPrice> pl = bean(GetGoods.class).getGoodPrices(gu);
			EX.asserte(pl, "Good Unit [", gu, "] has no prices!");
			ps = pl.toArray(new GoodPrice[pl.size()]);
			ctx.set(CTX_GOOD_PRICES_PREFIX + gu, ps);
		}

		return (ps.length == 0)?(null):
		  ps[ctx.gen().nextInt(ps.length)];
	}

	protected static final String CTX_GOODS_WITH_PRICES =
	  GenInvoiceBase.class.getName() + ": goods with prices";

	/**
	 * Selects the Good Units having prices (in any
	 * Price List), and are not purely produced goods
	 * (not a semi-ready products).
	 */
	@SuppressWarnings("unchecked")
	public static void      retainGoodsWithPrices
	  (GenCtx ctx, Collection<GoodUnit> goods)
	{
		Set<Long> ids = (Set<Long>) ctx.get(CTX_GOODS_WITH_PRICES);

		if(ids == null) ctx.set(CTX_GOODS_WITH_PRICES, ids =
			  new HashSet<Long>(bean(GetGoods.class).
			   getGoodsWithPrices(ctx.get(Domain.class).getPrimaryKey())
			));

		for(Iterator<GoodUnit> i = goods.iterator();(i.hasNext());)
		{
			GoodUnit gu = i.next();

			//?: {good has no price}
			if(!ids.contains(gu.getPrimaryKey()))
				i.remove();
			//?: {good is a product}
			else if(gu.getGoodCalc() != null)
				//?: {and it is not semi-ready}
				if(!gu.getGoodCalc().isSemiReady())
					i.remove();
		}
	}


	/* protected: goods generation */

	protected GoodUnit[]    selectGoods(GenCtx ctx, InvoiceData data)
	{
		return ctx.get(GoodUnit[].class);
	}

	@SuppressWarnings("unchecked")
	protected void          genGoods(GenCtx ctx, InvoiceData data)
	{
		GoodUnit[] gunits = selectGoods(ctx, data);
		EX.asserte(gunits, "Good Units selection for Invoice is empty!");

		int        gcount = 1 + ctx.gen().nextInt(
		  (gunits.length < getGoodsMax())?(gunits.length):(getGoodsMax()));

		//~: create good unit selection indices
		ArrayList<Integer> selind = new ArrayList<Integer>(gunits.length);
		for(int i = 0;(i < gunits.length);i++) selind.add(i);
		Collections.shuffle(selind, ctx.gen());

		//~: create the goods of the number
		for(int i = 0;(i < gcount);i++)
		{
			//~: create good instance
			InvGood good = createGood();

			//~: primary key
			setPrimaryKey(session(), good, true);

			//~: data
			good.setData(data);

			//~: set the good unit   //<-- random selection by the shuffled indices
			good.setGoodUnit(gunits[selind.get(i)]);

			//~: good volume
			good.setVolume(createGoodVolume(ctx, good));

			//~: assign the good
			assignGood(ctx, good);

			//!: add the good to the data
			((List<InvGood>)data.getGoods()).add(good);
		}
	}

	protected void          genResGoods(GenCtx ctx, InvoiceData data)
	{}

	protected BigDecimal    createGoodVolume(GenCtx ctx, InvGood good)
	{
		int v = volumeMin +
		  ctx.gen().nextInt(volumeMax - volumeMin);

		if(good.getGoodUnit().getMeasure().getOx().isFractional())
			return new BigDecimal("" + v + '.' + ctx.gen().nextInt(1000)).setScale(3);
		return BigDecimal.valueOf(v);
	}


	/* private: parameters of the generator */

	private String invoiceType;
	private String orderType;

	private int    goodsMax  = 10;
	private int    volumeMin = 1;
	private int    volumeMax = 100;
}