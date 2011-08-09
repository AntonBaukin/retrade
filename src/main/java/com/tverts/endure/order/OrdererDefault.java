package com.tverts.endure.order;

/* standard Java classes */

import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: endure */

import com.tverts.endure.UnityType;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


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
 * WARNING. This strategy works on the level of the
 * database issuing update HQL requests. If there
 * order items are in the Hibernate Session' cache,
 * they become invalid. Cleanup the cache before and
 * after the order updates!
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

	public static final long   DEF_INSERT_STEP        = 16384;

	public static final int    DEF_SPREAD_LIMIT       = 16;


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
		if((p = s2s(p)) == null) throw new IllegalArgumentException();
		this.orderOwnerIDProp = p;
	}

	public String getOrderTypeProp()
	{
		return orderTypeProp;
	}

	public void   setOrderTypeProp(String p)
	{
		if((p = s2s(p)) == null) throw new IllegalArgumentException();
		this.orderTypeProp = p;
	}

	public String getOrderIndexProp()
	{
		return orderIndexProp;
	}

	public void   setOrderIndexProp(String p)
	{
		if((p = s2s(p)) == null) throw new IllegalArgumentException();
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
			return (indexClass != null)?(indexClass):
			  (indexClass = instance(getRequest()).getClass());
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

from OrderIndex where ($orderOwner = :orderOwner) and
  ($orderType X :orderType) and ($orderIndex is not null)
  order by $orderIndex asc

*/
		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner) and\n" +
"  ($orderType X :orderType) and ($orderIndex is not null)\n" +
"  order by $orderIndex asc"

		).
		  setMaxResults(1).
		  list();

		//?: {has right (smallest) border}
		if(!r.isEmpty())
			odata.setRight((OrderIndex)r.get(0));
	}

	protected void      findInsertBorderRight(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner) and
  ($orderType X :orderType) and ($orderIndex is not null)
  order by $orderIndex desc

*/
		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner) and\n" +
"  ($orderType X :orderType) and ($orderIndex is not null)\n" +
"  order by $orderIndex desc"

		).
		  setMaxResults(1).
		  list();

		//?: {has left (biggest) border}
		if(!r.isEmpty())
			odata.setLeft((OrderIndex)r.get(0));
	}

	protected void      findInsertBorderBefore(OrderData odata)
	{

/*

from OrderIndex where ($orderOwner = :orderOwner) and
  ($orderType X :orderType) and ($orderIndex < :orderIndex)
  order by $orderIndex desc

*/

		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner) and\n" +
"  ($orderType X :orderType) and ($orderIndex < :orderIndex)\n" +
"  order by $orderIndex desc"

		).
		  setLong("orderIndex", reference(odata).getOrderIndex()).
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

from OrderIndex where ($orderOwner = :orderOwner) and
  ($orderType X :orderType) and ($orderIndex > :orderIndex)
  order by $orderIndex asc

*/

		List r = indexQuery(odata,

"from OrderIndex where ($orderOwner = :orderOwner) and\n" +
"  ($orderType X :orderType) and ($orderIndex > :orderIndex)\n" +
"  order by $orderIndex asc"

		).
		  setLong("orderIndex", reference(odata).getOrderIndex()).
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
  ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex < :orderIndex) order by $orderIndex desc

*/

		Query      q = indexQuery(odata,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
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
  ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex > :orderIndex) order by $orderIndex asc

