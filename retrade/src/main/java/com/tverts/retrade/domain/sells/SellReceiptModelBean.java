package com.tverts.retrade.domain.sells;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.NumericModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.sells.SellReceiptModelData;


/**
 * Model bean to display goods of a {@link SellReceipt}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "sell-receipt-model")
public class SellReceiptModelBean extends NumericModelBean
{
	/* public: constructors */

	public SellReceiptModelBean()
	{}

	public SellReceiptModelBean(SellReceipt receipt)
	{
		setInstance(receipt);
	}


	/* Sell Receipt Model Bean (read) */

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


	/* Model Bean (data access) */

	public ModelData   modelData()
	{
		return new SellReceiptModelData(this);
	}
}