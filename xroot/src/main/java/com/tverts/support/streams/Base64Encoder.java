package com.tverts.support.streams;

/* standard Java classes */

import java.io.IOException;
import java.io.OutputStream;

/**
 * Encodes byte stream into stream of BASE64 characters.
 * The origination of this class comes from Base64
 * Java project taken from SourceForge.
 *
 * @author Anton Baukin  (anton.baukin@gmail.com)
 * @author Robert Harder (rob@iharder.net) 
 */
public final class Base64Encoder extends OutputStream
{
	/* public: constructors */

	/**
	 * Creates the buffer for standard 77-character lines.
	 * Each line has 76 characters for 19 income bytes and
	 * ends with '\n' separator.
	 *
	 * The number of lines in the buffer is 16. It's total
	 * length is 1232 characters (2464 bytes).
	 */
	public Base64Encoder(OutputStream stream)
	{
		this(stream, 19, 16);
	}

	public Base64Encoder(OutputStream stream, int bsize)
	{
		this(stream, 19, bsize);
	}

	/**
	 * @param lsize
	 *
	 *   defines the factor maximum length of the output string.
	 *   Real maximum length equals to <tt>4*lsize</tt> because
	 *   each 3 income bytes produces 4 characters. After each
	 *   <tt>4*lsize</tt> characters '\n' character is written.
	 *
	 *   The argument may be zero. In this case no '\n' characters
	 *   are written. By default is is equals to 19 (76 characters).
	 *
	 * @param bsize
	 *
	 *   defines the number of lines in the internal buffer used
	 *   while encoding. When the buffer fully filled, it is flashed
	 *   to the output.
	 *
	 *   The length of the buffer (in characters) equals to
	 *   <tt>(4*lsize + 1)*bsize</tt>. One character is reserved for
	 *   '\n'. When '\n' are not inserted, the length of the buffer
	 *   is  equal to <tt>4*bsize</tt> not to break character stream
	 *   in the middle of 3-byte triple.
	 */
	public Base64Encoder(OutputStream stream, int lsize, int bsize)
	{
		if(lsize <  0) throw new IllegalArgumentException();
		if(bsize <= 0) throw new IllegalArgumentException();

		this.stream = stream;
		this.lmax   = (lsize != 0)?(4*lsize):(-1);
		this.buffer = new byte[
		  (lsize != 0)?((4*lsize + 1)*bsize):(4*bsize)];
	}

	/* public: OutputStream interface */

	public void write(int bt)
	  throws IOException
	{
		//?: {has space in three-buffer} add byte & return
		if(ts != 24)
		{
			three |= ((bt & 0xFF) << ts);
			ts += 8;
			return;
		}

		// flush three-buffer

		//?: {streaming buffer is full} flush it
		if(bsize == buffer.length)
		{
			stream.write(buffer);
			lsize = bsize = 0;
		}

		//append the bytes
		encode3to4();
		lsize += 4;
		bsize += 4;

		//?: {the line is full} flush it
		if(lsize == lmax)
		{
			buffer[bsize++] = '\n';
			lsize = 0;
		}

		//~flush three-buffer

		//append byte
		three = bt & 0xFF;
		ts = 8;
	}

	public void write(byte b[], int off, int len)
	  throws IOException
	{
		int bt;

		for(int o = off; (len != 0); o++, len--)
		{
			bt = b[o];

			//?: {has space in three-buffer} add byte & return
			if(ts != 24)
			{
				three |= ((bt & 0xFF) << ts);
				ts += 8;
				continue;
			}

			// flush three-buffer

			//?: {streaming buffer is full} flush it
			if(bsize == buffer.length)
			{
				stream.write(buffer);
				lsize = bsize = 0;
			}

			//append the bytes
			encode3to4();
			lsize += 4;
			bsize += 4;

			//?: {the line is full} flush it
			if(lsize == lmax)
			{
				buffer[bsize++] = '\n';
				lsize = 0;
			}

			//~flush three-buffer

			//append byte
			three = bt & 0xFF;
			ts = 8;
		}
	}

	public void close()
	  throws IOException
	{
		flushBeforeClose();
		stream.close();
	}

