package com.tverts.objects;

/* standard Java classes */

import java.util.List;

/**
 * Represents a reference to an ordered collection
 * of typed objects.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface ObjectsReference<O>
{
	/* public: ObjectsReference interface */

	public List<O> dereferObjects();
}