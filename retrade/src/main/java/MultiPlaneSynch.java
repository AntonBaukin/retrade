import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Demonstration all-in-single-file program
 * for Multi-Plane Synchronization concept.
 *
 * @author anton.baukin@gmail.com.
 */
public class MultiPlaneSynch
{
	/* Global Parameter of the Tests */

	/**
	 * The number of working threads.
	 * Value 1 means execution by the Master only.
	 */
	static final int   THREADS  = 2;

	/**
	 * The number of queue positions ahead the Master
	 * cursor to forward the execution. Depends on
	 * the number of competing working threads.
	 */
	static final int   PRESEE   = THREADS * 2;

	/**
	 * Delay (in milliseconds) introduced by
	 * the database when accessing client data
	 * for the first time.
	 *
	 * Zero value means no delay.
	 */
	static final int   DELAY    = 0;

	/**
	 * The total number of test requests.
	 */
	static final int   REQUESTS = 100000;

	/**
	 * The number of test clients.
	 */
	static final int   CLIENTS  = 1000;

	/**
	 * The number of the generated requests executions.
	 */
	static final int   RUNS     = 10;

	/**
	 * Generator seed used to create test requests.
	 */
	static Long        SEED     = 1L; //<-- undefined means current time

	/**
	 * Range of initial money amounts of the clients.
	 */
	static final int[] INITIAL  = new int[] { 100, 1000 };

	/**
	 * Range of buy request cost.
	 */
	static final int[] BUY      = new int[] {  20, 250 };

	/**
	 * Range of credit request cost.
	 */
	static final int[] CREDIT   = new int[] {  10, 200 };


	/* Program Entry Point */

	public static void main(String[] argv)
	  throws InterruptedException
	{
		//~: test the parameters
		assert THREADS  >= 1;
		assert PRESEE   >= 1;
		assert DELAY    >= 0;
		assert REQUESTS >= 1;
		assert CLIENTS  >= 2;
		assert RUNS     >= 1;

		assert INITIAL[0] > 0 && INITIAL[0] <= INITIAL[1];
		assert BUY[0]     > 0 && BUY[0]     <= BUY[1];
		assert CREDIT[0]  > 0 && CREDIT[0]  <= CREDIT[1];

		if(SEED == null) SEED = System.currentTimeMillis();

		//~: warm-up with self-tests
		testDebtsBuffer();

		//~: generate test requests & clients
		Request[] requests = generateRequests();
		Database  database = generateDatabase();

		Locale.setDefault(Locale.US);
		System.out.printf("[%d] Workers; Requests [%d], Seed [%d]; [%d] runs:\n",
		  THREADS, REQUESTS, SEED, RUNS
		);

		//c: do test runs
		long[] times = new long[RUNS];
		for(int run = 0;(run < RUNS);run++)
			times[run] = testRun(run+1, requests, new Database(database));

		//~: average run time
		int TX = (RUNS > 5)?(3):(0);
		long time = 0L; for(int i = TX;(i < RUNS);i++) time += times[i];
		System.out.printf("Average run time: %.1f\n", (double)time / (RUNS - TX));
	}


	/* Requests to the System */

	static abstract class Request
	{
		/**
		 * The buyer.
		 */
		public final int a;

		/**
		 * The recipient.
		 */
		public final int b;

		/**
		 * The cost of the deal.
		 */
		public final int s;

		Request(int a, int b, int s)
		{
			this.a = a;
			this.b = b;
			this.s = s;
		}
	}

	/**
	 * Client buys if currently possess all
	 * the money needed for the deal.
	 */
	static final class Buy extends Request
	{
		public Buy(int a, int b, int s)
		{
			super(a, b, s);
		}
	}

	/**
	 * Client buys anyway, and the money lack
	 * is added to his debts list.
	 */
	static final class Credit extends Request
	{
		public Credit(int a, int b, int s)
		{
			super(a, b, s);
		}
	}

	static Request[] generateRequests()
	{
		Request[] res = new Request[REQUESTS];
		Random    gen = (SEED != null)?(new Random(SEED)):(new Random());

		for(int i = 0;(i < REQUESTS);i++)
		{
			int a = gen.nextInt(CLIENTS);
			int b = a; while(b == a)
				b = gen.nextInt(CLIENTS);

			//?: {buy}
			if(gen.nextBoolean())
				res[i] = new Buy(a, b,
				  BUY[0] + gen.nextInt(BUY[1] - BUY[0] + 1));
			//~: credit
			else
				res[i] = new Credit(a, b,
				  CREDIT[0] + gen.nextInt(CREDIT[1] - CREDIT[0] + 1));
		}

		return res;
	}

