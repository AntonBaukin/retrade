package com.tverts.retrade.domain.firm;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade domain (invoice) */

import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Read access to the {@link Contractor} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getContractor")
public class GetContractor extends GetObjectBase
{
	/* Get Contractor */

	public Contractor getContractor(Long pk)
	{
		return (pk == null)?(null):
		  (Contractor)session().get(Contractor.class, pk);
	}

	public Contractor getContractor(Domain domain, String code)
	{
		if(domain == null)
			throw new IllegalArgumentException();

		if((code = s2s(code)) == null)
			throw new IllegalArgumentException();

/*

from Contractor c where
  (c.domain = :domain) and (c.code = :code)

*/

		return (Contractor)Q(

		  "from Contractor c where\n" +
		  "  (c.domain = :domain) and (c.code = :code)"

		).
		  setParameter("domain", domain).
		  setString("code", code).
		  uniqueResult();
	}

	public int        countTestContractors(Domain domain)
	{
		if(domain == null)
			throw new IllegalArgumentException();

/*

 select count(id) from Contractor where
   (domain = :domain) and (id < 0)

*/
		return ((Number)Q(

		  "select count(id) from Contractor where\n" +
		  "   (domain = :domain) and (id < 0)"

		).
		  setParameter("domain", domain).
		  uniqueResult()).intValue();
	}

	public Contractor getTestContractor(Domain domain, int skip)
	{
		if(domain == null)
			throw new IllegalArgumentException();


// from Contractor where (domain = :domain) and (id < 0)

		return (Contractor)Q(
		  "from Contractor where (domain = :domain) and (id < 0)"
		).
		  setParameter("domain", domain).
		  setMaxResults(1).
		  setFirstResult(skip).
		  uniqueResult();
	}


	/* Contractors selection */

	public long             countContractors(InvoiceEditModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Contractor co");
		qb.nameEntity("Contractor", Contractor.class);

		//~: select clause
		qb.setClauseSelect("count(co.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "co.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict by the search words
		coWordsSearch(qb, mb.getContractorsWords());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Contractor> selectContractors(InvoiceEditModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Contractor co");
		qb.nameEntity("Contractor", Contractor.class);

		//~: select clause
		qb.setClauseSelect("co");

		//~: order by
		qb.setClauseOrderBy("co.nameProc");

		//~: the limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "co.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict by the search words
		coWordsSearch(qb, mb.getContractorsWords());

		return (List<Contractor>) QB(qb).list();
	}

	public long             countContractors(ContractorsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Contractor co");
		qb.nameEntity("Contractor", Contractor.class);

		//~: select clause
		qb.setClauseSelect("count(co.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "co.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict by the search words
		coWordsSearch(qb, mb.getSearchNames());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	/**
	 * Returns list with arrays of:
	 *  0  the contractor instance;
	 *  1  the contractor's firm (may be null);
	 *  2  aggregated value of the contractor debt.
	 */
	public List             selectContractors(ContractorsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

/*

Contractor co left outer join co.firm fi, Debt debt

*/

		//~: from clause
		qb.nameEntity("Contractor", Contractor.class);
		qb.nameEntity("Debt", AggrValue.class);

		qb.setClauseFrom(

"Contractor co left outer join co.firm fi, Debt debt"

		);

		//~: select clause
		qb.setClauseSelect("co, fi, debt");

		//~: order by
		qb.setClauseOrderBy("co.nameProc");

		//~: the limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: contractor >< debt  [1-to-1, strict]
		qb.getClauseWhere().addPart(
		  "debt.owner.id = co.id"
		);

		//~: cost aggr type
		qb.getClauseWhere().addPart(
		  "debt.aggrType = :aggrType"
		).
		  param("aggrType", Contractors.aggrTypeContractorDebt());


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "co.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict by the search words
		coWordsSearch(qb, mb.getSearchNames());

		return QB(qb).list();
	}


	/* private: shortage routines */

	private void coWordsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(co.code) like :w").param("w", w);
			p.addPart("co.nameProc    like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}
}