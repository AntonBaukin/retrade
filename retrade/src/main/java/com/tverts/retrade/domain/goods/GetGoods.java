package com.tverts.retrade.domain.goods;

/* Java */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Spring Framework */

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
import com.tverts.hibery.qb.WhereText;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: models */

import com.tverts.model.DataSearchModel;
import com.tverts.model.DataSelectModel;
import com.tverts.model.DataSelectModelBean;
import com.tverts.model.DataSortModel;
import com.tverts.model.ModelBeanBase;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: endure (core, aggregation, trees) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.tree.GetTree;
import com.tverts.endure.tree.TreeCross;
import com.tverts.endure.tree.TreeItem;

/* com.tverts: retrade domain (invoices, prices, selections, stores) */

import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.MoveGood;
import com.tverts.retrade.domain.invoice.SellGood;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceModelBean;
import com.tverts.retrade.domain.prices.PriceListModelBean;
import com.tverts.retrade.domain.prices.RepriceDocsModelBean;
import com.tverts.retrade.domain.selset.GetSelSet;
import com.tverts.retrade.domain.store.TradeStoreModelBean;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Loads various objects related to the goods.
 *
 * @author anton.baukin@gmail.com
 */
@Component
public class GetGoods extends GetObjectBase
{
	/* Get Good Units */

	public GoodUnit       getGoodUnit(Long pk)
	{
		return get(GoodUnit.class, pk);
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
/*

 Remark: sub-good has the same code as it's owner.

 from GoodUnit gu where (gu.domain.id = :domain) and
   (gu.code = :code) and (gu.superGood is null)

 */
		final String Q =

"from GoodUnit gu where (gu.domain.id = :domain) and " +
"  (gu.code = :code) and (gu.superGood is null)";

		EX.assertn(domain);
		EX.asserts(code);
		return object(GoodUnit.class, Q, "domain", domain, "code", code);
	}

	public GoodUnit       getSubGood(Long superGood, String measure)
	{
/*

 from GoodUnit gu where (gu.superGood.id = :superGood)
   and (gu.measure.code = :measure)

 */
		final String Q =
"from GoodUnit gu where (gu.superGood.id = :superGood)\n" +
"  and (gu.measure.code = :measure)";

		return object(GoodUnit.class, Q,
		  "superGood", superGood, "measure", measure);
	}

	/**
	 * Returns all sub-goods of the given owning good.
	 */
	public List<GoodUnit> getSubGoods(Long gu)
	{
		final String Q =
"  from GoodUnit gu where (gu.superGood.id = :gu)";

		EX.assertn(gu);
		return list(GoodUnit.class, Q, "gu", gu);
	}

	public List<GoodUnit> getGoodUnits(Domain domain)
	{
		final String Q =
"  from GoodUnit gu where (gu.domain = :domain)";

		EX.assertn(domain);
		return list(GoodUnit.class, Q, "domain", domain);
	}

	public List<GoodUnit> getSubGoods(Domain domain)
	{
		final String Q =
"  from GoodUnit gu where (gu.domain = :domain) and (gu.superGood is not null)";

		EX.assertn(domain);
		return list(GoodUnit.class, Q, "domain", domain);
	}

	public List<Long>     getGoodUnitsKeys(Domain domain)
	{
		EX.assertn(domain);

/*

select gu.id from GoodUnit gu
  where (gu.domain = :domain)
order by gu.id

*/
		final String Q =

"select gu.id from GoodUnit gu\n" +
"  where (gu.domain = :domain)\n" +
"order by gu.id";

		return list(Long.class, Q, "domain", domain);
	}

	/**
	 * Returns the Good Unit entities directly selected.
	 */
	public List<GoodUnit> getSelectedGoodUnits(Long domain, String selset)
	{
/*

 from GoodUnit gu where (gu.domain.id = :domain) and
   gu.id in (select si.object from SelItem si join si.selSet ss
     where (ss.name = :selset) and (ss.login.id = :login))

 */
		final String Q =

"from GoodUnit gu where (gu.domain.id = :domain) and\n" +
"  gu.id in (select si.object from SelItem si join si.selSet ss\n" +
"    where (ss.name = :selset) and (ss.login.id = :login))";

		return list(GoodUnit.class, Q,
		  "domain", domain, "selset", selset,
		  "login",  SecPoint.login()
		);
	}

	@SuppressWarnings("unchecked")
	public List<Long>     getSelectedGoodsKeys(String selset)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("GoodUnit", GoodUnit.class);
		qb.setClauseFrom("GoodUnit gu");

		//~: select clause
		qb.setClauseSelect("gu.id");


