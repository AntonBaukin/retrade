package com.tverts.endure;

/* com.tverts: endure (core) */

import com.tverts.api.support.EX;
import com.tverts.endure.core.Entity;
import com.tverts.support.CMP;


/**
 * {@link United} instance has it unified mirror
 * {@link Unity} not obligatory. In the most cases
 * such a classes are candidates to be an {@link Entity}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnitedBase
       extends        NumericBase
       implements     United
{
	/* Numeric Identity */

	public Long    getPrimaryKey()
	{
		Long pk = super.getPrimaryKey();

		//?: {has no key here}
		if((pk == null) && (unity != null))
		{
			pk = unity.getPrimaryKey();
			if(pk != null)
				setPrimaryKey(pk);
		}

		return pk;
	}

	public void    setPrimaryKey(Long pk)
	{
		super.setPrimaryKey(pk);

		//?: {has unity with undefined key}
		if((pk != null) && (getUnity() != null))
			if(getUnity().getPrimaryKey() == null)
				getUnity().setPrimaryKey(pk);
	}


	/* United Interface */

	public Unity   getUnity()
	{
		return unity;
	}

	private Unity  unity;

	public void    setUnity(Unity unity)
	{
		this.unity = unity;
		setUnityKey();
	}

	protected void setUnityKey()
	{
		Long pk = super.getPrimaryKey();
		if((unity != null) && (pk != null))
			//~: assign primary key
			if(unity.getPrimaryKey() == null)
				unity.setPrimaryKey(pk);
			else
				EX.assertx(CMP.eq(pk, unity.getPrimaryKey()));
	}
}