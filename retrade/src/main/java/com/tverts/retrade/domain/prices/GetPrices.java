package com.tverts.retrade.domain.prices;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Collection of database routines to access
 * Price Lists and the related entities.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetPrices extends GetGoods
{
	/* Firm Prices  */

	public List<FirmPrices> loadPrices(Long contractor)
	{
		EX.assertn(contractor);

		final String Q =

"  from FirmPrices fp where (fp.contractor.id = :cid) order by fp.priority";

		return list(FirmPrices.class, Q, "cid", contractor);
	}

	/**
	 * Returns the number of Good Units available
	 * for the given client contractor (firm).
	 */
	public int countContractorGoodUnits(GoodsModelBean mb, Long contractor)
	{
		EX.assertn(mb);
		EX.assertn(contractor);

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("PriceCross", PriceCross.class);
		qb.setClauseFrom(
		  "PriceCross pc join pc.goodUnit gu " +
		  "join pc.goodPrice gp join gp.priceList pl"
		);

		//~: select clause
		qb.setClauseSelect("count(gu.id)");

		//~: restrict contractor
		qb.getClauseWhere().addPart(
		  "pc.contractor.id = :c"
		).
		  param("c", contractor);

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * Returns the prices of the Good Units available
	 * for the given client contractor (firm).
	 *
	 * Returns rows are:
	 * [0] Good Unit;
	 * [1] Good Price;
	 * [2] Price List Key;
	 * [3] Price List Code;
	 * [4] Price List Name.
	 */
	public List selectContractorGoodUnits(GoodsModelBean mb, Long contractor)
	{
		EX.assertn(mb);
		EX.assertn(contractor);

		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("PriceCross", PriceCross.class);
		qb.setClauseFrom(
		  "PriceCross pc join pc.goodUnit gu " +
		  "join pc.goodPrice gp join gp.priceList pl"
		);

		//~: select clause
		qb.setClauseSelect("gu, gp, pl.id, pl.code, pl.name");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict contractor
		qb.getClauseWhere().addPart(
		  "pc.contractor.id = :c"
		).
		  param("c", contractor);

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		return QB(qb).list();
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
	public List selectEffectivePrices(Long contractor)
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
	public List selectCurrentPrices(Long contractor)
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


	/* protected: shortage routines */

	protected String[] gusSearch()
	{
		return new String[] {
		  "gu.unity.oxSearch like :w"
		  //"pl.unity.oxSearch like :w"
		};
	}
}