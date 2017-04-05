package com.tverts.retrade.web.views.goods;

/* JavaServer Faces */

import java.math.BigDecimal;

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
import com.tverts.model.SimpleModelBean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Measure;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.goods.MeasureUnitView;

/* com.tverts: retrade data (goods) */

import com.tverts.retrade.web.data.goods.MeasureUnitsModelData;

/* com.tverts: support */

import com.tverts.support.CMP;
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
		getModel().put(MeasureUnitView.class,
		  new MeasureUnitView().init(mu)
		);

		return "edit";
	}

	public String gotoCreateMeasureUnit()
	{
		//~: create empty view
		getModel().put(MeasureUnitView.class, new MeasureUnitView());

		//~: default class unit
		//getMeasureView().setClassUnit(
		//  new java.math.BigDecimal("1.00")
		//);

		return "create";
	}

	public String gotoCancelEdit()
	{
		getModel().put(MeasureUnitView.class, null);
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

		Measure m = mu.getOx();

		//~: code
		m.setCode(getMeasureView().getCode());

		//~: name
		m.setName(getMeasureView().getName());

		//~: class code
		m.setClassCode(getMeasureView().getClassCode());

		//~: fractional flag
		m.setFractional(getMeasureView().isFractional());

		//~: class unit
		m.setClassUnit(getMeasureView().getClassUnit());
		if(m.getClassUnit() == null) if(m.isFractional())
			m.setClassUnit(BigDecimal.ONE.setScale(1));
		else
			m.setClassUnit(BigDecimal.ONE.setScale(0));
		EX.assertx(CMP.grZero(m.getClassUnit()));

		//?: {is integer} set zero scale
		if(!m.isFractional())
			m.setClassUnit(m.getClassUnit().setScale(0));
		else if(m.getClassUnit().scale() <= 0)
			m.setClassUnit(m.getClassUnit().setScale(1));


		//!: update the ox-measure
		mu.updateOx();

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

	public SimpleModelBean getModel()
	{
		return (SimpleModelBean) super.getModel();
	}

	public MeasureUnitView getMeasureView()
	{
		return EX.assertn(
		  getModel().getx(MeasureUnitView.class),
		  "Edit view of Measure Unit not present!"
		);
	}

	public String getEditWindowTitle()
	{
		if(getMeasureView().getObjectKey() == null)
			return "Создание единицы измерения";

		return formatTitle("Ед. изм.",
		  getMeasureView().getCode(),
		  getMeasureView().getName()
		);
	}

	private Boolean formValid;

	public boolean isFormValid()
	{
		return !Boolean.FALSE.equals(formValid);
	}

	private boolean codeExists;

	public boolean isCodeExists()
	{
		return codeExists;
	}

	public boolean isCreate()
	{
		return (getMeasureView().getObjectKey() == null);
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

	protected SimpleModelBean createModel()
	{
		SimpleModelBean mb = new SimpleModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//~: marker
		mb.put(MeasureUnit.class, true);

		//~: data with the measures list
		mb.setDataClass(MeasureUnitsModelData.class);

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SimpleModelBean) &&
		  Boolean.TRUE.equals(((SimpleModelBean)model).get(MeasureUnit.class));
	}
}