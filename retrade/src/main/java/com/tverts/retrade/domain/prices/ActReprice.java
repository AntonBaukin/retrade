package com.tverts.retrade.domain.prices;

/* Java */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

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
import com.tverts.actions.ActionsPoint;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade api (prices) */

import com.tverts.api.retrade.prices.PriceChanges;

/* com.tverts: retrade domain (core + goods) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


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

	/**
	 * Action to fix the prices of the document and to
	 * update the Price List referred if there are no
	 * Price Change Documents following.
	 *
	 * Note that when {@link #CHANGE_TIME} parameter
	 * is set, and it is in the past (there are future
	 * changes), the implementation correctly takes
	 * the previous price and does not update the
	 * actual Price List good price!
	 */
	public static final ActionType ACT_FIX_PRICES   =
	  new ActionType("fix", RepriceDoc.class);

	public static final ActionType UPDATE           =
	  ActionType.UPDATE;


	/* action builder parameters */

	/**
	 * Required parameter for the save action. Denotes
	 * the Unity Type name of the Price Change Document.
	 */
	public static final String REPRICE_TYPE         =
	  ActionsPoint.UNITY_TYPE;

	/**
	 * Assign this parameter when fixing the document
	 * within the Genesis process. For now it is not
	 * possible to do this throw UI by the users.
	 */
	public static final String CHANGE_TIME          =
	  ActReprice.class.getName() + ": change time";

	public static final String REPRICE_EDIT         =
	  ActReprice.class.getName() + ": price change document edit";


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

		//~: create the payment way unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, chooseRepriceType(abr));

		complete(abr);
	}

	protected void fixRepriceDoc(ActionBuildRec abr)
	{
		//?: {target is not a RepriceDoc}
		checkTargetClass(abr, RepriceDoc.class);

		//?: {the document is already fixed}
		EX.assertx((target(abr, RepriceDoc.class).getChangeTime() == null),

		  "Price Change Document [", target(abr, RepriceDoc.class).getPrimaryKey(),
		  "] with code [", target(abr, RepriceDoc.class).getCode(), "] is already fixed!"
		);

		//?: {document has no changed}
		EX.asserte(target(abr, RepriceDoc.class).getChanges(),

		  "Price Change Document [", target(abr, RepriceDoc.class).getPrimaryKey(),
		  "] with code [", target(abr, RepriceDoc.class).getCode(), "] has no changes!"
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


	/* protected: helpers */

	protected UnityType chooseRepriceType(ActionBuildRec abr)
	{
		RepriceDoc rd = target(abr, RepriceDoc.class);
		String     tn = param(abr, REPRICE_TYPE, String.class);

		if(tn != null) //?: {has type name defined}
			return UnityTypes.unityType(RepriceDoc.class, tn);

		throw EX.state("Can't figure out the Unity Type of Price Change Document [",
		  rd.getPrimaryKey(), "] code [", rd.getCode(), "]!");
	}


	/* fix price action */

	protected static class ActionFixPriceChanges
	          extends      ActionWithTxBase
	{
		/* public: constructor */

		public ActionFixPriceChanges(ActionTask task)
		{
			super(task);
		}


		/* Action Fix Price Changes */

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
			GetPrices  gg = bean(GetPrices.class);
			RepriceDoc rd = target(RepriceDoc.class);

			//~: set change time
			EX.assertx(rd.getChangeTime() == null);
			rd.setChangeTime((changeTime != null)?(changeTime):(new Date()));

			//?: {has no changes}
			EX.asserte(rd.getChanges());

			//~: processed goods
			HashSet<Long> goods = new HashSet<>(rd.getChanges().size());

			//~: affect the changes
			int ichange = 0;
			for(PriceChange pc : rd.getChanges())
			{
				//?: {has no good unit}
				EX.assertn(pc.getGoodUnit());

				//?: {already has this good}
				EX.assertx(!goods.contains(pc.getGoodUnit().getPrimaryKey()),

				  "Price Change for good [", pc.getGoodUnit().getPrimaryKey(),
				  "] with code [", pc.getGoodUnit().getCode(), "] appears twice!"
				);

				goods.add(pc.getGoodUnit().getPrimaryKey());

				//~: check the price
				if(pc.getPriceNew() != null)
				{
					EX.assertx(CMP.grZero(pc.getPriceNew()));

					//~: the scale is .XY
					if(pc.getPriceNew().scale() < 2)
						pc.setPriceNew(pc.getPriceNew().setScale(2));

					EX.assertx(pc.getPriceNew().scale() == 2);
				}

				//=: primary key
				if(pc.getPrimaryKey() == null)
					setPrimaryKey(session(), pc, isTestInstance(rd));

				//=: price change document
				pc.setRepriceDoc(rd);

				//=: price list copy
				pc.setPriceList(rd.getPriceList());

				//=: change time copy
				pc.setChangeTime(rd.getChangeTime());

				//=: change position
				pc.setDocIndex(ichange++);

				//~: find the next change
				PriceChange nxt = gg.getPriceChangeAfter(
				  pc.getPriceList().getPrimaryKey(),
				  pc.getGoodUnit().getPrimaryKey(),
				  pc.getChangeTime()
				);

				//HINT: we not update the old price of the
				//  next entry to the new price of our entry
				//  inserted in the middle!

				//?: {found the next}
				if(nxt != null)
				{
					//~: find previous change
					PriceChange prv = gg.getPriceChangeBefore(
					  pc.getPriceList().getPrimaryKey(),
					  pc.getGoodUnit().getPrimaryKey(),
					  pc.getChangeTime()
					);

					//?: {found it} assign the old price
					if(prv != null)
						pc.setPriceOld(prv.getPriceNew());

					//HINT: we inserting the change in the middle and
					// do not update the actual price of the list!

					continue;
				}

				//~: load the actual good price
				GoodPrice gp = gg.getGoodPrice(pc.getPriceList(), pc.getGoodUnit());

				//~: take the price as the old
				if(gp != null)
					pc.setPriceOld(EX.assertn(gp.getPrice()));

				//?: {new price is undefined, remove from the price list}
				if(pc.getPriceNew() == null)
				{
					//HINT: we remove the good from the price list, but it is not
					//  there now! This entry has no meaning

					//?: {has no current price}
					if(gp == null) continue;

					//TODO implement Removing Good Price entities (eliminate all direct GoodPrice references)
					throw EX.unop("Removing Good Price entity is not implemented!");

					//continue;
				}
				//?: {has good price} just assign the new price
				else if(gp != null)
				{
					//=: assign the new price
					gp.setPrice(pc.getPriceNew());

					//=: update transaction number
					TxPoint.txn(tx(), gp);
				}
				//~: create new price entry
				else
				{
					gp = new GoodPrice();

					//=: primary key
					setPrimaryKey(session(), gp, isTestInstance(rd.getPriceList()));

					//=: transaction number
					TxPoint.txn(tx(), gp);

					//=: price list
					gp.setPriceList(pc.getPriceList());

					//=: good unit
					gp.setGoodUnit(pc.getGoodUnit());

					//=: set the price
					gp.setPrice(pc.getPriceNew());

					//!: save it
					session().save(gp);
				}
			}
		}


		/* protected: additional parameters */

		protected Date changeTime;
	}


	/* update action */

	protected static class ActionUpdateRepriceDoc
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
			rd.setPriceList(bean(GetPrices.class).
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
			  new HashMap<>(rd.getChanges().size());

			for(PriceChange pc : rd.getChanges())
				pcm.put(pc.getGoodUnit().getCode(), pc);

			//~: build result changes map: good code -> change edit
			HashMap<String, PriceChangeEdit> cem =
			  new HashMap<>(re.getPriceChanges().size());

			for(PriceChangeEdit pce : re.getPriceChanges())
				if(SU.sXe(pce.getGoodCode()))
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
			  new ArrayList<>(pcm.size());

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