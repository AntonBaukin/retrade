package com.tverts.endure.report;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: endure (catalogues) */

import com.tverts.data.Datas;
import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;

/* com.tverts: data sources */

import com.tverts.data.DataSource;


/**
 * A view of Report Template (Catalogue Item).
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "report-template")
@XmlType(name = "report-template-view")
public class ReportTemplateView extends CatItemView
{
	public static final long serialVersionUID = 20150317L;


	/* Report Template View */

	public String getDid()
	{
		return did;
	}

	private String  did;

	public void setDid(String did)
	{
		this.did = did;
	}

	public boolean isSystem()
	{
		return system;
	}

	private boolean system;

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	public String getSourceName()
	{
		return sourceName;
	}

	private String  sourceName;

	public void setSourceName(String sourceName)
	{
		this.sourceName = sourceName;
	}

	public boolean isReady()
	{
		return ready;
	}

	private boolean ready;

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	public boolean isHasUI()
	{
		return hasUI;
	}

	private boolean hasUI;

	public void setHasUI(boolean hasUI)
	{
		this.hasUI = hasUI;
	}


	/* Initialization */

	public ReportTemplateView init(CatItem ci)
	{
		this.init((ReportTemplate) ci);
		return (ReportTemplateView) super.init(ci);
	}

	public ReportTemplateView init(ReportTemplate rt)
	{
		this.did    = rt.getDid();
		this.system = rt.isSystem();
		this.ready  = rt.isReady();

		//~: data source
		DataSource src = Datas.INSTANCE.getSource(this.did);
		this.sourceName = (src == null)?(null):(src.getNameLo());
		this.hasUI = (src != null) && (src.getUiPath() != null);

		return this;
	}
}