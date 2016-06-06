package com.tverts.support.fs;

/* com.tverts: support */

import com.tverts.support.EX;

/**
 * Runnable that throws an exception.
 *
 * @author anton.baukin@gmail.com.
 */
@FunctionalInterface
public interface Callable extends Runnable
{
	public void  call()
	  throws Throwable;

	default void run()
	{
		try
		{
			this.call();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}
}
