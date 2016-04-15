package com.tverts.model;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Class that stores additional information
 * about a {@link ModelBean} that returns it.
 *
 * @author anton.baukin@gmail.com.
 */
public class ModelInfo
{
	/**
	 * Tells the prefix string to use when
	 * generating Model Bean key.
	 *
	 * When undefined, Model Bean class'
	 * simple name is taken.
	 */
	public String keysPrefix;

	public ModelInfo keysPrefix(String kp)
	{
		this.keysPrefix = (kp == null)?(null):EX.asserts(kp);
		return this;
	}

	/**
	 * Estimates the size of the model bean.
	 * Defaults to 0 that means the bean is 'small'.
	 * Implementation may calculate the size only
	 * of the large parts (texts, bytes) of the model.
	 *
	 * This value is considered by the store to find
	 * whether to store the model in the memory cache.
	 */
	public int size;

	public ModelInfo size(int size)
	{
		this.size = size;
		return this;
	}
}