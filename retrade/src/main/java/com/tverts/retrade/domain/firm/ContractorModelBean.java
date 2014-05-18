package com.tverts.retrade.domain.firm;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (documents + invoices) */

import com.tverts.retrade.domain.doc.DocsSearchModelBean;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Model bean with main info on the {@link Contractor} given.
 *
 * @author anton.baukin@gmail.com
 */
public class ContractorModelBean extends UnityModelBean
{
	public static final long serialVersionUID = 0L;


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


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return null;
	}


	/* public: support interface */

	public Contractor accessEntity()
	{
		return (Contractor) super.accessEntity();
	}


	/* private: documents search model */

	private DocsSearchModelBean docsSearch;
}