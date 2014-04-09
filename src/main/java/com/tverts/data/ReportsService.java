package com.tverts.data;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (services + tx) */

import com.tverts.support.streams.BytesStream;
import com.tverts.system.services.Event;
import com.tverts.system.services.ServiceBase;
import com.tverts.system.tx.TxBean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: models */

import com.tverts.model.ModelBean;

/* com.tverts: endure (auth + reports) */

import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportRequest;
import com.tverts.endure.report.ReportTemplate;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Service that takes Report Requests and
 * plans to execute them in the near future.
 *
 * @author anton.baukin@gmail.com
 */
public class ReportsService extends ServiceBase
{
	/* Reports Service Singleton */

	public static final ReportsService INSTANCE =
	  new ReportsService();

	public static ReportsService getInstance()
	{
		return INSTANCE;
	}

	private ReportsService()
	{}


	/* constants */

	public static final String SERVICE_UID =
	  "Reports Service";


	/* public: Reports Service */

	/**
	 * Executed in own transaction: saves the
	 * given Report Model and plans it's execution.
	 *
	 * Returns false if the Report Template denoted
	 * has no report file assigned, and no request
	 * may be created.
	 */
	public boolean plan(ReportModel m)
	{
		final ReportRequest r = new ReportRequest();

		//~: the template
		r.setTemplate(EX.assertn(
		  bean(GetReports.class).getReportTemplate(m.getTemplate()),
		  "Report Model [", m.getModelKey(), "] has unknown template assigned!"
		));

		//?: {template has no file}
		if(!r.getTemplate().isReady())
			return false;

		//~: auth login
		r.setOwner(EX.assertn(
		  bean(GetAuthLogin.class).getLogin(m.getLogin()),
		  "Report Model [", m.getModelKey(), "] has unknown login assigned!"
		));

		//~: report request time (now)
		r.setTime(new java.util.Date());

		//~: the model serialized as xml
		r.setModel(OU.obj2xml(m));

		//~: report format
		r.setFormat(m.getFormat());


		//!: plan the request
		plan(r);

		return true;
	}

	/**
	 * The same as the model planning, but is for
	 * the reports with the data sources without
	 * web configuration interface.
	 */
	public void    plan(ReportTemplate rt, ReportFormat fmt, DataCtx ctx)
	{
		final ReportRequest r = new ReportRequest();

		//~: the template
		r.setTemplate(rt);

		//?: {the template is not ready}
		EX.assertx(rt.isReady());

		//~: auth login
		r.setOwner(EX.assertn(
		  bean(GetAuthLogin.class).getLogin(ctx.getLogin()),
		  "No Auth Login [", ctx.getLogin(), "] is found!"
		));

		//~: report request time (now)
		r.setTime(new java.util.Date());

		//?: {has parameter object} assign as xml data
		if(ctx.getParams() != null)
			r.setModel(OU.obj2xml(ctx.getParams()));

		//~: report format
		r.setFormat(EX.assertn(fmt));

		//!: plan the request
		plan(r);
	}

	public void    service(Event event)
	{
		if(!(event instanceof MakeReportEvent))
			return;

		//~: load the request
		ReportRequest r = bean(GetReports.class).
		  getReportRequest(((MakeReportEvent)event).getReportRequest());

		//?: {that request does not exist now}
		if(r == null) return;

		//?: {the request is already done}
		if(r.getReady() != null) return;

		try
		{
			//?: {the template is not ready}
			EX.assertn(r.getTemplate().getTemplate(),
			  "Reports Template [", r.getTemplate().getCode(),
			  "] is not ready: has no report template file assigned!"
			);

			//~: get the data source
			DataSource src = Datas.source(r.getTemplate().getDid());

			//~: the model data
			Object model = (r.getModel() == null)?(null):
			  OU.xml2obj(r.getModel());

			//~: get the report data
			Object data; if(SU.sXe(src.getUiPath()))
				data = src.provideData(new DataCtx().init(r, model));
			else
			{
				//?: {not a model bean}
				if(!(model instanceof ModelBean)) throw EX.arg(
				  "Model given is not a Model Bean, but: [", LU.cls(model), "]!");

				data = src.provideData((ModelBean) model);
			}

			//?: {no data was created}
			EX.assertn(data, "Data Source [", src.did(),
			  "] was not able to provide data for requested model [",
			  LU.cls(model), "] of template: [", r.getTemplate().getCode(), "]!"
			);

			//?: {xml report is requested} take the resulting bytes
			if(ReportFormat.XML.equals(r.getFormat()))
				r.setReport(Datas.bytes(data));
			//~: make report in the external service
			else
			{
				//~: represent the data as a stream
				BytesStream stream = Datas.stream(data);

				//?: {has no bytes}
				EX.assertx(stream.length() != 0L,
				  "Reporting Subsystem was unable to convert data object of class [",
				  LU.cls(data), "] provided Data Source [", r.getTemplate().getDid(),
				  "] into an XML document!"
				);

				//~: make the report bytes
				r.setReport(makeReport(r.getTemplate().getTemplate(), stream));
			}
		}
		catch(Throwable e)
		{
			//~: mark the report as ready
			r.setFormat(ReportFormat.ERROR);

			//~: write the error bytes
			String stack = EX.print(e); try
			{
				r.setReport(stack.getBytes("UTF-8"));
			}
			catch(Exception e2)
			{}
		}

		//!: mark the report as ready
		r.setReady(r.getPrimaryKey());
	}


	/* protected: service internals */

	protected void   plan(final ReportRequest r)
	{
		//~: save the request in it's own transaction
		bean(TxBean.class).setNew().execute(new Runnable()
		{
			public void run()
			{
				actionRun(ActionType.SAVE, r);
			}
		});

		//~: send the event to this service
		self(new MakeReportEvent(r.getPrimaryKey()));
	}

	protected byte[] makeReport(byte[] template, BytesStream xml)
	{
		try
		{
			return xml.bytes();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Unexpected error occurred while making report ",
			  "in the external reporting server!"
			);
		}
		finally
		{
			xml.close();
		}
	}
}
