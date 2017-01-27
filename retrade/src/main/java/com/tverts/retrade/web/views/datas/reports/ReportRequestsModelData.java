package com.tverts.retrade.web.views.datas.reports;

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

/* com.tverts: endure (reports) */

import com.tverts.endure.report.GetReports;
import com.tverts.endure.report.ReportRequestView;


/**
 * Data of the model with Reports Requests.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {
  "model", "requestsNumber", "requests"
})
public class ReportRequestsModelData implements ModelData
{
	/* public: constructors */

	public ReportRequestsModelData()
	{}

	public ReportRequestsModelData(ReportRequestsModelBean model)
	{
		this.model = model;
	}


	/* public: GoodsModelData (bean) interface */

	@XmlElement
	public ReportRequestsModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "requestsNumber")
	public int getRequestsNumber()
	{
		return bean(GetReports.class).countRequests(model);
	}

	@XmlElementWrapper(name = "report-requests")
	@XmlElement(name = "report-request")
	@SuppressWarnings("unchecked")
	public List<ReportRequestView> getRequests()
	{
		List list = bean(GetReports.class).selectRequests(model);
		List res  = new ArrayList(list.size());

		for(Object r : list)
			res.add(new ReportRequestView().init(r));

		return res;
	}



	/* private: model */

	private ReportRequestsModelBean model;
}