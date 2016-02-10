package com.tverts.endure.auth;

/* Java */

import java.util.Date;

/* com.tverts: endure (core + catalogues + persons) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;
import com.tverts.endure.person.PersonEntity;
import com.tverts.endure.person.Persons;


/**
 * System login account for a {@link PersonEntity},
 * or a {@link ComputerEntity} (external) system.
 *
 * Note that a person or a computer may have
 * more than one login (with different roles,
 * supposed).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AuthLogin extends Entity implements CatItem
{
	/* public: UserLogin (bean) properties */

	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public PersonEntity getPerson()
	{
		return person;
	}

	private PersonEntity person;

	public void setPerson(PersonEntity person)
	{
		if((this.computer != null) && (person != null))
			throw new IllegalStateException();
		this.person = person;
	}

	public ComputerEntity getComputer()
	{
		return computer;
	}

	private ComputerEntity computer;

	public void setComputer(ComputerEntity computer)
	{
		if((computer != null) && (this.person != null))
			throw new IllegalStateException();
		this.computer = computer;
	}

	/**
	 * Login string code unique within the domain.
	 * When AuthLogin is being closed, the code
	 * is concatenated with the close time.
	 */
	public String getCode()
	{
		return login;
	}

	private String login;

	public void setCode(String login)
	{
		this.login = login;
	}

	public String getName()
	{
		if(name != null)
			return name;

		if(getPerson() != null)
			return name = Persons.name(getPerson());
		else if(getComputer() != null)
			return name = Auth.name(getComputer());

		return null;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * HEX string with 20-byte SHA-1 hash of the password.
	 *
	 * Only this hash is stored in the database,
	 * not the real text of the password.
	 */
	public String getPasshash()
	{
		return passhash;
	}

	private String passhash;

	public void setPasshash(String passhash)
	{
		this.passhash = passhash;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	private Date createTime;

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date getCloseTime()
	{
		return closeTime;
	}

	private Date closeTime;

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public String getDescr()
	{
		return descr;
	}

	private String descr;

	public void setDescr(String descr)
	{
		this.descr = descr;
	}

	public String getUserLinks()
	{
		return userLinks;
	}

	private String userLinks;

	public void setUserLinks(String userLinks)
	{
		this.userLinks = userLinks;
	}
}