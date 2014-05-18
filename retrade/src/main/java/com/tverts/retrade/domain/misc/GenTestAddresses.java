package com.tverts.retrade.domain.misc;

/* SAX Parser */

import javax.xml.parsers.SAXParserFactory;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenUtils;


/**
 * Test {@link Address}es generation utility.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestAddresses
{
	/* GenAddress Singleton */

	public static GenTestAddresses getInstance()
	{
		return INSTANCE;
	}

	private static final GenTestAddresses INSTANCE =
	  new GenTestAddresses();

	protected GenTestAddresses()
	{}


	/* public: GenTestAddresses interface */

	public Address genRandomAddress(GenCtx ctx)
	{
		ReadTestAddresses r = new ReadTestAddresses().
		  setSeed(ctx.gen().nextInt());

		//!: invoke the sax parser
		try
		{
			SAXParserFactory.newInstance().newSAXParser().parse(
			  GenTestAddresses.class.getResource("GenTestAddresses.xml").
			    toURI().toString(), r
			);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		//?: {has no result}
		if(r.getAddress() == null) throw new IllegalStateException();

		//~: set primary key
		setPrimaryKey(ctx.session(), r.getAddress(), true);

		//~: gen postal index
		if(r.getAddress().getPostalIndex() == null)
			r.getAddress().setPostalIndex(GenUtils.number(ctx.gen(), 6));

		return r.getAddress();
	}
}
