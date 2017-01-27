package com.tverts.endure.core;

/* Java */

import java.math.BigDecimal;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * General attribute of United object
 * having it's mirror, {@link Unity}.
 *
 * @author anton.baukin@gmail.com
 */
public class UnityAttr extends NumericBase
{
	/* Attribute Selector */

	/**
	 * Reference to the Unity owning attribute.
	 */
	public Unity getUnity()
	{
		return unity;
	}

	private Unity unity;

	public void setUnity(Unity unity)
	{
		this.unity = unity;
	}

	/**
	 * Type of the attribute that is unique for a Unity.
	 */
	public AttrType getAttrType()
	{
		return attrType;
	}

	private AttrType attrType;

	public void setAttrType(AttrType attrType)
	{
		this.attrType = attrType;
	}

	/**
	 * Index for values of array or list types.
	 */
	public Integer getIndex()
	{
		return index;
	}

	private Integer index;

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	/**
	 * The source of the attribute defined
	 * when it's taken from some else entity.
	 * Valid for shared attribute types.
	 */
	public UnityAttr getSource()
	{
		return source;
	}

	private UnityAttr source;

	public void setSource(UnityAttr source)
	{
		this.source = source;
	}


	/* Attribute Values */

	public String getString()
	{
		return string;
	}

	private String string;

	public void setString(String string)
	{
		this.string = string;
	}

	public Long getInteger()
	{
		return integer;
	}

	private Long integer;

	public void setInteger(Long integer)
	{
		this.integer = integer;
	}

	public BigDecimal getNumber()
	{
		return number;
	}

	private BigDecimal number;

	public void setNumber(BigDecimal number)
	{
		this.number = number;
	}

	public byte[] getBytes()
	{
		return bytes;
	}

	private byte[] bytes;

	public void setBytes(byte[] bytes)
	{
		this.bytes = bytes;
	}
}