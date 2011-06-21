package com.tverts.shunts;

/**
 * Plain Self Shunts are POJO targets for
 * {@link SelfShuntTarget} units.
 *
 * This implementation makes the definition
 * of a plain shunt look better.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ShuntPlain
       implements     Cloneable
{
	/* public: Cloneable interface */

	public ShuntPlain clone()
	{
		try
		{
			return (ShuntPlain)super.clone();
		}
		catch( CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}