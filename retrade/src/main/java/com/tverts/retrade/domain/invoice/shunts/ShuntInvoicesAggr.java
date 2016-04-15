package com.tverts.retrade.domain.invoice.shunts;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core + aggregated values) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.GetAggrValue;

/* com.tverts: retrade domain (goods + stores) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade domain (invoices + sells) */

import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.ResGood;
import com.tverts.retrade.domain.invoice.SellGood;
import com.tverts.retrade.domain.sells.Sells;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.misc.Pair;


/**
 * Tests aggregated values related (and derived) from
 * the goods of the fixed Invoices. Every aggregation
 * is checked with alternate (plain) implementation.
 *
 * @author anton.baukin@gmail.com
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:invoices", "retrade:aggr", "retrade:aggr:invoices"})
@SelfShuntDescr("Checks aggregated values derived from Buy-Sell Invoices.")
public class ShuntInvoicesAggr extends ShuntPlain
{
	/* shunt methods */

	@SelfShuntMethod(order = 0, critical = true)
	@SuppressWarnings("unchecked")
	public void testGoodAggrVolumeItems()
	{

/*

 select i.id, i.invoiceType, d.subType,
   sg.volumePositive, sg.volumeNegative, sg.volumeLeft
 from StoreGood sg join sg.invoiceState.invoice i
   join i.invoiceData d
 where (sg.store = :store) and (sg.goodUnit.id = :good)
 order by i.orderIndex, sg.volumePositive, sg.volumeNegative

 */
		Query QI = session().createQuery(

"select i.id, i.invoiceType, d.subType,\n" +
"  sg.volumePositive, sg.volumeNegative, sg.volumeLeft\n" +
"from StoreGood sg join sg.invoiceState.invoice i\n" +
"  join i.invoiceData d\n" +
"where (sg.store = :store) and (sg.goodUnit.id = :good)\n" +
"order by i.orderIndex, sg.volumePositive, sg.volumeNegative"

		);

/*

 select vi.sourceKey, vi.orderIndex, vi.historyIndex,
   vi.volumePositive, vi.volumeNegative,
   vi.aggrPositive, vi.aggrNegative, vi.aggrFixed, av.id
 from AggrItemVolume vi join vi.aggrValue av
 where (av.aggrType = :aggrType) and
   (av.owner = :store) and (av.selectorKey = :good) and
   ((vi.historyIndex is null) or (vi.aggrFixed = true))
 order by vi.orderIndex

 */
		Query QVI = session().createQuery(

"select vi.sourceKey, vi.orderIndex, vi.historyIndex,\n" +
"  vi.volumePositive, vi.volumeNegative,\n" +
"  vi.aggrPositive, vi.aggrNegative, vi.aggrFixed, av.id\n" +
"from AggrItemVolume vi join vi.aggrValue av\n" +
"where (av.aggrType = :aggrType) and\n" +
"  (av.owner = :store) and (av.selectorKey = :good) and\n" +
"  ((vi.historyIndex is null) or (vi.aggrFixed = true))\n" +
"order by vi.orderIndex"

		);

		QVI.setParameter("aggrType", UnityTypes.unityType(
		  AggrValue.class, Goods.AGGRVAL_STORE_VOLUME
		));


		//~: select all the stores and goods
		List<TradeStore>  stores = bean(GetTradeStore.class).
		  getTradeStores(domain().getPrimaryKey());

		List<Long>        goods  = bean(GetGoods.class).
		  getGoodUnitsKeys(domain());

		//c: for each Trade Store
		for(TradeStore store : stores)
		{
			QI.setParameter("store", store);
			QVI.setParameter("store", store);

			//c: for each Good Unit
			for(Long good : goods)
			{
				//~: select all the invoices having good in store
				List invs = QI.setLong("good", good).list();

				//~: select all aggregated volume components
				List vits = QVI.setLong("good", good).list();

				//?: {check the sizes are equal}
				if(invs.size() != vits.size())
				{
					HashSet<Long>   vids = new HashSet<Long>(vits.size());
					for(Object o : vits) vids.add((Long)((Object[])o)[0]);

					HashSet<Long>   ivds = new HashSet<Long>(invs.size());
					for(Object o : invs) ivds.add((Long)((Object[])o)[0]);

					HashSet<Long>   iset = new HashSet<Long>(vids);
					iset.removeAll(ivds);
					ArrayList<Long> vres = new ArrayList<Long>(iset);
					Collections.sort(vres); Collections.reverse(vres);

					iset = new HashSet<Long>(ivds); iset.removeAll(vids);
					ArrayList<Long> ires = new ArrayList<Long>(iset);
					Collections.sort(ires); Collections.reverse(ires);

					throw EX.ass(
					  "Not all Store [", store.getPrimaryKey(), "] Goods [",
					  good, "] has corresponding Aggr Volume Items!",
					  "\nHas item, but no good: [", SU.scats(", ", vres), "]",
					  "\nHas good, but no item: [", SU.scats(", ", ires), "]"
					);
				}

				//HINT: this special reordering is for those invoices
				//  where are several positions with the same good.
				{
					Long      curi = null;
					Set<Long> goti = new HashSet<Long>(17);

					//~: invoice id -> [index first, index last]
					Map<Long, Pair<Integer, Integer>> xids =
					  new HashMap<Long, Pair<Integer, Integer>>(17);

					//~: scan for the straps of the same invoices
					for(int i = 0;(i < vits.size());i++)
					{
						Object[] vit = (Object[]) vits.get(i);
						Long     iid = (Long) vit[0];

						//?: {the same invoice}
						if(iid.equals(curi))
						{
							Pair<Integer, Integer> xid = xids.get(curi);
							if(xid != null) xid.setValue(i);
							else xids.put(curi, xid = new Pair<Integer, Integer>(i - 1, i));
						}
						//?: {else invoice} check the gaps
						else EX.assertx( !goti.contains(iid),
						  "Store [", store.getPrimaryKey(), "], Good [", good,
						  "] aggregated volume items has gap for Invoice [", iid,
						  "] with Invoice [", curi, "]!"
						);

						//~: [++]
						curi = iid;
						goti.add(iid);
					}

					for(Pair<Integer, Integer> p : xids.values())
					{
						Integer b = p.getKey();
						Integer e = p.getValue(); //<-- exclusive
						if(e == null) e = vits.size(); else e++; //<-- was inclusive

						//~: check is the same invoice
						curi = null; for(int i = b;(i < e);i++)
						{
							Object[] vit = (Object[]) vits.get(i);
							Long     iid = (Long) vit[0];

							EX.assertx((curi == null) || curi.equals(iid));
							curi = iid;
						}

						Collections.sort(vits.subList(b, e), new Comparator()
						{
							public int compare(Object l, Object r)
							{
								//~: positive volumes
								BigDecimal vl = (BigDecimal) ((Object[])l)[3];
								BigDecimal vr = (BigDecimal) ((Object[])r)[3];
								if((vl != null) && (vr != null))
									return vl.compareTo(vr);

								//~: negative volumes
								vl = (BigDecimal) ((Object[])l)[4];
								vr = (BigDecimal) ((Object[])r)[4];
								if((vl != null) && (vr != null))
									return vl.compareTo(vr);

								return 0;
							}
						});
					}
				}


				//~: auto-produce (move) invoices
				Map<Long, List> apis = new HashMap<Long, List>(1);

				//~: accumulated values
				BigDecimal ap = BigDecimal.ZERO;
				BigDecimal an = BigDecimal.ZERO;
				BigDecimal sp = BigDecimal.ZERO; //<-- sum of plain items
				BigDecimal sn = BigDecimal.ZERO; // from last fixed item
				BigDecimal xp = BigDecimal.ZERO; //<-- aggregated value
				BigDecimal xn = BigDecimal.ZERO; // of last fixed item

				//~: check invoice-by-invoice
				for(int i = 0;(i < invs.size());i++)
				{
					Object[] inv = (Object[]) invs.get(i);
					Object[] vit = (Object[]) vits.get(i);

					String logstr = SU.cats(
					  "Shunt Aggr Volume [", vit[8], "] Items in Store [",
					  store.getPrimaryKey(), "] for Good [", good, "]"
					);

					//~: invoice is the source
					EX.assertx( CMP.eq(inv[0], vit[0]),
					  logstr, ": order breaked for Invoice [", inv[0], "]!"
					);

					//~: invoice + store good volumes
					Long       iid = (Long) inv[0];
					UnityType  iut = (UnityType) inv[1]; //<-- invoice unity type
					Character  ist = (Character) inv[2]; //<-- invoice sub-type
					BigDecimal ivp = v(inv[3]);
					BigDecimal ivn = v(inv[4]);
					BigDecimal ivl = v(inv[5]);

					//<: check invoice type Vs store good

					//?: {buy invoice}
					if(Invoices.typeInvoiceBuy().equals(iut))
					{
						EX.assertx(CMP.greZero(ivp));
						EX.assertx(CMP.eqZero(ivn));
						EX.assertx(CMP.eqZero(ivl));
					}
					//?: {is sell | pos sells invoice}
					else if(Invoices.typeInvoiceSell().equals(iut) ||
					  Sells.typeSellsInvoice().equals(iut))
					{
						EX.assertx(CMP.greZero(ivn));
						EX.assertx(CMP.eqZero(ivl));

						//?: {regular sell invoice}
						if(ist == null)
							EX.assertx(CMP.eqZero(ivp));
						//~: altered sell invoice
						else if(!CMP.eqZero(ivp)) //?: {this item is transient product}
							EX.assertx(CMP.eq(ivp, ivn));
					}
					//?: {is move invoice}
					else if(Invoices.typeInvoiceMove().equals(iut))
					{
						if(CMP.eqZero(ivp))
							EX.assertx(CMP.grZero(ivn));

						if(CMP.eqZero(ivn))
							EX.assertx(CMP.grZero(ivp));

						EX.assertx(CMP.eqZero(ivl));
					}
					//?: {is not volume check document}
					else if(!Invoices.typeVolumeCheck().equals(iut))
						throw EX.ass(logstr, ": unexpected Invoice type having Store Good!");

					//>: check invoice type Vs store good

					//~: item attributes
					Long       voi = (Long) vit[1];
					Long       vhi = (Long) vit[2];
					BigDecimal vvp = v(vit[3]);
					BigDecimal vvn = v(vit[4]);
					BigDecimal vap = v(vit[5]);
					BigDecimal van = v(vit[6]);
					boolean    vfx = Boolean.TRUE.equals(vit[7]);

					//~: order index must be defined
					EX.assertn(voi);

					//~: fixed history item
					EX.assertx(vfx == Invoices.typeVolumeCheck().equals(iut));

					//~: check history index
					if(vfx) EX.assertx(CMP.eq(vhi, voi));
					else EX.assertx(vhi == null);

					//~: check volume added for plain items
					if(!vfx)
					{
						EX.assertx(CMP.eqZero(vap));
						EX.assertx(CMP.eqZero(van));

						//?: {auto-produce move invoice}
						if(Invoices.typeInvoiceMove().equals(iut) && CMP.eq('A', ist))
						{
							List l = apis.get(iid);
							if(l == null) apis.put(iid, l = new ArrayList(2));

							//~: for further checks
							l.add(inv); l.add(vit);
						}
						else
						{
							EX.assertx(CMP.eq(vvp, ivp),
							  logstr, ": (vvp != ivp), Invoice ", iid);

							EX.assertx(CMP.eq(vvn, ivn), logstr,
							  ": (vvn != ivn), Invoice ", iid);
						}
					}

					//?: {this is fixed history item}
					if(vfx)
					{
						//~: calculate expected aggregates
						BigDecimal ep = xp.add(sp);
						BigDecimal en = xn.add(sn);

						//~: add volume deltas stored in item
						ep = ep.add(vvp);
						en = en.add(vvn);

						//~: subtract & compare
						BigDecimal e = ep.subtract(en);     //<-- expected with delta
						BigDecimal a = vap.subtract(van);   //<-- actual volume
						EX.assertx(CMP.eq(e, a));

						//~: check volume deltas stored in the good
						//     (assigned by the calculator)
						if(!CMP.eq(vvp, ivp))
							throw EX.ass(logstr, ": v+ in store good != fixed item!");
						if(!CMP.eq(vvn, ivn))
							throw EX.ass(logstr, ": v- in store good != fixed item!");

						//~: erase calculation state
						xp = ap = vap;
						xn = an = van;
						sp = sn = BigDecimal.ZERO;
					}
					//~: this is plain volume item
					else
					{
						ap = ap.add(vvp); an = an.add(vvn);
						sp = sp.add(vvp); sn = sn.add(vvn);
					}
				}

				//~: check the items of auto-produce invoices
				for(Long iid : apis.keySet())
				{
					List l = apis.get(iid);

					//~: even size
					EX.assertx((l.size() % 2) == 0);

					//~: for all store goods found
					for(int i = 0;(i < l.size());i += 2)
					{
						Object[]   inv = (Object[]) l.get(i);
						Object[]   vit = null;
						BigDecimal ivp = v(inv[3]);
						BigDecimal ivn = v(inv[4]);

						//~: search for the volume item with the same +/- volumes
						for(int j = 1;(i < l.size());j += 2)
						{
							if((vit = (Object[]) l.get(j)) == null)
								continue;

							BigDecimal vvp = v(vit[3]);
							BigDecimal vvn = v(vit[4]);

							//?: {match volumes}
							if(CMP.eq(vvp, ivp) && CMP.eq(vvn, ivn))
							{
								l.set(j, null);
								break;
							}
							else
								vit = null;
						}

						//?: {not found}
						EX.assertn(vit);
					}
				}

				//~: load the aggregated value
				AggrValue av  = bean(GetAggrValue.class).getAggrValue(
				  store.getPrimaryKey(), Goods.aggrTypeStoreVolume(), good);

				EX.assertn(av, "Store [", store.getPrimaryKey(),
				  "] must have volume Aggregated Value for Good Unit [", good, "]!"
				);

				//~: compare with it
				BigDecimal avp = v(av.getAggrPositive());
				BigDecimal avn = v(av.getAggrNegative());
				BigDecimal avv = avp.subtract(avn);
				BigDecimal axv = ap.subtract(an);

				EX.assertx( CMP.eq(axv, avv),

				  "Good Unit [", good, "] Volume aggregated ",
				  "value in Store [", store.getPrimaryKey(),
				  "] has wrong value [", avv, "] needed: [", axv, "]!"
				);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@SelfShuntMethod(order = 1, critical = true)
	public void testStoresVolumes()
	{

/*

 select st.id, gu.id, sum(sg.volumePositive), sum(sg.volumeNegative) from
   StoreGood sg join sg.store st join sg.goodUnit gu
 where (sg.store.domain = :domain)
 group by st.id, gu.id

 */

		//~: select the stores volumes aggregated
		List<Object[]> vols = (List<Object[]>) session().createQuery(

"select st.id, gu.id, sum(sg.volumePositive), sum(sg.volumeNegative) from\n" +
"   StoreGood sg join sg.store st join sg.goodUnit gu\n" +
"where (sg.store.domain = :domain)\n" +
"group by st.id, gu.id"

		).
		  setParameter("domain", domain()).
		  list();


		//~: map positive and negative volumes
		HashMap<Pair<Long, Long>, BigDecimal> pvols =
		  new HashMap<Pair<Long, Long>, BigDecimal>(vols.size());
		HashMap<Pair<Long, Long>, BigDecimal> nvols =
		  new HashMap<Pair<Long, Long>, BigDecimal>(vols.size());

		for(Object[] o : vols)
		{
			Pair<Long, Long> k =
			  new Pair<Long, Long>((Long)o[0], (Long)o[1]);

			pvols.put(k, (BigDecimal)o[2]);
			nvols.put(k, (BigDecimal)o[3]);
		}

		//~: check the aggregated values of stores
		GetAggrValue      gav    = bean(GetAggrValue.class);
		List<TradeStore>  stores = bean(GetTradeStore.class).
		  getTradeStores(domain().getPrimaryKey());
		List<Long>        goods  = bean(GetGoods.class).
		  getGoodUnitsKeys(domain());

		//c: for all the stores
		for(TradeStore store : stores)
		{
			//c: for all the goods
			for(Long good : goods)
			{
				Pair<Long, Long> k =
				  new Pair<Long, Long>(store.getPrimaryKey(), good);

				//~: load the aggregated value (good is the selector)
				AggrValue av  = gav.getAggrValue(
				  k.getKey(), Goods.aggrTypeStoreVolume(), k.getValue());

				EX.assertn( av, "Store [", k.getKey(),
				  "] must have volume Aggregated Value for Good [", k.getValue(), "]!"
				);

				BigDecimal vp = v(av.getAggrPositive());
				BigDecimal vn = v(av.getAggrNegative());
				BigDecimal v  = vp.subtract(vn);

				BigDecimal sp = v(pvols.get(k));
				BigDecimal sn = v(nvols.get(k));
				BigDecimal s  = sp.subtract(sn);

				EX.assertx( CMP.eq(v, s),

				  "Good Unit [", good, "] Volume aggregated ",
				  "value in Store [", store.getPrimaryKey(),
				  "] has wrong value [", v, "] needed: [", s, "]!"
				);
			}
		}
	}

	@SelfShuntMethod(order = 2, critical = true)
	@SuppressWarnings("unchecked")
	public void testRestCostsItemsOrder()
	{
		List ITYPES = Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell(),
		  Sells.typeSellsInvoice()
		);

/*

select i.id, i.orderIndex from Invoice i where
  (i.domain = :domain) and (i.invoiceType in (:invoiceTypes))
  and (i.invoiceState.unity.unityType = :stateType)

*/

		//~: select the invoices that affect goods volumes
		List ids = session().createQuery(

"select i.id, i.orderIndex from Invoice i where\n" +
"  (i.domain = :domain) and (i.invoiceType in (:invoiceTypes))\n" +
"  and (i.invoiceState.unity.unityType = :stateType)"

		).
		  setParameter("domain",           domain()).
		  setParameterList("invoiceTypes", ITYPES).
		  setParameter("stateType",        Invoices.typeInvoiceStateFixed()).
		  list();


		//~: build invoices order map
		HashMap order = new HashMap(ids.size());

		for(Object id : ids)
			order.put(((Object[])id)[0], ((Object[])id)[1]);

/*

 select r.id, i.id, av.id, r.orderIndex, r.historyIndex from
   AggrItemRestCost r join r.aggrValue av, Invoice i
 where (r.sourceKey = i.id) and (i.domain = :domain)
   and (i.invoiceType in (:invoiceTypes)) and
   (i.invoiceState.unity.unityType = :stateType)
 order by av.id, r.orderIndex

*/
		List items = session().createQuery(

"select r.id, i.id, av.id, r.orderIndex, r.historyIndex from\n" +
"  AggrItemRestCost r join r.aggrValue av, Invoice i\n" +
"where (r.sourceKey = i.id) and (i.domain = :domain)\n" +
"  and (i.invoiceType in (:invoiceTypes)) and\n" +
"  (i.invoiceState.unity.unityType = :stateType)\n" +
"order by av.id, r.orderIndex"

		).
		  setParameter    ("domain",       domain()).
		  setParameterList("invoiceTypes", ITYPES).
		  setParameter    ("stateType",    Invoices.typeInvoiceStateFixed()).
		  list();


		for(int i = 1;(i < items.size());i++)
		{
			Long r0 = (Long)((Object[])items.get(i - 1))[0];
			Long i0 = (Long)((Object[])items.get(i - 1))[1];
			Long a0 = (Long)((Object[])items.get(i - 1))[2];

			Long r1 = (Long)((Object[])items.get(i))[0];
			Long i1 = (Long)((Object[])items.get(i))[1];
			Long a1 = (Long)((Object[])items.get(i))[2];

			//?: {aggregated value is changed} skip
			if(!a0.equals(a1)) continue;

			Long o0 = (Long)order.get(i0);
			EX.assertn(o0);

			Long o1 = (Long)order.get(i1);
			EX.assertn(o1);

			EX.assertn( o0 <= o1, String.format(

			  "Aggr Value Rest Cost [%d] items [%d, %d] do break invoices " +
			  "[%d, %d] order [%d, %d]!", a0, r0, r1, i0, i1, o0, o1
			));

			//~: check history index equals
			Long x0 = (Long)((Object[])items.get(i - 1))[3];
			Long y0 = (Long)((Object[])items.get(i - 1))[4];

			EX.assertn(x0);
			EX.assertx( (y0 == null) || y0.equals(x0), String.format(

			  "Aggr Value Rest Cost [%d] item [%d] of invoice [%d] has " +
			  "wrong history index [%d], must be [%d]!", a0, r0, i0, y0, x0
			));
		}
	}

	@SelfShuntMethod(order = 3, critical = true)
	@SuppressWarnings("unchecked")
	public void testRestCosts()
	{
		List ITYPES = Arrays.asList(
		  Invoices.typeInvoiceBuy(),
		  Invoices.typeInvoiceSell(),
		  Sells.typeSellsInvoice()
		);

/*

select i.id from Invoice i where (i.domain = :domain)
  and (i.invoiceType in (:invoiceTypes)) and
  (i.invoiceState.unity.unityType = :stateType)
order by i.orderIndex

*/

		//<: select the invoices that affect goods volumes
		List<Long> ids = (List<Long>) session().createQuery(

"select i.id from Invoice i where (i.domain = :domain)\n" +
"  and (i.invoiceType in (:invoiceTypes)) and\n" +
"  (i.invoiceState.unity.unityType = :stateType)\n" +
"order by i.orderIndex"

		).
		  setParameter("domain",           domain()).
		  setParameterList("invoiceTypes", ITYPES).
		  setParameter("stateType",        Invoices.typeInvoiceStateFixed()).
		  list();

		LU.I(getLog(), " found ", ids.size(),
		  " Invoices affecting goods rest costs... ");

		//>: select the invoices...

		//~: Good Unit pk -> summary volume
		HashMap<Long, BigDecimal> vols =
		  new HashMap<Long, BigDecimal>(17);

		//~: Good Unit pk -> rest cost value
		HashMap<Long, BigDecimal> costs =
		  new HashMap<Long, BigDecimal>(17);

		//c: for all the invoices selected
		for(Long id : ids)
		{
			//~: load the invoice
			Invoice i  = (Invoice) session().load(Invoice.class, id);

			//~: invoice flags
			boolean xs = Invoices.isSellInvoice(i) || Sells.isSellsInvoice(i);
			boolean xb = Invoices.isBuyInvoice(i);
			boolean xa = i.getInvoiceData().isAltered();

			//~: take the proper goods list
			List<? extends InvGood> gs = (xa)
			  ?(i.getInvoiceData().getResGoods())
			  :(i.getInvoiceData().getGoods());


			//c: for all the invoice' goods
			for(InvGood g : gs)
			{
				Long       gid = g.getGoodUnit().getPrimaryKey();
				BigDecimal w0  = vols.get(gid);
				if(w0 == null) w0 = BigDecimal.ZERO;

				//?: {it is altered sell invoice}
				if(g instanceof ResGood)
				{
					EX.assertx(xs && xa);

					//?: {this is transient position} skip it
					if(((ResGood)g).getGoodCalc() != null)
						continue;
				}

				//?: {it is sell invoice} just subtract the volume
				if((g instanceof SellGood) || (g instanceof ResGood))
				{
					EX.assertx(xs);

					BigDecimal w1 = w0.subtract(g.getVolume());
					vols.put(gid, w1);

					continue;
				}

				//~: recalculate cost for buy invoice
				if(!(xb && (g instanceof BuyGood)))
					throw EX.ass();

				BuyGood    bg = (BuyGood) g;
				BigDecimal w1 = w0.add(g.getVolume());
				BigDecimal co = costs.get(gid);

				if((co != null) && (w0.signum() == 1))
					co = co.multiply(w0).add(bg.getCost());
				else
				{
					w1 = g.getVolume();
					co = bg.getCost();
				}

				co = co.divide(w1, 64, BigDecimal.ROUND_HALF_UP);

				int p = co.precision();
				if(BigDecimal.ONE.compareTo(co) == +1)
					p += 1; //!: see AggregatorRestCost.roundCostValue()

				if(p > 63)
				{
					int scale = co.scale() - (p - 63);
					EX.assertx( (scale >= 0),

					  "Decimal value [", co.toString(),
					  "] is too large for Rest cost storage!"
					);

					co = co.setScale(scale, BigDecimal.ROUND_HALF_UP);
				}

				//~: save updated values
				vols.put(gid, w1);
				costs.put(gid, co);
			}

			//~: clear the session
			session().clear();
		}

		GetAggrValue gav = bean(GetAggrValue.class);

		//~: check the aggregated volumes
		for(Long gid : vols.keySet())
		{
			//~: load the aggregated value
			AggrValue av = gav.getAggrValue(gid, Goods.aggrTypeRestCost(), null);
			EX.assertn(av, "Good Unit [", gid, "] must have Rest cost Aggregated Value!");

			BigDecimal s0 = av.getAggrValue();
			BigDecimal s1 = costs.get(gid);

			if(s0 == null) s0 = BigDecimal.ZERO;
			if(s1 == null) s1 = BigDecimal.ZERO;

			 //~: aggregated value in db has 10 scale
			s1 = s1.setScale(10, BigDecimal.ROUND_HALF_UP);

			//!: compare the values
			EX.assertx( CMP.eq(s0, s1), "Good Unit [",  gid,
			  "] Rest cost aggregated value [", av.getPrimaryKey(),
			  "] has wrong value [", s0, "] vs tested [", s1, "]!"
			);

			//~: clear the session
			session().clear();
		}
	}

	/**
	 * This test checks the AggrItemVolume lines has
	 * helper history items standing from each other
	 * not closer than N/2, and not longer than 2N
	 * plain items.
	 *
	 * Note that fixed history items are also considered
	 * as they are history items too.
	 */
	@SelfShuntMethod(order = 4, critical = true)
	@SuppressWarnings("unchecked")
	public void testVolumeHelperHistoryItemsStepping()
	{
		EX.assertn( getVolumeHistoryStep(),
		  "Aggregation Volume History Step is not defined!"
		);

		int N = getVolumeHistoryStep();
/*

 select
   g.fk_aggr_value as "av",
   g.history_index as "hi",
   count(x.pk_aggr_item) as "S"
 from aggr_item_volume g, aggr_item_volume x where
   (g.fk_aggr_value = x.fk_aggr_value) and
   (g.history_index is not null) and (x.history_index is null) and
   (x.order_index < g.history_index)
 group by g.fk_aggr_value, g.history_index
 order by g.fk_aggr_value, g.history_index


 select g.historyIndex, g.aggrFixed, count(x.id) from
   AggrItemVolume g join g.aggrValue av, AggrItemVolume x
 where (x.orderIndex < g.historyIndex) and
   (g.historyIndex is not null) and (x.historyIndex is null) and
   (av.id = x.aggrValue.id) and (av.aggrType = :aggrType) and
   (av.owner = :store) and (av.selectorKey = :good)
 group by g.historyIndex, g.aggrFixed order by g.historyIndex

 */
		//~: select the items with count of plains preceding
		Query q = session().createQuery(

"select g.historyIndex, g.aggrFixed, count(x.id) from\n" +
"  AggrItemVolume g join g.aggrValue av, AggrItemVolume x\n" +
" where (x.orderIndex < g.historyIndex) and\n" +
"  (g.historyIndex is not null) and (x.historyIndex is null) and\n" +
"  (av.id = x.aggrValue.id) and (av.aggrType = :aggrType) and\n" +
"  (av.owner = :store) and (av.selectorKey = :good)\n" +
"group by g.historyIndex, g.aggrFixed order by g.historyIndex"

		);

		//~: store good aggregated volumes
		q.setParameter("aggrType", UnityTypes.unityType(
		  AggrValue.class, Goods.AGGRVAL_STORE_VOLUME
		));


		//~: select all the stores and goods
		List<TradeStore>  stores = bean(GetTradeStore.class).
		  getTradeStores(domain().getPrimaryKey());

		List<Long>        goods  = bean(GetGoods.class).
		  getGoodUnitsKeys(domain());

		//c: for each Trade Store
		for(TradeStore store : stores )
		{
			q.setParameter("store", store);

			//c: for each Good Unit
			for(Long good : goods)
			{
				//~: select the items line
				List<Object[]> items = (List<Object[]>)q.setLong("good", good).list();

				//c: inspect them
				for(int i = 1;(i < items.size());i++)
				{
					long    i0 = (Long)(items.get(i - 1)[0]);
					boolean f0 = Boolean.TRUE.equals(items.get(i - 1)[1]);
					long    s0 = ((Number)(items.get(i - 1)[2])).longValue();
					long    i1 = (Long)(items.get(i)[0]);
					boolean f1 = Boolean.TRUE.equals(items.get(i)[1]);
					long    s1 = ((Number)(items.get(i)[2])).longValue();

					int     d  = (int)(s1 - s0);

					//?: {both items are fixed} skip them
					if(f0 && f1) continue;

					EX.assertx( d > N/2,

					  "Aggr Volume history items with indices [", i0, "] - [", i1,
					  "] are too close to each other! Store is [", store.getPrimaryKey(),
					  "], Good is [", good, "]."
					);

					EX.assertx( d < N*2,

					  "Aggr Volume history items with indices [", i0, "] - [", i1,
					  "] are too distant from each other! Store is [",
					  store.getPrimaryKey(), "], Good is [", good, "]."
					);
				}
			}
		}
	}

	@SelfShuntMethod(order = 5, critical = true)
	@SuppressWarnings("unchecked")
	public void testVolumeHelperHistoryItemsVolumes()
	{
/*

 select vi.volumePositive, vi.volumeNegative,
   vi.aggrPositive, vi.aggrNegative,
   vi.historyIndex, vi.aggrFixed, vi.id
 from AggrItemVolume vi join vi.aggrValue av
 where (av.aggrType = :aggrType) and
   (av.owner = :store) and (av.selectorKey = :good)
 order by vi.orderIndex

 */
		//~: select the items with count of plains preceding
		Query q = session().createQuery(

"select vi.volumePositive, vi.volumeNegative,\n" +
"  vi.aggrPositive, vi.aggrNegative,\n" +
"  vi.historyIndex, vi.aggrFixed, vi.id\n" +
"from AggrItemVolume vi join vi.aggrValue av\n" +
"where (av.aggrType = :aggrType) and\n" +
"  (av.owner = :store) and (av.selectorKey = :good)\n" +
"order by vi.orderIndex"

		);

		//~: store good aggregated volumes
		q.setParameter("aggrType", UnityTypes.unityType(
		  AggrValue.class, Goods.AGGRVAL_STORE_VOLUME
		));


		//~: select all the stores and goods
		List<TradeStore>  stores = bean(GetTradeStore.class).
		  getTradeStores(domain().getPrimaryKey());

		List<Long>        goods  = bean(GetGoods.class).
		  getGoodUnitsKeys(domain());

		//c: for each Trade Store
		for(TradeStore store : stores )
		{
			q.setParameter("store", store);

			//c: for each Good Unit
			for(Long good : goods)
			{
				//~: select the items line
				List<Object[]> items = (List<Object[]>)q.setLong("good", good).list();

				BigDecimal sp = BigDecimal.ZERO;
				BigDecimal sn = BigDecimal.ZERO;

				//c: inspect them
				for(Object[] item : items)
				{
					BigDecimal vp = v(item[0]);
					BigDecimal vn = v(item[1]);
					BigDecimal ap = (BigDecimal) item[2];
					BigDecimal an = (BigDecimal) item[3];
					boolean    hi = (item[4] != null);
					boolean    af = Boolean.TRUE.equals(item[5]);
					Long       id = (Long) item[6];

					//?: {fixed history item} just assign the aggregates
					if(hi && af)
					{
						sp = v(ap);
						sn = v(an);
						continue;
					}

					//?: {helper history item}
					if(hi)
					{
						EX.assertx( CMP.eq(sp, ap) && CMP.eq(sn, an),

						  "Aggr Volume helper history item [", id,
						  "] has wrong volumes: +[", ap, "] vs [", sp,
						  "], -[", an, "] vs [", sn, "]!"
						);

						continue;
					}

					//~: just volume item
					sp = sp.add(vp); sn = sn.add(vn);
				}
			}
		}
	}


	/* public: parameters */

	private Integer volumeHistoryStep;

	public Integer getVolumeHistoryStep()
	{
		return volumeHistoryStep;
	}

	public void setVolumeHistoryStep(Integer volumeHistoryStep)
	{
		this.volumeHistoryStep = volumeHistoryStep;
	}

	public ShuntInvoicesAggr setDomain(Domain domain)
	{
		super.setDomain(domain);
		return this;
	}

	/* private: shunt helpers */

	private static BigDecimal v(Object o)
	{
		if(o == null) return BigDecimal.ZERO;
		if(!(o instanceof BigDecimal))
			throw EX.ass("Not a volume decimal!");

		BigDecimal n = (BigDecimal) o;
		EX.assertx(CMP.greZero(n), "Volume component is below zero!");
		return n;
	}
}