package com.tverts.endure.order;

/* Java */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import org.hibernate.type.LongType;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Fully implements strategy of defining sparse indices
 * of an order. Note that this strategy itself does not
 * check the type of the income request and always says
 * {@link #isThatRequest(OrderRequest)} {@code true}.
 *
 * Has two parameters: insert step, and spread limit.
 *
 * When new item is inserted as the first or the last one
 * it's index is distanced from the closest item by the
 * insert step.
 *
 * When inserting item in the middle, and there is no free
 * space between the left and the right borders of insert,
 * that borders are moved aside. The number of items to move
 * in one side (left or right) is defined by spread limit.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class OrdererDefault extends OrdererBase
{
	/* Orderer Default (strategy configuration) */

	public String getOrderOwnerIdProp()
	{
		return orderOwnerIdProp;
	}

	private String orderOwnerIdProp = "orderOwner.id";

	public void setOrderOwnerIdProp(String p)
	{
		this.orderOwnerIdProp = EX.asserts(p);
	}

	public String getOrderTypeProp()
	{
		return orderTypeProp;
	}

	private String orderTypeProp = "orderType";

	public void   setOrderTypeProp(String p)
	{
		this.orderTypeProp = EX.asserts(p);
	}

	public String getOrderIndexProp()
	{
		return orderIndexProp;
	}

	private String orderIndexProp = "orderIndex";

	public void   setOrderIndexProp(String p)
	{
		this.orderIndexProp = EX.asserts(p);
	}

	public int    getInsertStep()
	{
		return insertStep;
	}

	private int insertStep = 256;

	public void   setInsertStep(int step)
	{
		EX.assertx(step >= 1);
		this.insertStep = step;
	}

	public int    getSpreadLimit()
	{
		return spreadLimit;
	}

	private int spreadLimit = 8; //<-- 16 + 1 for both sides

	public void   setSpreadLimit(int limit)
	{
		EX.assertx(limit >= 1);
		this.spreadLimit = limit;
	}


	/* protected: orderer base */

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
		OrderData od = createOrderData(request);

		//!: flush the objects to the database
		HiberPoint.flush(session(od));

		//~: find the insert borders
		findInsertBorders(od);

		//?: {insert as the first}
		if(od.getLeft() == null)
		{
			insertFirst(od);
			return;
		}

		//?: {insert as the last}
		if(od.getRight() == null)
		{
			insertLast(od);
			return;
		}

		//!: insert in the middle
		insertMiddle(od);
	}


	/* Strategy Request Data */

	public static class OrderData
	{
		public OrderData(OrderRequest request)
		{
			this.request = EX.assertn(request);
		}

		public final OrderRequest request;


		/* Order Data */

		public OrderRequest getRequest()
		{
			return request;
		}

		public Class<?>     getIndexClass()
		{
			return (indexClass != null)?(indexClass):
			  (request.getIndexClass() != null)?(request.getIndexClass()):
			  (indexClass = HiberPoint.type(getRequest().getInstance()));
		}

		private Class<?> indexClass;

		public OrderData    setIndexClass(Class<?> cls)
		{
			this.indexClass = cls;
			return this;
		}


		/* public: order borders */

		/**
		 * Returns the instance with closest smaller
		 * index. May be undefined: the instance
		 * to insert would be the first.
		 */
		public OrderIndex   getLeft()
		{
			return left;
		}

		private OrderIndex   left;

		public OrderData    setLeft(OrderIndex left)
		{
			this.left = left;
			return this;
		}

		/**
		 * Returns the instance with closest bigger i
		 * ndex. May be undefined: the instance
		 * to insert would be the last.
		 */
		public OrderIndex   getRight()
		{
			return right;
		}

		private OrderIndex right;

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

		private Long[] leftSpread;

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

		private Long[] rightSpread;

		public OrderData    setRightSpread(Long[] spread)
		{
			this.rightSpread = spread;
			return this;
		}

		public OrderIndex   getReference()
		{
			return (reference != null)?(reference):(request.getReference());
		}

		private OrderIndex reference;

		public void         setReference(OrderIndex reference)
		{
			this.reference = reference;
		}
	}


	/* protected: strategy variants */

	protected OrderData createOrderData(OrderRequest request)
	{
		return new OrderData(request);
	}

	protected void      insertSingle(OrderData od)
	{
		//~: just set 0 index
		instance(od).setOrderIndex(0L);
	}

	protected void      insertFirst(OrderData od)
	{
		//?: {there is no right (lowest) item} insert single
		if(od.getRight() == null)
		{
			insertSingle(od);
			return;
		}

		//~: just decrement by the insert step
		instance(od).setOrderIndex(
		  od.getRight().getOrderIndex() - insertStep);
	}

	protected void      insertLast(OrderData od)
	{
		//?: {there is no left (biggest) item} insert single
		if(od.getLeft() == null)
		{
			insertSingle(od);
			return;
		}

		//~: just increment by the insert step
		instance(od).setOrderIndex(
		  od.getLeft().getOrderIndex() + insertStep);
	}

	/**
	 * Inserts the index between defined the left and the right
	 * index borders.
	 */
	protected void      insertMiddle(OrderData od)
	{
		long l  = od.getLeft().getOrderIndex();
		long r = od.getRight().getOrderIndex();

		//?: {there is enough space to insert}
		if(l + 1 < r)
			insertMiddleNormal(od);
		//!: there is no space left -> do spread
		else
			insertMiddleSpread(od);
	}

	/**
	 * Selects the index between the left and the right
	 * borders when there is enough space there.
	 */
	protected void      insertMiddleNormal(OrderData od)
	{
		long oileft  = od.getLeft().getOrderIndex();
		long oiright = od.getRight().getOrderIndex();

		//~: just set the index in the middle
		instance(od).setOrderIndex((oileft + oiright)/2);
	}

	protected void      insertMiddleSpread(OrderData od)
	{
		//!: do spread order indices
		spreadOrderIndices(od);

		//~: insert in the middle as normal
		insertMiddleNormal(od);
	}


	/* protected: insert borders selection */

	protected void      findInsertBorders(OrderData od)
	{
		//?: {reference is undefined} insert as the first or the last
		if(reference(od) == null)
		{
			//?: {beforeAfter = true} as the last
			if(request(od).isBeforeAfter())
				findInsertBorderRight(od);
			//!: {beforeAfter = false} as the first
			else
				findInsertBorderLeft(od);

			return;
		}

		EX.assertn(reference(od).getOrderIndex(),
		  "Order reference ", od.getIndexClass().getSimpleName(),
		  " [", reference(od).getPrimaryKey(), "] has order index undefined!"
		);

		//!: ensure the order index of the reference
		ensureReferenceOrderIndex(od);

		//?: {before the reference}
		if(request(od).isBeforeAfter())
			findInsertBorderAfter(od);
		else
			findInsertBorderBefore(od);
	}

	@SuppressWarnings("unchecked")
	protected void      findInsertBorderLeft(OrderData od)
	{

/*

 select o, o.$orderIndex from OrderIndex o where
   (o.$orderOwner = :orderOwner) and (o.id <> :target)
   $and$orderType=:orderType$ and (o.$orderIndex is not null)
 order by o.$orderIndex asc

 */
		List<Object[]> r = (List<Object[]>) indexQuery(od,

"select o, o.$orderIndex from OrderIndex o where\n" +
"  (o.$orderOwner = :orderOwner) and (o.id <> :target)\n" +
"  $and$orderType=:orderType$ and (o.$orderIndex is not null)\n" +
"order by o.$orderIndex asc"

		).
		  setParameter("target", od.request.getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has right (smallest) border}
		if(!r.isEmpty())
			od.setRight(ensureOrderIndex(od, r.get(0)));
	}

	@SuppressWarnings("unchecked")
	protected void      findInsertBorderRight(OrderData od)
	{

/*

 select o, o.$orderIndex from OrderIndex o where
   (o.$orderOwner = :orderOwner) and (o.id <> :target)
   $and$orderType=:orderType$ and (o.$orderIndex is not null)
 order by o.$orderIndex desc

 */
		List<Object[]> r = (List<Object[]>) indexQuery(od,

"select o, o.$orderIndex from OrderIndex o where\n" +
"  (o.$orderOwner = :orderOwner) and (o.id <> :target)\n" +
"  $and$orderType=:orderType$ and (o.$orderIndex is not null)\n" +
"order by o.$orderIndex desc"

		).
		  setParameter("target", od.request.getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has left (biggest) border}
		if(!r.isEmpty())
			od.setLeft(ensureOrderIndex(od, r.get(0)));
	}

	@SuppressWarnings("unchecked")
	protected void      findInsertBorderBefore(OrderData od)
	{

/*

 select o, o.$orderIndex from OrderIndex o where
   (o.$orderOwner = :orderOwner) and (o.id <> :target)
   $and$orderType=:orderType$ and (o.$orderIndex < :orderIndex)
 order by o.$orderIndex desc

 */

		List<Object[]> r = (List<Object[]>) indexQuery(od,

"select o, o.$orderIndex from OrderIndex o where\n" +
"  (o.$orderOwner = :orderOwner) and (o.id <> :target)\n" +
"  $and$orderType=:orderType$ and (o.$orderIndex < :orderIndex)\n" +
"order by o.$orderIndex desc"

		).
		  setParameter("orderIndex", reference(od).getOrderIndex()).
		  setParameter("target", od.request.getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has left border}
		if(!r.isEmpty())
			od.setLeft(ensureOrderIndex(od, r.get(0)));

		//~: right border is the reference
		od.setRight(reference(od));
	}

	@SuppressWarnings("unchecked")
	protected void      findInsertBorderAfter(OrderData od)
	{

/*

 select o, o.$orderIndex from OrderIndex o where
   (o.$orderOwner = :orderOwner) and (o.id <> :target)
   $and$orderType=:orderType$ and (o.$orderIndex > :orderIndex)
 order by o.$orderIndex asc

 */

		List<Object[]> r = (List<Object[]>) indexQuery(od,

"select o, o.$orderIndex from OrderIndex o where\n" +
"  (o.$orderOwner = :orderOwner) and (o.id <> :target)\n" +
"  $and$orderType=:orderType$ and (o.$orderIndex > :orderIndex)\n" +
"order by o.$orderIndex asc"

		).
		  setParameter("orderIndex", reference(od).getOrderIndex()).
		  setParameter("target", od.request.getInstance().getPrimaryKey()).
		  setMaxResults(1).
		  list();

		//?: {has right border}
		if(!r.isEmpty())
			od.setRight(ensureOrderIndex(od, r.get(0)));

		//~: left border is the reference
		od.setLeft(reference(od));
	}


	/* protected: spread order indices */

	protected void      spreadOrderIndices(OrderData od)
	{
		//~: load indices before and after the border
		loadSpreadLines(od);

		//?: {there was no space in the spread loaded} do total move
		if(!spreadReservePlace(od))
			spreadMoveOrder(od);
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
	protected void      loadSpreadLines(OrderData od)
	{

/*

 select $orderIndex from OrderIndex where
   ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex < :orderIndex) order by $orderIndex desc

 */

		Query      q = indexQuery(od,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex < :orderIndex) order by $orderIndex desc"

		).
		  setParameter("orderIndex", od.getLeft().getOrderIndex()).
		  setMaxResults(getSpreadLimit());

		List<Long> r = (List<Long>)q.list();
		Long[]     a = new Long[r.size() + 1];

		a[0] = od.getLeft().getOrderIndex();
		for(int i = 0;(i < r.size());i++)
			a[i + 1] = r.get(i);

		od.setLeftSpread(a);


/*

 select $orderIndex from OrderIndex where
   ($orderOwner = :orderOwner) $and$orderType=:orderType$ and
   ($orderIndex > :orderIndex) order by $orderIndex asc

 */
		q = indexQuery(od,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex > :orderIndex) order by $orderIndex asc"

		).
		  setParameter("orderIndex", od.getRight().getOrderIndex()).
		  setMaxResults(getSpreadLimit());

		r = (List<Long>)q.list();
		a = new Long[r.size() + 1];

		a[0] = od.getRight().getOrderIndex();
		for(int i = 0;(i < r.size());i++)
			a[i + 1] = r.get(i);

		od.setRightSpread(a);
	}

	/**
	 * Tries to find free space within the loaded spread lines
	 * on the right or on the left. Returns {@code true} if the
	 * place was found.
	 *
	 * If there was no free place on both the sides a total
	 * move is performed on all the index order.
	 */
	protected boolean   spreadReservePlace(OrderData od)
	{
		//HINT: we reserve in each spread half not to allow
		//  the distribution to slip into one direction.

		boolean r = spreadReservePlaceRight(od);
		boolean l = spreadReservePlaceLeft(od);

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

	protected boolean   spreadReservePlaceRight(OrderData od)
	{
		Long[] spread = od.getRightSpread();

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

		Query q = indexQuery(od, spreadReservePlaceRightQuery()).
		  setParameter("smove", smove).
		  setParameter("startIndex", od.getRight().getOrderIndex()).
		  setParameter("endIndex", spread[spos]);

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		od.getRight().setOrderIndex(
		  od.getRight().getOrderIndex() + smove
		);

		//~: update the selected spread line in the range
		for(int i = 0;(i < spos);i++)
			spread[i] += smove;

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(od,
		  od.getRight().getOrderIndex(), spread[spos] - 1);

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

	protected boolean   spreadReservePlaceLeft(OrderData od)
	{
		Long[] spread = od.getLeftSpread();

		//~: select space position
		long   smove  = 0L;
		int    spos;

		for(spos = 1;(spos < spread.length);spos++)
			//?: {is there at least one free slot available} found it!

			//HINT: the order is descending
			if((smove = spread[spos - 1] - spread[spos]) > 1L)
				break;

		//?: {not found free slot} nothing to do here
		if(smove <= 1L) return false;

		//~: select move position as the middle of available space
		smove = (smove - 1)/2; if(smove == 0L) smove = 1L;

		//~: issue UPDATE moving left all the items in the
		//   range of (spread[spos], left]

		Query q = indexQuery(od, spreadReservePlaceLeftQuery()).
		  setParameter("smove", smove).
		  setParameter("startIndex", spread[spos]).
		  setParameter("endIndex", od.getLeft().getOrderIndex());

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		od.getLeft().setOrderIndex(
		  od.getLeft().getOrderIndex() - smove
		);

		//~: update the selected spread line in the range
		for(int i = 0;(i < spread.length);i++)
			spread[i] -= smove;

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(od,
		  spread[spos] + 1, od.getLeft().getOrderIndex());

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
	protected void      spreadMoveOrder(OrderData od)
	{
		//~: move the right half
		spreadMoveOrderRight(od);
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
"where ($orderOwner = :orderOwner) $and$orderType=:orderType$ and\n" +
"  ($orderIndex >= :orderIndex)";

	}

	protected void      spreadMoveOrderRight(OrderData od)
	{
		Query q = indexQuery(od, spreadMoveOrderRightQuery()).
		  setParameter("insertStep", insertStep).
		  setParameter("orderIndex", od.getRight().getOrderIndex());

		//!: execute update
		q.executeUpdate();

		//~: update the selected spread line
		Long[] spread = od.getRightSpread();
		for(int i = 0;(i < spread.length);i++)
			spread[i] += insertStep;

		//~: reload the indices of the updated rows
		reloadUpdatedOrderIndices(od,
		  od.getRight().getOrderIndex(), null);
	}

	@SuppressWarnings("unchecked")
	protected void      reloadUpdatedOrderIndices
	  (OrderData od, Long left, Long right)
	{
		//~: get the instances in the persistence context
		Set entities = HiberSystem.getInstance().
		  findAttachedEntities(session(od), od.getIndexClass());

		//~: remove our goal instance (it is set later)
		entities.remove(instance(od));

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
		  new HashMap<>(entities.size());

		for(Object e : entities)
			updates.put(((OrderIndex)e).getPrimaryKey(), (OrderIndex)e);

/*

 select oi.id, oi.$orderIndex from OrderIndex oi
   where oi.id in (:orderIndices)

 */

		Query           query = indexQuery(od,

"select oi.id, oi.$orderIndex from OrderIndex oi\n" +
"  where oi.id in (:orderIndices)"

		);

		ArrayList<Long> qkeys = new ArrayList<>(10);
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

	protected Query        indexQuery(OrderData od, String hql)
	{
		String  Q = hql;
		//?: query has OrderIndex alias 'o'
		boolean A = hql.contains("o.$orderIndex");

		Q = SU.replace(Q, "$orderOwner", getOrderOwnerIdProp());
		Q = SU.replace(Q, "$orderIndex", getOrderIndexProp());

		//?: {has order type defined}
		if(orderType(od) == null)
			Q = SU.replace(Q, _AND_ORDER_TYPE_EQ_, "");
		else
			Q = SU.replace(Q, _AND_ORDER_TYPE_EQ_, String.format(
			  "and (%s%s = :orderType)", A?("o."):(""), getOrderTypeProp()));

		Query q = HiberPoint.query(session(request(od)), Q,
		  "OrderIndex", od.getIndexClass());

		//~: set order owner
		if(Q.contains(":orderOwner"))
			q.setParameter("orderOwner", orderOwnerID(od));

		//~: set order type
		if(Q.contains(":orderType"))
			q.setParameter("orderType", orderType(od));

		return q;
	}


	/* protected: support functions */

	protected OrderRequest request(OrderData od)
	{
		return od.getRequest();
	}

	protected OrderIndex   instance(OrderData od)
	{
		return od.request.getInstance();
	}

	protected OrderIndex   reference(OrderData od)
	{
		return od.getReference();
	}

	protected Session      session(OrderData od)
	{
		return session(od.getRequest());
	}

	protected Long         orderOwnerID(OrderData od)
	{
		return request(od).getOrderOwner().getPrimaryKey();
	}

	protected UnityType    orderType(OrderData od)
	{
		return request(od).getOrderType();
	}

	protected void         ensureReferenceOrderIndex(OrderData od)
	{

// select o.$orderIndex from OrderIndex o where (o.id = :pk)

		Long oi = (Long) indexQuery(od,

"select o.$orderIndex from OrderIndex o where (o.id = :pk)"

		).
		  setParameter("pk", reference(od).getPrimaryKey()).
		  uniqueResult();

		EX.assertn(oi);

		od.setReference(ensureOrderIndex(od,
		  new Object[] { reference(od), oi }
		));
	}

	@SuppressWarnings("unchecked")
	protected OrderIndex   ensureOrderIndex(OrderData od, Object[] row)
	{
		OrderIndex o = null;
		Long       i = null;

		for(Object x : row)
		{
			if(x instanceof OrderIndex)
				o = (OrderIndex) x;
			if(x instanceof Long)
				i = (Long) x;
		}

		if((o == null) || (i == null)) throw EX.state();

		//?: {cached instance has the same index} no problems
		if(i.equals(o.getOrderIndex()))
			return o;

		//~: evict those instance
		HiberPoint.flush(session(od));
		session(od).evict(o);

		//!: reload it
		return (OrderIndex) session(od).load(
		  od.getIndexClass(), o.getPrimaryKey());
	}
}