package com.tverts.retrade.domain.firm;

/* com.tverts: endure */

import com.tverts.endure.core.Entity;


/**
 * Entity that represents an organization.
 *
 * @author anton.baukin@gmail.com
 */
public class Firm extends Entity
{
	/* public: bean interface */

	public String  getShortName()
	{
		return shortName;
	}

	public void    setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public String  getFullName()
	{
		return fullName;
	}

	public void    setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String  getTaxNumber()
	{
		return taxNumber;
	}

	public void    setTaxNumber(String taxNumber)
	{
		this.taxNumber = taxNumber;
	}

	public String  getTaxCode()
	{
		return taxCode;
	}

	public void    setTaxCode(String taxCode)
	{
		this.taxCode = taxCode;
	}

	public String  getAddressString()
	{
		return addressString;
	}

	public void    setAddressString(String addressString)
	{
		this.addressString = addressString;
	}

	public String  getPhonesString()
	{
		return phonesString;
	}

	public void    setPhonesString(String phonesString)
	{
		this.phonesString = phonesString;
	}


	/* private: persisted attributes */

	private String shortName;
	private String fullName;
	private String taxNumber;
	private String taxCode;
	private String addressString;
	private String phonesString;
}