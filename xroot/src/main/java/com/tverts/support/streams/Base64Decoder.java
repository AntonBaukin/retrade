package com.tverts.support.streams;

/* standard Java classes */

import java.io.InputStream;
import java.io.IOException;

/**
 * Decodes BASE64-encoded text back into the bytes.
 * The origination of this class comes from Base64
 * Java project taken from SourceForge.
 *
 * @author Anton Baukin  (anton.baukin@gmail.com)
 * @author Robert Harder (rob@iharder.net)
 */
public final class Base64Decoder extends InputStream
{
	/* public: constructors */

	/**
	 * Creates the decoder with the standard input (decode) buffer
	 * to store 1024 3-byte triples.
	 */
	public Base64Decoder(InputStream stream)
	{
		this(stream, 1024*3);
	}

	public Base64Decoder(InputStream stream, int buffer_size)
	{
		if(buffer_size <= 0)
			throw new IllegalArgumentException();
		this.stream = stream;
		this.buffer = new byte[buffer_size];
	}

	/* public: InputStream interface */

	public int read()
	  throws IOException
	{
		//?: {has valid bytes in three array} take the current one
		if(to != ts)
			return (three >>> ((to += 8) - 8)) & 0xFF;
		to = ts = 0;

		int f = four;  //<-- stack access to four-bytes
		int p = 0;     //<-- position in the four-bytes (in BITS!)
		int r = -1;    //<-- the result

		while(r == -1)
		{
			//?: {has valid bytes in three array} take the current one
			if(to != ts)
				return (three >>> ((to += 8) - 8)) & 0xFF;

			//?: {need to upload the buffer}
			if(boffs >= bsize)
			{
				upload_to_buffer();

				//?: {has no more bytes in the input stream} stop reading
				if(bsize == 0) return -1;
			}

			//pickup the next character
			byte o = buffer[boffs++];   //<-- signed octet (character)
			byte d;

			//?: {is not a meaningful value} skip it
			if(((o >> 7) != 0) || ((d = DECODABET[o]) <= WS))
				continue;

			f |= ((d & 0xFF) << p); //<-- append to the decode array
			p += 8;

			//NOTE: that we waste the ending characters of the stream is
			//  being closed if they do not form a valid 4-character pack.

			//?: {has all the four bytes set} decode them
			if(p == 32)
			{
				four = f;
				decode4to3();
				four = f = p = 0;
			}
		}

		return r;
	}

	public int read(byte dst[], int off, int len)
	  throws IOException
	{
		byte[] buf = buffer;
		int    pos = off;    //<-- the position in 'dst'

		int    p   = 0;      //<-- position in the four-bytes (in BITS!)
		int    f   = four;   //<-+
		int    t   = three;  //<-- stack access optimizations

		while(len > 0)
		{
			//?: {has valid bytes in three array} take the current one
			if(to != ts)
			{
				dst[pos++] = (byte)(t >>> to);
				to += 8; len--;
				continue;
			}
			to = ts = 0;

			//?: {need to upload the buffer}
			if(boffs >= bsize)
			{
				upload_to_buffer();

				//NOTE: that we waste the ending characters of the stream
				//  if they do not form a full 4-character pack.

				//?: {has no more bytes in the input stream} stop reading
				if(bsize == 0)
					//?: {had bytes} return the number
					return (pos != off)?(pos - off):(-1);
			}

			//pickup the next character
			byte o = buf[boffs++];      //<-- signed octet (character)
			byte d;

			//?: {is not a meaningful value} skip it
			if(((o >> 7) != 0) || ((d = DECODABET[o]) <= WS))
				continue;

			f |= ((d & 0xFF) << p); //<-- append to the decode array
			p += 8;

			//NOTE: that we waste the ending characters of the stream is
			//  being closed if they do not form a valid 4-character pack.

			//?: {has all the four bytes set} decode them
			if(p == 32)
			{
				four = f;
				decode4to3();
				t    = three;
				four = f = p = 0;
			}
		}

		//HINT: here we do not return -1 in the case when the input
		// buffer was smaller than 4 characters.

		return pos - off;
	}

	public void close()
	  throws IOException
	{
		stream.close();
	}

