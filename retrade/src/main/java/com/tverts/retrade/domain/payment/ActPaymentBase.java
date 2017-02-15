package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;

/* Hibernate Persistence Layer */

import org.hibernate.type.TimestampType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionsCollection.SetOrderIndex;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.ActUnity;

/* com.tverts: endure (aggregation) */

import com.tverts.aggr.AggrAction;
import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;
import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;

/* com.tverts: retrade domain (core + accounts) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.account.Accounts;
import com.tverts.retrade.domain.account.PayIt;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Base class for Payments action builders.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActPaymentBase
       extends        ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* parameters */

	public static final String ORDER_REFERENCE =
	  Payments.PAYMENT_ORDER_REFERENCE;

	public static final String ORDER_AUTO      =
	  Payments.PAYMENT_AUTO_ORDER;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			savePayment(abr);
	}


	/* protected: action methods */

	protected void savePayment(ActionBuildRec abr)
	{
		//?: {target is not a Payment}
		checkTargetClass(abr, Payment.class);

		//~: aggregate on the payment ways
		aggrPayIts(abr);

		//~: set the order index
		setPaymentOrder(abr);

		//?: {the time is not set}
		EX.assertx(target(abr, Payment.class).getTime() != null,
		  "Payment has no time set!"
		);

		//~: effect this payment in the order
		updatePaymentOrder(abr);

		//~: save the payment
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the settling unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getPaymentType(abr));

		complete(abr);
	}


	/* protected: builder support */

	protected abstract UnityType
	               getPaymentType(ActionBuildRec abr);

	protected void updatePaymentOrder(ActionBuildRec abr)
	{
		chain(abr).first(new UpdatePaymentOrder(task(abr), true));
	}


	/* protected: ordering */

	protected void      setPaymentOrder(ActionBuildRec abr)
	{
		Action oa = createOrderAction(abr);

		if(oa != null)
			chain(abr).first(oa);
	}

	public Action       createOrderAction(ActionBuildRec abr)
	{
		Payment tgt = target(abr, Payment.class);
		Payment ref = param(abr, ORDER_REFERENCE, Payment.class);

		//?: {has no reference} auto get it
		if((ref == null) && flag(abr, ORDER_AUTO))
			ref = autoOrderPayment(abr);

		//?: {reference is defined} check the timestamp
		if(ref != null) EX.assertx(
		  !ref.getTime().after(tgt.getTime()),
		  "Requested save Payment with order reference Payment [",
		  ref.getPrimaryKey(), "] that has timestamp after the ",
		  "save target itself which must be inserted after!"
		);

		//HINT: BeforeAfter = true to insert after the defined reference,
		//  or BeforeAfter = false to insert as the first in the order.

		return new SetOrderIndex(task(abr), tgt, ref).
		  setIndexClass(Payment.class).
		  setBeforeAfter(ref != null); //<-- as the first if no reference
	}

	protected Payment   autoOrderPayment(ActionBuildRec abr)
	{
/*

 from Payment p where (p.domain = :domain) and (p.time < :time)
  order by p.time desc, p.orderIndex desc

 */
		return (Payment) session(abr).createQuery(

"from Payment p where (p.domain = :domain) and (p.time < :time)\n" +
" order by p.time desc, p.orderIndex desc"

		).
		  setParameter("domain", target(abr, Payment.class).getDomain()).
		  setParameter("time", target(abr, Payment.class).getTime(), TimestampType.INSTANCE).
		  setMaxResults(1).
		  uniqueResult();
	}


	/* protected: aggregation */

	protected void      aggrPayIts(ActionBuildRec abr)
	{
		//~: aggregate on the self payment
		aggrPayIt(abr, target(abr, Payment.class).getPaySelf());
	}

	protected void      aggrPayIt(ActionBuildRec abr, PayIt it)
	{
		BigDecimal i = target(abr, Payment.class).getIncome();
		BigDecimal e = target(abr, Payment.class).getExpense();

		EX.assertx((i != null) || (e != null),
		  "Payment income and-or expense must be defined!"
		);

		AggrAction action = new AggrAction(task(abr));

		//~: remove contractor debt
		aggrPayIt(abr, it, action);

		//!: add the action to the chain
		if(!action.isEmpty())
			chain(abr).first(action);
	}

	protected void      aggrPayIt(ActionBuildRec abr, PayIt it, AggrAction action)
	{
		Payment p = target(abr, Payment.class);

		//~: load the aggregated value
		AggrValue   av = bean(GetAggrValue.class).loadAggrValue(
		  it.getPrimaryKey(), Accounts.AGGRVAL_PAYIT_BALANCE, null);

		EX.assertn(av, "Pay Way [", it.getPrimaryKey(),
		  "] has no Balance Aggregated Value!");

		//~: create the aggregation request
		AggrRequest ar = new AggrRequest();

		//~: assign the value
		ar.setAggrValue(av);

		//~: create volume aggregation task
		AggrTaskVolumeCreate task = new AggrTaskVolumeCreate();

		//~: volumes
		task.setVolumePositive(p.getIncome());
		task.setVolumeNegative(p.getExpense());

		//~: task source: this payment
		task.setSourceClass(HiberPoint.type(p));
		task.setSource(delayEntity(p));

		//~: assign request task & add it to the action
		ar.setAggrTask(task);
		action.add(ar);
	}


	/* update payment order action */

	public static class UpdatePaymentOrder
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public UpdatePaymentOrder(ActionTask task, boolean effect)
		{
			super(task);
			this.effect = effect;
		}


		/* public: Action interface */

		public Payment getResult()
		{
			return target(Payment.class);
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			Payment    p = target(Payment.class);
			BigDecimal I = r(p.getPayOrder().getActualIncome());
			BigDecimal E = r(p.getPayOrder().getActualExpense());
			BigDecimal i = r(p.getIncome());
			BigDecimal e = r(p.getExpense());

			if(effect)
			{
				I = I.add(i);
				E = E.add(e);
			}
			else
			{
				I = I.subtract(i);
				E = E.subtract(e);
			}

			p.getPayOrder().setActualIncome(w(I));
			p.getPayOrder().setActualExpense(w(E));
		}


		/* protected: support functions */

		protected static BigDecimal r(BigDecimal n)
		{
			if(n == null) return BigDecimal.ZERO;
			EX.assertx(BigDecimal.ZERO.compareTo(n) <= 0);
			return n;
		}

		protected static BigDecimal w(BigDecimal n)
		{
			if(n == null) return null;

			int x = BigDecimal.ZERO.compareTo(n);
			if(x == 0)    return null;
			EX.assertx(x < 0);
			return n;
		}


		/* effect flag */

		protected final boolean effect;
	}
}