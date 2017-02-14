package com.tverts.support.misc;

/* Java */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Collects and logs statistics on sample
 * time measures. Logs it not more than
 * once per second (as debug).
 *
 * @author anton.baukin@gmail.com
 */
public final class Sampler
{
	public Sampler(String log)
	{
		this.log = EX.asserts(log);
	}

	/**
	 * Logging destination.
	 */
	public final String log;


	/* Sampler Interface */

	/**
	 * Increments the counter having no time.
	 */
	public void inc(String what)
	{
		//~: increment the counter
		samples.computeIfAbsent(what, Sample::new).
		  n.getAndIncrement();

		this.log(); //<-- log the report
	}

	/**
	 * Increments the counter and add the time delta
	 * with the current nano time and the given one.
	 */
	public void inc(String what, long nt)
	{
		//~: get the sample
		final Sample s = samples.
		  computeIfAbsent(what, Sample::new);

		//~: increment the counter
		s.n.incrementAndGet();

		//~: accumulate the elsapsed time
		s.nt.addAndGet(System.nanoTime() - nt);

		this.log(); //<-- log the report
	}

	private final Map<String, Sample> samples =
	  new ConcurrentHashMap<>();

	/**
	 * Time of previous logging request.
	 */
	private final AtomicLong logt =
	  new AtomicLong();


	/* Object & Logging */

	public String  toString()
	{
		StringBuilder b = new StringBuilder(128);

		//c: for each sample collected
		samples.forEach((k, s) ->
		{
			if(b.length() != 0)
				b.append(", ");

			b.append(s.what).append(" [");
			b.append(s.n);

			final long dt = s.nt.get();
			if(dt != 0L)
			{
				b.append(" in ");
				LU.td(b, dt / 1000000);
			}

			b.append(']');
		});

		return b.toString();
	}

	public boolean log()
	{
		final long ts = System.currentTimeMillis();
		final long xt = logt.getAndSet(ts);

		//?: {it's too early}
		if(xt + 1000 > ts)
		{
			logt.compareAndSet(ts, xt);
			return false;
		}

		//~: log
		LU.D(log, this.toString());

		return true;
	}


	/* Sample Record  */

	public static final class Sample
	{
		public Sample(String what)
		{
			this.what = what;
		}

		public final String what;

		public final AtomicLong n =
		  new AtomicLong();

		/**
		 * Nano time accumulated.
		 * Zero if not considered.
		 */
		public final AtomicLong nt =
		  new AtomicLong();
	}
}