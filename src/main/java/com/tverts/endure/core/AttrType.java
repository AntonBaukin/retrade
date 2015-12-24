package com.tverts.endure.core;

/* com.tverts: endure (core) */

import com.tverts.endure.OxNumericBase;
import com.tverts.endure.UnityType;


/**
 * Type of Unity Attribute.
 *
 * @author anton.baukin@gmail.com.
 */
public class AttrType extends OxNumericBase
{
	/**
	 * Domain where this type is located.
	 */
	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	/**
	 * Type of attribute defines a scope of
	 * the names within a Domain. Class of
	 * ox-object depends on this type.
	 */
	public UnityType getAttrType()
	{
		return attrType;
	}

	private UnityType attrType;

	public void setAttrType(UnityType unityType)
	{
		this.attrType = unityType;
	}

	/**
	 * Name of the attribute up to 255 symbols.
	 * Must be unique within (Domain, Type) pair.
	 */
	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}
}