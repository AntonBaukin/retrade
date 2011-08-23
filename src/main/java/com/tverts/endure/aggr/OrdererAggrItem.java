package com.tverts.endure.aggr;

/* Hibernate Persistence Layer */

import com.tverts.endure.order.OrderRequest;
import org.hibernate.Query;

/* com.tverts: endure (ordering) */

import com.tverts.endure.order.OrdererDefault;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Implements ordering strategy for {@link AggrItemBase}
 * instances. They store two order indices: the main
 * one, and the historical. If historical index is defined,
 * it must be equal to the main.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class OrdererAggrItem extends OrdererDefault
{
	/* public: parameters defaults */

	public static final String DEF_AGGRITEM_OWNERID_PROP = "aggrValue.id";

	public static final String DEF_HISTORY_INDEX_PROP    = "historyIndex";



	/* public: constructor */

	public OrdererAggrItem()
	{
		setOrderOwnerIDProp(DEF_AGGRITEM_OWNERID_PROP);
	}


	/* public: OrdererAggrItem (bean) interface */

	public String getHistoryIndexProp()
	{
		return historyIndexProp;
	}

	public void   setHistoryIndexProp(String p)
	{
		if((p = s2s(p)) == null) throw new IllegalArgumentException();
		this.historyIndexProp = p;
	}


	/* protected: OrdererDefault interface */

	protected boolean isThatRequest(OrderRequest request)
	{
		return AggrItemBase.class.isAssignableFrom(
		  instance(request).getClass());
	}

	protected void    spreadMoveOrderRight(OrderData odata)
	{
/*

update OrderIndex set
  $orderIndex   = $orderIndex + :insertStep,
  $historyIndex = $orderIndex + :insertStep
where ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex >= :orderIndex)

*/

		Query q = indexQuery(odata,

"update OrderIndex set\n" +
"  $orderIndex  = $orderIndex + :insertStep,\n" +
"  historyIndex = $orderIndex + :insertStep\n" +
"where ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
"  ($orderIndex >= :orderIndex)"

		).
		  setLong("insertStep", getInsertStep()).
		  setLong("orderIndex", odata.getRight().getOrderIndex());

		//!: execute update
		q.executeUpdate();

		//~: update the selected spread line
		Long[] spread = odata.getRightSpread();
		for(int i = 0;(i < spread.length);i++)
			spread[i] += getInsertStep();

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(odata,
		  odata.getRight().getOrderIndex(), null);
	}

	protected Query   indexQuery(OrderData odata, String hql)
	{
		return super.indexQuery(odata, hql.
		  replace("$historyIndex", getHistoryIndexProp())
		);
	}


	/* private: parameters of the strategy */

	private String historyIndexProp = DEF_HISTORY_INDEX_PROP;
}