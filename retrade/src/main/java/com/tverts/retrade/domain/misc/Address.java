package com.tverts.retrade.domain.misc;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Postal address.
 *
 * @author anton.baukin@gmail.com
 */
public class      Address
       extends    NumericBase
       implements DomainEntity
{
	/* public: Address (bean) interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getDistrict()
	{
		return district;
	}

	public void setDistrict(String district)
	{
		this.district = district;
	}

	public String getSettlement()
	{
		return settlement;
	}

	public void setSettlement(String settlement)
	{
		this.settlement = settlement;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getBuilding()
	{
		return building;
	}

	public void setBuilding(String building)
	{
		this.building = building;
	}

	public String getOffice()
	{
		return office;
	}

	public void setOffice(String office)
	{
		this.office = office;
	}

	public String getPostalIndex()
	{
		return postalIndex;
	}

	public void setPostalIndex(String postalIndex)
	{
		this.postalIndex = postalIndex;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* public: Object interface */

	public String toString()
	{
		return SU.scats(", ",
		  postalIndex, country, province, district,
		  settlement, street, building, office
		);
	}

	public String toShorterString()
	{
		return SU.scats(", ",
		  province, settlement, street, building, office
		);
	}


	/* domain reference */

	private Domain domain;


	/* address attributes */

	private String country;
	private String province;
	private String district;
	private String settlement;
	private String street;
	private String building;
	private String office;
	private String postalIndex;
	private String remarks;
}