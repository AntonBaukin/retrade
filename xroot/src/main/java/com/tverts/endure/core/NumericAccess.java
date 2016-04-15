package com.tverts.endure.core;

/* standard Java classes */

import java.io.Serializable;

/* com.tverts: objects */

import com.tverts.endure.NumericIdentity;
import com.tverts.objects.ObjectAccess;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure */


/**
 * Implements object access interface (as a factory)
 * to load {@link NumericIdentity} instances defined
 * by the class and the primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      NumericAccess<N extends NumericIdentity>
       implements ObjectAccess<N>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public NumericAccess(Class<N> c1ass, Long pk)
	{
		if(c1ass == null) throw new IllegalArgumentException();

		this.objectClass = c1ass;
		this.primaryKey  = pk;
	}


	/* public: ObjectAccess interface */

	public N accessObject()
	{
		return (primaryKey == null)?(null):
		  bean(GetUnity.class).getNumeric(objectClass, primaryKey);
	}


	/* protected: primary key */

	protected final Class<N> objectClass;
	protected final Long     primaryKey;
}