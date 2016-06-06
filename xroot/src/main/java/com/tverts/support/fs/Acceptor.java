package com.tverts.support.fs;

/* Java */

import java.util.function.Consumer;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Consumer that throws exception.
 *
 * @author anton.baukin@gmail.com.
 */
@FunctionalInterface
public interface Acceptor<T> extends Consumer<T>
{
	public void  invoke(T object)
	  throws Throwable;

	default void accept(T t)
	{
		try
		{
			this.invoke(t);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}
}