	static Database generateDatabase()
	{
		Database res = new Database(CLIENTS);
		Random   gen = (SEED != null)?(new Random(SEED)):(new Random());

		for(int id = 0;(id < CLIENTS);id++)
			res.client(id).money = INITIAL[0] +
			  gen.nextInt(INITIAL[1] - INITIAL[0] + 1);

		return res;
	}


	/* Clients Database */

	/**
	 * Global data.
	 */
	static final class Database
	{
		/* public: constructor */

		public Database(int clients)
		{
			this.clients = new Client[clients];
			for(int id = 0;(id < clients);id++)
				this.clients[id] = new Client(id);
			this.delays = null;
		}

		public Database(Database s)
		{
			//~: copy the clients
			this.clients = new Client[s.clients.length];
			int i = 0; for(Client c : s.clients)
				this.clients[i++] = Client.copy(c);

			if(DELAY == 0) this.delays = null; else
			{
				this.delays = new int[clients.length];
				Arrays.fill(this.delays, DELAY);
			}
		}


		/* Database Access */

		/**
		 * Thread-unsafe operation to directly access client.
		 */
		public Client  client(int id)
		{
			return clients[id];
		}

		/**
		 * Thread-safe operation to get a full copy
		 * of the client data.
		 */
		public Client  copy(int id)
		{
			final Client x = clients[id];

			synchronized(x)
			{
				if(DELAY != 0) delay(id);
				return Client.copy(x);
			}
		}

		/**
		 * Thread-safe operation to get a full copy
		 * of the client data if it differs from
		 * the data given.
		 *
		 * Returns the argument if it the same, else a copy.
		 */
		public Client  copy(Client s)
		{
			final Client x = clients[s.id];

			synchronized(x)
			{
				if(DELAY != 0) delay(s.id);

				if(x.equals(s))
					return s;
				else
					return Client.copy(clients[s.id]);
			}
		}

		/**
		 * Thread-safe operation to assign the global data
		 * of the client. Only Master thread is allowed this!
		 */
		public void      assign(Client s)
		{
			final Client x = clients[s.id];

			synchronized(x)
			{
				x.assign(s);
			}
		}

		/**
		 * Calculates the resulting hash of the database.
		 * Needed to check the results.
		 */
		public Hash    hash()
		{
			Hash hash = new Hash();

			for(Client c : clients)
				c.hash(hash);

			return hash;
		}

		private void   delay(int id)
		{
			if(delays[id] == 0) return;

			final Object w = new Object();
			try
			{
				synchronized(w)
				{
					w.wait(delays[id]);
					delays[id] = 0;
				}
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}


		/* private: the clients */

		private final Client[] clients;
		private final int[]    delays;
	}


	/* Client Data Item */

	static final class Client
	{
		/* Client Data */

		public final int id;
		public int       money;
		public Debts     debts;


		/* public: constructors */

		public Client(int id)
		{
			this.id    = id;
			this.debts = new Debts();
		}

		public static Client copy(Client s)
		{
			return new Client(s);
		}

		private Client(Client s)
		{
			this.id    = s.id;
			this.money = s.money;
			this.debts = new Debts(s.debts);
		}


		/* Client Operations */

		public void    assign(Client s)
		{
			this.money = s.money;
			this.debts = new Debts(s.debts);
		}

		/**
		 * Compares the data of the same client clone.
		 */
		public boolean equals(Client s)
		{
			return (this.money == s.money) && this.debts.equals(s.debts);
		}

		public void    hash(Hash hash)
		{
			hash.update(id);
			hash.update(money);
			debts.rehash();
		}
	}


	/* Debts List */

	static final class Debts
	{
		/* public: constructors */

		public Debts()
		{
			this.hash = new Hash();
		}

