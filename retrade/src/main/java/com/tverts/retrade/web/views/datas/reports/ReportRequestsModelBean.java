package com.tverts.retrade.web.views.datas.reports;

/* Java */

import java.io.IOException;

/* Java Servlet */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring + transactions */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: servlets */

import com.tverts.servlet.Download;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportRequest;
import com.tverts.endure.report.ReportsSelectModelBean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Model with the Report Requests of the current user.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
@XmlType(name = "report-requests-model")
public class      ReportRequestsModelBean
       extends    ReportsSelectModelBean
       implements Download
{
	public static final long serialVersionUID = 0L;


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new ReportRequestsModelData(this);
	}


	/* public: Binary Source */

	public void download(HttpServletRequest req, HttpServletResponse res)
	  throws IOException, ServletException
	{
		//~: select the report request
		Long pk = Long.parseLong(EX.asserts(req.getParameter("reportRequest")));
		ReportRequest r = bean(GetReports.class).getReportRequest(pk);
		EX.assertn(r, "Report Request [", pk, "] is not found!");

		//sec: {request of this user}
		if(!r.getAuthSession().getLogin().getPrimaryKey().equals(SecPoint.login()))
			throw EX.forbid();

		//?: {request is not ready yet}
		byte[] file = r.getReport(); if(file == null)
		{
			res.sendError(HttpServletResponse.SC_NO_CONTENT,
			  "Report is not ready yet!"
			);
			return;
		}

		//~: report content type
		res.setContentType(r.getFormat().contentType());

		//~: dialog to save it
		res.setHeader("Content-Disposition", "attachment;");

		//~: write the file bytes
		res.setContentLength(file.length);
		res.getOutputStream().write(file);

		//~: mark the report as downloaded
		if(r.getLoadTime() == null)
			r.setLoadTime(new java.util.Date());
		TxPoint.txn(r);
	}
}