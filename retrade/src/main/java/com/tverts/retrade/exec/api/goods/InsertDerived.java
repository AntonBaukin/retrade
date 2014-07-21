package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: hibery */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.Good;

/* com.tverts: retrade domain (tree + goods) */

import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeDomain;
import com.tverts.endure.tree.TreeItem;
import com.tverts.retrade.domain.goods.CalcPart;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.DerivedGood;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Creates {@link GoodUnit} instance having derived
 * Calculation based on the given API {@link DerivedGood}.
 *
 * @author anton.baukin@gmail.com.
 */
public class InsertDerived extends InsertGood
{
	protected boolean  isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof DerivedGood);
	}

	protected GoodUnit insert(Good g)
	{
		GoodUnit    gu = super.insert(g);
		DerivedGood dg = (DerivedGood)g;

		//<: create the derived calculation

		GoodCalc gc = new GoodCalc();
		Calc      c = gc.getOx();

		//~: good unit <-> calc
		gc.setGoodUnit(gu);

		//~: semi-ready
		c.setSemiReady(dg.isSemiReady());

		//~: open time: from the beginning
		c.setTime(new java.util.Date(0L));

		//~: super good unit
		gc.setSuperGood(loadSuperGood(dg));

		//~: sub-code
		c.setSubCode(EX.assertn(SU.s2s(dg.getSubCode())));

		//~: sub-volume
		EX.assertx(CMP.grZero(dg.getSubVolume()));
		c.setSubVolume(dg.getSubVolume());

		//~: calculation part
		CalcPart p = new CalcPart();

		//~: part <-> calc
		p.setGoodCalc(gc);
		gc.getParts().add(p);

		//~: part good (super good)
		p.setGoodUnit(gc.getSuperGood());

		//~: volume
		p.setVolume(c.getSubVolume());

		//>: create the derived calculation

		//!: save it
		gc.updateOx();
		ActionsPoint.actionRun(ActionType.SAVE, gc);

		//~: good unit <-> calc
		gu.setGoodCalc(gc);

		//~: add good to the goods tree
		addToGoodsTree(gu);

		return gu;
	}


	/* protected: support */

	protected GoodUnit loadSuperGood(DerivedGood dg)
	{
		//?: {has no good}
		EX.assertn(dg.getSuperGood(), "Derived Good with xkey [",
		  dg.getXkey(), "] has no super-good reference!");

		GoodUnit gu = bean(GetGoods.class).getGoodUnit(dg.getSuperGood());

		//?: {not exists}
		EX.assertn(gu, "Super-good with p-key [", dg.getSubCode(),
		  "] refered by derived good x-key [", dg.getXkey(),
		  "] doesn't exist!"
		);

		//sec: check the domain
		checkDomain(gu);

		return gu;
	}

	protected void     addToGoodsTree(GoodUnit gu)
	{
		//~: get the super-good
		GoodUnit       sg = EX.assertn(gu.getGoodCalc().getGoodUnit());

		//~: goods tree domain
		TreeDomain     td = bean(GetTree.class).getDomain(
		  gu.getDomain().getPrimaryKey(), Goods.TYPE_GOODS_TREE
		);

		//~: the list of the items of the super good
		List<TreeItem> is = bean(GetTree.class).
		  getTreeItems(td, sg.getPrimaryKey());

		//!: add to all the folder
		for(TreeItem i : is)
			ActionsPoint.actionRun(
			  ActTreeFolder.ADD, i.getFolder(),
			  ActTreeFolder.PARAM_ITEM, gu
			);
	}
}