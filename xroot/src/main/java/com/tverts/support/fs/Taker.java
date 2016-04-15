package com.tverts.support.fs;

/**
 * Consumer that throws exception.
 *
 * @author anton.baukin@gmail.com.
 */
public interface Taker<T>
{
	public void take(T object)
	  throws Throwable;
}
