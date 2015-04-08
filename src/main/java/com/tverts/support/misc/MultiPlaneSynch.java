package com.tverts.support.misc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


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
	 */
	static final int   THREADS  = 1;

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

		//c: do test runs
		Stat[] stats = new Stat[RUNS];
		for(int run = 0;(run < RUNS);run++)
			stats[run] = testRun(run, requests, new Database(database));

		printStats(stats);
	}

	static void printStats(Stat[] stats)
	{
		Locale.setDefault(Locale.US);
		System.out.printf("[%d] Workers; Requests [%d], Seed [%d]; [%d] runs:\n",
		  THREADS, REQUESTS, SEED, RUNS
		);

		System.out.println(" #  TIME   HITS  MISS               HASH               ");

		//~: print the timing and the hash
		for(int i = 0;(i < stats.length);i++)
			System.out.printf("%2d %5d  %5.1f %5.1f  %s\n",
			  i+1, stats[i].runtime.longValue(),
			  (100.0 * stats[i].hits / stats[i].size),
			  (100.0 * stats[i].miss / stats[i].size),
			  stats[i].databaseHash
		);

		//~: minimum run time
		int min = 0; long time = Long.MAX_VALUE;
		for(int i = 0;(i < stats.length);i++)
			if(stats[i].runtime.get() < time)
				{ min = i; time = stats[i].runtime.get(); }

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
		 * Directly access to the client.
		 */
		public Client  client(int id)
		{
			return clients[id];
		}

		/**
		 * A full copy of the requested client data.
		 */
		public Client  copy(int id)
		{
			return new Client(clients[id]);
		}

		/**
		 * Tests whether global Client data is the same
		 * as the given ones. Event items is a client ids.
		 * Odd items is the copy of the same client data.
		 *
		 * @see {@link Data#initial}
		 */
		public boolean same(Object[] initial)
		{
			for(int i = 0;(i < initial.length);i += 2)
				//?: {Hash of the client given differs}
				if(!clients[(Integer)initial[i]].equals((Hash) initial[i+1]))
					return false;

			return true;
		}

		/**
		 * Assigns the global data of the clients.
		 * Only Master thread is allowed this request!
		 * Note that the source object may not be used further.
		 */
		public void    assign(Client[] cs, int len)
		{
			for(int i = 0;(i < len);i++)
				clients[cs[i].id] = cs[i];
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


	/* Client Data Item */

	private static final class Client
	{
		/* public: constructors */

		public  Client(int id)
		{
			this.id    = id;
			this.debts = new Debts();
			this.hash  = new Hash();
		}

		private Client(Client s)
		{
			this.id      = s.id;
			this.money   = s.money;
			this.debts   = new Debts(s.debts);
			this.hash    = new Hash();
			this.updated = true;
		}

		public final int id;


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
		private int    b, e = line.length;
	}


	/* Hash Function */

	/**
	 * Implementation based on 128-bit Murmur hash 3
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


	/* Execution Statistics */

	static class Stat
	{
		public int size;

		/**
		 * When thread executes as a Master it
		 * measures the consistency hits.
		 */
		public int hits;
		public int miss;

		/**
		 * Queue execution time (milliseconds).
		 */
		public final AtomicLong runtime =
		  new AtomicLong();

		public String databaseHash;
	}


	/* Processing Data */

	/**
	 * Processing data is a wrapper of a Request.
	 * It also stores the pre-execution results.
	 */
	static class Data
	{
		/* Processing Data */

		public final Request request;
		public final int     index;

		public Data(Request request, int index)
		{
			this.request = request;
			this.index   = index;
		}


		/* Pre-Execution Data */

		/**
		 * Each [2i] position is Client ID; [2i+1] is Client Hash.
		 * @see {@link Database#same(Object[])}
		 */
		public Object[] initial;

		/**
		 * The client data after they were processed.
		 */
		public Client[] results;
	}


	/* Local Context of a Processing Thread */

	/**
	 * Processing data context provides shared objects
	 * access for a Worker. Each worker has it's own
	 * copy of a Context.
	 */
	static class Context
	{
		/**
		 * Shared (global) database.
		 */
		public final Database database;

		/**
		 * Requests data of the queue. Private
		 * copy, but all the Data are shared.
		 */
		public final Data[]   datas;

		/**
		 * Shared Master cursor position as Array of [1].
		 * Note that the cursor is not atomic. Workers
		 * read it's value, but only ont with Master role
		 * is allowed to increment it.
		 */
		public final Cursor   cursor;

		public final Stat     stat;


		/* public: constructors */

		/**
		 * Creates original processing context.
		 */
		public Context(Database database, Request[] requests)
		{
			this.database = database;

			this.datas    = new Data[requests.length];
			for(int i = 0;(i < requests.length);i++)
				this.datas[i] = new Data(requests[i], i);

			this.cursor   = new Cursor();
			this.stat     = new Stat();
		}

		/**
		 * Creates copy of the original context.
		 * Shares the data by the links.
		 */
		public Context(Context shared)
		{
			this.database = shared.database;
			this.datas    = shared.datas;
			this.cursor   = shared.cursor;
			this.stat     = shared.stat;
		}

		public static class Cursor
		{
			public int value;
		}
	}


	/* Worker */

	/**
	 * Working thread task selects the next request from the queue
	 * and executes it in one of the roles: as a Queue Master, or
	 * as a Support. Master role means that the request selected
	 * is the earliest request not executed yet.
	 */
	static final class Worker extends Processing implements Runnable
	{
		/* public: constructor */

		/**
		 * Worker id is offset of
		 */
		public final int id;

		/**
		 * Execution context of this worker.
		 */
		public final Context ctx;

		public Worker(int id, Context ctx)
		{
			this.cursor = this.id = id;
			this.ctx = ctx;
		}


		/* Runnable */

		public void run()
		{
			long runtime = System.currentTimeMillis();

			//c: execute the requests of the queue
			while((data = select()) != null)
				if(master)
					execute();
				else
					prexecute();

			ctx.stat.runtime.compareAndSet(0L,
			  System.currentTimeMillis() - runtime);
		}


		/* private: execution */

		protected Client client(int id)
		{
			return (master)?(clientMaster(id)):(clientSupport(id));
		}

		/**
		 * Currently executed request.
		 */
		private Data data;

		/**
		 * Local copy of the cursor. @see {@link #select()}.
		 * It is the smallest not yet Master-committed
		 * request from the Workers' sequence.
		 * The initial value of the cursor is Worker id.
		 */
		private int cursor;

		/**
		 * Pre-execution cursor offset index.
		 */
		private int presee;

		/**
		 * Selects next request for execution.
		 * Returns null when the queue is done.
		 *
		 * Each Worker has it's own sequence of the
		 * requests to process. It has indices of:
		 * [i * THREADS + id] that never intersect.
		 * That's why Worker do not need to lock a
		 * request Data.
		 */
		private Data     select()
		{
			//HINT: only Master increments shared cursor!
			final int c = ctx.cursor.value;

			//?: {the queue is done}
			if(c >= ctx.datas.length)
				return null;

			//?: {possess global position} execute as master
			if(c == cursor)
			{
				cursor += THREADS; //<-- shift own cursor to the next master target
				presee  = 0;       //<-- clear pre-execute cursor

				master = true;
				return ctx.datas[c];
			}

			//~: pre-execute position within the own sequence
			int p = this.cursor + THREADS*presee;

			//?: {almost done with the queue}
			if(p >= ctx.datas.length)
				p = 0; //<-- take the future master record

			master = false;
			return ctx.datas[p];
		}


		/* Master Behaviour */

		/**
		 * This flag is set when Worker currently in Master role.
		 * In this case the Context cursor equals to the Data index.
		 */
		private boolean master;

		private Client   clientMaster(int id)
		{
			//?: {found it in the local cache}
			Client c = cache.get(id);
			if(c != null) return c;

			//~: load from the database & cache it
			c = ctx.database.client(id);
			cache.put(c);

			return c;
		}

		private void     execute()
		{
			//?: {the resulting data may be applied}
			if(consistent())
				ctx.database.assign(data.results, data.results.length);
				//~: execute again
			else
			{
				//~: execute the request
				execute(data.request);

				//~: assign all the changes to the database
				cache.assign(ctx.database);

				cache.clear();
			}

			//!: increment the cursor
			ctx.cursor.value++;
		}

		/**
		 * A pre-executed request is consistent when
		 * the hashes of all the clients involved in
		 * the processing are the same now as they
		 * were during the pre-execution.
		 */
		private boolean  consistent()
		{
			if(data.results == null)
				return false;

			//?: {global copy differs}
			if(!ctx.database.same(data.initial))
			{
				ctx.stat.miss++; //<-- support work is wasted
				return false;
			}

			ctx.stat.hits++; //<-- support work is applied
			return true;
		}


		/* Support Behaviour */

		private ArrayList<Object> initial;
		private ArrayList<Client> results;

		/**
		 * Cache where clients are held during a request processing.
		 */
		private final Cache cache = new Cache();

		private Client   clientSupport(int id)
		{
			//?: {found it in the local cache}
			Client c = cache.get(id);
			if(c != null) return c;

			//~: load from the database & cache it
			c = ctx.database.copy(id);
			cache.put(c);

			//~: remember the initial version
			initial.add(c.id);
			initial.add(new Hash(c.hash()));

			//~: add to the results
			results.add(c);

			return c;
		}

		private void     prexecute()
		{
			//~: allocate execution data
			initial = new ArrayList<>(4);
			results = new ArrayList<>(2);

			//~: execute the request
			execute(data.request);

			//~: save data results
			data.initial = initial.toArray(new Object[initial.size()]);
			data.results = results.toArray(new Client[results.size()]);

			initial = null; results = null;
			cache.clear();
		}
	}

	static Stat testRun(int run, Request[] requests, Database database)
	  throws InterruptedException
	{
		//~: create the contexts
		Context[] cxs = new Context[THREADS];
		cxs[0] = new Context(database, requests);
		for(int i = 1;(i < cxs.length);i++)
			cxs[i] = new Context(cxs[0]);

		//~: create the working threads
		Thread[] ths = new Thread[cxs.length];
		for(int i = 0;(i < ths.length);i++)
		{
			ths[i] = new Thread(new Worker(i, cxs[i]));
			ths[i].setName("Support-" + i);
			ths[i].start();
		}

		//~: join them all
		for(Thread th : ths)
			th.join();

		//~: save requests number
		cxs[0].stat.size = cxs[0].datas.length;

		//~: save database hash
		cxs[0].stat.databaseHash = cxs[0].database.hash().toString();

		return cxs[0].stat;
	}


	/* Clients Cache */

	static class Cache
	{
		public Client get(int id)
		{
			for(int i = 0;(i < len);i++)
				if(ids[i] == id)
					return cls[i];
			return null;
		}

		public void   put(Client c)
		{
			if(len == ids.length)
			{
				int[]    a = new int[len + 2];
				System.arraycopy(ids, 0, a, 0, len);
				ids = a;

				Client[] b = new Client[len + 2];
				System.arraycopy(cls, 0, b, 0, len);
				cls = b;
			}

			ids[len] = c.id;
			cls[len] = c;
			len++;
		}

		public void clear()
		{
			len = 0;
		}

		public void assign(Database db)
		{
			db.assign(cls, len);
		}

		private int[]    ids = new int[4];
		private Client[] cls = new Client[4];
		private int      len;
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
}