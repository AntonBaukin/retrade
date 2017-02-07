package com.tverts.endure.aggr;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: endure (ordering) */

import com.tverts.endure.order.OrdererDefault;
import com.tverts.endure.order.OrderIndex;
import com.tverts.endure.order.OrderRequest;

/* com.tverts: support */

import com.tverts.support.SU;


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
		setOrderOwnerIdProp(DEF_AGGRITEM_OWNERID_PROP);
	}


	/* public: OrdererAggrItem (bean) interface */

	public String getHistoryIndexProp()
	{
		return historyIndexProp;
	}

	public void   setHistoryIndexProp(String p)
	{
		if((p = SU.s2s(p)) == null) throw new IllegalArgumentException();
		this.historyIndexProp = p;
	}


	/* protected: OrdererDefault interface */

	protected boolean isThatRequest(OrderRequest request)
	{
		return AggrItemBase.class.isAssignableFrom(
		  HiberPoint.type(request.getInstance()));
	}

	protected String  spreadReservePlaceRightQuery()
	{
/*

 update OrderIndex set
   $orderIndex   = $orderIndex   + :smove,
   $historyIndex = $historyIndex + :smove
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)

*/
		return

"update OrderIndex set\n" +
"  $orderIndex   = $orderIndex   + :smove,\n" +
"  $historyIndex = $historyIndex + :smove\n" +
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)";

	}

	protected String  spreadReservePlaceLeftQuery()
	{
/*

 update OrderIndex set
   $orderIndex   = $orderIndex   - :smove,
   $historyIndex = $historyIndex - :smove
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)

*/
		return

"update OrderIndex set\n" +
"  $orderIndex   = $orderIndex   - :smove,\n" +
"  $historyIndex = $historyIndex - :smove\n" +
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)";

	}

	protected String  spreadMoveOrderRightQuery()
	{

/*

 update OrderIndex set
   $orderIndex   = $orderIndex   + :insertStep,
   $historyIndex = $historyIndex + :insertStep
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex >= :orderIndex)

*/
		return

"update OrderIndex set\n" +
"  $orderIndex   = $orderIndex   + :insertStep,\n" +
"  $historyIndex = $historyIndex + :insertStep\n" +
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex >= :orderIndex)";

	}

	protected Query   indexQuery(OrderData odata, String hql)
	{
		return super.indexQuery(odata,
		  SU.replace(hql, "$historyIndex", getHistoryIndexProp())
		);
	}

	protected void    updateOrderIndex(OrderIndex entity, long oi)
	{
		entity.setOrderIndex(oi);

		//~: update the history index
		if(((AggrItemBase)entity).isHistorical())
			((AggrItemBase)entity).setHistoryIndex(oi);
	}


	/* private: parameters of the strategy */

	private String historyIndexProp = DEF_HISTORY_INDEX_PROP;
}