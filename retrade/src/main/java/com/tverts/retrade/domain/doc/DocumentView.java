package com.tverts.retrade.domain.doc;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.UnityView;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.TradeStore;


/**
 * Document is view on the entities as on
 * system produced financial items.
 *
 * @author anton.baukin@gmail.com
 */
public class DocumentView extends UnityView
{
	/* public: DocumentView (bean) interface */

	/**
	 * The Document Type. Defaults to the view owner'
	 * type, but may be altered.
	 */
	public UnityType   getDocType()
	{
		return (docType != null)?(docType):
		  (docType = getOwnerType());
	}

	public void        setDocType(UnityType docType)
	{
		this.docType = docType;
	}

	public String      getDocCode()
	{
		return docCode;
	}

	public void        setDocCode(String docCode)
	{
		this.docCode = docCode;
	}

	public Date        getDocDate()
	{
		return docDate;
	}

	public void        setDocDate(Date docDate)
	{
		this.docDate = docDate;
	}

	/**
	 * Additional user comment on the view.
	 */
	public String      getDocText()
	{
		return docText;
	}

	public void        setDocText(String docText)
	{
		this.docText = docText;
	}

	public BigDecimal  getDocCost()
	{
		return docCost;
	}

	public void        setDocCost(BigDecimal c)
	{
		if((c != null) && (c.scale() != 8))
			c = c.setScale(8);

		this.docCost = c;
	}

	public TradeStore getStore()
	{
		return store;
	}

	public void       setStore(TradeStore store)
	{
		this.store = store;
	}


	/* persisted attributes */

	private UnityType  docType;
	private String     docCode;
	private Date       docDate;
	private String     docText;
	private BigDecimal docCost;
	private TradeStore store;
}