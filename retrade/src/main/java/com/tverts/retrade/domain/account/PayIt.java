package com.tverts.retrade.domain.account;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.UnitedTxBase;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Links {@link Account} with Payment Destination
 * {@link PayWay}. Implements n-2-n linking, but
 * it assumed that each Pay Way is related to one
 * Account only.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class PayIt
       extends        UnitedTxBase
       implements     DomainEntity
{
	/* public: PayIt (bean) interface */

	public Account  getAccount()
	{
		return account;
	}

	public void     setAccount(Account account)
	{
		this.account = account;
	}

	public PayWay   getPayWay()
	{
		return payWay;
	}

	public void     setPayWay(PayWay payWay)
	{
		this.payWay = payWay;
	}


	/* public: DomainEntity interface */

	public Domain   getDomain()
	{
		return (getAccount() != null)?(getAccount().getDomain()):
		  (getPayWay() != null)?(getPayWay().getDomain()):(null);
	}


	/* account and payment destination */

	private Account account;
	private PayWay  payWay;
}