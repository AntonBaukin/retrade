package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: model */

import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (goods, invoices) */

import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.invoice.InvoiceModelBean;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * The view of a move Invoice read-only info.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceMoveView
       extends FacesInvoiceViewBase
{
	/* public: view [info] interface */

	public String  getInvoiceStore()
	{
		return Goods.getStoreFullName(
		  getEntity().getInvoiceData().getStore());
	}

	public String  getInvoiceSource()
	{
		return Goods.getStoreFullName(
		  ((MoveData)getEntity().getInvoiceData()).getSourceStore());
	}

	public String  getWindowTitleInfo()
	{
		return formatTitle(
		  Invoices.getInvoiceEffectiveType(getEntity()).getTitleLo(),
		  getEntity().getCode(), DU.date2str(getEntity().getInvoiceDate())
		);
	}

	public boolean isAutoProduce()
	{
		return Invoices.isAutoProduceInvoice(getEntity().getInvoiceData());
	}

	public boolean isFreeProduce()
	{
		return Invoices.isFreeProduceInvoice(getEntity().getInvoiceData());
	}

	public boolean isCorrection()
	{
		return Invoices.isCorrectionInvoice(getEntity().getInvoiceData());
	}

	public boolean isTwoTables()
	{
		return isCorrection();
	}


	/* protected: UnityModelView interface */

	/**
	 * TODO delete setPositiveVolumeOnly()
	 */
	protected UnityModelBean createModelInstance()
	{
		InvoiceModelBean mb = new InvoiceModelBean();
		mb.setPositiveVolumeOnly(true);
		return mb;
	}
}