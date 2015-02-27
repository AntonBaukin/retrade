package com.tverts.retrade.domain.sells;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnitySelectModelBean;

/* com.tverts: retrade domain (documents) */

import com.tverts.retrade.domain.doc.DocsSearchModelBean;

/* com.tverts: retrade data */

import com.tverts.retrade.web.data.sells.SellsSessionModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model bean that provides the list of the {@link SellReceipt}s
 * of the {@link SellsSession} given.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "sells-session-model")
public class SellsSessionModelBean extends UnitySelectModelBean
{
	/* Sells Session Model (bean) */

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


	/* Sells Session Model (read) */

	public SellsSession accessEntity()
	{
		return (SellsSession) super.accessEntity();
	}


	/* Model Bean (data access) */

	public ModelData    modelData()
	{
		return new SellsSessionModelData(this);
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