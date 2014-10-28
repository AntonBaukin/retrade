package com.tverts.retrade.data;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.GetUnityType;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (firm) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: retrade domain (invoice) */

import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.InvoiceView;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provider ov various models related to the
 * Invoices editing interface.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "goods", "dateCloseInvoices",
  "contractorsNumber", "contractors"
})
public class InvoiceEditModelData implements ModelData
{
	/* public: constructors */

	public InvoiceEditModelData()
	{}

	public InvoiceEditModelData(InvoiceEditModelBean model)
	{
		this.model = model;
	}


	/* public: InvoiceEditModelData (bean) interface */

	@XmlElement
	public InvoiceEditModelBean  getModel()
	{
		return model;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<InvoiceGoodView> getGoods()
	{
		if(!ModelRequest.isKey(null))
			return new ArrayList<InvoiceGoodView>(0);
		return getModel().getInvoice().getGoods();
	}

	@XmlElement(name = "invoice")
	@XmlElementWrapper(name = "date-close-invoices")
	public List<InvoiceView> getDateCloseInvoices()
	{
		if(!ModelRequest.isKey("edit-date"))
			return null;

		InvoiceEdit invoice = getModel().getInvoice();
		Set<Long>   exclude = null;
		if(invoice.objectKey() != null)
			exclude = Collections.singleton(invoice.objectKey());

		UnityType   invType = bean(GetUnityType.class).
		  getUnityType(invoice.getInvoiceType());
		if(invType == null) throw new IllegalStateException();
		
		//~: selection numbers
		int N = getModel().getInvocesNumber(), N2 = N/2;
		EX.assertx(N2*2 == N);

		//~: get the invoices on the date left order
		List<Invoice> lfs;

		//?: {order by data}
		if("date".equals(getModel().getFirstSortProp()))
			lfs = bean(GetInvoice.class).findLeftInvoicesByDate(
			  getModel().findDomain(), invType,
			  invoice.getEditDate(), N, exclude
			);
		//~: order by code
		else
			lfs = bean(GetInvoice.class).findLeftInvoices(
			  getModel().findDomain(), getModel().findOrderType(),
			  invoice.getEditDate(), N, exclude
			);

		//~: get the invoices on the date right order
		List<Invoice> rts;

		//?: {order by data}
		if("date".equals(getModel().getFirstSortProp()))
			rts = bean(GetInvoice.class).findRightInvoicesByDate(
			  getModel().findDomain(), invType,
			  invoice.getEditDate(), N, exclude
			);
		//~: order by code
		else
			rts = bean(GetInvoice.class).findRightInvoices(
			  getModel().findDomain(), getModel().findOrderType(),
			  invoice.getEditDate(), N, exclude
			);

		//~: balance the size to get total N
		int lsz = (lfs.size() > N2)?(N2):(lfs.size());
		int rsz = (rts.size() > N2)?(N2):(rts.size());

		if(lsz < N2) rsz += N2 - lsz;
		if(rsz < N2) lsz += N2 - rsz;
		if(lsz > lfs.size()) lsz = lfs.size();
		if(rsz > rts.size()) rsz = rts.size();


		List<InvoiceView> res = new ArrayList<InvoiceView>(N);

		//~: add left invoices (!: from the tail as in reverse order)
		for(int i = lsz - 1;(i >= 0);i--)
			res.add(new InvoiceView().init(lfs.get(i)));

		//~: add the invoice being edited
		res.add(invoice);

		//~: add right invoices
		for(int i = 0;(i < rsz);i++)
			res.add(new InvoiceView().init(rts.get(i)));

		return res;
	}

	@XmlElement(name = "contractorsNumber")
	public Long getContractorsNumber()
	{
		if(!ModelRequest.isKey("edit-contractor"))
			return null;

		return bean(GetContractor.class).
		  countContractors(getModel());
	}

	@XmlElement(name = "contractor")
	@XmlElementWrapper(name = "contractors")
	public List<CatItemView> getContractors()
	{
		if(!ModelRequest.isKey("edit-contractor"))
			return null;

		List<Contractor>  cos = bean(GetContractor.class).
		  selectContractors(getModel());
		List<CatItemView> res = new ArrayList<CatItemView>(cos.size());

		for(Contractor co : cos)
			res.add(new CatItemView().init(co));

		return res;
	}


	/* private: the model */

	private InvoiceEditModelBean model;
}