package com.tverts.endure.order;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: system (tx) */

import com.tverts.system.tx.Tx;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Collects the parameters for {@link Orderer} strategy.
 *
 * @author anton.baukin@gmail.com
 */
public class OrderRequest
{
	/**
	 * Creates the order request for the instance defined.
	 * The order index of the instance would be updated.
	 *
	 * The reference of insert may be not defined, but if yes,
	 * it must has the same order owner and the same order type.
	 */
	public OrderRequest(OrderIndex instance, OrderIndex reference)
	{
		this.instance  = instance;
		this.reference = reference;

		//~: check the indices given
		checkIndices();

		//~: set the derived state
		assignState();
	}

	protected final OrderIndex instance;

	protected final OrderIndex reference;


	/* Order Request */

	/**
	 * The instance to set the order. Is always defined.
	 */
	public OrderIndex   getInstance()
	{
		return instance;
	}

	/**
	 * The reference to insert before or after. May be not
	 * defined. If defined, has the same owner and the same type.
	 */
	public OrderIndex   getReference()
	{
		return reference;
	}

	/**
	 * Tells whether to insert before {@code false} or after
	 * {@code true} the reference instance.
	 *
	 * If the reference is not defined, before {@code false}
	 * means to insert as the first item of the order, after
	 * {@code true} means to insert as the last one.
	 */
	public boolean      isBeforeAfter()
	{
		return beforeAfter;
	}

	private boolean beforeAfter;

	public OrderRequest setBeforeAfter(boolean beforeAfter)
	{
		this.beforeAfter = beforeAfter;
		return this;
	}

	/**
	 * Provides optional transaction context. If not specified,
	 * the global one would be used as default.
	 */
	public Tx           getTx()
	{
		return tx;
	}

	private Tx tx;

	/**
	 * TODO assign tx-context on each Order Request created.
	 */
	public OrderRequest setTx(Tx tx)
	{
		this.tx = tx;
		return this;
	}

	public Class<?>     getIndexClass()
	{
		return indexClass;
	}

	private Class<?> indexClass;

	public OrderRequest setIndexClass(Class<?> cls)
	{
		this.indexClass = cls;
		return this;
	}


	/* Order Request (state) */

	public boolean      isComplete()
	{
		return complete;
	}

	private boolean complete;

	public OrderRequest setComplete()
	{
		this.complete = true;
		return this;
	}

	public Unity        getOrderOwner()
	{
		return orderOwner;
	}

	private Unity orderOwner;

	public UnityType    getOrderType()
	{
		return orderType;
	}

	private UnityType  orderType;


	/* public: Object interface */

	public String       toString()
	{
		return SU.cats("Order Index Request for [",
		  LU.sig(getInstance()), "] reference [",
		  LU.sig(getReference()), "] insert ",
		  (isBeforeAfter())?("after"):("before")
		);
	}


	/* protected: order request internals */

	protected void checkIndices()
	{
		//?: {instance is not defined}
		EX.assertn(getInstance(), "Order Request ",
		  "may not be created on undefined instance!");

		//?: {owner is not defined}
		EX.assertn(getInstance().getOrderOwner(),
		  "Order Request may not be created on instance ",
		  LU.sig(getInstance()), " with undefined order owner!"
		);

		//?: {there is no reference} skip
		if(getReference() == null)
			return;

		//?: {reference order owner is not the same}
		EX.assertx(CMP.eq(getInstance().getOrderOwner(),
		  getReference().getOrderOwner()),
		  "Order Request may not be created on instance ",
		  LU.sig(getInstance()), " and reference ",
		  LU.sig(getReference()), " that are not ",
		  "of the same order owner!"
		);

		//~: check the order type equals
		EX.assertx(CMP.eq(getInstance().getOrderType(),
		  getReference().getOrderType()),
		  "Order Request may not be created on instance ",
		  LU.sig(getInstance()), " and reference ",
		  LU.sig(getReference()), " that are not ",
		  "of the same order type!"
		);
	}

	protected void assignState()
	{
		this.orderOwner = getInstance().getOrderOwner();
		this.orderType  = getInstance().getOrderType();
	}
}