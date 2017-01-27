package com.tverts.retrade.web.views.datas.inoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.param;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: data sources */

import com.tverts.data.DataCtx;
import com.tverts.data.DataSource;
import com.tverts.data.Datas;
import com.tverts.data.ReportFormat;
import com.tverts.data.ReportsService;
import com.tverts.data.models.AdaptedEntitiesSelected;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportTemplate;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.GetInvoice;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade (views) */

import com.tverts.retrade.web.views.datas.DatasModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Faces back bean for Data Source window
 * selecting Buy and Sell Invoices with
 * {@link AdaptedEntitiesSelected} model.
 *
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesInvoiceDataBuySell extends ModelView
{
	/* actions */

	public String doSubmit()
	{
		//~: get the invoice
		Long    pk = Long.parseLong(EX.assertn(SU.s2s(param("selectedInvoice"))));
		Invoice i  = EX.assertn(bean(GetInvoice.class).getInvoice(pk));

		//sec: {invoice is on the same domain}
		if(!i.getDomain().getPrimaryKey().equals(getModel().domain()))
			throw EX.forbid();

		//sec: {can read this invoice}
		if(!isSecureEntity(i, "view"))
			throw EX.forbid();

		//~: select the invoice to the model
		getModel().setEntity(pk);

		//?: {report is requested}
		if(findRequestedModel(DatasModelBean.class) == null)
			EX.assertx(ReportsService.INSTANCE.plan(getModel()));
		//~: xml data requested
		else
		{
			//~: assign the xml data
			DatasModelBean dm = findRequestedModel(DatasModelBean.class);
			dm.setXMLData(Datas.bytes(getModel()));

			//~: invoice title
			String title = Invoices.getInvoiceTitleFull(i).
			  replace('/', '-');

			//~: create the download link
			this.downloadLink = SU.cats(
			  "/download/", title,
			  ".xml?model=", dm.getModelKey()
			);
		}

		return null;
	}


	/* public: the view */

	public boolean isValid()
	{
		return SU.sXe(errorText);
	}

	private String errorText;

	public String getErrorText()
	{
		return errorText;
	}

	public String getDownloadLink()
	{
		return downloadLink;
	}

	private String downloadLink;

	public boolean isFormatPDF()
	{
		return ReportFormat.PDF.equals(getModel().getFormat());
	}

	public void setFormatPDF(boolean v)
	{
		if(v) getModel().setFormat(ReportFormat.PDF);
	}

	public boolean isFormatXLS()
	{
		return ReportFormat.XLS.equals(getModel().getFormat());
	}

	public void setFormatXLS(boolean v)
	{
		if(v) getModel().setFormat(ReportFormat.XLS);
	}


	/* protected: ModelView interface */

	public AdaptedEntitiesSelected getModel()
	{
		return (AdaptedEntitiesSelected) super.getModel();
	}

	protected AdaptedEntitiesSelected createModel()
	{
		DataSource              src;
		AdaptedEntitiesSelected mb;
		ReportFormat            fmt;
		ReportTemplate          rt = null;

		//?: {this is a data source request}
		if(!SU.sXe(param("dataSource")))
		{
			src = Datas.source(EX.assertn(SU.s2s(param("dataSource"))));
			fmt = ReportFormat.XML;
		}
		//?: {this is a report template request}
		else if(!SU.sXe(param("reportTemplate")))
		{
			//~: get the report template requested
			rt = EX.assertn(bean(GetReports.class).getReportTemplate(
			  Long.parseLong(EX.assertn(SU.s2s(param("reportTemplate"))))));

			//sec: {has not the same domain}
			if(!rt.getDomain().getPrimaryKey().equals(getDomainKey()))
				throw EX.forbid();

			//~: get the data source
			src = Datas.source(rt.getDid());

			//?: {has report parameter}
			if(!SU.sXe(param("reportFormat")))
				fmt = EX.assertn(ReportFormat.valueOf(param("reportFormat")));
			else
				fmt = ReportFormat.PDF;
		}
		else
			throw EX.state();

		//~: create the model
		mb = (AdaptedEntitiesSelected) src.createModel(new DataCtx().init());

		//~: domain
		mb.setDomain(getDomainKey());

		//~: report format
		mb.setFormat(fmt);

		//~: report template
		if(rt != null)
			mb.setTemplate(rt.getPrimaryKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof AdaptedEntitiesSelected);
	}
}