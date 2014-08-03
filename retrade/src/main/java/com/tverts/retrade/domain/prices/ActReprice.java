package com.tverts.retrade.domain.prices;

/* Java */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: system transactions */

import com.tverts.api.retrade.prices.PriceChanges;
import com.tverts.system.tx.TxPoint;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.sXe;


/**
 * Actions builder for price change documents
 * {@link RepriceDoc} saving and fixing.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActReprice extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE             =
	  ActionType.SAVE;

	public static final ActionType ACT_FIX_PRICES   =
	  new ActionType("fix", RepriceDoc.class);

	public static final ActionType UPDATE           =
	  ActionType.UPDATE;


	/* action builder parameters */

	public static final String CHANGE_TIME          =
	  ActReprice.class.getName() + ": change time";

	public static final String REPRICE_EDIT         =
	  ActReprice.class.getName() + ": reprice doc edit";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveRepriceDoc(abr);

		if(ACT_FIX_PRICES.equals(actionType(abr)))
			fixRepriceDoc(abr);

		if(UPDATE.equals(actionType(abr)))
			updateRepriceDoc(abr);
	}


	/* protected: action methods */

	protected void saveRepriceDoc(ActionBuildRec abr)
	{
		//?: {target is not a RepriceDoc}
		checkTargetClass(abr, RepriceDoc.class);

		//?: {has reprice edit instance} add update
		if(param(abr, REPRICE_EDIT) != null)
			nest(abr, UPDATE, target(abr),
			  REPRICE_EDIT, param(abr, REPRICE_EDIT)
			);

		//~: save the price change doc
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		complete(abr);
	}

	protected void fixRepriceDoc(ActionBuildRec abr)
	{
		//?: {target is not a RepriceDoc}
		checkTargetClass(abr, RepriceDoc.class);

		//?: {the document is already fixed}
		if(target(abr, RepriceDoc.class).getChangeTime() != null)
			throw EX.state("The price change document '",
			  target(abr, RepriceDoc.class).getCode(),
			  "' is already fixed!"
			);

		//~: fix the price change doc
		chain(abr).first(new ActionFixPriceChanges(task(abr)).
		 setChangeTime(param(abr, CHANGE_TIME, Date.class))
		);

		complete(abr);
	}

	protected void updateRepriceDoc(ActionBuildRec abr)
	{
		//?: {target is not a RepriceDoc}
		checkTargetClass(abr, RepriceDoc.class);

		//~: save the price change doc
		chain(abr).first(new ActionUpdateRepriceDoc(
		  task(abr), param(abr, REPRICE_EDIT, RepriceDocEdit.class)));

		complete(abr);
	}


	/* fix price action */

	public static class ActionFixPriceChanges
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public ActionFixPriceChanges(ActionTask task)
		{
			super(task);
		}


		/* public: ActionFixPriceChanges interface */

		public ActionFixPriceChanges setChangeTime(Date ct)
		{
			this.changeTime = ct;
			return this;
		}


		/* public: Action interface */

		public RepriceDoc getResult()
		{
			return target(RepriceDoc.class);
		}


		/* protected: ActionBase interface */

		protected void    execute()
		  throws Throwable
		{
			GetGoods    gg = bean(GetGoods.class);
			RepriceDoc  rd = target(RepriceDoc.class);

			//~: set change time
			if(rd.getChangeTime() != null) throw EX.state();
			rd.setChangeTime((changeTime != null)?(changeTime):(new Date()));

			//~: affect the changes
			for(PriceChange pc : rd.getChanges())
			{
				if(pc.getPriceNew() == null)
					throw EX.arg();

				GoodPrice gp = gg.getGoodPrice(
				  pc.getRepriceDoc().getPriceList(), pc.getGoodUnit());

				//~: remember the current price (if exists)
				pc.setPriceOld((gp == null)?(null):(gp.getPrice()));

				//?: {has good price} assign the new price
				if(gp != null)
					gp.setPrice(pc.getPriceNew());
				//!: create new price entry
				else
				{
					gp = new GoodPrice();

					//~: primary key
					setPrimaryKey(session(), gp,
					  isTestInstance(rd.getPriceList()));

					//~: transaction number
					TxPoint.txn(tx(), gp);

					//~: price list
					gp.setPriceList(rd.getPriceList());

					//~: good unit
					gp.setGoodUnit(pc.getGoodUnit());

					//~: set the price
					gp.setPrice(pc.getPriceNew());

					//!: save it
					session().save(gp);
				}

				//~: the change time
				pc.setChangeTime(rd.getChangeTime());
			}
		}


		/* protected: additional parameters */

		protected Date changeTime;
	}


	/* update action */

	public static class ActionUpdateRepriceDoc
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public ActionUpdateRepriceDoc(ActionTask task, RepriceDocEdit re)
		{
			super(task);

			if(re == null) throw new IllegalArgumentException();
			this.repriceEdit = re;
		}


		/* public: Action interface */

		public RepriceDoc getResult()
		{
			return target(RepriceDoc.class);
		}


		/* protected: ActionBase interface */

		protected void    execute()
		  throws Throwable
		{
			RepriceDoc     rd = target(RepriceDoc.class);
			PriceChanges   pc = rd.getOx();
			RepriceDocEdit re = this.repriceEdit;

			//~: code
			rd.setCode(re.getCode());

			//~: price list
			rd.setPriceList(bean(GetGoods.class).
			  getPriceList(re.getPriceListKey()));
			if(rd.getPriceList() == null) throw new IllegalArgumentException();

			//~: change reason
			pc.setRemarks(re.getChangeReason());

			//~: assign the changes
			assignChanges();

			//~: update ox
			rd.updateOx();
		}

		protected void    assignChanges()
		{
			RepriceDoc     rd = target(RepriceDoc.class);
			RepriceDocEdit re = this.repriceEdit;
			GetGoods       gg = bean(GetGoods.class);
			Long           dk = rd.getDomain().getPrimaryKey();

			//~: build current changes map: good code -> change
			HashMap<String, PriceChange> pcm =
			  new HashMap<String, PriceChange>(rd.getChanges().size());

			for(PriceChange pc : rd.getChanges())
				pcm.put(pc.getGoodUnit().getCode(), pc);

			//~: build result changes map: good code -> change edit
			HashMap<String, PriceChangeEdit> cem =
			  new HashMap<String, PriceChangeEdit>(re.getPriceChanges().size());

			for(PriceChangeEdit pce : re.getPriceChanges())
				if(sXe(pce.getGoodCode()))
					throw EX.state("Price Change good has no code!");
				else
					cem.put(pce.getGoodCode(), pce);

			//~: remove changes that do not exist now
			pcm.keySet().retainAll(cem.keySet());

			//!: remove them from the database
			for(PriceChange pc : rd.getChanges())
				if(!pcm.containsKey(pc.getGoodUnit().getCode()))
					session().delete(pc);

			//~: copy | create the changes edited
			for(String gc : cem.keySet())
			{
				PriceChange     pc = pcm.get(gc);
				PriceChangeEdit pe = cem.get(gc);

				//?: {it does not exist} create it
				if(pc == null)
				{
					pcm.put(gc, pc = new PriceChange());

					//~: set primary key
					setPrimaryKey(session(), pc, isTestInstance(rd));

					//~: reprice doc
					pc.setRepriceDoc(rd);

					//~: good unit
					pc.setGoodUnit(gg.getGoodUnit(dk, gc));
					if(pc.getGoodUnit() == null)
						throw EX.state("Good Unit code [", gc, "] not found!");
				}

				//~: new price
				pc.setPriceNew(pe.getPriceNew());
			}

			//~: set indices of the changes according to the edit
			ArrayList<PriceChange> changes =
			  new ArrayList<PriceChange>(pcm.size());

			for(int i = 0;(i < re.getPriceChanges().size());i++)
			{
				String      gc = re.getPriceChanges().get(i).getGoodCode();
				PriceChange pc = pcm.get(gc);
				if(pc == null) throw EX.state();

				changes.add(pc);
				pc.setDocIndex(i);
			}

			//!: assign the results
			rd.setChanges(changes);
		}


		/* protected: reprice doc edit */

		protected final RepriceDocEdit repriceEdit;
	}
}