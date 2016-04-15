package com.tverts.objects;

/**
 * Container of return value from
 * a inline class body.
 *
 * @author anton.baukin@gmail.com.
 */
public class Result<T>
{
	/* public: Result interface */

	public T    get()
	{
		return result;
	}

	public void set(T result)
	{
		this.result = result;
	}


	/* private: the result */

	private T result;
}