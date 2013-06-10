package com.tverts.endure.secure;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;


/**
 * Secure Set is a collection of {@link SecAble}
 * links to System Login. They form a template of
 * a Set which would be copied to ordinary logins.
 *
 * Default Secure Set has empty name, it must
 * be always created for Domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SecSet
       extends    Entity
       implements DomainEntity
{
	/* public: SecSet (bean) interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String code)
	{
		this.name = code;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
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


	/* secure set attributes */

	private Domain domain;
	private String name;
	private Date   createTime;
	private String comment;
}