		//sec: domain restriction
		qb.getClauseWhere().
		  addPart("gu.domain.id = :domain").
		  param("domain", SecPoint.domain());

		//!: restrict by the selection set
		restrictGoodsBySelSet(qb, selset, "all");

		return (List<Long>) QB(qb).list();
	}

	@SuppressWarnings("unchecked")
	public List<GoodUnit> searchGoodUnits(DataSelectModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("GoodUnit", GoodUnit.class);
		qb.setClauseFrom("GoodUnit gu");

		//~: select clause
		qb.setClauseSelect("gu");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: order by
		orderGoods(qb, mb);

		//~: domain restriction
		qb.getClauseWhere().
		  addPart("gu.domain.id = :domain").
		  param("domain", mb.domain());

		//~: general restriction
		restrictGoodsGeneral(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

		return (List<GoodUnit>) QB(qb).list();
	}

	public int            countGoodUnits(DataSelectModelBean mb)
	{
		return doCountGoodUnits(mb);
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
		if(!mb.isAggrValues()) //?: {select in a plain}
		{
			List res = this.searchGoodUnits(mb);

			for(int i = 0;(i < res.size());i++)
			{
				Object[] r = new Object[3];
				r[0] =  res.get(i);
				res.set(i, r);
			}

			return res;
		}

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

		//~: general restriction
		restrictGoodsGeneral(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

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
			r[2] = q.uniqueResult();
		}

		return res;
	}

	public int            countGoodUnits(GoodsModelBean mb)
	{
		return doCountGoodUnits(mb);
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
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

		return QB(qb).list();
	}

	public int            countGoodUnits(TradeStoreModelBean  mb)
	{
		//HINT: we also do not restrict by the store
		// as each store has all Good Units related.

		return doCountGoodUnits(mb);
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

		//~: general restriction
		restrictGoodsGeneral(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

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

		//~: general restriction
		restrictGoodsGeneral(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

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


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict the tree folder
		restrictTreeGoods(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "goods", "folders");

		return QB(qb).list();
	}

	public int            countGoodUnits(GoodsTreeModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Good", GoodUnit.class);
		qb.setClauseFrom("Good gu");

		//~: select clause
		qb.setClauseSelect("count(gu.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "gu.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict the tree folder
		restrictTreeGoods(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "goods", "folders");

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * Checks whether this good is referred by distinct
	 * entities of the domain: sub-Goods (flag 1),
	 * Calculations (flag 2), Prices (flag 4),
	 * Invoices (Buy 8, Sell or Sells 16, Move 32,
	 * Volume Check 64, Resulting Good 128).
	 *
	 * Parameter 'stop' orders not to check all the items.
	 */
	public int            isGoodUsed(Long pk, boolean stop)
	{
		EX.assertn(pk);

/*

 select id from GoodUnit where (superGood.id = :pk)

 select id from GoodCalc where (superGood.id = :pk)

 select id from CalcPart where (goodUnit.id = :pk)

 select id from GoodPrice where (goodUnit.id = :pk)

 select id from BuyGood where (goodUnit.id = :pk)

 select id from MoveGood where (goodUnit.id = :pk)

 select id from SellGood where (goodUnit.id = :pk)

 select id from VolGood where (goodUnit.id = :pk)

 select id from ResGood where (goodUnit.id = :pk)

 */
		final Object[] Qs = new Object[]
		{
		  "select id from GoodUnit where (superGood.id = :pk)",  1,

		  "select id from GoodCalc where (superGood.id = :pk)",  2,

		  "select id from CalcPart where (goodUnit.id = :pk)",   2,

		  "select id from GoodPrice where (goodUnit.id = :pk)",  4,

		  "select id from BuyGood where (goodUnit.id = :pk)",    8,

		  "select id from MoveGood where (goodUnit.id = :pk)",   32,

		  "select id from SellGood where (goodUnit.id = :pk)",   16,

		  "select id from VolGood where (goodUnit.id = :pk)",    64,

		  "select id from ResGood where (goodUnit.id = :pk)",    128
		};

		int result = 0;

		//c: for all the queries
		for(int i = 0;(i < Qs.length);i += 2)
		{
			String  Q = (String)  Qs[i];
			Integer F = (Integer) Qs[i+1];

			if(first(Object.class, Q, "pk", pk) != null)
				if(stop)
					return F;
				else
					result |= F;
		}

		return result;
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
		return get(MeasureUnit.class, pk);
	}

	public MeasureUnit       getMeasureUnit(Long domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

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
		GoodCalc gc = new GoodCalc();
		Calc      c = gc.getOx();

		//~: good unit
		if(v.getGoodUnit() != null)
			gc.setGoodUnit(getGoodUnitStrict(v.getGoodUnit()));

		//~: open time
		c.setTime(v.getOpenTime());

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
			gc.setSuperGood(getGoodUnit(v.getSuperGood()));
		}

		//c: build the parts
		for(CalcPartView p : v.getParts())
		{
			CalcPart x = new CalcPart();

			//~: good calc
			x.setGoodCalc(gc);
			gc.getParts().add(x);

			//~: good unit
			x.setGoodUnit(getGoodUnitStrict(p.getGoodUnit()));

			//~: volume
			x.setVolume(p.getVolume());

			//~: semi-ready
			x.setSemiReady(p.getSemiReady());
		}

		//!: update ox
		gc.updateOx();

		return gc;
	}


	/* Get Good Attributes */

	public GoodAttr        getAttrType(Long domain, String code)
	{
		AttrType at = bean(GetUnity.class).getAttrType(
		  domain, Goods.typeGoodAttr().getPrimaryKey(), code);

		return (at == null)?(null):(GoodAttr)at.getOx();
	}

	public String          getAttrString(GoodUnit gu, String attr)
	{
		EX.assertn(gu);
		EX.asserts(attr);

/*

 select ua.string from UnityAttr ua where (ua.unity.id = :gu) and
   (ua.attrType.domain = :domain) and (ua.attrType.attrType = :type)
   and (ua.attrType.name = :attr)

*/

		final String Q =

"select ua.string from UnityAttr ua where (ua.unity.id = :gu) and\n" +
"  (ua.attrType.domain = :domain) and (ua.attrType.attrType = :type)\n" +
"  and (ua.attrType.name = :attr)";

		return object(String.class, Q, "gu", gu.getPrimaryKey(),
		  "domain", gu.getDomain(), "type", Goods.typeGoodAttr(), "attr", attr);
	}

	public List<String>    getAttrStrings(Long domain, String attr)
	{
		EX.assertn(domain);
		EX.asserts(attr);

/*

 select distinct ua.string from UnityAttr ua where
   (ua.attrType.domain.id = :domain) and (ua.attrType.attrType = :type)
   and (ua.attrType.name = :attr)

*/

		final String Q =

"select distinct ua.string from UnityAttr ua where\n" +
"  (ua.attrType.domain.id = :domain) and (ua.attrType.attrType = :type)\n" +
"  and (ua.attrType.name = :attr)";

		return list(String.class, Q, "domain", domain,
		  "type", Goods.typeGoodAttr(), "attr", attr);
	}


	/* Goods Selections */

	/**
	 * Assuming the GoodUnit has alias 'gu', builds the list
	 * of restrictions adding them to AND or OR composite.
	 * Selection set must be defined (the default is "").
	 * What-list enumerates what restrictions to add:
	 *
	 * · all       all the options below;
	 * · goods     direct goods;
	 * · folders   goods from the tree folders (deeply);
	 * · prices    goods from the price lists;
	 * · buys      buy invoices;
	 * · sells     sell and sells invoices;
	 * · moves     move invoices;
	 * · reprices  price change documents.
	 */
	public void restrictGoodsBySelSet(WherePartLogic or, String selset, String... what)
	{
		HashSet<String> w = new HashSet<>(Arrays.asList(what));
		if(w.contains("all")) w.addAll(Arrays.asList(SU.s2a(
		  "goods folders prices buys sells moves reprices"
		)));

/* --> goods

 gu.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :sset) and (ss.login.id = :login))

 */

		if(w.contains("goods")) or.addPart(

"gu.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"

		).
		  param("sset", selset).
		  param("login", SecPoint.login());


/* --> folders

 gu.id in (select ti.item.id from TreeCross tc join tc.item ti
   where tc.folder.id in (select si.object from
     SelItem si join si.selSet ss where
       (ss.name = :sset) and (ss.login.id = :login)))

 */

		if(w.contains("folders")) or.addPart(

"gu.id in (select ti.item.id from TreeCross tc join tc.item ti\n" +
"  where tc.folder.id in (select si.object from\n" +
"    SelItem si join si.selSet ss where\n" +
"      (ss.name = :sset) and (ss.login.id = :login)))"

		).
		  param("sset",  selset).
		  param("login", SecPoint.login());


/* --> prices

 gu.id in (select gpx.goodUnit.id from GoodPrice gpx where
   gpx.priceList.id in (select si.object from SelItem si join si.selSet ss
     where (ss.name = :sset) and (ss.login.id = :login)))

 */

		if(w.contains("prices")) or.addPart(

"gu.id in (select gpx.goodUnit.id from GoodPrice gpx where\n" +
"  gpx.priceList.id in (select si.object from SelItem si join si.selSet ss\n" +
"    where (ss.name = :sset) and (ss.login.id = :login)))"

		).
		  param("sset", selset).
		  param("login", SecPoint.login());


/* --> buy, sell, move invoices

 gu.id in (select ig.goodUnit.id from InvGood ig join ig.data d
   where d.invoice.id in (select si.object from SelItem si join si.selSet ss
     where (ss.name = :sset) and (ss.login.id = :login)))

 */

		final String IG =

"gu.id in (select ig.goodUnit.id from InvGood ig join ig.data d" +
"  where d.invoice.id in (select si.object from SelItem si join si.selSet ss" +
"    where (ss.name = :sset) and (ss.login.id = :login)))";


// --> buy invoices

		if(w.contains("buys")) or.addPart(
		  IG.replace("InvGood", BuyGood.class.getName())
		).
		  param("sset", selset).
		  param("login", SecPoint.login());


// --> sell, sells invoices

		if(w.contains("sells")) or.addPart(
		  IG.replace("InvGood", SellGood.class.getName())
		).
		  param("sset", selset).
		  param("login", SecPoint.login());


// --> move invoices

		if(w.contains("move")) or.addPart(
		  IG.replace("InvGood", MoveGood.class.getName())
		).
		  param("sset", selset).
		  param("login", SecPoint.login());


/* --> price change documents

 gu.id in (select pc.goodUnit.id from PriceChange pc where
   pc.repriceDoc.id in (select si.object from SelItem si join si.selSet ss
     where (ss.name = :sset) and (ss.login.id = :login)))

 */

		if(w.contains("reprices")) or.addPart(

"gu.id in (select pc.goodUnit.id from PriceChange pc where" +
"  pc.repriceDoc.id in (select si.object from SelItem si join si.selSet ss" +
"    where (ss.name = :sset) and (ss.login.id = :login)))"

		).
		  param("sset", selset).
		  param("login", SecPoint.login());
	}

	public void restrictGoodsByFilters(
	  WherePartLogic or, WherePartLogic and, String selset)
	{
		//~: select current filters
		Set<GoodsFilter> filters = new HashSet<>(bean(GetSelSet.class).
		  getTypedItems(selset, GoodsFilter.class, true));

/*

 exists (from UnityAttr ua where (ua.unity.id = gu.id) and
   (ua.attrType.id = :attrType) and (ua.XYZ))

 */
		final String Q =

"exists (from UnityAttr ua where (ua.unity.id = gu.id) and\n" +
"  (ua.attrType.id = :attrType) and (ua.XYZ))";

		//c: for each filter
		for(GoodsFilter f : filters)
		{
			String    q = Q;
			Object    m = (f.getMin() == null)?(null):(f.getMin().value());
			Object    M = (f.getMax() == null)?(null):(f.getMax().value());
			Object    t = (m != null)?(m):(M);
			WhereText w = null;

			//?: {has no attribute type}
			if(f.getAttrType() == null) continue;

			//?: {is string type}
			if(t instanceof String)
			{
				String op = "string = :m";

				//?: {substring}
				if("substring".equals(f.getOp()))
					op = "string like '%' || :m || '%'";

				q = q.replace("XYZ", op);
				w = new WhereText(q);
				w.param("m", m);
			}

			//?: {is integer or decimal}
			if((t instanceof Long) || (t instanceof BigDecimal))
			{
				String  field  = (t instanceof Long)?("integer "):("number ");
				boolean ranged = (f.getMin() != null) && (f.getMax() != null);

				//?: {between}
				if((m != null) && (M != null))
				{
					q = q.replace("XYZ", field + "between :m and :M");
					w = new WhereText(q);
					w.param("m", m);
					w.param("M", M);
				}
				//?: {greater}
				else if(m != null)
				{
					q = q.replace("XYZ", field + ((ranged)?(">= :m"):("= :m")));
					w = new WhereText(q);
					w.param("m", m);
				}
				//?: {smaller}
				else if(M != null)
				{
					q = q.replace("XYZ", field + ((ranged)?("<= :M"):("= :M")));
					w = new WhereText(q);
					w.param("M", M);
				}
			}

			//?: {has no restriction}
			if(w == null) continue;

			//=: atribute type
			w.param("attrType", f.getAttrType());

			//~: add to destination
			if(f.isOr())
				or.addPart(w);
			else
				and.addPart(w);
		}
	}


	/* protected: shortage routines */

	protected <T extends ModelBeanBase & DataSelectModel & DataSearchModel> int
	                   doCountGoodUnits(T mb)
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
		  param("domain", mb.domain());

		//~: general restriction
		if(mb instanceof DataSelectModelBean)
			restrictGoodsGeneral(qb, mb);

		//~: keywords search restrictions
		gusSearch(qb, mb.searchNames());

		//~: selection set search
		restrictGoodsBySelSet(qb, mb.getSelSet(), "all");

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	protected void     orderGoods(QueryBuilder qb, DataSortModel sm)
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
				s.append("gu.sortName");
			else
				continue;

			if(d) s.append(" desc");
		}

		if(s.length() == 0)
			qb.setClauseOrderBy("gu.sortName");
		else
			qb.setClauseOrderBy(s.toString());
	}

	protected void     gusSearch(QueryBuilder qb, String[] words)
	{
		String[] gus = gusSearch();
		EX.asserte(gus);

		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			//~: create OR
			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
			qb.getClauseWhere().addPart(p);

			//~: collect the restrictions
			for(String x : gus)
				p.addPart(x).param("w", w);
		}
	}

	protected String[] gusSearch()
	{
		return new String[] { "gu.unity.oxSearch like :w" };
	}

	protected void     restrictDates(QueryBuilder qb, GoodPriceModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "pc.changeTime <= :maxDate"
		).
		  param("maxDate", DU.lastTime(mb.getMaxDate()));

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "pc.changeTime >= :minDate"
		).
		  param("minDate", DU.cleanTime(mb.getMinDate()));

		//~: between
		else qb.getClauseWhere().addPart(
		  "pc.changeTime between :minDate and :maxDate"
		).
		  param("minDate", DU.cleanTime(mb.getMinDate())).
		  param("maxDate", DU.lastTime(mb.getMaxDate()));
	}

	protected void     restrictDates(QueryBuilder qb, RepriceDocsModelBean mb)
	{
		if((mb.getMinDate() == null) && (mb.getMaxDate() == null))
			return;

		//~: <= max date
		if(mb.getMinDate() == null) qb.getClauseWhere().addPart(
		  "rd.changeTime <= :maxDate"
		).
		  param("maxDate", DU.lastTime(mb.getMaxDate()));

		//~: >= min date
		else if(mb.getMaxDate() == null) qb.getClauseWhere().addPart(
		  "rd.changeTime >= :minDate"
		).
		  param("minDate", DU.cleanTime(mb.getMinDate()));

		//~: between
		else qb.getClauseWhere().addPart(
		  "rd.changeTime between :minDate and :maxDate"
		).
		  param("minDate", DU.cleanTime(mb.getMinDate())).
		  param("maxDate", DU.lastTime(mb.getMaxDate()));
	}

	protected void     restrictTreeGoods(QueryBuilder qb, GoodsTreeModelBean mb)
	{
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

			qb.getClauseWhere().addPart(XOUT).param("tree",
			  bean(GetTree.class).getDomain(EX.assertn(mb.getTreeDomain())));
		}
	}

	protected void     restrictGoodsBySelSet
	  (QueryBuilder qb, String selset, String... what)
	{
		//?: {has no selection set}
		if(selset == null) return;

		//~: create OR
		WherePartLogic or = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(or);

		//~: restrict by the types
		restrictGoodsBySelSet(or, selset, what);

		//~: create and
		WherePartLogic and = new WherePartLogic().setOp(WhereLogic.AND);

		//~: restrict by the filters
		restrictGoodsByFilters(or, and, selset);

		//?: {has and filters}
		if(!and.isEmpty())
			qb.getClauseWhere().addPart(and);
	}

	protected void     restrictGoodsGeneral(QueryBuilder qb, DataSelectModel mb)
	{
		if(Boolean.TRUE.equals(mb.getRestriction("goods.buy")))
			qb.getClauseWhere().addPart("gu.visibleBuy = true");

		if(Boolean.TRUE.equals(mb.getRestriction("goods.sell")))
			qb.getClauseWhere().addPart("gu.visibleSell = true");

		if(Boolean.TRUE.equals(mb.getRestriction("goods.lists")))
			qb.getClauseWhere().addPart("gu.visibleLists = true");

		if(Boolean.TRUE.equals(mb.getRestriction("goods.reports")))
			qb.getClauseWhere().addPart("gu.visibleReports = true");

		//~: restrict by the goods folder
		Long folder = (Long) mb.getRestriction("goods-folder");
		if(folder != null)
		{
			final String Q =
"gu.id in (select ti.item.id from TreeItem ti where (ti.folder.id = :folder))";

			qb.nameEntity("TreeCross", TreeCross.class);

			//~: restrict by the folder
			qb.getClauseWhere().addPart(Q).
			  param("folder", folder);
		}
	}
}