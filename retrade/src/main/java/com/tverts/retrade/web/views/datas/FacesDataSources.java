package com.tverts.retrade.web.views.datas;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.param;

/* com.tverts: faces */

import com.tverts.data.ReportFormat;
import com.tverts.data.ReportsService;
import com.tverts.faces.ModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: data sources */

import com.tverts.data.DataCtx;
import com.tverts.data.DataSource;
import com.tverts.data.Datas;

/* com.tverts: endure report */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportTemplate;

/* com.tverts: support */

import com.tverts.servlet.REQ;
import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of the data sources and the reports tables.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesDataSources extends ModelView
{
	/* actions */

	public String doInvokeXML()
	{
		//~: get the data source referred
		String     did = this.dataSource =
		  EX.assertn(SU.s2s(param("dataSource")));

		DataSource src = Datas.source(did);

		//?: {the source has configuration interface} open it
		if(src.getUiPath() != null)
		{
			this.openWindow = src.getUiPath();
			this.dataOnly = true;
		}
		//~: directly invoke the source
		else
		{
			//~: invoke the data source
			Object data = src.provideData(new DataCtx().init());

			//?: {has no data}
			if(data == null)
			{
				this.errorText = SU.cats("Источник данных [", did,
				  "] не поддерживает интерфейс конфигурации, и при вызове ",
				  "вернул пустой объект с данными!"
				);

				return null;
			}

			//~: request the report in XML format
			try
			{
				byte[] bytes = Datas.bytes(data);
				getModel().setXMLData(bytes);
			}
			catch(Throwable e)
			{
				this.errorText = SU.cats("Источник данных [", did,
				  "] не поддерживает интерфейс конфигурации, и при вызове ",
				  "произошла ошибка!"
				);
			}

			//~: file time + file name
			String t = DU.datetime2str(new java.util.Date()).replace(':', '-');
			String n = "Данные " + src.getNameLo().
			  replaceAll("[:\\/\\\\]", "-");

			//~: create the download link
			this.downloadLink = SU.cats(
			  "/download/", n, " от ", t,
			  ".xml?model=", getModel().getModelKey()
			);
		}

		return null;
	}

	public String doCreateReport()
	{
		//~: load the template
		Long pk = Long.parseLong(EX.asserts(param("reportTemplate")));
		ReportTemplate rt = EX.assertn(bean(GetReports.class).getReportTemplate(pk));

		//sec: check the domain
		if(!rt.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid();

		//?: {this template has no file}
		if(!rt.isReady())
		{
			this.errorText = SU.cats("Невозможно создать отчёт, ",
			  "поскольку файл шаблона '", rt.getName(), "' не указан!");
			return null;
		}

		//~: the template data source
		DataSource src = Datas.source(rt.getDid());

		//~: the report format
		ReportFormat fmt = ReportFormat.PDF;
		if("xls".equalsIgnoreCase(param("reportFormat")))
			fmt = ReportFormat.XLS;

		//?: {the source has configuration interface} open it
		if(src.getUiPath() != null)
			this.openWindow = REQ.param(src.getUiPath(),
			  "reportTemplate", pk, "reportFormat", fmt.name()
			);
		//~: just save the report request
		else
			ReportsService.INSTANCE.plan(rt, fmt, new DataCtx().init());

		return null;
	}

	public String doSearchTemplates()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(param("searchWords")));

		return null;
	}

	public String doDeleteTemplate()
	{
		//~: load the template
		Long pk = Long.parseLong(EX.asserts(param("reportTemplate")));
		ReportTemplate rt = EX.assertn(bean(GetReports.class).getReportTemplate(pk));

		//sec: check the domain
		if(!rt.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid();

		//?: {has related requests}
		if(bean(GetReports.class).hasReportRequests(rt.getPrimaryKey()))
		{
			errorText = "Выбранный шаблон удалить невозможно, так как " +
			  "существуют запросы генерации, связанные с ним!";
			return null;
		}

		//!: delete
		actionRun(ActionType.DELETE, rt);

		return null;
	}


	/* public: the view */

	public boolean isValid()
	{
		return SU.sXe(errorText);
	}

	public String  getErrorText()
	{
		return errorText;
	}

	public String  getDownloadLink()
	{
		return downloadLink;
	}

	public String  getOpenWindow()
	{
		return openWindow;
	}

	public boolean isDataOnly()
	{
		return dataOnly;
	}

	public String  getDataSource()
	{
		return dataSource;
	}


	/* protected: ModelView interface */

	public DatasModelBean getModel()
	{
		return (DatasModelBean) super.getModel();
	}

	protected DatasModelBean createModel()
	{
		DatasModelBean mb = new DatasModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof DatasModelBean);
	}


	/* private: the view state */

	private String  errorText;
	private String  downloadLink;
	private String  openWindow;
	private String  dataSource;
	private boolean dataOnly;
}