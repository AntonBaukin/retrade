package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Spring Framework */

import com.tverts.retrade.domain.goods.CalcPart;
import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.ActGoodCalc.CycliCalcError;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.misc.Pair;


/**
 * Requesting and processing of {@link Invoice} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getInvoice")
public class GetInvoice extends GetObjectBase
{
	/* Get Invoice */

	public Invoice  getInvoice(Long primaryKey)
	{
		return (primaryKey == null)?(null):
		  (Invoice)session().get(Invoice.class, primaryKey);
	}

	public Object   getInvoiceEditInit(Long primaryKey)
	{
/*

select i, ib, c from InvoiceBill ib right outer join ib.invoice i
  left outer join ib.contractor c where (i.id = :primaryKey)

*/
		return Q(

"select i, ib, c from InvoiceBill ib right outer join ib.invoice i\n" +
"  left outer join ib.contractor c where (i.id = :primaryKey)"

		).
		  setLong("primaryKey", primaryKey).
		  uniqueResult();
	}

	public Long     getInvoiceKey(Long domain, String code)
	{
/*

 select i.id from Invoice i where
   (i.domain.id = :domain) and (i.code = :code)

 */
		return object(Long.class,

"select i.id from Invoice i where\n" +
"  (i.domain.id = :domain) and (i.code = :code)",

		   "domain", domain,
		   "code",   code
		);
	}


	/* Invoice ordering */

	/**
	 * Finds invoices in the given domain and the order type
	 * having smaller or equal timestamp as the given and
	 * the maximum order indices.
	 *
	 * Note that the order index is descending. The invoices
	 * are in reverse order!
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice> findLeftInvoices(
	  Domain d, UnityType orderType, Date ts, int limit, Set<Long> exclude)
	{

		if(exclude == null)
			exclude = Collections.singleton(Long.MAX_VALUE);

/*

from Invoice where (domain = :domain) and
  (orderType = :orderType) and (invoiceDate <= :timestamp)
  and (id not in :exclude)
order by orderIndex desc

*/
		return (List<Invoice>) Q(

"from Invoice where (domain = :domain) and\n" +
"  (orderType = :orderType) and (invoiceDate <= :timestamp)\n" +
"  and (id not in :exclude)\n" +
"order by orderIndex desc"

		).
		  setParameter("domain",      d).
		  setParameter("orderType",   orderType).
		  setParameter("timestamp",   ts).
		  setParameterList("exclude", exclude).
		  setMaxResults(limit).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> findLeftInvoicesByDate(
	  Domain d, UnityType invoiceType, Date ts, int limit, Set<Long> exclude)
	{

		if(exclude == null)
			exclude = Collections.singleton(Long.MAX_VALUE);

/*

from Invoice where (domain = :domain) and
  (invoiceType = :invoiceType) and (invoiceDate <= :timestamp)
  and (id not in :exclude)
order by invoiceDate desc

*/
		return (List<Invoice>) Q(

"from Invoice where (domain = :domain) and\n" +
"  (invoiceType = :invoiceType) and (invoiceDate <= :timestamp)\n" +
"  and (id not in :exclude)\n" +
"order by invoiceDate desc"

		).
		  setParameter("domain",      d).
		  setParameter("invoiceType", invoiceType).
		  setParameter("timestamp",   ts).
		  setParameterList("exclude", exclude).
		  setMaxResults(limit).
		  list();
	}

	/**
	 * Finds invoices in the given domain and the order type
	 * having greater (not equal!) timestamp as the given and
	 * the minimum order indices.
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice> findRightInvoices(
	  Domain d, UnityType orderType, Date ts, int limit, Set<Long> exclude)
	{

		if(exclude == null)
			exclude = Collections.singleton(Long.MAX_VALUE);

/*

from Invoice where (domain = :domain) and
  (orderType = :orderType) and (invoiceDate > :timestamp)
  and (id not in :exclude)
order by orderIndex asc

*/
		return (List<Invoice>) Q(

"from Invoice where (domain = :domain) and\n" +
"  (orderType = :orderType) and (invoiceDate > :timestamp)\n" +
"  and (id not in :exclude)\n" +
"order by orderIndex asc"

		).
		  setParameter("domain",      d).
		  setParameter("orderType",   orderType).
		  setParameter("timestamp",   ts).
		  setParameterList("exclude", exclude).
		  setMaxResults(limit).
		  list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> findRightInvoicesByDate(
	  Domain d, UnityType invoiceType, Date ts, int limit, Set<Long> exclude)
	{

		if(exclude == null)
			exclude = Collections.singleton(Long.MAX_VALUE);

/*

from Invoice where (domain = :domain) and
  (invoiceType = :invoiceType) and (invoiceDate > :timestamp)
  and (id not in :exclude)
order by invoiceDate asc

*/
		return (List<Invoice>) Q(

"from Invoice where (domain = :domain) and\n" +
"  (invoiceType = :invoiceType) and (invoiceDate > :timestamp)\n" +
"  and (id not in :exclude)\n" +
"order by invoiceDate asc"

		).
		  setParameter("domain",      d).
		  setParameter("invoiceType", invoiceType).
		  setParameter("timestamp",   ts).
		  setParameterList("exclude", exclude).
		  setMaxResults(limit).
		  list();
	}


	/* Goods Calculations */

	/**
	 * Returns the Calculation of the Good Unit
	 * effective at the time given.
	 */
	public GoodCalc         getGoodCalc(Long goodUnit, Date time)
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

	public List<ResGood>    calcInvoice(InvoiceData data)
	{
		EX.assertn( data.getInvoice().getInvoiceDate(),
		  "Invoice [", data.getInvoice().getPrimaryKey(), "] is not time set!"
		);

		if(data instanceof SellData)
			return doCalcInvoice(data, null);

		if(data instanceof MoveData)
			return doCalcInvoice((MoveData) data);

		return null;
	}

	protected List<ResGood> doCalcInvoice(MoveData data)
	{
		Set<InvGood> except = new HashSet<InvGood>(5);

		for(MoveGood g : data.getGoods())
			//?: {place-only operation}
			if(Boolean.TRUE.equals(g.getMoveOn()))
				except.add(g);

		return doCalcInvoice(data, except);
	}

	protected List<ResGood> doCalcInvoice(InvoiceData data, Set<InvGood> except)
	{
		EX.assertx(data.isAltered(), "Invoice [",
		  data.getInvoice().getPrimaryKey(), "] is not altered!");

		//~: invoice time
		Date time = data.getInvoice().getInvoiceDate();

		//~: (good unit, need calc == transitive) -> resulting good
		Map<Pair<GoodUnit, Boolean>, BigDecimal> res =
		  new LinkedHashMap<Pair<GoodUnit, Boolean>, BigDecimal>(11);

		//~: cache of the calculations
		Map<GoodUnit, GoodCalc> cache = new HashMap<GoodUnit, GoodCalc>(11);
		except = (except != null)?(except):(Collections. <InvGood>emptySet());

		//c: for all the goods to precess except...
		for(InvGood g : data.getGoods()) if(!except.contains(g))
		{
			Pair<GoodUnit, Boolean> k = new Pair<GoodUnit, Boolean>(
			  g.getGoodUnit(), true
			);

			GoodCalc calc = null;

			//?: {it is as-is without calculations}
			if(Boolean.FALSE.equals(((NeedCalcGood)g).getNeedCalc()))
				k.setValue(false);
			//~: lookup the calc
			else
			{
				calc = calcGetCached(cache, k.getKey(), time);
				if(calc == null) k.setValue(false);
			}

			//?: {no calculation} just add the volume
			if(Boolean.FALSE.equals(k.getValue()))
			{
				if(!res.containsKey(k)) res.put(k, g.getVolume());
				else res.put(k, res.get(k).add(g.getVolume()));
				continue;
			}

			//~: deep into the calculation
			calcDeeply(res, cache, new HashSet<GoodUnit>(5),
			  time, calc, g.getVolume(),
			  (((NeedCalcGood)g).getNeedCalc() == null)?(null):(false) //<-- null | false
			);
		}

		return calcResGoods(res, cache);
	}

	protected void          calcDeeply (
	    Map<Pair<GoodUnit, Boolean>, BigDecimal> res,
	    Map<GoodUnit, GoodCalc> cache, Set<GoodUnit> stack,
	    Date time, GoodCalc calc, BigDecimal v, Boolean stop
	  )
	{
		Pair<GoodUnit, Boolean> k;

		//?: {cyclic calculation} error
		if(stack.contains(calc.getGoodUnit()))
			throw new CycliCalcError(stack);

		//!: stack push
		stack.add(calc.getGoodUnit());

		//?: {has stop flag default} set as semi-ready
		if(stop == null)              //HINT: semi-ready products are
			stop = calc.isSemiReady(); //  not calculated by the default.

		//~: add the good volume (stop == not transitive)
		k = new Pair<GoodUnit, Boolean>(calc.getGoodUnit(), Boolean.FALSE.equals(stop));
		if(!res.containsKey(k)) res.put(k, v);
		else res.put(k, res.get(k).add(v));

		//?: {not stop at this point} take the parts
		if(Boolean.FALSE.equals(stop)) for(CalcPart p : calc.getParts())
		{
			//~: search for the good calc of this part
			GoodCalc pcalc = calcGetCached(cache, p.getGoodUnit(), time);

			//?: {this good is a product} deep into it
			if(pcalc != null)
				calcDeeply(res, cache, stack, time, pcalc,
				  v.multiply(p.getVolume()), p.getSemiReady());
			//~: add volume as-is
			else
			{
				//?: {cyclic calculation} error
				if(stack.contains(p.getGoodUnit()))
					throw new CycliCalcError(stack);

				k = new Pair<GoodUnit, Boolean>(p.getGoodUnit(), false);
				if(!res.containsKey(k)) res.put(k, v.multiply(p.getVolume()));
				else res.put(k, res.get(k).add(v.multiply(p.getVolume())));
			}
		}

		//!: stack pop
		stack.remove(calc.getGoodUnit());
	}

	protected GoodCalc      calcGetCached
	  (Map<GoodUnit, GoodCalc> cache, GoodUnit good, Date time)
	{
		//~: lookup in the cache
		GoodCalc res = cache.get(good);
		if(res != null) return res;

		//?: {good has no calc}
		if(cache.containsKey(good))
			return null;

		//~: search
		res = getGoodCalc(good.getPrimaryKey(), time);
		cache.put(good, res);

		return res;
	}

	protected List<ResGood> calcResGoods (
	    Map<Pair<GoodUnit, Boolean>, BigDecimal> map,
	    Map<GoodUnit, GoodCalc> cache
	  )
	{
		List<ResGood> res = new ArrayList<ResGood>(map.size());

		for(Pair<GoodUnit, Boolean> k : map.keySet())
		{
			//~: create resulting good
			ResGood g = new ResGood();
			res.add(g);

			//~: good unit
			g.setGoodUnit(k.getKey());

			//~: round up the volume
			g.setVolume(map.get(k).setScale(3, BigDecimal.ROUND_HALF_EVEN));

			//?: {transitive position} assign the calc from the cache
			if(Boolean.TRUE.equals(k.getValue()))
				g.setGoodCalc(EX.assertn(cache.get(k.getKey())));
		}

		return res;
	}
}