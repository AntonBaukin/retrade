package com.tverts.support.streams;

/* Java */

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Adapts {@link DataOutput} to {@link OutputStream}.
 *
 * @author anton.baukin@gmail.com.
 */
public final class OutputDataStream extends OutputStream
{
	/* public: constructor */

	public OutputDataStream(DataOutput d)
	{
		this.d = EX.assertn(d);
	}

	private DataOutput d;


	/* Output Stream */

	public void write(int b)
	  throws IOException
	{
		d.write(b);
	}

	public void write(byte[] b)
	  throws IOException
	{
		d.write(b);
	}

	public void write(byte[] b, int off, int len)
	  throws IOException
	{
		d.write(b, off, len);
	}

	public void close()
	  throws IOException
	{
		if(d instanceof AutoCloseable) try
		{
			((AutoCloseable)d).close();
		}
		catch(IOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}
}