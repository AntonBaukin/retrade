import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
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
	 * The number of times Support thread may go ahead
	 * by one pre-execution block. Doesn't depend on the
	 * number of Support threads, may be 0.
	 */
	static final int   PRESEE   = 2;

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
	static final int   REQUESTS = 200000;

	/**
	 * The number of test clients.
	 */
	static final int   CLIENTS  = 100;

	/**
	 * The number of the generated requests executions.
	 */
	static final int   RUNS     = 20;

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
		System.out.printf("[%d] Supporters; Requests [%d], Seed [%d]; [%d] runs:\n",
		  THREADS-1, REQUESTS, SEED, RUNS
		);

		//c: do test runs
		long[] times = new long[RUNS];
		for(int run = 0;(run < RUNS);run++)
			times[run] = testRun(run+1, requests, new Database(database));

		//~: minimum run time
		long time = Long.MAX_VALUE;
		for(int i = 0;(i < RUNS);i++)
			time = Math.min(time, times[i]);
		System.out.printf("Minimum run time: %d\n", time);
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
	private static final class Buy extends Request
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
	private static final class Credit extends Request
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
			res.client(id).money(INITIAL[0] +
			  gen.nextInt(INITIAL[1] - INITIAL[0] + 1));

		return res;
	}


	/* Clients Database */

	/**
	 * Global data.
	 */
	private static final class Database
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
				this.clients[i++] = new Client(c);

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
				return new Client(x);
			}
		}

		/**
		 * Thread-safe operation to test whether
		 * global copy is the same as the given one.
		 */
		public boolean same(int id, Hash clientHash)
		{
			final Client x = clients[id];

			synchronized(x)
			{
				if(DELAY != 0) delay(id);
				return x.equals(clientHash);
			}
		}

		/**
		 * Thread-safe operation to assign the global data
		 * of the client. Only Master thread is allowed this!
		 * Note that the source object may not be used further.
		 */
		public void    assign(Client s)
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

	private static final class Client
	{
		/* public: constructors */

		public final int id;

		public Client(int id)
		{
			this.id    = id;
			this.debts = new Debts();
			this.hash  = new Hash();
		}

		private Client(Client s)
		{
			this.id    = s.id;
			this.money = s.money;
			this.debts = new Debts(s.debts);
			this.hash  = new Hash(s.hash());
		}


		/* Money Operations */

		public int  money()
		{
			return this.money;
		}

		public void money(int m)
		{
			this.money  = m;
			this.updated = true;
		}


		/* Debts Operations */

		public boolean debts()
		{
			return debts.isEmpty();
		}

		public int     creditor()
		{
			return debts.client();
		}

		public int     debt()
		{
			return debts.debt();
		}

		public void    debt(int d)
		{
			debts.debt(d);
			this.updated = true;
		}

		public void    debt(int client, int debt)
		{
			debts.add(client, debt);
			this.updated = true;
		}

		public void    payed()
		{
			debts.pop();
			this.updated = true;
		}


		/* Client Operations */

		public void    assign(Client s)
		{
			this.money   = s.money;
			this.debts   = s.debts;
			this.hash    = s.hash;
			this.updated = s.updated;
		}

		/**
		 * Compares the data of the same client clone.
		 */
		public boolean equals(Hash hash)
		{
			return this.hash().equals(hash);
		}

		public Hash    hash(Hash hash)
		{
			hash.update(id);
			hash.update(money);
			debts.hash(hash);

			return hash;
		}

		/**
		 * Returns the Hash of this Client
		 * re-calculating if Client is updated.
		 */
		public Hash    hash()
		{
			if(!updated)
				return this.hash;

			this.hash.reset();
			this.hash(this.hash);
			updated = false;

			return this.hash;
		}


		/* private: client data */

		private int     money;

		private Debts   debts;

		private boolean updated;

		/**
		 * The hash of the debts line;
		 */
		private Hash    hash;
	}


	/* Debts List */

	private static final class Debts
	{
		/* public: constructors */

		public Debts()
		{}

		public Debts(Debts d)
		{
			//?: {has no data}
			if(d.e == d.line.length)
				return;

			//?: {has no data rounded}
			if(d.e > d.b)
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
		}


		/* Debts */

		public boolean isEmpty()
		{
			return (this.e == line.length);
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
			line[b+1] = d;
		}

		/**
		 * Removes the first (current) debt.
		 */
		public void    pop()
		{
			//HINT: e is always < line.length
			if((b += 2) == line.length) b = 0;

			//?: {has no items left}
			if(b == e)
				e = line.length;
		}

		/**
		 * Adds the debt to the end of the buffer.
		 */
		public void    add(int client, int debt)
		{
			final int l = line.length;

			//?: {has free space}
			int x = e + 2;
			if(((e < b) & (x <= b)) || ((e > b) & (x <= l)) || (e == l))
			{
				if(e == l) { b = e = 0; x = 2; }

				line[e]   = client;
				line[e+1] = debt;
				e = (x == l)?(0):(x);

				return;
			}

			//~: allocate the new buffer
			int[] temp = new int[l*2];

			//~: {has no data rounded}
			if(e > b)
				System.arraycopy(line, b, temp, 0, x = e - b);
			//~: copy the tail and the head
			else
			{
				System.arraycopy(line, b, temp, 0, x = l - b);
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
			if(e == line.length) return;

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


		/* private: debts data */

		/**
		 * Line is a cyclic buffer where stored
		 * pairs of (client, debt).
		 */
		private int[]  line  = new int[8];

		/**
		 * Begin position in the buffer, and end position
		 * following the last pair. Note that as the buffer
		 * is cyclic, end may be before the head.
		 */
		private int     b, e = line.length;
	}


	/* Murmur Hash 3 */

	/**
	 * Implementation based on 128-bit Murmur hash
	 * function introduced by Austin Appleby.
	 */
	private static final class Hash
	{
		/* Hash */

		public Hash()
		{
			reset();
		}

		public Hash(Hash h)
		{
			assign(h);
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

		public Hash    assign(Hash h)
		{
			this.x = h.x; this.y = h.y;
			return this;
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

					Hash a = new Hash();
					copy.hash(a);

					Hash b = new Hash();
					deb.hash(b);

					assert a.equals(b);
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
	private static final class Queue
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

		public Data master(Data data)
		{
			//?: {release previous request}
			if(data != null)
				datas[data.index] = null; //<-- reduce GC pulsation

			final int x = cursor[0];
			if(x >= requests.length)
			{
				data.locked.set(2);
				return null;
			}

			//~: advance the cursor
			cursor[0]++;

			//HINT: Support thread ID starts with 1. Having 1 Support
			// next pre-execute position is +2 after this (x) request.
			// +2 (not +1) is better as when Support will take it,
			// Master will be about to take +1.

			//~: move prepare position
			cursor[1] = x + THREADS-1;

			//~: unlock waiting Support threads
			if(data != null)
				data.locked.lazySet(2);

			return datas[x];
		}

		public Data support(final int id)
		{
			int offset = id;
			int presee = 0;

			//c: queue competition cycle
			while(true)
			{
				//~: position to pre-execute (starts with 1)
				final int index = cursor[1] + offset + presee;

				//?: {the queue is almost done}
				if(index >= requests.length)
					return null; //<-- thread exit

				//?: {master is ahead}
				final Data data = datas[index];
				if(data == null)
				{
					offset += THREADS-1;
					continue;
				}

				//?: {this item is not locked} take it
				if(data.locked.compareAndSet(0, 1))
					return data;

				//?: {pre-see limit not gained}
				if(presee++ <= PRESEE)
				{
					offset += THREADS-1;
					continue;
				}

				//~: spin on this item
				while(data.locked.get() != 2);

				//~: go ahead and wake up else supporters

			}
		}

		/**
		 * Request execution context.
		 */
		private final Data[] datas;

		/**
		 * Current request to process at [0] and
		 * request to pre-execute [1].
		 *
		 * HINT: we use array instead of volatile
		 * fields as extensive concurrent access
		 * to them reduces the performance by 10%.
		 */
		private final int[] cursor = new int[2];
	}


	/**
	 * Processing data of one of the Support threads.
	 */
	private static final class Data
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
		 * The client data hash before they were processed.
		 * Assigned by a Support thread had pre-executed
		 * this request.
		 */
		public volatile Map<Integer, Hash> initial;

		/**
		 * The client data after they were processed.
		 */
		public volatile List<Client> results;

		/**
		 * Lock for Support threads to exclusively
		 * access request pre-execution.
		 *
		 * Note that as request is pre-executed only once,
		 * lock is never removed: it is also indicator not
		 * to re-execute the same request twice.
		 *
		 * Master thread never asks for locks and
		 * executes requests directly.
		 *
		 * Value meanings are: 0 - not locked, 1 - locked
		 * by Support, 2 - Support wait release.
		 */
		public final AtomicInteger locked = new AtomicInteger();
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
			if(c.money() < r.s)
				return;

			//~: take the money
			c.money(c.money() - r.s);

			//~: give
			give(r.b, r.s);
		}

		private void credit(Credit r)
		{
			Client c = client(r.a);

			//?: {client has enough money}
			if(c.money() >= r.s)
			{
				//~: take the money
				c.money(c.money() - r.s);

				//~: give
				give(r.b, r.s);

				return;
			}

			//~: calculate the debt
			int d = r.s - c.money();

			//~: now has no money
			int s = c.money();
			c.money(0);

			//~: make the debt
			c.debt(r.b, d);

			//~: give the money
			if(s != 0)
				give(r.b, s);
		}

		private void give(int client, int s)
		{
			Client c = client(client);

			//?: {client has no debts}
			if((c.money() != 0) || c.debts())
			{
				c.money(c.money() + s);
				return;
			}

			//~: return all the debts
			while((s > 0) && !c.debts())
			{
				//~: target creditor
				int b = c.creditor();

				//~: take the most
				int d = c.debt();
				int x = Math.min(s, d);

				//?: {whole debt}
				if(x == d)
					c.payed();
				//~: reduce the debt
				else
					c.debt(d - x);

				//~: subtract the amount
				s -= x;

				//recursive: give that amount
				give(b, x);
			}

			//~: add the rest
			c.money(c.money() + s);
		}
	}


	/* Master Thread Task */

	private static final class Master extends Processing implements Runnable
	{
		/* public: constructor */

		public Master(Queue queue)
		{
			this.queue = queue;
		}


		/* Worker */

		public void run()
		{
			runtime = System.currentTimeMillis();

			//c: execute the requests of the queue
			while((data = queue.master(data)) != null)
				execute();

			runtime = System.currentTimeMillis() - runtime;
		}

		public int  getHits()
		{
			return hits;
		}

		public int  getMiss()
		{
			return miss;
		}

		public long getRuntime()
		{
			return runtime;
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

			return c;
		}

		private void     execute()
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

		private boolean  consistent()
		{
			final List<Client> results = data.results;
			if(results == null) return false;

			final Map<Integer, Hash> initial = data.initial;
			for(Map.Entry<Integer, Hash> e : initial.entrySet())
				//?: {global copy differs}
				if(!queue.database.same(e.getKey(), e.getValue()))
				{
					miss++;
					return false;
				}

			hits++; //<-- support work is not wasted

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
		private int         miss;

		/**
		 * Queue execution time.
		 */
		private long        runtime;

		/**
		 * Cache where clients are held during
		 * a request processing.
		 */
		private final Map<Integer, Client> cache = new HashMap<>(7);
	}


	/* Support Thread Task */

	private static final class Support extends Processing implements Runnable
	{
		/* public: constructor */

		public Support(Queue queue, int id)
		{
			assert (id >= 1);

			this.queue = queue;
			this.id    = id;
		}

		/* Worker */

		public void run()
		{
			while((data = queue.support(id)) != null)
				execute();
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

			//~: remember the initial version
			initial.put(c.id, new Hash(c.hash()));

			//~: add to the results
			results.add(c);

			return c;
		}

		private void     execute()
		{
			//~: clear the cache
			cache.clear();

			//~: allocate execution data
			this.initial = new HashMap<>(3);
			this.results = new ArrayList<>(4);

			//~: execute the request
			execute(data.request);

			//~: calculate hashes
			for(Client c : results)
				c.hash();

			//~: save data results
			data.initial = this.initial;
			data.results = this.results;
		}


		/* private: worker state */

		private final Queue queue;

		/**
		 * Support thread identifier. Starts from 1.
		 */
		private final int   id;

		/**
		 * Current execution context.
		 */
		private Data        data;

		/**
		 * Cache where clients are held during
		 * a request processing.
		 */
		private final Map<Integer, Client> cache = new HashMap<>(7);

		/**
		 * Initial and final processing states
		 * used when working as a Support.
		 */
		private Map<Integer, Hash> initial;
		private List<Client>       results;
	}

	static long testRun(int run, Request[] requests, Database database)
	  throws InterruptedException
	{
		//~: create the queue
		Queue    queue   = new Queue(requests, database);

		//~: create the master task
		Master   master  = new Master(queue);

		//~: create the working threads
		Thread[] threads = new Thread[THREADS];

		//~: master thread
		threads[0] = new Thread(master);
		threads[0].setName("Master");

		//~: support threads
		for(int i = 1;(i < THREADS);i++)
		{
			threads[i] = new Thread(new Support(queue, i));
			threads[i].setName("Support-" + i);
			threads[i].setDaemon(true);
			threads[i].start();
		}

		//!: start the master
		threads[0].start();

		//~: and wait it
		threads[0].join();

		if(run == 1)
			System.out.println(" #  TIME   HITS  MISS               HASH               ");

		//~: print the timing and the hash
		System.out.printf("%2d %5d  %5.1f %5.1f  %s\n",
		  run, master.getRuntime(),
		  (100.0 * master.getHits() / requests.length),
		  (100.0 * master.getMiss() / requests.length),
		  database.hash().toString()
		);

		return master.getRuntime();
	}
}