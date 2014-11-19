package com.tverts.api.retrade.document;

/* Java */

import java.util.Date;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.core.JustTxObject;
import com.tverts.api.support.TimestampAdapter;
import com.tverts.api.core.CodedObject;
import com.tverts.api.core.RemovableObject;
import com.tverts.api.core.Timed;


/**
 * A document abstraction.
 */
@XmlType(name = "document", propOrder = {
  "removed", "code", "fixed", "time", "remarks"
})
public abstract class Document
       extends        JustTxObject
       implements     CodedObject, RemovableObject, Timed
{
	@XmlAttribute
	public Boolean getRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	private boolean removed;

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void setCode(String code)
	{
		this.code = code;
	}

	public boolean isFixed()
	{
		return fixed;
	}

	private boolean fixed;

	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
	}

	/**
	 * The timestamp when the document is created.
	 * In external systems defines the order
	 * of the documents.
	 */
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getTime()
	{
		return time;
	}

	private Date time;

	public void setTime(Date time)
	{
		this.time = time;
	}

	public String getRemarks()
	{
		return remarks;
	}

	private String  remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}
}