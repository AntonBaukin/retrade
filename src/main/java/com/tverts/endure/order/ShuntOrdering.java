package com.tverts.endure.order;

/* standard Java classes */

import java.util.List;

/* JUnit */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.session;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GenTestDomain;

import static com.tverts.endure.UnityTypes.unityType;


/**
 * Creates and saves {@link ExternalOrder} instances
 * with Test Domain owner and 'Test: Ordering' order type.
 *
 * {@link TestOrderer} strategy is used to actually
 * order the instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"domain:core", "domain:core:order-index"})
@SelfShuntDescr("Test Order Index implementation.")
public class ShuntOrdering extends ShuntPlain
{
	/* shunting constants  */

	public static final String INDEX_TYPE =
	  "Test: Domain: Order Index";


	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testBegin()
	{
		//~: access the test domain
		Domain testDomain = GenTestDomain.testDomain();
		assertNotNull("Test Domain", testDomain);
		orderOwner = testDomain.getUnity();

		//~: get index type
		orderType = unityType(ExternalOrder.class, INDEX_TYPE);

		assertNotNull(String.format(
		  "Test Order Type '%s' must be defined!", INDEX_TYPE),
		              orderOwner);

		//~: delete all the test items present
/*

delete from ExternalOrder where
  (orderOwner = :orderOwner) and (orderType = :orderType)

*/
		Q(

"delete from ExternalOrder where\n" +
"  (orderOwner = :orderOwner) and (orderType = :orderType)"

		).
		  setParameter("orderOwner", orderOwner).
		  setParameter("orderType",  orderType).
		  executeUpdate();
	}


	@SelfShuntMethod(order = 1, critical = true)
	public void testInitial()
	{
		ExternalOrder o = newOrder();

		OrderPoint.orderBefore(o, null);
		assertEquals(0L, o.getOrderIndex());

		session().save(o);
		session().flush();
	}


	@SelfShuntMethod(order = 2, critical = true)
	public void testFirst()
	{
		ExternalOrder o = newOrder();

		OrderPoint.orderBefore(o, select(0L));
		assertEquals(-2L, o.getOrderIndex());

		session().save(o);
		session().flush();
	}


	@SelfShuntMethod(order = 3, critical = true)
	public void testLast()
	{
		ExternalOrder o = newOrder();

		OrderPoint.orderAfter(o, select(0L));
		assertEquals(2L, o.getOrderIndex());

		session().save(o);
		session().flush();
	}


	@SelfShuntMethod(order = 4, critical = true)
	public void testMiddleRight()
	{
		ExternalOrder o = newOrder();

		OrderPoint.orderBefore(o, select(0L));
		assertEquals(-1L, o.getOrderIndex());

		session().save(o);
		session().flush();
	}


	@SelfShuntMethod(order = 5, critical = true)
	public void testMiddleLeft()
	{
		ExternalOrder o = newOrder();

		OrderPoint.orderAfter(o, select(0L));
		assertEquals(1L, o.getOrderIndex());

		session().save(o);
		session().flush();
	}


	@SelfShuntMethod(order = 6, critical = true)
	public void testSpreadSimple()
	{
		ExternalOrder o;

		o = newOrder();
		OrderPoint.orderBefore(o, select(-2L));
		assertEquals(-4L, o.getOrderIndex());

		session().save(o);
		session().flush();

		o = newOrder();
		OrderPoint.orderAfter(o, select(2L));
		assertEquals(4L, o.getOrderIndex());

		session().save(o);
		session().flush();

		//           --  --  --  -  ->  ->  --
		//have:      -4  -2  -1  0  +1  +2  +4
		//                        xx

		//will:      -4  -2  -1  0  +1  +2  +3  +4
		//                          xx

		assertOrder(-4,  -2,  -1,  0,  +1,  +2,  +4);

		o = newOrder();
		OrderPoint.orderAfter(o, select(0L));
		assertEquals(1L, o.getOrderIndex());

		session().save(o);
		session().flush();

		assertOrder(-4,  -2,  -1,  0,  +1,  +2,  +3,  +4);
	}


	@SelfShuntMethod(order = 7, critical = true)
	public void testSpreadHarder()
	{
		ExternalOrder o;

		//have:   -4  -2  -1  0  +1  +2  +3  +4
		//                     xx

		//will:   -4  -2  -1  0  +1  +3  +4  +5  +6
		//                       xx

		assertOrder(-4,  -2,  -1,  0,  +1,  +2,  +3,  +4);

		o = newOrder();
		OrderPoint.orderAfter(o, select(0L));
		assertEquals(1L, o.getOrderIndex());

		session().save(o);
		session().flush();

		assertOrder(-4,  -2,  -1,  0,  +1,  +3,  +4,  +5,  +6);


		//               ->  >  ->
		//have:  -4  -2  -1  0  +1  +3  +4  +5  +6
		//             xx

		//will:  -4  -2  -1  0  +1  +2  +3  +4  +5  +6
		//               xx

		o = newOrder();
		OrderPoint.orderBefore(o, select(-1));
		assertEquals(-1L, o.getOrderIndex());

		session().save(o);
		session().flush();

		assertOrder(-4,  -2,  -1,  0,  +1,  +2,  +3,  +4,  +5,  +6);
	}


	/* protected: prepared data */

	protected Unity     orderOwner; //<-- test domain
	protected UnityType orderType;


	/* protected: shunt support */

	protected Query          Q(String hql)
	{
		return session().createQuery(hql);
	}

	protected ExternalOrder  newOrder()
	{
		ExternalOrder o = new ExternalOrder();

		setPrimaryKey(session(), o, true);
		o.setOrderOwner(orderOwner);
		o.setOrderType(orderType);
		o.setOrderInstance(0L);
		o.setOrderIndex(Long.MAX_VALUE);

		return o;
	}

	protected ExternalOrder  select(long orderIndex)
	{

/*

from ExternalOrder where (orderOwner = :orderOwner) and
  (orderType = :orderType) and (orderIndex = :orderIndex)

*/

		List list = Q(

"from ExternalOrder where (orderOwner = :orderOwner) and\n" +
"  (orderType = :orderType) and (orderIndex = :orderIndex)"

		).
		  setParameter("orderOwner", orderOwner).
		  setParameter("orderType",  orderType).
		  setLong("orderIndex", orderIndex).
		  list();

		if(list.isEmpty()) fail(String.format(
		  "No External Order instance found with index %d", orderIndex));

		if(list.size() != 1) fail(String.format(
		  "More than one External Order instance found with index %d", orderIndex));

		return (ExternalOrder)list.get(0);
	}

	protected void           assertOrder(long... order)
	{
		assertArrayEquals("Wrong Order index in the table!", order, load());
	}

	protected long[]         load()
	{
/*

select orderIndex from ExternalOrder where
  (orderOwner = :orderOwner) and (orderType = :orderType)

*/

		List list  = Q(

"select orderIndex from ExternalOrder where\n" +
"  (orderOwner = :orderOwner) and (orderType = :orderType)"

		).
		  setParameter("orderOwner", orderOwner).
		  setParameter("orderType",  orderType).
		  list();

		long[] res = new long[list.size()];

		for(int i = 0;(i < res.length);i++)
			res[i] = (Long)list.get(i);

		return res;
	}
}