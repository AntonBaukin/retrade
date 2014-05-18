package com.tverts.retrade.domain.account;

/* SAX Parser */

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Read generated (test) banks from
 * GenTestBanks.xml data file.
 *
 * @author anton.baukin@gmail.com
 */
public class ReadTestBanks extends DefaultHandler
{
	/* public: parameters */

	/**
	 * When reading random address, set this parameter
	 * to go to the random position according to the
	 * number of the records in the file.
	 */
	public ReadTestBanks setSeed(int seed)
	{
		this.seed = seed;
		return this;
	}

	public String        getBankId()
	{
		return bankId;
	}

	public String        getBankName()
	{
		return bankName;
	}

	public String        getBankAccount()
	{
		return bankAccount;
	}


	/* public: DefaultHandler interface */

	public void startElement(String uri, String lname, String qname, Attributes a)
	{
		//?: {root tag <banks>}
		if("banks".equals(qname)) try
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

		//?: {!start the new address}not that state
		if("bank".equals(qname))
		{
			//?: {this is not that address to return}
			if(left != 0)
				return;
		}
	}

	public void endElement(String uri, String lname, String qname)
	{
		//?: {bank}
		if("bank".equals(qname))
		{
			left--;
			return;
		}

		//?: {<bank> & we are not in the bank of interest}
		if(left != 0) return;

		//~: bank id
		if("id".equals(qname))
			bankId = buffer.toString();

		//~: bank name
		if("name".equals(qname))
			bankName = buffer.toString();

		//~: bank account
		if("account".equals(qname))
			bankAccount = buffer.toString();
	}

	public void characters(char[] ch, int start, int length)
	{
		if(left == 0)
			buffer.append(ch, start, length);
	}

	private static final String ERR = "Illegal format of GenTestBanks.xml";


	/* parameters */

	private int seed;


	/* read state */

	private StringBuffer buffer = new StringBuffer(256);
	private int          left;


	/* read results */

	private String bankId;
	private String bankName;
	private String bankAccount;
}