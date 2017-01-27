package com.tverts.support.misc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Demonstration all-in-single-file program
 * for Multi-Plane Synchronization concept.
 *
 * @author anton.baukin@gmail.com
 */
public class MultiPlaneSynch
{
	/* Global Parameter of the Tests */

	/**
	 * The number of working threads.
	 */
	static final int   THREADS  = 2;

	/**
	 * This constant tells the Support threads
	 * the distance from the Master cursor to
	 * take the next pre-execution task.
	 */
	static final int   AHEAD    = 10;

	/**
	 * The total number of test requests.
	 */
	static final int   REQUESTS = 1000000;

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
		assert AHEAD    >= 1;
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
		java.util.Locale.setDefault(java.util.Locale.US);
		System.out.printf("[%d] Supporters; Requests [%d], Seed [%d]; [%d] runs:\n",
		  THREADS-1, REQUESTS, SEED, RUNS
		);

		System.out.println(" #  TIME   HITS  MISS  ERRS             HASH               ");

		//~: print the timing and the hash
		for(int i = 0;(i < stats.length);i++)
			System.out.printf("%2d %5d  %5.1f %5.1f %5d %s\n",
			  i+1, stats[i].runtime,
			  (100.0 * stats[i].hits / REQUESTS),
			  (100.0 * stats[i].miss / REQUESTS),
			  stats[i].errs.get(),
			  stats[i].databaseHash
		);

		//~: minimum run time
		int min = 0; long time = Long.MAX_VALUE;
		for(int i = 0;(i < stats.length);i++)
			if(stats[i].runtime < time)
				{ min = i; time = stats[i].runtime; }

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
			if(initial == null) return false;

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

	static final class Client
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
		/**
		 * When thread executes as a Master it
		 * measures the consistency hits.
		 */
		public int    hits;
		public int    miss;

		/**
		 * The number of pre-executed requests
		 * denied due to some error.
		 */
		public final AtomicInteger errs =
		  new AtomicInteger();

		/**
		 * Queue execution time (milliseconds).
		 */
		public long   runtime;

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


	/* Master Worker */

	/**
	 * Master working task executes the requests as a plain
	 * single-threaded application would be. It takes no locks
	 * and waits nothing. It advances the queue global cursor.
	 */
	static final class Master extends Processing implements Runnable
	{
		/* public: constructor */

		/**
		 * Execution context of this worker.
		 */
		public final Context ctx;

		public Master(Context ctx)
		{
			this.ctx      = ctx;
			this.snapshot = new Snapshot(ctx.database);
		}


		/* Runnable */

		public void      run()
		{
			ctx.stat.runtime = System.currentTimeMillis();

			//c: execute the requests of the queue
			for(Data data : ctx.datas)
				execute(data);

			ctx.stat.runtime = System.currentTimeMillis() - ctx.stat.runtime;
		}


		/* protected: Processing */

		/**
		 * Takes global Client object
		 * directly into the snapshot.
		 */
		protected Client client(int id)
		{
			Client c = snapshot.lookup(id);
			return (c != null)?(c):(snapshot.client(id));
		}

		private final Snapshot snapshot;


		/* private: execution */

		private void     execute(Data data)
		{
			//?: {the resulting data may be applied}
			if(consistent(data))
			{
				ctx.database.assign(data.results, data.results.length);
				data.results = null;
			}
			//~: execute the request
			else
			{
				execute(data.request);

				//~: commit the results
				snapshot.commit();
			}

			//!: increment the global cursor
			ctx.cursor.value++;
		}

		/**
		 * A pre-executed request is consistent when
		 * the hashes of all the clients involved in
		 * the processing are the same now as they
		 * were during the pre-execution.
		 */
		private boolean  consistent(Data data)
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
	}


	/* Support Worker */

	/**
	 * Support worker goes ahead of the Master and pre-executes
	 * the requests working with Client copies. Each Support
	 * threads has own private sub-sequence of the requests
	 * based oin it's id number.
	 */
	static final class Support extends Processing implements Runnable
	{
		/* public: constructor */

		/**
		 * Worker id starting with 0.
		 */
		public final int id;

		/**
		 * Execution context of this worker.
		 */
		public final Context ctx;

		public Support(int id, Context ctx)
		{
			this.id       = id;
			this.ctx      = ctx;
			this.snapshot = new Snapshot(ctx.database);
		}


		/* Runnable */

		public void      run()
		{
			//c: execute the requests of the queue
			Data data; while((data = select()) != null)
				prexecute(data);
		}


		/* protected: Processing */

