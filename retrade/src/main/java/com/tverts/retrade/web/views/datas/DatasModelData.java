package com.tverts.retrade.web.views.datas;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: data sources */

import com.tverts.data.DataSource;
import com.tverts.data.Datas;
import com.tverts.data.DataSourceView;

/* com.tverts: endure (reports) */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportTemplateView;


/**
 * Data provider for {@link DatasModelBean}.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "dataSources", "templatesNumber", "templates"
})
public class DatasModelData implements ModelData
{
	/* public: constructors */

	public DatasModelData()
	{}

	public DatasModelData(DatasModelBean model)
	{
		this.model = model;
	}


	/* public: GoodsModelData (bean) interface */

	@XmlElement
	public DatasModelBean getModel()
	{
		return model;
	}

	@XmlElementWrapper(name = "data-sources")
	@XmlElement(name = "data-source")
	@SuppressWarnings("unchecked")
	public List<DataSourceView> getDataSources()
	{
		if(!ModelRequest.isKey("sources"))
			return null;

		DataSource[] srcs = Datas.INSTANCE.copySources();
		List<DataSourceView> res = new ArrayList<DataSourceView>(srcs.length);

		for(DataSource ds : srcs)
			res.add(new DataSourceView().init(ds));

		return res;
	}

	@XmlElement(name = "templates-number")
	public Integer getTemplatesNumber()
	{
		if(!ModelRequest.isKey("templates"))
			return null;

		return bean(GetReports.class).countTemplates(model);
	}

	@XmlElementWrapper(name = "report-templates")
	@XmlElement(name = "report-template")
	@SuppressWarnings("unchecked")
	public List<ReportTemplateView> getTemplates()
	{
		if(!ModelRequest.isKey("templates"))
			return null;

		List list = bean(GetReports.class).selectTemplates(model);
		List res  = new ArrayList(list.size());

		for(Object rt : list)
			res.add(new ReportTemplateView().init(rt));

		return res;
	}


	/* private: model */

	private DatasModelBean model;
}