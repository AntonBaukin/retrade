package com.tverts.endure.cats;

/* Java */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A read-only view on the catalogue item instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "cat-item")
@XmlType(name = "cat-item-view")
public class CatItemView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* Catalogue Item View Bean */

	public Long   getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void   setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void   setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	private String name;

	public void   setName(String name)
	{
		this.name = name;
	}


	/* Initialization */

	public CatItemView init(Object obj)
	{
		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		if(obj instanceof CatItem)
			return init((CatItem)obj);

		return this;
	}

	public CatItemView init(CatItem ci)
	{
		//~: primary key
		this.objectKey = ci.getPrimaryKey();

		//~: code
		this.code = ci.getCode();

		//~: name
		this.name = ci.getName();

		return this;
	}
}