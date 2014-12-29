package com.tverts.retrade.domain.prices;

/* Java */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionType;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: retrade api */

import com.tverts.api.retrade.prices.FirmGoodPrice;

/* com.tverts: retrade domain (core + firms) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * This action builder must be assigned
 * to the Contractor class. It handles
 * assigning Price Lists to the firms.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActFirmPrices extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType ASSIGN =
	  new ActionType(Contractor.class, "assign-price-list");


	/* parameters of the action task */

	/**
	 * This required parameter must be a list
	 * of {@link PriceListEntity} references
	 * to assign to the Contractor target.
	 */
	public static final String LISTS        =
	  ActFirmPrices.class.getName() + ": price-lists";


	/**
	 * Collection of {@link FirmGoodPrice} objects where
	 * to add changes related to the target contractor.
	 */
	public static final String FIRM_CHANGES =
	  ActFirmPrices.class.getName() + ": firm-good-prices";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ASSIGN.equals(actionType(abr)))
			assignPriceLists(abr);
	}


	/* protected: assign action */

	@SuppressWarnings("unchecked")
	protected void assignPriceLists(ActionBuildRec abr)
	{
		//?: {target is not a Contractor}
		checkTargetClass(abr, Contractor.class);

		//~: get the price lists
		List lists = EX.assertn(param(abr, LISTS, List.class),
		  "Required parameter with the Price Lists is not given!"
		);

		//~: save the price list
		chain(abr).first(new AssignPriceLists(
		   task(abr), (List<PriceListEntity>)lists,
		  (List<FirmGoodPrice>)param(abr, FIRM_CHANGES, List.class)
		));

		complete(abr);
	}

	protected static class AssignPriceLists extends ActionWithTxBase
	{
		/* public: constructor */

		public AssignPriceLists(ActionTask task,
		  List<PriceListEntity> lists, List<FirmGoodPrice> changes)
		{
			super(task);

			this.lists   = EX.assertn(lists);
			this.changes = changes;
		}

		/* public: Action interface */

		public List<FirmPrices> getResult()
		{
			return this.prices;
		}


		/* protected: ActionBase interface */

		@SuppressWarnings("unchecked")
		protected void execute()
		  throws Throwable
		{
			//~: load existing items
			current = bean(GetPrices.class).
			  loadPrices(target(Contractor.class).getPrimaryKey());

			//~: select current items [good, price, cross]
			crosses = (List<Object[]>) bean(GetPrices.class).
			  selectCurrentPrices(target(Contractor.class).getPrimaryKey());

			//~: map existing by the price lists
			Map<Long, FirmPrices> p2fp = new HashMap<>(current.size());
			for(FirmPrices fp : current)
				EX.assertx(p2fp.put(fp.getPriceList().getPrimaryKey(), fp) == null);

			//HINT: Firm Prices objects are never reused by linking
			//  else Price List. Priority may be updated.

			//~: the resulting list
			prices = new ArrayList<>(lists.size());

			//~: deleted items
			deleted = new HashSet<>(7);

			//~: added items
			added = new HashSet<>(7);

			//c: process the new lists
			for(int i = 0;(i < lists.size());i++)
			{
				PriceListEntity p = EX.assertn(lists.get(i));

				//~: x - existing firm price at the current position
				FirmPrices      x = (i < current.size())?(current.get(i)):(null);

				//~: y - existing firm price
				FirmPrices      y = p2fp.get(p.getPrimaryKey());

				//?: {price list at this position is the same} reuse it
				if((x != null) && (x == y))
				{
					prices.add(x);
					continue;
				}

				//?: {this item is in else position}
				if(y != null)
				{
					prices.add(y);
					continue;
				}

				//?: {has item to delete}
				if(x != null) deleted.add(x);

				//~: create new item for this index
				added.add(y = new FirmPrices());
				prices.add(y);

				//=: contractor
				y.setContractor(target(Contractor.class));

				//=: price list
				y.setPriceList(p);
			}

			//~: cache of price crosses
			this.cache = new HashMap<>(deleted.size());

			//[0]: remove the obsolete associations
			deleted.addAll(current);   //<-- all existing
			deleted.removeAll(prices); //<-- except the retained
			deletePrices();

			//[1]: assign the priorities of all the associations
			for(int i = 0;(i < prices.size());i++)
				prices.get(i).setPriority(i);

			//[2]: add the new links
			savePrices();

			//[3]: reconsider prices
			relinkPrices();
		}

		protected void deletePrices()
		{
			if(deleted.isEmpty()) return;

			//c: for each delete association
			GetPrices get = bean(GetPrices.class);
			for(FirmPrices fp : deleted)
			{
				//~: delete the cross items
				get.deletePriceCrosses(fp, this.cache);

				//~: delete the association
				session().delete(fp);
			}

			//!: flush the session
			HiberPoint.flush(session());
		}

		protected void savePrices()
		{
			if(added.isEmpty()) return;

			//!: flush the session (to update priorities)
			HiberPoint.flush(session());

			for(FirmPrices fp : added)
			{
				//=: primary key
				EX.assertx(fp.getPrimaryKey() == null);
				HiberPoint.setPrimaryKey(session(), fp,
				  HiberPoint.isTestInstance(fp.getPriceList())
				);

				//~: save
				session().save(fp);
			}

			//!: flush the session
			HiberPoint.flush(session());
		}

		@SuppressWarnings("unchecked")
		protected void relinkPrices()
		{
			//~: map previous crosses by the goods [good, price, cross]
			Map<Long, Object[]> m = new HashMap<>(this.crosses.size());
			for(Object[] r : this.crosses) m.put((Long)r[0], r);

			//~: select effective items
			List<Object[]> e = (List<Object[]>) bean(GetPrices.class).
			  selectEffectivePrices(target(Contractor.class).getPrimaryKey());

			//~: all effective goods
			Set<Long> a = new HashSet<>(e.size());

			//~: current price crosses to delete
			Set<Long> d = new HashSet<>(17);

			//~: crosses to add
			Map<Long, Long> i = new HashMap<>(17);

			//c: scan for the changes
			for(Object[] r : e)
			{
				//~: good & price
				Long g = (Long)r[0], p = (Long)r[1];
				EX.assertx( a.add(g), //<-- this good has price
				  "Good Unit [", g, "] price is selected twice for Contractor [",
				  target(Contractor.class).getPrimaryKey(), "]!"
				);

				//~: current record for this good
				Object[] x = m.get(g);

				//?: {has the same price} skip
				if((x != null) && p.equals(x[1]))
					continue; //<-- x[1] is Good Price

				//!: add new cross
				i.put(g, p);

				//?: {had price for this good}
				if(x != null) //<-- x[2] is Price Cross
					d.add((Long) x[2]);
			}

			//~: find obsolete crosses
			{
				Set<Long> xo = new HashSet<>(m.keySet());
				xo.removeAll(a); //<-- remove goods with prices
				for(Long g : xo) d.add((Long)(m.get(g)[2]));
			}

			//?: {has crosses to delete}
			Map<Long, FirmGoodPrice> fgpm = new HashMap<>();
			if(!d.isEmpty())
			{
				//~: for all the crosses
				for(Long x : d)
				{
					//~: take the cross from the items
					PriceCross pc = cache.get(x);
					if(pc == null) pc = (PriceCross) session().
					  load(PriceCross.class, x);

					//?: {has changes to save}
					if(changes != null)
					{
						FirmGoodPrice fgp; changes.add(fgp = new FirmGoodPrice());
						fgpm.put(pc.getGoodUnit().getPrimaryKey(), fgp);

						//=: contractor
						fgp.setContractor(target(Contractor.class).getPrimaryKey());

						//=: good
						fgp.setGood(pc.getGoodUnit().getPrimaryKey());

						//=: old price list
						fgp.setOldList(pc.getGoodPrice().getPriceList().getPrimaryKey());

						//~: old price
						fgp.setOldPrice(pc.getGoodPrice().getPrice());
					}

					//!: delete the cross
					session().delete(pc);
				}


				//HINT: we flush here as price crosses have
				// unique constraint (contractor; good)!

				//!: flush the session
				HiberPoint.flush(session());
			}

			//?: {has no crosses to insert}
			if(i.isEmpty()) return;

			//~: map the associated price lists
			Map<Long, FirmPrices> p2fp = new HashMap<>(prices.size());
			for(FirmPrices fp : prices)
				p2fp.put(fp.getPriceList().getPrimaryKey(), fp);

			//~: insert them
			for(Long g : i.keySet())
			{
				PriceCross pc = new PriceCross();

				//~: load good price
				GoodPrice gp = (GoodPrice) session().
				  load(GoodPrice.class, i.get(g));
				session().setReadOnly(gp, true);

				//~: firm prices
				FirmPrices fp = EX.assertn(
				  p2fp.get(gp.getPriceList().getPrimaryKey()),

				  "Price List [", gp.getPriceList(),
				  "] is not associated with Contractor [",
				  target(Contractor.class).getPrimaryKey(), "]!"
				);

				//=: firm price
				pc.setFirmPrices(fp);

				//=: contractor
				pc.setContractor(target(Contractor.class));

				//=: good price
				pc.setGoodPrice(gp);

				//=: good unit
				pc.setGoodUnit(gp.getGoodUnit());

				//=: primary key
				HiberPoint.setPrimaryKey(session(), pc,
				  HiberPoint.isTestInstance(pc.getGoodUnit())
				);

				//!: save the cross
				session().save(pc);

				//?: {has changes to save}
				if(changes != null)
				{
					//~: lookup from delete entry
					FirmGoodPrice fgp = fgpm.get(pc.getGoodUnit().getPrimaryKey());
					if(fgp == null) //<-- ?: {not found it}
					{
						changes.add(fgp = new FirmGoodPrice());
						fgpm.put(pc.getGoodUnit().getPrimaryKey(), fgp);

						//=: contractor
						fgp.setContractor(target(Contractor.class).getPrimaryKey());

						//=: good
						fgp.setGood(pc.getGoodUnit().getPrimaryKey());
					}

					//=: new price list
					fgp.setNewList(pc.getGoodPrice().getPriceList().getPrimaryKey());

					//~: new price
					fgp.setNewPrice(pc.getGoodPrice().getPrice());
				}
			}

			//!: flush the session (to be sure)
			HiberPoint.flush(session());
		}


		/* protected: task parameters */

		protected final List<PriceListEntity> lists;
		protected final List<FirmGoodPrice>   changes;


		/* protected: task state */

		protected List<FirmPrices>      prices;
		protected List<FirmPrices>      current;
		protected List<Object[]>        crosses;
		protected Set<FirmPrices>       deleted;
		protected Set<FirmPrices>       added;
		protected Map<Long, PriceCross> cache;
	}
}