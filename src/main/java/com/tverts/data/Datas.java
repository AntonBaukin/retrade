package com.tverts.data;

/* Java */

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/* com.tverts: execution */

import com.tverts.exec.ExecPoint;

/* com.tverts: objects */

import com.tverts.objects.XMAPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.streams.BytesStream;


/**
 * Point to access data objects via the Data Sources.
 *
 * @author anton.baukin@gmail.com
 */
public class Datas
{
	/* Data Access Singleton */

	public static final Datas INSTANCE =
	  new Datas();

	public static Datas getInstance()
	{
		return INSTANCE;
	}

	private Datas()
	{}


	/* public: support interface */

	public static DataSource  source(String did)
	{
		EX.asserts(did, "Valid Data Source ID must be provided!");

		return EX.assertn(INSTANCE.getSource(did),
		  "Data Source with DID [", did, "] is not registered!"
		);
	}

	/**
	 * Passes the data object given to the execution
	 * subsystem to obtain it's XML representation.
	 *
	 * If data object is string, converts it to UTF-8 bytes.
	 * If data object is an array of bytes, writes them to
	 * the resulting stream. If data object is Bytes Stream,
	 * returns it without any further actions.
	 *
	 * Else, invokes the execution layer. If it returns
	 * null result, as the final attempt, tries to convert
	 * the data object directly using {@link XMAPoint}.
	 *
	 * Interesting moment: this function works recursively
	 * in the case the execution layer had returned
	 * a defined instance.
	 */
	public static BytesStream stream(Object data)
	{
		//?: {is a bytes stream}
		if(data instanceof BytesStream)
			return (BytesStream)data;

		//?: {is a string}
		if(data instanceof String) try
		{
			data = ((String)data).getBytes("UTF-8");
		}
		catch(IOException e)
		{
			throw EX.wrap(e);
		}

		//?: {is a bytes array}
		if(data instanceof byte[]) try
		{
			BytesStream stream = new BytesStream();
			stream.write((byte[]) data);
			return stream;
		}
		catch(IOException e)
		{
			throw EX.wrap(e);
		}

		//~: execute the data object
		Object xdata = ExecPoint.executeTx(data);

		//?: {has data result}
		if(xdata != null)
			return stream(xdata);

		//~: write the object mapped to XML
		BytesStream stream = new BytesStream();
		XMAPoint.writeObject(data, stream);
		return stream;
	}

	/**
	 * The same as {@link #stream(Object)},
	 * but is optimized for bytes array.
	 *
	 * Also note that input Bytes Stream
	 * is closed here.
	 */
	public static byte[]      bytes(Object data)
	{
		//?: {is a bytes array}
		if(data instanceof byte[])
			return (byte[]) data;

		//?: {is a string}
		if(data instanceof String) try
		{
			return ((String)data).getBytes("UTF-8");
		}
		catch(IOException e)
		{
			throw EX.wrap(e);
		}

		//?: {is a bytes stream}
		if(data instanceof BytesStream) try
		{
			byte[] bytes = ((BytesStream)data).bytes();
			((BytesStream)data).close();
			return bytes;
		}
		catch(IOException e)
		{
			throw EX.wrap(e);
		}

		//~: execute the data object
		Object xdata = ExecPoint.executeTx(data);

		//?: {has data result}
		if(xdata != null)
			return bytes(xdata);

		//~: write the object mapped to XML
		return XMAPoint.writeObject(data);
	}

	public static byte[]      bytes(ReportModel model)
	{
		//?: {model has no data source assigned}
		EX.assertn(model.getDataSource());

		//~: take it & get the data
		DataSource src  = source(model.getDataSource());
		Object     data = EX.assertn(src.provideData(model),
		  "Data Source [", src.did(), "] was not able to provide data for Model Bean [",
		  model.getClass().getName(), "] with key [", model.getModelKey(), "]!"
		);

		return bytes(data);
	}


	/* public: access interface */

	public void         setReference(DataSourceReference reference)
	{
		EX.assertx(sources.isEmpty());

		for(DataSource ds : reference.dereferObjects())
		{
			EX.assertn(ds);

			//?: {did is undefined}
			EX.assertn(ds.did(), "Data Source of class [",
			  ds.getClass().getName(), "] has DID undefined!");

			//?: {already registered}
			EX.assertx( !sources.containsKey(ds.did()),
			  "Data Source DID [", ds.did(), "] is already registered!"
			);

			//~: put the source
			sources.put(ds.did(), ds);
		}
	}

	public DataSource   getSource(String did)
	{
		return sources.get(did);
	}

	public DataSource[] copySources()
	{
		DataSource[] res = new DataSource[sources.size()];
		res = sources.values().toArray(res);

		Arrays.sort(res, new Comparator<DataSource>()
		{
			public int compare(DataSource a, DataSource b)
			{
				String na = a.getNameLo();
				String nb = b.getNameLo();

				if(SU.sXe(na) || SU.sXe(nb))
				{
					na = a.getName();
					nb = b.getName();
				}

				return na.compareToIgnoreCase(nb);
			}
		});

		return res;
	}


	/* private: the root & the sources*/

	private Map<String, DataSource> sources =
	  new HashMap<String, DataSource>(17);
}