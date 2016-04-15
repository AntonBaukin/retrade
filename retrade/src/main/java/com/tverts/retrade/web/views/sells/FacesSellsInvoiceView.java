package com.tverts.retrade.web.views.sells;

/* standard Java classes */

import java.math.BigDecimal;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: retrade (goods, invoices, sells) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.sells.SellsData;
import com.tverts.retrade.domain.sells.SellsSession;

/* com.tverts: retrade web views (invoices) */

import com.tverts.retrade.web.views.invoices.FacesInvoiceViewBase;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.SU;


/**
 * The view of a Sells Invoice read-only info.
 * (Sells Invoices are produced by the Sells Sessions.)
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSellsInvoiceView extends FacesInvoiceViewBase
{
	/* actions */

	public String gotoSellsSession()
	{
		return "session";
	}


	/* public: view shared interface */

	public String       getWindowTitleInfo()
	{
		return formatTitle(
		  getEntity().getInvoiceType().getTitleLo(),
		  SU.cats(" сессия №", getSellsSession().getCode()),
		  DU.datetime2str(getEntity().getInvoiceDate())
		);
	}

	public String       getInvoiceStore()
	{
		return Goods.getStoreFullName(
		  getEntity().getInvoiceData().getStore());
	}

	public String       getInvoiceSumm()
	{
		BigDecimal cost = Invoices.getInvoiceGoodsCost(getEntity());
		return (cost == null)?(null):
		  (cost.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
	}

	public SellsSession getSellsSession()
	{
		return ((SellsData) getEntity().
		  getInvoiceData()).getSession();
	}
}