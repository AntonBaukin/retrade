package com.tverts.retrade.domain.payment;

/* com.tverts: actions */

import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;


/**
 * Collects some constants and static routines
 * for dealing with {@link PayOrder}s and the
 * related objects.
 *
 * @author anton.baukin@gmail.com
 */
public class Payments
{
	/* unity types */

	/**
	 * Unity Type name for {@link FirmOrder}
	 * with income payments (debits).
	 */
	public static final String TYPE_FIRM_ORDER_INCOME  =
	  "ReTrade: Payment Order: Firm: Income";

	/**
	 * Unity Type name for {@link FirmOrder}
	 * with outcome payments (credits).
	 */
	public static final String TYPE_FIRM_ORDER_EXPENSE =
	  "ReTrade: Payment Order: Firm: Expense";

	/**
	 * Unity Type of {@link Settling}.
	 */
	public static final String TYPE_SETTLING           =
	  "ReTrade: Payment: Settling";


	/* action types */

	/**
	 * Send this task to make {@link InvoiceBill} instance
	 * related to the Invoice is being fixed to become effective.
	 */
	public static final ActionType INVOICE_BILL_EFFECT =
	  ActInvoiceBill.EFFECT;

	/**
	 * Send this task to make {@link InvoiceBill} instance
	 * related to the Invoice is being edited to become ineffective.
	 */
	public static final ActionType INVOICE_BILL_DEFECT =
	  ActInvoiceBill.DEFECT;

	/**
	 * Adds Invoice Bill to this order updating total amounts.
	 * Note that income-expense type is checked.
	 */
	public static final ActionType FIRM_ORDER_ADD_BILL =
	  ActFirmOrder.ADD_BILL;

	/**
	 * Deletes Invoice Bill from this updating total amounts.
	 */
	public static final ActionType FIRM_ORDER_DEL_BILL =
	  ActFirmOrder.DEL_BILL;


	/* action parameters */

	/**
	 * Parameter with Unity Type of {@link PayOrder}
	 * is being saved.
	 */
	public static final String PAY_ORDER_TYPE          =
	  Payments.class.getName() + ": payment order type";

	/**
	 * {@link InvoiceBill} reference for add-delete actions.
	 */
	public static final String INVOICE_BILL            =
	  Payments.class.getName() + ": invoice bill";

	/**
	 * Parameter with Payment order reference.
	 *
	 * The payments are always inserted after the
	 * reference given, or as the first item if
	 * the latter is undefined.
	 *
	 * The owner of the Payments order is their Domain.
	 */
	public static final String PAYMENT_ORDER_REFERENCE =
	  Payments.class.getName() + ": payment order reference";

	/**
	 * Set this parameter to true to automatically get
	 * order reference by the payment time. If a reference
	 * set, this parameter is ignored.
	 */
	public static final String PAYMENT_AUTO_ORDER      =
	  Payments.class.getName() + ": payment auto order";


	/* public: payment types access */

	public static UnityType typeFirmOrderExpense()
	{
		return UnityTypes.unityType(FirmOrder.class, TYPE_FIRM_ORDER_EXPENSE);
	}

	public static boolean   isFirmOrderExpense(UnityType ut)
	{
		return typeFirmOrderExpense().equals(ut);
	}

	public static UnityType typeFirmOrderIncome()
	{
		return UnityTypes.unityType(FirmOrder.class, TYPE_FIRM_ORDER_INCOME);
	}

	public static boolean   isFirmOrderIncome(UnityType ut)
	{
		return typeFirmOrderIncome().equals(ut);
	}
}