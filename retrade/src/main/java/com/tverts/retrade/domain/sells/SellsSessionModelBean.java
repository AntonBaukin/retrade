package com.tverts.retrade.domain.sells;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnitySelectModelBean;

/* com.tverts: retrade domain (documents) */

import com.tverts.retrade.domain.doc.DocsSearchModelBean;

/* com.tverts: retrade data */

import com.tverts.retrade.data.sells.SellsSessionModelData;


/**
 * Model bean that provides the list of the {@link SellReceipt}s
 * of the {@link SellsSession} given.
 *
 * @author anton.baukin@gmail.com
 */
public class SellsSessionModelBean extends UnitySelectModelBean
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

		//~: Sells Invoices only
		docsSearch.retainDocTypes(
		  Sells.typeSellsInvoice()
		);

		//~: this sells session only restriction
		docsSearch.setDocOwnerKey(getPrimaryKey());
		docsSearch.setDocOwnerClass(SellsSession.class);
		docsSearch.setDocOwnerType(Sells.TYPE_SELLS_SESS);

		return docsSearch;
	}

	public void setDocsSearch(DocsSearchModelBean docsSearch)
	{
		this.docsSearch = docsSearch;
	}


	/* public: support interface */

	public SellsSession accessEntity()
	{
		return (SellsSession) super.accessEntity();
	}


	/* public: ModelBean (data access) interface */

	public ModelData    modelData()
	{
		return new SellsSessionModelData(this);
	}


	/* private: documents search model */

	private DocsSearchModelBean docsSearch;

}