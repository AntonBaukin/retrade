package com.tverts.endure;

/* com.tverts: support */

import static com.tverts.support.OU.cls;
import static com.tverts.support.SU.s2s;


/**
 * COMMENT UnityType
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


	/* public: UnityType (bean) interface */

	public String getTypeName()
	{
		return typeName;
	}

	public void   setTypeName(String typeName)
	{
		if((typeName = s2s(typeName)) == null)
			throw new IllegalArgumentException();
		this.typeName = typeName;
	}

	public Class  getTypeClass()
	{
		return typeClass;
	}

	public void   setTypeClass(Class typeClass)
	{
		if(typeClass == null) throw new IllegalArgumentException();
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

	public String getTitle()
	{
		return title;
	}

	public void   setTitle(String title)
	{
		this.title = title;
	}

	public String getTitleLo()
	{
		return titleLo;
	}

	public void   setTitleLo(String titleLo)
	{
		this.titleLo = titleLo;
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


	/* public: Object interface */

	public boolean   equals(Object t)
	{
		if(this == t)
			return true;

		if(!(t instanceof UnityType))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((UnityType)t).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public boolean   equals(Class typeClass, String typeName)
	{
		return (getTypeClass() != null) && getTypeClass().equals(typeClass) &&
		  (getTypeName() != null) && getTypeName().equals(typeName);
	}

	public int       hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}

	public String    toString()
	{
		return String.format(
		  "Unity Type [%c%c] '%s' class '%s'",

		  getSystemFlag(), getTypeFlag(),
		  getTypeName(), cls(getTypeClass())
		);
	}

	/* private: persistent attributes */

	private Long      primaryKey;

	private String    typeName;
	private Class     typeClass;
	private char      typeFlag   = '?';
	private char      systemFlag = 'S';

	private String    title;
	private String    titleLo;
}