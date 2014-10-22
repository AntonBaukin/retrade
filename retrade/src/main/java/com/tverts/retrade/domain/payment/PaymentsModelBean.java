package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.other.PaymentsModelData;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.jaxb.DateAdapter;


/**
 * Model to display payments table in various projections.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class PaymentsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getMinDate()
	{
		return minDate;
	}

	public void setMinDate(Date minDate)
	{
		if(minDate != null)
			minDate = DU.cleanTime(minDate);
		this.minDate = minDate;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getMaxDate()
	{
		return maxDate;
	}

	public void setMaxDate(Date maxDate)
	{
		if(maxDate != null)
			maxDate = DU.lastTime(maxDate);
		this.maxDate = maxDate;
	}

	public String getProjection()
	{
		return projection;
	}

	public void setProjection(String projection)
	{
		this.projection = projection;
	}

	public Map<String, String> getProjectionsLabels()
	{
		if(projectionsLabels != null)
			return projectionsLabels;

		Map<String, String> r = new LinkedHashMap<String, String>(3);

		r.put("gen",      "Обобщённая сводка");
		r.put("con",      "Операции с контрагентами");
		r.put("conBank",  "Банковские платежи контрагентов");

		//r.put("bank", "Банковские платежи");
		//r.put("cash", "Наличные платежи");
		//r.put("pos",  "Сессии POS-продаж");

		return projectionsLabels = r;
	}

	public boolean isWithIncome()
	{
		return withIncome;
	}

	public void setWithIncome(boolean withIncome)
	{
		this.withIncome = withIncome;
	}

	public boolean isWithExpense()
	{
		return withExpense;
	}

	public void setWithExpense(boolean withExpense)
	{
		this.withExpense = withExpense;
	}

	public String getModelFlags()
	{
		return modelFlags;
	}

	public void setModelFlags(String modelFlags)
	{
		this.modelFlags = modelFlags;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PaymentsModelData(this);
	}


	/* private: model attributes */

	private Date    minDate;
	private Date    maxDate;
	private String  projection;
	private boolean withIncome  = true;
	private boolean withExpense = true;
	private String  modelFlags;

	private Map<String, String> projectionsLabels;
}