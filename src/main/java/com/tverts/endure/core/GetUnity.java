package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure */

import com.tverts.endure.Unity;


/**
 * Loads {@link Unity} instances and provides support
 * routines to handle them.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("getUnity")
public class GetUnity extends GetObjectBase
{
	/* Get Unity */

	public Unity getUnity(Long primaryKey)
	{
		return (Unity)session().get(Unity.class, primaryKey);
	}
}