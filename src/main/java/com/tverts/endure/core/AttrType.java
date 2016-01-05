package com.tverts.endure.core;

/* com.tverts: endure (core, catalogues) */

import com.tverts.endure.OxNumericBase;
import com.tverts.endure.UnityType;
import com.tverts.endure.cats.NamedEntity;


/**
 * Type of Unity Attribute.
 *
 * @author anton.baukin@gmail.com.
 */
public class      AttrType
       extends    OxNumericBase
       implements NamedEntity
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

	public String getNameLo()
	{
		return nameLo;
	}

	private String nameLo;

	public void setNameLo(String nameLo)
	{
		this.nameLo = nameLo;
	}

	/**
	 * Tells whether this type may be altered or removed.
	 */
	public boolean isSystem()
	{
		return system;
	}

	private boolean system;

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	/**
	 * Tells that this attribute type supports array of values.
	 */
	public boolean isArray()
	{
		return array;
	}

	private boolean array;

	public void setArray(boolean array)
	{
		this.array = array;
	}

	/**
	 * Tells that this attribute type may be shared.
	 * (Used for attributes of sub-goods.)
	 */
	public boolean isShared()
	{
		return shared;
	}

	private boolean shared;

	public void setShared(boolean shared)
	{
		this.shared = shared;
	}
}