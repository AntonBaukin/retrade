package com.tverts.endure.keys;

/**
 * Strategy to generate new primary keys
 * for a persistent entity within the context given.
 *
 * @author anton.baukin@gmail.com
 */
public interface KeysGenerator
{
	/* public: KeysGenerator interface */

	public Object createPrimaryKey(KeysContext context);
}