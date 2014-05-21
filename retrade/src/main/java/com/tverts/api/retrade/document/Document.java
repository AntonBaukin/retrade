package com.tverts.api.retrade.document;

/* standard Java classes */

import java.io.Serializable;
import java.util.Date;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.support.TimestampAdapter;
import com.tverts.api.core.CodedObject;
import com.tverts.api.core.RemovableObject;
import com.tverts.api.core.Timed;
import com.tverts.api.core.TwoKeysObject;
import com.tverts.api.core.TxObject;


/**
 * A document abstraction.
 */
@XmlType(name = "document", propOrder = {
  "pkey", "tx", "xkey", "removed", "code", "fixed", "time", "remarks"
})
public abstract class Document
       implements     Serializable,
                      TxObject, TwoKeysObject,
                      CodedObject, RemovableObject, Timed
{
	public static final long serialVersionUID = 0L;


	/**
	 * The primary key of the document
	 * in the source database.
	 */
	@XmlAttribute(name = "pkey")
	public Long getPkey()
	{
		return (pkey == 0L)?(null):(pkey);
	}

	public void setPkey(Long pkey)
	{
		this.pkey = (pkey == null)?(0L):(pkey);
	}

	/**
	 * The transaction number assigned
	 * by the source database.
	 */
	@XmlAttribute(name = "tx")
	public Long getTx()
	{
		return (tx == 0L)?(null):(tx);
	}

	public void setTx(Long tx)
	{
		this.tx = (tx == null)?(0L):(tx);
	}

	/**
	 * The primary key of the document
	 * in the integrated database.
	 */
	@XmlElement(name = "xkey")
	public String getXkey()
	{
		return xkey;
	}

	public void setXkey(String xkey)
	{
		this.xkey = xkey;
	}

	@XmlAttribute(name = "removed")
	public Boolean isRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}

	@XmlElement(name = "code")
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlElement(name = "fixed")
	public boolean isFixed()
	{
		return fixed;
	}

	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
	}

	/**
	 * The timestamp when the document is created.
	 * In external systems defines the order
	 * of the documents.
	 */
	@XmlElement(name = "time")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	@XmlElement(name = "remarks")
	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/* attributes */

	private long    pkey;
	private long    tx;
	private String  xkey;
	private boolean removed;
	private String  code;
	private boolean fixed;
	private Date    time;
	private String  remarks;
}