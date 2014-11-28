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
  "phones", "registryAddress", "contactAddress"
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

	public String getPhones()
	{
		return phones;
	}

	private String phones;

	public void setPhones(String phones)
	{
		this.phones = phones;
	}

	@XmlElement(name = "registry-address")
	public Address getRegistryAddress()
	{
		return registryAddress;
	}

	private Address registryAddress;

	public void setRegistryAddress(Address registryAddress)
	{
		this.registryAddress = registryAddress;
	}

	/**
	 * Contact address assigned when it
	 * differs from the registration one.
	 */
	@XmlElement(name = "contact-address")
	public Address getContactAddress()
	{
		return contactAddress;
	}

	private Address contactAddress;

	public void setContactAddress(Address contactAddress)
	{
		this.contactAddress = contactAddress;
	}
}