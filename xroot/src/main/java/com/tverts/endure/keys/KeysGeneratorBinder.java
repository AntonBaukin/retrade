package com.tverts.endure.keys;

/**
 * Strategy to access entities primary keys
 * generation strategy.
 *
 * @author anton.baukin@gmail.com
 */
public interface KeysGeneratorBinder
{
	/* public: KeysGeneratorBinder interface */

	public String        getGeneratorName();

	public KeysGenerator getGeneratorBound();

	public void          bindGenerator();
}