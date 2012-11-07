package com.tverts.endure.auth;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core + persons) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;
import com.tverts.endure.person.Person;


/**
 * System login account for a human {@link Person},
 * or a {@link Computer} (external) system.
 *
 * Note that a person or a computer may have
 * more than one login (with different roles,
 * supposed).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Login extends Entity implements DomainEntity
{
	/* public: UserLogin (bean) properties */

	public Domain    getDomain()
	{
		return domain;
	}

	public void      setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public Person    getPerson()
	{
		return person;
	}

	public void      setPerson(Person person)
	{
		if((this.computer != null) && (person != null))
			throw new IllegalStateException();
		this.person = person;
	}

	public Computer  getComputer()
	{
		return computer;
	}

	public void      setComputer(Computer computer)
	{
		if((computer != null) && (this.person != null))
			throw new IllegalStateException();
		this.computer = computer;
	}

	public String    getLogin()
	{
		return login;
	}

	public void      setLogin(String login)
	{
		this.login = login;
	}

	public String    getPasshash()
	{
		return passhash;
	}

	public void      setPasshash(String passhash)
	{
		this.passhash = passhash;
	}

	public Date      getCreateTime()
	{
		return createTime;
	}

	public void      setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date      getCloseTime()
	{
		return closeTime;
	}

	public void      setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}


	/* persisted attributes */

	private Domain   domain;
	private Person   person;
	private Computer computer;

	private String   login;
	private String   passhash;

	private Date     createTime;
	private Date     closeTime;
}