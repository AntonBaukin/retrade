package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;

/**
 * TODO comment Entity
 *
 * @author anton.baukin@gmail.com
 */
public abstract class Entity implements United
{
	/* public: PrimaryIdentity interface */

	public Long getPrimaryKey()
	{
		if((primaryKey == null) && (unity != null))
			primaryKey = unity.getPrimaryKey();

		return primaryKey;
	}

	public void setPrimaryKey(Long pk)
	{
		//?: {try to undefine the key}
		if((pk == null) && (getPrimaryKey() != null))
			throw new IllegalArgumentException(
			  "Primary key of an Entity may not be set to undefined!"
			);

		//?: {try to change the key}
		if((pk != null) && (getPrimaryKey() != null) &&
		   !pk.equals(unity.getPrimaryKey())
		  )
			throw new IllegalArgumentException(
			  "Primary key of an Entity may not be changed!"
			);

		this.primaryKey = pk;
	}

	/* public: United interface */

	public Unity getUnity()
	{
		return unity;
	}

	public void  setUnity(Unity unity)
	{
		//?: {try to undefine the unity}
		if((this.unity != null) && (unity == null))
			throw new IllegalArgumentException(
			  "Unified mirrow of an Entity may not be set to undefined!"
			);

		//?: {unity is not defined} nothing to do
		if(unity == null) return;

		//?: {unity has differ primary key}
		if((getPrimaryKey() != null) && (unity.getPrimaryKey() != null) &&
		   !getPrimaryKey().equals(unity.getPrimaryKey())
		  )
			throw new IllegalArgumentException(
			  "Unified mirrow of an Entity may not have differ primary key value!"
			);

		this.unity = unity;
	}

	/* private: persistent attributes */

	private Long  primaryKey;
	private Unity unity;
}