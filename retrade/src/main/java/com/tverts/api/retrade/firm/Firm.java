package com.tverts.api.retrade.firm;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A Contractor is being a Firm.
 */
@XmlType(name = "firm", propOrder = {
  "fullName", "taxNumber", "taxCode", "addressString", "phonesString"
})
public class Firm extends CatItem
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "full-name")
	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	@XmlElement(name = "tax-number")
	public String getTaxNumber()
	{
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber)
	{
		this.taxNumber = taxNumber;
	}

	@XmlElement(name = "tax-code")
	public String getTaxCode()
	{
		return taxCode;
	}

	public void setTaxCode(String taxCode)
	{
		this.taxCode = taxCode;
	}

	@XmlElement(name = "address-string")
	public String getAddressString()
	{
		return addressString;
	}

	public void setAddressString(String addressString)
	{
		this.addressString = addressString;
	}

	@XmlElement(name = "phones-string")
	public String getPhonesString()
	{
		return phonesString;
	}

	public void setPhonesString(String phonesString)
	{
		this.phonesString = phonesString;
	}


	/* attributes */

	private String fullName;
	private String taxNumber;
	private String taxCode;
	private String addressString;
	private String phonesString;
}