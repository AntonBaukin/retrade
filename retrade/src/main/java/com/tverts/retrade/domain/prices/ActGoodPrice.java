package com.tverts.retrade.domain.prices;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * Actions builder for {@link GoodPrice} instances.
 * Save action is implemented.
 *
 * @author anton.baukin@gmail.com
 */
public class ActGoodPrice extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveGoodPrice(abr);
	}


	/* protected: action methods */

	protected void saveGoodPrice(ActionBuildRec abr)
	{
		//?: {target is not a GoodPrice}
		checkTargetClass(abr, GoodPrice.class);

		//~: save the price entry
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setOwner(target(abr, GoodPrice.class).getPriceList()));

		complete(abr);
	}
}