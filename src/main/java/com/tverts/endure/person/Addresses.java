package com.tverts.endure.person;

/* Java */

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* com.tverts: api */

import com.tverts.api.clients.Address;

/* com.tverts: genesis */

import com.tverts.genesis.GenUtils;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Test {@link Address}es generation utility.
 *
 * Note that it uses limited collection
 * of test entries that may be exhausted!
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Addresses
{
	/* Address Generation Singleton */

	public static Addresses getInstance()
	{
		return INSTANCE;
	}

	public static final Addresses INSTANCE =
	  new Addresses();

	private Addresses()
	{}


	/* Support Routines */

	public static String a2s(Address a)
	{
		return (a == null)?(null):SU.scats("; ",
		  a.getCountry(),    a.getPostalIndex(),
		  a.getProvince(),   a.getDistrict(),
		  a.getSettlement(), a.getStreet(),
		  a.getBuilding(),   a.getOffice(),
		  a.getPhones()
		);
	}

	/**
	 * Shorter version of an address.
	 */
	public static String a2ss(Address a)
	{
		return (a == null)?(null):SU.scats("; ",
		  a.getPostalIndex(), a.getProvince(),
		  a.getSettlement(),  a.getStreet(),
		  a.getBuilding(),    a.getOffice()
		);
	}


	/* Address Test Generation */

	public Address selectRandomAddress(Random gen)
	{
		Address a; synchronized(this)
		{
			//?: {has no addresses loaded}
			if(addresses == null)
			{
				addresses = new ArrayList<Address>(512);
				loadAddresses(addresses);
			}

			//?: {has no more}
			EX.asserte(addresses, "No test Address instances left to use!");

			//~: take the next
			a = addresses.remove(gen.nextInt(addresses.size()));
		}

		//~: assign the missing values
		initAddress(gen, a);

		return a;
	}

	protected List<Address> addresses;


	/* addresses loading and generation */

	protected void loadAddresses(List<Address> dest)
	{
		createProcessor(dest).
		  process(getDataFile().toString());
	}

	protected void initAddress(Random gen, Address a)
	{
		//?: {has no postal index}
		if(SU.sXe(a.getPostalIndex()))
			a.setPostalIndex(GenUtils.number(gen, 6));

		//?: {has no phones}
		if(SU.sXe(a.getPhones()))
			a.setPhones(GenUtils.phones(gen, "+7-920-", 3, 7));
	}


	/* protected: XML Processor State */

	protected static class GenState
	{
		public Address address;
	}


	/* protected: XML Processor */

	protected SaxProcessor<?> createProcessor(List<Address> dest)
	{
		return new ReadTestAddresses(dest);
	}

	protected URL             getDataFile()
	{
		return EX.assertn(
		  getClass().getResource("GenTestAddresses.xml"),
		  "No GenTestAddresses.xml file found!"
		);
	}

	protected class ReadTestAddresses extends SaxProcessor<GenState>
	{
		public ReadTestAddresses(List<Address> dest)
		{
			this.dest = dest;
			this.collectTags = true;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: <address>
			if(istag(1, "address"))
			{
				event().state(new GenState());
				state().address = new Address();
			}

			else if(islevel(1))
				throw wrong();
		}

		protected void open()
		{}

		protected void close()
		{
			//?: {<address>}
			if(islevel(1)) try
			{
				EX.assertx(istag("address"));

				//~: assign the tags
				requireFillClearTags(state().address, true,
				  "settlement", "street", "building");

				//!: add to the list
				dest.add(state().address);
			}
			catch(Throwable e)
			{
				throw EX.wrap(e);
			}
		}


		/* destination list */

		private List<Address> dest;
	}
}
