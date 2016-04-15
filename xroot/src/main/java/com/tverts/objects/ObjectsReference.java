package com.tverts.objects;

/* standard Java classes */

import java.util.List;

/**
 * Represents a reference to an ordered collection
 * of typed objects.
 *
 * @author anton.baukin@gmail.com
 */
public interface ObjectsReference<O>
{
	/* public: ObjectsReference interface */

	public List<O> dereferObjects();
}