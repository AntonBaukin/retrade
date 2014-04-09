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

	public static void report(ReportModel m)
	{
		INSTANCE.plan(EX.assertn(m));
	}

	/**
	 * Executed in own transaction: saves the
	 * given Report Model and plans it's execution.
	 */
	public void        plan(ReportModel m)
	{
		//<: create the report request

		final ReportRequest r = new ReportRequest();

		//~: the template
		r.setTemplate(EX.assertn(
		  bean(GetReports.class).getReportTemplate(m.getTemplate()),
		  "Report Model [", m.getModelKey(), "] has unknown template assigned!"
		));

		//~: auth login
		r.setOwner(EX.assertn(
			 bean(GetAuthLogin.class).getLogin(m.getLogin()),
			 "Report Model [", m.getModelKey(), "] has unknown login assigned!"
		  )
		);

		//~: report request time (now)
		r.setTime(new java.util.Date());

		//~: the model serialized as xml
		r.setModel(OU.obj2xml(m));

		//~: report format
		r.setFormat(m.getFormat());

		//>: create the report request


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
