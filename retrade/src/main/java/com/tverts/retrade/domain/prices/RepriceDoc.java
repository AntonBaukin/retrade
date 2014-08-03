package com.tverts.retrade.domain.prices;

/* Java */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* com.tverts: api */

import com.tverts.api.retrade.prices.PriceChanges;

/* com.tverts: endure (core) */

import com.tverts.endure.cats.CodedEntity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.OxEntity;

/* com.tverts: support */

import com.tverts.support.EX;


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
       extends    OxEntity
       implements DomainEntity, CodedEntity
{
	/* Object Extraction */

	public PriceChanges getOx()
	{
		PriceChanges pc = (PriceChanges) super.getOx();
		if(pc == null) setOx(pc = new PriceChanges());
		return pc;
	}

	public void setOx(Object ox)
	{
		EX.assertx(ox instanceof PriceChanges);
		super.setOx(ox);
	}


	/* Price Change Document */

	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void setCode(String code)
	{
		this.code = code;
	}

	public Date getChangeTime()
	{
		return changeTime;
	}

	private Date changeTime;

	public void setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	public PriceListEntity getPriceList()
	{
		return priceList;
	}

	private PriceListEntity priceList;

	public void setPriceList(PriceListEntity priceList)
	{
		this.priceList = priceList;
	}

	public List<PriceChange> getChanges()
	{
		return (changes != null)?(changes):
		  (changes = new ArrayList<PriceChange>(32));
	}

	private List<PriceChange> changes;

	public void setChanges(List<PriceChange> changes)
	{
		this.changes = changes;
	}
}