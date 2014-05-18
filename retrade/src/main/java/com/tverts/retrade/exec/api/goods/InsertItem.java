package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: endure (trees) */

import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: retrade api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.TreeItem;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Saves new Goods Tree Item from the given API object.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertItem extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof TreeItem) &&
		  Goods.TYPE_GOOD_UNIT.equals(holder.getTypeName());
	}

	protected Long    insert(Object source)
	{
		TreeItem   i = (TreeItem) source;

		//~: load the goods tree domain
		TreeDomain t = bean(GetTree.class).
		  getDomain(domain().getPrimaryKey(), Goods.TYPE_GOODS_TREE);

		//~: load the folder to insert
		TreeFolder f = bean(GetTree.class).getFolder(EX.assertn(
		  i.getParent(), "Good x-key [", i.getXkey(),
		  "] has no folder primary key, x-key is [", i.getXParent(), "]!"
		));

		EX.assertn(f, "Good x-key [", i.getXkey(),
		  "] refers unknown goods folder p-key [", i.getParent(),
		  "], x-key is [", i.getXParent(), "]!"
		);

		//sec: check the folder tree domain
		EX.assertx(f.getDomain().equals(t));

		//~: load the good unit
		GoodUnit g = bean(GetGoods.class).getGoodUnit(EX.assertn(
		  i.getGood(), "Tree item with by x-key [", i.getXkey(), "] has no good p-key!"
		));

		EX.assertn(g, "Good by x-key [", i.getXkey(), "] and p-key [",
		  i.getGood(), "] was not found!"
		);


		//!: do add single item
		com.tverts.endure.tree.TreeItem r = ActionsPoint.actionResult(
		  com.tverts.endure.tree.TreeItem.class, ActionsPoint.actionRun(
		  ActTreeFolder.ADD, f,
		  ActTreeFolder.PARAM_ITEM, g,
		  ActTreeFolder.PARAM_SINGLE, true
		));


		//~: add the derived goods
		insertDerived(f, g);

		return r.getPrimaryKey();
	}


	/* protected: support */

	@SuppressWarnings("unchecked")
	protected void    insertDerived(TreeFolder f, GoodUnit g)
	{
		//~: get the derived goods
		List<GoodUnit> ds = bean(GetGoods.class).
		  getDerivedGoods(g.getPrimaryKey());

		for(GoodUnit d : ds)
		{
			//?: {it is already in the tree} skip
			List tis = bean(GetTree.class).getTreeItems(f.getDomain(), d.getPrimaryKey());
			if(!tis.isEmpty()) continue;

			//~: add it to the same folder
			ActionsPoint.actionRun(ActTreeFolder.ADD, f, ActTreeFolder.PARAM_ITEM, d);
		}
	}
}