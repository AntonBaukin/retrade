package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: retrade (goods, invoices) */

import com.tverts.retrade.domain.goods.Goods;


/**
 * The view of a Volume Check Document read-only info.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesVolumeCheckView extends FacesInvoiceViewBase
{
	/* public: view shared interface */

	public String getInvoiceStore()
	{
		return Goods.getStoreFullName(
		  getEntity().getInvoiceData().getStore());
	}
}