package com.tverts.retrade.domain.doc;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Transfer view on Document View.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "document")
public class DocView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public Long getDocViewKey()
	{
		return docViewKey;
	}

	public void setDocViewKey(Long docViewKey)
	{
		this.docViewKey = docViewKey;
	}

	public String getDocTypeLo()
	{
		return docTypeLo;
	}

	public void setDocTypeLo(String docTypeLo)
	{
		this.docTypeLo = docTypeLo;
	}

	public String getDocStateLo()
	{
		return docStateLo;
	}

	public void setDocStateLo(String docStateLo)
	{
		this.docStateLo = docStateLo;
	}

	public String getDocCode()
	{
		return docCode;
	}

	public void setDocCode(String docCode)
	{
		this.docCode = docCode;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getDocDate()
	{
		return docDate;
	}

	public void setDocDate(Date docDate)
	{
		this.docDate = docDate;
	}

	public String getDocText()
	{
		return docText;
	}

	public void setDocText(String docText)
	{
		this.docText = docText;
	}

	public BigDecimal getDocCost()
	{
		return docCost;
	}

	public void setDocCost(BigDecimal docCost)
	{
		this.docCost = docCost;
	}

	public Long getStoreKey()
	{
		return storeKey;
	}

	public void setStoreKey(Long storeKey)
	{
		this.storeKey = storeKey;
	}

	public String getStoreCode()
	{
		return storeCode;
	}

	public void setStoreCode(String storeCode)
	{
		this.storeCode = storeCode;
	}

	public String getStoreName()
	{
		return storeName;
	}

	public void setStoreName(String storeName)
	{
		this.storeName = storeName;
	}


	/* public: init interface */

	public DocView init(Object o)
	{
		if(o instanceof DocumentView)
			return this.init((DocumentView) o);

		return this;
	}

	public DocView init(DocumentView d)
	{
		objectKey  = d.getViewOwner().getPrimaryKey();
		docViewKey = d.getPrimaryKey();

		docTypeLo = EX.assertn(d.getDocType()).getTitleLo();
		if(d.getOwnerState() != null)
			docStateLo = d.getOwnerState().getTitleLo();

		docCode = d.getDocCode();
		docDate = d.getDocDate();
		docText = d.getDocText();

		docCost = d.getDocCost();
		if(docCost != null)
			docCost = docCost.setScale(2, BigDecimal.ROUND_HALF_EVEN);

		if(d.getStore() != null)
			this.init(d.getStore());

		return this;
	}

	public DocView init(TradeStore s)
	{
		storeKey  = s.getPrimaryKey();
		storeCode = s.getCode();
		storeName = s.getName();

		return this;
	}


	/* view attributes */

	private Long       objectKey;
	private Long       docViewKey;

	private String     docTypeLo;
	private String     docStateLo;

	private String     docCode;
	private Date       docDate;
	private String     docText;
	private BigDecimal docCost;

	private Long       storeKey;
	private String     storeCode;
	private String     storeName;
}