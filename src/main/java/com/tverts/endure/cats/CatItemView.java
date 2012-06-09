package com.tverts.endure.cats;

/* standard Java classes */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;


/**
 * A read-only view on the catalogue item instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class CatItemView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: CatItemView (main properties) interface */

	public Long   getObjectKey()
	{
		return objectKey;
	}

	public void   setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getCode()
	{
		return code;
	}

	public void   setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		this.name = name;
	}


	/* public: initialization interface */

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

	public CatItemView init(CatItem ts)
	{
		//~: primary key
		this.objectKey = ts.getPrimaryKey();

		//~: code
		this.code = ts.getCode();

		//~: name
		this.name = ts.getName();

		return this;
	}


	/* private: properties of the catalogue item */

	private Long   objectKey;
	private String code;
	private String name;
}