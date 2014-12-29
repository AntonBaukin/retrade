package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodsModelBean;
import com.tverts.retrade.domain.goods.GoodsTreeModelBean;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.MeasureUnit;

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
	/* Get Price Lists */

	public PriceListEntity getPriceList(Long pk)
	{
		return get(PriceListEntity.class, pk);
	}

	public PriceListEntity getPriceList(Long domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

/*

 from PriceListEntity pl where
   (pl.domain.id = :domain) and (pl.code = :code)

*/
		final String Q =

"from PriceListEntity pl where\n" +
"  (pl.domain.id = :domain) and (pl.code = :code)";

		return object(PriceListEntity.class, Q, "domain", domain, "code", code);
	}

	@SuppressWarnings("unchecked")
	public List<PriceListEntity> getPriceLists(Long domain)
	{
		EX.assertn(domain);

/*

from PriceListEntity pl where (pl.domain.id = :domain)
order by lower(pl.name)

*/
		return (List<PriceListEntity>) Q(

"from PriceListEntity pl where (pl.domain.id = :domain)\n" +
"order by lower(pl.name)"

		).
		  setLong("domain", domain).
		  list();
	}

	public PriceListEntity getPriceListDefault(Long domain)
	{
		EX.assertn(domain);

/*

from PriceListEntity pl where (pl.domain.id = :domain)
order by lower(pl.name)

*/
		return (PriceListEntity) Q(

"from PriceListEntity pl where (pl.domain.id = :domain)\n" +
"order by lower(pl.name)"

		).
		  setMaxResults(1).
		  setLong("domain", domain).
		  uniqueResult();
	}

	public List<Long>      getGoodsWithPrices(Long domain)
	{
/*

 select distinct gu.id from
   GoodPrice gp join gp.goodUnit gu
 where (gu.domain.id = :domain)

 */
		return list(Long.class,

"select distinct gu.id from\n" +
"  GoodPrice gp join gp.goodUnit gu\n" +
"where (gu.domain.id = :domain)",

		  "domain", domain
		);
	}

	public List<GoodPrice> getGoodPrices(Long goodUnit)
	{
		EX.assertn(goodUnit);

// from GoodPrice where (goodUnit.id = :goodUnit)

		return list(GoodPrice.class,

"from GoodPrice where (goodUnit.id = :goodUnit)",

		  "goodUnit", goodUnit
		);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]>  getGoodPricesLists(Long goodUnit)
	{
		EX.assertn(goodUnit);

/*

select gp, pl from GoodPrice gp join gp.priceList pl
  where (gp.goodUnit.id = :goodUnit)
order by lower(pl.name)

*/
		return (List<Object[]>) Q(

"select gp, pl from GoodPrice gp join gp.priceList pl\n" +
"  where gp.goodUnit.id = :goodUnit\n" +
"order by lower(pl.name)"

		).
		  setLong("goodUnit", goodUnit).
		  list();
	}

	/**
	 * Returns list of arrays [3] where:
	 *
	 *  0  is the Good Unit primary key;
	 *  1  is the good price (BigDecimal);
	 *  2  Txn of the GoodPrice change.
	 */
	@SuppressWarnings("unchecked")
	public List            getPriceListPrices(Long priceList)
	{
		EX.assertn(priceList);

/*

select gu.id, gp.price, gp.txn from
  GoodPrice gp join gp.goodUnit gu
where (gp.priceList.id = :priceList)
order by gu.id

*/

		return Q(

"select gu.id, gp.price, gp.txn from\n" +
"  GoodPrice gp join gp.goodUnit gu\n" +
"where (gp.priceList.id = :priceList)\n" +
"order by gu.id"

		).
		  setLong("priceList", priceList).
		  list();
	}

	/**
	 * Returns filtered by the Good Unit codes
	 * list of arrays [3] where:
	 *
	 *  0  Good Unit;
	 *  1  Measure Unit;
	 *  2  good price (BigDecimal or null).
	 */
	@SuppressWarnings("unchecked")
	public List            getPriceListPrices
	  (PriceListEntity priceList, Collection<String> goodCodes)
	{
		EX.assertn(priceList);

/*


 select gu, mu, 1 from GoodUnit gu join gu.measure mu
   where (gu.domain = :domain) and gu.code in (:goodCodes)

 select gu.id, gp.price from GoodPrice gp join gp.goodUnit gu
   where (gp.priceList = :priceList) and gu.code in (:goodCodes)

*/

		//~: select goods
		List<Object[]> gs = (List<Object[]>) Q(

"select gu, mu, 1 from GoodUnit gu join gu.measure mu\n" +
"  where (gu.domain = :domain) and gu.code in (:goodCodes)"

		).
		  setParameter("domain", priceList.getDomain()).
		  setParameterList("goodCodes", goodCodes).
		  list();


		//~: select prices
		List<Object[]> ps = (List<Object[]>) Q(

"select gu.id, gp.price from GoodPrice gp join gp.goodUnit gu\n" +
"  where (gp.priceList = :priceList) and gu.code in (:goodCodes)"

		).
		  setParameter    ("priceList", priceList).
		  setParameterList("goodCodes", goodCodes).
		  list();

		//~: map the prices
		HashMap<Long, BigDecimal> m = new HashMap<>(gs.size());

		for(Object[] o : ps)
			m.put((Long)o[0], (BigDecimal)o[1]);
		for(Object[] o : gs)
			o[2] = m.get(((GoodUnit)o[0]).getPrimaryKey());

		return gs;
	}

	public List<GoodPrice> getPriceListPrices(PriceListEntity pl)
	{
		EX.assertn(pl);

		final String Q =
"  from GoodPrice gp where (gp.priceList = :pl)";

		return list(GoodPrice.class, Q, "pl", pl);
	}

	/**
	 * Retrieves all the prices of the list given and maps
	 * them as Good Unit key to Good Price key.
	 */
	@SuppressWarnings("unchecked")
	public void            getPriceListPrices(Long pl, Map<Long, Long> pm)
	{
		EX.assertn(pl);
		EX.assertn(pm);

/*

 select gu.id, gp.id from
   GoodPrice gp join gp.goodUnit gu
 where (gp.priceList.id = :pl)

 */
		final String Q =

"select gu.id, gp.id from\n" +
"  GoodPrice gp join gp.goodUnit gu\n" +
"where (gp.priceList.id = :pl)";

		List rows = list(List.class, Q, "pl", pl);
		for(Object[] x : (List<Object[]>)rows)
			pm.put((Long)x[0], (Long)x[1]);
	}

	public GoodPrice       getGoodPrice(Long pk)
	{
		return (GoodPrice) session().get(GoodPrice.class, pk);
	}

	public GoodPrice       getGoodPrice(PriceListEntity pl, GoodUnit gu)
	{
		EX.assertn(pl);
		EX.assertn(gu);

/*

from GoodPrice gp where
  (gp.priceList = :pl) and (gp.goodUnit = :gu)

*/
		return object( GoodPrice.class,

"from GoodPrice gp where\n" +
"  (gp.priceList = :pl) and (gp.goodUnit = :gu)",

		  "pl", pl,
		  "gu", gu
		);
	}

	public GoodPrice       getGoodPrice(Long pl, Long gu)
	{
		EX.assertn(pl);
		EX.assertn(gu);

/*

from GoodPrice gp where
  (gp.priceList.id = :pl) and (gp.goodUnit.id = :gu)

*/
		return object( GoodPrice.class,

"from GoodPrice gp where\n" +
"  (gp.priceList.id = :pl) and (gp.goodUnit.id = :gu)",

		  "pl", pl,
		  "gu", gu
		);
	}


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
	public int  countContractorGoodUnits(GoodsModelBean mb, Long contractor)
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
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSetAlsoPriceLists(qb, mb.getSelSet(), true);

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
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSetAlsoPriceLists(qb, mb.getSelSet(), true);

		return QB(qb).list();
	}

	public int  countContractorGoodUnits(GoodsTreeModelBean mb, Long contractor)
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

		//~: restrict the tree folder
		restrictTreeGoods(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), false);

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * The result is the same as for the method:
	 * {@link #selectContractorGoodUnits(GoodsModelBean, Long)}.
	 */
	public List selectContractorGoodUnits(GoodsTreeModelBean mb, Long contractor)
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

		//~: restrict the tree folder
		restrictTreeGoods(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		return QB(qb).list();
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
	 * Finds the effective associated price for the
	 * given Contractor and the good. Returns array of:
	 *
	 * [0] Good Price;
	 * [1] Firm Prices.
	 */
	public Object[] findEffectivePrice(Long co, Long gu)
	{
/*

 select gp, fp from FirmPrices fp, GoodPrice gp where
   (fp.contractor.id = :co) and (gp.goodUnit.id = :gu)
   and (fp.priceList.id = gp.priceList.id)
 order by fp.priority desc

 */
		final String Q =

"select gp, fp from FirmPrices fp, GoodPrice gp where\n" +
"  (fp.contractor.id = :co) and (gp.goodUnit.id = :gu)\n" +
"  and (fp.priceList.id = gp.priceList.id)\n" +
"order by fp.priority desc";

		return first(Object[].class, Q, "co", co, "gu", gu);
	}


	/* Price Crosses  */

	public void deletePriceCrosses(FirmPrices fp, Map<Long, PriceCross> cache)
	{

// from PriceCross where (firmPrices = :fp)

		final String Q =
"  from PriceCross where (firmPrices = :fp)";

		//~: select the items
		List<PriceCross> sel = list(PriceCross.class, Q, "fp", fp);

		//~: map them and remove
		for(PriceCross pc : sel)
		{
			//~: un-proxy & cache
			pc = HiberPoint.unproxy(pc);
			cache.put(pc.getPrimaryKey(), pc);

			//!: remove the item
			session().delete(pc);
		}
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

	public List<PriceCross> selectCrosses(GoodPrice gp)
	{
		final String Q =
"from PriceCross where (goodPrice = :gp)";

		return list(PriceCross.class, Q, "gp", gp);
	}

	/**
	 * This query finds the Price Crosses of all the Contractors
	 * having associated the given Price List that become ineffective
	 * if a Good Price for the given Good Unit would be inserted
	 * in that Price List with greater priority.
	 */
	public List<PriceCross> findObsoleteCrosses(PriceListEntity pl, GoodUnit gu)
	{
/*

 select pc from PriceCross pc where (pc.goodPrice.goodUnit = :gu)
   and pc.firmPrices.priority < (select fp.priority from FirmPrices fp
     where (fp.priceList = :pl) and (fp.contractor.id = pc.contractor.id))

 */
		final String Q =

"select pc from PriceCross pc where (pc.goodPrice.goodUnit = :gu)\n" +
"  and pc.firmPrices.priority < (select fp.priority from FirmPrices fp\n" +
"    where (fp.priceList = :pl) and (fp.contractor.id = pc.contractor.id))";

		return list(PriceCross.class, Q, "pl", pl, "gu", gu);
	}


	/* Get Price Changes (prices history) */

	public RepriceDoc      getRepriceDoc(Long pk)
	{
		return (pk == null)?(null):
		  (RepriceDoc) session().get(RepriceDoc.class, pk);
	}

	public RepriceDoc      getRepriceDoc(Long domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

/*

 from RepriceDoc rd where
   (rd.domain.id = :domain) and (rd.code = :code)

*/

		return (RepriceDoc) Q(

"from RepriceDoc rd where\n" +
"  (rd.domain.id = :domain) and (rd.code = :code)"

		).
		  setLong("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	public PriceChange     getPriceChange(Long pk)
	{
		return (PriceChange) session().get(PriceChange.class, pk);
	}

	public PriceChange     getPriceChangeBefore(Long pl, Long gu, Date time)
	{
/*

 from PriceChange pc where (pc.priceList.id = :pl) and
   (pc.goodUnit.id = :gu) and (pc.changeTime < :time)
 order by pc.changeTime desc

*/
		final String Q =

"from PriceChange pc where (pc.priceList.id = :pl) and\n" +
" (pc.goodUnit.id = :gu) and (pc.changeTime < :time)\n" +
"order by pc.changeTime desc";

		return first(PriceChange.class, Q, "pl", pl, "gu", gu, "time", time);
	}

	public PriceChange     getPriceChangeAfter(Long pl, Long gu, Date time)
	{
/*

 from PriceChange pc where (pc.priceList.id = :pl) and
   (pc.goodUnit.id = :gu) and (pc.changeTime > :time)
 order by pc.changeTime asc

*/
		final String Q =

"from PriceChange pc where (pc.priceList.id = :pl) and\n" +
"  (pc.goodUnit.id = :gu) and (pc.changeTime > :time)\n" +
"order by pc.changeTime asc";

		return first(PriceChange.class, Q, "pl", pl, "gu", gu, "time", time);
	}

	public List<PriceChange> selectPriceHistory(Long priceList, Long goodUnit)
	{
/*

 from PriceChange pc where (pc.priceList.id = :pl) and
   (pc.goodUnit.id = :gu) order by pc.changeTime

 */
		final String Q =

"from PriceChange pc where (pc.priceList.id = :pl) and\n" +
"  (pc.goodUnit.id = :gu) order by pc.changeTime";

		return list(PriceChange.class, Q, "pl", priceList, "gu", goodUnit);
	}

	/**
	 * Returns array of {@link PriceChange} and
	 * {@link RepriceDoc} instances.
	 */
	public List            selectPriceHistory(GoodPriceModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("PriceChange pc join pc.repriceDoc rd");
		qb.nameEntity("PriceChange",  PriceChange.class);

		//~: select clause
		qb.setClauseSelect("pc, rd");

		//~: order by
		qb.setClauseOrderBy("pc.changeTime");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: good unit
		qb.getClauseWhere().addPart(
		  "pc.goodUnit = :good"
		).
		  param("good", mb.goodPrice().getGoodUnit());

		//~: price list
		qb.getClauseWhere().addPart(
		  "rd.priceList = :plist"
		).
		  param("plist", mb.goodPrice().getPriceList());

		//~: restrict by the dates
		restrictDates(qb, mb);


		return QB(qb).list();
	}

	public long            countPriceHistory(GoodPriceModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("PriceChange pc join pc.repriceDoc rd");
		qb.nameEntity("PriceChange",  PriceChange.class);

		//~: select clause
		qb.setClauseSelect("count(pc.id)");


		//~: good unit
		qb.getClauseWhere().addPart(
		  "pc.goodUnit = :goodUnit"
		).
		  param("goodUnit", mb.goodPrice().getGoodUnit());

		//~: price list
		qb.getClauseWhere().addPart(
		  "rd.priceList = :plist"
		).
		  param("plist", mb.goodPrice().getPriceList());

		//~: restrict by the dates
		restrictDates(qb, mb);


		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	public List            selectReprices(RepriceDocsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("RepriceDoc rd join rd.priceList pl");
		qb.nameEntity("RepriceDoc",  RepriceDoc.class);

		//~: select clause
		qb.setClauseSelect("rd, pl");

		//~: order by
		if(mb.isFixedOnly())
			qb.setClauseOrderBy("rd.changeTime");
		else
			qb.setClauseOrderBy("rd.code");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: domain
		qb.getClauseWhere().addPart(
		  "rd.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: not fixed only
		if(!mb.isFixedOnly())
			qb.getClauseWhere().addPart(
			  "rd.changeTime is null");

		//~: restrict by the dates
		if(mb.isFixedOnly())
			restrictDates(qb, mb);

		return QB(qb).list();
	}

	public long            countReprices(RepriceDocsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("RepriceDoc rd join rd.priceList pl");
		qb.nameEntity("RepriceDoc",  RepriceDoc.class);

		//~: select clause
		qb.setClauseSelect("count(rd.id)");


		//~: domain
		qb.getClauseWhere().addPart(
		  "rd.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: not fixed only
		if(!mb.isFixedOnly())
			qb.getClauseWhere().addPart(
			  "rd.changeTime is null");

		//~: restrict by the dates
		if(mb.isFixedOnly())
			restrictDates(qb, mb);

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	public List            selectPriceChanges(RepriceDocModelBean mb)
	{
		return selectPriceChanges(mb.repriceDoc());
	}

	/**
	 * Returns array of {@link PriceChange}, {@link GoodUnit},
	 * {@link GoodPrice} or null, and {@link MeasureUnit} instances.
	 */
	@SuppressWarnings("unchecked")
	public List            selectPriceChanges(RepriceDoc rd)
	{
/*

 select pc, gu, 1, mu from
   PriceChange pc join pc.goodUnit gu join gu.measure mu
 where (pc.repriceDoc = :rd)
 order by pc.docIndex

 select gu.id, gp from GoodPrice gp join gp.goodUnit gu where
   (gp.priceList = :pl) and gu.id in (select pc.goodUnit.id from
     PriceChange pc where (pc.repriceDoc = :rd))

 */
		final String S =

"select pc, gu, 1, mu from\n" +
"  PriceChange pc join pc.goodUnit gu join gu.measure mu\n" +
"where (pc.repriceDoc = :rd)\n" +
"order by pc.docIndex";


		final String P =

"select gu.id, gp from GoodPrice gp join gp.goodUnit gu where\n" +
" (gp.priceList = :pl) and gu.id in (select pc.goodUnit.id from\n" +
"   PriceChange pc where (pc.repriceDoc = :rd))";


		//~: select the entries of the price change document
		List<Object[]> pcs = list(Object[].class, S, "rd", rd);

		//~: select current prices of the entries goods
		List<Object[]> gps = list(Object[].class, P,
		  "pl", rd.getPriceList(), "rd", rd);

		//~: map the prices
		HashMap<Long, GoodPrice> m = new HashMap<>(gps.size());

		for(Object[] o : gps)
			m.put((Long)o[0], (GoodPrice)o[1]);
		for(Object[] o : pcs)
			o[2] = m.get(((GoodUnit)o[1]).getPrimaryKey());

		return pcs;
	}

	/**
	 * Returns array of {@link GoodPrice}, {@link GoodUnit},
	 * {@link MeasureUnit}}, and Rest Cost {@link AggrValue}
	 * instances.
	 */
	public List            selectGoodPrices(PriceChangeEditModelBean mb, int limit)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom(
		  "GoodPrice gp join gp.goodUnit gu join gu.measure mu, Cost c"
		);

		qb.nameEntity("GoodPrice", GoodPrice.class);
		qb.nameEntity("Cost", AggrValue.class);

		//~: select clause
		qb.setClauseSelect("gp, gu, mu, c");

		//~: order by
		qb.setClauseOrderBy("gu.sortName");

		//~: the limits
		qb.setFirstRow(0);
		qb.setLimit(limit);


		//~: good >< cost  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "c.owner.id = gu.id"
		);

		//~: cost aggr type
		qb.getClauseWhere().addPart(
		  "c.aggrType = :aggrType"
		).
		  param("aggrType", Goods.aggrTypeRestCost());

		//~: price list
		EX.assertn(mb.getPriceChange().getPriceList());

		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :priceList"
		).
		  param("priceList", mb.getPriceChange().getPriceList());

		//~: restrict good units by code | name
		gusSearch(qb, mb.getSearchGoods());

		return QB(qb).list();
	}


	/* protected: shortage routines */

	protected String[] gusSearch()
	{
		return new String[] {
		  "gu.unity.oxSearch like :w"
		//"pl.unity.oxSearch like :w"
		};
	}

	protected void     restrictGoodsBySelSetAlsoPriceLists
	  (QueryBuilder qb, String selset, boolean folders)
	{
		//?: {has no selection set}
		if(selset == null) return;

		//~: create OR
		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

		//~: restrict the goods
		restrictGoodsBySelSet(p, selset, folders);

/*

 pl.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :sset) and (ss.login.id = :login))

 */

		//~: restrict the price lists
		p.addPart(

"pl.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"

		).
		  param("sset",  selset).
		  param("login", SecPoint.login());
	}
}