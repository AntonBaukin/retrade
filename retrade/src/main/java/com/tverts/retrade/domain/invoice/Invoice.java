package com.tverts.retrade.domain.invoice;

/* Java */

import java.util.Date;

/* com.tverts: endure (core, catalogues, ordering) */

import com.tverts.endure.EntityState;
import com.tverts.endure.StatefulEntity;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.cats.CodedEntity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;
import com.tverts.endure.order.OrderIndex;


/**
 * Invoice is a general entity to save data on
 * a goods operation, such as: move goods from one
 * store to another, sell, buy goods.
 *
 * The state of an Invoice is in separated
 * instance {@link InvoiceState}.
 *
 * Invoiced do contain some data related to their
 * concrete type. Not to create the subclasses,
 * that data is separated into hierarchy of
 * {@link InvoiceData}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      Invoice
       extends    Entity
       implements OrderIndex, DomainEntity,
                  StatefulEntity, CodedEntity
{
	/* public: StatefulEntity interface */

	public EntityState   getEntityState()
	{
		return getInvoiceState();
	}


	/* public: Invoice (bean) interface */

	public Domain        getDomain()
	{
		return domain;
	}

	public void          setDomain(Domain domain)
	{
		this.domain = domain;
	}

	/**
	 * Invoice type is the same reference as the Invoice'
	 * Unity type. This de-normalizing attribute is created
	 * for the unique code index.
	 */
	public UnityType     getInvoiceType()
	{
		return (invoiceType != null)?(invoiceType):
		  (getUnity() == null)?(null):(invoiceType = getUnity().getUnityType());
	}

	/**
	 * This set call is actually ignored.
	 */
	public void          setInvoiceType(UnityType invoiceType)
	{
		this.invoiceType = invoiceType;
	}

	public String        getCode()
	{
		return code;
	}

	public void          setCode(String code)
	{
		this.code = code;
	}

	public InvoiceState  getInvoiceState()
	{
		return invoiceState;
	}

	/**
	 * Initialises or changes the state if the Invoice.
	 *
	 * When the state is changed, the reference from the old
	 * state to this Invoice is cleared.
	 */
	public void          setInvoiceState(InvoiceState invoiceState)
	{
		if(this.invoiceState != null)
		{
			if(this.invoiceState.equals(invoiceState))
				return;

			//~: clear the reference to this state from the old state
			this.invoiceState.setInvoice(null);
		}

		this.invoiceState = invoiceState;

		//~: assign the reference to this state to the new state
		if(invoiceState != null)
			invoiceState.setInvoice(this);
	}

	public InvoiceData   getInvoiceData()
	{
		return invoiceData;
	}

	public void          setInvoiceData(InvoiceData invoiceData)
	{
		this.invoiceData = invoiceData;
	}

	public Date          getInvoiceDate()
	{
		return invoiceDate;
	}

	public void          setInvoiceDate(Date invoiceDate)
	{
		this.invoiceDate = invoiceDate;
	}

	public String        getRemarks()
	{
		return remarks;
	}

	public void          setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* public: OrderIndex interface */

	/**
	 * In this implementation Invoice' order owner is the Domain.
	 */
	public Unity         getOrderOwner()
	{
		return (getDomain() == null)?(null):
		  (getDomain().getUnity());
	}

	/**
	 * By default, Invoice' order type is it's
	 * {@link #getInvoiceType()}. Note that some
	 * types of invoices are still in the same order.
	 */
	public UnityType     getOrderType()
	{
		return (orderType != null)?(orderType):
		  (getInvoiceType());
	}

	public void          setOrderType(UnityType orderType)
	{
		this.orderType = orderType;
	}

	public Long          getOrderIndex()
	{
		return orderIndex;
	}

	public void          setOrderIndex(Long orderIndex)
	{
		this.orderIndex = orderIndex;
	}


	/* persisted attributes: unique triple (domain, type, code) */

	private Domain       domain;
	private String       code;


	/* persisted attributes: invoice state, etc. */

	private InvoiceState invoiceState;
	private UnityType    invoiceType;
	private InvoiceData  invoiceData;
	private Date         invoiceDate;
	private String       remarks;


	/* persisted attributes: order index */

	private UnityType    orderType;
	private Long         orderIndex;
}