		protected Client client(int id)
		{
			Client c = snapshot.lookup(id);
			return (c != null)?(c):(snapshot.copy(id));
		}


		/* private: pre-execution */

		/**
		 * Local copy of the cursor. @see {@link #select()}.
		 * It is the smallest not yet Master-committed request
		 * from the Workers' sequence.
		 */
		private int cursor;

		/**
		 * Pre-execution cursor offset index.
		 */
		private int presee;

		/**
		 * The list of (Client ID, Client Hash) pairs for original
		 * versions of Clients before any updates took place.
		 * @see {@link Database#same(Object[])}.
		 */
		private final ArrayList<Object> initial =
		  new ArrayList<>(4);

		/**
		 * Cache where clients are held during a request processing.
		 */
		private final Snapshot snapshot;

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
			final int c = ctx.cursor.value;

			//?: {master is still before}
			if(c < cursor)
				cursor += THREADS - 1; //HINT: 0-thread is Master
			//~: go ahead
			else
			{
				cursor = c + AHEAD;

				//?: {not in own sub-sequence} shift right
				if((c + AHEAD) % (THREADS - 1) != id)
					cursor += THREADS - 1 - (c + AHEAD)%(THREADS - 1);
			}

			//?: {the queue is not done yet}
			return (cursor < ctx.datas.length)?(ctx.datas[cursor]):(null);
		}

		private void     prexecute(Data data)
		{
			try
			{
				//~: execute the request
				execute(data.request);

				//~: commit the results
				snapshot.commit(data);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				ctx.stat.errs.incrementAndGet();
				snapshot.rollback();
			}
		}
	}

	static Stat testRun(int run, Request[] requests, Database database)
	  throws InterruptedException
	{
		//~: create Master task & thread
		Master master = new Master(new Context(database, requests));
		Thread thread = new Thread(master);
		thread.setName("Master");

		//~: create Support threads
		Thread[] ths = new Thread[THREADS - 1];
		for(int id = 0;(id < ths.length);id++)
		{
			ths[id] = new Thread(new Support(id, new Context(master.ctx)));
			ths[id].setName("Support-" + id);
		}

		//~: start them
		thread.start();
		for(Thread t : ths)
			t.start();

		//~: wait for the Master
		thread.join();

		//~: save database hash
		master.ctx.stat.databaseHash = master.ctx.database.hash().toString();
		return master.ctx.stat;
	}


	/* Database Snapshot */

	static final class Snapshot
	{
		public final Database db;

		public Snapshot(Database db)
		{
			this.db = db;
		}


		/* Database Snapshot */

		public Client lookup(int id)
		{
			for(int i = 0;(i < len);i++)
				if(cls[i].id == id)
					return cls[i];
			return null;
		}

		public Client client(int id)
		{
			//~: copy from the database
			Client c = db.client(id);

			//?: {cache is full}
			if(len == cls.length)
			{
				Client[] xcls = new Client[len + 8];
				System.arraycopy(cls, 0, xcls, 0, len);
				cls = xcls;
			}

			//~: put to the cache
			cls[len++] = c;
			return c;
		}

		public Client copy(int id)
		{
			//~: copy from the database
			Client c = db.copy(id);

			//?: {cache is full}
			if(len == cls.length)
			{
				Client[] xcls = new Client[len + 8];
				System.arraycopy(cls, 0, xcls, 0, len);
				cls = xcls;

				Hash[] xini = new Hash[len + 8];
				System.arraycopy(ini, 0, xini, 0, len);
				ini = xini;
			}

			//~: put to the cache with the hash
			ini[len]   = c.hash();
			cls[len++] = c;

			return c;
		}

		public void   commit()
		{
			//~: assign the clients to the database
			db.assign(cls, len);

			//~: clear the cache
			len = 0;
		}

		public void   commit(Data data)
		{
			final Client[] results = new Client[len];
			final Object[] initial = new Object[len * 2];

			//~: copy the clients
			System.arraycopy(cls, 0, results, 0, len);

			//~: copy the hashes
			for(int i = 0;(i < len);i++)
			{
				initial[2*i    ] = cls[i].id;
				initial[2*i + 1] = ini[i];
			}

			//~: save to the data
			data.initial = initial;
			data.results = results;

			//~: clear the cache
			len = 0;
		}

		public void   rollback()
		{
			//~: clear the cache
			len = 0;
		}


		/* private: clients cache */

		private int      len;
		private Client[] cls = new Client[16];

		/**
		 * The list of (Client ID, Client Hash) pairs for original
		 * versions of Clients before any updates took place.
		 * @see {@link Database#same(Object[])}.
		 */
		private Hash[]   ini = new Hash[16];
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