package com.tverts.endure.order;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import org.hibernate.type.LongType;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Fully implements strategy of defining sparse indices
 * of an order. Note that this strategy itself does not
 * check the type of the income request and always says
 * {@link #isThatRequest(OrderRequest)} {@code true}.
 *
 * Has two parameters:
 *  · insert step;
 *  · spread limit.
 *
 * When new item is inserted as the first of the last one
 * it's index is distanced from the closest item by the
 * insert step.
 *
 * When inserting item in the middle, and there is no
 * free space between the left and the right borders
 * of insert, that borders are moved aside. The number
 * of items to move in one side (left or right) is
 * defined by spread limit parameter.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class OrdererDefault extends OrdererBase
{
	/* public: parameters defaults */

	public static final String DEF_ORDER_OWNERID_PROP = "orderOwner.id";
	public static final String DEF_ORDER_TYPE_PROP    = "orderType";
	public static final String DEF_ORDER_INDEX_PROP   = "orderIndex";

	public static final long   DEF_INSERT_STEP        = 256;

	public static final int    DEF_SPREAD_LIMIT       = 8; //<-- 16 + 1 for both sides


	/* protected: OrdererBase interface */

	/**
	 * This strategy does not check the type of the request
	 * and always returns {@code true}.
	 */
	protected boolean isThatRequest(OrderRequest request)
	{
		return true;
	}

	protected void    order(OrderRequest request)
	{
		//~: create order strategy' data
		OrderData odata = createOrderData(request);

		//~: find the insert borders
		findInsertBorders(odata);

		//?: {insert as the first}
		if(odata.getLeft() == null)
		{
			insertFirst(odata);
			return;
		}

		//?: {insert as the last}
		if(odata.getRight() == null)
		{
			insertLast(odata);
			return;
		}

		//!: insert in the middle
		insertMiddle(odata);
	}


	/* public: access strategy parameters */

	public String getOrderOwnerIDProp()
	{
		return orderOwnerIDProp;
	}

	public void   setOrderOwnerIDProp(String p)
	{
		if((p = SU.s2s(p)) == null) throw new IllegalArgumentException();
		this.orderOwnerIDProp = p;
	}

	public String getOrderTypeProp()
	{
		return orderTypeProp;
	}

	public void   setOrderTypeProp(String p)
	{
		if((p = SU.s2s(p)) == null) throw new IllegalArgumentException();
		this.orderTypeProp = p;
	}

	public String getOrderIndexProp()
	{
		return orderIndexProp;
	}

	public void   setOrderIndexProp(String p)
	{
		if((p = SU.s2s(p)) == null) throw new IllegalArgumentException();
		this.orderIndexProp = p;
	}

	public long   getInsertStep()
	{
		return insertStep;
	}

	public void   setInsertStep(long insertStep)
	{
		if(insertStep < 1L) throw new IllegalArgumentException();
		this.insertStep = insertStep;
	}

	public int    getSpreadLimit()
	{
		return spreadLimit;
	}

	public void   setSpreadLimit(int spreadLimit)
	{
		if(spreadLimit < 0) throw new IllegalArgumentException();
		this.spreadLimit = spreadLimit;
	}


	/* protected: strategy request */

	protected class OrderData
	{
		/* public: constructor */

		public OrderData(OrderRequest request)
		{
			if(request == null) throw new IllegalArgumentException();
			this.request = request;
		}


		/* public: access order data */

		public OrderRequest getRequest()
		{
			return request;
		}

		public Class        getIndexClass()
		{
			if(indexClass != null)
				return indexClass;

			indexClass = HiberSystem.getInstance().
			  findActualClass(instance(getRequest()));

			return indexClass;
		}

		public OrderData    setIndexClass(Class indexClass)
		{
			this.indexClass = indexClass;
			return this;
		}


		/* public: order borders */

		/**
		 * Returns the instance with closest smaller index.
		 * May be undefined: the instance to insert would be the first.
		 */
		public OrderIndex   getLeft()
		{
			return left;
		}

		public OrderData    setLeft(OrderIndex left)
		{
			this.left = left;
			return this;
		}

		/**
		 * Returns the instance with closest bigger index.
		 * May be undefined: the instance to insert would be the last.
		 */
		public OrderIndex   getRight()
		{
			return right;
		}

		public OrderData    setRight(OrderIndex right)
		{
			this.right = right;
			return this;
		}


		/* private: spread lines */

		/**
		 * Indices of the items before the left border.
		 * The first value is the left border's index.
		 */
		public Long[]       getLeftSpread()
		{
			return leftSpread;
		}

		public OrderData    setLeftSpread(Long[] spread)
		{
			this.leftSpread = spread;
			return this;
		}

		/**
		 * Indices of the items after the right border.
		 * The first value is the right border's index.
		 */
		public Long[]       getRightSpread()
		{
			return rightSpread;
		}

		public OrderData    setRightSpread(Long[] spread)
		{
			this.rightSpread = spread;
			return this;
		}


		/* private: ordering data */

		private OrderRequest request;
		private Class        indexClass;


		/* private: insert borders */


		private OrderIndex   left;
		private OrderIndex   right;

		/* private: spread lines */

		private Long[]       leftSpread;
		private Long[]       rightSpread;
	}


	/* protected: strategy variants */

	protected OrderData createOrderData(OrderRequest request)
	{
		return new OrderData(request);
	}

	protected void      insertSingle(OrderData odata)
	{
		//~: just set 0 index
		instance(odata).setOrderIndex(0L);
	}

	protected void      insertFirst(OrderData odata)
	{
		//?: {there is no right (lowest) item} insert single
		if(odata.getRight() == null)
		{
			insertSingle(odata);
			return;
		}

		//~: just decrement by the insert step
		instance(odata).setOrderIndex(
		  odata.getRight().getOrderIndex() - getInsertStep()
		);
	}

	protected void      insertLast(OrderData odata)
	{
		//?: {there is no left (biggest) item} insert single
		if(odata.getLeft() == null)
		{
			insertSingle(odata);
			return;
		}

		//~: just increment by the insert step
		instance(odata).setOrderIndex(
		  odata.getLeft().getOrderIndex() + getInsertStep()
		);
	}

	/**
	 * Inserts the index between defined the left and the right
	 * index borders.
	 */
	protected void      insertMiddle(OrderData odata)
	{
		long oileft  = odata.getLeft().getOrderIndex();
		long oiright = odata.getRight().getOrderIndex();

		//?: {there is enough space to insert}
		if(oileft + 1 < oiright)
			insertMiddleNormal(odata);
		//!: there is no space left -> do spread
		else
			insertMiddleSpread(odata);
	}

	/**
	 * Selects the index between the left and the right
	 * borders when there is enough space there.
	 */
	protected void      insertMiddleNormal(OrderData odata)
	{
		long oileft  = odata.getLeft().getOrderIndex();
		long oiright = odata.getRight().getOrderIndex();

		//~: just set the index in the middle
		instance(odata).setOrderIndex((oileft + oiright)/2);
	}

	protected void      insertMiddleSpread(OrderData odata)
	{
		//!: do spread order indices
		spreadOrderIndices(odata);

		//~: insert in the middle as normal
		insertMiddleNormal(odata);
	}


	/* protected: insert borders selection */

	protected void      findInsertBorders(OrderData odata)
	{
		//?: {reference is undefined} insert as the first or the last
		if((reference(odata) == null) || (reference(odata).getOrderIndex() == null))
		{
			//?: {beforeAfter = true} as the last
			if(request(odata).isBeforeAfter())
				findInsertBorderRight(odata);
			//!: {beforeAfter = false} as the first
			else
				findInsertBorderLeft(odata);

			return;
		}

		//?: {before the reference}
		if(request(odata).isBeforeAfter())
			findInsertBorderAfter(odata);
		else
			findInsertBorderBefore(odata);
	}

	protected void      findInsertBorderLeft(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner)
  $and$orderType=:orderType$ and ($orderIndex is not null)
  and (id <> :invoice)
order by $orderIndex asc

*/
		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner)\n" +
"  $and$orderType=:orderType$ and ($orderIndex is not null)\n" +
"  and (id <> :invoice)\n" +
"order by $orderIndex asc"

		).
		  setLong("invoice", odata.getRequest().getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has right (smallest) border}
		if(!r.isEmpty())
			odata.setRight((OrderIndex)r.get(0));
	}

	protected void      findInsertBorderRight(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner)
  $and$orderType=:orderType$ and ($orderIndex is not null)
  and (id <> :invoice)
