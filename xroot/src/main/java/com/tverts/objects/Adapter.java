package com.tverts.objects;

/* Java */

import java.io.Serializable;


/**
 * A strategy to represent the given instance
 * as instance of else interface.
 *
 * Adaptor must be a XML-compatible Java Bean.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Adapter extends Serializable
{
	/* public: Adaptor interface */

	public Object adapt(Object o);
}