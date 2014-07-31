package com.tverts.retrade.domain.prices;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: support */

import com.tverts.support.EX;


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
		EX.assertn(contractor);

		final String Q =

"  from FirmPrices fp where (fp.contractor.id = :cid) order by fp.priority";

		return list(FirmPrices.class, Q, "cid", contractor);
	}


	/* Price Crosses  */

	public void deletePriceCrosses(FirmPrices fp)
	{

// delete from PriceCross where (firmPrices = :fp)

		final String Q =
"  delete from PriceCross where (firmPrices = :fp)";

		Q(Q, "fp", fp).executeUpdate();
	}

	/**
	 * Selects the effective prices of the Price Lists
	 * associated with the contractor given according
	 * to the priority of the Firm Prices assigned.
	 *
	 * Returns arrays of:
	 *
	 * [0] Good Unit primary key;
	 * [1] Good Price primary key.
	 */
	public List<Object[]> selectEffectivePrices(Long contractor)
	{

/*

 select gp.goodUnit.id, gp.id from GoodPrice gp where
  gp.priceList.id = (select fp.priceList.id from
   FirmPrices fp where (fp.contractor.id = :c) and
    fp.priority = (select max(fp2.priority) from
     FirmPrices fp2 join fp2.priceList pl2, GoodPrice gp2
      where (fp2.contractor.id = :c) and
       (gp2.priceList.id = pl2.id) and
       (gp2.goodUnit.id = gp.goodUnit.id)
    )
  )

 */

		final String Q =

"select gp.goodUnit.id, gp.id from GoodPrice gp where\n" +
" gp.priceList.id = (select fp.priceList.id from\n" +
"  FirmPrices fp where (fp.contractor.id = :c) and\n" +
"   fp.priority = (select max(fp2.priority) from\n" +
"    FirmPrices fp2 join fp2.priceList pl2, GoodPrice gp2\n" +
"     where (fp2.contractor.id = :c) and\n" +
"      (gp2.priceList.id = pl2.id) and\n" +
"      (gp2.goodUnit.id = gp.goodUnit.id)\n" +
"   )\n" +
" )";

		return list(Object[].class, Q, "c", contractor);
	}

	/**
	 * Selects the exiting {@link PriceCross} items.
	 *
	 * Returns arrays of:
	 *
	 * [0] Good Unit primary key;
	 * [1] Good Price primary key;
	 * [2] Price Cross primary key.
	 */
	public List<Object[]> selectCurrentPrices(Long contractor)
	{

/*

 select pc.goodUnit.id, pc.goodPrice.id, pc.id
 from PriceCross pc where (pc.contractor.id = :c)

 */

		final String Q =

"select pc.goodUnit.id, pc.goodPrice.id, pc.id\n" +
"from PriceCross pc where (pc.contractor.id = :c)";

		return list(Object[].class, Q, "c", contractor);
	}
}