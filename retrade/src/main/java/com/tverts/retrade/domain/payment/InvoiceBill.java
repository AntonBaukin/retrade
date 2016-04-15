package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;

/* com.tverts: retrade domain (firms + invoices) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.invoice.Invoice;


/**
 * This object tells that the {@link Invoice} is
 * related to the {@link Contractor}, and the amount
 * of summary money is a debt, or an expense.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      InvoiceBill
       extends    NumericBase
       implements TxEntity
{
	/* public: InvoiceBill (bean) interface */

	public Contractor    getContractor()
	{
		return contractor;
	}

	public void          setContractor(Contractor contractor)
	{
		this.contractor = contractor;
	}

	public FirmOrder     getOrder()
	{
		return order;
	}

	public void          setOrder(FirmOrder order)
	{
		this.order = order;
	}

	public Invoice       getInvoice()
	{
		return invoice;
	}

	public void          setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}

	public BigDecimal    getExpense()
	{
		return expense;
	}

	public void          setExpense(BigDecimal e)
	{
		if((e != null) && (e.scale() != 10))
			e = e.setScale(10);

		this.expense = e;
	}

	public BigDecimal    getIncome()
	{
		return income;
	}

	public void          setIncome(BigDecimal i)
	{
		if((i != null) && (i.scale() != 10))
			i = i.setScale(10);

		this.income = i;
	}

	/**
	 * This flag is set when the Invoice of this Bill
	 * is in fixed state. It does not tell whether the
	 * Invoice is actually payed.
	 */
	public boolean       isEffective()
	{
		return effective;
	}

	public void          setEffective(boolean effective)
	{
		this.effective = effective;
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* persisted attributes */

	private Contractor   contractor;
	private FirmOrder    order;
	private Invoice      invoice;
	private BigDecimal   expense;
	private BigDecimal   income;
	private boolean      effective;
}