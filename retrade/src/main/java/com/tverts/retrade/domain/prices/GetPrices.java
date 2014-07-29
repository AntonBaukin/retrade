package com.tverts.retrade.domain.prices;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Collection of database routines to access
 * Price Lists and the related entities.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetPrices extends GetObjectBase
{
	/* Firm Prices  */

	public List<FirmPrices> loadPrices(Long contractor)
	{
		final String Q =

"  from FirmPrices fp where (fp.contractor.id = :cid) order by fp.priority";

		return list(FirmPrices.class, Q, "cid", contractor);
	}
}