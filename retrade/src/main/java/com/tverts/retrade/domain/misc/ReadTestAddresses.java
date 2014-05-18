package com.tverts.retrade.domain.misc;

/* SAX Parser */

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Read generated (test) Addresses from
 * GenTestContractors.xml data file.
 *
 * @author anton.baukin@gmail.com
 */
public class ReadTestAddresses extends DefaultHandler
{
	/* public: parameters */

	/**
	 * When reading random address, set this parameter
	 * to go to the random position according to the
	 * number of the records in the file.
	 */
	public ReadTestAddresses setSeed(int seed)
	{
		this.seed = seed;
		return this;
	}

	public Address           getAddress()
	{
		return address;
	}


	/* public: DefaultHandler interface */

	private static final String ERR = "Illegal format of GenTestContractors.xml";

	public void startElement(String uri, String lname, String qname, Attributes a)
	{
		//?: {root tag <addresses>}
		if("addresses".equals(qname)) try
		{
			int amount = Integer.parseInt(a.getValue("amount"));
			this.left  = ((seed < 0)?(-seed):(seed)) % amount;

			return;
		}
		catch(Exception e)
		{
			throw new IllegalStateException(ERR, e);
		}

		//~: clear the character buffer
		buffer.delete(0, buffer.length());

		//?: {!start the new address} not that state
		if("address".equals(qname))
		{
			//?: {this is not that address to return}
			if(left != 0)
				return;

			//?: {the address read is already started}
			if(address != null) throw new IllegalStateException();
			address = new Address();
		}
	}

	public void endElement(String uri, String lname, String qname)
	{
		//?: {address}
		if("address".equals(qname))
		{
			left--;
			return;
		}

		//?: {<address> & we are not in the address of interest}
		if(left != 0) return;

		if("country".equals(qname))
			address.setCountry(buffer.toString());
		if("province".equals(qname))
			address.setProvince(buffer.toString());
		if("district".equals(qname))
			address.setDistrict(buffer.toString());
		if("settlement".equals(qname))
			address.setSettlement(buffer.toString());
		if("street".equals(qname))
			address.setStreet(buffer.toString());
		if("building".equals(qname))
			address.setBuilding(buffer.toString());
		if("office".equals(qname))
			address.setOffice(buffer.toString());
		if("index".equals(qname))
			address.setPostalIndex(buffer.toString());
	}

	public void characters(char[] ch, int start, int length)
	{
		if(left == 0)
			buffer.append(ch, start, length);
	}


	/* parameters */

	private int seed;


	/* read state */

	private Address      address;
	private StringBuffer buffer = new StringBuffer(256);
	private int          left;
}