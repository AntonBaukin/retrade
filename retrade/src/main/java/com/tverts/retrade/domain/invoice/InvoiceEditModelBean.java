package com.tverts.retrade.domain.invoice;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

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

import com.tverts.retrade.web.data.InvoiceEditModelData;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.IO;
import com.tverts.support.SU;


/**
 * Model bean for {@link InvoiceEdit} is being edited.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "incoice-edit-model")
public class InvoiceEditModelBean extends DataSelectModelBean
{
	/* Invoice Edit Model (bean) */

	public InvoiceEdit getInvoice()
	{
		return invoice;
	}

	public void setInvoice(InvoiceEdit invoice)
	{
		this.invoice = invoice;
	}

	public Long getLastPriceList()
	{
		return lastPriceList;
	}

	public void setLastPriceList(Long lastPriceList)
	{
		this.lastPriceList = lastPriceList;
	}

	@XmlTransient
	public String getTradeStore()
	{
		Long store = getInvoice().getTradeStore();
		return (store == null)?(null):(store.toString());
	}

	public void setTradeStore(String store)
	{
		getInvoice().setTradeStore(
		  (store == null)?(null):(Long.parseLong(store))
		);
	}

	@XmlTransient
	public String getTradeStoreSource()
	{
		Long store = getInvoice().getTradeStoreSource();
		return (store == null)?(null):(store.toString());
	}

	public void setTradeStoreSource(String store)
	{
		getInvoice().setTradeStoreSource(
		  (store == null)?(null):(Long.parseLong(store))
		);
	}

	/**
	 * Event number of the invoices to display
	 * in the date-time edit table. Default is 20.
	 */
	public int getInvocesNumber()
	{
		return invocesNumber;
	}

	public void setInvocesNumber(int invocesNumber)
	{
		EX.assertx(invocesNumber > 0);
		EX.assertx(invocesNumber % 2 == 0);
		this.invocesNumber = invocesNumber;
	}


	/* Invoice Edit Model (support) */

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
		if(getInvoice() == null)
			return null;

		if(getInvoice().getOrderType() != null)
			return bean(GetUnityType.class).
			  getUnityType(getInvoice().getOrderType());

		Invoice invoice = bean(GetInvoice.class).
		  getInvoice(getInvoice().objectKey());

		return (invoice == null)?(null):(invoice.getOrderType());
	}


	/* Invoice Edit Model (contractors select) */

	public String[]     contractorsSearch()
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


	/* Model Bean (data access) */

	public ModelData    modelData()
	{
		return new InvoiceEditModelData(this);
	}


	/* private: encapsulated data */

	private InvoiceEdit invoice;
	private Long        lastPriceList;
	private int         invocesNumber = 20;
	private String      contractorsSearch;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.xml(o, invoice);
		IO.longer(o, lastPriceList);
		o.writeInt(invocesNumber);
		IO.str(o, contractorsSearch);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		invoice           = IO.xml(i, InvoiceEdit.class);
		lastPriceList     = IO.longer(i);
		invocesNumber     = i.readInt();
		contractorsSearch = IO.str(i);
	}
}