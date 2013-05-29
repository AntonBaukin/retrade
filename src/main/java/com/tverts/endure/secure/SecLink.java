package com.tverts.endure.secure;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.Unity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Security Links are created by {@link SecRule}.
 * Links connects {@link Unity} unified mirror
 * of some entity with the Rule.
 *
 * Entity is allowed for a User when there is
 * at least one allow Link and no deny Links
 * between some Rule able ({@link SecAble})
 * for the user.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SecLink
       extends    NumericBase
       implements DomainEntity
{
	/* public: SecLink (bean) interface */

	public SecRule getRule()
	{
		return rule;
	}

	public void setRule(SecRule rule)
	{
		this.rule = rule;
	}

	public Unity getTarget()
	{
		return target;
	}

	public void setTarget(Unity target)
	{
		this.target = target;
	}

	public SecKey getKey()
	{
		return key;
	}

	public void setKey(SecKey key)
	{
		this.key = key;
	}

	/**
	 * Flag indicating that the action defined by
	 * the Key is actually forbidden (when 1).
	 * Value 0 (default) means the action is allowed.
	 */
	public byte getDeny()
	{
		return deny;
	}

	public void setDeny(byte deny)
	{
		if((deny != 0) & (deny != 1))
			throw new IllegalArgumentException();
		this.deny = deny;
	}


	/* public: DomainEntity interface */

	public Domain getDomain()
	{
		return (getRule() == null)?(null):
		  getRule().getDomain();
	}


	/* link & attributes */

	private SecRule rule;
	private Unity   target;
	private SecKey  key;
	private byte    deny;
}