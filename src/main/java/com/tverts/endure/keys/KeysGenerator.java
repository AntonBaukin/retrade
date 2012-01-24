package com.tverts.endure.keys;

/**
 * COMMENT KeysGenerator
 *
 * @author anton.baukin@gmail.com
 */
public interface KeysGenerator
{
	/* public: KeysGenerator interface */

	public Object createPrimaryKey(KeysContext context);
}