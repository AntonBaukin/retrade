package com.tverts.retrade.domain.account;

/**
 * Payment to the bank account.
 *
 * @author anton.baukin@gmail.com
 */
public class PayBank extends PayWay
{
	/* public: PayBank (bean) interface */

	public String getBankId()
	{
		return bankId;
	}

	public void setBankId(String bankId)
	{
		this.bankId = bankId;
	}

	public String getBankName()
	{
		return bankName;
	}

	public void setBankName(String bankName)
	{
		this.bankName = bankName;
	}

	public String getBankAccount()
	{
		return bankAccount;
	}

	public void setBankAccount(String bankAccount)
	{
		this.bankAccount = bankAccount;
	}

	public String getRemitteeAccount()
	{
		return remitteeAccount;
	}

	public void setRemitteeAccount(String remitteeAccount)
	{
		this.remitteeAccount = remitteeAccount;
	}

	public String getRemitteeName()
	{
		return remitteeName;
	}

	public void setRemitteeName(String remitteeName)
	{
		this.remitteeName = remitteeName;
	}

	public String getRemitteeTaxId()
	{
		return remitteeTaxId;
	}

	public void setRemitteeTaxId(String remitteeTaxId)
	{
		this.remitteeTaxId = remitteeTaxId;
	}

	public String getRemitteeTaxCode()
	{
		return remitteeTaxCode;
	}

	public void setRemitteeTaxCode(String remitteeTaxCode)
	{
		this.remitteeTaxCode = remitteeTaxCode;
	}


	/* bank payment attributes */

	private String bankId;
	private String bankName;
	private String bankAccount;

	private String remitteeAccount;
	private String remitteeName;
	private String remitteeTaxId;
	private String remitteeTaxCode;
}