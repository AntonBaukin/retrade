package com.tverts.endure.report;

/* Java */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Collection of functions to load instances
 * related to report templates and requests.
 *
 * @author anton.baukin@gmail.com.
 */
@Component
public class GetReports extends GetObjectBase
{
	/* Get Report Templates */

	public int countTemplates(ReportsSelectModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Template rt");
		qb.nameEntity("Template", ReportTemplate.class);

		//~: select clause
		qb.setClauseSelect("count(rt.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "rt.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: filter by the words
		restrictTemplates(qb, mb.getSearchWords());

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<ReportTemplate> selectTemplates(ReportsSelectModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Template rt");
		qb.nameEntity("Template", ReportTemplate.class);

		//~: select clause
		qb.setClauseSelect("rt");

		//~: order by clause
		if("name".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("lower(rt.name) " + mb.getFirstSortDir());
		else if("did".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("lower(rt.did) " + mb.getFirstSortDir());
		else if("code".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("lower(rt.code) " + mb.getFirstSortDir());
		else
			qb.setClauseOrderBy("lower(rt.code) asc");

		//~: the limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "rt.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: filter by the words
		restrictTemplates(qb, mb.getSearchWords());

		return (List<ReportTemplate>) QB(qb).list();
	}


	/* private: support methods */

	private void restrictTemplates(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = SU.s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(rt.code) like :w").param("w", w);
			p.addPart("lower(rt.name) like :w").param("w", w);
			p.addPart("lower(rt.did) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}
}