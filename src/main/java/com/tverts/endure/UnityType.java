package com.tverts.endure;

/**
 * TODO comment UnityType
 *
 * @author anton.baukin@gmail.com
 */
public class UnityType implements PrimaryIdentity
{
	/* public: PrimaryIdentity interface */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	/* public: UnityType bean interface */

	public String getTypeName()
	{
		return typeName;
	}

	public void   setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public Class  getTypeClass()
	{
		return typeClass;
	}

	public void   setTypeClass(Class typeClass)
	{
		this.typeClass = typeClass;
	}

	public char   getTypeFlag()
	{
		return typeFlag;
	}

	public void   setTypeFlag(char typeFlag)
	{
		this.typeFlag = typeFlag;
	}

	public char   getSystemFlag()
	{
		return systemFlag;
	}

	public void   setSystemFlag(char systemFlag)
	{
		this.systemFlag = systemFlag;
	}

	/* public: UnityType interface (support) */

	public boolean   isSystem()
	{
		return (getSystemFlag() == 'S');
	}

	public UnityType setSystem()
	{
		setSystemFlag('S');
		return this;
	}

	public boolean   isUserDefined()
	{
		return (getSystemFlag() == 'U');
	}

	public UnityType setUserDefined()
	{
		setSystemFlag('U');
		return this;
	}

	public boolean   isEntityType()
	{
		return (getTypeFlag() == 'E');
	}

	public UnityType setEntityType()
	{
		setTypeFlag('E');
		return this;
	}

	public boolean   isLinkType()
	{
		return (getTypeFlag() == 'L');
	}

	public UnityType setLinkType()
	{
		setTypeFlag('L');
		return this;
	}

	public boolean   isAttributeType()
	{
		return (getTypeFlag() == 'A');
	}

	public UnityType setAttributeType()
	{
		setTypeFlag('A');
		return this;
	}

	/* private: persistent attributes */

	private Long      primaryKey;

	private String    typeName;
	private Class     typeClass;
	private char      typeFlag;
	private char      systemFlag;
}