package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.IncValues;

/* com.tverts: retrade domain (invoices actions) */

import com.tverts.retrade.domain.invoice.actions.ActInvoiceBase;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceBuySellSave;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceBuySellUpdate;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceSaveBase;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Collects some constants and static routines
 * for dealing with {@link Invoice}s.
 *
 * @author anton.baukin@gmail.com
 */
public class Invoices
{
	/* invoices and states types */

	/**
	 * Unity Type name for {@link Invoice} of buying
	 * goods to a store.
	 */
	public static final String TYPE_INVOICE_BUY      =
	  "ReTrade: Invoice: Buy";

	/**
	 * Unity Type name for {@link Invoice} of selling
	 * goods from a store.
	 */
	public static final String TYPE_INVOICE_SELL     =
	  "ReTrade: Invoice: Sell";

	/**
	 * Unity Type name for altered {@link Invoice}
	 * of selling goods from a store. Note that
	 * Invoice not taking this type actually!
	 */
	public static final String TYPE_INVOICE_SELL_ALT =
	  "ReTrade: Invoice: Sell Altered";

	/**
	 * Special version of Invoice initiated by
	 * Sells Session to take the goods from the Stores.
	 */
	public static final String TYPE_INVOICE_SELLS    =
	  "ReTrade: Sells: Invoice";

	/**
	 * Unity Type name for {@link Invoice} of moving
	 * goods between stores.
	 */
	public static final String TYPE_INVOICE_MOVE     =
	  "ReTrade: Invoice: Move";

	/**
	 * Unity Type name for altered Move {@link Invoice}
	 * of auto-produce the goods.
	 *
	 * Note that Invoice not taking this type actually!
	 */
	public static final String TYPE_INVOICE_MOVE_APR =
	  "ReTrade: Invoice: Auto Produce";

	/**
	 * Unity Type name for altered Move {@link Invoice}
	 * of auto-produce the goods.
	 *
	 * Note that Invoice not taking this type actually!
	 */
	public static final String TYPE_INVOICE_MOVE_FPR =
	  "ReTrade: Invoice: Free Produce";

	/**
	 * Unity Type name for altered Move {@link Invoice}
	 * of correcting the goods volumes.
	 *
	 * Note that Invoice not taking this type actually!
	 */
	public static final String TYPE_INVOICE_MOVE_COR =
	  "ReTrade: Invoice: Volume Correction";

	/**
	 * Invoice of check goods volumes in some Store.
	 * The check overwrites the aggregated volumes
	 * of the goods in that store.
	 */
	public static final String TYPE_VOLUME_CHECK     =
	  "ReTrade: Invoice: Volume Check";

	/**
	 * Unity Type name of {@link Invoice} state with
	 * class {@link InvoiceState}.
	 *
	 * In this state invoice does not affects the
	 * state of the system.
	 */
	public static final String TYPE_INVSTATE_EDIT    =
	  "ReTrade: Invoice State: Edit";

	/**
	 * Unity Type name of {@link Invoice} state with
	 * class {@link InvoiceStateFixed}.
	 *
	 * The goods of the fixed invoice are stored
	 * in the store thus affecting the aggregated values.
	 */
	public static final String TYPE_INVSTATE_FIXED   =
	  "ReTrade: Invoice State: Fixed";

	/**
	 * Order type of Buy-Sell invoices.
	 */
	public static final String OTYPE_INV_BUYSELL     =
	  "ReTrade: Order Type: Invoice Buy-Sell";


	/* action types */

	/**
	 * Saves the invoice.
	 */
	public static final ActionType ACT_SAVE   =
	  ActionType.SAVE;

	/**
	 * Update the invoice. The invoice must be in the edit state.
	 */
	public static final ActionType ACT_UPDATE =
	  ActionType.UPDATE;

	/**
	 * Changes the Invoice' state from {@link InvoiceState} Edit
	 * to {@link InvoiceStateFixed}.
	 */
	public static final ActionType ACT_FIX    =
	  new ActionType("fix", Invoice.class);

	/**
	 * Changes the Invoice' state from {@link InvoiceStateFixed}
	 * to {@link InvoiceState} Edit.
	 */
	public static final ActionType ACT_EDIT   =
	  new ActionType("edit", Invoice.class);


