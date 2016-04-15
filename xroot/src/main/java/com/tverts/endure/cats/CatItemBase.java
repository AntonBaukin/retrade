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
	/* Catalogue Item Base */

	public Domain  getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String  getCode()
	{
		return code;
	}

	private String code;

	public void    setCode(String code)
	{
		this.code = code;
	}

	public String  getName()
	{
		return name;
	}

	private String name;

	public void    setName(String name)
	{
		this.name = name;
	}


	/* Transactional Entity */

	public Long    getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void    setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}
}