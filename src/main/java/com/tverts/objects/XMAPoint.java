package com.tverts.objects;

/* standard Java classes */

import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/* Spring Framework */

import org.springframework.oxm.Marshaller;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Point to access root OXM Marshaller.
 *
 * @author anton.baukin@gmail.com
 */
public class XMAPoint
{
	/* XMAPoint Singleton */

	public static XMAPoint getInstance()
	{
		return INSTANCE;
	}

	private static final XMAPoint INSTANCE =
	  new XMAPoint();

	protected XMAPoint()
	{}


	/* public: XMAPoint (bean) interface */

	public Marshaller  getMarshaller()
	{
		return marshaller;
	}

	public void        setMarshaller(Marshaller marshaller)
	{
		this.marshaller = marshaller;
	}


	/* public: support interface */

	public static void writeObject(Object object, Result result)
	{
		Marshaller m = getInstance().getMarshaller();

		if(m == null) throw new IllegalStateException(
		  "Can't transform Object to XML as no root marshaller set!");

		try
		{
			m.marshal(object, result);
		}
		catch(Exception e)
		{
			throw new RuntimeException(String.format(
			  "Error occured when OXM marshalling object of class '%s'",
			  OU.cls(object)), e);
		}
	}

	public static void writeObject(Object object, Writer stream)
	{
		writeObject(object, new StreamResult(stream));
	}


	/* private: root marshaller */

	private Marshaller marshaller;
}