		public Debts(Debts d)
		{
			//?: {has no data}
			if(d.line == null)
				this.hash = new Hash();
			//?: {has no data rounded}
			else if(d.e > d.b)
			{
				e = d.e - d.b;
				this.line = new int[e + 8];
				System.arraycopy(d.line, d.b, this.line, 0, e);
			}
			//~: has two data parts
			else
			{
				e = d.line.length - d.b + d.e;
				this.line = new int[e + 8];
				System.arraycopy(d.line, d.b, this.line, 0, d.line.length - d.b);
				System.arraycopy(d.line, 0, this.line, d.line.length - d.b, d.e);
			}

			//~: clone the hash
			d.rehash();
			this.hash = d.hash.copy();
		}


		/* Debts */

		public boolean isEmpty()
		{
			return (this.line == null);
		}

		/**
		 * Returns the top creditor client ID.
		 */
		public int     client()
		{
			return line[b];
		}

		/**
		 * Returns the debt for the top creditor client.
		 */
		public int     debt()
		{
			return line[b+1];
		}

		/**
		 * Updates the debt for the top creditor.
		 */
		public void    debt(int d)
		{
			updated = updated || (line[b+1] != d);
			line[b+1] = d;
		}

		/**
		 * Removes the first (current) debt.
		 */
		public void    pop()
		{
			updated = true;

			//HINT: e is always < line.length
			if((b += 2) == line.length) b = 0;

			//?: {has no items left}
			if(b == e)
			{
				b = e = 0;
				this.line = null;
			}
		}

		/**
		 * Adds the debt to the end of the buffer.
		 */
		public void    add(int client, int debt)
		{
			int x = e + 2;
			updated = true;

			//HINT: e is always < line.length

			//?: {has free space}
			if(((e < b) & (x <= b)) || (line == null) || ((e > b) & (x <= line.length)))
			{
				if(line == null)
					this.line = new int[8];

				line[e]   = client;
				line[e+1] = debt;
				e = (x == line.length)?(0):(x);

				return;
			}

			//~: allocate the new buffer
			int[] temp = new int[line.length*2];

			//~: {has no data rounded}
			if(e > b)
				System.arraycopy(line, b, temp, 0, x = e - b);
			//~: copy the tail and the head
			else
			{
				System.arraycopy(line, b, temp, 0, x = line.length - b);
				System.arraycopy(line, 0, temp, x, e);
				x += e;
			}

			//~: assign the buffer, re-call
			this.line = temp;
			b = 0; e = x;
			add(client, debt);
		}

		public void    hash(Hash hash)
		{
			//?: {has no data}
			if(line == null) return;

			//~: {has no data rounded}
			if(e > b)
				for(int i = b;(i < e);i++)
					hash.update(line[i]);
			//~: scan two parts
			else
			{
				for(int i = b;(i < line.length);i++)
					hash.update(line[i]);
				for(int i = 0;(i < e);i++)
					hash.update(line[i]);
			}
		}

		public void    rehash()
		{
			if(updated)
			{
				updated = false;
				this.hash.reset();
				hash(this.hash);
			}
		}

		public boolean equals(Debts d)
		{
			this.rehash(); d.rehash();
			return hash.equals(d.hash);
		}


		/* private: debts data */

		/**
		 * Line is a cyclic buffer where stored
		 * pairs of (client, debt).
		 */
		private int[]   line;

		/**
		 * Begin position in the buffer, and end position
		 * following the last pair. Note that as the buffer
		 * is cyclic, end may be before the head.
		 */
		private int     b, e;

		private boolean updated;

		/**
		 * The hash of the debts line;
		 */
		private Hash    hash;
	}


	/* Murmur Hash 3 */

	/**
	 * Implementation based on 128-bit Murmur hash
	 * function introduced by Austin Appleby.
	 */
	static final class Hash
	{
		/* Hash */

		public Hash()
		{
			reset();
		}

		public void    update(int v)
		{
			long x = C + v, y = ~C + v;

			//~: mix v
			x *= X; y *= Y;
			x = Long.rotateLeft(x, 31);
			y = Long.rotateLeft(y, 33);
			x *= Y; y *= X;

			//~: mix x
			x = this.x ^ x;
			x = Long.rotateLeft(x, 27) + this.y;
			x = x*5 + 0x52DCE729;

			//~: mix y
			y = this.y ^ y;
			y = Long.rotateLeft(y, 31) + x;
			y = y*5 + 0x38495AB5;

			//~: results
			this.x = x; this.y = y;
		}

		public void    reset()
		{
			this.x =  S;
			this.y = ~S;
		}

