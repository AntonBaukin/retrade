package com.tverts.endure;

/**
 * TODO comment Unity
 *
 * @author anton.baukin@gmail.com
 */
public class Unity implements PrimaryIdentity
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

	/* public: Unity bean interface */

	public UnityType getUnityType()
	{
		return unityType;
	}

	public void      setUnityType(UnityType unityType)
	{
		if(unityType == null)
			throw new IllegalArgumentException();

		this.unityType = unityType;
	}

	/* private: persistent attributes */

	private Long     primaryKey;
	public UnityType unityType;
}