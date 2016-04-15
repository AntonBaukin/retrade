package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.UnityType;


/**
 * This table stores the present incremented
 * values defined by domain, type and selector
 * unique triple.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      IncValue
       implements NumericIdentity, DomainEntity
{
	/* public: IncTable (bean) interface */

	public Long       getPrimaryKey()
	{
		return primaryKey;
	}

	public void       setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Domain     getDomain()
	{
		return domain;
	}

	public void       setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public UnityType  getValueType()
	{
		return valueType;
	}

	public void       setValueType(UnityType valueType)
	{
		this.valueType = valueType;
	}

	public String     getSelector()
	{
		return selector;
	}

	public void       setSelector(String selector)
	{
		this.selector = selector;
	}

	public long       getValue()
	{
		return value;
	}

	public void       setValue(long value)
	{
		this.value = value;
	}


	/* persisted attributes */

	private Long      primaryKey;
	private Domain    domain;
	private UnityType valueType;
	private String    selector;
	private long      value;
}