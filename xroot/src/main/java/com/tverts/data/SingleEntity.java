package com.tverts.data;

/* Java */

import java.io.Serializable;


/**
 * Data object related to single United instance.
 *
 * @author anton.baukin@gmail.com
 */
public class SingleEntity implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Long getUnityType()
	{
		return unityType;
	}

	public void setUnityType(Long unityType)
	{
		this.unityType = unityType;
	}


	/* private: entity reference */

	private Long primaryKey;
	private Long unityType;
}