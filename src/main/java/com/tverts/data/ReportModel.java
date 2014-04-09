package com.tverts.data;

/* com.tverts: models */

import com.tverts.model.ModelBean;


/**
 * UI Model that is for creating Report Requests.
 *
 * @author anton.baukin@gmail.com.
 */
public interface ReportModel extends ModelBean
{
	/* public: Report Model interface */

	public String       getSecSession();

	public String       getDataSource();

	public Long         getTemplate();

	public ReportFormat getFormat();
}