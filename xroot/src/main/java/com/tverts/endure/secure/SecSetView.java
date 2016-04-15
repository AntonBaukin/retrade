package com.tverts.endure.secure;

/* standard Java classes */

import java.io.Serializable;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * View on Secure Set.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "secure-able")
public class SecSetView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: SecSetView (bean) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}


	/* public: init interface */

	public SecSetView init(SecSet s)
	{
		objectKey = s.getPrimaryKey();
		name = s.getName();
		createTime = s.getCreateTime();
		comment = s.getComment();

		return this;
	}


	/* secure set attributes */

	private Long   objectKey;
	private String name;
	private Date   createTime;
	private String comment;
}
