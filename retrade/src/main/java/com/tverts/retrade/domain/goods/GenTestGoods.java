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
import com.tverts.genesis.GenUtils;
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

import com.tverts.support.CMP;
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

			//--> needed for sub-goods
			g.setPkey(gu.getPrimaryKey());

			return;
		}

		gu = new GoodUnit();

		//=: primary key
		HiberPoint.setPrimaryKey(ctx.session(), gu, true);
		g.setPkey(gu.getPrimaryKey());

		//=: domain
		gu.setDomain(ctx.get(Domain.class));

		//=: measure
		setMeasure(ctx, gu, g);

		//~: generate the fields
		genGoodFields(ctx, gu, g);

		//!: save the good
		gu.setOx(Goods.initOx(gu, g)); //<-- ox-update is there
		actionRun(ActionType.SAVE, gu);

		LU.I(log(ctx), logsig(), " generated good [",
		  gu.getPrimaryKey(), "] with code: [", gu.getCode(),
		  "], measure [", gu.getMeasure().getCode(), "]"
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

	public void takeSubGood(GenCtx ctx, Good g, String m, BigDecimal v)
	{
		//?: {parent good was not generated}
		EX.assertn(g.getPkey());

		//~: load parent good
		GoodUnit gu = EX.assertn(bean(GetGoods.class).
		  getGoodUnit(g.getPkey()));

		//?: {not an owner}
		EX.assertx(gu.getSuperGood() == null);

		//~: search for that sub-good
		GoodUnit sub = bean(GetGoods.class).
		  getSubGood(gu.getPrimaryKey(), m);

		//?: {had found it} do not update
		if(sub != null)
		{
			//~: add to the goods map
			addGood(ctx, HiberPoint.unproxyDeeply(ctx.session(), sub));
			return;
		}

		//~: create sub-good
		sub = new GoodUnit();

		//~: assign the measure
		setMeasure(ctx, sub, EX.asserts(m));

		//?: {has no coerce volume}
		if(v == null) v = EX.assertn(Goods.coerce(gu.getMeasure(), sub.getMeasure()),
		  "Can't auto-define measures coerce coefficient for measure [", m,
		  "] of sub-good [", gu.getCode(), "]!");

		//?: {illegal coefficient}
		EX.assertx(CMP.grZero(v), "Illegal sub-good coerce coefficient for measure [",
		  m, "] of sub-good [", gu.getCode(), "]!");

		//=: primary key
		HiberPoint.setPrimaryKey(ctx.session(), sub, true);

		//~: assign the sub-good
		Goods.copySub(gu, sub);
		initSubGoodsOwnOx(sub, g);

		//!: save it
		actionRun(ActionType.SAVE, sub);

		//~: initialize calculation
		Calc c = new Calc();
		c.setXSuperGood(gu.getCode());
		c.setSubCode(">" + m);
		c.setSubVolume(v);

		//!: create & save it
		genDerived(ctx, sub, c);

		//~: generate the fields
		genGoodFields(ctx, sub, sub.getOxOwn());

		//~: update own ox
		Goods.initOx(sub, sub.getOxOwn());
		sub.updateOxOwn();

		//!: update action
		actionRun(ActionType.UPDATE, sub);

		//~: add to the goods map
		addGood(ctx, sub);

		LU.I(log(ctx), logsig(), " generated sub-good [",
		  gu.getPrimaryKey(), "] with code: [", gu.getCode(),
		  "], measure [", gu.getMeasure().getCode(), "]"
		);
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

		if(mum == null) ctx.set(MeasureUnit.class, mum = new HashMap<>(7));

		EX.assertx( mum.put(mu.getCode(), mu) == null,
		  "Measure Unit with code [", mu.getCode(), "] is generated twice!"
		);
	}

	@SuppressWarnings("unchecked")
	protected void addGood(GenCtx ctx, GoodUnit gu)
	{
		Map<String, GoodUnit> gum = (Map<String, GoodUnit>)
		  ctx.get((Object) GoodUnit.class);

		if(gum == null) ctx.set(GoodUnit.class, gum = new HashMap<>());

		EX.assertx( gum.put(Goods.subCode(gu), gu) == null,
		  "Good Unit with sub-code [", Goods.subCode(gu), "] is generated twice!"
		);
	}

	protected void setMeasure(GenCtx ctx, GoodUnit gu, Good g)
	{
		//~: load the measure
		setMeasure(ctx, gu, EX.asserts(g.getXMeasure(),
		  "Good Unit with code [", g.getCode(), "] has no measure defined!"));

		//~: clear x-measure
		g.setXMeasure(null);
	}

	@SuppressWarnings("unchecked")
	protected void setMeasure(GenCtx ctx, GoodUnit gu, String m)
	{
		//~: existing measures map
		Map<String, MeasureUnit> mum = (Map<String, MeasureUnit>)
		  EX.assertn(ctx.get((Object) MeasureUnit.class));

		//~: find it
		MeasureUnit mu = EX.assertn(mum.get(EX.asserts(m)),
		  "Good refers unknown measure code [", m, "]!");

		//=: measure
		gu.setMeasure(mu);
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

		if(gu.getSuperGood() == null) LU.I(log(ctx), logsig(),
		  " created derived Good Calc [", gc.getPrimaryKey(),
		  "] for test Good Unit [", gu.getPrimaryKey(),
		  "], code [", gu.getCode(), "]"
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

	protected void initSubGoodsOwnOx(GoodUnit sub, Good g)
	{
		Good s = sub.getOxOwn();

		s.setVisibleBuy(g.isVisibleBuy());
		s.setVisibleSell(g.isVisibleSell());
		s.setVisibleLists(g.isVisibleLists());
		s.setVisibleReports(g.isVisibleReports());
	}

	protected void genGoodFields(GenCtx ctx, GoodUnit gu, Good g)
	{
		//~: attributes map
		Map<String, Object> ats = g.getAttrs();
		if(ats == null) g.setAttrs(ats = new HashMap<>());

		//~: bar codes (1 up to 3)
		if(ats.get(Goods.AT_BARCODE) == null)
		{
			String[] cs = new String[1 + ctx.gen().nextInt(3)];
			for(int i = 0;(i < cs.length);i++)
				cs[i] = GenUtils.number(ctx.gen(), 13);

			ats.put(Goods.AT_BARCODE, (cs.length == 1)?(cs[0]):(cs));
		}

		//~: net weight
		if(ats.get(Goods.AT_NET_WEIGHT) == null)
			if(ats.get(Goods.AT_GROSS_WEIGHT) != null)
				ats.put(Goods.AT_NET_WEIGHT, ats.get(Goods.AT_GROSS_WEIGHT));
			//?: {generate for ordinary good}
			else if(gu.getSuperGood() == null)
			{
				//?: {kg measure}
				if("166".equals(gu.getMeasure().getOx().getClassCode()))
				{
					BigDecimal cu = gu.getMeasure().getOx().getClassUnit();
					if(cu == null) cu = BigDecimal.ONE.setScale(2);

					ats.put(Goods.AT_NET_WEIGHT, cu);
				}
				//~: generate random weight about 1 kg
				else
					ats.put(Goods.AT_NET_WEIGHT, new BigDecimal(1.0 - 0.1 * ctx.gen().
					  nextInt(6)).setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			//~: generate for a sub-good
			else
			{
				Map<String, Object> pats = EX.assertn(gu.getSuperGood().getOx().getAttrs());

				BigDecimal nw = EX.assertn((BigDecimal) pats.get(Goods.AT_NET_WEIGHT));
				BigDecimal  x = EX.assertn(gu.getGoodCalc()).getOx().getSubVolume();

				ats.put(Goods.AT_NET_WEIGHT, nw.multiply(EX.assertn(x)).
				  setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

		//?: {gross weight is smaller}
		if(ats.get(Goods.AT_GROSS_WEIGHT) != null)
			EX.assertx(CMP.gre((BigDecimal) ats.get(Goods.AT_GROSS_WEIGHT),
			  (BigDecimal) ats.get(Goods.AT_NET_WEIGHT)));
		//~: slightly more than net weight
		else
		{
			BigDecimal nw = EX.assertn((BigDecimal) ats.get(Goods.AT_NET_WEIGHT));
			ats.put(Goods.AT_GROSS_WEIGHT, nw.add(new BigDecimal(0.1 * ctx.gen().nextInt(4))).
			  setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}
	}
}