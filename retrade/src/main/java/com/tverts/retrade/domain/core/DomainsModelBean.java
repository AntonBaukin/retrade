package com.tverts.retrade.domain.core;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.system.DomainsModelData;


/**
 * Model bean for Domains table.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class DomainsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new DomainsModelData(this);
	}
}