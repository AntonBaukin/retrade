package com.tverts.api.clients;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * Organization, also named as Firm.
 */
@XmlRootElement(name = "firm")
@XmlType(name = "firm", propOrder = {
  "fullName", "taxNumber", "taxCode",
  "addressString", "phonesString"
})
public class Firm extends CatItem
{
	@XmlElement(name = "full-name")
	public String getFullName()
	{
		return fullName;
	}

	private String fullName;

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	@XmlElement(name = "tax-number")
	public String getTaxNumber()
	{
		return taxNumber;
	}

	private String taxNumber;

	public void setTaxNumber(String taxNumber)
	{
		this.taxNumber = taxNumber;
	}

	@XmlElement(name = "tax-code")
	public String getTaxCode()
	{
		return taxCode;
	}

	private String taxCode;

	public void setTaxCode(String taxCode)
	{
		this.taxCode = taxCode;
	}

	@XmlElement(name = "address-string")
	public String getAddressString()
	{
		return addressString;
	}

	private String addressString;

	public void setAddressString(String addressString)
	{
		this.addressString = addressString;
	}

	@XmlElement(name = "phones-string")
	public String getPhonesString()
	{
		return phonesString;
	}

	private String phonesString;

	public void setPhonesString(String phonesString)
	{
		this.phonesString = phonesString;
	}
}