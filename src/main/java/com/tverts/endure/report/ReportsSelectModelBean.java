package com.tverts.endure.report;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;


/**
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement
public abstract class ReportsSelectModelBean
       extends        DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(String sortOrder)
	{
		this.sortOrder = sortOrder;
	}


	/* model attributes */

	private String sortOrder;
}