package com.tverts.retrade.domain.goods;

/* Java */

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: scripting */

import com.tverts.jsx.JsX;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.CalcItem;
import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Generates the test {@link MeasureUnit},
 * and {@link GoodUnit}s by reading file
 * 'GenTestGoods.js'.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestGoods extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: read test data
		readTestData(ctx);

		//~: collect the goods
		collectGoods(ctx);
	}


	/* public: JS generation callbacks */

	public void takeMeasure(GenCtx ctx, Measure m)
	{
		//~: load existing measure unit
		MeasureUnit mu = bean(GetGoods.class).getMeasureUnit(
		  ctx.get(Domain.class).getPrimaryKey(),
		  EX.asserts(m.getCode())
		);

		//HINT: we allow user-defined modifications for the measures.

		//?: {had found it} do not update
		if(mu != null)
		{
			//~: add to the measures map
			addMeasure(ctx, mu);
			return;
		}

		//?: {measure has class-code}
		if(!SU.sXe(m.getClassCode()) && (m.getClassUnit() == null))
			if(m.isFractional())
				m.setClassUnit(BigDecimal.ONE.setScale(1));
			else
				m.setClassUnit(BigDecimal.ONE);

		//~: create measure with ox
		mu = new MeasureUnit();
		mu.setOx(m); //<-- ox-update is there

		//=: domain
		mu.setDomain(ctx.get(Domain.class));

		//!: save the measure
		actionRun(ActionType.SAVE, mu);

		LU.I(log(ctx), logsig(), " created test Measure Unit [",
		  mu.getPrimaryKey(), "] with code: ", mu.getCode()
		);

		//~: add to the measures map
		addMeasure(ctx, mu);
	}

	public void takeGood(GenCtx ctx, Good g, Calc c)
	{
		//~: load existing good unit
		GoodUnit gu = bean(GetGoods.class).getGoodUnit(
		  ctx.get(Domain.class).getPrimaryKey(),
		  EX.asserts(g.getCode())
		);

		//HINT: we allow user-defined modifications for the goods.

		//?: {had found it} do not update
		if(gu != null)
		{
			//~: add to the goods map
			addGood(ctx, HiberPoint.unproxyDeeply(ctx.session(), gu));
			return;
		}

		gu = new GoodUnit();

		//=: primary key
		HiberPoint.setPrimaryKey(ctx.session(), gu, true);

		//=: domain
		gu.setDomain(ctx.get(Domain.class));

		//=: measure
		setMeasure(ctx, gu, g);

		//!: save the good
		gu.setOx(g); //<-- ox-update is there
		actionRun(ActionType.SAVE, gu);

		LU.I(log(ctx), logsig(), " created test Good Unit [",
		  gu.getPrimaryKey(), "] with code: ", gu.getCode()
		);

		//~: add to the goods map
		addGood(ctx, gu);

		//?: {good has calculation} generate it
		if(c != null)
			//?: {derived good}
			if(c.getXSuperGood() != null)
				genDerived(ctx, gu, c);
			else
				genCalculation(ctx, gu, c);
	}


	/* protected: generation */

	protected void readTestData(GenCtx ctx)
	  throws GenesisError
	{
		//~: invoke GenTestMeasures.js
		JsX.apply("domain/goods/GenTestMeasures.js",
		  "genTestMeasures", ctx, this
		);

		//~: invoke GenTestGoods.js
		JsX.apply("domain/goods/GenTestGoods.js",
		  "genTestGoods", ctx, this
		);
	}

	@SuppressWarnings("unchecked")
	protected void collectGoods(GenCtx ctx)
	{
		//~: get the goods
		Map<String, GoodUnit> gum = (Map<String, GoodUnit>)
		  EX.assertn(ctx.get((Object) GoodUnit.class));

		EX.assertx(0 != gum.size(), "Test Goods were not generated!");

		//~: the code-sorted
		Map<String, GoodUnit> gtr = new TreeMap<>(gum);

		//!: save into the genesis context
		ctx.set(GoodUnit[].class, gtr.values().stream().
		  toArray(GoodUnit[]::new));
	}

	@SuppressWarnings("unchecked")
	protected void addMeasure(GenCtx ctx, MeasureUnit mu)
	{
		Map<String, MeasureUnit> mum = (Map<String, MeasureUnit>)
		  ctx.get((Object) MeasureUnit.class);

		if(mum == null) ctx.set((Object) MeasureUnit.class,
		  mum = new HashMap<String, MeasureUnit>(7)
		);

		EX.assertx( mum.put(mu.getCode(), mu) == null,
		  "Measure Unit with code [", mu.getCode(), "] is generated twice!"
		);
	}

	@SuppressWarnings("unchecked")
	protected void addGood(GenCtx ctx, GoodUnit gu)
	{
		Map<String, GoodUnit> gum = (Map<String, GoodUnit>)
		  ctx.get((Object) GoodUnit.class);

		if(gum == null) ctx.set((Object) GoodUnit.class,
		  gum = new HashMap<String, GoodUnit>(7)
		);

		EX.assertx( gum.put(gu.getCode(), gu) == null,
		  "Good Unit with code [", gu.getCode(), "] is generated twice!"
		);
	}

	@SuppressWarnings("unchecked")
	protected void setMeasure(GenCtx ctx, GoodUnit gu, Good g)
	{
		Map<String, MeasureUnit> mum = (Map<String, MeasureUnit>)
		  EX.assertn(ctx.get((Object) MeasureUnit.class));

		MeasureUnit mu = mum.get(EX.asserts(
		  g.getXMeasure(), "Good Unit with code [", g.getCode(),
		  "] has no <XMeasure> tag defined!"
		));

		//=: measure
		gu.setMeasure(EX.assertn(mu, "Good Unit with code [", g.getCode(),
		  "] refers unknown measure by code [", g.getXMeasure(), "]!"
		));

		//~: clear x-measure
		g.setXMeasure(null);
	}

	protected void genDerived(GenCtx ctx, GoodUnit gu, Calc c)
	{
		GoodCalc gc = new GoodCalc();

		//=: destination good
		gc.setGoodUnit(gu);

		//=: search the super-good
		setSuperGood(ctx, gc, c);

		//=: open time
		c.setTime(EX.assertn(ctx.get(Date.class)));

		//?: {has no sub-code}
		EX.asserts(c.getSubCode(), "Good with code [", gu.getCode(),
		  "] derived Calculation has no <sub-code> tag value!"
		);

		//?: {has no sub-volume}
		EX.assertn(c.getSubVolume(), "Good with code [", gu.getCode(),
		  "] derived Calculation has no <sub-volume> tag value!"
		);

		//?: {has items}
		EX.assertx(c.getItems().isEmpty(), "Good with code [", gu.getCode(),
		  "] Calculation has items, but it is derived!"
		);

		//~: create single calc part
		CalcPart cp = new CalcPart();

		//=: calc <-> part
		gc.getParts().add(cp);
		cp.setGoodCalc(gc);

		//=: part good
		cp.setGoodUnit(gc.getSuperGood());

		//~: part volume
		cp.setVolume(c.getSubVolume());

		//!: assign ox-calc
		gc.setOx(c);

		//!: save the calc
		actionRun(ActionType.SAVE, gc);

		LU.I(log(ctx), logsig(), " created derived Good Calc [",
		  gc.getPrimaryKey(), "] for test Good Unit [",
		  gu.getPrimaryKey(), "], code [", gu.getCode(), "]"
		);
	}

	@SuppressWarnings("unchecked")
	protected void setSuperGood(GenCtx ctx, GoodCalc gc, Calc c)
	{
		Map<String, GoodUnit> gum = (Map<String, GoodUnit>)
		  EX.assertn(ctx.get((Object) GoodUnit.class));

		GoodUnit xu = gum.get(EX.asserts(
		  c.getXSuperGood(), "Good Unit with code [", gc.getGoodUnit().getCode(),
		  "] has derived Calculation with no <xsuper-good> tag defined!"
		));

		//=: super good
		gc.setSuperGood(EX.assertn(xu, "Good Unit with code [",
		  gc.getGoodUnit().getCode(), "] has derived Calculation that refers ",
		  "unknown Good by code [", c.getXSuperGood(), "]!"
		));

		//~: clear super good
		c.setXSuperGood(null);
	}

	protected void genCalculation(GenCtx ctx, GoodUnit gu, Calc c)
	{
		GoodCalc gc = new GoodCalc();

		//=: destination good
		gc.setGoodUnit(gu);

		//=: open time
		c.setTime(EX.assertn(ctx.get(Date.class)));

		//?: {is false derived}
		EX.assertx(c.getXSuperGood() == null);
		EX.assertx(c.getSubCode()    == null);
		EX.assertx(c.getSubVolume()  == null);

		//?: {has no items}
		EX.asserte(c.getItems(), "Good with code [", gu.getCode(),
		  "] Calculation has no items, and it is not derived either!"
		);

		//c: for all parts
		for(CalcItem ci : c.getItems())
		{
			CalcPart cp = new CalcPart();

			//=: calc <-> part
			gc.getParts().add(cp);
			cp.setGoodCalc(gc);

			//=: good unit
			setPartGood(ctx, cp, ci);

			//=: volume
			cp.setVolume(EX.assertn(ci.getVolume()));
			EX.assertx(ci.getVolume().scale() <= 8);

			//?: {measure of referred good is integer}
			if(!cp.getGoodUnit().getMeasure().getOx().isFractional())
				EX.assertx(ci.getVolume().scale() == 0);
		}

		//~: clear the items in the ox
		c.setItems(null);

		//!: assign ox-calc
		gc.setOx(c);

		//!: save the calc
		actionRun(ActionType.SAVE, gc);

		LU.I(log(ctx), logsig(), " created Good Calc [",
		  gc.getPrimaryKey(), "] for test Good Unit [",
		  gu.getPrimaryKey(), "], code [", gu.getCode(),
		  "] having ", gc.getParts().size(), " parts"
		);
	}

	@SuppressWarnings("unchecked")
	protected void setPartGood(GenCtx ctx, CalcPart cp, CalcItem ci)
	{
		Map<String, GoodUnit> gum = (Map<String, GoodUnit>)
		  EX.assertn(ctx.get((Object) GoodUnit.class));

		GoodUnit xu = gum.get(EX.asserts(
		  ci.getXGood(), "Good Unit with code [",
		  cp.getGoodCalc().getGoodUnit().getCode(),
		  "] has Calculation with no code attribute defined for <good> tag!"
		));

		//=: good
		cp.setGoodUnit(EX.assertn(xu, "Good Unit with code [",
		  cp.getGoodCalc().getGoodUnit().getCode(),
		  "] has Calculation that refers unknown Good by code [",
		  ci.getXGood(), "]!"
		));
	}
}