package com.tverts.endure.order;

/* Java */

import java.util.Collections;
import java.util.List;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;
import com.tverts.system.tx.TxPoint;

/* com.tverts: support  */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Essentials of an {@link Orderer} implementation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class OrdererBase
       implements     Orderer, OrdererReference
{
	/* Orderer */

	public void setOrderIndex(OrderRequest request)
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

	protected void             orderInTx(OrderRequest request)
	{
		//!: require transactional context
		TxPoint.txContext();

		this.order(request);
	}


	/* protected: access transaction context */

	/**
	 * Returns the effective Tx context: whether that is
	 * provided with the request, or the global one.
	 */
	protected Tx      txContext(OrderRequest r)
	{
		return EX.assertn((r.getTx() != null)?
		  r.getTx():TxPoint.txContext(),
		  LU.cls(this), " has no tx-context!"
		);
	}

	/**
	 * Returns Hibernate Session bound
	 * to the request tx-context.
	 */
	protected Session session(OrderRequest r)
	{
		return EX.assertn(
		  txContext(r).getSessionFactory().getCurrentSession(),
		  LU.cls(this), " has no session in the tx-context!"
		);
	}


	/* protected: helping methods */
}