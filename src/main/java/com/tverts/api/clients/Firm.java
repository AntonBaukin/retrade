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
  "fullName", "taxNumber", "taxCode", "regCode", "formCode",
  "type", "email", "phones", "persons", "agreement", "remarks",
  "registryAddress", "contactAddress", "officeAddress"
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

	@XmlElement(name = "reg-code")
	public String getRegCode()
	{
		return regCode;
	}

	private String regCode;

	public void setRegCode(String regCode)
	{
		this.regCode = regCode;
	}

	@XmlElement(name = "form-code")
	public String getFormCode()
	{
		return formCode;
	}

	private String formCode;

	public void setFormCode(String formCode)
	{
		this.formCode = formCode;
	}

	/**
	 * Organizational type (locale and language dependent).
	 */
	public String getType()
	{
		return type;
	}

	private String type;

	public void setType(String type)
	{
		this.type = type;
	}

	@XmlElement(name = "email")
	public String getEmail()
	{
		return email;
	}

	private String email;

	public void setEmail(String email)
	{
		this.email = email;
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

	/**
	 * Text enumerating the contact persons as free text.
	 */
	public String getPersons()
	{
		return persons;
	}

	private String persons;

	public void setPersons(String persons)
	{
		this.persons = persons;
	}

	/**
	 * Information about the agreements with this firm.
	 */
	public String getAgreement()
	{
		return agreement;
	}

	private String agreement;

	public void setAgreement(String agreement)
	{
		this.agreement = agreement;
	}

	public String getRemarks()
	{
		return remarks;
	}

	private String remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/**
	 * Registration address of this firm.
	 */
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
	 * Contact (postal) address assigned when it
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

	/**
	 * Main office address assigned when it
	 * differs from the registration one.
	 */
	@XmlElement(name = "office-address")
	public Address getOfficeAddress()
	{
		return officeAddress;
	}

	private Address officeAddress;

	public void setOfficeAddress(Address officeAddress)
	{
		this.officeAddress = officeAddress;
	}
}