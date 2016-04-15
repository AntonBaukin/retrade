package com.tverts.objects;

/* Java */

import java.io.Serializable;


/**
 * Access object by direct reference given.
 *
 * @author anton.baukin@gmail.com
 */
public class      ObjectAccessRef<O>
       implements ObjectAccess<O>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public ObjectAccessRef(O ref)
	{
		this.ref = ref;
	}


	/* public: ObjectAccess interface */

	public O accessObject()
	{
		return ref;
	}


	/* protected: the reference */

	protected final O ref;
}
