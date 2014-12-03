import java.util.LinkedList;
import java.util.Random;

/**
 * Demonstration all-in-single-file program
 * for Multi-Plane Synchronization concept.
 *
 * @author anton.baukin@gmail.com.
 */
public class MultiPlaneSynch
{

	/* program entry point */

	public static void main(String[] argv)
	{
		//~: warm-up virtual machine with self-tests
		testDebtsBuffer();
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


	/* Clients Database */

	static final class Database
	{
		/* public: constructor */

		public Database(int clients)
		{
			this.clients = new Client[clients];
			for(int id = 0;(id < clients);id++)
				this.clients[id] = new Client(id);
		}


		/* Database Access */

		/**
		 * Thread-unsafe operation to directly access client.
		 */
		public Client client(int id)
		{
			return clients[id];
		}

		/**
		 * Thread-safe operation to get a full copy
		 * of the client data.
		 */
		public Client copy(int id)
		{
			synchronized(clients[id])
			{
				return new Client(clients[id]);
			}
		}

		/**
		 * Thread-safe operation to assign the global data
		 * of the client. Only Master thread is allowed this!
		 */
		public void   assign(Client s)
		{
			synchronized(clients[s.id])
			{
				clients[s.id].assign(s);
			}
		}

		/**
		 * Calculates the resulting hash of the database.
		 * Needed to check the results.
		 */
		public Hash   hash()
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


		/* Constants */

		static final long S = System.currentTimeMillis();
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
			synchronized(this)
			{
				if(cursor >= requests.length)
					return null;

				Data data = datas[cursor];
				datas[cursor] = null; //<-- clear the data

				//?: {has no data}
				if(data == null)
					data = new Data(requests[cursor], cursor);

				//!: advance
				cursor++;
				return data;
			}
		}

		private int cursor;

		/**
		 * Returns the next data object to process
		 * for the asking Support thread.
		 */
		public Data next(Data data)
		{
			synchronized(this)
			{
				if(cursor >= requests.length)
					return null;

				//HINT: cursor points to the next item the Master will take.

				//?: {has data to return}
				if((data != null) && (data.index >= cursor))
					datas[data.index] = data;

				//HINT: we take the item after the next item of
				//  the Master thread as Master may now wait for
				//  the next item -> i = cursor + 1

				//~: search for the next item to take
				for(int i = cursor + 1;(i < requests.length) && (i != data.index);i++)
					if(datas[i] != null)
					{
						data = datas[i];
						datas[i] = null; //<-- thread exclusively takes it
						return data;
					}

				return null; //<-- noting found, thread will exit
			}
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
	}

}