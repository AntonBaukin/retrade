package com.tverts.retrade.web.views.invoices;

/* standard Java classes */

import java.math.BigDecimal;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: retrade (goods, invoices) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * The view of a buy Invoice read-only info.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceBuyView
       extends FacesInvoiceViewBase
{
	/* public: view shared interface */

	public String  getInvoiceStore()
	{
		return Goods.getStoreFullName(
		  getEntity().getInvoiceData().getStore());
	}

	public String  getInvoiceSumm()
	{
		BigDecimal cost = Invoices.getInvoiceGoodsCost(getEntity());
		return (cost == null)?(null):
		  (cost.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
	}
}