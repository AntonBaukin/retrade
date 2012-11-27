package com.tverts.objects;

/* standard Java classes */

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/* Spring Framework */

import com.tverts.support.streams.BytesStream;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

/* com.tverts: support */

import com.tverts.support.LU;


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

	public Marshaller   getMarshaller()
	{
		return marshaller;
	}

	public void         setMarshaller(Marshaller marshaller)
	{
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller()
	{
		if(unmarshaller != null)
			return unmarshaller;

		if(getMarshaller() instanceof Unmarshaller)
			return (Unmarshaller) getMarshaller();

		return null;
	}

	public void         setUnmarshaller(Unmarshaller unmarshaller)
	{
		this.unmarshaller = unmarshaller;
	}


	/* public: singleton interface */

	public static void   writeObject(Object object, Result result)
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
			  "Error occured when OXM marshalling object of class '%s'!",
			  LU.cls(object)), e);
		}
	}

	public static void   writeObject(Object object, Writer stream)
	{
		writeObject(object, new StreamResult(stream));
	}

	public static byte[] writeObject(Object object)
	{
		BytesStream bs = new BytesStream();

		try
		{
			Writer   wr = new OutputStreamWriter(bs, "UTF-8");

			writeObject(object, wr);
			wr.flush();

			return bs.bytes();
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			bs.close();
		}
	}

	public static Object readObject(Source source)
	{
		Unmarshaller m = getInstance().getUnmarshaller();

		if(m == null) throw new IllegalStateException(
		  "Can't recover Object from XML as no root unmarshaller set!");

		try
		{
			return m.unmarshal(source);
		}
		catch(Exception e)
		{
			throw new RuntimeException(
			  "Error occured when unmarshalling OXM document!", e);
		}
	}

	public static Object readObject(Reader stream)
	{
		return readObject(new StreamSource(stream));
	}

	public static Object readObject(byte[] bytes)
	{
		try
		{
			return readObject(new StreamSource(
			  new ByteArrayInputStream(bytes)
			));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/* private: OXM implementation */

	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
}