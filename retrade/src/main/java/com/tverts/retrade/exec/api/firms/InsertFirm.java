package com.tverts.retrade.exec.api.firms;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: retrade api */

import com.tverts.api.retrade.firm.Firm;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;
import com.tverts.retrade.exec.api.accounts.EnsureDefaults;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.ActContractor;
import com.tverts.retrade.domain.firm.Contractor;


/**
 * Saves new {@link Contractor} having given API {@link Firm}.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertFirm extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean  isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Firm);
	}

	protected Long     insert(Object source)
	{
		Firm       s = (Firm) source;
		Contractor d = new Contractor();

		//~: domain
		d.setDomain(domain());

		//~: assign the contractor' firm
		assignFirm(d, s);


		//!: do save
		ActionsPoint.actionRun(ActionType.SAVE, d,
		  ActContractor.SAVE_FIRM, true
		);


		//~: create the accounts
		createFirmAccounts(d);

		return d.getPrimaryKey();
	}



	/* insert support */

	public static void assignFirm(Contractor d, Firm s)
	{
		//~: code
		d.setCode(s.getCode());

		//~: name
		d.setName(s.getName());

		//~: create firm
		d.setFirm(new com.tverts.endure.person.Firm());

		//~: firm short name
		d.getFirm().setShortName(s.getName());

		//~: firm full name
		d.getFirm().setFullName(s.getFullName());

		//~: firm tax number
		d.getFirm().setTaxNumber(s.getTaxNumber());

		//~: firm tax code
		d.getFirm().setTaxCode(s.getTaxCode());

		//~: firm address string
		d.getFirm().setAddressString(s.getAddressString());

		//~: firm phones string
		d.getFirm().setPhonesString(s.getPhonesString());
	}

	protected void     createFirmAccounts(Contractor c)
	{
		new EnsureDefaults(c, null).ensure();
	}
}