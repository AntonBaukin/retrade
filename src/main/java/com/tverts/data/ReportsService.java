package com.tverts.data;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (transaction) */

import com.tverts.system.tx.TxBean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure (auth + reports) */

import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportRequest;
import com.tverts.endure.report.ReportTemplate;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.OU;


/**
 * Service that takes Report Requests and
 * plans to execute them in the near future.
 *
 * @author anton.baukin@gmail.com
 */
public class ReportsService
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

	protected void plan(final ReportRequest r)
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
		EX.assertn(r.getPrimaryKey());
	}
}
