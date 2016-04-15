package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (core + firms + invoices) */

import com.tverts.endure.core.ActUnity;
import com.tverts.retrade.domain.ActionBuilderReTrade;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Actions builder for Firm Payment Orders of types
 * {@link Payments#TYPE_FIRM_ORDER_EXPENSE} and
 * {@link Payments#TYPE_FIRM_ORDER_INCOME}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActFirmOrder extends ActionBuilderReTrade
{
	/* action types */

	/**
	 * Save given {@link FirmOrder} instance.
	 *
	 * {@link #PARAM_PAY_ORDER_TYPE} parameter must be set:
	 * {@link Payments#TYPE_FIRM_ORDER_EXPENSE}, or
	 * {@link Payments#TYPE_FIRM_ORDER_INCOME}.
	 */
	public static final ActionType SAVE     =
	  ActionType.SAVE;

	/**
	 * @see {@link Payments#FIRM_ORDER_ADD_BILL}.
	 */
	public static final ActionType ADD_BILL =
	  new ActionType(FirmOrder.class, "add-invoice-bill");

	/**
	 * @see {@link Payments#FIRM_ORDER_DEL_BILL}.
	 */
	public static final ActionType DEL_BILL =
	  new ActionType(FirmOrder.class, "delete-invoice-bill");


	/* action parameters */

	public static final String PARAM_PAY_ORDER_TYPE    =
	  Payments.PAY_ORDER_TYPE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveFirmOrder(abr);

		if(ADD_BILL.equals(actionType(abr)))
			addInvoiceBill(abr);

		if(DEL_BILL.equals(actionType(abr)))
			deleteInvoiceBill(abr);
	}


	/* protected: action methods */

	protected void saveFirmOrder(ActionBuildRec abr)
	{
		//?: {target is not an Firm Order}
		checkTargetClass(abr, FirmOrder.class);

		//~: save the order
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//0: save order unified mirror
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, findFirmOrderType(abr));

		complete(abr);
	}

	protected void addInvoiceBill(ActionBuildRec abr)
	{
		//?: {target is not an Firm Order}
		checkTargetClass(abr, FirmOrder.class);

		//~: check add operation
		checkOperation(abr, false);

		//~: add action
		chain(abr).first(new OperateInvoiceBill(
		  task(abr), getInvoiceBill(abr), false));

		complete(abr);
	}

	protected void deleteInvoiceBill(ActionBuildRec abr)
	{
		//?: {target is not an Firm Order}
		checkTargetClass(abr, FirmOrder.class);

		//~: check delete operation
		checkOperation(abr, true);

		//~: delete action
		chain(abr).first(new OperateInvoiceBill(
		  task(abr), getInvoiceBill(abr), true));

		complete(abr);
	}


	/* protected: actions support */

	protected UnityType   findFirmOrderType(ActionBuildRec abr)
	{
		Object p = param(abr, Payments.PAY_ORDER_TYPE);

		if(p instanceof String)
			p = UnityTypes.unityType(FirmOrder.class, (String) p);

		if(p == null)
		{
			FirmOrder o = target(abr, FirmOrder.class);
			if(o.getUnity() != null)
				p = o.getUnity().getUnityType();
		}

		if(!(p instanceof UnityType))
			throw EX.arg("Not a Unity Type parameter");

		UnityType ut = (UnityType)p;

		if(!Payments.isFirmOrderExpense(ut) && !Payments.isFirmOrderIncome(ut))
			throw EX.arg("Wrong Firm Order Unity Type: '", ut.getTypeName(), "'!");
		return ut;
	}

	protected InvoiceBill getInvoiceBill(ActionBuildRec abr)
	{
		InvoiceBill ib = param(abr, Payments.INVOICE_BILL, InvoiceBill.class);

		if(ib == null) throw EX.arg("Invoice Bill parameter is not defined!");
		return ib;
	}

	protected void        checkOperation(ActionBuildRec abr, boolean delete)
	{
		InvoiceBill ib = getInvoiceBill(abr);

		//?: {adding & bill refers some order}
		if(!delete) if(ib.getOrder() != null)
			throw EX.state("Invoice Bill [", ib.getPrimaryKey(),
			  "] already refers Firm Order [", ib.getOrder().getPrimaryKey(),
			  "] and may not be added to Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);
		//?: {bill is effective}
		else if(ib.isEffective())
		//?: {expense order, but income bill}
		if(Payments.isFirmOrderExpense(findFirmOrderType(abr)))
		{
			if(ib.getIncome() != null) throw EX.state(
			  "Invoice Bill [", ib.getPrimaryKey(),
			  "] has income value and may not be added to expense Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);

			if(ib.getExpense() == null) throw EX.state(
			  "Invoice Bill [", ib.getPrimaryKey(),
			  "] has no expense value and may not be added to expense Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);
		}
		//?: {income order, but expense bill}
		else if(Payments.isFirmOrderIncome(findFirmOrderType(abr)))
		{
			if(ib.getExpense() != null) throw EX.state(
			  "Invoice Bill [", ib.getPrimaryKey(),
			  "] has expense value and may not be added to income Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);

			if(ib.getIncome() == null) throw EX.state(
			  "Invoice Bill [", ib.getPrimaryKey(),
			  "] has no income value and may not be added to income Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);
		}

		//?: {removing & bill refers no order}
		if(delete) if(ib.getOrder() == null)
			throw EX.state("Invoice Bill [", ib.getPrimaryKey(),
			  "] refers no Firm Order and may not be removed from Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);
		//?: {removing & bill refers not this order}
		else if(!ib.getOrder().equals(target(abr)))
			throw EX.state("Invoice Bill [", ib.getPrimaryKey(),
			  "] refers another Firm Order [", ib.getOrder().getPrimaryKey(),
			  "] and may not be removed from Firm Order [",
			  target(abr, FirmOrder.class).getPrimaryKey(), "]!"
			);
	}


	/* add-delete invoice bill action */

	protected static class OperateInvoiceBill
	          extends      ActionWithTxBase
	{
		/* public: constructor */

		public OperateInvoiceBill(ActionTask task, InvoiceBill bill, boolean delete)
		{
			super(task);
			this.bill   = bill;
			this.delete = delete;
		}


		/* public: Action interface */

		public Object  getResult()
		{
			return bill;
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			FirmOrder o = target(FirmOrder.class);

			//?: {delete operation}
			if(delete)
			{
				//~: clear the reference of bill
				bill.setOrder(null);

				//~: subtract
				if(bill.isEffective())
				{
					o.setTotalIncome(w(r(o.getTotalIncome()).
					  subtract(r(bill.getIncome()))));
					o.setTotalExpense(w(r(o.getTotalExpense()).
					  subtract(r(bill.getExpense()))));
				}
			}
			//~: {add operation}
			else
			{
				//~: set the reference of bill
				bill.setOrder(o);

				//~: add
				if(bill.isEffective())
				{
					o.setTotalIncome(w(r(o.getTotalIncome()).
					  add(r(bill.getIncome()))));
					o.setTotalExpense(w(r(o.getTotalExpense()).
					  add(r(bill.getExpense()))));
				}
			}

			//debug: test the totals
			//(new ShuntInvoicePaymentOrders()).testFirmOrdersTotals(o.getDomain());
		}

		private BigDecimal r(BigDecimal n)
		{
			n = (n == null)?(BigDecimal.ZERO):(n);
			if(BigDecimal.ZERO.compareTo(n) > 0) throw EX.state();
			return n;
		}

		private BigDecimal w(BigDecimal n)
		{
			int i = BigDecimal.ZERO.compareTo(n);
			if(i > 0) throw EX.state();
			return (i == 0)?(null):(n);
		}


		/* invoice bill */

		private InvoiceBill bill;
		private boolean     delete;
	}
}