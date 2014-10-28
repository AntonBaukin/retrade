package com.tverts.retrade.domain.firm;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
@XmlType(name = "contractors-model")
public class ContractorsModelBean extends DataSelectModelBean
{
	/* Model Bean (data access) */

	public ModelData modelData()
	{
		ModelData md = super.modelData();

		//?: {data provider was not overwritten}
		return (md != null)?(md):(new ContractorsModelData(this));
	}
}