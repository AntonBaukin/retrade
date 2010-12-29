package com.tverts.mp;

/* standard Java classes */

import java.util.HashMap;

/**
 * Suppose that your thread must wait till one or more
 * operations are completed. Operations may be initiated
 * during the waiting. At the moment when all of them
 * are complete, all waiting threads are released.
 *
 * This implementation handles the case when the same
 * flag is set more than one time. If some procedure
 * sets a flag, it must release it lately.
 *
 * It is also possible to release flag before occupying
 * it what can reduce additional synchronizations.
 * If some flag is released before occupation, the
 * latter would not cause the following wait request
 * to block (on this flag).
 *
 * As the result: a flag is removed from the set when
 * it's occupy/release counter equals to zero. When
 * all the flags are removed, the waiting threads are
 * resumed.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public final class WaitEmptyFlags
{
	/* public: WaitEmptyFlags interface */

	/**
	 * Tells to wait on the following flags.
	 * (Note about pre-released flags.)
	 */
	public void    occupy(Object... flags)
	{
		synchronized(mutex)
		{
			for(Object flag : flags)
			{
				Integer n = this.flags.get(flag);
				n = (n == null)?(1):(n + 1);

				if(n == 0)
					this.flags.remove(flag);
				else
					this.flags.put(flag, n);
			}
		}
	}

	/**
	 * Tells that the specified flags are
	 * no busy. Note about pre-release and
	 * the occupy/release counter.
	 */
	public void    release(Object... flags)
	{
		synchronized(mutex)
		{
			for(Object flag : flags)
			{
				Integer n = this.flags.get(flag);

				//~: we allow negative values
				//   to handle pre-releases.
				if(n == null)
					n = -1;
				else if(n == 1)
					n = null;
				else
					n = n - 1;

				if(n == null)
					this.flags.remove(flag);
				else
					this.flags.put(flag, n);
			}

			//?: {all the flags are cleared} notify
			if(this.flags.isEmpty())
				mutex.notifyAll();
		}
	}

	/**
	 * This call blocks the invoking thread
	 * until all the flags are removed from
	 * the set, what means that occupy/release
	 * queries on all the flags are balanced
	 * to zero.
	 */
	public void    waitEmptyFlags()
	  throws InterruptedException
	{
		synchronized(mutex)
		{
			while(!this.flags.isEmpty())
				mutex.wait();
		}
	}

	/**
	 * The same wait as {@link #waitEmptyFlags()},
	 * but with the timeout. Returns {@code true} if
	 * wait operation was successful.
	 */
	public boolean waitEmptyFlags(long timeout)
	  throws InterruptedException
	{
		if(timeout < 0L)
			throw new IllegalArgumentException();

		synchronized(mutex)
		{
			while(!this.flags.isEmpty())
				mutex.wait(timeout);
			return this.flags.isEmpty();
		}
	}

	/* private: mutex state */

	private HashMap<Object, Integer>
	                     flags = new HashMap<Object, Integer>(7);
	private final Object mutex = new Object();
}