	/* private: streaming procedures */

	/**
	 * Uploads bytes to the buffer from the input stream. Bytes of the buffer
	 * starting from <tt>valid_offset</tt> are moved to the beginning of the
	 * buffer.
	 */
	private void upload_to_buffer()
	  throws IOException
	{
		//NOTE: that here we always have (offset == bsize) 

		boffs = 0;
		bsize = stream.read(buffer);
		if(bsize < 0) bsize = 0;
	}

	/* private: decoding procedure */

	/**
	 * Translates a Base64 value to either its 6-bit reconstruction value
	 * or a negative number indicating some other meaning.
	 */
	private final static byte[] DECODABET =
	{
	  // Decimal  0 -  8
	  -9, -9, -9, -9, -9, -9, -9, -9, -9,

	  // Whitespace: Tab and Linefeed
	  -5, -5,

	  // Decimal 11 - 12
	  -9, -9,                                      // Decimal 11 - 12

	  // Whitespace: Carriage Return
	  -5,

	  // Decimal 14 - 26
	  -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,

	  // Decimal 27 - 31
	  -9, -9, -9, -9, -9,

	  // Whitespace: Space
	  -5,

	  // Decimal 33 - 42
	  -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,

	  // Plus sign at decimal 43
	  62,

	  // Decimal 44 - 46
	  -9, -9, -9,

	  // Slash at decimal 47
	  63,

	  // Numbers zero through nine
	  52, 53, 54, 55, 56, 57, 58, 59, 60, 61,

	  // Decimal 58 - 60
	  -9, -9, -9,

	  // Equals sign at decimal 61
	  -1,

	  // Decimal 62 - 64
	  -9, -9, -9,

	  // Letters 'A' through 'N'
	  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
	  // Letters 'O' through 'Z'
	  14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,

	  // Decimal 91 - 96
	  -9, -9, -9, -9, -9, -9,

	  // Letters 'a' through 'm'
	  26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
	  // Letters 'n' through 'z'
	  39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,

	  -9, -9, -9, -9
	};

	/**
	 * Indicates white space in encoding.
	 */
	private final static byte WS = -5;

	/**
	 * Indicates white space in encoding.
	 */
	private final static int  EQ = 0xFF;

	/**
	 * Converts four Base-64 characters into up to 3 bytes. 
	 */
	private void decode4to3()
	{
		int f = four;  //<-- stack access optimizations
		int t;
		int a;
		int b;

		//?: {????} has all three bytes
		if((f & 0xFF000000) != (EQ << 24))
		{
			a  = ((f             ) >>> 24);
			b  = ((f & 0x0000FF00) <<   4);
			a |= ((f & 0x00FF0000) >>> 10);
			b |= ((f & 0x000000FF) <<  18);
			t  = a | b;

			three = ((a & 0xFF) << 16) | (t & 0xFF00) | (t >>> 16);
			ts    = 24;
			return;
		}

		//?: {???=} has two bytes
		if((f & 0x00FF0000) != (EQ << 16))
		{
			a  = ((f & 0x0000FF00) <<   4);
			b  = ((f & 0x00FF0000) >>> 10);
			a |= ((f & 0x000000FF) <<  18);
			t  = b | a;

			three = (t >>> 16) | (t & 0xFF00);
			ts    = 16;
			return;
		}

		//?: {??==} has one byte
		if((f & 0x0000FF00) != (EQ << 8))
		{
			t = ((f & 0x000000FF) <<  18) |
			    ((f & 0x0000FF00) <<   4);

			three = (t >>> 16);
			ts    = 8;
			return;
		}

		//!: there is no bytes
		ts = 0;
	}

	/* private: streaming data */

	private InputStream  stream;
	private byte[]       buffer;
	private int          bsize;  //<-- the number of valid bytes in the buffer
	private int          boffs;  //<-- the position in the buffer 

	/* private: decoding data */

	private int four;   //<-- 'buffer' of four income characters
	private int three;  //<-- 'buffer' of three outcome bytes
	private int to;     //<-- offset in 'three' array (in BITS!)
	private int ts;     //<-- the number of valid BITS in 'three' array
}