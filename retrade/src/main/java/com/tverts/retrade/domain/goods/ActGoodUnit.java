package com.tverts.retrade.domain.goods;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: objects */

import com.tverts.objects.XPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;
import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.UnityAttr;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.ActAggrValue;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Good;

/* com.tverts: retrade domain (core + stores) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * {@link GoodUnit} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActGoodUnit extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;

	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* parameters of the action task */

	/**
	 * Boolean flag, if set the {@link MeasureUnit} reference
	 * would be explicitly saved.
	 */
	public static final String SAVE_MEASURE_UNIT =
	  ActGoodUnit.class.getName() + ": save measure unit";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
		{
			saveGoodUnit(abr);

			//!: take exclusive locks (is executed very first!)
			lockGoodUnits(abr);
		}

		if(UPDATE.equals(actionType(abr)))
			updateGoodUnit(abr);

		if(ENSURE.equals(actionType(abr)))
		{
			ensureGoodUnit(abr);

			//!: take exclusive locks (is executed very first!)
			lockGoodUnits(abr);
		}
	}


	/* protected: action methods */

	protected void saveGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: ensure the good unit        <-- is executed last!
		xnest(abr, ActGoodUnit.ENSURE, target(abr));

		//~: process the attributes
		chain(abr).first(new GoodAttrsAction(task(abr)));

		//~: save the good unit
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//?: {set good unit' unity}
		if(target(abr, GoodUnit.class).getUnity() == null)
			xnest(abr, ActUnity.CREATE, target(abr));

		//?: {has save measure unit flag set}
		if(flag(abr, SAVE_MEASURE_UNIT))
			saveMeasureUnit(abr);

		complete(abr);
	}

	protected void saveMeasureUnit(ActionBuildRec abr)
	{
		MeasureUnit munit = target(abr, GoodUnit.class).getMeasure();

		//?: {there is no measure unit reference}
		if(munit == null) return;

		//~: the domain
		munit.setDomain(target(abr, GoodUnit.class).getDomain());

		//?: {it is a test good unit} create test key
		if((munit.getPrimaryKey() == null) && isTestTarget(abr))
			HiberPoint.setPrimaryKey(session(abr), munit, true);

		//!: nest the measure creation
		xnest(abr, ActMeasureUnit.SAVE, munit);
	}

	protected void updateGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: process the attributes
		chain(abr).first(new GoodAttrsAction(task(abr)));

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void ensureGoodUnit(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, GoodUnit.class);

		//~: create the needed aggregated values
		ensureGoodUnitAggrValues(abr);

		complete(abr);
	}

	protected void ensureGoodUnitAggrValues(ActionBuildRec abr)
	{
		//~: the rest cost of the good unit
		buildAggrValue(abr, Goods.AGGRVAL_GOOD_REST_COST, null);

		GoodUnit good = target(abr, GoodUnit.class);

		//~: ensure volume aggregated values for each store present
		List<TradeStore> stores = bean(GetTradeStore.class).
		  getTradeStores(good.getDomain().getPrimaryKey());

		for(TradeStore store : stores)
			buildAggrValue(abr, Goods.AGGRVAL_STORE_VOLUME, good,
			  ActAggrValue.OWNER, store,
			  ActAggrValue.CALCS, Goods.AGGR_CALC_VOL_CHECK
			);
	}

	/**
	 * Takes {@link Goods#LOCK_XGOODS} lock.
	 */
	protected void lockGoodUnits(ActionBuildRec abr)
	{
		takeLock(abr, target(abr, GoodUnit.class).getDomain(),
		  Goods.LOCK_XGOODS);
	}


	/* Attribute Save-Update Action */

	public static class GoodAttrsAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public GoodAttrsAction(ActionTask task)
		{
			super(task);
		}


		/* Action */

		/**
		 * Returns the list of Unity Attribute
		 * of the target Good Unit that were
		 * created or updated by this action.
		 */
		public List<UnityAttr> getResult()
		{
			return result;
		}


		/* protected: Action Base */

		@SuppressWarnings("unchecked")
		protected void execute()
		  throws Throwable
		{
			//~: ox-good
			Good g = EX.assertn(target(GoodUnit.class).getOx());

			//!: explicitly forbid to save attributes in ox-good
			g.setAttrValues(null);

			//?: {has no attributes}
			if(g.getAttrs() == null) return;

			GetUnity get = bean(GetUnity.class);

			//~: load all the attributes
			List<AttrType> atts = get.getAttrTypes(
			  target(GoodUnit.class).getDomain().getPrimaryKey(),
			  Goods.typeGoodAttr().getPrimaryKey()
			);

			//~: map the names
			HashMap<String, AttrType> n2at = new HashMap<>(atts.size());
			for(AttrType a : atts) n2at.put(a.getName(), a);

			//~: the values copy
			Map<String, Object> vals = new LinkedHashMap<>(g.getAttrs());

			//~: check all the attributes exist
			for(Map.Entry<String, Object> e : vals.entrySet())
				EX.assertx(n2at.containsKey(e.getKey()), "Good Attribute [",
				  e.getKey(), "] is not found in the database!");

			//~: load all existing attribute values
			List<UnityAttr> uas = get.getAttrs(
			  target(GoodUnit.class).getPrimaryKey());

			//~: map attributes by the types
			Map<AttrType, List<UnityAttr>> a2vs = new HashMap<>();
			for(UnityAttr ua : uas)
			{
				List<UnityAttr> x = a2vs.get(ua.getAttrType());
				if(x == null) a2vs.put(ua.getAttrType(),
				  x = new ArrayList<UnityAttr>(1));

				x.add(ua);
			}

			//~: update existing attributes
			for(Map.Entry<AttrType, List<UnityAttr>> e : a2vs.entrySet())
			{
				String name = e.getKey().getName();

				//?: {has no this attribute} skip it, not remove!
				if(!vals.containsKey(name))
					continue;

				//~: emit value
				updateAttrs(e.getValue(), vals.remove(name), e.getKey());
			}

			//~: insert new attributes
			for(Map.Entry<String, Object> e : vals.entrySet())
				updateAttrs(new ArrayList<>(0), e.getValue(),
				  EX.assertn(n2at.get(e.getKey())));
		}

		@SuppressWarnings("unchecked")
		protected void updateAttrs(List<UnityAttr> uas, Object v, AttrType type)
		{
			if(v instanceof Object[])
				v = Arrays.asList((Object[]) v);
			if(!(v instanceof Collection))
				v = Collections.singletonList(v);
			if(!(v instanceof List))
				v = new ArrayList((Collection) v);

			List      vs = (List)v;
			UnityAttr ua;
			boolean   cr;

			EX.assertx(vs.size() != 0);

			//~: sort existing attributes by index
			Collections.sort(uas, (l, r) -> CMP.cmp(l.getIndex(), r.getIndex()));

			//?: {type is not an array}
			if(!type.isArray())
			{
				//?: {has multiple values}
				EX.assertx(vs.size() == 1, "Good Attribute [",
				  type.getName(), "] may not contain multiple values!");

				if(cr = uas.isEmpty())
					initAttr(ua = new UnityAttr(), type);
				else
					ua = uas.set(0, null);

				//=: null-index
				ua.setIndex(null);

				//=: null-source
				ua.setSource(null);

				//~: assign the value
				updateAttr(ua, vs.iterator().next());

				//~: add to the result
				result.add(ua);

				//?: {created} save
				if(cr) session().save(ua);
			}
			//~: persist multiple records
			else for(int i = 0;(i < vs.size());i++)
			{
				if(cr = (i >= uas.size()))
					initAttr(ua = new UnityAttr(), type);
				else
					ua = uas.set(i, null);

				//=: i-index
				ua.setIndex(i);

				//=: null-source
				ua.setSource(null);

				//~: assign the value
				updateAttr(ua, vs.get(i));

				//~: add to the result
				result.add(ua);

				//?: {created} save
				if(cr) session().save(ua);
			}

			//~: remove records not needed
			for(UnityAttr x : uas)
				if(x != null)
					session().delete(x);
		}

		protected void initAttr(UnityAttr ua, AttrType type)
		{
			//=: primary key
			HiberPoint.setPrimaryKey(session(), ua,
			  HiberPoint.isTestInstance(target(GoodUnit.class)));

			//=: good unity
			ua.setUnity(EX.assertn(target(GoodUnit.class).getUnity()));

			//=: attribute type
			ua.setAttrType(type);
		}

		protected void updateAttr(UnityAttr ua, Object v)
		{
			ua.setString(null);
			ua.setInteger(null);
			ua.setNumber(null);
			ua.setBytes(null);

			if(v instanceof String)
				ua.setString((String) v);
			else if(v instanceof BigDecimal)
				ua.setNumber((BigDecimal) v);
			else if(v instanceof Long)
				ua.setInteger((Long) v);
			else if(v instanceof Integer)
				ua.setInteger(((Integer) v).longValue());
			else if(v != null)
			{
				EX.assertx(!(v instanceof Collection));
				EX.assertx(!(v instanceof Object[]));

				ua.setBytes(XPoint.xml().write(true, v));
			}
		}

		protected List<UnityAttr> result =
		  new ArrayList<>();
	}
}