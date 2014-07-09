package com.tverts.objects;

/* standard Java classes */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/* Spring Framework */

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.EX;
import com.tverts.support.streams.BytesStream;


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
		Marshaller m = EX.assertn( getInstance().getMarshaller(),
		  "Can't transform Object to XML as no root marshaller set!");

		try
		{
			m.marshal(object, result);
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured when OXM marshalling ",
			  "object of class [", LU.cls(object), "]!"
			);
		}
	}

	public static void   writeObject(Object object, Writer stream)
	{
		writeObject(object, new StreamResult(stream));
	}

	/**
	 * Write the object to the raw stream given in UTF-8.
	 * Note that the stream is closed (also, in the case of error)!
	 */
	public static void   writeObject(Object object, OutputStream stream)
	{
		Throwable error  = null;
		boolean   closed = false;

		try
		{
			Writer wr = new OutputStreamWriter(stream, "UTF-8");
			writeObject(object, wr);
			wr.close();
			closed = true;
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			if(!closed) try
			{
				stream.close();
			}
			catch(Throwable e)
			{
				error = e;
			}
		}

		if(error != null)
			throw EX.wrap(error);
	}

	public static void   writeObject(Object object, BytesStream stream)
	{
		//~: prevent close on the exit
		boolean notclose = stream.isNotCloseNext();

		try
		{
			//~: write the instance
			Writer wr = new OutputStreamWriter(stream, "UTF-8");
			writeObject(object, wr);

			stream.setNotCloseNext(true);
			wr.close(); //<-- without the bytes stream

			//~: restore original not-close flag
			stream.setNotCloseNext(notclose);
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
	}

	public static byte[] writeObject(boolean gzip, Object object)
	{
		BytesStream bs = new BytesStream();
		bs.setNotClose(true);

		try
		{
			OutputStream os = (!gzip)?(bs):(new GZIPOutputStream(bs));

			//~: encode
			writeObject(object, os);

			if(gzip) //<-- close the deflating stream only
				os.close();

			return bs.bytes();
		}
		catch(Throwable e)
		{
			throw EX.wrap(e);
		}
		finally
		{
			bs.closeAlways();
		}
	}

	public static Object readObject(Source source)
	{
		Unmarshaller m = EX.assertn( getInstance().getUnmarshaller(),
		  "Can't recover Object from XML as no root unmarshaller set!"
		);

		try
		{
			return m.unmarshal(source);
		}
		catch(Exception e)
		{
			throw EX.wrap(e, "Error occured when unmarshalling OXM document!");
		}
	}

	public static Object readObject(Reader stream)
	{
		return readObject(new StreamSource(stream));
	}

	public static Object readObject(InputStream stream)
	{
		return readObject(new StreamSource(stream));
	}

	@SuppressWarnings("unchecked")
	public static <T> T  readObject(boolean gzip, Class<T> cls, byte[] bytes)
	{
		InputStream is = new ByteArrayInputStream(bytes);

		try
		{
			//?: {data are gun-zipped}
			if(gzip) is = new GZIPInputStream(is);

			//~: decode the object
			Object res = readObject(new StreamSource(is));
			is.close();

			//~: check the result
			EX.assertx( (res == null) || cls.isAssignableFrom(res.getClass()),
			  "JAXB-XML mapped object of class [", LU.cls(res),
			  "] is not of the request class [", LU.cls(cls), "]!"
			);

			return (T) res;
		}
		catch(Exception e)
		{
			throw EX.wrap(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T  cloneObject(T obj)
	{
		if(obj == null)
			return null;

		BytesStream s = new BytesStream();
		s.setNotClose(true);

		try
		{
			//~: write object
			writeObject(obj, s);

			//~: read it back
			return (T) readObject(s.inputStream());
		}
		finally
		{
			s.closeAlways();
		}
	}


	/* private: OXM implementation */

	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
}