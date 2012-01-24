package com.tverts.endure.keys;

/**
 * COMMENT KeysGeneratorBinder
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