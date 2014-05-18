package com.tverts.retrade.domain.firm;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.firms.ContractorsModelData;


/**
 * Model bean for table with views on all the contractors.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class ContractorsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: ContractorsModelBean (bean) interface */

	public String[] getSearchNames()
	{
		return searchNames;
	}

	public void     setSearchNames(String[] searchNames)
	{
		this.searchNames = searchNames;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new ContractorsModelData(this);
	}


	/* private: model attributes */

	private String[] searchNames;
}