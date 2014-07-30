package com.tverts.retrade.domain.firm;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.FirmEntity;
import com.tverts.endure.person.GetFirm;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Read access to the {@link Contractor} instances.
 *
 * @author anton.baukin@gmail.com
 */
@Component
public class GetContractor extends GetFirm
{
	/* Get Contractor */

	public Contractor getContractor(Long pk)
	{
		return get(Contractor.class, pk);
	}

	public Contractor getContractor(Domain domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

// from Contractor c where (c.domain = :domain) and (c.code = :code)

		final String Q =
"  from Contractor c where (c.domain = :domain) and (c.code = :code)";

		return object(Contractor.class, Q, "domain", domain, "code", code);
	}

	public Contractor getContractor(FirmEntity fe)
	{
		EX.assertn(fe);

// from Contractor where (firm = :fe)

		final String Q =
"  from Contractor where (firm = :fe)";

		return object(Contractor.class, Q, "fe", fe);
	}

	public int        countTestContractors(Domain domain)
	{
		EX.assertn(domain);

/*

 select count(id) from Contractor where
   (domain = :domain) and (id < 0)

*/
		final String Q =

"select count(id) from Contractor where\n" +
"  (domain = :domain) and (id < 0)";

		return object(Number.class, Q, "domain", domain).intValue();
	}

	public Contractor getTestContractor(Domain domain, int skip)
	{
		EX.assertn(domain);


// from Contractor where (domain = :domain) and (id < 0)

		final String Q =
"  from Contractor where (domain = :domain) and (id < 0)";


		return (Contractor) Q(Q).
		  setParameter("domain", domain).
		  setMaxResults(1).setFirstResult(skip).
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

// Contractor co left outer join co.firm fi, Debt debt

		//~: from clause
		qb.nameEntity("Contractor", Contractor.class);
		qb.nameEntity("Debt", AggrValue.class);

		qb.setClauseFrom(

"  Contractor co left outer join co.firm fi, Debt debt"

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
		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(co.code) like :w").param("w", w);
			p.addPart("co.nameProc    like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}
}