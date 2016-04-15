package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: system (tx) */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.hibery.HiberPoint;
import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Builder for actions processing Good Calculations.
 *
 * @author anton.baukin@gmail.com
 */
public class ActGoodCalc extends ActionBuilderReTrade
{
	/* action types */

	/**
	 * When Good Calculation is saved, it's Good Unit
	 * is checked whether it has current calculation.
	 * If has, that calculation is closed with the time
	 * equal to the open time of the opened.
	 */
	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(!(targetOrNull(abr) instanceof GoodCalc))
			return;

		if(SAVE.equals(actionType(abr)))
			saveGoodCalc(abr);
	}


	/* protected: action methods */

	protected void saveGoodCalc(ActionBuildRec abr)
	{
		//?: {target is not a Good Calculation}
		checkTargetClass(abr, GoodCalc.class);

		GoodCalc calc = target(abr, GoodCalc.class);

		//?: {calculation has no parts}
		EX.asserte(calc.getParts(),
		  "Good Calculation [", calc.getPrimaryKey(),
		  "] is being saved has no parts!"
		);

		//~: save the calculation
		chain(abr).first(new SaveGoodCalcAction(task(abr)).
		  setOwner(calc.getGoodUnit())
		);

		complete(abr);
	}


	/* calculation save action */

	public static class SaveGoodCalcAction
	       extends      SaveNumericIdentified
	{
		public SaveGoodCalcAction(ActionTask task)
		{
			super(task);
		}

		public GoodCalc getSaveTarget()
		{
			return (GoodCalc) super.getSaveTarget();
		}

		protected void execute()
		  throws Throwable
		{
			GoodCalc gc = getSaveTarget();
			Calc      c = gc.getOx();
			GoodUnit gu = gc.getGoodUnit();
			GoodCalc xc = gu.getGoodCalc();

			//~: check there is a calculation cycle
			checkCalculationsCycle(gc);

			//?: {has no open time} set it now
			if(c.getTime() == null)
				c.setTime(new java.util.Date());

			//~: assign primary keys for the parts
			for(CalcPart p : gc.getParts())
				HiberPoint.setPrimaryKey(tx(), p,
				  HiberPoint.isTestInstance(gu));

			//?: {has no current calculation}
			if(xc == null)
			{
				if(c.getCloseTime() == null)
					xc = gc; //<-- set if current is not closed
			}
			//~: process current calculation
			else
			{
				//?: {calculation saved is before the current one}
				if(c.getTime().before(xc.getOpenTime()))
				{
					//!: calculation saved must be closed
					EX.assertx( c.getCloseTime() != null,
					  "Good Unit [", gu.getPrimaryKey(),
					  "] Calculation at [", DU.datetime2str(c.getTime()),
					  "] is not the last Calculation, but is not closed!"
					);
				}
				//~: close the current one
				else
				{
					if(xc.getCloseTime() == null)
					{
						//~: assign close time
						xc.getOx().setCloseTime(c.getTime());

						//!: update ox-calc
						xc.updateOx();

						//~: update the tx-number of the current-obsolete calculation
						TxPoint.txn(tx(), xc);
					}

					//~: assign current calculation | unlink
					if(c.getCloseTime() == null)
						xc = gc;
					else
						xc = null;
				}
			}

			//!: update ox-calc
			gc.updateOx();

			//~: do save
			super.execute();

			//?: {current calculation changed} update the good
			if(!CMP.eq(gu.getGoodCalc(), xc))
			{
				gu.setGoodCalc(xc);

				//~: update the tx-number of the good
				TxPoint.txn(tx(), gu);
			}
		}
	}


	/* error of cyclic calculations */

	public static class CycliCalcError extends RuntimeException
	{
		/* public: constructor */

		public CycliCalcError(Collection<GoodUnit> cycle)
		{
			this(cycle.toArray(new GoodUnit[cycle.size()]));
		}

		public CycliCalcError(GoodUnit[] cycle)
		{
			this.cycle = cycle;
		}

		/* public: */

		/**
		 * Returns the Good Units having Calculations
		 * in cyclic dependency. The first good is the
		 * checked one, the last good refers Calculation
		 * having part pointing to the first good.
		 *
		 * If a good is looped, cycle contains only it.
		 */
		public GoodUnit[] getCycle()
		{
			return cycle;
		}

		private GoodUnit[] cycle;
	}

	public static void  checkCalculationsCycle(GoodCalc calc)
	  throws CycliCalcError
	{
		List<GoodCalc> line  = new ArrayList<GoodCalc>(4);
		line.add(calc);

		Set<GoodUnit>  goods = new LinkedHashSet<GoodUnit>(17);
		goods.add(EX.assertn( calc.getGoodUnit(),
		  "Good Calculation must refer Good Unit"
		));

		checkCalculationsCycle(line, goods);
	}

	private static void checkCalculationsCycle
	  (List<GoodCalc> line, Set<GoodUnit> goods)
	{
		GoodCalc c = line.get(line.size() - 1);

		//c: for all the parts of the calculation
		for(CalcPart p : c.getParts())
		{
			GoodUnit g = p.getGoodUnit();

			//?: {part good is in the line goods}
			if(goods.contains(g))
				throw new CycliCalcError(goods);

			//?: {the good has calculation}
			if(g.getGoodCalc() != null)
			{
				line.add(g.getGoodCalc());
				goods.add(g);

				//~: check that calculation
				checkCalculationsCycle(line, goods);

				line.remove(line.size() - 1);
				goods.remove(g);
			}
		}
	}
}