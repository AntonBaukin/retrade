package com.tverts.endure.core;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


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

	public Unity  getUnity(Long primaryKey)
	{
		return (Unity)session().get(Unity.class, primaryKey);
	}

	/**
	 * Loads the actual instance having this
	 * Unity object as it's unified mirror.
	 */
	public United getUnited(Long primaryKey)
	{

/*

select ut from Unity u join u.unityType ut
  where u.primaryKey = :primaryKey

*/
		UnityType ut = (UnityType) Q(

"select ut from Unity u join u.unityType ut\n" +
"  where u.primaryKey = :primaryKey"

		).
		  setLong("primaryKey", primaryKey).
		  uniqueResult();

		//?: {not found it}
		if(ut == null) return null;

		//?: {not an entity type}
		if(!ut.isEntityType())
			throw new IllegalStateException(String.format(
			  "Requested United instance [%d] has Unity type not " +
			  "of an entity, but: %s!", primaryKey, ut.toString()));

		//?: {not a united class}
		if(!United.class.isAssignableFrom(ut.getTypeClass()))
			throw new IllegalStateException(String.format(
			  "Requested United instance [%d] has Unity type not " +
			  "of United class, but: %s!", primaryKey, ut.toString()));

		return (United)(session().get(ut.getTypeClass(), primaryKey));
	}
}