		public boolean equals(Hash h)
		{
			return (x == h.x) & (y == h.y);
		}

		public Hash    copy()
		{
			Hash r = new Hash();
			r.x = x; r.y = y;
			return r;
		}

		public String  toString()
		{
			StringBuilder s = new StringBuilder(32);
			String        a = Long.toHexString(x).toUpperCase();
			String        b = Long.toHexString(y).toUpperCase();

			for(int i = a.length();(i < 16);i++)
				s.append('0');
			s.append(a);

			for(int i = b.length();(i < 16);i++)
				s.append('0');
			s.append(b);

			return s.toString();
		}


		/* Constants */

		static final long S = SEED;
		static final long X = 0x87C37B91114253D5L;
		static final long Y = 0x4CF5AD432745937FL;
		static final long C = 0x5555555555555555L;


		/* private: hash state */

		private long x, y;
	}


	/* self-tests: debts and hashes */

	private static void testDebtsBuffer()
	{
		final int    CYCLES = 128; //<-- the number of test cycles
		final int    STEPS  = 512; //<-- the number of probes per cycle
		final Random GEN    = new Random(1L);

		for(int cycle = 0;(cycle < CYCLES);cycle++)
		{
			Debts             deb = new Debts();
			LinkedList<int[]> ref = new LinkedList<>();

			for(int step = 0;(step < STEPS);step++)
			{
				int w = GEN.nextInt(10);

				//?: {0..2 add some items}
				if(w <= 2)
				{
					int N = GEN.nextInt(100);

					for(int n = 0;(n < N);n++)
					{
						int[] x = new int[] {GEN.nextInt(100), GEN.nextInt(1000)};
						ref.add(x);
						deb.add(x[0], x[1]);
						cmp(deb, ref.getFirst());
					}
				}

				//?: {3..5 pop some items}
				if(w > 2 && w <= 5)
				{
					int N = GEN.nextInt(100);

					for(int n = 0;(n < N);n++)
					{
						if(ref.isEmpty() || deb.isEmpty())
						{
							assert ref.isEmpty() && deb.isEmpty();
							break;
						}

						int[] x = ref.removeFirst();
						cmp(deb, x);
						deb.pop();
					}

					if(ref.isEmpty() || deb.isEmpty())
						assert ref.isEmpty() && deb.isEmpty();
				}

				//?: {6..7 clone debts}
				if(w > 5 && w <= 7)
				{
					Debts clone = new Debts(deb);

					for(int[] x : ref)
					{
						cmp(clone, x);
						clone.pop();
					}

					assert clone.isEmpty();
				}

				//?: {7..8 copy and compare}
				if(w > 7 && w <= 8)
				{
					Debts copy = new Debts();

					for(int[] x : ref)
					{
						copy.add(x[0], x[1]);
						cmp(copy, ref.getFirst());
					}

					assert copy.equals(deb);
				}

				//?: {9 drain all}
				if(w == 9)
				{
					for(int[] x : ref)
					{
						cmp(deb, x);
						deb.pop();
					}

					assert deb.isEmpty();
					ref.clear();
				}
			}
		}
	}

	private static void cmp(Debts deb, int[] x)
	{
		assert !deb.isEmpty();
		assert (deb.client() == x[0]) & (deb.debt() == x[1]);
	}


	/* Execution Queue */

	/**
	 * Queue with support for Master and Support
	 * threads processing. In this implementation
	 * Master and Support are temporary roles
	 * assigned to a working thread that selects
	 * the pending request.
	 */
	static final class Queue
	{
		/* Queue Parameters */

		public final Request[] requests;
		public final Database  database;

		public Queue(Request[] requests, Database database)
		{
			this.requests = requests;
			this.database = database;

			this.datas    = new Data[requests.length];
			for(int i = 0;(i < requests.length);i++)
				datas[i] = new Data(requests[i], i);
		}


		/* Queue Access */

