package com.tverts.retrade.web.views.datas;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.param;

/* com.tverts: faces */

import com.tverts.endure.report.ReportTemplateView;
import com.tverts.faces.ModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: data sources */

import com.tverts.data.DataSource;
import com.tverts.data.Datas;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportTemplate;
import com.tverts.endure.report.ReportTemplateModelBean;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Faces back bean to create and edit
 * Report Template Instances.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesReportTemplateEdit extends ModelView
{
	/* actions */

	public String doCheckCodeExists()
	{
		String code = SU.s2s(param("code"));

		//~: check the code exists
		codeExists = (code == null) || checkCodeExists(code);

		return null;
	}

	public String doCommitEdit()
	{
		//~: check the code exists
		codeExists = SU.sXe(getView().getCode()) ||
		  checkCodeExists(SU.s2s(getView().getCode()));
		if(codeExists) return null;

		//?: {creating}
		ReportTemplate rt; if(isCreate())
			rt = new ReportTemplate();
		//~: load existing template
		else
		{
			rt = EX.assertn(bean(GetReports.class).
				 getReportTemplate(getView().getObjectKey())
			);

			//sec: of the same domain
			if(!rt.getDomain().getPrimaryKey().equals(getModel().domain()))
				throw EX.forbid();
		}

		//~: domain
		rt.setDomain(loadModelDomain());

		//~: code
		rt.setCode(getView().getCode());

		//~: name
		rt.setName(getView().getName());

		//~: remarks
		rt.setRemarks(getView().getRemarks());

		//~: data source id
		if(!SU.sXe(did)) rt.setDid(did); else
		{
			rt.setDid(getView().getDid());
			if(SU.sXe(rt.getDid()))
				rt.setDid(EX.asserts(getModel().getDid()));
		}

		//~: this template is used-defined
		rt.setSystem(false);

		//?: {has template file bytes}
		if(getModel().getTemplate() != null)
		{
			rt.setTemplate(getModel().getTemplate());

			if(SU.sXe(rt.getRemarks()) && !SU.sXe(getModel().getFileName()))
				rt.setRemarks(SU.cats(
				  "Установлен файл шаблона «",
				   getModel().getFileName(), "»."
				));
		}


		//!: create | update
		if(isCreate())
			actionRun(ActionType.SAVE, rt);
		else
			actionRun(ActionType.UPDATE, rt);

		return null;
	}


	/* public: the view */

	public boolean isValid()
	{
		return SU.sXe(errorText) && !codeExists;
	}

	public String getErrorText()
	{
		return errorText;
	}

	public boolean isCodeExists()
	{
		return codeExists;
	}

	public ReportTemplateView getView()
	{
		return getModel().getView();
	}

	public boolean isCreate()
	{
		return (getView().getObjectKey() == null);
	}

	public String getWindowTitle()
	{
		if(isCreate())
			return "Создание шаблона отчёта";
		return SU.formatTitle("Ред. шаблона отчёта: ", getView().getCode());
	}

	public String getDid()
	{
		return SU.sXe(getView().getDid())?(getModel().getDid()):(getView().getDid());
	}

	public void setDid(String did)
	{
		this.did = did;
	}

	public boolean isAllowDid()
	{
		return !isCreate() && (getModel().getDid() != null) &&
		  !CMP.eq(getView().getDid(), getModel().getDid());
	}

	public boolean isResource()
	{
		return resource;
	}

	public void setResource(boolean resource)
	{
		this.resource = resource;
	}


	/* protected: actions support */

	protected boolean checkCodeExists(String code)
	{
		ReportTemplate rt = bean(GetReports.class).
		  getReportTemplate(getModel().domain(), code);

		return (rt != null) && (
		  (getView().getObjectKey() == null) || // <-- creating
		  !getView().getObjectKey().equals(rt.getPrimaryKey())
		);
	}


	/* protected: ModelView interface */

	public ReportTemplateModelBean getModel()
	{
		return (ReportTemplateModelBean) super.getModel();
	}

	protected ReportTemplateModelBean createModel()
	{
		ReportTemplateModelBean mb = new ReportTemplateModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		//?: {NOT creating} take the existing report template
		if(!"true".equals(param("create")))
		{
			//~: load the report template
			ReportTemplate rt = bean(GetReports.class).
			  getReportTemplate(obtainEntityKeyFromRequestStrict());

			//?: {not found}
			EX.assertn(rt, "Report Template specified is not found!");

			//~: initialize the view
			mb.getView().init((Object) rt);
		}

		//?: {has data source parameter}
		if(param("dataSource") != null)
		{
			DataSource src = Datas.source(param("dataSource"));

			mb.setDid(src.did());
			mb.setDataSourceName(src.getNameLo());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ReportTemplateModelBean);
	}


	/* private: the view state */

	private String  errorText;
	private boolean codeExists;
	private String  did;
	private boolean resource;
}