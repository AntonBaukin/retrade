package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/* com.tverts: api */

import com.tverts.api.AuthClient;
import com.tverts.api.Payload;


/**
 * Prints XML of ReTrade Goods API.
 *
 * @author anton.baukin@gmail.com
 */
public class TestApiGoods
{
	@org.junit.Test
	@SuppressWarnings("unchecked")
	public void testGetMeasures()
	  throws Exception
	{
		Payload p = new Payload();
		p.setOperation(Measure.class.getName());

		p.getList().add(measure(
		  100, 2, "ID__B___0___0___1409___0___0_________7639_____",
		  false, "000073", "кг", "166", "1.0"
		));

		p.getList().add(measure(
		  101, 2, "ID__B___0___0___1409___0___0_________7963_____",
		  false, "000397", "шт", "796", null
		));

		p.getList().add(measure(
		  102, 3, "ID__B___0___0___1409___0___0_________XXXX_____",
		  true
		));

		print(p, "Get all measures");
	}


	@org.junit.Test
	@SuppressWarnings("unchecked")
	public void testGetGoods()
	  throws Exception
	{
		Payload p = new Payload();
		p.setOperation(Good.class.getName());

		p.getList().add(good(
		  500, 10, "ID__B___0___0___12___0___0________10184_____",
		  false, "000076", "Мандарины", 100, "48.65"
		));

		p.getList().add(good(
		  501, 10, "ID__B___0___0___12___0___0________11082_____",
		  false, "000974", "Ананас", 101, "110.50"
		));

		p.getList().add(good(
		  502, 11, "ID__B___0___0___12___0___0________XXXXX_____",
		  true
		));

		print(p, "Get all goods");
	}


	/* private: object creation */

	private Measure measure(long key, Object... x)
	{
		Measure m = new Measure(); int i = 0;

		m.setPkey(key);
		m.setTx(x(Long.class, i++, x));
		m.setXkey(x(String.class, i++, x));
		m.setRemoved(x(Boolean.class, i++, x));
		m.setCode(x(String.class, i++, x));
		m.setName(x(String.class, i++, x));

		m.setClassCode(x(String.class, i++, x));
		m.setClassUnit(x(BigDecimal.class, i++, x));
		m.setFractional(x(Boolean.class, i/*++*/, x));

		return m;
	}


	private Good good(long key, Object... x)
	{
		Good g = new Good(); int i = 0;

		g.setPkey(key);
		g.setTx(x(Long.class, i++, x));
		g.setXkey(x(String.class, i++, x));
		g.setRemoved(x(Boolean.class, i++, x));
		g.setCode(x(String.class, i++, x));
		g.setName(x(String.class, i++, x));

		g.setMeasure(x(Long.class, i++, x));
		//g.setPrice(x(BigDecimal.class, i/*++*/, x));

		return g;
	}

	private PriceList list(long key, Object... x)
	{
		PriceList l = new PriceList(); int i = 0;

		l.setPkey(key);
		l.setTx(x(Long.class, i++, x));
		l.setXkey(x(String.class, i++, x));
		l.setRemoved(x(Boolean.class, i++, x));
		l.setCode(x(String.class, i++, x));
		l.setName(x(String.class, i/*++*/, x));

		return l;
	}


	/* private: support functions */

	private static JAXBContext context;

	@org.junit.BeforeClass
	public static void initContext()
	  throws Exception
	{
		context = new AuthClient().
		  getJAXBContext();
	}

	private static void print(Object obj, Object... msgs)
	  throws Exception
	{
		StringBuilder sb = new StringBuilder(32);

		for(Object s : msgs) if(s != null)
			sb.append(s.toString());

		System.out.println();

		if(sb.length() != 0)
		{
			System.out.println("<!--");
			System.out.println(sb);
			System.out.println("-->");
		}

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		m.marshal(obj, System.out);
	}

	@SuppressWarnings("unchecked")
	private static <T> T x(Class<T> c, int i, Object[] x)
	{
		Object v = (i < x.length)?(x[i]):(null);

		if(v == null)
			return null;

		if(BigDecimal.class.equals(c) && (v instanceof String))
			v = new BigDecimal((String)v);

		if(Long.class.equals(c) && (v instanceof Integer))
			v = ((Integer)v).longValue();

		return (T) v;
	}
}