package com.tverts.data;

/* Java */

import java.util.List;

/* com.tverts: servlets */

import com.tverts.endure.report.GetReports;
import com.tverts.objects.Result;
import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts.system: services */

import com.tverts.system.services.ServicesPoint;


/**
 * This startup listener find all the Report
 * Requests not executed till now and plans them.
 *
 * @author anton.baukin@gmail.com.
 */
public class PlanReportsMaking extends ServletContextListenerBase
{
	protected void init()
	{
		final Result<List<Long>> reports =
		  new Result<List<Long>>();

		//~: select the reports
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				reports.set(bean(GetReports.class).selectReportsToMake());
			}
		});

		//c: send event for each of them
		for(Long id : reports.get())
			ServicesPoint.send(Datas.SERVICE, new MakeReportEvent(id));
	}
}