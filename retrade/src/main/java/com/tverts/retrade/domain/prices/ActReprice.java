package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

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

/* com.tverts: retrade domain (core + goods + firms) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.firm.Contractor;

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

	protected void saveRepriceDoc(final ActionBuildRec abr)
	{
		//?: {target is not a RepriceDoc}
		checkTargetClass(abr, RepriceDoc.class);

		//?: {has reprice edit instance} add update
		if(param(abr, REPRICE_EDIT) != null)
			nest(abr, UPDATE, target(abr),
			  REPRICE_EDIT, param(abr, REPRICE_EDIT)
			);

		//~: save the price change doc
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setBeforeSave(new Runnable()
		{
			public void run()
			{
				RepriceDoc rd = target(abr, RepriceDoc.class);

				//~: assign primary keys & this document to the changes
				for(PriceChange pc : rd.getChanges())
				{
					//=: primary key
					if(pc.getPrimaryKey() == null)
						HiberPoint.setPrimaryKey(session(abr), pc,
						  HiberPoint.isTestInstance(rd));

					//=: price change document
					pc.setRepriceDoc(rd);

					//=: price list (from the document)
					pc.setPriceList(EX.assertn(rd.getPriceList()));
				}
			}
		}));

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
		  task(abr), param(abr, REPRICE_EDIT, RepriceDocEdit.class))
		);

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

		protected Date changeTime;


		/* Action */

		public RepriceDoc getResult()
		{
			return target(RepriceDoc.class);
		}


		/* protected: Action Base */

		protected void execute()
		  throws Throwable
		{
			RepriceDoc rd = target(RepriceDoc.class);

			//=: initialize the change time
			EX.assertx(rd.getChangeTime() == null);
			rd.setChangeTime((changeTime != null)?(changeTime):(new Date()));

			//~: initialize the changes
			get = bean(GetPrices.class);
			initChanges();

			//~: process them
			processChanges();

			//~: init the object-extraction
			initOx();

			//=: update transaction number
			TxPoint.txn(tx(), rd);

			//~: update the changes document
			rd.updateOx();

			//~: relink the price crosses
			relinkPriceCrosses();
		}

		protected void initChanges()
		{
			RepriceDoc rd = target(RepriceDoc.class);

			//c: for each change
			int ichange = 0;
			for(PriceChange pc : rd.getChanges())
			{
				//?: {has no good unit}
				EX.assertn(pc.getGoodUnit());

				//?: {already has this good}
				EX.assertx(!changes.containsKey(pc.getGoodUnit().getPrimaryKey()),

				  "Price Change for good [", pc.getGoodUnit().getPrimaryKey(),
				  "] with code [", pc.getGoodUnit().getCode(), "] appears twice!"
				);

				//~: remember this change
				changes.put(pc.getGoodUnit().getPrimaryKey(), pc);

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
					HiberPoint.setPrimaryKey(session(), pc,
					  HiberPoint.isTestInstance(rd));

				//=: price change document
				pc.setRepriceDoc(rd);

				//=: price list copy
				pc.setPriceList(rd.getPriceList());

				//=: copy the change time
				pc.setChangeTime(rd.getChangeTime());

				//=: change position
				pc.setDocIndex(ichange++);
			}
		}

		protected void processChanges()
		{
			RepriceDoc rd = target(RepriceDoc.class);

			//~: flush to make selections safe
			HiberPoint.flush(session());

			//c: for each change
			for(PriceChange pc : rd.getChanges())
			{
				//~: find the next change
				PriceChange nxt = get.getPriceChangeAfter(
				  pc.getPriceList().getPrimaryKey(),
				  pc.getGoodUnit().getPrimaryKey(),
				  pc.getChangeTime()
				);

				//HINT: we not update the old price of the
				//  next entry to the new price of our entry
				//  is being inserted in the middle!

				//?: {found the next}
				if(nxt != null)
				{
					//~: find previous change
					PriceChange prv = get.getPriceChangeBefore(
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
				GoodPrice gp = get.getGoodPrice(pc.getPriceList(), pc.getGoodUnit());

				//~: take the price as the old
				if(gp != null)
					pc.setPriceOld(EX.assertn(gp.getPrice()));

				//?: {new price is undefined, remove from the price list}
				if(pc.getPriceNew() == null)
				{
					//~: mark this good as removed
					removed.put(pc.getGoodUnit().getPrimaryKey(), gp);

					//HINT: we remove the good from the price list, and if it's
					//  not there now, this entry has no meaning, but we allow it.

					//?: {has current price} react on added entry
					if(gp != null)
						onRemovePrice(gp);

					//continue;
				}
				//?: {has good price} just assign the new price
				else if(gp != null)
				{
					//=: remember as the updated
					updated.put(pc.getGoodUnit().getPrimaryKey(), gp);

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
					HiberPoint.setPrimaryKey(session(), gp,
					  HiberPoint.isTestInstance(rd.getPriceList()));

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

					//=: remember as the added
					added.put(pc.getGoodUnit().getPrimaryKey(), gp);

					//~: react on added entry
					onAddPrice(gp);
				}
			}
		}

		protected void initOx()
		{
			RepriceDoc   rd = target(RepriceDoc.class);
			PriceChanges ox = rd.getOx();

			//~: fill the old prices
			ox.getOldPrices().clear();
			for(PriceChange pc : rd.getChanges())
			{
				com.tverts.api.retrade.prices.GoodPrice i =
				  new com.tverts.api.retrade.prices.GoodPrice();
				ox.getOldPrices().add(i);

				//=: good key
				i.setGood(pc.getGoodUnit().getPrimaryKey());

				//=: old price
				i.setPrice(pc.getPriceOld());

				//?: {removed}
				if(removed.containsKey(pc.getGoodUnit().getPrimaryKey()))
				{
					EX.assertx(pc.getPriceNew() == null);
					i.setRemoved(true);
				}
			}

			//~: fill the new prices
			ox.getNewPrices().clear();
			for(PriceChange pc : rd.getChanges())
				if(pc.getPriceNew() != null)
			{
				com.tverts.api.retrade.prices.GoodPrice i =
				  new com.tverts.api.retrade.prices.GoodPrice();
				ox.getNewPrices().add(i);

				//=: good key
				i.setGood(pc.getGoodUnit().getPrimaryKey());

				//=: new price
				i.setPrice(pc.getPriceNew());
			}
		}

		/**
		 * Note that no entity relates Good Price except
		 * {@link PriceCross} that links Contractor with
		 * the Price List entry as to it's effective price
		 * within the associated Price Lists.
		 */
		protected void onRemovePrice(GoodPrice gp)
		{
			List<PriceCross> pcs = get.selectCrosses(gp);

			//c: for each actual cross found
			for(PriceCross pc : pcs)
			{
				//~: relink by this contractor
				Map<Long, BigDecimal> cs = relink.get(pc.getContractor());
				if(cs == null) relink.put(pc.getContractor(),
				  cs = new HashMap<Long, BigDecimal>(5));
				cs.put(gp.getGoodUnit().getPrimaryKey(), gp.getPrice());

				//!: remove the cross
				session().delete(pc);
			}
		}

		/**
		 * When adding Good Price we must find out
		 * whether it becomes a new effective price
		 * for each Contractor having the affected
		 * Price List associated.
		 */
		protected void onAddPrice(GoodPrice gp)
		{
			List<PriceCross> pcs = get.findObsoleteCrosses(
			  gp.getPriceList(), gp.getGoodUnit());

			//c: for each obsolete cross found
			for(PriceCross pc : pcs)
			{
				//~: relink by this contractor
				Map<Long, BigDecimal> cs = relink.get(pc.getContractor());
				if(cs == null) relink.put(pc.getContractor(),
				  cs = new HashMap<Long, BigDecimal>(5));
				cs.put(gp.getGoodUnit().getPrimaryKey(), null);

				//!: remove the cross
				session().delete(pc);
			}
		}

		protected void relinkPriceCrosses()
		{
			if(relink.isEmpty()) return;

			RepriceDoc rd = target(RepriceDoc.class);

			//HINT: here we flush the Good Price items to the database
			//  to select them properly as Price Cross candidates.

			HiberPoint.flush(session());

			//c: for each contractor updated
			for(Contractor co : relink.keySet())
			{
				Map<Long, BigDecimal> o = relink.get(co);
				Map<Long, BigDecimal> n = new HashMap<>(o.size());
				newps.put(co, n);

				//c: for each good processed
				for(Long gu : o.keySet())
				{
					//~: find the new effective price
					Object[] x = get.findEffectivePrice(co.getPrimaryKey(), gu);

					//?: {not found it} the good is not available now
					if(x == null)
					{
						n.put(gu, null);
						continue;
					}

					//~: create the cross
					PriceCross pc = new PriceCross();

					//=: primary key
					HiberPoint.setPrimaryKey(session(), pc,
					  HiberPoint.isTestInstance(rd));

					//=: contractor
					pc.setContractor(co);

					//=: firm prices
					pc.setFirmPrices((FirmPrices) x[1]);
					EX.assertx(CMP.eq(co, pc.getFirmPrices().getContractor()));

					//=: good price
					pc.setGoodPrice((GoodPrice) x[0]);

					//=: good unit
					pc.setGoodUnit(pc.getGoodPrice().getGoodUnit());
					EX.assertx(CMP.eq(pc.getGoodUnit(), gu));

					//!: save it
					session().save(pc);
				}
			}
		}


		/* protected: execution state */

		protected GetPrices get;

		//maps: good unit -> price change
		protected Map<Long, PriceChange> changes = new HashMap<>();

		//maps: good unit -> good price (out of the price list)
		protected Map<Long, GoodPrice>   removed = new HashMap<>();

		//maps: good unit -> good price
		protected Map<Long, GoodPrice>   updated = new HashMap<>();

		//maps: good unit -> good price
		protected Map<Long, GoodPrice>   added   = new HashMap<>();

		//maps: contractor -> maps: (good -> old cost)
		protected Map<Contractor, Map<Long, BigDecimal>> relink = new HashMap<>();

		//maps: contractor -> maps: (good -> new cost)
		protected Map<Contractor, Map<Long, BigDecimal>> newps = new HashMap<>();
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
					HiberPoint.setPrimaryKey(session(), pc,
					  HiberPoint.isTestInstance(rd));

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