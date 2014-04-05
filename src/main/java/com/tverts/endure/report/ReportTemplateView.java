package com.tverts.endure.report;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;


/**
 * A view of Report Template (Catalogue Item).
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "report-template")
public class ReportTemplateView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* public: did */

	public String getDid()
	{
		return did;
	}

	public void setDid(String did)
	{
		this.did = did;
	}

	public boolean isSystem()
	{
		return system;
	}

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* public: initialization */

	public ReportTemplateView init(CatItem ci)
	{
		this.init((ReportTemplate) ci);
		return (ReportTemplateView) super.init(ci);
	}

	public ReportTemplateView init(ReportTemplate rt)
	{
		this.did = rt.getDid();
		this.system = rt.isSystem();
		this.remarks = rt.getRemarks();

		return this;
	}


	/* view attributes */

	private String  did;
	private boolean system;
	private String  remarks;
}