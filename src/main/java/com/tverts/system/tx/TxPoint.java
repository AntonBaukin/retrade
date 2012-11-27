package com.tverts.system.tx;

/* standard Java classes */

import java.util.ArrayList;

/* Hibernate Persistence Layer */

import org.hibernate.Session;


/**
 * COMMENT TxPoint
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

	private static final TxPoint INSTANCE =
	  new TxPoint();

	protected TxPoint()
	{}


	/* public: TxPoint interface */

	public static TxContext txContext()
	{
		return TxPoint.getInstance().getTxContextStrict();
	}

	public static Session   txSession()
	{
		TxContext ctx = txContext();
		if(ctx.getSessionFactory() == null)
			throw new IllegalStateException(String.format(
			  "Tx Context of class [%s] has no " +
			  "Hibernate Session Factory assigned!",
			  ctx.getClass().getSimpleName()
			));

		Session   res = ctx.getSessionFactory().getCurrentSession();
		if(res == null) throw new IllegalStateException(
		  "Hibernate Session Factory assigned to Tx Context " +
		  "is not linked to the actual Session instance!"
		);

		return res;
	}

	/**
	 * Gives the global transaction context associated
	 * with the current request to the system. The result
	 * may be undefined.
	 */
	public TxContext        getTxContext()
	{
		ArrayList<TxContext> s = contexts.get();
		return s.isEmpty()?(null):(s.get(s.size() - 1));
	}

	/**
	 * Returns the global transaction context if it presents,
	 * or raises {@link IllegalStateException}.
	 */
	public TxContext        getTxContextStrict()
	{
		TxContext tx = this.getTxContext();

		if(tx == null) throw new IllegalStateException(
		  "Global transaction context is not defined!"
		);

		return tx;
	}

	/**
	 * Transaction contexts if thread form a stack:
	 * set tx instance to push, set {@code null} to pop.
	 * Note that push the same tx instance places new
	 * item to the stack, and corresponding pop is needed.
	 */
	public void             setTxContext(TxContext tx)
	{
		ArrayList<TxContext> s = contexts.get();

		//?: {has no contexts stack yet} create it
		if(s == null)
			contexts.set(s = new ArrayList<TxContext>(1));

		//?: {has context defined} add to the end (the top)
		if(tx != null)
			s.add(tx);
		//?: {pop context query}
		else if(!s.isEmpty())
			s.remove(s.size() - 1);
	}

	/**
	 * Creates default transaction context and pushes it to the stack.
	 *
	 * Remember that tx context is not an actual transactional resources,
	 * but a collection of indirect references to actual resources controlled
	 * by Spring kernel and the application server. So, the default context
	 * is just a number of default references.
	 */
	public void             setTxContext()
	{
		TxContext tx = getTxCreator().createTxContext();

		//?: {was unable to create tx context}
		if(tx == null) throw new IllegalStateException(
		  "Tx Point was unable to create defualt Transaction Context!"
		);

		//~: push the tx created
		this.setTxContext(tx);
	}

	protected void          clearTxContexts()
	{
		contexts.remove();
	}


	/* public: transaction context creator */

	/**
	 * Strategy to create default tx contexts.
	 */
	public static interface TxContextCreator
	{
		/* public: TxContextCreator interface */

		public TxContext createTxContext();
	}


	/* public: TxPoint bean interface */

	public TxContextCreator getTxCreator()
	{
		return txCreator;
	}

	public void             setTxCreator(TxContextCreator txCreator)
	{
		if(txCreator == null) throw new  IllegalArgumentException();
		this.txCreator = txCreator;
	}


	/* private: thread bound contexts stacks */

	private final ThreadLocal<ArrayList<TxContext>>
	  contexts = new ThreadLocal<ArrayList<TxContext>>();


	/* private: default contexts creator */

	private volatile TxContextCreator txCreator =
	  SystemTxContext.CREATOR;
}