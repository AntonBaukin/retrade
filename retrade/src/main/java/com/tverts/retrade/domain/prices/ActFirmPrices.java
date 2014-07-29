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
	public static final String LISTS      =
	  ActFirmPrices.class.getName() + ": price-lists";


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
		  task(abr), (List<PriceListEntity>)lists));

		complete(abr);
	}

	protected static class AssignPriceLists extends ActionWithTxBase
	{
		/* public: constructor */

		public AssignPriceLists(ActionTask task, List<PriceListEntity> lists)
		{
			super(task);
			this.lists = EX.assertn(lists);
		}

		/* public: Action interface */

		public List<FirmPrices> getResult()
		{
			return this.prices;
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			//~: load existing items
			current = bean(GetPrices.class).
			  loadPrices(target(Contractor.class).getPrimaryKey());

			//~: map existing by the price lists
			Map<Long, FirmPrices> p2fp = new HashMap<Long, FirmPrices>(current.size());
			for(FirmPrices fp : current)
				EX.assertx(p2fp.put(fp.getPriceList().getPrimaryKey(), fp) == null);

			//HINT: Firm Prices objects are never reused by linking
			//  else Price List. Priority may be updated.

			//~: the resulting list
			prices = new ArrayList<FirmPrices>(lists.size());

			//~: deleted items
			deleted = new HashSet<FirmPrices>(7);

			//~: added items
			added = new HashSet<FirmPrices>(7);

			//c: process the new lists
			Map<Integer, FirmPrices> imap =
			  new HashMap<Integer, FirmPrices>(lists.size());
			for(int i = 0;(i < lists.size());i++)
			{
				PriceListEntity p = EX.assertn(lists.get(i));
				FirmPrices      x = (i < current.size())?(current.get(i)):(null);
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

					//=: update the index
					y.setPriority(i);

					continue;
				}

				//?: {has item to delete}
				if(x != null) deleted.add(x);

				//~: create new item for this index
				added.add(y = new FirmPrices());

				//=: contractor
				y.setContractor(target(Contractor.class));

				//=: price list
				y.setPriceList(p);

				//=: index
				y.setPriority(i);
			}

			//[0]: unlink the price joins
			unlinkPrices();

			//[1]: remove the obsolete links
			deletePrices();

			//[2]: add the new links
			deletePrices();

			//[3]: reconsider prices
			relinkPrices();
		}

		protected void unlinkPrices()
		{}

		protected void deletePrices()
		{
			if(deleted.isEmpty()) return;

			for(FirmPrices fp : deleted)
				session().delete(fp);

			//!: flush the session
			HiberPoint.flush(session());
		}

		protected void savePrices()
		{
			if(added.isEmpty()) return;

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

		protected void relinkPrices()
		{}


		/* protected: task state */

		protected final List<PriceListEntity> lists;
		protected List<FirmPrices>            prices;
		protected List<FirmPrices>            current;
		protected Set<FirmPrices>             deleted;
		protected Set<FirmPrices>             added;
	}
}