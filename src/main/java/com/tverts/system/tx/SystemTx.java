package com.tverts.system.tx;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring framework */

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.system.HiberSystem;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint.TxContextCreator;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Initial transaction context shows Hibernate
 * session within @Transactional scope as a
 * {@link Tx} instance.
 *
 * It allows mark the transaction as to rollback only,
 * it calls Spring' {@code TransactionAspectSupport}.
 *
 * Initial transaction context is in the most cases
 * the first context created.
 *
 *
 * @author anton.baukin@gmail.com
 */
class SystemTx implements Tx
{
	/* constructor */

	private SystemTx(SessionFactory sf)
	{
		this.sessionFactory = EX.assertn( sf,
		  "System Tx Context can't be created without valid ",
		  "Hibernate Session Factory provided!"
		);

		this.txid = SU.cats(TXID.incrementAndGet(), '-',
		  SU.i2h(System.currentTimeMillis()));
	}


	/* public: TxContext interface */

	public long            txn()
	{
		if(txn != null)
			return txn;

		return (txn = newTxn());
	}

	public long            newTxn()
	{
		return HiberSystem.getInstance().
		  createTxNumber(sessionFactory, this);
	}

	public void            free()
	{
		if(!isRollbackOnly()) try
		{
			Session session = TxPoint.txSession(this);
			HiberPoint.flush(session, HiberPoint.CLEAR);
		}
		catch(RuntimeException e)
		{
			//~: for debug purposes...
			throw e;
		}
	}

	public SessionFactory  getSessionFactory()
	{
		return this.sessionFactory;
	}

	public boolean         isRollbackOnly()
	{
		return this.rollbackOnly;
	}

	public void            setRollbackFlag()
	{
		setRollbackOnly();
	}

	public void            setRollbackOnly()
	{
		this.rollbackOnly = true;

		try
		{
			TransactionAspectSupport.currentTransactionStatus().
			  setRollbackOnly();
		}
		catch(NoTransactionException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public String          txid()
	{
		return this.txid;
	}

	@SuppressWarnings("unchecked")
	public <I> void        set(Class<I> cls, I instance)
	{
		EX.assertx((cls != null) && ((instance == null) ||
		  cls.isAssignableFrom(instance.getClass()))
		);

		if(instance != null)
		{
			if(adapts == null)
				adapts = new HashMap(3);
			adapts.put(cls, instance);
		}
		else if(adapts != null)
			adapts.remove(cls);
	}

	@SuppressWarnings("unchecked")
	public <I> I           get(Class<I> cls)
	{
		return (I)((adapts == null)?(null):(adapts.get(cls)));
	}

	@SuppressWarnings("unchecked")
	public Object          val(Object key)
	{
		return (vals == null)?(null):(vals.get(key));
	}

	@SuppressWarnings("unchecked")
	public void            val(Object key, Object val)
	{
		EX.assertn(key);

		if(val != null)
		{
			if(vals == null)
				vals = new HashMap(7);
			vals.put(key, val);
		}
		else if(vals != null)
			vals.remove(key);
	}


	/* private: the context state */

	private SessionFactory sessionFactory;
	private Long           txn;
	private String         txid;
	private boolean        rollbackOnly;
	private Map            adapts;
	private Map            vals;



	/* private: static context */

	static final TxContextCreator CREATOR = new TxContextCreator()
	{
		/* public: TxContextCreator interface */

		public Tx createTxContext()
		{
			return new SystemTx(
			  HiberPoint.getInstance().getSessionFactory());
		}
	};

	static final AtomicLong TXID =
	  new AtomicLong();
}