package com.tverts.retrade.web.views.goods;

/* Java */

import java.math.BigDecimal;
import java.util.List;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelRequest;
import com.tverts.model.SimpleModelBean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: scripting */

import com.tverts.jsx.JsX;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.Good;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.ActGoodCalc;
import com.tverts.retrade.domain.goods.CalcPartView;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodCalcView;
import com.tverts.retrade.domain.goods.GoodModelBean;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade domain (selection sets) */

import com.tverts.retrade.domain.selset.ActSelSet;
import com.tverts.retrade.domain.selset.GetSelSet;
import com.tverts.retrade.domain.selset.SelSet;

/* com.tverts: retrade domain (data) */

import com.tverts.retrade.web.data.goods.GoodCalcModelData;
import com.tverts.retrade.web.data.goods.GoodCalcsModelData;
import com.tverts.retrade.web.data.goods.GoodGroupsModelData;
import com.tverts.retrade.web.data.goods.MeasureUnitsModelData;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Faces view to edit or create a {@link GoodUnit}.
 *
 * @author anton.baukin@gmail.com
 */
@Component @Scope("request")
public class FacesGoodEditView extends ModelView
{
	/* actions */

	public String doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		formValid = (code == null) || !checkCodeExists(code);
		return null;
	}

	public String doCommitEdit()
	{
		forceSecure("edit: goods");

		if(SU.sXe(getGoodView().getGoodCode()))
			throw EX.arg("Good Unit code must be defined!");
		if(SU.sXe(getGoodView().getGoodName()))
			throw EX.arg("Good Unit name must be defined!");

		//~: check the code exists
		formValid = !checkCodeExists(getGoodView().getGoodCode());
		if(!formValid) return null;

		GoodUnit gu;
		Good     g;
		GetGoods get = bean(GetGoods.class);
		boolean  u   = false;

		//?: {create new good}
		if(getGoodView().getObjectKey() == null)
		{
			gu = new GoodUnit();

			//=: domain
			gu.setDomain(loadModelDomain());

			//=: primary key
			HiberPoint.setPrimaryKey(TxPoint.txSession(), gu,
			  HiberPoint.isTestInstance(gu.getDomain()));

			g = gu.getOx();
		}
		//!: load it
		else
		{
			gu = EX.assertn(get.getGoodUnitStrict(getGoodView().getObjectKey()));

			//sec: the same domain
			SecPoint.isSameDomain(gu);

			g  = gu.getOx();

			//?: {test is used}
			u = (get.isGoodUsed(gu.getPrimaryKey(), true) != 0);
		}

		//~: load measure unit
		MeasureUnit mu = EX.assertn(get.getMeasureUnit(
		  getGoodView().getMeasureKey()));

		//?: {changing measure of used good}
		if(u && !CMP.eq(gu.getMeasure(), mu))
			throw EX.state("Good Unit is used, Measure may not be updated!");

		//=: good code
		g.setCode(getGoodView().getGoodCode());

		//~: good name
		g.setName(getGoodView().getGoodName());

		//~: good group
		g.setGroup(SU.s2s(getGoodView().getGoodGroup()));

		//~: assign it
		gu.setMeasure(mu);

		//~: update ox
		gu.updateOx();

		//!: save | update the good
		if(getGoodView().getObjectKey() == null)
		{
			actionRun(ActionType.SAVE, gu);
			goodAddedKey = gu.getPrimaryKey();
		}
		else
		{
			//~: update the good
			actionRun(ActionType.UPDATE, gu);

			//~: load, update the sub-goods
			for(GoodUnit sub : get.getSubGoods(gu.getPrimaryKey()))
			{
				//~: copy parent fields
				Goods.copySub(gu, sub);

				//~: update own ox-good
				sub.updateOxOwn();

				//!: update sub
				actionRun(ActionType.UPDATE, sub);
			}
		}

		//?: {has calculation updated}
		if(getModel().isCalcUpdated())
		{
			GoodCalc gc = bean(GetGoods.class).buildCalc(getCalcView());
			Calc      c = gc.getOx();

			//=: open time (now)
			c.setTime(new java.util.Date());

			//=: (this) good unit
			gc.setGoodUnit(gu);

			//!: save it
			gc.updateOx();
			actionRun(ActionType.SAVE, gc);
		}
		//~: update the comment
		else if((getCalcView() != null) && (gu.getGoodCalc() != null))
		{
			gu.getGoodCalc().getOx().setRemarks(getCalcView().getRemarks());
			gu.getGoodCalc().updateOx();
		}

		//?: {has selection set} add to it
		if(getModel().isSelSetAble() && (getSelSet() != null))
		{
			SelSet set = bean(GetSelSet.class).
			  getSelSet(SecPoint.login(), getSelSet());

			if(set == null) throw EX.state(
			  "No Selection Set named [", getSelSet(), "]!"
			);

			actionRun(ActSelSet.ADD, set, ActSelSet.OBJECTS, gu);
		}

		//?: {has goods folder} add to it
		if(getModel().getGoodsFolder() != null)
		{
			TreeFolder f = bean(GetTree.class).
			  getFolder(getModel().getGoodsFolder());

			//!: add
			actionRun(ActTreeFolder.ADD, f, ActTreeFolder.PARAM_ITEM, gu);
		}

		return null;
	}

	public String gotoWhere(String where)
	{
		if("calc".equals(where))
			if(getCalcView() != null)
				return "calc";
			else
				where = "calc-edit";

		if("calc-back".equals(where))
			if(!isHistoryCalc())
				return "edit";
			else
			{
				//!: clean history view
				getModel().setHistoryCalc(null);
				return "history";
			}

		if("calcs-history".equals(where))
			return "history";

		if("calcs-history-back".equals(where))
			return "calc";

		if("calc-edit".equals(where))
		{
			getModel().setEditCalc(OU.cloneBest(
			  isHistoryCalc()?(getModel().getHistoryCalc()):(getCalcView())
			));

			//?: {has no calculation} create the empty one
			if(getModel().getEditCalc() == null)
				getModel().setEditCalc(new GoodCalcView().init(getGoodView()));

			return "calc-edit";
		}

		if("calc-edit-back".equals(where))
		{
			getModel().setEditCalc(null);
			return (getCalcView() == null)?("edit"):("calc");
		}

		if("calc-edit-submit".equals(where))
		{
			getModel().setEditCalc(null);
			return "edit";
		}

		if("measures".equals(where))
		{
			if("true".equals(request().getParameter("edit-mode")))
				getModel().setEditMode(true);
			return "measures";
		}

		if("measures-back".equals(where))
			return "edit";

		throw EX.state("How to go from where [", where, "]?");
	}

	public String doRemarkCalc()
	{
		forceSecure("edit: goods");

		getCalcView().setRemarks(SU.s2s(
		  request().getParameter("remarks")));

		return null;
	}

	public String gotoHistoryCalc()
	{
		EX.assertx(!isHistoryCalc(), "Nesting Calculations history!");

		//~: load the calculation
		GoodCalc c = bean(GetGoods.class).getGoodCalc(
		  obtainEntityKeyFromRequestStrict());
		EX.assertn("Good Calculation [",
		  obtainEntityKeyFromRequest(), "] not found!");

		//sec: the calc of the same good
		if(!c.getGoodUnit().getPrimaryKey().equals(getGoodView().getObjectKey()))
			throw EX.forbid();

		//~: create calc history view
		getModel().setHistoryCalc(new GoodCalcView().
		  init(c).initParts(c));

		return "calc";
	}

	public String doSubmitCalc()
	{
		forceSecure("edit: goods");

		GoodCalcView c = EX.assertn(getModel().getEditCalc(),
		  "Good Calculation is not edited!"
		);

		//~: remove current parts
		c.getParts().clear();

		//c: index the parameters
		for(int i = 0;;i++)
		{
			String gk = SU.s2s(request().getParameter("goodUnit" + i));
			String gc = SU.s2s(request().getParameter("goodCode"+i));
			if((gk == null) || (gc == null)) break;

			String vo = SU.s2s(request().getParameter("volume"+i));
			String sr = SU.s2s(request().getParameter("semiReady"+i));

			//?: {has no volume}
			if(vo == null)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "объём не указан!"
				);

				return null;
			}

			//~: create and add the part view
			CalcPartView p = new CalcPartView();
			c.getParts().add(p);

			//~: load good unit
			GoodUnit gu = bean(GetGoods.class).getGoodUnit(Long.parseLong(gk));
			if(gu == null)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "товар с указанным кодом не найден!"
				);

				return null;
			}

			//sec: good of the same domain
			if(!gu.getDomain().getPrimaryKey().equals(getModel().getDomain()))
				throw EX.forbid();

			//~: assign good unit
			p.init(gu);

			//~: assign the volume
			try
			{
				p.setVolume(new BigDecimal(vo));

				//?: {volume must be integer}
				if(!gu.getMeasure().getOx().isFractional())
					p.setVolume(p.getVolume().setScale(0));
			}
			catch(Throwable e)
			{
				errorEvent = SU.cats(
				  "Товар [", gc, "]: ",
				  "объём имеет неверный формат!"
				);

				return null;
			}

			//~: semi-ready
			if("true".equals(sr))
				p.setSemiReady(true);
			else if("false".equals(sr))
				p.setSemiReady(false);
		}

		//?: {has cyclic dependency}
		if(checkCycliCalc(c))
			return null;

		//~: semi-ready good
		c.setSemiReady("true".equals(request().getParameter("semiReady")));

		//~: clear derived flag & super-good
		c.initSuperGood(null);

		//~: assign the calculation
		EX.asserte(c.getParts(), "Calculation has no Parts!");
		getModel().setCalc(c);

		//~: mark the calc as updated
		getModel().setCalcUpdated(true);

		//~: only edit mode from now
		getModel().setEditMode(true);

		return null;
	}

	public String doSubmitDerived()
	{
		forceSecure("edit: goods");

		GoodCalcView c = EX.assertn(getModel().getEditCalc(),
		  "Good Calculation is not edited!"
		);

		//~: check sub-code is set
		c.setSubCode(SU.s2s(c.getSubCode()));
		if(c.getSubCode() == null)
		{
			errorEvent = "Суб-код товара не указан!";
			return null;
		}

		//~: load super-good-unit
		GoodUnit gu = bean(GetGoods.class).
		  getGoodUnit(getModel().domain(), c.getSuperGoodCode());

		if(gu == null)
		{
			errorEvent = SU.cats(
			  "Товар с указанным кодом [", c.getSuperGoodCode(), "] не найден!"
			);

			return null;
		}

		//~: assign super-good
		c.initSuperGood(gu);

		//~: check sub-code exists
		subCodeExists = checkSubCodeExists(c);
		if(subCodeExists) { formValid = false; return null; }

		//~: remove current parts & add derived one
		CalcPartView p = new CalcPartView();
		c.getParts().clear();
		c.getParts().add(p);

		//~: assign super-good-unit
		p.init(gu);

		//~: assign the volume
		try
		{
			p.setVolume(c.getSubVolume());

			//?: {volume must be integer}
			if(!gu.getMeasure().getOx().isFractional())
				p.setVolume(p.getVolume().setScale(0));
		}
		catch(Throwable e)
		{
			errorEvent = "Объём имеет неверный формат!";
			return null;
		}

		//?: {has cyclic dependency}
		if(checkCycliCalc(c))
			return null;

		//~: semi-ready good
		c.setSemiReady("true".equals(request().getParameter("semiReady")));

		//~: assign the calculation
		getModel().setCalc(c);

		//~: mark the calc as updated
		getModel().setCalcUpdated(true);

		//~: only edit mode from now
		getModel().setEditMode(true);

		return null;
	}


	/* public: view interface */

	public GoodModelBean getModel()
	{
		return (GoodModelBean) super.getModel();
	}

	public GoodUnitView getGoodView()
	{
		EX.assertn(getModel().getView(),
		  "Edit view of Good Unit not present!"
		);

		return getModel().getView();
	}

	public GoodCalcView getCalcView()
	{
		return (getModel().getHistoryCalc() != null)?
		  (getModel().getHistoryCalc()):(getModel().getCalc());
	}

	public boolean isHistoryCalc()
	{
		return (getModel().getHistoryCalc() != null);
	}

	public boolean isMaterial()
	{
		return (getCalcView() == null);
	}

	public boolean isSemiReady()
	{
		return (getCalcView() != null) && getCalcView().isSemiReady();
	}

	public String getInfoWindowTitle()
	{
		return formatTitle("Товар",
		  getGoodView().getGoodCode(),
		  getGoodView().getGoodName());
	}

	public String getMeasuresWindowTitle()
	{
		return formatTitle("Ед. изм. товара",
		  getGoodView().getGoodCode(),
		  getGoodView().getGoodName());
	}


	/* public: edit interface */

	public boolean isCreating()
	{
		return (getGoodView().getObjectKey() == null);
	}

	public String getEditWindowTitle()
	{
		if(getGoodView().getObjectKey() == null)
			return "Создание товара";

		return formatTitle("Ред. товара",
		  getGoodView().getGoodCode(),
		  getGoodView().getGoodName());
	}

	public String getErrorEvent()
	{
		return errorEvent;
	}

	private String errorEvent;

	public boolean isFormValid()
	{
		return SU.sXe(errorEvent) && (formValid ||
		  "true".equals(request().getParameter("immediate")));
	}

	private boolean formValid = true;

	public boolean isCodeExists()
	{
		return codeExists;
	}

	private boolean codeExists;

	public boolean isSubCodeExists()
	{
		return subCodeExists;
	}

	private boolean subCodeExists;

	public String getSameSubCode()
	{
		return sameSubCode;
	}

	private String sameSubCode;

	public String getSelSet()
	{
		return selSet;
	}

	private String selSet;

	public void setSelSet(String selSet)
	{
		this.selSet = selSet;
	}

	private Long goodAddedKey;

	public Long getGoodAddedKey()
	{
		return goodAddedKey;
	}


	/* public: calculation interface */

	public String getCalcWindowTitle()
	{
		if(getGoodView().getObjectKey() == null)
			return "Формула создаваемого товара";

		boolean hc = isHistoryCalc();
		String  dt = (getCalcView() == null)?(""):
		  DU.datetime2str(getCalcView().getOpenTime());

		return formatTitle(
		  SU.cats((!hc)?("Формула"):SU.cats("Формула от ", dt), " товара"),
		  getGoodView().getGoodCode(), getGoodView().getGoodName()
		);
	}

	public String getCalcEditWindowTitle()
	{
		if(getGoodView().getObjectKey() == null)
			return "Формула создаваемого товара";

		return formatTitle("Ред. ф-лы товара",
		  getGoodView().getGoodCode(),
		  getGoodView().getGoodName()
		);
	}

	public String getCalcsHistoryWindowTitle()
	{
		EX.assertn(getGoodView().getObjectKey());

		return formatTitle("История формул товара",
		  getGoodView().getGoodCode(), getGoodView().getGoodName()
		);
	}


	/* public: measures interface */

	public String getMeasuresEncoded()
	{
		//~: load the target good
		EX.assertn(getGoodView().getObjectKey());
		GoodUnit goodUnit = bean(GetGoods.class).
		  getGoodUnitStrict(getGoodView().getObjectKey());

		//~: invoke the coding script
		return (String) JsX.apply("web/views/goods/Goods.jsx",
		  "encodeGoodMeasures", goodUnit);
	}


	/* public: ModelView (access model) interface */

	public ModelBean provideModel()
	{
		SimpleModelBean mb = new SimpleModelBean();

		//=: domain
		mb.setDomain(getDomainKey());

		//?: {measure units}
		if(ModelRequest.isKey("measures"))
		{
			//~: create model data
			mb.setDataClass(MeasureUnitsModelData.class);

			return mb;
		}

		//?: {goods groups}
		if(ModelRequest.isKey("groups"))
		{
			//~: create model data
			mb.setDataClass(GoodGroupsModelData.class);

			return mb;
		}

		//=: good unit
		mb.put(GoodUnit.class, getGoodView().getObjectKey());

		//?: {good calculations list}
		if(ModelRequest.isKey("calcs"))
		{
			EX.assertn(getGoodView().getObjectKey(),
			  "Good Unit is being created has no Calculations yet! "
			);

			//~: create model data
			mb.setDataClass(GoodCalcsModelData.class);

			return mb;
		}

		//?: {good calculation parts}
		if(ModelRequest.isKey("parts"))
		{
			EX.assertn(getCalcView(),
			  "Good Unit [", getGoodView().getObjectKey(), "] has no Calculation!"
			);

			//~: set calc view
			mb.put(GoodCalcView.class, getCalcView());

			//~: create model data
			mb.setDataClass(GoodCalcModelData.class);

			return mb;
		}

		//?: {good calculation parts for edit}
		if(ModelRequest.isKey("parts-edit"))
		{
			EX.assertn(getModel().getEditCalc());

			//~: set calc view
			mb.put(GoodCalcView.class, getModel().getEditCalc());

			//~: create model data
			mb.setDataClass(GoodCalcModelData.class);

			return mb;
		}

		throw EX.state("No valid model data key requested!");
	}


	/* protected: actions support */

	protected boolean checkCodeExists(String code)
	{
		GoodUnit gu = bean(GetGoods.class).
		  getGoodUnit(getModel().domain(), code);

		return codeExists = (gu != null) && (
		  (getGoodView().getObjectKey() == null) || // <-- creating
		  !getGoodView().getObjectKey().equals(gu.getPrimaryKey())
		);
	}

	protected boolean checkCycliCalc(GoodCalcView c)
	{
		//~: check cyclic dependencies
		if(getGoodView().getObjectKey() != null) try
		{
			GoodCalc calc = bean(GetGoods.class).buildCalc(c);

			//~: good unit
			calc.setGoodUnit(bean(GetGoods.class).
			  getGoodUnitStrict(getGoodView().getObjectKey()));

			ActGoodCalc.checkCalculationsCycle(calc);
		}
		catch(ActGoodCalc.CycliCalcError e)
		{
			StringBuilder s = new StringBuilder(128);
			s.append("Циклическая зависимость производства товаров: ");

			for(int i = 0;(i < e.getCycle().length);i++)
				s.append((i == 0)?(""):(", ")).
				  append(e.getCycle()[i].getCode());

			errorEvent = s.toString();
			return true;
		}

		return false;
	}

	protected boolean checkSubCodeExists(GoodCalcView c)
	{
		List<GoodCalc> calcs = bean(GetGoods.class).
		  getGoodCalcsBySuperGood(c.getSuperGood());

		//c: for all not closed calculations
		for(GoodCalc gc : calcs) if(c.getCloseTime() != null)
			if(gc.getOx().getSubCode().equals(c.getSubCode()))
			{
				if(gc.getPrimaryKey().equals(c.getObjectKey()))
					continue;

				sameSubCode = gc.getGoodUnit().getCode();
				return true;
			}

		return false;
	}


	/* protected: ModelView interface */

	protected GoodModelBean createModel()
	{
		GoodModelBean mb = new GoodModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: create empty view
		mb.setView(new GoodUnitView());

		//?: {NOT creating} almost done here
		if(!"true".equals(request().getParameter("create")))
		{
			//~: key of good | calc
			Long     pk = obtainEntityKeyFromRequestStrict();
			GoodUnit gu;         //<-- target good unit
			GoodCalc hc  = null; //<-- history calculation

			//?: {opening calculation}
			if("true".equals(request().getParameter("goodCalc")))
			{
				hc = EX.assertn(bean(GetGoods.class).getGoodCalc(pk),
				  "Good Unit Calculation [", pk, "] is not found!");

				//~: good of that calculation
				gu = hc.getGoodUnit();
			}
			//~: load the good unit
			else
			{
				gu = EX.assertn(bean(GetGoods.class).getGoodUnit(pk),
				  "Good Unit [", pk, "] is not found!");

				//?: {is a sub-good} retarget
				if(gu.getSuperGood() != null)
					gu = gu.getSuperGood();
			}

			//sec: check good of the same domain
			if(!gu.getDomain().getPrimaryKey().equals(mb.domain()))
				throw EX.forbid("Good Unit of else Domain!");

			//!: init view with this good
			mb.getView().init(gu);

			//?: {good has calculation} create it's view
			if(gu.getGoodCalc() != null)
				mb.setCalc(new GoodCalcView().
				  init(gu.getGoodCalc()).
				  initParts(gu.getGoodCalc())
				);

			//?: create calc history view
			if((hc != null) && !CMP.eq(hc, gu.getGoodCalc()))
				getModel().setHistoryCalc(new GoodCalcView().
				  init(hc).initParts(hc));

			return mb;
		}

		//!: assign new code
		mb.getView().setGoodCode(
		  Goods.genNextGoodCode(loadDomain())
		);

		//=: is editing mode for create
		mb.setEditMode(true);

		//~: enable the selection set
		mb.setSelSetAble(!"true".equals(request().getParameter("disableSelSet")));

		//?: {has goods folder specified}
		if(!SU.sXe(request().getParameter("goodsFolder")))
		{
			String     p = SU.s2s(request().getParameter("goodsFolder"));
			TreeFolder f = bean(GetTree.class).getFolder(Long.parseLong(p));

			if(f == null) throw EX.arg("No Tree Folder [", p, "]!");

			//?: {not a goods folder}
			if(!Goods.TYPE_GOODS_TREE.equals(f.getDomain().getTreeType().getTypeName()))
				throw EX.state();

			if(!Goods.TYPE_GOODS_FOLDER.equals(f.getUnity().getUnityType().getTypeName()))
				throw EX.state();

			//sec: {else domain}
			if(!mb.domain().equals(f.getDomain().getDomain().getPrimaryKey()))
				throw EX.forbid();

			mb.setGoodsFolder(f.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof GoodModelBean);
	}
}