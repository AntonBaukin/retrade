package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.goods.MeasureUnitModelBean;
import com.tverts.retrade.domain.goods.MeasureUnitView;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the Measure Units table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesMeasuresView extends ModelView
{
	/* actions */

	public String gotoEditMeasureUnit()
	{
		//~: load the measure unit
		Long        pk = obtainEntityKeyFromRequestStrict();
		MeasureUnit mu = bean(GetGoods.class).getMeasureUnit(pk);
		if(mu == null) throw EX.state(
		  "Measure Unit [", pk, "] is not found!");

		//sec: entity of else domain
		if(!mu.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid("Measure Unit of else Domain!");

		//~: create and init the view
		getModel().setView(new MeasureUnitView().init(mu));

		return "edit";
	}

	public String gotoCreateMeasureUnit()
	{
		//~: create empty view
		getModel().setView(new MeasureUnitView());

		//~: default class unit
		getModel().getView().setClassUnit(
		  new java.math.BigDecimal("1.00"));

		return "create";
	}

	public String gotoCancelEdit()
	{
		getModel().setView(null);
		return "cancel";
	}

	public String doCommitEdit()
	{
		if(SU.sXe(getMeasureView().getCode()))
			throw EX.arg("Measure code must be defined!");
		if(SU.sXe(getMeasureView().getName()))
			throw EX.arg("Measure name must be defined!");

		//~: check the code exists
		formValid = !checkCodeExists(getMeasureView().getCode());
		if(!formValid) return null;

		//?: {is not fractional} set default class unit
		if(!getMeasureView().isFractional())
			getMeasureView().setClassUnit(
			  java.math.BigDecimal.ONE.setScale(2));

		MeasureUnit mu;

		//?: {create new measure}
		if(getMeasureView().getObjectKey() == null)
		{
			mu = new MeasureUnit();

			//~: domain
			mu.setDomain(loadModelDomain());
		}
		//!: load it
		else
		{
			mu = bean(GetGoods.class).
			  getMeasureUnit(getMeasureView().getObjectKey());

			if(mu == null) throw EX.state();
		}

		//~: code
		mu.setCode(getMeasureView().getCode());

		//~: name
		mu.setName(getMeasureView().getName());

		//~: class code
		mu.setClassCode(getMeasureView().getClassCode());

		//~: class unit
		mu.setClassUnit(getMeasureView().getClassUnit());
		if(mu.getClassUnit() == null)
			mu.setClassUnit(new java.math.BigDecimal("1.00"));

		//~: fractional flag
		mu.setFractional(getMeasureView().isFractional());


		//!: save | update it
		if(getMeasureView().getObjectKey() == null)
			actionRun(ActionType.SAVE, mu);
		else
			actionRun(ActionType.UPDATE, mu);

		return null;
	}

	public String doCheckCodeExists()
	{
		String code = SU.s2s(request().getParameter("code"));

		//~: check the code exists
		formValid = (code == null) || !checkCodeExists(code);
		return null;
	}


	/* public: view interface */

	public MeasureUnitModelBean getModel()
	{
		return (MeasureUnitModelBean) super.getModel();
	}

	public MeasureUnitView getMeasureView()
	{
		if(getModel().getView() == null)
			throw EX.state("Edit view of Measure Unit not present!");
		return getModel().getView();
	}

	public String getEditWindowTitle()
	{
		if(getMeasureView().getObjectKey() == null)
			return "Создание единицы измерения";

		return SU.cats(
		  "Единица измерения [",
		  getMeasureView().getCode(), "] ",
		  getMeasureView().getName()
		);
	}

	private boolean formValid;

	public boolean isFormValid()
	{
		return formValid;
	}

	private boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}


	/* protected: actions support */

	protected boolean checkCodeExists(String code)
	{
		MeasureUnit mu = bean(GetGoods.class).
		  getMeasureUnit(getModel().domain(), code);

		return codeExists = (mu != null) && (
		  (getMeasureView().getObjectKey() == null) || // <-- creating
		  !getMeasureView().getObjectKey().equals(mu.getPrimaryKey())
		);
	}


	/* protected: ModelView interface */

	protected MeasureUnitModelBean createModel()
	{
		MeasureUnitModelBean mb = new MeasureUnitModelBean();

		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof MeasureUnitModelBean);
	}
}