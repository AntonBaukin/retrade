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
		return (unity != null)?(unity.getPrimaryKey()):(null);
	}

	public void setPrimaryKey(Long primaryKey)
	{
		//?: {try to undefine the key}
		if((primaryKey == null) && (unity != null) &&
		   (unity.getPrimaryKey() != null)
		  )
			throw new IllegalArgumentException();

		//?: {try to change the key}
		if((primaryKey != null) && (unity != null) &&
		   !primaryKey.equals(unity.getPrimaryKey())
		  )
			throw new IllegalArgumentException();
	}

	/* public: United interface */

	public Unity getUnity()
	{
		return unity;
	}

	public void  setUnity(Unity unity)
	{
		if(unity == null)
			throw new IllegalArgumentException();

		this.unity = unity;
	}

	/* private: persistent attributes */

	private Unity unity;
}