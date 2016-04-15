package com.tverts.auth.server.support;

/**
 * Support class for transfer encodings.
 *
 * @author anton.baukin@gmail.com
 */
public class Encodings
{
	/* hexadecimal strings */

	public static char[]  bytes2hex(byte[] a)
	{
		if(a == null) return null;

		char[] c = new char[a.length * 2];

		for(int i = 0, j = 0;(i < a.length);i++, j += 2)
		{
			int b = a[i];

			//HINT: higher comes first!

			c[j    ] = BYTES2HEX[ (b & 0xF0) >> 4 ];
			c[j + 1] = BYTES2HEX[ (b & 0x0F)      ];
		}

		return c;
	}

	private static char[] BYTES2HEX =
	  "0123456789ABCDEF".toCharArray();


	public static byte[]  hex2bytes(char[] c)
	{
		byte[] a = new byte[c.length / 2];
		int    l = 0, h = 16;

		for(char xc : c)
		{
			int x = (int)xc;

			//?: {not ASCII character}
			if((x & 0xFF00) != 0)
				continue;

			int b = HEX2BYTES[x];

			//?: {not a HEX character}
			if(b == 16)
				continue;

			//HINT: higher comes first!

			//?: {higher is not set yet}
			if(h == 16)
			{
				h = b;
				continue;
			}

			//HINT: 'b' is a lower part here...
			a[l++] = (byte)(b | (h << 4));
			h = 16;
		}

		//?: {resulting array is longer}
		if(l != a.length)
		{
			byte[] a2 = new byte[l];
			System.arraycopy(a, 0, a2, 0, l);
			a = a2;
		}

		return a;
	}

	private static byte[] HEX2BYTES =
	  new byte[256]; //<-- values are: 0 .. 15, 16

	static
	{
		char[] hex = "0123456789abcdef".toCharArray();
		char[] HEX = "0123456789ABCDEF".toCharArray();


		for(int i = 0;(i < 256);i++)
			HEX2BYTES[i] = 16;

		for(byte j = 0;(j < 16);j++)
		{
			HEX2BYTES[((int)hex[j]) & 0xFF] = j;
			HEX2BYTES[((int)HEX[j]) & 0xFF] = j;
		}
	}
}