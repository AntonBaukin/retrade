package com.tverts.system.tx;

/* standard Java classes */

import java.util.ArrayList;

/* Hibernate Persistence Layer */

import org.hibernate.FlushMode;
import org.hibernate.Session;

/* com.tverts: endure (core) */

import com.tverts.endure.TxEntity;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Point to access current transaction provider.
 *
 * @author anton.baukin@gmail.com
 */
public class TxPoint
{
	/* public: Singleton */

	public static TxPoint getInstance()
	{
		return INSTANCE;
	}

	public static final TxPoint INSTANCE =
	  new TxPoint();

	protected TxPoint()
	{}


	/* public: TxPoint (static) interface */

	public static Tx      txContext()
	{
		return TxPoint.getInstance().getTxContextStrict();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Tx> T
	                      txContext(Class<T> cls)
	{
		return (T) TxPoint.getInstance().getTxContext(cls);
	}

	public static Session txSession()
	{
		return txSession(txContext());
	}

	public static Session txSession(Tx ctx)
	{
		EX.assertn( ctx.getSessionFactory(),
		  "Tx Context of class [", ctx.getClass().getSimpleName(),
		  "] has no Hibernate Session Factory assigned!"
		);

		Session res = EX.assertn( ctx.getSessionFactory().getCurrentSession(),
		  "Hibernate Session Factory assigned to Tx Context ",
		  "is not linked to the actual Session instance!"
		);

		//?: {not a manual flush mode}
		if(!FlushMode.MANUAL.equals(res.getHibernateFlushMode()))
			res.setHibernateFlushMode(FlushMode.MANUAL);

		return res;
	}

	/**
	 * Updates the Transaction Number of the instance given
	 * with the value taken from the current transaction context.
	 */
	public static void    txn(Object obj)
	{
		if(!(obj instanceof TxEntity))
			return;

		((TxEntity)obj).setTxn(txContext().txn());
	}

	public static void    txn(Tx tx, Object obj)
	{
		if(!(obj instanceof TxEntity))
			return;

		((TxEntity)obj).setTxn(tx.txn());
	}


	/* public: TxPoint (instance) interface */

	public long    newTxn()
	{
		ArrayList<Tx> s = contexts.get();
		Long          n = null;

		if(s != null) for(Tx tx : s)
			if(tx instanceof SystemTx)
			{
				n = ((SystemTx)tx).newTxn();
				break;
			}

		return EX.assertn(n,
		  "New Transaction Number may no be generated as ",
		  "there is no System Tx in the contexts stack!"
		);
	}

	/**
	 * Gives the global transaction context associated
	 * with the current request to the system. The result
	 * may be undefined.
	 */
	public Tx      getTxContext()
	{
		ArrayList<Tx> s = contexts.get();
		return ((s == null) || s.isEmpty())?(null):(s.get(s.size() - 1));
	}

	public Tx      getTxContext(Class<? extends Tx> cls)
	{
		ArrayList<Tx> s = contexts.get();
		if(s == null) return null;

		for(int i = s.size() - 1;(i >= 0);i--)
			if(cls.isAssignableFrom(s.get(i).getClass()))
				return s.get(i);

		return null;
	}

	/**
	 * Returns the global transaction context if it presents,
	 * or raises {@link IllegalStateException}.
	 */
	public Tx      getTxContextStrict()
	{
		return EX.assertn(this.getTxContext(), "Transaction context is not defined!");
	}

	/**
	 * Transaction contexts if thread form a stack:
	 * set tx instance to push, set {@code null} to pop.
	 * Note that push the same tx instance places new
	 * item to the stack, and corresponding pop is needed.
	 *
	 * TODO involve special clean method instead of setTxContext(null)
	 */
	public void    setTxContext(Tx tx)
	{
		ArrayList<Tx> s = contexts.get();

		//?: {push new context}
		if(tx != null)
		{
			//?: {has no contexts stack yet} create it
			if(s == null) contexts.set(s = new ArrayList<Tx>(4));
			s.add(tx);
		}
		//~: {pop context}
		else if((s != null) && !s.isEmpty())
		{
			tx = s.remove(s.size() - 1);
			tx.free(); //<-- and free it

			if(s.isEmpty())
				contexts.remove();
		}
	}

	/**
	 * Creates default transaction context and pushes it to the stack.
	 *
	 * Remember that tx context is not an actual transactional resources,
	 * but a collection of indirect references to actual resources controlled
	 * by Spring kernel and the application server. So, the default context
	 * is just a number of default references.
	 */
	public Tx      setTxContext()
	{
		Tx tx = EX.assertn( getTxCreator().createTxContext(),
		  "Tx Point was unable to create defualt Transaction Context!"
		);

		//~: push the tx created
		this.setTxContext(tx);
		return tx;
	}

	protected void clearTxContexts()
	{
		ArrayList<Tx> s = contexts.get();
		Throwable     e = null;

		if(s != null) while(!s.isEmpty())
		{
			Tx tx = s.get(s.size() - 1);

			//~: free the context
			try
			{
				tx.free();
			}
			catch(Throwable err)
			{
				e = err;
			}
			finally
			{
				//~: remove the context
				s.remove(s.size() - 1);
			}
		}

		contexts.remove();

		//?: {has error} throw it
		if(e != null) throw EX.wrap(e);
	}


	/* public: transaction context creator */

	/**
	 * Strategy to create default tx contexts.
	 */
	public static interface TxContextCreator
	{
		/* public: TxContextCreator interface */

		public Tx createTxContext();
	}


	/* public: TxPoint (bean) interface */

	public TxContextCreator getTxCreator()
	{
		return txCreator;
	}

	public void setTxCreator(TxContextCreator txCreator)
	{
		this.txCreator = EX.assertn(txCreator);
	}


	/* private: thread bound contexts stacks */

	private final ThreadLocal<ArrayList<Tx>>
	  contexts = new ThreadLocal<ArrayList<Tx>>();


	/* private: default contexts creator */

	private volatile TxContextCreator txCreator =
	  SystemTx.CREATOR;
}