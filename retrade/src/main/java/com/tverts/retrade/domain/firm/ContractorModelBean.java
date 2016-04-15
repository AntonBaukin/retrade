package com.tverts.retrade.domain.firm;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: models */

import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (documents + invoices) */

import com.tverts.retrade.domain.doc.DocsSearchModelBean;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean with main info on the {@link Contractor} given.
 *
 * @author anton.baukin@gmail.com
 */
public class ContractorModelBean extends UnityModelBean
{
	/* public: bean interface */

	public DocsSearchModelBean getDocsSearch()
	{
		if(docsSearch != null)
			return docsSearch;

		docsSearch = new DocsSearchModelBean();

		//~: domain
		docsSearch.setDomain(getDomain());

		//~: Buy-Sell Invoices only
		docsSearch.retainDocTypes(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell()
		);

		//~: this contractor only restriction
		docsSearch.setDocOwnerKey(getPrimaryKey());
		docsSearch.setDocOwnerClass(Contractor.class);
		docsSearch.setDocOwnerType(Contractors.TYPE_CONTRACTOR);

		return docsSearch;
	}

	public void setDocsSearch(DocsSearchModelBean docsSearch)
	{
		this.docsSearch = docsSearch;
	}


	/* public: support interface */

	public Contractor accessEntity()
	{
		return (Contractor) super.accessEntity();
	}


	/* private: encapsulated data */

	private DocsSearchModelBean docsSearch;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		IO.obj(o, docsSearch);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		docsSearch = IO.obj(i, DocsSearchModelBean.class);
	}
}