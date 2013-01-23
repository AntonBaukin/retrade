package com.tverts.endure.cats;

/* Hibernate Persistence Layer */

import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txn;
import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.cats.ReadCatItems.OnCatItem;


/**
 * Basic implementation of catalogue items
 * read and database check plus save callback.
 *
 * @author anton.baukin@gmail.com
 */
public class      SaveCatItemsBase<C extends CatItem>
       implements OnCatItem<C>
{
	/* public: constructor */

	public SaveCatItemsBase(Domain domain, Class<C> itemClass)
	{
		this.domain    = domain;
		this.itemClass = itemClass;
	}


	/* public: OnCatItemBase interface */

	public Class<C>         getItemClass()
	{
		return itemClass;
	}

	public SaveCatItemsBase session(Session session)
	{
		this.session = session;
		return this;
	}


	/* public: OnCatItem interface */

	public void             onCatItem(C item)
	{
		C stored = search(item);

		//?: {the item is found in the database}
		if(stored != null)
			ensure(stored, item);
		//!: save the item
		else
			save(item);
	}


	/* protected: items handling support */

	protected Session       session()
	{
		return (session != null)?(session):(txSession());
	}

	@SuppressWarnings("unchecked")
	protected C             search(C item)
	{
/*

from CatItem ci where (ci.domain = :domain)
  and (ci.code = :code)

*/
		return (C) HiberPoint.query(session(),

"from CatItem ci where (ci.domain = :domain)\n" +
"  and (ci.code = :code)",

		"CatItem", getItemClass()
		).
		  setParameter("domain", domain).
		  setString   ("code",   item.getCode()).
		  uniqueResult();
	}

	public void             save(C item)
	{
		//~: set primary key
		HiberPoint.setPrimaryKey(session(), item,
		  HiberPoint.isTestInstance(domain)
		);

		//~: set the domain
		item.setDomain(domain);

		//0: do try save via actions system first
		if(ActionsPoint.actionOrNullRun(ActionType.SAVE, item) == null)
		{
			//~: assign transaction number
			txn(item);

			//1: save in a plain
			session().save(item);
		}
	}

	public void             ensure(C stored, C item)
	{}


	/* callback parameters */

	protected final Domain   domain;
	protected final Class<C> itemClass;
	private Session          session;
}