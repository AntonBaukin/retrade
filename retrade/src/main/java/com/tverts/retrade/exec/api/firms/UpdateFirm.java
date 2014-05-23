package com.tverts.retrade.exec.api.firms;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.UpdateEntityBase;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.ActContractor;
import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: retrade api */

import com.tverts.api.retrade.firm.Firm;


/**
 * Updates {@link Contractor} with API {@link Firm} instance.
 * Note that Contractor may have no Firm associated.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateFirm extends UpdateEntityBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Firm);
	}

	protected Class   getUnityClass(Holder holder)
	{
		return Contractor.class;
	}

	protected void    update(Object entity, Object source)
	{
		Firm       s = (Firm) source;
		Contractor d = (Contractor) entity;

		//?: {create new firm}
		boolean saveFirm = (d.getFirm() == null);
		if(saveFirm) d.setFirm(new com.tverts.endure.person.Firm());

		//~: assign the contractor' firm
		InsertFirm.assignFirm(d, s);


		//!: update action
		ActionsPoint.actionRun(ActionType.UPDATE, d,
		  ActContractor.SAVE_FIRM, saveFirm
		);
	}
}