package com.tverts.endure.aggr;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: endure (aggregation calculation)s */

import com.tverts.endure.aggr.calc.AggrCalc;


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
	public AggrValue getAggrValue(Long owner, UnityType aggrType, Long selectorKey)
	{
		if(owner == null)     throw new IllegalArgumentException();
		if(aggrType == null)  throw new IllegalArgumentException();

/*

from AggrValue where (owner.id = :owner) and
  (aggrType = :aggrType) and (selectorKey is null)

from AggrValue where (owner.id = :owner) and
  (aggrType = :aggrType) and (selectorKey = :selectorKey)

*/

		final String Q1 =

"from AggrValue where (owner.id = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorKey is null)";

		final String Q2 =

"from AggrValue where (owner.id = :owner) and\n" +
"  (aggrType = :aggrType) and (selectorKey = :selectorKey)";

		Query        q  = Q((selectorKey == null)?(Q1):(Q2));

		//HINT: we expect 0 or 1 instances, but have to detect more.
		q.setMaxResults(2);

		q.setParameter("owner",    owner);
		q.setParameter("aggrType", aggrType);

		if(selectorKey != null)
			q.setParameter("selectorKey", selectorKey);

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

	public AggrValue getAggrValue(Long owner, String aggrType, Long selectorKey)
	{
		return getAggrValue(owner,
		  UnityTypes.unityType(AggrValue.class, aggrType), selectorKey);
	}

	public AggrValue loadAggrValue(Long owner, UnityType aggrType, Long selectorKey)
	{
		AggrValue result = getAggrValue(owner, aggrType, selectorKey);

		if(result == null) throw new IllegalStateException(
		  "Couldn't load Aggregated Value of the type " + aggrType.toString() +
		  " for owning Unity with primary key " + owner + "!");

		return result;
	}

	public AggrValue loadAggrValue(Long owner, String aggrType, Long selectorKey)
	{
		return loadAggrValue(owner,
		  UnityTypes.unityType(AggrValue.class, aggrType), selectorKey);
	}


	/* Get Aggregation Calculation (general) */

	@SuppressWarnings("unchecked")
	public List<AggrCalc> getAggrCalcs(AggrValue av)
	{

// from AggrCalc where aggrValue = :av

		return (List<AggrCalc>) Q(
		  "from AggrCalc where aggrValue = :av"
		).
		  setParameter("av", av).
		  list();
	}

	public AggrCalc       getAggrCalc(AggrValue av, UnityType calcType)
	{

/*

from AggrCalc where (aggrValue = :av)
  and (unity.unityType = :calcType)

*/
		return (AggrCalc) Q(

"from AggrCalc where (aggrValue = :av)\n" +
"  and (unity.unityType = :calcType)"

		).
		  setParameter("av",       av).
		  setParameter("calcType", calcType).
		  uniqueResult();
	}
}