package com.tverts.retrade.domain.firm;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* SAX Parser */

import com.tverts.api.clients.Firm;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.FirmEntity;


/**
 * Read generated (test) Contractors from
 * GenTestContractors.xml data file.
 *
 * @author anton.baukin@gmail.com
 */
public class ReadTestContractors extends DefaultHandler
{
	/* public: constructor */

	public ReadTestContractors(GenCtx genCtx)
	{
		this.genCtx = genCtx;
	}


	/* public: ReadTestContractors interface */

	public List<Contractor> getResult()
	{
		return result;
	}


	/* public: DefaultHandler interface */

	public void startElement(String uri, String lname, String qname, Attributes a)
	{
		if("contractor".equals(qname) && (state == null))
		{
			state = "contractor";

			contrCode = a.getValue("code");
			contrName = a.getValue("name");

			return;
		}

		if("firm".equals(qname) && "contractor".equals(state))
		{
			state = "contractor.firm";
			return;
		}

		if("name".equals(qname) && "contractor.firm".equals(state))
		{
			bufon = true;
			return;
		}

		if("short-name".equals(qname) && "contractor.firm".equals(state))
		{
			bufon = true;
		}

	}

	public void endElement(String uri, String lname, String qname)
	{
		if("contractor".equals(qname) && "contractor".equals(state))
		{
			addContractor();
			clearState();
			return;
		}

		if("firm".equals(qname) && "contractor.firm".equals(state))
		{
			state = "contractor";
			return;
		}

		if("name".equals(qname) && "contractor.firm".equals(state))
		{
			bufon = false;

			firmName = buffer.toString();
			buffer.delete(0, buffer.length());

			return;
		}

		if("short-name".equals(qname) && "contractor.firm".equals(state))
		{
			bufon = false;

			firmShort = buffer.toString();
			buffer.delete(0, buffer.length());
		}
	}

	public void characters(char[] ch, int start, int length)
	{
		if(bufon) buffer.append(ch, start, length);
	}


	/* protected: contractor instance creation */

	protected void addContractor()
	{
		Contractor c = new Contractor();

		//~: contractor code
		c.setCode(contrCode);

		//~: contractor catalogue item name
		c.setName(contrName);

		//?: {firm tag present}
		if(firmName != null)
			addContractorFirm(c);

		//!: add it to the results
		result.add(c);
	}

	protected void addContractorFirm(Contractor c)
	{
		FirmEntity fe = new FirmEntity();
		Firm       f  = fe.getOx();

		//~: firm code
		f.setCode(c.getCode());

		//~: firm short name
		f.setName(firmShort);

		//~: firm full name
		f.setFullName(firmName);

		//!: assign it to the contractor
		c.setFirm(fe);
	}

	protected void clearState()
	{
		state = null;
		bufon = false;
		buffer.delete(0, buffer.length());

		contrCode = contrName = null;
		firmName = firmShort = null;
	}


	/* handler state */

	protected List<Contractor> result = new ArrayList<Contractor>(32);
	protected final GenCtx     genCtx;
	protected String           state;
	protected StringBuilder    buffer = new StringBuilder(256);
	protected boolean          bufon;


	/* xml elements */

	protected String contrCode;
	protected String contrName;

	protected String firmName;
	protected String firmShort;
}