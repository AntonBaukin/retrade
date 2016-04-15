package com.tverts.retrade.exec.datas.invoices;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.TuneQuery;

/* com.tverts: data sources */

import com.tverts.data.models.AdaptedEntitiesSelected;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Strategy to filter altered Invoices by their
 * sub-types. Intended to be used only for
 * {@link AdaptedEntitiesSelected} model'
 *
 * @author anton.baukin@gmail.com.
 */
public class SelectInvoiceSubType implements TuneQuery
{
	public static final long serialVersionUID = 20140806;


	/* Tune Query */

	public void tuneQuery(QueryBuilder qb)
	{
		//~: get & check teh sub-types
		String[] ts = SU.s2aws(subTypes);
		for(String t : ts) EX.assertx( (t.length() == 1),
		  "Wrong invoice sub-type character [", t, "]!"
		);

		//?: {has no sub-types} may not be altered
		if(ts.length == 0)
			qb.getClauseWhere().addPart(
			  "e.invoiceData.subType is null"
			);
		//~: restrict by the sub-types
		else
			qb.getClauseWhere().addPart(
			  "e.invoiceData.subType in (:subTypes)"
			).param("subTypes", ts);
	}


	/* Select Invoice Sub-Type (bean) */

	/**
	 * A space-separated characters with
	 * the sub-types of the invoices.
	 */
	public String getSubTypes()
	{
		return subTypes;
	}

	private String subTypes;

	public void setSubTypes(String subTypes)
	{
		this.subTypes = subTypes;
	}
}