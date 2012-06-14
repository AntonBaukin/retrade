package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;

/**
 * Represents persistent property assigned on the
 * system level, or within the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Property implements NumericIdentity
{
	/* public: Property (bean) interface */

	public Long    getPrimaryKey()
	{
		return primaryKey;
	}

	public void    setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		this.name = name;
	}

	public String  getArea()
	{
		return area;
	}

	public void    setArea(String area)
	{
		this.area = area;
	}

	public Domain  getDomain()
	{
		return domain;
	}

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public Class   getType()
	{
		return type;
	}

	public void    setType(Class type)
	{
		this.type = type;
	}

	public String  getValue()
	{
		return value;
	}

	public void    setValue(String value)
	{
		this.value = value;
	}

	public String  getObject()
	{
		return object;
	}

	public void    setObject(String object)
	{
		this.object = object;
	}


	/* persisted attributes */

	private Long   primaryKey;
	private String name;
	private String area;
	private Domain domain;
	private Class  type;
	private String value;
	private String object;
}