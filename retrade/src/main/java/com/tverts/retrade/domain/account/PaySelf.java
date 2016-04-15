package com.tverts.retrade.domain.account;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Links Payment Ways of Domain own Accounts
 * (having the contractor undefined).
 *
 * @author anton.baukin@gmail.com
 */
public class PaySelf extends PayIt
{
	public void setAccount(Account account)
	{
		if((account != null) && (account.getContractor() != null))
			throw EX.arg("Not Domain own Account!");

		super.setAccount(account);
	}
}