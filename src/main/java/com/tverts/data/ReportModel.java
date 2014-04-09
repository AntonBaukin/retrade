package com.tverts.data;

/* com.tverts: models */

import com.tverts.model.ModelBean;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.ReportFormat;


/**
 * UI Model that is for creating Report Requests.
 *
 * @author anton.baukin@gmail.com.
 */
public interface ReportModel extends ModelBean
{
	/* public: Report Model interface */

	public Long         getLogin();

	public String       getDataSource();

	public Long         getTemplate();

	public ReportFormat getFormat();
}