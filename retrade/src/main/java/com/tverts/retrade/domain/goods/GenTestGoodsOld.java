package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.actions.ActionsPoint.actionResult;
import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;
import static com.tverts.hibery.HiberPoint.unproxyDeeply;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.ActPriceList;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceList;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import static com.tverts.support.SU.sXe;
import static com.tverts.support.SU.s2s;
import com.tverts.support.xml.SaxProcessor;


/**
 * Generates the test {@link GoodUnit}s,
 * {@link MeasureUnit}s, and {@link PriceList}s
 * with {@link GoodPrice}s.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestGoodsOld extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public static final String CTX_GOODS_MAP =
	  GenTestGoodsOld.class.getName() + ": code -> good";

	public void         generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: create test PriceLists
		//createTestPriceLists(ctx);

		//~: good units map
		ctx.set(CTX_GOODS_MAP, new HashMap<String, GoodUnit>(11));

		//~: create test GoodUnits
		try
		{
			createTestGoodUnits(ctx);
		}
		catch(Exception e)
		{
			throw new GenesisError(e, this, ctx);
		}

		Set<GoodUnit>  tgus = new LinkedHashSet<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class)));

		//~: load the test good units present
		Set<GoodUnit>  rest = new LinkedHashSet<GoodUnit>(
		  bean(GetGoods.class).getTestGoodUnits(ctx.get(Domain.class)));

		//~: remove the good units generated (they are ready)
		rest.removeAll(tgus);

		//?: {the user-created goods are not present} quit...
		if(rest.isEmpty()) return;

		//~: ensure the rest...
		for(GoodUnit gu : rest)
			ensureTestGoodUnit(ctx, gu);

		//~: assign the full list
		tgus.addAll(rest);
		setGoodUnits(ctx, tgus.toArray(new GoodUnit[tgus.size()]));
	}


	/* protected: test instances generation & verification */

	protected void      setGoodUnits(GenCtx ctx, GoodUnit[] gus)
	{
		ctx.set(gus);
	}

	protected void      setPriceLists(GenCtx ctx, PriceList[] pls)
	{
		ctx.set(pls);
	}

	protected void      createTestPriceLists(GenCtx ctx)
	{
		PriceList[] pls = new PriceList[4];

		//~: main
		pls[0] = createTestPriceList(ctx, "ОСН", "Основной");

		//~: 1-2-3
		pls[1] = createTestPriceList(ctx, "ПРВ", "Первый");
		pls[2] = createTestPriceList(ctx, "ВТО", "Второй");
		pls[3] = createTestPriceList(ctx, "ТРЕ", "Третий");

		setPriceLists(ctx, pls);
	}

	protected void      ensureTestPriceList(GenCtx ctx, PriceList pl)
	{
		//!: invoke ensure action
		actionRun(ActPriceList.ENSURE, pl);

		//~: log success
		if(LU.isD(log(ctx))) LU.D(log(ctx), logsig(),
		  " had ensured test PriceList with pk ", pl.getPrimaryKey());
	}

	/**
	 * The 'x' array has this items:
	 *
	 *  0  code of price list to create;
	 *  1  name of price list (when creating).
	 */
	protected PriceList createTestPriceList(GenCtx ctx, String... x)
	{
		String plc = x[0];
		String pln = x[1];

		PriceList pl = bean(GetGoods.class).
		  getPriceList(ctx.get(Domain.class).getPrimaryKey(), plc);

		//?: {this instance already exists} ensure it and quit
		if(pl != null)
		{
			ensureTestPriceList(ctx, pl);

			if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
			  " found test PriceList ", plc,
			  " with pk ", pl.getPrimaryKey());

			return pl;
		}

		//~: create test instance
		pl = new PriceList();
		setPrimaryKey(session(), pl, true);

		//~: domain & code
		pl.setDomain(ctx.get(Domain.class));
		pl.setCode(plc);

		//~: name
		pl.setName(pln);

		//!: do save
		actionRun(ActGoodUnit.SAVE, pl);

		//~: log success
		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " had created test PriceList ", plc,
		  " with pk ", pl.getPrimaryKey()
		);

		return  pl;
	}

	protected void      createTestGoodUnits(GenCtx ctx)
	  throws Exception
	{
		Object url = getClass().getResource("GenTestGoods.xml");
		if(url == null) throw new GenesisError(this, ctx,
		  EX.state("No 'GenTestGoods.xml' file found!"));

		//~: create the reader
		ReadTestGoods reader = createTestGoodsReader(ctx);

		//!: read the file
		try
		{
			reader.process(url.toString());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}

		//~: assign the resulting goods
		setGoodUnits(ctx, reader.getResult().toArray(
		  new GoodUnit[reader.getResult().size()]
		));
	}

	protected void      ensureTreeFolder(GenCtx ctx, GenState state)
	{
		StateFolder sf = state.folders.get(state.folders.size() - 1);
		TreeFolder  tf = new TreeFolder();

		//~: domain
		tf.setDomain(bean(GetTree.class).getDomain(
		  ctx.get(Domain.class).getPrimaryKey(), Goods.TYPE_GOODS_TREE
		));

		//~: code
		tf.setCode(sf.code);

		//~: name
		tf.setName(sf.name);

		//?: {parent folder}
		if(state.folders.size() > 1)
			tf.setParent(EX.assertn(state.folders.
			  get(state.folders.size() - 2).folder));

		//!: ensure it
		sf.folder = actionResult(TreeFolder.class, actionRun(
		  ActionType.ENSURE, tf,
		  ActTreeFolder.PARAM_TYPE, Goods.TYPE_GOODS_FOLDER
		));


		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  " had ", (tf == sf.folder)?("created"):("found"),
		  " test goods Tree Folder [", sf.folder.getPrimaryKey(),
		  "] with code [", sf.folder.getCode(),
		  "] named: ", sf.folder.getName()
		);
	}

	@SuppressWarnings("unchecked")
	protected void      ensureTestGoodUnit(GenCtx ctx, GoodUnit gu)
	{
		//!: invoke ensure action
		actionRun(ActGoodUnit.ENSURE, gu);

		//~: log success
		if(LU.isD(log(ctx))) LU.D(log(ctx), logsig(),
		   " had ensured test Good Unit [", gu.getPrimaryKey(), "]"
		);

		//~: put in the context map
		((Map<String, GoodUnit>) ctx.get(CTX_GOODS_MAP)).
		  put(gu.getCode(), unproxyDeeply(session(), gu));
	}

	@SuppressWarnings("unchecked")
	protected GoodUnit  ensureTestGoodUnit(GenCtx ctx, GenState state)
	{
		String guc = state.good.goodCode;
		String gun = state.good.goodName;
		String muc = state.good.meunCode;
		String mun = state.good.meunName;
		String muC = state.good.meunClassCode;
		String muU = state.good.meunClassUnit;
		String muf = state.good.meunInt;
		String prc = state.good.goodCost;

		GoodUnit gu = state.good.good = bean(GetGoods.class).
		  getGoodUnit(ctx.get(Domain.class).getPrimaryKey(), guc);

		//?: {this instance already exists} ensure it and quit
		if(gu != null)
		{
			ensureTestGoodUnit(ctx, gu);

			if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
			  " found test Good Unit [", gu.getPrimaryKey(),
			  "] with code [", guc, "] named: ", gu.getName()
			);

			//?: {good has calculation} ensure it
			if(state.good.calc != null)
				ensureTestGoodCalc(ctx, state);

			//~: add the good to the folder
			if(!state.folders.isEmpty())
				ensureGoodInFolder(ctx, state);

			return gu;
		}

		//~: create test instance
		state.good.good = gu = new GoodUnit();
		setPrimaryKey(session(), gu, true);

		//=: domain
		gu.setDomain(ctx.get(Domain.class));

		//=: good code
		Good g = gu.getOx();
		g.setCode(guc);

		//=: good name
		g.setName(gun);

		//~: create measure unit
		MeasureUnit mu = bean(GetGoods.class).
		  getMeasureUnit(ctx.get(Domain.class).getPrimaryKey(), muc);

		//?: {has no that MeasureUnit} create it
		if(mu == null)
		{
			Measure m = (mu = new MeasureUnit()).getOx();

			//~: measure unit' code & name
			m.setCode(muc);

			if(sXe(mun)) throw EX.arg("Measure Unit [", muc, "] has no name defined!");
			m.setName(mun);

			//~: measure unit' class code & unit
			m.setClassCode(muC);
			if(muU != null)
				m.setClassUnit(new BigDecimal(muU));
			else if(muC != null)
				m.setClassUnit(BigDecimal.ONE);

			//~: fractional
			if("I".equals(muf))
				m.setFractional(false);

			//!: update ox-measure
			mu.updateOx();
		}

		//~: assign measure unit
		gu.setMeasure(mu);

		//!: do save this good unit
		gu.updateOx();
		actionRun(ActGoodUnit.SAVE, gu,
		  ActGoodUnit.SAVE_MEASURE_UNIT, (mu.getPrimaryKey() == null)
		);

		//~: generate & save the prices
		//if(!sXe(prc))
		//	genGoodUnitPrices(ctx, gu, new BigDecimal(prc));

		//~: log success
		LU.I(log(ctx), logsig(), " created test Good Unit [",
		  gu.getPrimaryKey(), "] with code: ", guc
		);

		//~: put in the context map
		((Map<String, GoodUnit>) ctx.get(CTX_GOODS_MAP)).
		  put(gu.getCode(), gu);

		//?: {good has calculation} ensure it
		if(state.good.calc != null)
			ensureTestGoodCalc(ctx, state);

		//~: add the good to the folder
		if(!state.folders.isEmpty())
			ensureGoodInFolder(ctx, state);

		return gu;
	}

	protected void      genGoodUnitPrices(GenCtx ctx, GoodUnit gu, BigDecimal base)
	{
		PriceList[] pls = ctx.get(PriceList[].class);

		for(int i = 0;(i < pls.length);i++)
		{
			GoodPrice gp = new GoodPrice();

			//~: test primary key
			setPrimaryKey(session(), gp, true);

			//~: price list
			gp.setPriceList(pls[i]);

			//~: good unit
			gp.setGoodUnit(gu);

			//~: set base price for main entry
			if(i == 0)
				gp.setPrice(base);
			else
				genGoodUnitPrice(ctx, gp, base);

			//!: save the price
			actionRun(ActionType.SAVE, gp);
		}
	}

	protected void      genGoodUnitPrice(GenCtx ctx, GoodPrice gp, BigDecimal base)
	{
		long cents = base.multiply(BigDecimal.valueOf(100L)).longValue();
		long min   = cents * 75  / 100; if(min <= 0L)  min = 1L;
		long max   = cents * 125 / 100; if(max <= min) max = min + 1L;

		gp.setPrice(BigDecimal.valueOf(min + ctx.gen().nextInt((int)(max - min))).
		  multiply(new BigDecimal("0.01"))
		);
	}

	protected void      ensureGoodInFolder(GenCtx ctx, GenState state)
	{
		GoodUnit   g = state.good.good;
		TreeFolder f = EX.assertn(state.folders.
		  get(state.folders.size() - 1).folder);

		//!: add good to the folder on the top of the stack
		actionRun(ActTreeFolder.ADD, f, ActTreeFolder.PARAM_ITEM, g);

		if(LU.isI(log(ctx))) LU.I(log(ctx), logsig(),
		  "added good [", g.getCode(), "] into goods Folder [",
		  f.getPrimaryKey(), "], code: [", f.getCode(), "]"
		);
	}

	@SuppressWarnings("unchecked")
	protected GoodCalc  ensureTestGoodCalc(GenCtx ctx, GenState state)
	{
		GoodUnit  gu = state.good.good;
		GoodCalc  gc = gu.getGoodCalc();
		Calc       c;
		StateCalc sc = state.good.calc;

		//?: {good already has a calculation} skip this
		if(gc != null) return gc;

		//~: create a new one
		c = (gc = new GoodCalc()).getOx();

		//=: calc good
		gc.setGoodUnit(gu);

		//=: semi-ready
		c.setSemiReady(sc.semiReady);

		//=: open time
		c.setTime(EX.assertn(ctx.get(Date.class)));

		//?: {derived}
		if(sc.derived)
		{
			//=: super-good
			gc.setSuperGood(EX.assertn(
			  ((Map<String, GoodUnit>)ctx.get(CTX_GOODS_MAP)).get(sc.superGood),

			  "Derived Good Unit code [", gu.getCode(),
			  "] refers super Good [", sc.superGood, "] not existing!"
			));

			//~: sub-code
			c.setSubCode(sc.subCode);

			//~: sub-volume
			try
			{
				c.setSubVolume(new BigDecimal(sc.subVolume).
				  setScale(gc.getSuperGood().getMeasure().getOx().isFractional()?(3):(0)));
			}
			catch(Throwable e)
			{
				throw EX.arg(e, "Derived Good Unit code [", gu.getCode(),
				  "] has illegal sub-volume decimal [", sc.subVolume, "]!"
				);
			}

			//~: add the part
			EX.assertx(sc.parts.isEmpty());

			CalcPart gp = new CalcPart();
			gc.getParts().add(gp);

			//~: part calc
			gp.setGoodCalc(gc);

			//~: part good
			gp.setGoodUnit(gc.getSuperGood());

			//~: part volume
			gp.setVolume(c.getSubVolume());
		}

		//~: create the goods
		if(!sc.derived) EX.asserte(sc.parts);
		for(StatePart sp : sc.parts)
		{
			CalcPart gp = new CalcPart();
			gc.getParts().add(gp);

			//~: part calc
			gp.setGoodCalc(gc);

			//~: good unit
			GoodUnit g = EX.assertn(
			  ((Map<String, GoodUnit>)ctx.get(CTX_GOODS_MAP)).get(sp.goodCode),

			  "Good Unit code [", gu.getCode(),
			  "] Calculation' Part index [", sc.parts.indexOf(sp),
			  "] refers by code [", sp.goodCode,
			  "] Good Unit not existing!"
			);

			gp.setGoodUnit(g);

			//~: volume
			try
			{
				gp.setVolume(new BigDecimal(sp.volume).setScale(3));
			}
			catch(Throwable e)
			{
				throw EX.arg(e, "Good Unit code [", gu.getCode(),
				  "] Calculation' Part index [", sc.parts.indexOf(sp),
				  "] has illegal volume decimal [", sp.volume, "]!"
				);
			}

			//?: {measure of referred good is integer}
			if(!g.getMeasure().getOx().isFractional()) try
			{
				gp.setVolume(gp.getVolume().setScale(0));
			}
			catch(Throwable e)
			{
				throw EX.arg(e, "Good Unit code [", gu.getCode(),
				  "] Calculation' Part index [", sc.parts.indexOf(sp),
				  "] has volume decimal [", sp.volume,
				  "], but the Good referred [", g.getPrimaryKey(),
				  "] allows integer volumes only!"
				);
			}
		}

		//!: save (add) the calculation
		gc.updateOx();
		actionRun(ActionType.SAVE, gc);

		LU.I(log(ctx), logsig(), " created Good Calc [",
		  gc.getPrimaryKey(), "] for test Good Unit [",
		  gu.getPrimaryKey(), ']'
		);

		return gc;
	}


	/* protected: xml handler states */

	protected static class GenState
	{
		/**
		 * Currently processed good.
		 */
		public StateGood good;

		/**
		 * The stack of folders.
		 */
		public List<StateFolder> folders =
		  new ArrayList<StateFolder>(4);
	}

	protected static class StateFolder
	{
		public String code;
		public String name;

		public TreeFolder folder;
	}

	protected static class StateGood
	{
		public String goodCode;
		public String goodName;
		public String goodCost;
		public String meunCode;
		public String meunInt;
		public String meunName;
		public String meunClassCode;
		public String meunClassUnit;

		public GoodUnit  good;
		public StateCalc calc;
	}

	protected static class StateCalc
	{
		public boolean incalc;
		public boolean semiReady;
		public boolean derived;
		public String  superGood;
		public String  subCode;
		public String  subVolume;

		public List<StatePart> parts =
		  new ArrayList<StatePart>(4);
	}

	protected static class StatePart
	{
		public String  goodCode;
		public String  volume;
		public boolean semiReady;
	}


	/* protected: xml handler */

	protected ReadTestGoods createTestGoodsReader(GenCtx ctx)
	{
		return new ReadTestGoods(ctx);
	}

	protected class ReadTestGoods extends SaxProcessor<GenState>
	{
		/* constructor */

		public ReadTestGoods(GenCtx ctx)
		{
			this.ctx = ctx;
		}

/*

	<folder code="" name=""> [recursive]

		<good code="" [cost=""]>
			<name/>
			<measure code="" integer="" short-name="" name=""
			  [class-code="" class-unit=""]/>

			<derived good = "" semi-ready = ""
			  sub-code = "" volume = ""/>

			<calc>
				<good code = "" volume = ""/>
		</good>


*/
		/* public: ReadTestGoods interface */

		public List<GoodUnit> getResult()
		{
			return result;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			if(level() == 1)
				event().state(new GenState());

			if("folder".equals(event().tag()))
			{
				EX.assertx(
				  state(1).good == null,
				  "Test Goods has folder nested in good!"
				);

				StateFolder sf = new StateFolder();
				state(1).folders.add(sf);
			}

			if("good".equals(event().tag()))
			{
				StateGood sg = state(1).good;

				if(sg != null)
				{
					EX.assertn(sg.calc);
					EX.assertx(sg.calc.incalc);
				}
				else
					state(1).good = new StateGood();
			}
		}

		protected void open()
		{
			//~: <folder>
			if(event().istag("folder"))
			{
				EX.asserte(state(1).folders);

				StateFolder sf = state(1).folders.get(
				  state(1).folders.size() - 1);

				//~: folder code
				sf.code = event().attr("code");
				EX.assertx(!sXe(sf.code));

				//~: folder name
				sf.name = event().attr("name");
				EX.assertx(!sXe(sf.name));

				//!: ensure the folder
				ensureTreeFolder(ctx, state(1));
			}

			//~: <good> | <good> <calc> <good>
			if(event().istag("good"))
			{
				StateGood sg = EX.assertn(state(1).good);

				//?: {<good> <calc> <good>}
				if(sg.calc != null)
				{
					EX.assertx(sg.calc.incalc);

					StatePart p = new StatePart();
					sg.calc.parts.add(p);

					//~: good code
					p.goodCode = event().attr("code");
					EX.assertx(!sXe(p.goodCode));

					//~: volume
					p.volume = event().attr("volume");
					EX.assertx(!sXe(p.volume));

					//~: semi-ready
					p.semiReady = "true".equals(event().attr("semi-ready"));
				}
				else
				{
					//~: good code
					sg.goodCode = event().attr("code");
					EX.assertx(!sXe(sg.goodCode));

					//~: good cost
					sg.goodCost = event().attr("cost");
				}
			}

			//~: <good> <measure>
			if(event().istag("measure"))
			{
				StateGood sg = EX.assertn(state(1).good);

				sg.meunCode      = event().attr("code");
				EX.assertx(!sXe(sg.meunCode));

				sg.meunName      = event().attr("name");
				sg.meunInt       = ("true".equals(event().attr("integer")))?("I"):("F");
				sg.meunClassCode = event().attr("class-code");
				sg.meunClassUnit = event().attr("class-unit");
			}

			//~: <good> <derived>
			if(event().istag("derived"))
			{
				StateGood sg = EX.assertn(state(1).good);
				EX.assertx(sg.calc == null);

				sg.calc = new StateCalc();
				sg.calc.incalc = false; //<-- no parts

				//~: derived
				sg.calc.derived = true;

				//~: super-good
				sg.calc.superGood = event().attr("good");
				EX.assertx(!sXe(sg.calc.superGood));

				//~: sub-code
				sg.calc.subCode = event().attr("sub-code");
				EX.assertx(!sXe(sg.calc.subCode));

				//~: sub-volume
				sg.calc.subVolume = event().attr("volume");
				EX.assertx(!sXe(sg.calc.subVolume));

				//~: calc semi-ready
				sg.calc.semiReady = "true".equals(event().attr("semi-ready"));
			}

			//~: <good> <calc>
			if(event().istag("calc"))
			{
				StateGood sg = EX.assertn(state(1).good);
				EX.assertx(sg.calc == null);

				sg.calc = new StateCalc();
				sg.calc.incalc = true;

				//~: calc semi-ready
				sg.calc.semiReady = "true".equals(event().attr("semi-ready"));
			}
		}

		protected void close()
		{
			//~: <good> </name>
			if(event().istag("name") && (state(1).good != null))
			{
				StateGood sg = state(1).good;

				//~: name
				sg.goodName = s2s(event().text());
				EX.assertn(sg.goodName);
			}

			//~: <good> </calc>
			if(event().istag("calc"))
			{
				StateGood sg = EX.assertn(state(1).good);
				EX.assertn(sg.calc).incalc = false;
			}

			//~: </good>
			if(event().istag("good"))
			{
				StateGood sg = EX.assertn(state(1).good);

				//?: {not in the calc} create the good
				if((sg.calc == null) || !sg.calc.incalc)
				{
					//!: ensure the good
					result.add(ensureTestGoodUnit(ctx, state(1)));

					//~: cleanup the good
					state(1).good = null;
				}
			}

			//~: </folder>
			if(event().istag("folder"))
			{
				EX.assertx(!state(1).folders.isEmpty());

				//~: pop the top folder
				state(1).folders.remove(state(1).folders.size() - 1);
			}
		}


		/* genesis context & result */

		protected GenCtx        ctx;
		private List<GoodUnit>  result = new ArrayList<GoodUnit>(32);
	}
}