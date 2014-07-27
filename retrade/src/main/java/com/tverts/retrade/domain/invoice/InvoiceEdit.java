package com.tverts.retrade.domain.invoice;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SetOrderIndex;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.core.GetUnityType;

/* com.tverts: retrade domain (goods + prices + sells + stores) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.sells.SellsData;
import com.tverts.retrade.domain.store.GetTradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Edit version of {@link InvoiceView} data bean.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "invoice")
public class InvoiceEdit extends InvoiceViewExt
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceEdit (bean) interface */

	/**
	 * We see the invoice timestamp as the value
	 * is being edited.
	 */
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getInvoiceDate()
	{
		return (getEditDate() != null)
		  ?(getEditDate()):(super.getInvoiceDate());
	}

	public Date getInvoiceDateOriginal()
	{
		return originalDate;
	}


	public Long getOrderType()
	{
		return orderType;
	}

	public void setOrderType(Long orderType)
	{
		this.orderType = orderType;
	}

	public void setOrderType(UnityType ot)
	{
		this.orderType = (ot == null)?(null):(ot.getPrimaryKey());
	}

	public Long getOrderReference()
	{
		return orderReference;
	}

	public void setOrderReference(Long orderReference)
	{
		this.orderReference = orderReference;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getEditDate()
	{
		return (editDate != null)?(editDate):
		  (super.getInvoiceDate());
	}

	public void setEditDate(Date editDate)
	{
		this.editDate = editDate;
	}


	/* public: InvoiceEdit (support) interface */

	public InvoiceEdit init(Object obj)
	{
		return (InvoiceEdit) super.init(obj);
	}

	public InvoiceEdit init(Invoice i)
	{
		super.init(i);

		//~: order type
		if(getOrderType() != null)
			i.setOrderType(bean(GetUnityType.class).
			  getUnityType(getOrderType()));

		//~: edit & original date
		this.editDate     = getInvoiceDate();
		this.originalDate = new Date(this.editDate.getTime());

		return this;
	}

	public InvoiceEdit recalcGoodsCost()
	{
		BigDecimal summ = BigDecimal.ZERO;

		if(getGoods() != null)
			for(InvoiceGoodView g : getGoods())
				if(g.getVolumeCost() != null)
					summ = summ.add(g.getVolumeCost());

		this.setGoodsCost(summ);
		return this;
	}


	/* public: InvoiceEdit (create) interface */

	public Invoice createInvoice()
	{
		Invoice      i = new Invoice();
		InvoiceState s = new InvoiceState();
		InvoiceData  d = createData();


		//?: {move invoice}
		Long m = UnityTypes.unityType(Invoice.class,
		  Invoices.TYPE_INVOICE_MOVE).getPrimaryKey();
		if(m.equals(getInvoiceType()))
			d = new MoveData();

		//~: state <-> invoice
		i.setInvoiceState(s);
		s.setInvoice(i);

		//~: data <-> invoice
		d.setInvoice(i);
		i.setInvoiceData(d);

		//~: altered invoice
		d.setSubType(getSubType());

		//~: domain
		i.setDomain(bean(GetDomain.class).
		  getDomain(getDomain()));

		//~: insert goods
		insertNewGoods(i);

		//~: assign goods
		assignGoods(i);

		//~: assign invoice properties
		assignProps(i);

		//~: set order type
		if(getOrderType() != null)
			i.setOrderType(bean(GetUnityType.class).
			  getUnityType(getOrderType()));

		return i;
	}

	public void    assignProps(Invoice i)
	{
		InvoiceEdit e = this;
		InvoiceData d = i.getInvoiceData();

		//~: invoice code
		EX.assertx(!SU.sXe(e.getInvoiceCode()));
		i.setCode(e.getInvoiceCode());

		//~: invoice date
		i.setInvoiceDate(EX.assertn(e.getInvoiceDate()));

		//~: remarks
		i.setRemarks(e.getRemarks());

		//~: altered invoice
		d.setSubType(getSubType());

		//~: trade (destination) store
		EX.assertn(e.getTradeStore());
		d.setStore(bean(GetTradeStore.class).getTradeStore(e.getTradeStore()));
		EX.assertn(d.getStore());

		//~: trade (source) store
		if(d instanceof MoveData)
			//?: {altered move means production or correction}
			if(d.isAltered())
				((MoveData)d).setSourceStore(d.getStore());
			//~: {invoice is regular move}
			else
			{
				EX.assertn(e.getTradeStoreSource());

				((MoveData)d).setSourceStore(EX.assertn(
				  bean(GetTradeStore.class).getTradeStore(e.getTradeStoreSource())
				));
			}
	}

	/**
	 * Removes the Invoice Data goods that are not
	 * in the edit model now. Returns them.
	 */
	@SuppressWarnings("unchecked")
	public List<InvGood>
	               removeObsoleteGoods(Invoice i)
	{
		InvoiceEdit e = this;
		InvoiceData d = i.getInvoiceData();

		//~: get the keys of all the goods present
		HashSet<Long>     k = new HashSet<Long>(11);

		for(InvoiceGoodView g : e.getGoods())
			k.add(g.getObjectKey());

		//~: copy only the present goods
		List<InvGood> l = new ArrayList<InvGood>(k.size());
		List<InvGood> r = new ArrayList<InvGood>(k.size());

		for(InvGood g : d.getGoods())
			if(k.contains(g.getPrimaryKey()))
				l.add(g);
			//!: delete this invoice good
			else
				r.add(g);

		//~: assign the goods left
		d.getGoods().clear();
		((List<InvGood>)d.getGoods()).addAll(l);

		return r;
	}

	@SuppressWarnings("unchecked")
	public void    insertNewGoods(Invoice i)
	{
		InvoiceEdit           e  = this;
		InvoiceData           d  = i.getInvoiceData();
		List<InvGood>         ig = (List<InvGood>)d.getGoods();
		List<InvoiceGoodView> eg = e.getGoods();
		List<InvGood>         re = new ArrayList<InvGood>(eg.size());

		//c: for all the edit goods
		for(int j = 0;(j < eg.size());j++)
		{
			InvGood         iG = (j < ig.size())?(ig.get(j)):(null);
			InvoiceGoodView eG = eg.get(j);

			//?: {good edit has no key}
			EX.assertn(eG.getObjectKey());

			//?: {the good is in the proper position} just place it
			if((iG != null) && eG.getObjectKey().equals(iG.getPrimaryKey()))
			{
				re.add(iG);
				continue;
			}

			//~: search for the good
			iG = null; //<-- will find...
			for(InvGood g : ig)
				if(eG.getObjectKey().equals(g.getPrimaryKey()))
				{
					iG = g;
					break;
				}

			//?: {had found it}
			if(iG != null)
			{
				re.add(iG);
				continue;
			}

			//!: create the new InvGood (fill it lately)
			iG = createGood(i);

			iG.setPrimaryKey(eG.getObjectKey());
			iG.setData(d);

			re.add(iG);
		}

		//?: {something wrong with the size}
		EX.assertx(re.size() == eg.size());

		//!: assign the goods to the data
		d.getGoods().clear();
		((List<InvGood>)d.getGoods()).addAll(re);
	}

	@SuppressWarnings("unchecked")
	public void    assignGoods(Invoice i)
	{
		InvoiceEdit           e  = this;
		InvoiceData           d  = i.getInvoiceData();
		List<InvGood>         ig = (List<InvGood>)d.getGoods();
		List<InvoiceGoodView> eg = e.getGoods();

		//?: {something wrong with the size}
		EX.assertx(ig.size() == eg.size());

		//c: for all the goods
		for(int j = 0;(j < eg.size());j++)
			assignGood(ig.get(j), eg.get(j));
	}

	public Action  createOrderAction(ActionBuildRec abr, Invoice i)
	{
		EX.assertn(getInvoiceDate());

		//?: {the invoice timestamp is the same} do nothing
		if(CMP.eq(i.getInvoiceDate(), getInvoiceDate()))
			if(!((Long)Long.MAX_VALUE).equals(i.getOrderIndex()))
				return null;

		//~: get the order type
		UnityType ot = (getOrderType() == null)?(i.getOrderType()):
		  (bean(GetUnityType.class).getUnityType(getOrderType()));
		EX.assertn(ot);

		//~: lookup invoices on the left
		List<Invoice> l = bean(GetInvoice.class).findLeftInvoices(
		  i.getDomain(), ot, getInvoiceDate(), 1,
		  (i.getPrimaryKey() == null)?(null):
		    Collections.singleton(i.getPrimaryKey())
		);

		//?: {has on the left} insert after
		if(!l.isEmpty())
			return new SetOrderIndex(abr.getTask(), i, l.get(0)).
			  setBeforeAfter(true); //<-- means insert after

		//~: lookup invoices on the right
		List<Invoice> r = bean(GetInvoice.class).findRightInvoices(
		  i.getDomain(), ot, getInvoiceDate(), 1,
		  (i.getPrimaryKey() == null)?(null):
		    Collections.singleton(i.getPrimaryKey())
		);

		//?: {has on the right} insert before
		if(!r.isEmpty())
			return new SetOrderIndex(abr.getTask(), i, r.get(0)).
			  setBeforeAfter(false); //<-- means insert before

		//!: set order index as the last
		return new SetOrderIndex(abr.getTask(), i, null).
		  setBeforeAfter(true); //<-- means insert after
	}


	/* protected: edit support */

	protected InvoiceData createData()
	{
		//?: {sells invoice}
		if(isThatType(Invoices.TYPE_INVOICE_SELLS))
			return new SellsData();

		//?: {sell invoice}
		if(isThatType(Invoices.TYPE_INVOICE_SELL))
			return new SellData();

		//?: {move invoice}
		if(isThatType(Invoices.TYPE_INVOICE_MOVE))
			return new MoveData();

		//?: {buy invoice}
		if(isThatType(Invoices.TYPE_INVOICE_BUY))
			return new BuyData();

		throw EX.state("Unsupported Invoice Type [",
		  getInvoiceType(), "] Unity Name [", getInvoiceTypeName(), "]!");

	}

	protected boolean isThatType(String typeName)
	{
		return UnityTypes.unityType(Invoice.class, typeName).
		  getPrimaryKey().equals(getInvoiceType());
	}

	protected InvGood createGood(Invoice i)
	{
		InvoiceData d = i.getInvoiceData();

		if(d instanceof BuyData)
			return new BuyGood();

		if(d instanceof SellData)
			return new SellGood();

		if(d instanceof MoveData)
			return new MoveGood();

		throw EX.state("Unknown Invoice Data class: ", d.getClass(), '!');
	}

	protected void    assignGood(InvGood ig, InvoiceGoodView eg)
	{
		//?: {the goods units are not the same}
		if(!Goods.equals(ig.getGoodUnit(), eg.getGoodUnit()))
		{
			GoodUnit gu = EX.assertn(
			  bean(GetGoods.class).getGoodUnit(eg.getGoodUnit()),
			  "No Good Unit found with key [", eg.getGoodUnit(), "]!"
			);

			ig.setGoodUnit(gu);
		}

		//~: volume
		EX.assertx(CMP.greZero(eg.getGoodVolume()));
		ig.setVolume(eg.getGoodVolume());

		//?: {buy good}
		if(ig instanceof BuyGood)
			assignGood((BuyGood)ig, eg);

		//?: {sell good}
		if(ig instanceof SellGood)
			assignGood((SellGood)ig, eg);

		//?: {move good}
		if(ig instanceof MoveGood)
			assignGood((MoveGood)ig, eg);

		//~: need calculation
		if(ig instanceof NeedCalcGood)
			((NeedCalcGood)ig).setNeedCalc(eg.getNeedCalc());
	}

	protected void    assignGood(BuyGood ig, InvoiceGoodView eg)
	{
		//~: volume cost
		EX.assertx((eg.getVolumeCost() == null) || CMP.grZero(eg.getVolumeCost()));
		ig.setCost(eg.getVolumeCost());
	}

	protected void    assignGood(SellGood ig, InvoiceGoodView eg)
	{
		//~: volume cost
		EX.assertx((eg.getVolumeCost() == null) || CMP.grZero(eg.getVolumeCost()));
		ig.setCost(eg.getVolumeCost());

		Long plk = (ig.getPrice() == null)?(null)
		  :(ig.getPrice().getPriceList().getPrimaryKey());

		//?: {got no price list}
		if(eg.getPriceList() == null)
			ig.setPrice(null);
		//?: {the price list was changed}
		else if((plk == null) || !plk.equals(eg.getPriceList()))
		{
			GetGoods        gg = bean(GetGoods.class);
			PriceListEntity pl = EX.assertn(gg.getPriceList(eg.getPriceList()));
			GoodPrice       gp = EX.assertn(gg.getGoodPrice(pl, ig.getGoodUnit()));

			//~: assign new good price
			ig.setPrice(gp);
		}
	}

	protected void    assignGood(MoveGood ig, InvoiceGoodView eg)
	{
		//~: move-on flag
		ig.setMoveOn(eg.getMoveOn());
	}


	/* private: invoice edit attributes */

	private Date   editDate;
	private Date   originalDate;
	private Long   orderType;
	private Long   orderReference;


	/* private: the list of edited goods */

	private List<InvoiceGoodView> goods;
}