		public Data next(Data data)
		{
			int index = -1;

			//?: {has data to release}
			if(data != null)
			{
				index = data.index;

				//?: {is currently Master} increment the cursor
				if(data.master)
					cursor++;

				//!: release the lock
				data.unlock(); //<-- master lock is not removed ever
				data = null;
			}

			//c: queue competition cycle
			while(true)
			{
				//HINT: this unsafe assignment is still valid
				//  as we always lock Data via atomic CAS.

				final int x = cursor;

				//?: {processed all the requests}
				if(x >= requests.length)
					return null;

				//?: {locked it as a master}
				if((data = datas[x]).lock(true))
				{
					data.master = true; //<-- process as Master
					return data;
				}

				//HINT: we take item after the current as we checked it

				//~: the next index to take
				index = (index <= x)?(x + 1):(index + 1);

				//?: {the queue is almost done}
				if(index >= requests.length)
					continue; //<-- see to be Master

				//?: {too quick} stay hot
				if(index > x + PRESEE)
					continue; //<-- compete again for the next request

				//~: take fro pre-execute
				data = datas[index];

				//?: {obtained lock as a Support thread}
				if((data = datas[index]).lock(false))
				{
					//HINT: in this implementation we do not allow
					//  a request to be pre-processed twice even
					//  if currently it is not consistent - Master
					//  will re-execute such a requests.

					//?: {data not processed} take it
					if(data.results == null)
						return data;

					//!: unlock & take the next
					data.unlock();
				}
			}
		}

		/**
		 * Request execution context.
		 */
		private final Data[] datas;

		/**
		 * Current request to process.
		 */
		private volatile int cursor;


		/* Queue Shutdown */

		public void workerExit()
		{
			done.countDown();
		}

		/**
		 * Waits the queue to be fully processed.
		 */
		public void join()
		{
			try
			{
				done.await();
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}

		private final CountDownLatch done =
		  new CountDownLatch(THREADS);
	}

	/**
	 * Processing data of one of the Support threads.
	 */
	static final class Data
	{
		/* Processing Data */

		public final Request request;
		public final int     index;

		public Data(Request request, int index)
		{
			this.request = request;
			this.index   = index;
		}

		/**
		 * Assigned to true (only once) when working
		 * thread access this request as a Master.
		 */
		public boolean      master;

		/**
		 * The client data before they were processed.
		 * Assigned by a Support thread had pre-executed
		 * this request.
		 */
		public List<Client> initial;

		/**
		 * The client data after they were processed.
		 */
		public List<Client> results;


		/* Synchronization */

		/**
		 * Working thread obtains exclusive lock on
		 * the data as Master or as a Support thread.
		 *
		 * This call is never blocked: false is returned
		 * when the data object is already locked.
		 *
		 * Note that Master locked is never removed.
		 * Implemented with atomic CAS.
		 */
		public boolean lock(boolean master)
		{
			return lock.compareAndSet(0, (master)?(2):(1));
		}

		public void    unlock()
		{
			//HINT: we unlock only for Support thread lock
			// (expected value is 1). Master lock stays forever.

			lock.compareAndSet(1, 0);
		}

		/**
		 * 0 when unlocked;
		 * 1 when locked by a Support thread;
		 * 2 when locked by a Master thread.
		 */
		private final AtomicInteger lock = new AtomicInteger();
	}


	/* Working Thread Base */

	/**
	 * Implements the requests processing
	 * without any synchronization issues.
	 */
	static abstract class Processing
	{
		/* protected: requests execution */

		protected abstract Client client(int id);

		protected final void      execute(Object request)
		{
			//?: {buy}
			if(request instanceof Buy)
				buy((Buy) request);
			//~: credit
			else
				credit((Credit) request);
		}


		/* private: execution procedures */

		private void buy(Buy r)
		{
			Client c = client(r.a);

			//?: {has no enough money}
			if(c.money < r.s)
				return;

			//~: take the money
			c.money -= r.s;

			//~: give
			give(r.b, r.s);
		}

		private void credit(Credit r)
		{
			Client c = client(r.a);

			//?: {client has enough money}
			if(c.money >= r.s)
			{
				//~: take the money
				c.money -= r.s;

				//~: give
				give(r.b, r.s);

				return;
			}

			//~: calculate the debt
			int d = r.s - c.money;

			//~: now has no money
			int s = c.money;
			c.money = 0;

			//~: make the debt
			c.debts.add(r.b, d);

			//~: give the money
			if(s != 0)
				give(r.b, s);
		}

