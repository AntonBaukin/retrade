package com.tverts.endure.locks;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectPrototypeBase;

/* com.tverts: endure core */

import com.tverts.endure.UnityType;


/**
 * Loads {@link Lock} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getLock") @Scope("prototype")
public class GetLock extends GetObjectPrototypeBase
{
	/* Get Lock */

	public Lock getLock(Long owner, UnityType type)
	{
		if(owner == null) throw new IllegalArgumentException();
		if(type  == null) throw new IllegalArgumentException();


// from Lock where (owner.id = :owner) and (lockType = :type)

		return (Lock) session().createQuery(

"from Lock where (owner.id = :owner) and (lockType = :type)"

		).
		  setLong     ("owner", owner).
		  setParameter("type",  type).
		  uniqueResult();
	}

	public Lock getLockStrict(Long owner, UnityType type)
	{
		Lock res = this.getLock(owner, type);

		if(res == null) throw new IllegalStateException(String.format(
		  "Can't find Lock instance for owner [%d] and type %s!",
		  owner, type.toString()
		));

		return res;
	}
}