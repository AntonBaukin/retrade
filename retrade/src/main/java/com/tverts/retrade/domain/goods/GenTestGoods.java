package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.CalcItem;
import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (prices) */

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Generates the test {@link MeasureUnit},
 * and {@link GoodUnit}s by reading file
 * 'GenTestGoods.xml'.
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
	}


	/* protected: generation */

	protected URL  getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestGoods.xml"),
		  "No GenTestGoods.xml file found!"
		);
	}

	protected void readTestData(GenCtx ctx)
	  throws GenesisError
	{
		try
		{
			createProcessor(ctx).
			  process(getDataFile().toString());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}
	}

	protected void genMeasure(GenCtx ctx, Measure m)
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
				m.setClassUnit(BigDecimal.ZERO.setScale(1));
			else
				m.setClassUnit(BigDecimal.ZERO);

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

	protected void genGood(GenCtx ctx, Good g, Calc c)
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
			addGood(ctx, gu);
			return;
		}

		gu = new GoodUnit();

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

		return;
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


	/* protected: XML Processor */

	protected SaxProcessor<? extends GenState> createProcessor(GenCtx ctx)
	{
		return new ReadTestGoods(ctx);
	}

	protected static class GenState
	{
		public Measure measure;
		public Good    good;
		public Calc    calc;
	}

	protected class ReadTestGoods extends SaxProcessor<GenState>
	{
		public ReadTestGoods(GenCtx ctx)
		{
			this.ctx = ctx;
			this.collectTags = true;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: <measure>
			if(istag(1, "measure"))
			{
				event().state(new GenState());
				state().measure = new Measure();
			}

			//?: <good>
			else if(istag(1, "good"))
			{
				event().state(new GenState());
				state().good = new Good();
			}

			//?: <good> <calc>
			else if(istag(2, "calc"))
			{
				EX.assertn(state(1).good);
				EX.assertx(state(1).calc == null);
				state(1).calc = new Calc();
			}

			else if(islevel(1))
				throw wrong();
		}

		protected void open()
		{
			//?: <good> <calc> <good/>
			if(istag(3, "good"))
			{
				Calc     c = EX.assertn(state(1).calc);
				CalcItem i = new CalcItem();

				//~: related good code
				i.setXGood(EX.asserts(event().attr("code")));

				//~: good volume
				i.setVolume(new BigDecimal(EX.asserts(event().attr("volume"))));

				//?: {already has this good}
				for(CalcItem x : c.getItems())
					if(x.getXGood().equals(i.getXGood()))
						throw EX.ass("Generated Good with code [", tag("code"),
						  "] already has Calculation part for good with code [",
						  i.getXGood(), "]!"
						);

				//~: add the part
				c.getItems().add(i);
			}

			//?: <good> <calc>
			else if(istag(2, "calc"))
				EX.assertn(state(1).calc).setSemiReady(
				  "true".equals(event().attr("semi-ready")));
		}

		protected void close()
		{
			//?: <measure>
			if(istag(1, "measure"))
			{
				//~: fill the measure
				EX.assertn(state().measure);
				requireFillClearTags(state().measure, true, "code", "name");

				//~: generate it
				genMeasure(ctx, state().measure);
				state().measure = null;
			}

			//?: <good> <calc>
			else if(istag(2, "calc"))
			{
				//~: fill the calculation
				Calc c = EX.assertn(state(1).calc);
				requireFillClearTags(c, false);
			}

			//?: <good>
			else if(istag(1, "good"))
			{
				//~: fill the good
				Good g = EX.assertn(state().good);
				requireFillClearTags(g, true, "code", "name", "XMeasure");

				//!: generate the good
				genGood(ctx, g, state().calc);
			}
		}


		/* genesis context */

		private GenCtx ctx;
	}
}