package com.tverts.retrade.exec.api.firms;

/* com.tverts: objects */

import com.tverts.objects.XMAPoint;

/* com.tverts: hibery */

import com.tverts.endure.person.FirmEntity;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.Contractors;

/* com.tverts: api */

import com.tverts.api.clients.Firm;

/* com.tverts: execution (api) */

import com.tverts.api.core.DumpEntities;
import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps the domain Contractors having Firms.
 *
 * @author anton.baukin@gmail.com
 */
public class DumpFirms extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		Contractor s = (Contractor)src;
		Firm       d = XMAPoint.cloneObject(
		  EX.assertn(s.getFirm()).getOx());

		d.setPkey(s.getPrimaryKey());
		d.setTx(s.getTxn());

		return d;
	}

	protected Class  getUnityClass()
	{
		return FirmEntity.class;
	}

	protected Class  getEntityClass()
	{
		return Firm.class;
	}

	public String    getUnityType()
	{
		return Contractors.TYPE_FIRM;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected UnityType unityType(DumpEntities de)
	{
		return UnityTypes.unityType(Contractor.class, Contractors.TYPE_CONTRACTOR);
	}

	protected int    getDumpLimit()
	{
		return 32;
	}

	protected void   restrictDump(QueryBuilder qb, DumpEntities de)
	{
		//~: select only contractors with firms
		qb.getClauseWhere().addPart(
		  "e.firm is not null"
		);
	}
}
