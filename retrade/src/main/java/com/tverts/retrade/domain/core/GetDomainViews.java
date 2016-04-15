package com.tverts.retrade.domain.core;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.endure.core.Domain;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.GetDomain;
import com.tverts.endure.cats.CatItemView;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Extends Get Domain adding support
 * for UI components.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getDomainViews")
public class GetDomainViews extends GetDomain
{
	/* Get Domain */

	@SuppressWarnings("unchecked")
	public List<Domain> selectDomains(DomainsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Domain", Domain.class);
		qb.setClauseFrom("Domain d");

		//~: select clause
		qb.setClauseSelect("d");

		//~: order by
		qb.setClauseOrderBy("d.codeux");

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: keywords search restrictions
		domainsSearch(qb, mb.searchNames());

		return QB(qb).list();
	}

	public List<CatItemView> selectDomainViews(DomainsModelBean mb)
	{
		List<Domain>      domains = selectDomains(mb);
		List<CatItemView> result  = new ArrayList<CatItemView>(domains.size());

		for(Domain d : domains)
			result.add(new CatItemView().init(d));
		return result;
	}

	public int countDomains(DomainsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Domain", Domain.class);
		qb.setClauseFrom("Domain d");

		//~: select clause
		qb.setClauseSelect("count(d)");

		//~: keywords search restrictions
		domainsSearch(qb, mb.searchNames());

		return ((Number)QB(qb).uniqueResult()).intValue();
	}


	/* private: local restrictions */

	private void domainsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("d.codeux      like :w").param("w", w);
			p.addPart("lower(d.name) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}
}