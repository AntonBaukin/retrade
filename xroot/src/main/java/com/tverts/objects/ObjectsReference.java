package com.tverts.objects;

/* Java */

import java.util.List;


/**
 * Represents a reference to an ordered
 * collection of typed objects.
 *
 * @author anton.baukin@gmail.com
 */
public interface ObjectsReference<O>
{
	/* Objects Reference */

	public List<O> dereferObjects();
}