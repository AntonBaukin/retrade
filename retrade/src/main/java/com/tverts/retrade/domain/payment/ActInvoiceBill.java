package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: aggregation */

import com.tverts.aggr.AggrPoint;

/* com.tverts: endure (aggregation) */

import com.tverts.aggr.AggrAction;
import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;
import com.tverts.endure.aggr.calc.AggrCalcs;
import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;
import com.tverts.endure.aggr.volume.AggrTaskVolumeDelete;

/* com.tverts: retrade domain (core + firms + invoices) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * {@link InvoiceBill} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceBill extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType EFFECT =
	  new ActionType(InvoiceBill.class, "effect");

	public static final ActionType DEFECT =
	  new ActionType(InvoiceBill.class, "defect");


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveInvoiceBill(abr);

		if(EFFECT.equals(actionType(abr)))
			effectInvoiceBill(abr);

		if(DEFECT.equals(actionType(abr)))
			defectInvoiceBill(abr);
	}


	/* protected: action methods */

	protected void saveInvoiceBill(ActionBuildRec abr)
	{
		//?: {target is not an InvoiceBill}
		checkTargetClass(abr, InvoiceBill.class);

		//~: save the bill
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		complete(abr);
	}

	protected void effectInvoiceBill(ActionBuildRec abr)
	{
		//?: {target is not an InvoiceBill}
		checkTargetClass(abr, InvoiceBill.class);

		//~: set effective flag
		chain(abr).first(new EffectAction(task(abr), true));

		complete(abr);
	}

	protected void defectInvoiceBill(ActionBuildRec abr)
	{
		//?: {target is not an InvoiceBill}
		checkTargetClass(abr, InvoiceBill.class);

		//~: remove the contractor debt
		if(target(abr, InvoiceBill.class).getContractor() != null)
			defectContractorDebt(abr);

		//~: clear effective flag
		chain(abr).first(new EffectAction(task(abr), false));

		complete(abr);
	}


	/* protected: handle aggregated values */

	protected void defectContractorDebt(ActionBuildRec abr)
	{
		AggrAction action = new AggrAction(task(abr));

		//~: remove contractor debt
		deleteContractorDebt(abr, action);

		//!: add the action to the chain
		if(!action.isEmpty())
			chain(abr).first(action);
	}

	protected void deleteContractorDebt(ActionBuildRec abr, AggrAction action)
	{
		InvoiceBill ib = target(abr, InvoiceBill.class);

		//~: load the aggregated value
		AggrValue   av = bean(GetAggrValue.class).loadAggrValue(
		  ib.getContractor().getPrimaryKey(),
		  Contractors.AGGRVAL_CONTRACTOR_DEBT, null);

		//~: create the aggregation request
		AggrRequest ar = new AggrRequest();

		//~: assign the value
		ar.setAggrValue(av);

		//~: create volume aggregation task to delete
		AggrTaskVolumeDelete task = new AggrTaskVolumeDelete();

		//~: task source: this bill
		task.setSourceClass(InvoiceBill.class);
		task.setSource(ib.getPrimaryKey());
		task.setOrderPath("invoice.orderIndex");

		//~: assign request task & add it to the action
		ar.setAggrTask(task);
		action.add(ar);
	}


	/* public: effect action */

	public static class EffectAction extends ActionWithTxBase
	{
		/* public: constructor */

		public EffectAction(ActionTask task, boolean effective)
		{
			super(task);
			this.effective = effective;
		}


		/* public: Action interface */

		public InvoiceBill getResult()
		{
			return target(InvoiceBill.class);
		}


		/* protected: ActionBase interface */

		protected void     execute()
		  throws Throwable
		{
			//~: assign the effective flag
			target(InvoiceBill.class).setEffective(effective);

			//?: {effect order}
			if(effective)
			{
				//0: recalculate income-expense
				updateBillValues();

				//1: update firm order
				updateFirmOrder();

				//2: create aggregation requests
				createAggrRequests();
			}
			//~: {defect order}
			else
			{
				//0: create aggregation requests

				//HINT: aggregation components of this Bill
				// are removed by AggrAction created in the builder

				//1: update firm order
				updateFirmOrder();

				//2: clear income-expense
				updateBillValues();
			}
		}


		/* protected: execution stages */

		protected void updateBillValues()
		{
			InvoiceBill ib = target(InvoiceBill.class);

			ib.setExpense(null);
			ib.setIncome(null);

			if(effective) if(Invoices.isBuyInvoice(ib.getInvoice()))
				ib.setExpense(Invoices.getInvoiceGoodsCost(ib.getInvoice()));
			else
				ib.setIncome(Invoices.getInvoiceGoodsCost(ib.getInvoice()));
		}

		protected void updateFirmOrder()
		{
			InvoiceBill ib = target(InvoiceBill.class);
			FirmOrder   fo = ib.getOrder();
			if(fo == null) return;

			//?: {effective} add
			if(effective)
			{
				fo.setTotalIncome(w(r(fo.getTotalIncome()).add(r(ib.getIncome()))));
				fo.setTotalExpense(w(r(fo.getTotalExpense()).add(r(ib.getExpense()))));
			}
			//~: {not effective} subtract
			else
			{
				fo.setTotalIncome(w(r(fo.getTotalIncome()).subtract(r(ib.getIncome()))));
				fo.setTotalExpense(w(r(fo.getTotalExpense()).subtract(r(ib.getExpense()))));
			}
		}

		protected void createAggrRequests()
		{
			if(target(InvoiceBill.class).getContractor() == null)
				throw EX.state("Invoice Bill [",
				 target(InvoiceBill.class).getPrimaryKey(),
				 "] has no Contractor!"
				);

			//~: create aggregated debt of the contractor
			createContractorDebt();
		}

		protected void createContractorDebt()
		{
			InvoiceBill ib = target(InvoiceBill.class);

			//~: load the aggregated value
			AggrValue   av = bean(GetAggrValue.class).loadAggrValue(
			  ib.getContractor().getPrimaryKey(),
			  Contractors.AGGRVAL_CONTRACTOR_DEBT, null);

			//~: create the aggregation request
			AggrRequest ar = new AggrRequest();

			//~: assign the value
			ar.setAggrValue(av);

			//~: create volume aggregation task
			AggrTaskVolumeCreate task = new AggrTaskVolumeCreate();

			//~: task source: this bill
			task.setSourceClass(InvoiceBill.class);
			task.setSource(ib.getPrimaryKey());
			task.setOrderPath("invoice.orderIndex");

			//~: set the volumes
			task.setVolumePositive(ib.getIncome());
			task.setVolumeNegative(ib.getExpense());

			//~: assign request task & add it to the action
			ar.setAggrTask(task);

			//~: invoice timestamp for volume calculations
			task.param(AggrCalcs.PARAM_SOURCE_TIME,
			  ib.getInvoice().getInvoiceDate());

			//!: run it
			AggrPoint.aggr(ar);
		}


		/* private: helpers */

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


		/* protected: effective parameter */

		protected final boolean effective;
	}
}