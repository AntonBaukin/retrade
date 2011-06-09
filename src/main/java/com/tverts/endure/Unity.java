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

	public void setPrimaryKey(Long pk)
	{
		if((this.primaryKey != null) && (pk == null))
			throw new IllegalArgumentException(
			  "Primary key of Unity may not be set to undefined!"
			);

		if((this.primaryKey != null) && !this.primaryKey.equals(pk))
			throw new IllegalArgumentException(
			  "Primary key of Unity may not be changed!"
			);

		this.primaryKey = pk;
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