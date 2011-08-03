package com.tverts.endure.aggr;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * Loads {@link AggrValue} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAggrValue")
public class GetAggrValue extends GetObjectBase
{
	/* Get Aggregated Value */

	/**
	 * Finds the aggregated value of the given type and
	 * the owner. Selector' primary key is optional.
	 *
	 * @throws IllegalStateException
	 *   when there are more than one Aggregated Value
	 *   exists for the specified parameters.
	 */
	@Transactional
	public AggrValue getAggrValue(Unity owner, UnityType aggrType, Long selectorID)
	{

/*

from AggrValue where (owner = :owner) and
  (aggrType = :aggrType) and (selectorID is null)

from AggrValue where (owner = :owner) and
  (aggrType = :aggrType) and (selectorID = :selectorID)

*/

		final String Q1 =

"from AggrValue where (owner = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorID is null)";

		final String Q2 =

"from AggrValue where (owner = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorID = :selectorID)";

		Query        q  = Q((selectorID == null)?(Q1):(Q2));

		//HINT: we expect 0 or 1 instances, but have to detect more.
		q.setMaxResults(2);

		q.setParameter("owner",    owner);
		q.setParameter("aggrType", aggrType);

		if(selectorID != null)
			q.setParameter("selectorID", selectorID);

		//~:
		List         r = q.list();


		//?: {no id entries were found}
		if(r.size() == 0)
			return null;

		//?: {the value exists}
		if(r.size() == 1)
			return (AggrValue)r.get(0);

		//!: ambiguity
		throw new IllegalStateException();
	}
}