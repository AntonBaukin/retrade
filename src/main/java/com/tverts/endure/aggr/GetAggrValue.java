package com.tverts.endure.aggr;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;


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
	public AggrValue getAggrValue(Long owner, UnityType aggrType, Long selectorId)
	{
		if(owner == null)     throw new IllegalArgumentException();
		if(aggrType == null)  throw new IllegalArgumentException();

/*

from AggrValue where (owner.id = :owner) and
  (aggrType = :aggrType) and (selectorId is null)

from AggrValue where (owner.id = :owner) and
  (aggrType = :aggrType) and (selectorId = :selectorId)

*/

		final String Q1 =

"from AggrValue where (owner.id = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorId is null)";

		final String Q2 =

"from AggrValue where (owner.id = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorId = :selectorId)";

		Query        q  = Q((selectorId == null)?(Q1):(Q2));

		//HINT: we expect 0 or 1 instances, but have to detect more.
		q.setMaxResults(2);

		q.setLong     ("owner",    owner);
		q.setParameter("aggrType", aggrType);

		if(selectorId != null)
			q.setLong  ("selectorId", selectorId);

		//~:
		List         r = q.list();


		//?: {no id entries were found}
		if(r.size() == 0)
			return null;

		//?: {the value exists}
		if(r.size() == 1)
			return (AggrValue)r.get(0);

		//!: ambiguity
		throw new IllegalStateException(
		  "There are more than one aggregated value of the type " +
		  aggrType.toString() + " do exist for owning Unity with primary key " +
		  owner + "!");
	}

	public AggrValue getAggrValue(Long owner, String aggrType, Long selectorId)
	{
		return getAggrValue(owner,
		  UnityTypes.unityType(AggrValue.class, aggrType), selectorId);
	}

	public AggrValue loadAggrValue(Long owner, UnityType aggrType, Long selectorId)
	{
		AggrValue result = getAggrValue(owner, aggrType, selectorId);

		if(result == null) throw new IllegalStateException(
		  "Couldn't load Aggregated Value of the type " + aggrType.toString() +
		  " for owning Unity with primary key " + owner + "!");

		return result;
	}

	public AggrValue loadAggrValue(Long owner, String aggrType, Long selectorId)
	{
		return loadAggrValue(owner,
		  UnityTypes.unityType(AggrValue.class, aggrType), selectorId);
	}
}