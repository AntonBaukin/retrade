package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: model */

import com.tverts.model.DataSelectModel;
import com.tverts.model.ModelData;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.sells.SellReceiptModelData;


/**
 * Model bean to display goods of a {@link SellReceipt}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "sell-receipt")
public class SellReceiptModelBean extends NumericModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public SellReceiptModelBean()
	{}

	public SellReceiptModelBean(SellReceipt receipt)
	{
		setInstance(receipt);
	}


	/* public: read interface */

	public SellReceipt sellReceipt()
	{
		return (SellReceipt) this.accessNumeric();
	}

	@XmlElement
	public Long        getObjectKey()
	{
		SellReceipt sr = sellReceipt();
		return (sr == null)?(null):(sr.getPrimaryKey());
	}


	/* public: ModelBean (data access) interface */

	public ModelData   modelData()
	{
		return new SellReceiptModelData(this);
	}

}