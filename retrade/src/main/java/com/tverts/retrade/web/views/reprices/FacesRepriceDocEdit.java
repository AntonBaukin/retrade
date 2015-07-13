package com.tverts.retrade.web.views.reprices;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Spring Framework */

import com.tverts.support.CMP;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;
import com.tverts.actions.ActionType;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodsModelBean;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceChangeEdit;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.Prices;
import com.tverts.retrade.domain.prices.RepriceDoc;
import com.tverts.retrade.domain.prices.RepriceDocEdit;
import com.tverts.retrade.domain.prices.RepriceDocEditModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Faces controller to edit or create
 * a Price Change Document.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesRepriceDocEdit") @Scope("request")
public class FacesRepriceDocEdit extends ModelView
{
	/* public: actions */

	public String gotoCancelEditReprice()
	{
		getModel().setActive(false);
		return "cancel-edit";
	}

	public String doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		codeExists = (code == null) || checkCodeExists(code);
		return null;
	}

	public String doSubmitReprice()
	{
		//~: read the changes from the request
		ArrayList<PriceChangeEdit> changes = new ArrayList<PriceChangeEdit>(4);
		getModel().getView().setPriceChanges(changes);

		for(int i = 0;;i++)
		{
			String code = SU.s2s(request().getParameter("goodCode" + i));
			if(code == null) break;

			PriceChangeEdit pce = new PriceChangeEdit();
			changes.add(pce);

			//~: good code
			pce.setGoodCode(code);

			//~: price string
			pce.setPriceNew(new BigDecimal(
			  EX.asserts(request().getParameter("priceNew" + i))
			));

			//?: {wrong price value}
			EX.assertx(pce.getPriceNew().scale() < 3);
			EX.assertx(CMP.grZero(pce.getPriceNew()));
		}

		//~: check for duplicated codes
		HashSet<String> codes = new HashSet<String>(changes.size());
		for(PriceChangeEdit pce : changes)
			if(codes.contains(pce.getGoodCode()))
				throw EX.state();
			else
				codes.add(pce.getGoodCode());

		//~: load | create the price change document
		RepriceDoc rd; if(isCreate())
		{
			rd = new RepriceDoc();

			//~: domain
			rd.setDomain(loadModelDomain());
		}
		//~: load the document
		else
			rd = EX.assertn(bean(GetPrices.class).
			  getRepriceDoc(getModel().getView().getObjectKey()
			));

		//~: check code exists & assign it
		String code = EX.asserts(getModel().getView().getCode());
		if(codeExists = checkCodeExists(code)) return null;
		rd.setCode(code);

		//~: assign the price list
		PriceListEntity pl = EX.assertn(bean(GetPrices.class).
		  getPriceList(getModel().getView().getPriceListKey()
		));

		//sec: check the domain of price list
		if(!getModel().domain().equals(pl.getDomain().getPrimaryKey()))
			throw EX.forbid();

		//~: assign the price list
		rd.setPriceList(pl);


		//!: run save | update action
		ActionsPoint.actionRun(
		  isCreate()?(ActionType.SAVE):(ActionType.UPDATE),
		  rd, Prices.REPRICE_EDIT, getModel().getView(),
		  ActionsPoint.UNITY_TYPE, Prices.TYPE_REPRICE_DOC
		);

		return null;
	}

	@SuppressWarnings("unchecked")
	public String doUpdateGoodsInfo()
	{
		//~: read the codes from the request
		HashSet<String> codeset = new HashSet<>(11);
		String[]        codeprm = request().getParameterValues("goodCode");
		if(codeprm != null) codeset.addAll(Arrays.asList(codeprm));
		if(codeset.isEmpty()) throw EX.arg();

		//~: get the target price list
		GetPrices       gg = bean(GetPrices.class);
		String          pp = SU.s2s(request().getParameter("priceList"));
		PriceListEntity pl;

		//?: {has no price list parameter} use current price list
		if(pp == null)
			pl = gg.getPriceList(getModel().getView().getPriceListKey());
		//!: update current price list
		else
		{
			pl = gg.getPriceList(Long.parseLong(pp));

			//sec: check the domain of price list
			if(!getModel().domain().equals(pl.getDomain().getPrimaryKey()))
				throw EX.forbid();

			getModel().getView().setPriceListKey(pl.getPrimaryKey());
		}


		//~: read the good units of interest
		List<String> codes = new ArrayList<>(codeset);
		HashMap      info  = new HashMap(codes.size());

		for(int i = 0;(i < codes.size());)
		{
			int  j = i + 32; if(j > codes.size()) j = codes.size();
			List x = gg.getPriceListPrices(pl, codes.subList(i, j));

			for(Object o : x)
			{
				GoodUnit gu = (GoodUnit)((Object[])o)[0];
				info.put(gu.getCode(), o);
			}

			i = j; //<-- advance to the next position
		}

		//~: create the good info string
		StringBuilder s = new StringBuilder(512);
		s.append('{');

		for(Object code : info.keySet())
		{
			Object[]    o = (Object[]) info.get(code);
			GoodUnit    g = (GoodUnit)    o[0];
			MeasureUnit m = (MeasureUnit) o[1];
			BigDecimal  p = (BigDecimal)  o[2];

			//~: good code
			s.append("'").append(SU.jss(g.getCode())).append("': [");

			//~: good name
			s.append("'").append(SU.jss(g.getName())).append("', ");

			//~: good group
			s.append("'").append(SU.jss(g.getGroup())).append("', ");

			//~: measure code
			s.append("'").append(SU.jss(m.getCode())).append("', ");

			//~: price
			s.append(p).append("], ");
		}

		updateGoodsInfo = s.append('}').toString();

		return null;
	}


	/* public: view interface */

	public RepriceDocEditModelBean getModel()
	{
		return (RepriceDocEditModelBean)super.getModel();
	}

	public boolean isCreate()
	{
		return (getModel().getView().getObjectKey() == null);
	}

	public boolean isFailed()
	{
		return codeExists;
	}

	private boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}

	public String getWinmainTitle()
	{
		return (isCreate())?("Создание документа изменения цен"):
		  formatTitle("Ред. документа изменения цен", getModel().getView().getCode());
	}

	public Map<String, String> getPriceListsLabels()
	{
		Map<String, String>   res = new LinkedHashMap<String, String>(11);

		List<PriceListEntity> pls = bean(GetPrices.class).
		  getPriceLists(getModel().domain());

		for(PriceListEntity pl : pls) res.put(
		  pl.getPrimaryKey().toString(),
		  Prices.getPriceListFullName(pl)
		);

		return res;
	}

	private String updateGoodsInfo;

	public String getUpdateGoodsInfo()
	{
		return updateGoodsInfo;
	}


	/* public: ModelProvider interface */

	public ModelBean provideModel()
	{
		if(!ModelRequest.isKey("goods"))
			return super.provideModel();

		GoodsModelBean mb = new GoodsModelBean();

		//~: domain
		mb.setDomain(getModel().domain());

		//~: search words
		mb.setSearchNames(SU.urld(request().getParameter("searchGoods")));

		return mb;
	}


	/* protected: actions support */

	protected boolean checkCodeExists(String code)
	{
		RepriceDoc rd = bean(GetPrices.class).
		  getRepriceDoc(getModel().domain(), code);

		Long       pk = getModel().getView().getObjectKey();

		return (rd != null) && (
		  (pk == null) || // <-- creating
		  !pk.equals(rd.getPrimaryKey())
		);
	}


	/* protected: UnityModelView interface */

	@SuppressWarnings("unchecked")
	protected ModelBean createModel()
	{
		RepriceDocEditModelBean mb = new RepriceDocEditModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//?: {creating mode}
		if("true".equals(request().getParameter("create")))
		{
			RepriceDocEdit rde = new RepriceDocEdit();
			mb.setView(rde);

			//~: generate code
			rde.setCode(Prices.createRepriceDocCode(loadDomain()));

			//~: set initial price list
			PriceListEntity pl  = bean(GetPrices.class).
			  getPriceListDefault(getDomainKey());

			if(pl != null) rde.setPriceListKey(pl.getPrimaryKey());

			return mb;
		}

		//~: obtain price change document to edit
		RepriceDoc rd = bean(GetPrices.class).
		  getRepriceDoc(obtainEntityKeyFromRequestStrict());

		//?: {not found it}
		if(rd == null) throw EX.state(
		  "Proce Change Document [", obtainEntityKeyFromRequest(), "] not found!");

		//sec: document of that domain
		if(!rd.getDomain().getPrimaryKey().equals(getDomainKey()))
			throw EX.forbid();

		//~: create the edit model
		mb.setView((RepriceDocEdit) new RepriceDocEdit().
		  init(Arrays.asList(rd, rd.getPriceList()))
		);

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof RepriceDocEditModelBean);
	}
}