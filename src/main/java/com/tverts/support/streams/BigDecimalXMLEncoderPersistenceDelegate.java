package com.tverts.support.streams;

/* standard Java classes */

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

import java.math.BigDecimal;


/**
 * Allows to write and read BigDecimal properties
 * of Java Beans via XML Encoder and Decoder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   BigDecimalXMLEncoderPersistenceDelegate
       extends DefaultPersistenceDelegate
{
	/* BigDecimalXMLEncoderPersistenceDelegate Singleton */

	public static BigDecimalXMLEncoderPersistenceDelegate getInstance()
	{
		return INSTANCE;
	}

	private static final BigDecimalXMLEncoderPersistenceDelegate INSTANCE =
	  new BigDecimalXMLEncoderPersistenceDelegate();

	public BigDecimalXMLEncoderPersistenceDelegate()
	{}


	/* protected: DefaultPersistenceDelegate interface */

	protected Expression instantiate(Object value, Encoder out)
	{
		if(!(value instanceof BigDecimal))
			throw new IllegalArgumentException();

		return new Expression(value, BigDecimal.class, "new",
		  new Object[] {value.toString()});
	}

	protected boolean    mutatesTo(Object oldInstance, Object newInstance)
	{
		return oldInstance.equals(newInstance);
	}
}