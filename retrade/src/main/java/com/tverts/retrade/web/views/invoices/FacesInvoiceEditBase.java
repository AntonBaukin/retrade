package com.tverts.retrade.web.views.invoices;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure */

import static com.tverts.endure.ActionBuilderXRoot.SYNCH_AGGR;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.core.GetUnityType;

/* com.tverts: retrade (firms + goods + prices + stores) */

import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of InvoiceEdit model implementation base.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FacesInvoiceEditBase extends ModelView
{
	/* public: view actions */

	public String  doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		codeExists = (code == null) || !checkCodeExists(code);
		return null;
	}

	public String  gotoDoneEditInvoice()
	{
		getModel().setActive(false);
		return "done-edit";
	}

	public String  gotoCancelEditInvoice()
	{
		getModel().setActive(false);
		return "cancel-edit";
	}

	public String  gotoEditInvoiceDate()
	{
		return "edit-date";
	}

	public String  gotoEditContractor()
	{
		getModel().setContractorsSearch(null);
		return "edit-contractor";
	}

	public String  doSubmitEditGoods()
	{
		allowEmptyGoodsList = true;
		assignRequestedGoods();

		return null;
	}

	public String  doSubmitEditInvoice()
	{
		//~: assign the goods from the request
		if(!assignRequestedGoods()) return null;

		//~: load the invoice
		Invoice invoice = EX.assertn(bean(GetInvoice.class).
		  getInvoice(getInvoice().objectKey()),

		  "The Invoice to edit [",
		  getInvoice().objectKey(),
		  "] was not found in the database!"
		);

		//~: contractor
		if(contractorRequired()) EX.assertn(
		  bean(GetContractor.class).getContractor(
		    getModel().getInvoice().getContractor()),
		  "Invoice Contractor is required!"
		);

		//!: issue edit action
		actionRun(Invoices.ACT_UPDATE, invoice, SYNCH_AGGR, true,
		  Invoices.INVOICE_EDIT, getModel().getInvoice()
		);

		return null;
	}


	/* public: [view] interface */

	public Domain      getDomain()
	{
		return bean(GetDomain.class).
		  getDomain(getDomainKey());
	}

	public InvoiceEditModelBean getModel()
	{
		return (InvoiceEditModelBean)super.getModel();
	}

	public InvoiceEdit getInvoice()
	{
		return getModel().getInvoice();
	}

	public UnityType   getInvoiceType()
	{
		if(getInvoice().getInvoiceType() == null)
			return null;

		return bean(GetUnityType.class).
		  getUnityType(getInvoice().getInvoiceType());
	}

	public String      getInvoiceDate()
	{
		return DU.datetime2str(getInvoice().getInvoiceDate());
	}

	public BigDecimal  getInvoiceSumm()
	{
		BigDecimal summ = getInvoice().recalcGoodsCost().getGoodsCost();
		return ((summ == null) || CMP.eqZero(summ))?(null):(summ);
	}


	/* public: secure */

	public void        forceSecureEdit()
	{
		forceSecureEntityKey(getInvoice().objectKey(), "edit");
	}


	/* public: [edit] interface */

	public String      getWinmainTitleEdit()
	{
		StringBuilder sb = new StringBuilder(128);

		sb.append("Редактирование накладной");

		if(getInvoice().getInvoiceCode() != null)
			sb.append(" №").append(
			  getInvoice().getInvoiceCode());

		if(getInvoice().getInvoiceDate() != null)
			sb.append(" от ").append(
			  DU.date2str(getInvoice().getInvoiceDate()));

		return sb.toString();
	}

	public String      getContractorName()
	{
		return Contractors.getContractorName(
		  bean(GetContractor.class).getContractor(
			 getInvoice().getContractor()));
	}

	public Map<String, String>
	                   getTradeStoresLabels()
	{
		List<TradeStore> stores = bean(GetTradeStore.class).
		  getTradeStores(getDomainKey());

		Map<String, String> result =
		  new LinkedHashMap<String, String>(stores.size());

		for(TradeStore store : stores)
			result.put(
			  store.getPrimaryKey().toString(),
			  Goods.getStoreFullName(store)
			);

		return result;
	}


	/* public: view [validation] interface */

	public boolean isValid()
	{
		return SU.sXe(errorEvent) && !codeExists;
	}

	protected String errorEvent;

	public String  getErrorEvent()
	{
		return errorEvent;
	}

	protected boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}

	public boolean checkCodeExists(String code)
	{
		Long pk = bean(GetInvoice.class).
		  getInvoiceKey(getModel().getDomain(), code);

		return (pk == null) || pk.equals(getInvoice().objectKey());
	}


	/* protected: ModelView interface */

	protected ModelBean createModel()
	{
		//~: get the invoice to edit from the request
		Long   k = obtainEntityKeyFromRequest();

		if(k == null) throw new IllegalStateException(
		  "Can't obtain primary key of Invoice to " +
		  "edit from the HTTP request!");

		//~: load that invoice
		Object i = bean(GetInvoice.class).getInvoiceEditInit(k);

		if(i == null) throw new IllegalStateException(String.format(
		  "Can't find Invoice by the key %d given!", k));

		//~: create the model
		InvoiceEditModelBean m = new InvoiceEditModelBean();

		//~: create the edit invoice
		m.setInvoice(new InvoiceEdit().init(i));

		//~: set model domain
		if(m.getInvoice() != null)
			m.setDomain(m.getInvoice().getDomain());

		return m;
	}

	protected boolean   isRequestModelMatch(ModelBean model)
	{
		return (model instanceof InvoiceEditModelBean);
	}


	/* protected: actions support */

	protected boolean allowEmptyGoodsList;

	protected boolean contractorRequired()
	{
		return false;
	}

	protected boolean goodVolumeCostRequired()
	{
		return false;
	}

	protected List<InvoiceGoodView>
	                  readRequestedGoods()
	{
		return readRequestedGoods("");
	}

	protected List<InvoiceGoodView>
	                  readRequestedGoods(String prefix)
	{
		List<InvoiceGoodView> res = new ArrayList<InvoiceGoodView>(16);
		GetGoods              get = bean(GetGoods.class);

		//c: index the parameters
		for(int i = 0;;i++)
		{
			String gk = SU.s2s(request().getParameter(prefix + "goodUnit" + i));
			String gc = SU.s2s(request().getParameter(prefix + "goodCode"+i));
			if((gk == null) || (gc == null)) break;

			String vo = SU.s2s(request().getParameter(prefix + "goodVolume"+i));
			String vc = SU.s2s(request().getParameter(prefix + "volumeCost"+i));
			String pk = SU.s2s(request().getParameter(prefix + "goodPrice"+i));
			String nc = SU.s2s(request().getParameter(prefix + "needCalc"+i));

			//?: {has no volume}
			if(vo == null)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "объём не указан!"
				);

				return null;
			}

			//~: create and add the part view
			InvoiceGoodView g = new InvoiceGoodView();
			res.add(g);

			//~: load good unit
			GoodUnit gu = get.getGoodUnit(Long.parseLong(gk));
			if(gu == null)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "товар с указанным кодом не найден!"
				);

				return null;
			}

			//~: assign good unit
			g.init(gu);

			//sec: good of the same domain
			if(!gu.getDomain().getPrimaryKey().equals(getModel().getDomain()))
				throw EX.forbid();

			//~: assign the volume
			try
			{
				g.setGoodVolume(new BigDecimal(vo));

				//?: {volume must be integer}
				if(!gu.getMeasure().getOx().isFractional())
					g.setGoodVolume(g.getGoodVolume().setScale(0));
			}
			catch(Throwable e)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "объём имеет неверный формат!"
				);

				return null;
			}

			//~: assign the volume cost
			if(vc != null) try
			{
				g.setVolumeCost(new BigDecimal(vc));
			}
			catch(Throwable e)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "общая стоимость имеет неверный формат!"
				);

				return null;
			}

			//?: {volume cost is not set}
			if(goodVolumeCostRequired() && (g.getVolumeCost() == null))
			{
				errorEvent = SU.cats(
				  "Товар [", g.getGoodCode(), "]: ",
				  "общая стоимость имеет неверный формат!"
				);

				return null;
			}

			//~: assign the good price
			if(pk != null)
			{
				GoodPrice gp = EX.assertn(
				  get.getGoodPrice(Long.parseLong(pk)),
				  "Good Price [", pk, "] was not found!"
				);

				//~: check the price of that good
				EX.assertx(gu.equals(gp.getGoodUnit()));

				//~: set the price list
				g.setPriceList(gp.getPriceList().getPrimaryKey());
			}

			//~: need calc flag
			g.setNeedCalc(SU.sXe(nc)?(null):"true".equals(nc));
		}

		if(res.isEmpty() && !allowEmptyGoodsList)
		{
			errorEvent = "Список товаров не должен быть пустым!";
			return null;
		}

		return res;
	}

	protected void    reuseGoods(List<InvoiceGoodView> ext)
	{
		//~: map current goods by the good unit
		Map<Long, InvoiceGoodView> mcur =
		  new HashMap<Long, InvoiceGoodView>(17);
		for(InvoiceGoodView g : getModel().getInvoice().getGoods())
			mcur.put(g.getGoodUnit(), g);

		//~: assign the existing goods keys
		for(InvoiceGoodView g : ext) if(g.getObjectKey() == null)
		{
			InvoiceGoodView c = mcur.get(g.getGoodUnit());

			if(c != null)
				g.setObjectKey(c.getObjectKey());
			else
			{
				BuyGood ig = new BuyGood();

				//~: just create primary key
				HiberPoint.setPrimaryKey(TxPoint.txSession(), ig,
				  HiberPoint.isTestPrimaryKey(getInvoice().objectKey()));

				g.setObjectKey(ig.getPrimaryKey());
			}
		}
	}

	protected boolean assignRequestedGoods()
	{
		//~: read the goods from the request
		List<InvoiceGoodView> goods = readRequestedGoods();
		if((goods == null) || !validateGoods(goods))
			return false;

		//~: assign exiting invoice goods keys
		reuseGoods(goods);

		//~: assign the goods requested
		getModel().getInvoice().setGoods(goods);

		return true;
	}

	protected boolean validateGoods(List<InvoiceGoodView> goods)
	{
		return true;
	}
}