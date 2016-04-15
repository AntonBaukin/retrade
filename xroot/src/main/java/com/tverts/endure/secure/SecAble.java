package com.tverts.endure.secure;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.AltIdentity;
import com.tverts.endure.NumericBase;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.auth.AuthLogin;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Link instance between {@link AuthLogin}
 * user login entity and the Security Rule
 * instance that the user is assigned for.
 *
 * Link presence tells that the user is able
 * to do some of actions provided by the Rule.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SecAble
       extends    NumericBase
       implements DomainEntity, AltIdentity
{
	/* public: SecAble (bean) interface */

	public AuthLogin  getLogin()
	{
		return login;
	}

	public void       setLogin(AuthLogin login)
	{
		this.login = login;
	}

	public SecRule    getRule()
	{
		return rule;
	}

	public void       setRule(SecRule rule)
	{
		this.rule = rule;
	}

	public SecSet     getSet()
	{
		return set;
	}

	public void       setSet(SecSet set)
	{
		this.set = set;
	}

	public Date       getAbleTime()
	{
		return (ableTime != null)?(ableTime):
		  (ableTime = new Date());
	}

	public void       setAbleTime(Date ableTime)
	{
		this.ableTime = ableTime;
	}


	/* public: DomainEntity interface */

	public Domain     getDomain()
	{
		return (getLogin() != null)?(getLogin().getDomain()):
		  (getRule() != null)?(getRule().getDomain()):(null);
	}


	/* public: AltIdentity interface */

	public Object     altKey()
	{
		return SU.cats(
		  "class=",   getClass().getSimpleName(),
		  "&login=",  login.getPrimaryKey(),
		  "&rule=",   rule.getPrimaryKey(),
		  "&secset=", set.getPrimaryKey()
		);
	}


	/* linked entities */

	private AuthLogin login;
	private SecRule   rule;
	private SecSet    set;
	private Date      ableTime;
}