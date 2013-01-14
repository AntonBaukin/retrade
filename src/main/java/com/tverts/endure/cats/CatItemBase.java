package com.tverts.endure.cats;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;
import com.tverts.endure.core.Domain;


/**
 * Implementation base for simple catalogues.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class CatItemBase
       extends        NumericBase
       implements     CatItem, TxEntity
{
	/* public: CatItemBase (bean) interface */

	public Domain  getDomain()
	{
		return domain;
	}

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String  getCode()
	{
		return code;
	}

	public void    setCode(String code)
	{
		this.code = code;
	}

	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		this.name = name;
	}


	/* public: TxEntity interface */

	public long getTxn()
	{
		return txn;
	}

	private long txn;

	public void setTxn(long txn)
	{
		this.txn = txn;
	}


	/* domain reference */

	private Domain domain;


	/* catalogue attributes */

	private String code;
	private String name;
}