package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.RepriceDocModelData;


/**
 * Model bean for read-only view of
 * {@link RepriceDoc} document.
 *
 * @author anton.baukin@gmail.com
 */
public class RepriceDocModelBean extends NumericModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public RepriceDocModelBean()
	{}

	public RepriceDocModelBean(RepriceDoc rd)
	{
		this.setInstance(rd);
	}


	/* public: RepriceDocModelBean (read) interface */

	public RepriceDoc repriceDoc()
	{
		return (RepriceDoc)accessNumeric();
	}

	@XmlElement
	public Long       getObjectKey()
	{
		return (repriceDoc() == null)?(null):
		  (repriceDoc().getPrimaryKey());
	}


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return new RepriceDocModelData(this);
	}
}