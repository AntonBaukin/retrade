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

/* com.tverts: support  */

import static com.tverts.support.SU.s2s;


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

	protected Long       orderOwnerID(OrderRequest request)
	{
		return request.getOrderOwner().getPrimaryKey();
	}

	protected UnityType  orderType(OrderRequest request)
	{
		return request.getOrderType();
	}

	protected UnityType  ownerType(OrderRequest request)
	{
		return orderOwner(request).getUnityType();
	}

	protected boolean    isOrderType(OrderRequest request, UnityType type)
	{
		return (type != null) && type.equals(orderType(request));
	}

	protected boolean    isOrderType
	  (OrderRequest request, Class typeClass, String typeName)
	{
		if(typeClass == null) return false;

		if((typeName = s2s(typeName)) == null)
			return typeClass.equals(orderType(request).getTypeClass());

		UnityType t = unityType(typeClass, typeName);
		return (t != null) && t.equals(orderType(request));
	}

	protected boolean    isOwnerType(OrderRequest request, UnityType type)
	{
		UnityType t = ownerType(request);
		return (t != null) && t.equals(type);
	}

	protected boolean    isOwnerType
	  (OrderRequest request, Class typeClass, String typeName)
	{
		if(typeClass == null) return false;

		if((typeName = s2s(typeName)) == null)
			return typeClass.equals(ownerType(request).getTypeClass());

		UnityType t = unityType(typeClass, typeName);
		return (t != null) && t.equals(ownerType(request));
	}
}