order by $orderIndex desc

*/
		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner)\n" +
"  $and$orderType=:orderType$ and ($orderIndex is not null)\n" +
"  and (id <> :invoice)\n" +
"order by $orderIndex desc"

		).
		  setLong("invoice", odata.getRequest().getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has left (biggest) border}
		if(!r.isEmpty())
			odata.setLeft((OrderIndex)r.get(0));
	}

	protected void      findInsertBorderBefore(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner)
  $and$orderType=:orderType$ and ($orderIndex < :orderIndex)
  and (id <> :invoice)
order by $orderIndex desc

*/

		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner)\n" +
"  $and$orderType=:orderType$ and ($orderIndex < :orderIndex)\n" +
"  and (id <> :invoice)\n" +
"order by $orderIndex desc"

		).
		  setLong("orderIndex", reference(odata).getOrderIndex()).
		  setLong("invoice",    odata.getRequest().getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has left border}
		if(!r.isEmpty())
			odata.setLeft((OrderIndex)r.get(0));

		//~: right border is the reference
		odata.setRight(reference(odata));
	}

	protected void      findInsertBorderAfter(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner)
  $and$orderType=:orderType$ and ($orderIndex > :orderIndex)
  and (id <> :invoice)
order by $orderIndex asc

*/

		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner)\n" +
