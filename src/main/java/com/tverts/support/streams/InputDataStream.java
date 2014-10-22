package com.tverts.support.streams;

/* Java */

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Adapts {@link DataInput} to {@link InputStream}.
 *
 * @author anton.baukin@gmail.com.
 */
public class InputDataStream extends InputStream
{
	/* public: constructor */

	public InputDataStream(DataInput d)
	{
		this.d = EX.assertn(d);
	}

	private DataInput d;


	/* Input Stream */

	public int  read()
	  throws IOException
	{
		return d.readUnsignedByte();
	}

	public int  read(byte[] b)
	  throws IOException
	{
		d.readFully(b);
		return b.length;
	}

	public int  read(byte[] b, int off, int len)
	  throws IOException
	{
		d.readFully(b, off, len);
		return len;
	}

	public long skip(long n)
	  throws IOException
	{
		EX.assertx(n <= Integer.MAX_VALUE);
		return d.skipBytes((int) n);
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

	public void mark(int readlimit)
	{
		throw EX.unop();
	}

	public void reset()
	  throws IOException
	{
		throw EX.unop();
	}
}