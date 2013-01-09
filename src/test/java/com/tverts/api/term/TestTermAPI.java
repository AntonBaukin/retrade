package com.tverts.api.term;

/* Java API for XML Binding */

import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class TestTermAPI
{
	@org.junit.Test
	@SuppressWarnings("unchecked")
	public void testGetGoods()
	  throws Exception
	{
		Package p = new Package();
		p.setTx(100L);
		p.setOperation(Good.class);

		p.getList().add(good(
		  5001L, false, "ФР-БАН", "Бананы",
		  100L, "ВЕС-кг", "кг",
		  "48.65"
		));


		p.getList().add(good(
		  5002L, false, "ФР-ЯБЛ", "Яблоки",
		  100L, "ВЕС-кг", "кг",
		  "75.40"
		));

		print(p, "Get all goods");
	}

	@org.junit.Test
	@SuppressWarnings("unchecked")
	public void testGetPriceLists()
	  throws Exception
	{
		Package p = new Package();
		p.setOperation(PriceList.class);

		//~: Основной
		PriceList l = list(
		  1001, false, "ОСН", "Основной"
		);
		p.getList().add(l);


		l.getGoods().add(good(
		  5001L, false, "48.65"
		));

		l.getGoods().add(good(
		  5002L, false, "75.40"
		));


		//~: Специальный
		l = list(
		  1002, false, "ВИП", "Специальный"
		);
		p.getList().add(l);


		l.getGoods().add(good(
		  5001L, false, "40.00"
		));

		l.getGoods().add(good(
		  5002L, true, null
		));


		print(p, "Get all price lists");
	}


	/* private: object creation */

	private Good good(long key, boolean removed, String price)
	{
		Good g = new Good();

		g.setGoodKey(key);
		g.setRemoved(removed);
		if(!removed)
			g.setPrice(new BigDecimal(price));

		return g;
	}

	private Good good(long key, Object... x)
	{
		Good g = new Good(); int i = 0;

		g.setGoodKey(key);
		g.setRemoved(x(Boolean.class, i++, x));
		g.setGoodCode(x(String.class, i++, x));
		g.setGoodTitle(x(String.class, i++, x));

		g.setMeasureKey(x(Long.class, i++, x));
		g.setMeasureCode(x(String.class, i++, x));
		g.setMeasureTitle(x(String.class, i++, x));

		g.setPrice(x(BigDecimal.class, i/*++*/, x));

		return g;
	}

	private PriceList list(long key, Object... x)
	{
		PriceList l = new PriceList(); int i = 0;

		l.setKey(key);
		l.setRemoved(x(Boolean.class, i++, x));
		l.setCode(x(String.class, i++, x));
		l.setTitle(x(String.class, i/*++*/, x));

		return l;
	}


	/* private: support functions */

	private static JAXBContext context;

	@org.junit.BeforeClass
	public static void initContext()
	  throws Exception
	{
		context = JAXBContext.newInstance(
		  "com.tverts.api.term"
		);
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

		return (T) v;
	}
}