"  $and$orderType=:orderType$ and ($orderIndex > :orderIndex)\n" +
"  and (id <> :invoice)\n" +
"order by $orderIndex asc"

		).
		  setLong("orderIndex", reference(odata).getOrderIndex()).
		  setLong("invoice",    odata.getRequest().getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has right border}
		if(!r.isEmpty())
			odata.setRight((OrderIndex)r.get(0));

		//~: left border is the reference
		odata.setLeft(reference(odata));
	}


	/* protected: spread order indices */

	protected void      spreadOrderIndices(OrderData odata)
	{
		//~: load indices before and after the border
		loadSpreadLines(odata);

		//?: {there was no space in the spread loaded} do total move
		if(!spreadReservePlace(odata))
			spreadMoveOrder(odata);
	}

	/**
	 * Loads the spread indices before the left border
	 * and after the right one. The borders must be defined.
	 *
	 * The maximum length of each of the arrays is
	 * {@code #getSpreadLimit() + 1}. The first index
	 * belongs to the border itself.
	 */
	@SuppressWarnings("unchecked")
	protected void      loadSpreadLines(OrderData odata)
	{

/*

select $orderIndex from OrderIndex where
  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
  ($orderIndex < :orderIndex) order by $orderIndex desc

*/

		Query      q = indexQuery(odata,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex < :orderIndex) order by $orderIndex desc"

		).
		  setLong("orderIndex", odata.getLeft().getOrderIndex()).
		  setMaxResults(getSpreadLimit());

		List<Long> r = (List<Long>)q.list();
		Long[]     a = new Long[r.size() + 1];

		a[0] = odata.getLeft().getOrderIndex();
		for(int i = 0;(i < r.size());i++)
			a[i + 1] = r.get(i);

		odata.setLeftSpread(a);


