package com.tverts.endure.aggr;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.order.OrderIndex;


/**
 * This interface defines a component of aggregated value.
 * Each component is self-sufficient. It holds the values
 * needed to (re)calculate the aggregated value when adding
 * or discarding the component, or changing it's state or role.
 *
 * @author anton.baukin@gmail.com
 */
public interface AggrItem extends NumericIdentity, OrderIndex
{
	/* Aggregation Item */

	public AggrValue getAggrValue();

	/**
	 * Historical component stores the value of the
	 * aggregated value of that the value was the
	 * actual value of the aggregation. The present
	 * actual value may differ, but the historical one
	 * may not be removed while there are links to it
	 * from other entities of the system including
	 * components of other aggregated values.
	 */
	public boolean   isHistorical();

	/**
	 * Each component of aggregated value is assumed to
	 * have a source of it's value. A source is a Unity
	 * instance in the most cases.
	 *
	 * It is forbidden for the components of the same
	 * aggregated value to have equal source IDs that
	 * refer different database objects.
	 */
	public Long      getSourceKey();
}