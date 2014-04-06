package com.tverts.endure.report;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;


/**
 * Model to create and edit Report template.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
public class ReportTemplateModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public ReportTemplateView getView()
	{
		return (view != null)?(view):(view = new ReportTemplateView());
	}

	public void setView(ReportTemplateView view)
	{
		this.view = view;
	}

	public String getDid()
	{
		return did;
	}

	public void setDid(String did)
	{
		this.did = did;
	}

	public String getDataSourceName()
	{
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName)
	{
		this.dataSourceName = dataSourceName;
	}


	/* the report template view  */

	private ReportTemplateView view;
	private String             did;
	private String             dataSourceName;
}