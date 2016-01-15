package com.tverts.retrade.domain.goods;

/* Java s*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.UnityAttr;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Special processor of Attribute Types for Good Units.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActGoodAttrType extends ActionBuilderReTrade
{
	/* Action Types */

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* Action Builder */

	public void buildAction(ActionBuildRec abr)
	{
		//?: {target is not a GoodUnit}
		checkTargetClass(abr, AttrType.class);

		//?: {ensure type present}
		if(UPDATE.equals(actionType(abr)))
		{
			AttrType type = target(abr, AttrType.class);

			//?: {not a good attribute}
			if(!(type.getOx() instanceof GoodAttr))
				return;

			//~: update the attribute type
			chain(abr).first(new UpdateGoodAttrTypeAction(task(abr)));

			complete(abr);
		}
	}


	/* Attribute Type Update Action */

	public static class UpdateGoodAttrTypeAction
	       extends      ActGoodUnit.GoodAttrsActionBase
	{
		/* public: constructor */

		public UpdateGoodAttrTypeAction(ActionTask task)
		{
			super(task);
		}


		/* Action */

		/**
		 * Returns the actual instance of updated Attribute
		 * Type that is in the Hibernate Session. Original
		 * instance is left detached!
		 */
		public AttrType getResult()
		{
			return null;
		}

		protected AttrType result;


		/* protected: Action Base */

		protected void execute()
		{
			//~: take the argument type instance
			AttrType source = HiberPoint.unproxyDeeply(
			  session(), target(AttrType.class)
			);

			//~: detach it from the session
			session().evict(source);

			//~: load resulting instance
			result = (AttrType) session().load(
			  AttrType.class, source.getPrimaryKey());

			//~: forbid changing the type
			EX.assertx(CMP.eq(source.getAttrType(), result.getAttrType()));

			//~: forbid updating system attributes
			EX.assertx(!source.isSystem() && !result.isSystem());

			//HINT: result is the old value, source is the new one

			//?: {was array, now is not}
			if(result.isArray() && !source.isArray())
				removeArrayAttributes();

			//?: {was shared, now not}
			if(result.isShared() && !source.isShared())
				removeSharedAttributes();

			//?: {was not shared, now is}
			if(!result.isShared() && source.isShared())
				insertSharedAttributes();

			//=: name
			result.setName(EX.asserts(source.getName()));

			//=: name local
			if(!SU.sXe(source.getNameLo()))
				result.setNameLo(source.getNameLo());
			if(SU.sXe(result.getNameLo()))
				result.setNameLo(result.getName());

			//=: ox-good
			GoodAttr ga = (GoodAttr)source.getOx();
			result.setOx(ga);

			//=: is-array
			result.setArray(ga.isArray());

			//=: is-shared
			result.setShared(ga.isShared());
		}

		protected void removeSharedAttributes()
		{
			bean(GetUnity.class).removeSharedAttributes(result);
		}

		protected void removeArrayAttributes()
		{
			bean(GetUnity.class).removeArrayAttributes(result);
		}

		protected void insertSharedAttributes()
		{
			//~: select all attributes of the target type
			List<UnityAttr> all = bean(GetUnity.class).getAllAttrs(result);

			//~: map the attributes by the goods
			HashMap<Long, List<UnityAttr>> g2uas = new HashMap<>();
			for(UnityAttr ua : all)
			{
				List<UnityAttr> list = g2uas.get(ua.getUnity().getPrimaryKey());
				if(list == null) g2uas.put(ua.getUnity().getPrimaryKey(),
				  list = new ArrayList<UnityAttr>());

				list.add(ua);
			}

			//~: select all sub-goods
			List<GoodUnit> subs = bean(GetGoods.class).getSubGoods(result.getDomain());

			//c: process each sub-good
			for(GoodUnit sub : subs)
			{
				//~: take attributes of the sub-good
				List<UnityAttr> res = g2uas.get(sub.getPrimaryKey());
				if(res == null) res = new ArrayList<>();

				//~: take the attributes of the super-good
				List<UnityAttr> src = g2uas.get(sub.getSuperGood().getPrimaryKey());
				if(src == null) continue;

				//!: update them
				updateAttrs(result, sub, res, src);
			}
		}
	}
}