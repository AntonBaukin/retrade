package com.tverts.endure.order;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import static com.tverts.endure.UnityTypes.unityType;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxPoint;


/**
 * Essentials of an {@link Orderer} implementation.
 *
 * Each real ordering strategy needs access to the database.
 * This abstraction is coupled with Hibernate.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class OrdererBase
       implements     Orderer, OrdererReference
{
	/* public: Orderer interface */


	public void          setOrderIndex(OrderRequest request)
	{
		if(isThatRequest(request))
		{
			orderInTx(request);
			request.setComplete();
		}
	}

	/* public: OrdererReference interface */

	public List<Orderer> dereferObjects()
	{
		return Collections.<Orderer> singletonList(this);
	}


	/* protected: ordering invocation */

	/**
	 * Tells whether this ordering strategy supports
	 * the request given.
	 */
	protected abstract boolean isThatRequest(OrderRequest request);

	/**
	 * Actual implementation of order procedure.
	 */
	protected abstract void    order(OrderRequest request);

	@Transactional
	protected void             orderInTx(OrderRequest request)
	{
		this.order(request);
	}


	/* protected: access transaction context */

	/**
	 * Returns the effective Tx context: whether that is
	 * provided with the request, or the global one.
	 *
	 * Raises {@link IllegalStateException} if no context bound.
	 */
	protected TxContext  getTxContext(OrderRequest request)
	{
		return (request.getTx() != null)?(request.getTx()):
		  (getGlobalTxContext());
	}

	protected TxContext  getGlobalTxContext()
	{
		return TxPoint.txContext();
	}

	/**
	 * Returns Hibernate Session bound to the request
	 * transaction context.
	 */
	protected Session    session(OrderRequest request)
	{
		SessionFactory f = getTxContext(request).getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "Ordering Strategy got Tx Context not bound to " +
		  "Hibernate session (or factroy)!");

		return s;
	}


	/* protected: helping methods */

	protected OrderIndex instance(OrderRequest request)
	{
		return request.getInstance();
	}

	protected OrderIndex reference(OrderRequest request)
	{
		return request.getReference();
	}

	protected Unity      orderOwner(OrderRequest request)
	{
		return request.getOrderOwner();
	}

	protected UnityType  orderType(OrderRequest request)
	{
		return request.getOrderType();
	}

	protected boolean    isType(OrderRequest request, UnityType type)
	{
		UnityType t = orderType(request);
		return (t != null) && t.equals(type);
	}

	protected boolean    isType
	  (OrderRequest request, Class typeClass, String typeName)
	{
		UnityType t0 = orderType(request);
		UnityType t1 = unityType(typeClass, typeName);

		return (t0 != null) && t0.equals(t1);
	}

	@SuppressWarnings("unchecked")
	protected boolean    isAnInstance(OrderRequest request, Class instanceClass)
	{
		return instanceClass.isAssignableFrom(
		  instance(request).getClass());
	}
}