		private void give(int client, int s)
		{
			Client c = client(client);

			//?: {client has no debts}
			if((c.money != 0) || c.debts.isEmpty())
			{
				c.money += s;
				return;
			}

			//~: return all the debts
			while((s > 0) && !c.debts.isEmpty())
			{
				//~: target creditor
				int b = c.debts.client();

				//~: take the most
				int d = c.debts.debt();
				int x = Math.min(s, d);

				//?: {whole debt}
				if(x == d)
					c.debts.pop();
				//~: reduce the debt
				else
					c.debts.debt(d - x);

				//~: subtract the amount
				s -= x;

				//~: give that amount
				give(b, x);
			}

			//~: add the rest
			c.money += s;
		}
	}


	/* Master Thread */

	static final class Worker extends Processing implements Runnable
	{
		/* public: constructor */

		public Worker(Queue queue)
		{
			this.queue = queue;
		}


		/* Worker */

		public void run()
		{
			started = System.currentTimeMillis();

			try
			{
				while((data = queue.next(data)) != null)
					if(data.master)
						master();
					else
						support();
			}
			finally
			{
				finished = System.currentTimeMillis();
				queue.workerExit();
			}

		}

		public int  getHits()
		{
			return hits;
		}

		public long getStarted()
		{
			return started;
		}

		public long getFinished()
		{
			return finished;
		}


		/* protected: requests execution */

		protected Client client(int id)
		{
			//?: {found it in the local cache}
			Client c = cache.get(id);
			if(c != null) return c;

			//~: load from the database & cache it
			c = queue.database.copy(id);
			cache.put(c.id, c);

			//?: {master} nothing else
			if(data.master)
				return c;

			//~: remember the initial version
			initial.add(Client.copy(c));

			//~: add to the results
			results.add(c);

			return c;
		}

		private void     master()
		{
			cache.clear();

			//?: {the resulting data may be applied}
			if(consistent())
				apply(data.results);
			//~: execute again
			else
			{
				//~: execute the request
				execute(data.request);

				//~: apply all the changes to the database
				apply(cache.values());
			}
		}

		private void     support()
		{
			//~: clear the cache
			cache.clear();

			//~: allocate execution data
			this.initial = new ArrayList<>(4);
			this.results = new ArrayList<>(4);

			//~: execute the request
			execute(data.request);

			//~: save data results
			data.initial = this.initial;
			data.results = this.results;
		}

		private boolean  consistent()
		{
			if((data.initial == null) | (data.results == null))
				return false;

			for(Client o : data.initial)
			{
				//~: get the data copy
				Client x = queue.database.copy(o);
				cache.put(x.id, x);

				//?: {current state differs}
				if(x != o)
					return false;
			}

			//!: support work is not wasted
			hits++;

			return true;
		}

		private void     apply(Collection<Client> results)
		{
			for(Client r : results)
				queue.database.assign(r);
		}


		/* private: worker state */

		private final Queue queue;

		/**
		 * Current execution context.
		 */
		private Data        data;

		/**
		 * When thread executes as a Master it
		 * measures the consistency hits.
		 */
		private int         hits;

		private long        started;
		private long        finished;

		/**
		 * Cache where clients are held during
		 * a request processing.
		 */
		private final Map<Integer, Client> cache = new HashMap<>(7);

		/**
		 * Initial and final processing states
		 * used when working as a Support.
		 */
		private List<Client> initial, results;
	}

	static long testRun(int run, Request[] requests, Database database)
	  throws InterruptedException
	{
		//~: create the queue
		Queue queue = new Queue(requests, database);

		//~: create the workers
		Worker[] workers = new Worker[THREADS];
		Thread[] threads = new Thread[THREADS];

		for(int i = 0;(i < THREADS);i++)
		{
			workers[i] = new Worker(queue);
			threads[i] = new Thread(workers[i]);
			threads[i].setName("Worker-" + i);
		}

		//~: start the threads
		for(Thread thread : threads)
			thread.start();

		//~: wait the queue
		queue.join();

		//~: timings
		long started  = Long.MAX_VALUE; //<-- earliest start time
		long finished = 0L;             //<-- latest finish time
		for(Worker w : workers)
		{
			if(started > w.getStarted())
				started = w.getStarted();
			if(finished < w.getFinished())
				finished = w.getFinished();
		}

		//~: collect the hits
		int hits = 0; for(Worker w : workers)
			hits += w.getHits();

		//~: print the timing and the hash
		System.out.printf("%2d %5d  %5.1f  %s\n",
		  run, (finished-started), (100.0 * hits / requests.length),
		  database.hash().toString()
		);

		return (finished-started);
	}
}