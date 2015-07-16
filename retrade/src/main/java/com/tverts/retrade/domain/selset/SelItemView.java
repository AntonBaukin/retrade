package com.tverts.retrade.domain.selset;

/* standard Java classes */

import java.io.Serializable;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.GetUnity;
import com.tverts.endure.cats.CatItem;

/* com.tverts: formatters */

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


	/* public: SelItemView (bean) interface */

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

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}


	/* public: initialization interface */

	public SelItemView init(Object obj)
	{
		if(obj instanceof SelItem)
			return this.init((SelItem) obj);

		if(obj instanceof CatItem)
			this.init((CatItem) obj);

		if(this.name == null)
			this.name = FmtPoint.format(obj,
			  FmtPoint.LONG, FmtPoint.TYPE, FmtPoint.DISPLAY
			);

		return this;
	}

	public SelItemView init(SelItem obj)
	{
		this.objectKey = obj.getObject();

		return this.init(bean(GetUnity.class).
		  getUnited(this.objectKey));
	}

	public SelItemView init(CatItem obj)
	{
		this.code = obj.getCode();

		return this;
	}


	/* properties of the item */

	private Long   objectKey;
	private String name;
	private String code;
}