*/
		q = indexQuery(odata,

"select $orderIndex from OrderIndex where\n" +
"  ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
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

/*

update OrderIndex set $orderIndex = $orderIndex + :smove
where ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)

*/
		Query      q = indexQuery(odata,

"update OrderIndex set $orderIndex = $orderIndex + :smove\n" +
"where ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
"  ($orderIndex >= :startIndex) and ($orderIndex < :endIndex)"

		).
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

		return true;
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

/*

update OrderIndex set $orderIndex = $orderIndex - :smove
where ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)

*/
		Query      q = indexQuery(odata,

"update OrderIndex set $orderIndex = $orderIndex - :smove\n" +
"where ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
"  ($orderIndex > :startIndex) and ($orderIndex <= :endIndex)"

		).
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

		//~: move the left half
		//spreadMoveOrderLeft(odata);
	}

	protected void      spreadMoveOrderRight(OrderData odata)
	{

/*

update OrderIndex set $orderIndex = $orderIndex + :insertStep
where ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex >= :orderIndex)

*/

		Query      q = indexQuery(odata,

"update OrderIndex set $orderIndex = $orderIndex + :insertStep\n" +
"where ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
"  ($orderIndex >= :orderIndex)"

		).
		  setLong("insertStep", getInsertStep()).
		  setLong("orderIndex", odata.getRight().getOrderIndex());

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		odata.getRight().setOrderIndex(
		  odata.getRight().getOrderIndex() + getInsertStep()
		);

		Long[] spread = odata.getRightSpread();

		//~: update the selected spread line
		for(int i = 0;(i < spread.length);i++)
			spread[i] += getInsertStep();
	}

	protected void      spreadMoveOrderLeft(OrderData odata)
	{
/*

update OrderIndex set $orderIndex = $orderIndex - :insertStep
where ($orderOwner = :orderOwner) and ($orderType X :orderType) and
  ($orderIndex <= :orderIndex)

*/

		Query      q = indexQuery(odata,

"update OrderIndex set $orderIndex = $orderIndex - :insertStep\n" +
"where ($orderOwner = :orderOwner) and ($orderType X :orderType) and\n" +
"  ($orderIndex <= :orderIndex)"

		).
		  setLong("insertStep", getInsertStep()).
		  setLong("orderIndex", odata.getLeft().getOrderIndex());

		//!: execute update
		q.executeUpdate();


		//~: set the index of the right border
		odata.getLeft().setOrderIndex(
		  odata.getLeft().getOrderIndex() - getInsertStep()
		);

		Long[] spread = odata.getLeftSpread();

		//~: update the selected spread line
		for(int i = 0;(i < spread.length);i++)
			spread[i] -= getInsertStep();
	}


	/* protected: queries building */

	protected Query        indexQuery(OrderData odata, String hql)
	{
		String Q = hql.
		  replace("OrderIndex",  indexClass(odata)).
		  replace("$orderOwner", getOrderOwnerIDProp()).
		  replace("$orderType",  getOrderTypeProp()).
		  replace("$orderIndex", getOrderIndexProp());

		//?: {has order type defined}
		if(orderType(odata) != null)
			Q = Q.replace("X :orderType", "= :orderType");
		else
			Q = Q.replace("X :orderType", "is null");


		Query q =  session(request(odata)).createQuery(Q);

		//~: set order owner
		if(Q.indexOf(":orderOwner") != -1)
			q.setLong("orderOwner", orderOwnerID(odata));

		//~: set order type
		if(Q.indexOf(":orderType") != -1)
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

	protected Long         orderOwnerID(OrderData odata)
	{
		return orderOwnerID(request(odata));
	}

	protected UnityType    orderType(OrderData odata)
	{
		return orderType(request(odata));
	}

	protected String       indexClass(OrderData odata)
	{
		String name = odata.getIndexClass().getName();
		int    xind;

		//?: {Java Assist proxy}
		if((xind = name.indexOf("_$$_")) != -1)
			name = name.substring(0, xind);

		return name;
	}


	/* private: parameters of the strategy */

	private String orderOwnerIDProp = DEF_ORDER_OWNERID_PROP;
	private String orderTypeProp    = DEF_ORDER_TYPE_PROP;
	private String orderIndexProp   = DEF_ORDER_INDEX_PROP;

	private long insertStep         = DEF_INSERT_STEP;
	private int  spreadLimit        = DEF_SPREAD_LIMIT;
}