package com.tverts.retrade.domain.invoice;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.InvoiceModelData;


/**
 * COMMENT InvoiceModelBean
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class InvoiceModelBean extends UnityModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceModelBean (support) interface */

	public Invoice   invoice()
	{
		return (Invoice)accessEntity();
	}


	/* public: InvoiceModelBean (configuration) interface */

	public boolean   isPositiveVolumeOnly()
	{
		return positiveVolumeOnly;
	}

	public void      setPositiveVolumeOnly(boolean positiveVolumeOnly)
	{
		this.positiveVolumeOnly = positiveVolumeOnly;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new InvoiceModelData(this);
	}


	/* private: model configuration */

	private boolean positiveVolumeOnly;
}