/*

select $orderIndex from OrderIndex where
  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
  ($orderIndex > :orderIndex) order by $orderIndex asc

*/
		q = indexQuery(odata,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex > :orderIndex) order by $orderIndex asc"

		).
		  setLong("orderIndex", odata.getRight().getOrderIndex()).
		  setMaxResults(getSpreadLimit());

		r = (List<Long>)q.list();
		a = new Long[r.size() + 1];

		a[0] = odata.getRight().getOrderIndex();
		for(int i = 0;(i < r.size());i++)
			a[i + 1] = r.get(i);

		odata.setRightSpread(a);
	}

	/**
	 * Tries to find free space within the loaded spread lines
	 * on the right or on the left. Returns {@code true} if the
	 * place was found.
	 *
	 * If there was no free place on both the sides a total
	 * move is performed on all the index order.
	 */
	protected boolean   spreadReservePlace(OrderData odata)
	{
		//HINT: we reserve in each spread half not to allow
		//  the distribution to slip into one direction.

		boolean r = spreadReservePlaceRight(odata);
		boolean l = spreadReservePlaceLeft(odata);

		return r | l;
	}

	protected String    spreadReservePlaceRightQuery()
	{
/*

 update OrderIndex set $orderIndex = $orderIndex + :smove
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)

*/
		return

"update OrderIndex set $orderIndex = $orderIndex + :smove\n" +
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)";

	}

	protected boolean   spreadReservePlaceRight(OrderData odata)
	{
		Long[] spread = odata.getRightSpread();

		//~: select space position
		long   smove  = 0L;
		int    spos;

		for(spos = 1;(spos < spread.length);spos++)
			//?: {is there at least one free slot available} found it!
			if((smove = spread[spos] - spread[spos - 1]) > 1L)
				break;

		//?: {not found free slot} nothing to do here
		if(smove <= 1L) return false;

		//~: select move position as the middle of available space
		smove = (smove - 1)/2; if(smove == 0L) smove = 1L;

		//~: issue UPDATE moving right all the items in the
		//   range of [right, spread[spos])

		Query q = indexQuery(odata, spreadReservePlaceRightQuery()).
		  setLong("smove",      smove).
		  setLong("startIndex", odata.getRight().getOrderIndex()).
		  setLong("endIndex",   spread[spos]);

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		odata.getRight().setOrderIndex(
		  odata.getRight().getOrderIndex() + smove
		);

		//~: update the selected spread line in the range
		for(int i = 0;(i < spos);i++)
			spread[i] += smove;

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(odata,
		  odata.getRight().getOrderIndex(), spread[spos] - 1);

		return true;
	}

	protected String    spreadReservePlaceLeftQuery()
	{
/*

 update OrderIndex set $orderIndex = $orderIndex - :smove
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)

*/
		return

"update OrderIndex set $orderIndex = $orderIndex - :smove\n" +
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)";

	}

	protected boolean   spreadReservePlaceLeft(OrderData odata)
	{
		Long[] spread = odata.getLeftSpread();

		//~: select space position
		long   smove  = 0L;
		int    spos;

		for(spos = 1;(spos < spread.length);spos++)
			//?: {is there at least one free slot available} found it!

			//HINT: the order is descending
			if((smove = spread[spos] - spread[spos - 1]) > 1L)
				break;

		//?: {not found free slot} nothing to do here
		if(smove <= 1L) return false;

		//~: select move position as the middle of available space
		smove = (smove - 1)/2; if(smove == 0L) smove = 1L;

		//~: issue UPDATE moving left all the items in the
		//   range of (spread[spos], left]

		Query q = indexQuery(odata, spreadReservePlaceLeftQuery()).
		  setLong("smove",      smove).
		  setLong("startIndex", spread[spos]).
		  setLong("endIndex",   odata.getLeft().getOrderIndex());

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		odata.getLeft().setOrderIndex(
		  odata.getLeft().getOrderIndex() - smove
		);

		//~: update the selected spread line in the range
		for(int i = 0;(i < spread.length);i++)
			spread[i] -= smove;

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(odata,
		  spread[spos] + 1, odata.getLeft().getOrderIndex());

		return true;
	}

	/**
	 * Issues UPDATE request on the right and the left halves
	 * of the order. The indices of the left and the right
	 * borders and the loaded spreads are updated.
	 *
	 * Note that in present implementation only the right
	 * half is actually moved.
	 */
	protected void      spreadMoveOrder(OrderData odata)
	{
		//~: move the right half
		spreadMoveOrderRight(odata);
	}

	protected String    spreadMoveOrderRightQuery()
	{

/*

 update OrderIndex set $orderIndex = $orderIndex + :insertStep
 where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex >= :orderIndex)

*/
		return

"update OrderIndex set $orderIndex = $orderIndex + :insertStep\n" +
" where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"   ($orderIndex >= :orderIndex)";

	}

	protected void      spreadMoveOrderRight(OrderData odata)
	{
		Query q = indexQuery(odata, spreadMoveOrderRightQuery()).
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

	@SuppressWarnings("unchecked")
	protected void      reloadUpdatedOrderIndices
	  (OrderData odata, Long left, Long right)
	{
		//~: get the instances in the persistence context
		Set entities = HiberSystem.getInstance().
		  findAttachedEntities(session(odata), odata.getIndexClass());

		//~: remove our goal instance (it is set later)
		entities.remove(instance(odata));

		//~: remove instances that are on the left
		for(Iterator i = entities.iterator();(i.hasNext());)
		{
			Long oi = ((OrderIndex)i.next()).getOrderIndex();

			if(oi == null)
			{
				i.remove();
				continue;
			}

			if((left != null) && (oi < left))
			{
				i.remove();
				continue;
			}

			if((right != null) && (oi > right))
			{
				i.remove();
				//continue;
			}
		}

		//?: {nothing to update} quit
		if(entities.isEmpty()) return;

		//~: map the ids of the instances to update
		HashMap<Long, OrderIndex> updates =
		  new HashMap<Long, OrderIndex>(entities.size());

		for(Object e : entities)
			updates.put(((OrderIndex)e).getPrimaryKey(), (OrderIndex)e);

/*

select oi.id, oi.$orderIndex from OrderIndex oi
  where oi.id in (:orderIndices)

*/

		Query           query = indexQuery(odata,

"select oi.id, oi.$orderIndex from OrderIndex oi\n" +
"  where oi.id in (:orderIndices)"

		);

		ArrayList<Long> qkeys = new ArrayList<Long>(10);
		Set<Long>       ukeys = updates.keySet();
		List            inds;

		while(!updates.isEmpty())
		{
			//~: copy first 10 keys (not to overload SQL parser)
			qkeys.clear();
			for(Long ukey : ukeys)
			{
				qkeys.add(ukey);
				if(qkeys.size() >= 10) break;
			}

			//~: execute the query
			query.setParameterList("orderIndices", qkeys, LongType.INSTANCE);
			inds = query.list();

			//~: reassign the indices updated
			for(Object ind : inds)
			{
				long id = ((Number)((Object[])ind)[0]).longValue();
				long oi = ((Number)((Object[])ind)[1]).longValue();

				updateOrderIndex(updates.get(id), oi);
			}

			//~: remove the keys updated
			ukeys.removeAll(qkeys);
		}
	}

	protected void      updateOrderIndex(OrderIndex entity, long oi)
	{
		entity.setOrderIndex(oi);
	}


	/* protected: queries building */

	protected final String _AND_ORDER_TYPE_EQ_ =
	  "$and$orderType=:orderType$";

	protected Query        indexQuery(OrderData odata, String hql)
	{
		String Q = hql;

		Q = SU.replace(Q, "$orderOwner", getOrderOwnerIDProp());
		Q = SU.replace(Q, "$orderIndex", getOrderIndexProp());

		//?: {has order type defined}
		if(orderType(odata) == null)
			Q = SU.replace(Q, _AND_ORDER_TYPE_EQ_, "");
		else
			Q = SU.replace(Q, _AND_ORDER_TYPE_EQ_, String.format(
			  "and (%s = :orderType)", getOrderTypeProp()));

		Query q = HiberPoint.query(session(request(odata)), Q,
		  "OrderIndex", odata.getIndexClass());

		//~: set order owner
		if(Q.contains(":orderOwner"))
			q.setLong("orderOwner", orderOwnerID(odata));

		//~: set order type
		if(Q.contains(":orderType"))
			q.setParameter("orderType", orderType(odata));

		return q;
	}


	/* protected: support functions */

	protected OrderRequest request(OrderData odata)
	{
		return odata.getRequest();
	}

	protected OrderIndex   instance(OrderData odata)
	{
		return instance(request(odata));
	}

	protected OrderIndex   reference(OrderData odata)
	{
		return reference(request(odata));
	}

	protected Session      session(OrderData odata)
	{
		return session(odata.getRequest());
	}

	protected Long         orderOwnerID(OrderData odata)
	{
		return orderOwnerID(request(odata));
	}

	protected UnityType    orderType(OrderData odata)
	{
		return orderType(request(odata));
	}


	/* private: parameters of the strategy */

	private String orderOwnerIDProp = DEF_ORDER_OWNERID_PROP;
	private String orderTypeProp    = DEF_ORDER_TYPE_PROP;
	private String orderIndexProp   = DEF_ORDER_INDEX_PROP;

	private long insertStep         = DEF_INSERT_STEP;
	private int  spreadLimit        = DEF_SPREAD_LIMIT;
}