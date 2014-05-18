package com.tverts.retrade.domain.invoice.actions;

/* standard Java classes */

import java.util.Date;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;
import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: endure core */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade (goods + invoices) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;
import com.tverts.retrade.domain.invoice.ResGood;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides basic routines for action builders dealing
 * with {@link Invoice}s.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActInvoiceBase extends ActionBuilderReTrade
{
	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		//?: {it is not an invoice}
		if(!Invoice.class.isAssignableFrom(targetClass(abr)))
			return;

		//?: {the invoice of the desired state/type/...}
		if(!isThatInvoice(abr))
			return;

		//~: dispatch the building
		selectBuildActions(abr);
	}

	/* action builder parameters */

	public static final String INVOICE_TYPE         =
	  Invoices.INVOICE_TYPE;

	public static final String INVOICE_STATE_TYPE   =
	  Invoices.INVOICE_STATE_TYPE;


	/* protected: is that invoice */

	protected boolean      isThatInvoice(ActionBuildRec abr)
	{
		return isThatInvoiceType(abr) && isThatInvoiceState(abr);
	}

	protected boolean      isThatInvoiceType(ActionBuildRec abr)
	{
		return false;
	}

	protected boolean      isThatInvoiceState(ActionBuildRec abr)
	{
		return false;
	}


	/* protected: action build dispatching */

	protected abstract void selectBuildActions(ActionBuildRec abr);

	protected void          updateInvoice(ActionBuildRec abr)
	{
		//~: request for the standard update action
		xnest(abr, Invoices.ACT_UPDATE, target(abr));
	}


	/* protected: invoice and it's state helpers */

	protected InvoiceState getInvoiceState(ActionBuildRec abr)
	{
		return target(abr, Invoice.class).getInvoiceState();
	}

	protected InvoiceData  getInvoiceData(ActionBuildRec abr)
	{
		return target(abr, Invoice.class).getInvoiceData();
	}

	/**
	 * Detects the unity type of the target invoice. If the invoice
	 * has no Unity yet (is not saved yet), tries to find it out
	 * the type name by {@link #INVOICE_TYPE} parameter.
	 *
	 * Returns {@code null} if the type is not known.
	 */
	protected UnityType    getInvoiceType(ActionBuildRec abr)
	{
		Invoice i = target(abr, Invoice.class);
		Unity   u  = i.getUnity();

		//?: {has unity} no problems
		if((u != null) && (u.getUnityType() != null))
			return u.getUnityType();

		//?: {has invoice type direct reference}
		if(i.getInvoiceType() != null)
			return i.getInvoiceType();

		//~: inspect the parameter
		Object p = param(abr, INVOICE_TYPE);

		if(p instanceof String)
			return UnityTypes.unityType(Invoice.class, (String)p);

		if(p instanceof UnityType)
			return (UnityType)p;

		//~: take the type from the parameter
		throw EX.state("Imvoice Unity Type parameter is not set!");
	}

	/**
	 * Returns the name of the Invoice' Unity Type, or
	 * {@code null} if the type is unknown.
	 */
	protected String       getInvoiceTypeName(ActionBuildRec abr)
	{
		UnityType utype = getInvoiceType(abr);
		return (utype == null)?(null):(utype.getTypeName());
	}

	protected UnityType    getInvoiceStateType(ActionBuildRec abr)
	{
		InvoiceState state = getInvoiceState(abr);
		Unity        unity = (state == null)?(null):(state.getUnity());

		//?: {has unity} no problems
		if((unity != null) && (unity.getUnityType() != null))
			return unity.getUnityType();

		//~: get the type parameter
		return param(abr, INVOICE_STATE_TYPE, UnityType.class);
	}


	/* update invoice resulting goods */

	public static class UpdateInvoiceResults
	       extends      ActionWithTxBase
	{
		/* constructor */

		public UpdateInvoiceResults(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public InvoiceData getResult()
		{
			return target(Invoice.class).getInvoiceData();
		}


		/* protected: ActionBase interface */

		protected void    execute()
		  throws Throwable
		{
			//~: delete all the resulting goods present
			clearResults();

			//?: calculate the new results
			if(needCalc())
				calcResults();

			//~: init them
			initResults();

			//HINT: goods are saved with the data
		}

		protected void    clearResults()
		{
			InvoiceData data = getResult();
			if(data.getResGoods().isEmpty()) return;

			for(ResGood g : data.getResGoods())
				session().delete(g);

			data.getResGoods().clear();
		}

		protected boolean needCalc()
		{
			return getResult().isAltered();
		}

		protected void    calcResults()
		{
			InvoiceData data = getResult();

			List<ResGood> res = bean(GetInvoice.class).calcInvoice(data);
			if(res != null)
				data.getResGoods().addAll(res);
		}

		protected void    initResults()
		{
			InvoiceData data = getResult();

			for(ResGood g : data.getResGoods())
			{
				//~: primary key
				setPrimaryKey(session(), g, isTestInstance(data));

				//~: (this) data
				g.setData(data);
			}
		}

	}

	public static class UpdateMoveInvoiceResults
	       extends      UpdateInvoiceResults
	{
		/* constructor */

		public UpdateMoveInvoiceResults(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public MoveData getResult()
		{
			return (MoveData) target(Invoice.class).getInvoiceData();
		}


		/* protected: UpdateInvoiceResults interface */

		protected void  calcResults()
		{
			MoveData data = getResult();

			//?: {this is auto-produce invoice} set need-calc, calculate
			if(Invoices.isAutoProduceInvoice(data))
			{
				GetInvoice get  = bean(GetInvoice.class);
				Date       time = target(Invoice.class).getInvoiceDate();

				for(MoveGood g : data.getGoods())
				{
					GoodCalc c = get.getGoodCalc(g.getGoodUnit().getPrimaryKey(), time);

					//?: {product is semi-ready} order to calculate it, not move
					if((c != null) && c.isSemiReady())
						g.setNeedCalc(true);
					else
						g.setNeedCalc(null);
				}

				//!: calculate
				super.calcResults();
			}

			//?: {free-produce | correction} create resulting goods from take-only
			if(Invoices.isFreeProduceInvoice(data) || Invoices.isCorrectionInvoice(data))
				for(MoveGood g : data.getGoods())
				{
					//?: {a place-only good} skip it
					if(Boolean.TRUE.equals(g.getMoveOn()))
						continue;

					ResGood r = new ResGood();
					data.getResGoods().add(r);

					//~: good unit
					r.setGoodUnit(g.getGoodUnit());

					//~: volume
					r.setVolume(g.getVolume());
				}
		}
	}
}