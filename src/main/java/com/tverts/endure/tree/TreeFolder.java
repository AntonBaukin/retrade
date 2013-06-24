package com.tverts.endure.tree;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.PrimaryIdentity;
import com.tverts.endure.TxEntity;


/**
 * A folder within Tree Domain.
 *
 * @author anton.baukin@gmail.com
 */
public class      TreeFolder
       extends    NumericBase
       implements PrimaryIdentity, TxEntity
{
	/* public: TreeFolder (bean) interface */

	public TreeDomain getDomain()
	{
		return domain;
	}

	public void setDomain(TreeDomain domain)
	{
		this.domain = domain;
	}

	public TreeFolder getParent()
	{
		return parent;
	}

	public void setParent(TreeFolder parent)
	{
		this.parent = parent;
	}

	/**
	 * Code unique within Tree Domain.
	 */
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* tree domain reference */

	private TreeDomain domain;
	private TreeFolder parent;


	/* folder attributes */

	private String     code;
	private String     name;
}