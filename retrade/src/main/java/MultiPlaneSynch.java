import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


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
	 * The total number of test requests.
	 */
	static final int   REQUESTS = 100000;

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

	/**
	 * The number of queue positions ahead the Master
	 * cursor to forward the execution. Depends on
	 * the number of competing Support threads.
	 */
	static final int   PRESEE   = (THREADS-1) * 2;


	/* Program Entry Point */

	public static void main(String[] argv)
	  throws InterruptedException
	{
		//~: test the parameters
		assert THREADS  >= 1;
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
		System.out.printf("[%d] Aux. threads; Requests [%d], Seed [%d]; [%d] runs:\n",
		  (THREADS - 1), REQUESTS, SEED, RUNS
		);

		//c: do test runs
		for(int run = 1;(run <= RUNS);run++)
			testRun(run, requests, new Database(database));
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
		}

		public Database(Database s)
		{
			//~: copy the clients
			this.clients = new Client[s.clients.length];
			int i = 0; for(Client c : s.clients)
				this.clients[i++] = new Client(c);
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
			Client x = clients[id];

			synchronized(x)
			{
				return new Client(x);
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
			Client x = clients[s.id];

			synchronized(x)
			{
				if(x.equals(s))
					return s;
				else
					return new Client(clients[s.id]);
			}
		}

		public boolean test(Client c)
		{
			Client x = clients[c.id];

			synchronized(x)
			{
				return x.equals(c);
			}
		}

		/**
		 * Thread-safe operation to assign the global data
		 * of the client. Only Master thread is allowed this!
		 */
		public void    assign(Client s)
		{
			Client x = clients[s.id];

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


		/* private: the clients */

		private final Client[] clients;
	}

	/**
	 * Image of the global Database
	 * used and updated by the
	 * Support working threads.
	 */
	static final class Snapshot
	{
		/* public: constructors */

		public Snapshot(Database database)
		{
			this.database = database;
			this.local    = new HashMap<>(17);
		}

		public final Database database;
		public int            index;


		/* Snapshot Data Access */

		public Client  client(int id)
		{
			//~: lookup in the local
			Client res = local.get(id);
			if(res != null) return res;

			//~: take the global copy
			res = database.copy(id);
			local.put(id, res);

			return res;
		}

		public boolean test(Client c)
		{
			//~: lookup in the local
			Client res = local.get(c.id);
			if(res != null)
				return res.equals(c);

			//~: test in the database
			return database.test(c);
		}

		public void    copy(Collection<Client> cs)
		{
			for(Client c : cs)
				local.put(c.id, new Client(c));
		}

		private final Map<Integer, Client> local;
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

		public Client(Client s)
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
	 * threads processing.
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

		/**
		 * The next request for the master thread.
		 */
		public Data next()
		{
			Data data; synchronized(this)
			{
				if(cursor >= requests.length)
					return null;

				data = datas[cursor];
				datas[cursor] = null; //<-- clear the data

				//!: advance
				cursor++;
			}

			//!: enter the data
			data.masterEnter();

			return data;
		}

		private int cursor;

		/**
		 * Returns the next data object to process
		 * for the asking Support thread.
		 */
		public Data next(Data data)
		{
			int index = -1;

			//?: {has data to release}
			if(data != null)
			{
				index = data.index;

				//!: release the lock
				data.supportLeave();
				data = null;
			}

			//c: queue competition cycle
			while(true)
			{
				//~: select section
				synchronized(this)
				{
					if(cursor >= requests.length)
						return null;

					//~: the next index to take
					if(index <= cursor)
						index = cursor;
					else
					{
						index++;

						//?: {overlap}
						if((index >= cursor + PRESEE) || (index >= requests.length))
							index = cursor;
					}

					//~: will try this request
					data = datas[index];
				}

				//?: {obtained the lock}
				if(Boolean.TRUE.equals(data.trySupportEnter()))
					break;
			}

			return data;
		}

		private final Data[] datas;
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
			this.index = index;
		}

		/**
		 * The client data before they were processed.
		 */
		public List<Client> initial;

		/**
		 * The client data after they were processed.
		 */
		public List<Client> results;


		/* Synchronization */

		/**
		 * Master calls this method before entering
		 * this request processing. If this request
		 * is currently executed by a Support thread,
		 * master waits till that processing is done.
		 */
		public void    masterEnter()
		{
			synchronized(this)
			{
				//?: {lock is held}
				if(lock == 1) try
				{
					lock = 2;
					this.wait();
				}
				catch(InterruptedException e)
				{
					throw new RuntimeException(e);
				}

				//~: mark as entered
				lock = 3;
			}
		}

		/**
		 * This call must be done by Support thread
		 * to try to acquire the lock on this request.
		 * This call is not blocking. Results are:
		 *
		 *   true   when lock is acquired by callee;
		 *   false  when else Support thread possess;
		 *   null   when Master had entered,
		 *          or wants to enter this task.
		 */
		public Boolean trySupportEnter()
		{
			synchronized(this)
			{
				if(lock == 0)
				{
					lock = 1;
					return true;
				}

				return (lock == 1)?(false):(null);
			}
		}

		public void    supportLeave()
		{
			synchronized(this)
			{
				if(lock == 1)
				{
					lock = 0;
					return;
				}

				if(lock == 2)
					this.notify();
			}
		}

		/**
		 * 0 when unlocked;
		 * 1 when locked by a Support thread;
		 * 2 when master wants to enter;
		 * 3 when entered by the Master.
		 */
		private int lock;
	}


	/* Working Thread Base */

	static abstract class Worker implements Runnable
	{
		/* public: constructor */

		protected final Queue queue;

		protected Worker(Queue queue)
		{
			this.queue = queue;
		}


		/* protected: requests execution */

		protected abstract Client client(int id);

		protected void execute(Data data)
		{
			//?: {buy}
			if(data.request instanceof Buy)
				buy((Buy) data.request);
			//~: credit
			else
				credit((Credit) data.request);
		}

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

	static final class Master extends Worker
	{
		/* public: constructor */

		public Master(Queue queue)
		{
			super(queue);
		}


		/* Task */

		public void run()
		{
			//~: mark the start time
			runtime = System.currentTimeMillis();

			//c: while there is a task
			Data data; while((data = queue.next()) != null)
			{
				execute(data);
			}

			//~: mark the finish time
			runtime = System.currentTimeMillis() - runtime;
		}

		public long getRuntime()
		{
			return runtime;
		}

		public int  getHits()
		{
			return hits;
		}


		/* protected: requests execution */

		protected Client client(int id)
		{
			Client c = cache.get(id);
			if(c == null) cache.put(id, c = queue.database.copy(id));
			return c;
		}

		protected void   execute(Data data)
		{
			cache.clear();

			//?: {the resulting data may be applied}
			if(consistent(data))
				apply(data.results);
			//~: execute again
			else
			{
				super.execute(data);

				//~: apply all the changes to the database
				apply(cache.values());

				//DEBUG: wait 1sec
//				Object x = new Object();
//				try
//				{
//					synchronized(x)
//					{
//						x.wait(100L);
//					}
//				}
//				catch(InterruptedException e)
//				{}
			}
		}

		private boolean  consistent(Data data)
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


		/* private: master state */

		/**
		 * Master work time is for whole Queue processing.
		 */
		private long runtime;

		/**
		 * The support pre-execution hits.
		 */
		private int  hits;

		/**
		 * Cache where clients are held during
		 * a request processing.
		 */
		private final Map<Integer, Client> cache = new HashMap<>(7);
	}


	/* Support Thread */

	static final class Support extends Worker
	{
		/* public: constructor */

		public Support(Queue queue)
		{
			super(queue);
		}


		/* Task */

		public void run()
		{
			//c: while there is a task
			Data data = null; while((data = queue.next(data)) != null)
			{
				execute(data);
			}
		}

		protected Client client(int id)
		{
			//?: {found it in the cache}
			Client c = cache.get(id);
			if(c != null) return c;

			//~: load from the database snapshot
			c = snapshot.client(id);

			//~: remember the initial version
			initial.add(new Client(c));

			//~: add to the results & cache
			results.add(c);
			cache.put(c.id, c);

			return c;
		}

		protected void   execute(Data data)
		{
			//~: clear the cache
			cache.clear();

			//~: create execution data
			this.initial = new ArrayList<>(4);
			this.results = new ArrayList<>(4);


			//HINT: when queue moves Support thread back to the
			//  previous tasks, Snapshot from the future may
			//  not be used more and must be replaced.

			//?: {repeated previous tasks}
			if(snapshot != null) if(snapshot.index <= data.index)
				snapshot = null;

			//?: {has no snapshot}
			if(snapshot == null)
				snapshot = new Snapshot(queue.database);

			//~: current index of the snapshot
			snapshot.index = data.index;

			//?: {previous work is still actual}
			if(consistent(data))
				return;

			//~: create execution data
			this.initial = new ArrayList<>(4);
			this.results = new ArrayList<>(4);

			//~: execute
			super.execute(data);

			//~: save data results
			data.initial = this.initial;
			data.results = this.results;


			//HINT: initial list always contains a copy,
			//  but resulting list contains the same objects
			//  as local snapshot. To make it thread-safe we
			//  have to create additional copies.

			snapshot.copy(this.results);
		}

		private boolean  consistent(Data data)
		{
			if((data.initial == null) | (data.results == null))
				return false;

			for(Client o : data.initial)
				//?: {not the same data}
				if(!snapshot.test(o))
					return false;

			return true;
		}


		/* private: support thread state */

		/**
		 * Cache where clients are held during
		 * a request processing.
		 */
		private final Map<Integer, Client> cache = new HashMap<>(7);

		private List<Client> initial, results;

		/**
		 * Database snapshot used locally by this thread.
		 */
		private Snapshot snapshot;
	}

	static void testRun(int run, Request[] requests, Database database)
	  throws InterruptedException
	{
		//~: create the queue
		Queue queue = new Queue(requests, database);

		//~: create master task
		Master master = new Master(queue);

		//~: create support tasks
		List<Support> supports = new ArrayList<>();
		for(int i = 1;(i < THREADS);i++)
			supports.add(new Support(queue));

		//~: create master thread
		Thread m = new Thread(master);
		m.setName("Master");

		//~: create support threads
		Thread[] sp = new Thread[supports.size()];
		for(int i = 0;(i < sp.length);i++)
		{
			sp[i] = new Thread(supports.get(i));
			sp[i].setName("Support-" + i);
			sp[i].setDaemon(true);
		}

		//~: start support threads
		for(Thread s : sp)
			s.start();

		//~: start master thread
		m.start();

		//~: wait the master
		m.join();

		//~: print the timing and the hash
		System.out.printf("%2d %5d  %5.1f  %s\n",
		  run, master.getRuntime(),
		  100.0 * master.getHits() / requests.length,
		  database.hash().toString()
		);
	}
}