	/* Messages Types */

	public static final String MSG_CREATE_BUY  = "invoice: buy: create";
	public static final String MSG_CREATE_SELL = "invoice: sell: create";
	public static final String MSG_CREATE_MOVE = "invoice: move: create";

	public static final String MSG_FIX_BUY     = "invoice: buy: fix";
	public static final String MSG_FIX_SELL    = "invoice: sell: fix";
	public static final String MSG_FIX_MOVE    = "invoice: move: fix";

	public static final String MSG_EDIT_BUY    = "invoice: buy: edit";
	public static final String MSG_EDIT_SELL   = "invoice: sell: edit";
	public static final String MSG_EDIT_MOVE   = "invoice: move: edit";

	public static final String MSG_UPD_BUY     = "invoice: buy: updated";
	public static final String MSG_UPD_SELL    = "invoice: sell: updated";
	public static final String MSG_UPD_MOVE    = "invoice: move: updated";


	/* action builder parameters */

	/**
	 * Set this parameter to the Unity Type of the invoice
	 * when saving it. This parameter is actually always needed
	 * as {@link Invoice} class has more than one type related.
	 */
	public static final String INVOICE_TYPE         =
	  ActInvoiceBase.class.getName() + ": invoice type";

	/**
	 * As {@link InvoiceState} instance has related Unity also,
	 * the Unity Type must be provided.
	 */
	public static final String INVOICE_STATE_TYPE   =
	  ActInvoiceBase.class.getName() + ": invoice state type";

	/**
	 * Action builder parameter specific to {@link #ACT_UPDATE}.
	 * It is the in-memory {@link InvoiceEdit} of the target invoice.
	 */
	public static final String INVOICE_EDIT         =
	  ActInvoiceBuySellUpdate.class.getName() + ": invoice edit";

	/**
	 * Set this Boolean flag if the Invoice created must
	 * not be ordered via Order Index strategy.
	 */
	public static final String ORDER_NOT            =
	  ActInvoiceSaveBase.class.getName() + ": not order invoice";

	/**
	 * Optional order type (name, or Unity Type) of the
	 * invoice is being saved. By default equals to the
	 * invoice type.
	 */
	public static final String ORDER_TYPE           =
	  ActInvoiceSaveBase.class.getName() + ": invoice order type";

	/**
	 * Assign Invoice instance of the same Unity type
	 * to this property to insert the invoice is being saved
	 * after or before it.
	 *
	 * If this parameter is not set, the invoice is saved
	 * as the first, or as the last item in the order of the
	 * invoices of the same type according to the flag
	 * {@link #ORDER_BEFOREAFTER}.
	 */
	public static final String ORDER_REFERENCE      =
	  ActInvoiceSaveBase.class.getName() + ": order reference";

	/**
	 * Set this Boolean flag to {@code true} (the default)
	 * to insert the invoice after the reference provided
	 * by {@link #ORDER_REFERENCE} parameter (or as the
	 * last invoice if the parameter is not set).
	 *
	 * Defined {@code false} value means to insert before
	 * the reference (or as the first invoice).
	 */
	public static final String ORDER_BEFOREAFTER    =
	  ActInvoiceSaveBase.class.getName() + ": order before/after";

	/**
	 * Defines the Contractor of the Invoice.
	 */
	public static final String INVOICE_CONTRACTOR   =
	  ActInvoiceBuySellSave.class.getName() + ": contractor";


	/* public: invoice types  */

	public static String     getInvoiceTypeName(Invoice invoice)
	{
		UnityType ut = invoice.getInvoiceType();
		return (ut == null)?(null):(ut.getTitleLo());
	}

	public static UnityType  typeInvoiceBuySellOrder()
	{
		return UnityTypes.unityType(Invoice.class, OTYPE_INV_BUYSELL);
	}

