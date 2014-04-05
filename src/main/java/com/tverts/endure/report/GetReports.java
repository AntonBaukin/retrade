package com.tverts.endure.report;

/* Java */

import java.util.List;
import java.util.Set;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;

/* com.tverts: support */

import com.tverts.support.EX;


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
		if("code".equals(mb.getSortOrder()))
			qb.setClauseOrderBy("lower(rt.code)");
		else if("code".equals(mb.getSortOrder()))
			qb.setClauseOrderBy("lower(rt.name)");
		else if("did".equals(mb.getSortOrder()))
			qb.setClauseOrderBy("lower(rt.did)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "rt.domain.id = :domain"
		).
		  param("domain", mb.domain());

		return (List<ReportTemplate>) QB(qb).list();
	}
}