package com.tverts.model.store;

/* Java */

import java.util.Date;


/**
 * Hibernate mapping class for a Models Store entry.
 * Primary key is the key of the Model Bean stored.
 *
 * @author anton.baukin@gmail.com
 */
public class ModelRecord
{
	public Long getKey()
	{
		return key;
	}

	private Long key;

	public void setKey(Long key)
	{
		this.key = key;
	}

	public Date getAccessTime()
	{
		return accessTime;
	}

	private Date accessTime;

	public void setAccessTime(Date accessTime)
	{
		this.accessTime = accessTime;
	}

	public long getDomain()
	{
		return domain;
	}

	private long domain;

	public void setDomain(long domain)
	{
		this.domain = domain;
	}

	public long getLogin()
	{
		return login;
	}

	private long login;

	public void setLogin(long login)
	{
		this.login = login;
	}

	public byte[] getBean()
	{
		return bean;
	}

	private byte[] bean;

	public void setBean(byte[] bean)
	{
		this.bean = bean;
	}
}