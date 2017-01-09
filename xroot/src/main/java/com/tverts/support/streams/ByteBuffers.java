package com.tverts.support.streams;

/* Java */

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Weak buffer of 512 bytes arrays.
 *
 * @author anton.baukin@gmail.com
 */
public final class ByteBuffers
{
	/* Byte Buffers Singleton */

	public static final ByteBuffers INSTANCE =
	  new ByteBuffers();


	/* Byte Buffers */

	public byte[] get()
	{
		final WeakReference<Queue<byte[]>> wr = this.pool.get();
		final Queue<byte[]> q = (wr == null)?(null):(wr.get());

		//?: {queue does not exist}
		if(q == null)
			return new byte[512];

		//~: poll the quueue
		final byte[] b = q.poll();

		//~: return | create
		return (b != null)?(b):(new byte[512]);
	}

	public void   free(Collection<byte[]> bufs)
	{
		//?: {nothing to do}
		if((bufs == null) || bufs.isEmpty())
			return;

		//~: existing pool
		final WeakReference<Queue<byte[]>> wr = this.pool.get();
		final Queue<byte[]> q = (wr == null)?(null):(wr.get());

		if(q != null) //?: {queue does exist}
		{
			//~: add valid buffers
			for(byte[] buf : bufs)
				if((buf != null) && (buf.length == 512))
					q.offer(buf);

			return;
		}

		//~: create own pool
		final Queue<byte[]> q2 = new ConcurrentLinkedQueue<>();
		final WeakReference<Queue<byte[]>> wr2 =
		  new WeakReference<>(q2);

		//?: {swapped it not to the field} waste the buffers
		if(!this.pool.compareAndSet(wr, wr2))
			return;

		//~: add valid buffers
		for(byte[] buf : bufs)
			if((buf != null) && (buf.length == 512))
				q2.offer(buf);
	}

	public void   free(byte[] buf)
	{
		EX.assertx((buf != null) && (buf.length == 512));
		this.free(Collections.singleton(buf));
	}

	private final AtomicReference<WeakReference<Queue<byte[]>>>
	  pool = new AtomicReference<>();
}