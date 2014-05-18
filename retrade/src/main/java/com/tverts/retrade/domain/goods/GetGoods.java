package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/* Spring Framework */

import com.tverts.retrade.domain.prices.PriceListsTreeModelBean;
import org.springframework.stereotype.Component;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models */

import com.tverts.model.DataSortModel;

/* com.tverts: endure (core, aggregation, trees) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeCross;
import com.tverts.endure.tree.TreeItem;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.TradeStoreModelBean;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceModelBean;
import com.tverts.retrade.domain.prices.PriceChange;
import com.tverts.retrade.domain.prices.PriceChangeEditModelBean;
import com.tverts.retrade.domain.prices.PriceList;
import com.tverts.retrade.domain.prices.PriceListModelBean;
import com.tverts.retrade.domain.prices.RepriceDoc;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;
import com.tverts.retrade.domain.prices.RepriceDocsModelBean;

/* com.tverts: support */

import static com.tverts.support.DU.cleanTime;
import static com.tverts.support.DU.lastTime;
import com.tverts.support.EX;
import static com.tverts.support.SU.s2s;


/**
 * Loads various objects related to the goods.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getGoods")
public class GetGoods extends GetObjectBase
{
	/* Get Good Units */

	public GoodUnit       getGoodUnit(Long pk)
	{
		return (pk == null)?(null):
		  (GoodUnit)session().get(GoodUnit.class, pk);
	}

	public GoodUnit       getGoodUnitStrict(Long pk)
	{
		EX.assertn(pk, "Good Unit primary key is undefined!");
		return (GoodUnit) EX.assertn(
		  session().get(GoodUnit.class, pk),
		  "Good Unit [", pk, "] is not found!"
		);
	}

	public GoodUnit       getGoodUnit(Long domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

// from GoodUnit gu where (gu.domain.id = :domain) and (gu.code = :code)

		return (GoodUnit) Q(

"from GoodUnit gu where (gu.domain.id = :domain) and (gu.code = :code)"

		).
		  setLong  ("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> getGoodUnits(Domain domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();


// from GoodUnit gu where (gu.domain = :domain)

		return (List<GoodUnit>) Q(
"  from GoodUnit gu where (gu.domain = :domain)"
		).
		  setParameter("domain", domain).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Long>     getGoodUnitsKeys(Domain domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();


/*

select gu.id from GoodUnit gu
  where (gu.domain = :domain)
order by gu.id

*/

		return (List<Long>) Q(

"select gu.id from GoodUnit gu\n" +
"  where (gu.domain = :domain)\n" +
"order by gu.id"

		).
		  setParameter("domain", domain).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> getSelectedGoodUnits(Long domain, String selset)
	{
/*

 from GoodUnit gu where (gu.domain.id = :domain) and
   gu.id in (select si.object from SelItem si join si.selSet ss
    where (ss.name = :set) and (ss.login.id = :login))

 */
		return (List<GoodUnit>) Q(

"from GoodUnit gu where (gu.domain.id = :domain) and\n" +
" gu.id in (select si.object from SelItem si join si.selSet ss\n" +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  setLong  ("domain", domain).
		  setString("set",    selset).
		  setLong  ("login",  SecPoint.login()).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> getTestGoodUnits(Domain domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();


/*

from GoodUnit gu where
  (gu.domain = :domain) and (gu.primaryKey < 0)

*/

		return (List<GoodUnit>) Q(

"from GoodUnit gu where\n" +
"  (gu.domain = :domain) and (gu.primaryKey < 0)"

		).
		  setParameter("domain", domain).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> searchGoodUnits(Domain domain, String[] words, int limit)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseSelect("gu");
		qb.nameEntity("GoodUnit", GoodUnit.class);
		qb.setClauseFrom("GoodUnit gu");

		//~: the selection limits
		qb.setLimit(limit);

		//~: order by
		qb.setClauseOrderBy("gu.nameLower");

		//~: domain restriction
		qb.getClauseWhere().
		  addPart("gu.domain = :domain").
		  param("domain", domain);

		//~: keywords search restrictions
		gusSearch(qb, words);

		return (List<GoodUnit>) QB(qb).list();
	}

	/**
	 * Returns list with arrays of:
	 *  0  Good Unit;
	 *  1  rest cost aggregated value;
	 *  2  aggregated volume in all stores (BigDecimal).
	 */
	@SuppressWarnings("unchecked")
	public List           selectGoodUnits(GoodsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Good", GoodUnit.class);
		qb.nameEntity("Cost", AggrValue.class);
		qb.nameEntity("Volume", AggrValue.class);
		qb.setClauseFrom("Good gu, Cost c");

		//~: select clause
		qb.setClauseSelect("gu, c, gu.id");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: good >< cost  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "c.owner.id = gu.id"
		);

		//~: cost aggr type
		qb.getClauseWhere().addPart(
		  "c.aggrType = :aggrType"
		).
		  param("aggrType", Goods.aggrTypeRestCost());

		//~: domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		//~: select the values
		List<Object[]> res = (List<Object[]>) QB(qb).list();

		//~: aggregate the volumes

/*

 select sum(v.aggrValue) from GoodUnit gu, AggrValue v
 where (gu.id = :good) and (v.selectorKey = gu.id) and
   (v.aggrType = :aggrType)

 */

		Query q = Q(

"select sum(v.aggrValue) from GoodUnit gu, AggrValue v\n" +
" where (gu.id = :good) and (v.selectorKey = gu.id) and\n" +
"   (v.aggrType = :aggrType)",

		  "aggrType", Goods.aggrTypeStoreVolume()
		);

		for(Object[] r : res)
		{
			q.setLong("good", (Long)r[2]);
			r[2] = (BigDecimal) q.uniqueResult();
		}

		return res;
	}

	public long           countGoodUnits(GoodsModelBean mb)
	{
		return countGoodUnits(
		  mb.domain(), mb.getSearchGoods(), mb.getSelSet());
	}

	/**
	 * Note that the result does not check whether
	 * the store of the model actually contains
	 * Good Store instances of the Good Units selected.
	 * The store is used to select the volumes.
	 *
	 * Returns list with arrays of:
	 *  0  the good unit instance;
	 *  1  summary volume of the good in the store.
	 */
	public List           selectGoodUnits(TradeStoreModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Good",  GoodUnit.class);
		qb.nameEntity("Volume", AggrValue.class);
		qb.setClauseFrom("Good gu, Volume v");

		//~: select clause
		qb.setClauseSelect("gu, v");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: good >< volume  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "(v.owner.id = :storeId) and (v.selectorKey = gu.id)"
		).
		  param("storeId", mb.getPrimaryKey());

		//~: volume aggr type
		qb.getClauseWhere().addPart(
		  "v.aggrType = :aggrType"
		).
		  param("aggrType", Goods.aggrTypeStoreVolume());


		//HINT: domain restriction is not needed here as
		// selector of the store implicitly defines it.

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		return QB(qb).list();
	}

	public long           countGoodUnits(TradeStoreModelBean  mb)
	{
		//HINT: we also do not restrict by the store
		// as each store has all Good Units related.

		return countGoodUnits(mb.domain(), mb.getSearchGoods(), null);
	}

	/**
	 * Selects the prices for the model Price List.
	 * Note that the result contains only the goods
	 * having a price, but not all the goods via left-join.
	 *
	 * Returns list with arrays of:
	 *  0  the Good Unit;
	 *  1  the Good Price.
	 */
	@SuppressWarnings("unchecked")
	public List           selectGoodPrices(PriceListModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Price", GoodPrice.class);
		qb.setClauseFrom("Price gp join gp.goodUnit gu");

		//~: select clause
		qb.setClauseSelect("gu, gp");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the price list
		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :list"
		).
		  param("list", mb.getObjectKey());

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		return QB(qb).list();
	}

	public long           countGoodPrices(PriceListModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Price", GoodPrice.class);
		qb.setClauseFrom("Price gp join gp.goodUnit gu");

		//~: select clause
		qb.setClauseSelect("count(gu.id)");

		//~: restrict the price list
		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :list"
		).
		  param("list", mb.getObjectKey());

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	/**
	 * @see {@link #selectGoodPrices(PriceListModelBean)}.
	 */
	@SuppressWarnings("unchecked")
	public List           selectGoodPrices(PriceListsTreeModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Price", GoodPrice.class);
		qb.setClauseFrom("Price gp join gp.goodUnit gu");

		//~: select clause
		qb.setClauseSelect("gu, gp");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//!: price list must be defined
		EX.assertn(mb.getCurrentList());

		//~: restrict the price list
		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :list"
		).
		  param("list", mb.getCurrentList());

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: restrict by the selection set
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		return QB(qb).list();
	}

	public long           countGoodPrices(PriceListsTreeModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Price", GoodPrice.class);
		qb.setClauseFrom("Price gp join gp.goodUnit gu");

		//~: select clause
		qb.setClauseSelect("count(gp.id)");


		//!: price list must be defined
		EX.assertn(mb.getCurrentList());

		//~: restrict the price list
		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :list"
		).
		  param("list", mb.getCurrentList());

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: restrict by the selection set
		restrictGoodsBySelSet(qb, mb.getSelSet(), true);

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> selectGoodUnits(GoodsTreeModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Good", GoodUnit.class);
		qb.setClauseFrom("Good gu");

		//~: select clause
		qb.setClauseSelect("gu");

		//~: order by
		orderGoods(qb, mb);

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//?: {has current folder selected}
		if(mb.getCurrentFolder() != null)
		{
			final String WOUT =

"gu.id in (select ti.item.id from TreeItem ti where (ti.folder.id = :folder))";

			final String WITH =

"gu.id in (select c.item.item.id from TreeCross c where (c.folder.id = :folder))";

			qb.nameEntity("TreeItem",  TreeItem.class);
			qb.nameEntity("TreeCross", TreeCross.class);

			//~: restrict by the folder
			qb.getClauseWhere().addPart(
			  mb.isWithSubFolders()?(WITH):(WOUT)
			).
			  param("folder", mb.getCurrentFolder());
		}
		//?: {without sub-folders on the root} show items not included in the tree
		else if(!mb.isWithSubFolders())
		{
			qb.nameEntity("TreeItem",  TreeItem.class);

			final String XOUT =

"gu.id not in (select ti.item.id from TreeItem ti where (ti.folder.domain = :tree))";

			qb.getClauseWhere().addPart(XOUT).
			  param("tree", bean(GetTree.class).getDomain(
			    mb.domain(), Goods.TYPE_GOODS_TREE
			  ));
		}

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), false);

		return QB(qb).list();
	}

	public long           countGoodUnits(GoodsTreeModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Good", GoodUnit.class);
		qb.setClauseFrom("Good gu");

		//~: select clause
		qb.setClauseSelect("count(gu.id)");


		//~: domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//?: {has current folder selected}
		if(mb.getCurrentFolder() != null)
		{
			final String WOUT =

"gu.id in (select ti.item.id from TreeItem ti where (ti.folder.id = :folder))";

			final String WITH =

"gu.id in (select c.item.item.id from TreeCross c where (c.folder.id = :folder))";

			qb.nameEntity("TreeItem",  TreeItem.class);
			qb.nameEntity("TreeCross", TreeCross.class);

			//~: restrict by the folder
			qb.getClauseWhere().addPart(
			  mb.isWithSubFolders()?(WITH):(WOUT)
			).
			  param("folder", mb.getCurrentFolder());
		}
		//?: {without sub-folders on the root} show items not included in the tree
		else if(!mb.isWithSubFolders())
		{
			qb.nameEntity("TreeItem",  TreeItem.class);

			final String XOUT =

"gu.id not in (select ti.item.id from TreeItem ti where (ti.folder.domain = :tree))";

			qb.getClauseWhere().addPart(XOUT).
			  param("tree", bean(GetTree.class).getDomain(
			    mb.domain(), Goods.TYPE_GOODS_TREE
			  ));
		}

		//~: keywords search restrictions
		gusSearch(qb, mb.getSearchGoods());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), false);

		return ((Number) QB(qb).uniqueResult()).longValue();
	}


	/* Get Aggregated Values */

	public AggrValue      getGoodUnitRestCost(GoodUnit gu)
	{
		return (gu == null)?(null):
		  getGoodUnitRestCost(gu.getPrimaryKey());
	}

	public AggrValue      getGoodUnitRestCost(Long gu)
	{

// from AggrValue where (owner.id = :gu) and (aggrType = :at)


		return (AggrValue) Q(

"from AggrValue where (owner.id = :gu) and (aggrType = :at)"

		).
		  setLong     ("gu",  gu).
		  setParameter("at",  Goods.aggrTypeRestCost()).
		  uniqueResult();
	}


	/* Get Measure Units */

	public MeasureUnit       getMeasureUnit(Long pk)
	{
		return (MeasureUnit) session().
		  get(MeasureUnit.class, pk);
	}

	public MeasureUnit       getMeasureUnit(Long domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

/*

from MeasureUnit mu where
  (mu.domain.id = :domain) and (mu.code = :code)

*/

		return (MeasureUnit) Q(

"from MeasureUnit mu where\n" +
"  (mu.domain.id = :domain) and (mu.code = :code)"

		).
		  setLong  ("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<MeasureUnit> getMeasureUnits(Long domain)
	{

// from MeasureUnit where domain.id = :domain order by lower(code)


		return (List<MeasureUnit>) Q(

"  from MeasureUnit where domain.id = :domain order by lower(code)"

		).
		  setLong("domain", domain).
		  list();
	}


	/* Get Price Lists */

	public PriceList       getPriceList(Long pk)
	{
		return (pk == null)?(null):
		  (PriceList)session().get(PriceList.class, pk);
	}

	public PriceList       getPriceList(Long domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

/*

 from PriceList pl where
   (pl.domain.id = :domain) and (pl.code = :code)

*/

		return (PriceList) Q(

"from PriceList pl where\n" +
"  (pl.domain.id = :domain) and (pl.code = :code)"

		).
		  setLong  ("domain", domain).
		  setString("code",   code).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PriceList> getPriceLists(Long domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();

/*

from PriceList pl where (pl.domain.id = :domain)
order by lower(pl.name)

*/
		return (List<PriceList>) Q(

"from PriceList pl where (pl.domain.id = :domain)\n" +
"order by lower(pl.name)"

		).
		  setLong("domain", domain).
		  list();
	}

	public PriceList       getPriceListDefault(Long domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();

/*

from PriceList pl where (pl.domain.id = :domain)
order by lower(pl.name)

*/
		return (PriceList) Q(

"from PriceList pl where (pl.domain.id = :domain)\n" +
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
		if(priceList == null)
			throw new IllegalArgumentException();
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
	  (PriceList priceList, Collection<String> goodCodes)
	{
		if(priceList == null)
			throw new IllegalArgumentException();
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
		HashMap<Long, BigDecimal> m =
		  new HashMap<Long, BigDecimal>(gs.size());

		for(Object[] o : ps)
			m.put((Long)o[0], (BigDecimal)o[1]);
		for(Object[] o : gs)
			o[2] = m.get(((GoodUnit)o[0]).getPrimaryKey());

		return gs;
	}

	@SuppressWarnings("unchecked")
	public List<GoodPrice> getPriceListPrices(PriceList priceList)
	{
		if(priceList == null)
			throw new IllegalArgumentException();


/*

from GoodPrice gp where (gp.priceList = :priceList)
order by gp.goodUnit.id

*/

		return Q(

"from GoodPrice gp where (gp.priceList = :priceList)\n" +
"order by gp.goodUnit.id"

		).
		  setParameter("priceList", priceList).
		  list();
	}

	public GoodPrice       getGoodPrice(Long pk)
	{
		return (GoodPrice) session().get(GoodPrice.class, pk);
	}

	public GoodPrice       getGoodPrice(PriceList pl, GoodUnit gu)
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


	/* Get Price Changes (prices history) */

	public RepriceDoc      getRepriceDoc(Long pk)
	{
		return (pk == null)?(null):
		  (RepriceDoc) session().get(RepriceDoc.class, pk);
	}

	public RepriceDoc      getRepriceDoc(Long domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

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
		//~: select the entries of the price change document
		List<Object[]> pcs = (List<Object[]>) Q(

"select pc, gu, 1, mu from\n" +
"  PriceChange pc join pc.goodUnit gu join gu.measure mu\n" +
"where (pc.repriceDoc = :rd)\n" +
"order by pc.docIndex"

		).
		  setParameter("rd", rd).
		  list();


		//~: select current prices of the entries goods
		List<Object[]> gps = (List<Object[]>) Q(

"select gu.id, gp from GoodPrice gp join gp.goodUnit gu where\n" +
" (gp.priceList = :pl) and gu.id in (select pc.goodUnit.id from\n" +
"   PriceChange pc where (pc.repriceDoc = :rd))"

		).
		  setParameter("pl", rd.getPriceList()).
		  setParameter("rd", rd).
		  list();


		//~: map the prices
		HashMap<Long, GoodPrice> m =
		  new HashMap<Long, GoodPrice>(gps.size());

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
		qb.setClauseOrderBy("gu.nameLower");

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
		if(mb.getPriceChange().getPriceList() == null)
			throw new IllegalStateException();

		qb.getClauseWhere().addPart(
		  "gp.priceList.id = :priceList"
		).
		  param("priceList", mb.getPriceChange().getPriceList());

		//~: restrict good units by code | name
		gusSearch(qb, mb.getSearchGoods());

		return QB(qb).list();
	}


	/* Get Good Calculations */

	public GoodCalc        getGoodCalc(Long pk)
	{
		return get(GoodCalc.class, pk);
	}

	/**
	 * Returns the Calculation of the Good Unit
	 * effective at the time given.
	 */
	public GoodCalc        getGoodCalc(Long goodUnit, Date time)
	{
/*

 from GoodCalc where (goodUnit.id = :goodUnit) and
   (openTime <= :time)
 order by openTime desc

 */
		return (GoodCalc) Q(

"from GoodCalc where (goodUnit.id = :goodUnit) and\n" +
"  (openTime <= :time)\n" +
"order by openTime desc",

		  "goodUnit", goodUnit,
		  "time",     time
		).
		  setMaxResults(1).
		  uniqueResult();
	}

	public List<GoodCalc>  getGoodCalcsBySuperGood(Long superGood)
	{

// from GoodCalc where superGood.id = :superGood

		return list(GoodCalc.class,

"from GoodCalc where superGood.id = :superGood",

		  "superGood", superGood
		);
	}

	public List<GoodCalc>  getGoodCalcs(Long goodUnit)
	{
/*

 from GoodCalc c where (c.goodUnit.id = :goodUnit)
 order by c.openTime

 */
		return list(GoodCalc.class,

"from GoodCalc c where (c.goodUnit.id = :goodUnit)\n" +
" order by c.openTime",

		  "goodUnit", goodUnit
		);
	}

	public List<GoodUnit>  getDerivedGoods(Long goodUnit)
	{

// from GoodUnit g where g.goodCalc.superGood.id = :goodUnit

		return list(GoodUnit.class,

		  "from GoodUnit g where g.goodCalc.superGood.id = :goodUnit",

		  "goodUnit", goodUnit
		);

	}

	public GoodCalc        buildCalc(GoodCalcView v)
	{
		GoodCalc c = new GoodCalc();

		//~: good unit
		if(v.getGoodUnit() != null)
			c.setGoodUnit(getGoodUnitStrict(v.getGoodUnit()));

		//~: open time
		c.setOpenTime(v.getOpenTime());

		//~: semi-ready
		c.setSemiReady(v.isSemiReady());

		//~: remarks
		c.setRemarks(v.getRemarks());

		//?: derived good
		if(v.isDerived())
		{
			//~: sub-code
			c.setSubCode(v.getSubCode());

			//~: sub-volume
			c.setSubVolume(v.getSubVolume());

			//~: super good
			c.setSuperGood(getGoodUnit(v.getSuperGood()));
		}

		//c: build the parts
		for(CalcPartView p : v.getParts())
		{
			CalcPart x = new CalcPart();

			//~: good calc
			x.setGoodCalc(c);
			c.getParts().add(x);

			//~: good unit
			x.setGoodUnit(getGoodUnitStrict(p.getGoodUnit()));

			//~: volume
			x.setVolume(p.getVolume());

			//~: semi-ready
			x.setSemiReady(p.getSemiReady());
		}

		return c;
	}


	/* private: shortage routines */

	private long countGoodUnits(Long domain, String[] search, String selset)
	{
		QueryBuilder qb = new QueryBuilder();

		//HINT: as AggrValues are 1-to-1 strict,
		//  we do not (implicit) join with it.

		//~: from clause
		qb.nameEntity("Good", GoodUnit.class);
		qb.setClauseFrom("Good gu");

		//~: select clause
		qb.setClauseSelect("count(gu.id)");

		//~: domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", domain);

		//~: keywords search restrictions
		gusSearch(qb, search);

		//~: selection set search
		restrictGoodsBySelSet(qb, selset, true);

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	private void orderGoods(QueryBuilder qb, DataSortModel sm)
	{
		StringBuilder s = new StringBuilder(16);

		for(int i = 0;(i < sm.sortSize());i++)
		{
			String  p = sm.getSortProps()[i];
			boolean d = sm.getSortDesc()[i];

			if((s.length() != 0) && (s.charAt(s.length() - 1) != ','))
				s.append(',');

			if("goodCode".equals(p))
				s.append("lower(gu.code)");
			else if("goodName".equals(p))
				s.append("gu.nameLower");
			else
				continue;

			if(d) s.append(" desc");
		}

		if(s.length() == 0)
			qb.setClauseOrderBy("gu.nameLower");
		else
			qb.setClauseOrderBy(s.toString());
	}

	private void gusSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(gu.code) like :w").param("w", w);
			p.addPart("gu.nameLower   like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void restrictDates(QueryBuilder qb, GoodPriceModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "pc.changeTime <= :maxDate"
		).
		  param("maxDate", lastTime(mb.getMaxDate()));

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "pc.changeTime >= :minDate"
		).
		  param("minDate", cleanTime(mb.getMinDate()));

		//~: between
		else qb.getClauseWhere().addPart(
		  "pc.changeTime between :minDate and :maxDate"
		).
		  param("minDate", cleanTime(mb.getMinDate())).
		  param("maxDate", lastTime(mb.getMaxDate()));
	}

	private void restrictDates(QueryBuilder qb, RepriceDocsModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "rd.changeTime <= :maxDate"
		).
		  param("maxDate", lastTime(mb.getMaxDate()));

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "rd.changeTime >= :minDate"
		).
		  param("minDate", cleanTime(mb.getMinDate()));

		//~: between
		else qb.getClauseWhere().addPart(
		  "rd.changeTime between :minDate and :maxDate"
		).
		  param("minDate", cleanTime(mb.getMinDate())).
		  param("maxDate", lastTime(mb.getMaxDate()));
	}

	private void restrictGoodsBySelSet
	  (QueryBuilder qb, String selset, boolean folders)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 gu.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 gu.id in (select ti.item.id from TreeCross tc join tc.item ti
   where tc.folder.id in (select si.object from
     SelItem si join si.selSet ss where
       (ss.name = :set) and (ss.login.id = :login)))

*/

		//~: search goods directly
		p.addPart(

"gu.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set", selset).
		  param("login", SecPoint.login());


		//~: search goods in folders
		if(folders) p.addPart(

"gu.id in (select ti.item.id from TreeCross tc join tc.item ti\n" +
"  where tc.folder.id in (select si.object from\n" +
"    SelItem si join si.selSet ss where\n" +
"      (ss.name = :set) and (ss.login.id = :login)))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}
}