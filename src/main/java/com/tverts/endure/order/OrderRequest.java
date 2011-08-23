package com.tverts.endure.order;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxContext;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Collects the parameters for {@link Orderer} strategy.
 *
 * @author anton.baukin@gmail.com
 */
public class OrderRequest
{
	/* public: constructor */

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


	/* public: OrderRequest interface */

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

	public OrderRequest setBeforeAfter(boolean beforeAfter)
	{
		this.beforeAfter = beforeAfter;
		return this;
	}

	/**
	 * Provides optional transaction context. If not specified,
	 * the global one would be used as default.
	 */
	public TxContext    getTx()
	{
		return tx;
	}

	public OrderRequest setTx(TxContext tx)
	{
		this.tx = tx;
		return this;
	}

	public Map          getParams()
	{
		return (params != null)?(params):
		  (params = new HashMap(3));
	}

	public OrderRequest setParams(Map params)
	{
		this.params = params;
		return this;
	}


	/* public: OrderRequest (state) interface */

	public boolean      isComplete()
	{
		return complete;
	}

	public OrderRequest setComplete()
	{
		this.complete = true;
		return this;
	}

	public Unity        getOrderOwner()
	{
		return orderOwner;
	}

	public UnityType    getOrderType()
	{
		return orderType;
	}


	/* public: Object interface */

	public String       toString()
	{
		return String.format(
		  "Order Index request for instance [%s] %s reference [%s]",

		  OU.sig(getInstance()), OU.sig(getReference()),
		  (isBeforeAfter())?("after"):("before")
		);
	}


	/* protected: order request internals */

	protected void checkIndices()
	{
		//?: {instance is not defined}
		if(getInstance() == null) throw new IllegalArgumentException(
		  "Order Request may not be created on undefined instance!"
		);

		//~: check the order owner
		Unity owner = getInstance().getOrderOwner();

		if(owner == null) throw new IllegalArgumentException(
		  "Order Request may not be created on instance having " +
		  "undefined order owner!"
		);

		//?: {there is no reference} skip other checks
		if(getReference() == null) return;

		//?: {reference order owner is not the same}
		if(!owner.equals(getReference().getOrderOwner()))
			throw new IllegalArgumentException(
			  "Order Request may not be created on instance and " +
			  "reference are not with the same order owner!"
			);

		//~: check the order type equals
		UnityType itype = getInstance().getOrderType();
		UnityType rtype = getReference().getOrderType();

		//?: {reference order type is not the same}
		if(((itype == null) && (rtype != null)) ||
		   ((itype != null) && !itype.equals(rtype))
		  )
			throw new IllegalArgumentException(
			  "Order Request may not be created on instance and " +
			  "reference are not with the same order type!"
			);
	}

	protected void assignState()
	{
		this.orderOwner = getInstance().getOrderOwner();
		this.orderType  = getInstance().getOrderType();
	}


	/* private: order request parameters */

	private OrderIndex instance;
	private OrderIndex reference;
	private TxContext  tx;
	private Map        params;
	private boolean    beforeAfter;


	/* private: order request state */

	private Unity      orderOwner;
	private UnityType  orderType;
	private boolean    complete;
}