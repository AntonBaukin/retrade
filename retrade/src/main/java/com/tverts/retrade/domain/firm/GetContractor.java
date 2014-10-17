package com.tverts.retrade.domain.firm;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: endure (persons) */

import com.tverts.endure.person.FirmEntity;
import com.tverts.endure.person.GetFirm;

/* com.tverts: retrade domain (invoices, prices) */

import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.prices.FirmPrices;

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

	public Contractor getContractorFirm(long firmKey)
	{
		EX.assertn(firmKey);

// from Contractor where (firm = :fe)

		final String Q =
"  from Contractor where (firm = :fe)";

		return object(Contractor.class, Q, "fe", firmKey);
	}

	public Contractor getContractorFirmStrict(FirmEntity fe)
	{
		Contractor c = getContractorFirm(fe.getPrimaryKey());

		//?: {not found it} //<-- proxy usage care
		if(c == null) throw EX.ass(
		  "Firm [", fe.getPrimaryKey(), "] code [",
		  fe.getCode(), "] has no Contractor!"
		);

		return c;
	}

	public Long       getContractorByFirmKey(long firmKey)
	{
		EX.assertn(firmKey);

// select id from Contractor where (firm = :fe)

		final String Q =
"  select id from Contractor where (firm = :fe)";

		return object(Long.class, Q, "fe", firmKey);
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


	/* Contractors Selection */

	public List<Contractor> selectFirmContractors(Long domain)
	{
		EX.assertn(domain);

// from Contractor where (domain.id = :domain) and (firm is not null)

		final String Q =

"  from Contractor where (domain.id = :domain) and (firm is not null)";

		return list(Contractor.class, Q, "domain", domain);
	}

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
		restrictByNames(qb, mb.getContractorsWords());

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
		restrictByNames(qb, mb.getContractorsWords());

		return (List<Contractor>) QB(qb).list();
	}

	public int              countContractors(ContractorsModelBean mb)
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
		restrictByNames(qb, mb.searchNames());

		//~: restrict by the selection sets
		restrictsBySelSet(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).intValue();
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
		restrictByNames(qb, mb.searchNames());

		//~: restrict by the selection sets
		restrictsBySelSet(qb, mb.getSelSet());

		return QB(qb).list();
	}

	public int              countContractorsLists(ContractorsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("FirmPrices", FirmPrices.class);
		qb.setClauseFrom("FirmPrices fp join fp.contractor co");

		//~: select clause
		qb.setClauseSelect("count(distinct co.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "co.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: restrict by the search words
		restrictByNames(qb, mb.searchNames());

		//~: restrict by the selection sets
		restrictsBySelSetLists(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public void             selectContractorsLists
	  (ContractorsModelBean mb, Map<Contractor, List<FirmPrices>> prices)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("FirmPrices", FirmPrices.class);
		qb.setClauseFrom("FirmPrices fp join fp.contractor co");

		//~: select clause
		qb.setClauseSelect("fp, co");

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
		restrictByNames(qb, mb.searchNames());

		//~: restrict by the selection sets
		restrictsBySelSetLists(qb, mb.getSelSet());

		//~: select the items & map them
		List<Object[]> rows = (List<Object[]>) QB(qb).list();
		for(Object[] row : rows)
		{
			List<FirmPrices> fps = prices.get((Contractor) row[1]);
			if(fps == null) prices.put((Contractor)row[1],
			  fps = new ArrayList<FirmPrices>(4));
			
			fps.add((FirmPrices) row[0]);
		}
		
		//~: sort the price list associations by the priority
		for(List<FirmPrices> fps : prices.values())
			Collections.sort(fps, new Comparator<FirmPrices>()
			{
				public int compare(FirmPrices l, FirmPrices r)
				{
					return Integer.compare(l.getPriority(), r.getPriority());
				}
			});
	}


	/* private: shortage routines */

	private void           restrictByNames(QueryBuilder qb, String[] words)
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

	private WherePartLogic restrictsBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return null;

		//~: create OR
		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

	/*

 co.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :sset) and (ss.login.id = :login))

	*/

			//~: search contractors directly
			p.addPart(

"co.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"

			).
			  param("sset",  selset).
			  param("login", SecPoint.login());

			return p;
	}

	private WherePartLogic restrictsBySelSetLists(QueryBuilder qb, String selset)
	{
		WherePartLogic p = restrictsBySelSet(qb, selset);
		if(p == null) return null;

	/*

 fp.priceList.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :sset) and (ss.login.id = :login))

	*/

			//~: search by the associated price lists
			p.addPart(

"fp.priceList.id in (select si.object from SelItem si join si.selSet ss\n" +
"  where (ss.name = :sset) and (ss.login.id = :login))"

			).
			  param("sset",  selset).
			  param("login", SecPoint.login());

		return p;
	}
}