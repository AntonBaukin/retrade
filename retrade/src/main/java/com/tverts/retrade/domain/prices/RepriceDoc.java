package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.PrimaryIdentity;
import com.tverts.endure.TxEntity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Document containing the changes of the prices.
 * It has no state, and after fixing may not
 * be rolled back. The price change affects the
 * price list positions immediately.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      RepriceDoc
       extends    NumericBase
       implements PrimaryIdentity, DomainEntity, TxEntity
{
	/* public: RepriceDoc (bean) interface */

	public Domain     getDomain()
	{
		return domain;
	}

	public void       setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String     getCode()
	{
		return code;
	}

	public void       setCode(String code)
	{
		this.code = code;
	}

	public PriceList  getPriceList()
	{
		return priceList;
	}

	public void       setPriceList(PriceList priceList)
	{
		this.priceList = priceList;
	}

	public Date       getChangeTime()
	{
		return changeTime;
	}

	public void       setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	public String     getChangeReason()
	{
		return changeReason;
	}

	public void       setChangeReason(String changeReason)
	{
		this.changeReason = changeReason;
	}

	public List<PriceChange> getChanges()
	{
		return (changes != null)?(changes):
		  (changes = new ArrayList<PriceChange>());
	}

	public void       setChanges(List<PriceChange> changes)
	{
		this.changes = changes;
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


	/* persisted attributes */

	private Domain    domain;
	private String    code;
	private PriceList priceList;
	private Date      changeTime;
	private String    changeReason;

	private List<PriceChange> changes;
}