package com.tverts.model;

/**
 * Incapsulates strategy of accessing user Model Point.
 * The access parameters are defined implicitly by the
 * system query context.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ModelAccess
{
	/* public: ModelAccess interface */

	public ModelPoint accessModel();
}