package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure */

import com.tverts.endure.UnityType;


/**
 * Loads {@link UnityType} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getUnityType")
public class GetUnityType extends GetObjectBase
{
	/* Get UnityType */

	public UnityType getUnityType(Long id)
	{
		return (UnityType)sessionFactory.getCurrentSession().
		  get(UnityType.class, id);
	}

	public UnityType findUnityType(Class typeClass, String typeName)
	{
/*

from UnityType ut where
  ut.typeClass = :typeClass and ut.typeName = :typeName

*/
		return (UnityType) Q(

		  "from UnityType ut where\n" +
		  "  ut.typeClass = :typeClass and ut.typeName = :typeName"

		).
		  setParameter("typeClass", typeClass).
		  setParameter("typeName",  typeName).
		  uniqueResult();
	}
}