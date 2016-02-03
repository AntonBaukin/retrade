package com.tverts.retrade.web.views.goods;

/* Java */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

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

/* com.tverts: api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: endure (core + trees) */

import com.tverts.endure.tree.ActTreeFolder;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeFolder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: retrade domain (selection sets) */

import com.tverts.retrade.domain.selset.ActSelSet;
import com.tverts.retrade.domain.selset.GetSelSet;
import com.tverts.retrade.domain.selset.SelSet;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.web.data.goods.GoodGroupsModelData;
import com.tverts.retrade.web.data.goods.MeasureUnitsModelData;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Faces view to edit or create several goods
 * in single table.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesAddGoodsTableView") @Scope("request")
public class FacesAddGoodsTableView extends ModelView
{
	/* actions */

	public String doGenNextGoodCode()
	{
		nextGoodCode = Goods.genNextGoodCode(loadModelDomain());
		return null;
	}

	public String doCommitCreate()
	{
		//~: read the good from the request
		ArrayList<GoodUnitView> goods =
		  new ArrayList<GoodUnitView>(4);

		for(int i = 0;;i++)
		{
			String code = SU.s2s(request().getParameter("goodCode" + i));
			if(code == null) break;

			GoodUnitView g = new GoodUnitView();
			goods.add(g);

			//~: code
			g.setGoodCode(code);

			//~: name
			g.setGoodName(SU.s2s(request().getParameter("goodName" + i)));
			if(g.getGoodName() == null) throw EX.arg();

			//~: group
			g.setGoodGroup(SU.s2s(request().getParameter("goodGroup" + i)));

			//~: measure code
			g.setMeasureCode(SU.s2s(request().getParameter("measureCode" + i)));
			if(g.getMeasureCode() == null) throw EX.arg();
		}

		//?: {has no goods} nothing to do
		if(goods.isEmpty()) return null;

		//~: validate the goods codes
		errorMessage = validateGoods(goods);
		if(errorMessage != null) return null;

		//~: selection set
		SelSet     selset = null;
		List<Long> keys   = null;

		if(isSelSetAble() && (request().getParameter("selset") != null))
		{
			selset = bean(GetSelSet.class).
			  getSelSet(SecPoint.login(), request().getParameter("selset"));

			if(selset == null) throw EX.state(
			  "No Selection Set named [", request().getParameter("selset"), "]!"
			);

			keys = new ArrayList<Long>(goods.size());
		}

		//~: goods folder
		TreeFolder folder = null;

		if(getModel().get(TreeFolder.class) != null)
		{
			folder = bean(GetTree.class).
			  getFolder(getModel().get(TreeFolder.class, Long.class));

			if(folder == null) throw EX.state(
			  "Good Folder [", getModel().get(TreeFolder.class), "] not found!"
			);
		}

		//~: create the goods
		HashMap<String, MeasureUnit> measures = //<-- cache of the measures
		  new HashMap<String, MeasureUnit>(5);

		//c: for all goods to create
		for(GoodUnitView g : goods)
		{
			GoodUnit gu = new GoodUnit();
			Good      x;

			//=: domain
			gu.setDomain(loadModelDomain());

			//=: primary key
			HiberPoint.setPrimaryKey(TxPoint.txSession(), gu,
			  HiberPoint.isTestInstance(gu.getDomain()));

			//~: create ox-good
			x = gu.getOx();

			//=: good code
			x.setCode(g.getGoodCode());

			//=: good name
			x.setName(g.getGoodName());

			//=: good group
			Goods.attrs(x).put(Goods.AT_GROUP, g.getGoodGroup());

			//~: measure unit
			MeasureUnit mu = measures.get(g.getMeasureCode());
			if(mu == null) mu = bean(GetGoods.class).
			  getMeasureUnit(getModel().domain(), g.getMeasureCode());
			if(mu == null) throw EX.arg(
			  "Measure Unit with code [", g.getMeasureCode(), "] not found!");
			measures.put(mu.getCode(), mu);
			gu.setMeasure(mu);

			//!: save the good
			gu.updateOx();
			actionRun(ActionType.SAVE, gu);


			//?: {has selection set} add
			if(selset != null)
			{
				keys.add(gu.getPrimaryKey());

				actionRun(ActSelSet.ADD, selset, ActSelSet.OBJECTS, gu);
			}

			//?: {has folder} add
			if(folder != null)
				actionRun(ActTreeFolder.ADD, folder, ActTreeFolder.PARAM_ITEM, gu);
		}

		//~: new selection set items keys
		this.addedKeys = SU.scats(",", keys);

		return null;
	}


	/* public: view interface */

	public SimpleModelBean getModel()
	{
		return (SimpleModelBean) super.getModel();
	}

	public boolean isFailed()
	{
		return (errorMessage != null);
	}

	private String nextGoodCode;

	public String getNextGoodCode()
	{
		return nextGoodCode;
	}

	private String errorMessage;

	public String getErrorMessage()
	{
		return errorMessage;
	}

	private String addedKeys;

	public String getAddedKeys()
	{
		return addedKeys;
	}

	public boolean isSelSetAble()
	{
		return !Boolean.FALSE.equals(getModel().get(SelSet.class));
	}


	/* public: ModelProvider interface */

	public ModelBean provideModel()
	{
		SimpleModelBean mb = new SimpleModelBean();

		//~: domain
		mb.setDomain(getModel().getDomain());

		//?: {measure units}
		if(ModelRequest.isKey("measures"))
		{
			//~: create model data
			mb.setDataClass(MeasureUnitsModelData.class);

			return mb;
		}

		//?: {good groups}
		if(ModelRequest.isKey("groups"))
		{
			//~: create model data
			mb.setDataClass(GoodGroupsModelData.class);

			return mb;
		}

		throw EX.state();
	}


	/* protected: actions support */

	protected String validateGoods(List<GoodUnitView> goods)
	{
		HashSet<String> codes = new HashSet<String>(goods.size());
		GetGoods        get   = bean(GetGoods.class);

		for(int i = 1;(i <= goods.size());i++)
		{
			GoodUnitView g = goods.get(i - 1);

			//~: duplicate form code
			if(codes.contains(g.getGoodCode())) throw EX.state();
			codes.add(g.getGoodCode());

			//~: check database
			if(get.getGoodUnit(getModel().domain(), g.getGoodCode()) != null)
				return SU.cats(
				  "Товар с кодом [", g.getGoodCode(),
				  "] в строке [", i, "] уже существует!"
				);
		}

		return null;
	}


	/* protected: ModelView interface */

	protected SimpleModelBean createModel()
	{
		SimpleModelBean mb = new SimpleModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: this view flag
		mb.put(this.getClass(), true);

		//?: {selection set is disabled}
		if("true".equals(request().getParameter("disableSelSet")))
			mb.put(SelSet.class, false);

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

			mb.put(TreeFolder.class, f.getPrimaryKey());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SimpleModelBean) &&
		  Boolean.TRUE.equals(((SimpleModelBean)model).get(this.getClass()));
	}
}