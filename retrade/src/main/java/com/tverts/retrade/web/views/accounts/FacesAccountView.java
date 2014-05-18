package com.tverts.retrade.web.views.accounts;

/* standard Java classes */

import java.math.BigDecimal;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.Account;
import com.tverts.retrade.domain.account.AccountModelBean;
import com.tverts.retrade.domain.account.GetAccount;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The main info view of the {@link Account}.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesAccountView") @Scope("request")
public class FacesAccountView extends UnityModelView
{
	/* public: view interface */

	public AccountModelBean getModel()
	{
		return (AccountModelBean) super.getModel();
	}

	public Account getEntity()
	{
		return (Account) super.getEntity();
	}

	public BigDecimal getIncome()
	{
		return loadAccountBalance()[0];
	}

	public BigDecimal getExpense()
	{
		return loadAccountBalance()[1];
	}

	public BigDecimal getBalance()
	{
		return loadAccountBalance()[2];
	}

	public boolean isOwnAccount()
	{
		return (getEntity().getContractor() == null);
	}

	public String getWinmainTitleInfo()
	{
		if(isOwnAccount()) return SU.cats(
		 "Собственный учётный счёт №", getEntity().getCode(),
		 ", ", getEntity().getName()
		);

		return SU.cats(
		  "Учётный счёт №", getEntity().getCode(),
		  " контрагента №", getEntity().getContractor().getCode(),
		  ' ', getEntity().getContractor().getName()
		);
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new AccountModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof AccountModelBean);
	}


	/* protected: support interface */

	private BigDecimal[] balance;

	protected BigDecimal[] loadAccountBalance()
	{
		if(balance != null)
			return balance;

		return balance = bean(GetAccount.class).
		  getAccountBalance(getModel().getPrimaryKey());
	}
}