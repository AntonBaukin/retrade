package com.tverts.retrade.web.views.invoices;

/* Java */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: retrade (goods) */

import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Back bean for edit model of Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesInvoiceMoveEdit extends FacesInvoiceEditBase
{
	/* public: [edit] interface */

	public boolean isAutoProduce()
	{
		return (getInvoice().getSubType() != null) &&
		  (getInvoice().getSubType() == 'A');
	}

	public boolean isFreeProduce()
	{
		return (getInvoice().getSubType() != null) &&
		  (getInvoice().getSubType() == 'P');
	}

	public boolean isCorrection()
	{
		return (getInvoice().getSubType() != null) &&
		  (getInvoice().getSubType() == 'C');
	}

	public boolean isTwoTables()
	{
		return isFreeProduce() || isCorrection();
	}


	/* protected: actions support */

	protected List<InvoiceGoodView> readRequestedGoods()
	{
		List<InvoiceGoodView> res = readRequestedGoods("");

		//?: {only one table}
		if(!isTwoTables()) return res;

		//~: read take-only goods
		List<InvoiceGoodView> tos = readRequestedGoods("-");

		//?: {no one for free produce}
		if(isFreeProduce() && ((tos == null) || tos.isEmpty()))
		{
			errorEvent = "Список товаров-сырья пуст!";
			return null;
		}

		//?: {free produce | correction} make default goods by place-only
		if(isFreeProduce() || isCorrection())
			for(InvoiceGoodView g : res)
				g.setMoveOn(true);

		//~: assign take-only flag
		for(InvoiceGoodView g : tos)
			g.setMoveOn(false);

		//~: combine & return
		res.addAll(tos);
		return res;
	}

	protected boolean validateGoods(List<InvoiceGoodView> goods)
	{
		if(!isAutoProduce())
			return super.validateGoods(goods);

		List<String> codes = new ArrayList<String>(2);
		GetGoods     get   = bean(GetGoods.class);
		Date         time  = EX.assertn(getInvoice().getInvoiceDate());

		for(InvoiceGoodView ig : goods)
			if(get.getGoodCalc(ig.getGoodUnit(), time) == null)
				codes.add(ig.getGoodCode());

		//?: {has goods without calculations}
		if(!codes.isEmpty())
			errorEvent = SU.cats("Перечисленные товары не имеют формулы/рецепта " +
			  "либо не имели на момент даты накладной: ", SU.cat(null, ", ", codes)
			);

		return codes.isEmpty() && super.validateGoods(goods);
	}
}