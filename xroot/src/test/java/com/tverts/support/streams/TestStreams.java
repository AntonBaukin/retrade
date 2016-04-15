package com.tverts.support.streams;

/* standard Java classes */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

/* JUnit library */

import static org.junit.Assert.assertArrayEquals;


/**
 * Tests various supports classes separately.
 *
 * @author anton.baukin@gmail.com
 */
public class TestStreams
{
	/* test entry points */

	@org.junit.Test
	public void testBytesStream()
	  throws Exception
	{
		Random gen = new Random();

		for(int I = 0;(I < 512);I++)
		{
			byte[] src = new byte[gen.nextInt(8193)];
			gen.nextBytes(src);

			BytesStream bs = new BytesStream();
			InputStream is = bs.inputStream();
			BytesStream xs = new BytesStream();
			int         po = 0;
			int         sz = src.length;

			while(sz > 0)
			{
				int s = gen.nextInt(sz + 1);

				if(gen.nextBoolean())
					bs.write(new ByteArrayInputStream(src, po, s));
				else
					bs.write(src, po, s);
				po += s; sz -= s;

				xs.write(is);
			}

			assertArrayEquals(src, bs.bytes());

			ByteArrayOutputStream bos = new ByteArrayOutputStream(src.length);
			bs.copy(bos); bos.close(); bs.close();
			assertArrayEquals(src, bos.toByteArray());

			bos = new ByteArrayOutputStream(src.length);
			xs.copy(bos); bos.close(); xs.close();
			assertArrayEquals(src, bos.toByteArray());
		}
	}
}