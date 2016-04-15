package com.tverts.retrade.domain.selset;

/* standard Java classes */

import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: api */

import com.tverts.api.core.JString;

/* com.tverts: endure (core) */

import com.tverts.endure.core.GetUnity;

/* com.tverts: formatters */

import com.tverts.objects.XPoint;
import com.tverts.support.fmt.FmtPoint;


/**
 * XML view of Selection Set Item.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "selset-item")
public class SelItemView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* Selection Set Item View (bean) */

	public Long getItemKey()
	{
		return itemKey;
	}

	private Long itemKey;

	public void setItemKey(Long itemKey)
	{
		this.itemKey = itemKey;
	}

	public Long getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getTitle()
	{
		return title;
	}

	private String title;

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getOxClass()
	{
		return oxClass;
	}

	private String oxClass;

	public void setOxClass(String oxClass)
	{
		this.oxClass = oxClass;
	}

	/**
	 * String of JSON encoded object stored under
	 * {@link JString} or JAXB-annotated class.
	 */
	public String getOxString()
	{
		return oxString;
	}

	private String oxString;

	public void setOxString(String oxString)
	{
		this.oxString = oxString;
	}


	/* public: initialization interface */

	public SelItemView init(Object obj)
	{
		if(obj instanceof SelItem)
			return this.init((SelItem) obj);
		return this;
	}

	public SelItemView init(SelItem obj)
	{
		this.itemKey   = obj.getPrimaryKey();
		this.objectKey = obj.getObject();
		this.oxClass   = obj.getOxClass();

		//?: {has object key}
		if(this.objectKey != null)
		{
			//~: load the united instance
			Object u = bean(GetUnity.class).
			  getUnited(this.objectKey);

			//?: {found it} format
			if(u != null)
				this.title = FmtPoint.format(u, FmtPoint.LONG, FmtPoint.DISPLAY);
		}

		//?: {has ox-object}
		if(obj.getOx() != null)
		{
			//?: {need format it}
			if(this.title == null)
				this.title = FmtPoint.format(obj.getOx(), FmtPoint.DISPLAY);

			//?: {has JSON wrapper}
			if(obj.getOx() instanceof JString)
				this.oxString = ((JString)obj.getOx()).getJson();
			//?: {supported by JAXB}
			else if(XPoint.json().supported(obj.getOx()))
				this.oxString = XPoint.json().write(obj.getOx());
		}

		return this;
	}
}