	public static UnityType  typeInvoiceBuy()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_BUY);
	}

	public static boolean    isBuyInvoice(Unity unity)
	{
		return (unity != null) &&
		  typeInvoiceBuy().equals(unity.getUnityType());
	}

	public static boolean    isBuyInvoice(Invoice invoice)
	{
		return typeInvoiceBuy().equals(invoice.getInvoiceType());
	}

	public static boolean    isBuySellInvoice(Invoice invoice)
	{
		return typeInvoiceBuy().equals(invoice.getInvoiceType()) ||
		  typeInvoiceSell().equals(invoice.getInvoiceType());
	}

	public static UnityType  typeInvoiceSell()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_SELL);
	}

	public static UnityType  typeInvoiceSellAlt()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_SELL_ALT);
	}

	public static boolean    isSellInvoice(Unity unity)
	{
		return (unity != null) &&
		  typeInvoiceSell().equals(unity.getUnityType());
	}

	public static boolean    isSellInvoice(Invoice invoice)
	{
		return typeInvoiceSell().equals(invoice.getInvoiceType());
	}

	public static UnityType  typeInvoiceMove()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_MOVE);
	}

	public static UnityType  typeInvoiceAutoProduce()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_MOVE_APR);
	}

	public static UnityType  typeInvoiceFreeProduce()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_MOVE_FPR);
	}

	public static UnityType  typeInvoiceCorrection()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_INVOICE_MOVE_COR);
	}

	public static boolean    isMoveInvoice(Unity unity)
	{
		return (unity != null) &&
		  typeInvoiceMove().equals(unity.getUnityType());
	}

	public static boolean    isMoveInvoice(Invoice invoice)
	{
		return typeInvoiceMove().equals(invoice.getInvoiceType());
	}

	public static UnityType  typeVolumeCheck()
	{
		return UnityTypes.unityType(
		  Invoice.class, TYPE_VOLUME_CHECK);
	}

	public static boolean    isVolumeCheck(Unity unity)
	{
		return (unity != null) &&
		  typeVolumeCheck().equals(unity.getUnityType());
	}

	public static boolean    isVolumeCheck(Invoice invoice)
	{
		return typeVolumeCheck().equals(invoice.getInvoiceType());
	}

	public static boolean    isAutoProduceInvoice(InvoiceData d)
	{
		return (d instanceof MoveData) && (d.getSubType() != null) &&
		  (d.getSubType() == 'A');
	}

	public static boolean    isFreeProduceInvoice(InvoiceData d)
	{
		return (d instanceof MoveData) && (d.getSubType() != null) &&
		  (d.getSubType() == 'P');
	}

	public static boolean    isCorrectionInvoice(InvoiceData d)
	{
		return (d instanceof MoveData) && (d.getSubType() != null) &&
		  (d.getSubType() == 'C');
	}


	/* public: codes generation */

	/**
	 * Creates Invoice code of the given type and
	 * the global index.
	 */
	public static String     createInvoiceCode(UnityType type, long index)
	{
		if(typeInvoiceBuy().equals(type))
			return String.format("ЗАК-%d", index);

		if(typeInvoiceSell().equals(type))
			return String.format("ПРО-%d", index);

		if(typeInvoiceMove().equals(type))
			return String.format("ПЕР-%d", index);

		if(typeInvoiceAutoProduce().equals(type))
			return String.format("АВП-%d", index);

		if(typeInvoiceFreeProduce().equals(type))
			return String.format("СВП-%d", index);

		if(typeInvoiceCorrection().equals(type))
			return String.format("КОР-%d", index);

		if(typeVolumeCheck().equals(type))
			return String.format("ИНВ-%d", index);

		return null;
	}

	/**
	 * Creates Invoice code of the given type and
	 * the per-day index.
	 */
	public static String     createInvoiceCode
	  (UnityType type, Date date, int index, InvoiceData d)
	{
		if(typeInvoiceBuy().equals(type))
			return createInvoiceCode("ЗАК", date, index);

		if(typeInvoiceSell().equals(type))
			return createInvoiceCode("ПРО", date, index);

		//?: {implemented as move invoice}
		if(typeInvoiceMove().equals(type))
			//?: {automatic production}
			if(Invoices.isAutoProduceInvoice(d))
				return createInvoiceCode("АВП", date, index);
			//?: {free production}
			else if(Invoices.isFreeProduceInvoice(d))
				return createInvoiceCode("СВП", date, index);
			//?: {volume correction}
			else if(Invoices.isCorrectionInvoice(d))
				return createInvoiceCode("КОР", date, index);
			//~: regular move
			else
				return createInvoiceCode("ПЕР", date, index);

		if(typeVolumeCheck().equals(type))
			return createInvoiceCode("ИНВ", date, index);
		
		return null;
	}

	public static String     createInvoiceCode(String prefix, Date date, int index)
	{
		return String.format("%s-%s/%d",
		  prefix, DU.date2str(date), index
		);
	}

	public static String     createInvoiceCode(Domain d, UnityType type)
	{
		return createInvoiceCode(type,
		  bean(IncValues.class).txIncValue(d, type, "code", 1)
		);
	}


	/* public: invoice types & states */

	public static UnityType  getInvoiceAlteredType(Invoice invoice)
	{
		if(isSellInvoice(invoice))
			return typeInvoiceSellAlt();

		if(isMoveInvoice(invoice))
			if(isAutoProduceInvoice(invoice.getInvoiceData()))
				return typeInvoiceAutoProduce();
			else if(isFreeProduceInvoice(invoice.getInvoiceData()))
				return typeInvoiceFreeProduce();
			else if(isCorrectionInvoice(invoice.getInvoiceData()))
				return typeInvoiceCorrection();
			else
				throw EX.state();

		return null;
	}

	public static UnityType  getInvoiceEffectiveType(Invoice invoice)
	{
		if(invoice.getInvoiceData().isAltered())
			return getInvoiceAlteredType(invoice);
		return invoice.getInvoiceType();
	}

	public static String     getInvoiceTitleFull(Invoice invoice)
	{
		UnityType ut = EX.assertn(getInvoiceEffectiveType(invoice));

		return SU.cats(
		  ut.getTitleLo(), " №", invoice.getCode(),
		  " от ", DU.date2str(invoice.getInvoiceDate())
		);
	}

	public static UnityType  getInvoiceStateType(Invoice invoice)
	{
		if(invoice.getInvoiceState() == null) return null;
		if(invoice.getInvoiceState().getUnity() == null) return null;
		return invoice.getInvoiceState().getUnity().getUnityType();
	}

	public static String     getInvoiceStateName(Invoice invoice)
	{
		UnityType st = Invoices.getInvoiceStateType(invoice);

		if(Invoices.isInvoiceEdited(invoice))
			return st.getTitleLo();

		if(Invoices.isInvoiceFixed(invoice))
			return st.getTitleLo();

		return "Неизвестно";
	}

	public static UnityType  typeInvoiceStateEdited()
	{
		return UnityTypes.unityType(InvoiceState.class, TYPE_INVSTATE_EDIT);
	}

	public static boolean    isInvoiceEdited(Invoice invoice)
	{
		return typeInvoiceStateEdited().equals(
		  getInvoiceStateType(invoice));
	}

	public static UnityType  typeInvoiceStateFixed()
	{
		return UnityTypes.unityType(
		  InvoiceStateFixed.class, TYPE_INVSTATE_FIXED);
	}

	public static boolean    isInvoiceFixed(Invoice invoice)
	{
		return typeInvoiceStateFixed().equals(
		  getInvoiceStateType(invoice));
	}


	/* public: helpers for the goods */

	public static BigDecimal getInvoiceGoodsCost(Invoice invoice)
	{
		BigDecimal r = BigDecimal.ZERO;

		for(InvGood g : invoice.getInvoiceData().getGoods())
		{
			if(g instanceof BuyGood)
				r = r.add(((BuyGood)g).getCost());

			if(g instanceof SellGood)
				r = r.add(((SellGood)g).getCost());
		}

		//HINT: == means was only nulls
		return BigDecimal.ZERO.equals(r)?(null):(r);
	}

	public static String     getInvoiceGotoCode(UnityType ut)
	{
		if(Invoices.typeInvoiceSell().equals(ut))
			return "sell";
		if(Invoices.typeInvoiceMove().equals(ut))
			return "move";
		if(Invoices.typeInvoiceBuy().equals(ut))
			return "buy";

		throw EX.state("Invoice Go-To Type is not known!");
	}
}