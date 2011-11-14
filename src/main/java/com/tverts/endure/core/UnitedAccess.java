package com.tverts.endure.core;

/* standard Java classes */

import java.io.Serializable;

/* com.tverts: objects */

import com.tverts.objects.ObjectAccess;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure */

import com.tverts.endure.United;


/**
 * Implements object access interface (as a factory)
 * to load {@link United} instances defined by the
 * primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      UnitedAccess<U extends United>
       implements ObjectAccess<U>, Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: constructor */

	public UnitedAccess(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: ObjectAccess interface */

	@SuppressWarnings("unchecked")
	public U accessObject()
	{
		return (primaryKey == null)?(null):
		  (U)bean(GetUnity.class).getUnited(primaryKey);
	}


	/* protected: primary key */

	protected final Long primaryKey;
}