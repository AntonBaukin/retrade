package com.tverts.endure.types;

/* com.tverts: endure */

import java.util.Collections;
import java.util.List;

import com.tverts.endure.UnityType;


/**
 * A record describing {@link UnityType}
 * in Spring configuration.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class UnityTypeStruct implements UnityTypeStructReference
{
	/* public: UnityTypeStruct (bean) interface */

	public Class      getTypeClass()
	{
		return typeClass;
	}

	public void       setTypeClass(Class typeClass)
	{
		this.typeClass = typeClass;
	}

	public String     getTypeName()
	{
		return typeName;
	}

	public void       setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public Character  getTypeFlag()
	{
		return typeFlag;
	}

	public void       setTypeFlag(Character typeFlag)
	{
		this.typeFlag = typeFlag;
	}

	public String     getTitle()
	{
		return title;
	}

	public void       setTitle(String title)
	{
		this.title = title;
	}

	public String     getTitleLo()
	{
		return titleLo;
	}

	public void       setTitleLo(String titleLo)
	{
		this.titleLo = titleLo;
	}


	/* public: UnityTypeStruct interface (processing support) */

	public UnityType  getUnityType()
	{
		return unityType;
	}

	public void       setUnityType(UnityType unityType)
	{
		this.unityType = unityType;
	}


	/* public: UnityTypeStructReference interface */

	public List<UnityTypeStruct> dereferObjects()
	{
		return Collections.singletonList(this);
	}


	/* private: Unity Type attributes */

	private Class     typeClass;
	private String    typeName;
	private Character typeFlag;
	private String    title;
	private String    titleLo;

	/* private: support properties */

	private UnityType unityType;
}