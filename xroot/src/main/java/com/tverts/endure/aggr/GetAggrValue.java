package com.tverts.endure.aggr;

/* Java */

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

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Loads {@link AggrValue} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAggrValue")
public class GetAggrValue extends GetObjectBase
{
	/* Get Aggregated Value */

	public AggrValue getAggrValue(Long pk)
	{
		return get(AggrValue.class, pk);
	}

	/**
	 * Finds the aggregated value of the given type and
	 * the owner. Selector' primary key is optional.
	 */
	public AggrValue getAggrValue(Long owner, UnityType aggrType, Long selectorKey)
	{
		EX.assertn(owner);
		EX.assertn(aggrType);

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

		//~: query
		List r = q.list();


		//?: {no id entries were found}
		if(r.size() == 0)
			return null;

		//?: {the value exists}
		if(r.size() == 1)
			return (AggrValue)r.get(0);

		//!: ambiguity
		throw EX.state("There are more than one aggregated value of the type ",
		  aggrType, " do exist for owning Unity with primary key ", owner, "!");
	}

	public AggrValue getAggrValue(Long owner, String aggrType, Long selectorKey)
	{
		return getAggrValue(owner, UnityTypes.unityType(
		  AggrValue.class, aggrType), selectorKey);
	}

	public AggrValue loadAggrValue(Long owner, UnityType aggrType, Long selectorKey)
	{
		return EX.assertn(getAggrValue(owner, aggrType, selectorKey));
	}

	public AggrValue loadAggrValue(Long owner, String aggrType, Long selectorKey)
	{
		return loadAggrValue(owner, UnityTypes.unityType(
		  AggrValue.class, aggrType), selectorKey);
	}


	/* Get Aggregation Calculation (general) */

	public List<AggrCalc> getAggrCalcs(AggrValue av)
	{
		return list(AggrCalc.class,
		  "from AggrCalc where aggrValue = :av",
		  "av", av
		);
	}

	public AggrCalc       getAggrCalc(AggrValue av, UnityType calcType)
	{

/*

from AggrCalc where (aggrValue = :av)
  and (unity.unityType = :calcType)

*/
		final String Q =

"from AggrCalc where (aggrValue = :av)\n" +
" and (unity.unityType = :calcType)";

		return object(AggrCalc.class, Q, "av", av, "calcType", calcType);
	}


	/* Support for Aggregation Service */


	/**
	 * Returns primary keys of aggregated values having
	 * pending requests.
	 */
	public List<Long>        getAggrRequests()
	{
		final String Q =
"select distinct aggrValue.id from AggrRequest";

		return list(Long.class, Q);
	}

	public int               countAggrRequests()
	{
		final String Q =
"select count(id) from AggrRequest";

		return object(Number.class, Q).intValue();
	}


	public List<AggrRequest> getAggrRequests(long aggrValue)
	{
		final String Q =
"from AggrRequest where (aggrValue.id = :av) order by id";

		return list(AggrRequest.class, Q, "av", aggrValue);
	}
}