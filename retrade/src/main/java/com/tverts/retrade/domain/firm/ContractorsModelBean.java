package com.tverts.retrade.domain.firm;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ObjectsSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.firms.ContractorsModelData;


/**
 * Model bean for table with views on all the contractors.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class ContractorsModelBean extends ObjectsSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		ModelData md = super.modelData();
		return (md != null)?(md):(new ContractorsModelData(this));
	}
}