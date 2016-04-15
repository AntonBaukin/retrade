package com.tverts.api.clients;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * Postal address.
 */
@XmlRootElement(name = "address")
@XmlType(name = "address", propOrder = {
  "country", "postalIndex", "province", "district",
  "settlement", "street", "building", "office",
  "phones", "email", "remarks"
})
public class Address
{
	@XmlElement(name = "country")
	public String getCountry()
	{
		return country;
	}

	private String country;

	public void setCountry(String country)
	{
		this.country = country;
	}

	@XmlElement(name = "postal-index")
	public String getPostalIndex()
	{
		return postalIndex;
	}

	private String postalIndex;

	public void setPostalIndex(String postalIndex)
	{
		this.postalIndex = postalIndex;
	}

	@XmlElement(name = "province")
	public String getProvince()
	{
		return province;
	}

	private String province;

	public void setProvince(String province)
	{
		this.province = province;
	}

	@XmlElement(name = "district")
	public String getDistrict()
	{
		return district;
	}

	private String district;

	public void setDistrict(String district)
	{
		this.district = district;
	}

	@XmlElement(name = "settlement")
	public String getSettlement()
	{
		return settlement;
	}

	private String settlement;

	public void setSettlement(String settlement)
	{
		this.settlement = settlement;
	}

	@XmlElement(name = "street")
	public String getStreet()
	{
		return street;
	}

	private String street;

	public void setStreet(String street)
	{
		this.street = street;
	}

	@XmlElement(name = "building")
	public String getBuilding()
	{
		return building;
	}

	private String building;

	public void setBuilding(String building)
	{
		this.building = building;
	}

	@XmlElement(name = "office")
	public String getOffice()
	{
		return office;
	}

	private String office;

	public void setOffice(String office)
	{
		this.office = office;
	}

	@XmlElement(name = "phones")
	public String getPhones()
	{
		return phones;
	}

	private String phones;

	public void setPhones(String phones)
	{
		this.phones = phones;
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

	@XmlElement(name = "remarks")
	public String getRemarks()
	{
		return remarks;
	}

	private String remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* Support Interface */

	@XmlTransient
	public boolean isEmpty()
	{
		return (postalIndex == null) && (settlement == null) &&
		  (street == null) && (building == null) &&
		  (phones == null) && (email == null) && (remarks == null);
	}
}