	/**
	 * Flushes to the underlying stream all the lines
	 * that are filled fully. Hence the last line of the
	 * buffer may be not written.
	 */
	public void flush()
	  throws IOException
	{
		//?: {has new FULL bytes in three-buffer} flush them
		if(ts == 24)
		{
			//?: {streaming buffer is full} flush it
			if(bsize == buffer.length)
			{
				stream.write(buffer);
				lsize = bsize = 0;
			}

			//append the bytes of the three-array
			encode3to4();
			lsize += 4;
			bsize += 4;
			ts = three = 0;

			//?: {the line is full} terminate it
			if(lsize == lmax)
			{
				buffer[bsize++] = '\n';
				lsize = 0;
			}
		}

		//?: {not writing '\n'} write the whole buffer
		if(lmax == -1)
		{
			stream.write(buffer, 0, bsize);
			lsize = bsize = 0; //<-- 'lsize' means nothing in this case
		}
		//!: write to the buffer all the lines except the current one
		else if(bsize != lsize) //<-- the same as: (bsize > lsize)
		{
			stream.write(buffer, 0, bsize - lsize);
			if(lsize != 0)
				System.arraycopy(buffer, bsize - lsize, buffer, 0, lsize);
			bsize = lsize;
		}
	}

	/**
	 * Flushes the stream adding the padding characters
	 * to fulfill 4-characters terminal block. In the same
	 * manner the stream is flushed before closing.
	 */
	public void flushFully()
	  throws IOException
	{
		flushBeforeClose();
	}

	/* private: encoding procedure */

	private void encode3to4()
	{
		byte[] b = buffer;
		int    t = three;
		int    o = bsize;

		//?: {have all three income bytes}
		if(ts == 24)
		{
			//swap the first and the third bytes
			t = (t & 0xFF00) | ((t & 0xFF) << 16) | ((t >>> 16) & 0xFF);

			b[o    ] = ABC[(t >>> 18)       ];
			b[o + 1] = ABC[(t >>> 12) & 0x3F];
			b[o + 2] = ABC[(t >>>  6) & 0x3F];
			b[o + 3] = ABC[(t       ) & 0x3F];

			return;
		}

		//?: {have two income bytes}
		if(ts == 16)
		{
			//swap the first and the third bytes
			t = (t & 0xFF00) | ((t & 0xFF) << 16);

			b[o    ] = ABC[(t >>> 18)       ];
			b[o + 1] = ABC[(t >>> 12) & 0x3F];
			b[o + 2] = ABC[(t >>>  6) & 0x3F];
			b[o + 3] = EQ;

			return;
		}

		//?: {have one income byte}
		if(ts == 8)
		{
			//swap the first and the third bytes
			t = (t & 0xFF) << 16;

			b[o    ] = ABC[(t >>> 18)       ];
			b[o + 1] = ABC[(t >>> 12) & 0x3F];
			b[o + 2] = EQ;
			b[o + 3] = EQ;

			return;
		}

		//!: has no bytes
		b[o] = b[o + 1] = b[o + 2] = b[o + 3] = EQ;
	}

	private void flushBeforeClose()
	  throws IOException
	{
		//?: {has new bytes in three-buffer} flush them
		if(ts != 0)
		{
			//?: {streaming buffer is full} flush it
			if(bsize == buffer.length)
			{
				stream.write(buffer);
				lsize = bsize = 0;
			}

			//append the bytes of the three-array
			encode3to4();
			lsize += 4;
			bsize += 4;
			ts = three = 0;

			//?: {the line is full} terminate it
			if(lsize == lmax)
			{
				buffer[bsize++] = '\n';
				lsize = 0;
			}
		}

		//!: write the buffer as it is
		if(bsize != 0)
		{
			stream.write(buffer, 0, bsize);
			lsize = bsize = 0;
		}
	}

	/* private: encoding alphabet */

	private static final byte EQ = (int)('=');

	private static final byte[] ABC;
	private static final String ABCS =
	  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	static
	{
		try
		{
			ABC = ABCS.getBytes("ISO-8859-1");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/* private: streaming data */

	private OutputStream stream;
	private byte[]       buffer;
	private int          lmax;   //<-- the maximum length of the text line (%4 == 0)
	private int          bsize;  //<-- the number of valid bytes in the buffer
	private int          lsize;  //<-- the size of the current text line

	/* private: encoding data */

	private int          three;  //<-- 'buffer' of three income bytes
	private int          ts;     //<-- the number of valid BITS in three buffer
}