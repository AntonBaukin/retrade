package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.flush;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;
import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.tree.TreeNodeView;

/* com.tverts: retrade domain (core + goods) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Updates Price Lists as a Tree.
 * The implementation is the same as for Tree Nodes.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActPriceListsTreeUpdate extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType UPDATE =
	  new ActionType("update", PriceList.class);


	/* parameters of the action task */

	/**
	 * This parameter is a collection of
	 * {@link TreeNodeView} instances.
	 */
	public static final String LISTS =
	  ActPriceListsTreeUpdate.class.getName() + ": price lists";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(UPDATE.equals(actionType(abr)))
			updatePriceListsTree(abr);
	}


	/* protected: action methods */

	@SuppressWarnings("unchecked")
	protected void updatePriceListsTree(ActionBuildRec abr)
	{
		//?: {target is not a Domain}
		checkTargetClass(abr, Domain.class);

		//~: get the tree node views
		Collection lists = EX.assertn( param(abr, LISTS, Collection.class),
		  "Price Lists (Tree Node Views) to update collection is empty!");

		for(Object l : lists) EX.assertx(l instanceof TreeNodeView);

		//~: add update action
		chain(abr).first(new UpdatePriceListsTreeAction(
		  task(abr), (Collection<TreeNodeView>)lists));

		complete(abr);
	}


	/* action to update tree lists */

	protected static class UpdatePriceListsTreeAction
	          extends      ActionWithTxBase
	{
		/* constructor */

		public UpdatePriceListsTreeAction(ActionTask task, Collection<TreeNodeView> nodes)
		{
			super(task);
			this.nodes = nodes;
		}


		/* public: action interface */

		public List<PriceList> getResult()
		{
			return result;
		}


		/* protected: ActionBase interface */

		@SuppressWarnings("unchecked")
		protected void   execute()
		  throws Throwable
		{
			Domain domain = target(Domain.class);

			//~: load all the lists of tree domain
			List<PriceList> lists = bean(GetGoods.class).
			  getPriceLists(domain.getPrimaryKey());

			//~: map them by the keys (as strings)
			HashMap<String, PriceList> lkeys =
			  new HashMap<String, PriceList>(lists.size());
			for(PriceList l : lists)
				lkeys.put(l.getPrimaryKey().toString(), l);

			//~: map them by the codes
			HashMap<String, PriceList> lcodes =
			  new HashMap<String, PriceList>(lists.size());
			for(PriceList l : lists) lcodes.put(l.getCode(), l);

			//~: flush session updates
			flush(session());

			//0: set temporary codes for all the updated lists (with codes changed)
			for(TreeNodeView n : nodes)
			{
				//~: skip new lists
				if(n.getObjectKey().startsWith("$"))
					continue;

				PriceList l = EX.assertn(lkeys.get(n.getObjectKey()),
				  "Price List [", n.getObjectKey(), "] not found!"
				);

				//?: {code is changed}
				if(!l.getCode().equals(n.getCode()))
				{
					lcodes.remove(l.getCode());
					l.setCode(genTempCode());
				}
			}

			//~: flush temp codes to db
			flush(session());

			//1: reset codes back
			for(TreeNodeView n : nodes)
			{
				//~: skip new lists
				if(n.getObjectKey().startsWith("$"))
					continue;

				PriceList l = EX.assertn(lkeys.get(n.getObjectKey()),
				  "Price List [", n.getObjectKey(), "] not found!"
				);

				//?: {the code is not changed} do nothing
				if(l == lcodes.get(n.getCode()))
					continue;

				//?: {this code was written}
				EX.assertx( !lcodes.containsKey(n.getCode()),
				  "Duplicated Price List code [", n.getCode(), "]!"
				);

				l.setCode(n.getCode());
				lcodes.put(n.getCode(), l);

				//~: update transaction number
				TxPoint.txn(tx(), l);
			}

			//~: flush new codes to db (without parents, for now)
			flush(session());

			//2: insert new lists
			for(TreeNodeView n : nodes)
			{
				//~: skip existing lists
				if(!n.getObjectKey().startsWith("$"))
					continue;

				//?: {the code already exists}
				EX.assertx( !lcodes.containsKey(n.getCode()),
				  "Duplicated Price List code [", n.getCode(), "]!"
				);

				//~: create new price list
				PriceList l = new PriceList();

				//~: primary key
				setPrimaryKey(session(), l, isTestInstance(domain));

				//~: domain
				l.setDomain(domain);

				//~: code
				l.setCode(n.getCode());

				//~: name
				l.setName(n.getName());

				//!: save it (without checking cyclic dependencies)
				actionRun(ActionType.SAVE, l, ActPriceList.NOCYDEPS, true);

				lkeys.put(l.getPrimaryKey().toString(), l);
				lkeys.put(n.getObjectKey(), l); //<-- also it's $-key
				lcodes.put(l.getCode(), l);
				lists.add(l);
			}

			//~: flush new lists to db
			flush(session());


			//3: process the parents
			for(TreeNodeView n : nodes)
			{
				PriceList l = lkeys.get(n.getObjectKey());

				if(n.getParentKey() == null)
					l.setParent(null);
				else
				{
					PriceList p = l.getParent();

					//~: lookup by the key
					l.setParent(lkeys.get(n.getParentKey()));

					//?: {it not found}
					EX.assertn(l.getParent(),
					  "Parent Price List [", n.getParentKey(), "] not found!");
				}

				//~: update transaction number
				TxPoint.txn(tx(), l);
			}

			//4: inspect cyclic dependencies
			for(PriceList l : lists)
			{
				HashSet<Long> ancs = new HashSet<Long>(5);

				while(l != null)
				{
					EX.assertx( !ancs.contains(l.getPrimaryKey()),
					  "Cyclic dependency in the Price Lists Tree [", l.getPrimaryKey(), "]!"
					);

					ancs.add(l.getPrimaryKey());
					l = l.getParent();
				}
			}

			//5: update the names
			for(TreeNodeView n : nodes)
			{
				PriceList l = lkeys.get(n.getObjectKey());

				//~: name
				l.setName(n.getName());

				//~: update transaction number
				TxPoint.txn(tx(), l);
			}

			//~: flush the last updates
			flush(session());
		}

		private Random tempCodeGen;

		protected String genTempCode()
		  throws Exception
		{
			if(tempCodeGen == null)
				tempCodeGen = new Random();

			byte[] code = new byte[127];
			tempCodeGen.nextBytes(code);

			return new String(SU.bytes2hex(code));
		}


		/* the views parameter */

		private Collection<TreeNodeView> nodes;
		private List<PriceList>          result;
	}

}