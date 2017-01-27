package com.tverts.retrade.exec.api.goods;

/* standard Java classes */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: hibery */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.CalcItem;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.CalcPart;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: execution (api) */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Creates {@link GoodCalc} entity based on
 * the given API {@link Calc}.
 *
 * Note that Calculations updating is not supported
 * as the calculations are immutable after saving.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InsertCalc extends InsertEntityBase
{
	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Calc);
	}

	protected Long    insert(Object source)
	{
		Calc     c  = (Calc) source;
		GoodCalc gc = new GoodCalc();
		GetGoods g  = bean(GetGoods.class);

		//~: good unit must be specified
		EX.assertn( c.getGood(), "Good Calc x-key [", c.getXkey(),
		  "] has no Good Unit p-key specified!"
		);

		//~: load the good unit
		GoodUnit gu = EX.assertn(g.getGoodUnit(c.getGood()),

		  "Good Calc x-key [", c.getXkey(),
		  "] refers Good Unit p-key [",
		  c.getGood(), "] not found!"
		);

		//sec: check the domain
		checkDomain(gu);

		//~: set good unit
		gc.setGoodUnit(gu);

		//~: load existing calculations (ordered by the open time)
		List<GoodCalc> cs = g.getGoodCalcs(gu.getPrimaryKey());

		//~: check the time is not the same
		EX.assertn(c.getTime());
		for(GoodCalc x : cs)
			EX.assertx(!CMP.eq(c.getTime(), x.getOpenTime()));

		//!: assign calc ox
		gc.setOx(c);

		//~: find the calculation on the left
		GoodCalc l = null;
		for(int i = cs.size() - 1;(i >= 0);i++)
			if(cs.get(i).getOpenTime().before(c.getTime()))
			{
				l = cs.get(i);
				break;
			}

		//~: find the calculation on the right
		GoodCalc r = null;
		for(GoodCalc x : cs)
			if(x.getOpenTime().after(c.getTime()))
			{
				r = x;
				break;
			}

		//?: {has calculation on the left} update it's close time
		if(l != null) if((l.getCloseTime() == null) || l.getCloseTime().after(c.getTime()))
		{
			//=: close time
			l.getOx().setCloseTime(c.getTime());

			//~: update ox
			l.updateOx();

			//~: update tx-numbers
			TxPoint.txn(tx(), l);
		}


		//~: create the calculation parts
		for(CalcItem i : c.getItems())
		{
			CalcPart p = new CalcPart();

			//~: calc <-> part
			p.setGoodCalc(gc);
			gc.getParts().add(p);

			//~: load the good unit
			EX.assertn(c.getGood(), "Good Calc x-key [", c.getXkey(),
			  "] part for Good Unit x-key [", i.getXGood(), "] has no good p-key!"
			);

			GoodUnit gu2 = EX.assertn( g.getGoodUnit(i.getGood()),
			  "Good Calc x-key [", c.getXkey(), "] refers Good Unit p-key [",
			  i.getGood(), "] not found!"
			);

			//~: set the good
			p.setGoodUnit(gu2);

			//~: volume
			EX.assertn(CMP.grZero(i.getVolume()));
			p.setVolume(i.getVolume());
		}

		//HINT: calculation parts are not stored
		//  in the Calculation XML document.

		//~: clear the parts
		c.setItems(null);

		//?: {has right calc}
		if(r != null) //<-- set right' open time as the close time
			c.setCloseTime(EX.assertn(r.getOpenTime()));


		//!: save the calculation
		gc.updateOx();
		ActionsPoint.actionRun(ActionType.SAVE, gc);


		//?: {there is no right calculation}
		if(r == null) //<-- make new be the current
		{
			//~: assign current calculation
			EX.assertx(c.getCloseTime() == null);
			gu.setGoodCalc(gc);

			//~: update the transaction number
			TxPoint.txn(tx(), gu);
		}

		return gc.getPrimaryKey();
	}
}