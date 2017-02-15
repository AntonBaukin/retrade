package com.tverts.retrade.domain.invoice.actions;

/* standard Java classes */

import java.util.HashSet;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.DelayedEntity;
import com.tverts.endure.DelayedUnity;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import static com.tverts.endure.UnityTypes.unityType;

/* com.tverts: endure (aggregation) */

import com.tverts.aggr.AggrAction;
import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;

/* com.tverts: aggregator (volume) */

import com.tverts.endure.aggr.volume.AggrTaskVolumeCreate;
import com.tverts.endure.aggr.volume.AggrTaskVolumeDelete;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.ResGood;


/**
 * Implementation base for Actions Builders creating
 * aggregation requests to fix or edit Invoices.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActInvoiceAggrBase extends ActInvoiceBase
{
	/* action types */

	public static ActionType AGGR_EDIT_TO_FIXED =
	  new ActionType(Invoice.class, "aggr: fixing invoice");

	/**
	 * Send this request BEFORE changing the state of
	 * the target Invoice from fixed into edit.
	 */
	public static ActionType AGGR_FIXED_TO_EDIT =
	  new ActionType(Invoice.class, "aggr: editing invoice");


	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceState(ActionBuildRec abr)
	{
		UnityType st = getInvoiceStateType(abr);

		return Invoices.typeInvoiceStateFixed().equals(st) ||
		  Invoices.typeInvoiceStateEdited().equals(st);
	}


	/* protected: ActInvoiceBase interface */

	protected void selectBuildActions(ActionBuildRec abr)
	{
		//?: {creating fixed state}
		if(AGGR_EDIT_TO_FIXED.equals(actionType(abr)))
			aggrCreateFixedState(abr);

		//?: {creating edit state}
		if(AGGR_FIXED_TO_EDIT.equals(actionType(abr)))
			aggrCreateEditState(abr);
	}


	/* protected: aggregate on create fixed state */

	protected static class AggrCreateFixedState
	{
		public ActionBuildRec   abr;
		public AggrAction       action;
		public InvoiceData      data;
		public Long             store;
		public DelayedEntity    source;
		public GetAggrValue     getav;
		public InvGood          good;
	}

	protected abstract void checkCreateFixedStateAltered(AggrCreateFixedState x);

	/**
	 * Dispatches creating aggregation requests for
	 * the Invoice Good set in the structure.
	 */
	protected abstract void aggrCreateGoodAggrItems(AggrCreateFixedState x);

	protected abstract void aggrInitGoodUnitsInStoreVolume
	  (AggrCreateFixedState x, AggrTaskVolumeCreate task);

	protected void          aggrCreateFixedState(ActionBuildRec abr)
	{
		AggrCreateFixedState x = new AggrCreateFixedState();

		//~: fill create structure
		x.abr    = abr;
		x.action = new AggrAction(task(abr));
		x.data   = getInvoiceData(abr);
		x.store  = x.data.getStore().getPrimaryKey();
		x.source = new DelayedUnity(x.data.getInvoice());
		x.getav  = bean(GetAggrValue.class);

		//?: {invoice is altered} check it
		if(x.data.isAltered())
			checkCreateFixedStateAltered(x);

		//~: create the aggregation requests
		aggrCreateFixedState(x);

		//!: add the action
		chain(abr).first(x.action);

		complete(abr);
	}

	protected void          aggrCreateFixedState(AggrCreateFixedState x)
	{
		//?: {invoice is altered}
		if(x.data.isAltered())
			aggrCreateFixedStateAltered(x);
		else
			aggrCreateFixedStateRegular(x);
	}

	protected void          aggrCreateFixedStateRegular(AggrCreateFixedState x)
	{
		for(InvGood good : x.data.getGoods())
		{
			x.good = good;
			aggrCreateGoodAggrItems(x);
		}
	}

	protected void          aggrCreateFixedStateAltered(AggrCreateFixedState x)
	{
		for(ResGood good : x.data.getResGoods())
		{
			x.good = good;
			aggrCreateGoodAggrItems(x);
		}
	}

	/**
	 * Creates requests to add components of Aggregated
	 * Value {@link Goods#AGGRVAL_STORE_VOLUME} of the
	 * {@link GoodUnit}s of the goods in the target invoice.
	 */
	protected void          aggrCreateGoodUnitsInStoreVolume(AggrCreateFixedState x)
	{
		//~: unity type of the target aggregated value
		UnityType   avt = unityType(
		  AggrValue.class, Goods.AGGRVAL_STORE_VOLUME);

		//~: load the aggregated value
		AggrValue   val = x.getav.loadAggrValue(
		  x.store, avt, x.good.getGoodUnit().getPrimaryKey());

		//~: create the aggregation request
		AggrRequest req = new AggrRequest();

		//~: request aggregated value
		req.setAggrValue(val);

		//~: request source: the invoice
		req.setSource(x.source);

		//~: create the task & init it
		AggrTaskVolumeCreate task = new AggrTaskVolumeCreate();

		//~: set the volume
		aggrInitGoodUnitsInStoreVolume(x, task);

		//~: assign request task
		req.setAggrTask(task);

		//!: add the aggregation request
		x.action.add(req);
	}


	/* protected: aggregate on create edit state */

	protected static class AggrCreateEditState
	{
		public ActionBuildRec    abr;
		public AggrAction        action;
		public InvoiceData       data;
		public InvoiceStateFixed state;
		public Long              store;
		public Unity             invuni;
		public GetAggrValue      getav;
		public GoodUnit          good;
	}

	protected abstract void aggrDeleteGoodItems(AggrCreateEditState x);

	protected void          aggrCreateEditState(ActionBuildRec abr)
	{
		AggrCreateEditState x = new AggrCreateEditState();

		//~: fill create structure
		x.abr    = abr;
		x.action = new AggrAction(task(abr));
		x.data   = getInvoiceData(abr);
		x.state  = (InvoiceStateFixed)getInvoiceState(abr);
		x.store  = getInvoiceData(abr).getStore().getPrimaryKey();
		x.invuni = x.state.getInvoice().getUnity();
		x.getav  = bean(GetAggrValue.class);

		//~: collect the good units and delete the items
		aggrDeleteAggrItems(x);

		//!: add the action
		chain(abr).first(x.action);

		complete(abr);
	}

	protected void          aggrDeleteItemsCollectGoods
	  (AggrCreateEditState x, Set<GoodUnit> goods)
	{
		//?: {this invoice is regular}
		if(!x.data.isAltered()) for(InvGood g : x.data.getGoods())
			goods.add(g.getGoodUnit());

		//?: {this is an altered sell invoice}
		if(x.data.isAltered()) for(ResGood g : x.data.getResGoods())
			goods.add(g.getGoodUnit());
	}

	protected void          aggrDeleteAggrItems(AggrCreateEditState x)
	{
		//~: collect the good units
		Set<GoodUnit> goods = new HashSet<GoodUnit>(17);
		aggrDeleteItemsCollectGoods(x, goods);

		//~: for all the goods collected
		for(GoodUnit good : goods)
		{
			x.good = good;

			//~: delete the aggregated items for this good
			aggrDeleteGoodItems(x);
		}
	}

	/**
	 * Creates requests to remove components of Aggregated
	 * Value {@link Goods#AGGRVAL_STORE_VOLUME} of the
	 * {@link GoodUnit}s of the goods in the target invoice.
	 */
	protected void          aggrDeleteGoodUnitInStoreVolume(AggrCreateEditState x)
	{
		UnityType avt = unityType(AggrValue.class, Goods.AGGRVAL_STORE_VOLUME);

		//~: load the aggregated value
		AggrValue   val = x.getav.loadAggrValue(
		  x.store, avt, x.good.getPrimaryKey());

		//~: create the aggregation request
		AggrRequest req = new AggrRequest();

		//~: request aggregated value
		req.setAggrValue(val);

		//~: request source: the invoice
		req.setSource(x.invuni);


		AggrTaskVolumeDelete task = new AggrTaskVolumeDelete();

		//~: assign request task   <-- filled automatically
		req.setAggrTask(task);

		//!: add the aggregation request
		x.action.add(req);
	}
}