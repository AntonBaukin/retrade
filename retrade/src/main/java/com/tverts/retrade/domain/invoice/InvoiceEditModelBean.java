package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.core.GetUnityType;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.InvoiceEditModelData;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Model bean for {@link InvoiceEdit} is being edited.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class InvoiceEditModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceEditModelBean (bean) interface */

	public InvoiceEdit  getInvoice()
	{
		return invoice;
	}

	public void         setInvoice(InvoiceEdit invoice)
	{
		this.invoice = invoice;
	}

	public Long         getLastPriceList()
	{
		return lastPriceList;
	}

	public void         setLastPriceList(Long lastPriceList)
	{
		this.lastPriceList = lastPriceList;
	}

	public String       getTradeStore()
	{
		Long store = getInvoice().getTradeStore();
		return (store == null)?(null):(store.toString());
	}

	public void         setTradeStore(String store)
	{
		getInvoice().setTradeStore(
		  (store == null)?(null):(Long.parseLong(store))
		);
	}

	public String       getTradeStoreSource()
	{
		Long store = getInvoice().getTradeStoreSource();
		return (store == null)?(null):(store.toString());
	}

	public void         setTradeStoreSource(String store)
	{
		getInvoice().setTradeStoreSource(
		  (store == null)?(null):(Long.parseLong(store))
		);
	}


	/* public: InvoiceEditModelBean (support) interface */

	public Domain       findDomain()
	{
		if(getDomain() != null)
			return bean(GetDomain.class).getDomain(domain());

		Invoice invoice = (getInvoice() == null)?(null):
		  bean(GetInvoice.class).getInvoice(getInvoice().objectKey());

		return (invoice == null)?(null):(invoice.getDomain());
	}

	public UnityType    findOrderType()
	{
		if(getInvoice().getOrderType() != null)
			return bean(GetUnityType.class).
			  getUnityType(getInvoice().getOrderType());

		Invoice invoice = (getInvoice() == null)?(null):
		  bean(GetInvoice.class).getInvoice(getInvoice().objectKey());

		return (invoice == null)?(null):(invoice.getOrderType());
	}


	/* public: InvoiceEditModelBean (configuration) interface */

	/**
	 * TODO: remove isEditDateOrderByDate()
	 */
	public boolean      isEditDateOrderByDate()
	{
		return editDateOrderByDate;
	}

	public void         setEditDateOrderByDate(boolean v)
	{
		this.editDateOrderByDate = v;
	}


	/* public: InvoiceEditModelBean (contractors edit) interface */

	public String[]     getContractorsWords()
	{
		return SU.s2a(getContractorsSearch());
	}

	public String       getContractorsSearch()
	{
		return contractorsSearch;
	}

	public void         setContractorsSearch(String contractorsSearch)
	{
		this.contractorsSearch = contractorsSearch;
	}


	/* public: ModelBean (data access) interface */

	public ModelData    modelData()
	{
		return new InvoiceEditModelData(this);
	}


	/* private: invoice edited */

	private InvoiceEdit invoice;
	private Long        lastPriceList;


	/* private: configuration parameters */

	private boolean     editDateOrderByDate;


	/* private: contractors edit */

	private String      contractorsSearch;
}