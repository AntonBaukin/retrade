package com.tverts.endure.report;

/* Java */

import java.util.Date;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
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

	public ReportTemplate getReportTemplate(long pk)
	{
		return get(ReportTemplate.class, pk);
	}

	public ReportTemplate getReportTemplate (Long domain, String code)
	{
		EX.assertn(domain);
		EX.asserts(code);

// from ReportTemplate rt where (rt.domain.id = :domain) and (rt .code = :code)

		return object( ReportTemplate.class,

"  from ReportTemplate rt where (rt.domain.id = :domain) and (rt .code = :code)",

		  "domain", domain,
		  "code",   code
		);
	}

	public boolean hasReportRequests(long template)
	{

// select rr.id from ReportRequest rr where (rr.template.id = :template)

		return !Q(

"  select rr.id from ReportRequest rr where (rr.template.id = :template)"

		).
		  setLong("template", template).
		  setMaxResults(1).
		  list().isEmpty();
	}

	public int countTemplates(DataSelectModelBean mb)
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
		restrictTemplates(qb, mb.searchNames());

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<ReportTemplate> selectTemplates(DataSelectModelBean mb)
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
			qb.setClauseOrderBy(SU.cats(
			 "lower(rt.did) ", mb.getFirstSortDir(), ", lower(rt.name)"
			));
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
		restrictTemplates(qb, mb.searchNames());

		return (List<ReportTemplate>) QB(qb).list();
	}


	/* Get Report Requests */

	public ReportRequest getReportRequest(long pk)
	{
		return get(ReportRequest.class, pk);
	}

	public List<Long> selectReportsToMake()
	{

// select r.id from ReportRequest r where (r.ready = false) order by r.time

		return list(Long.class,

"  select r.id from ReportRequest r where (r.ready = false) order by r.time"

		);
	}

	public int countRequests(DataSelectModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Request", ReportRequest.class);
		qb.setClauseFrom("Request rr join rr.template rt");

		//~: select clause
		qb.setClauseSelect("count(rt.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "rt.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: filter templates by the words
		restrictTemplates(qb, mb.searchNames());

		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	/**
	 * Returns array of:
	 * [0] report request;
	 * [1] report template.
	 */
	@SuppressWarnings("unchecked")
	public List selectRequests(DataSelectModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("Request", ReportRequest.class);
		qb.setClauseFrom("Request rr join rr.template rt");

		//~: select clause
		qb.setClauseSelect("rr, rt");

		//~: order by clause
		if("time".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("rr.time " + mb.getFirstSortDir());
		else if("templateName".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("lower(rt.name) " + mb.getFirstSortDir());
		else if("templateDid".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy(SU.cats(
			 "lower(rt.did) ", mb.getFirstSortDir(), ", rr.time"
			));
		else if("templateCode".equals(mb.getFirstSortProp()))
			qb.setClauseOrderBy("lower(rt.code) " + mb.getFirstSortDir());
		else
			qb.setClauseOrderBy("rr.time asc");

		//~: the limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "rt.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//~: filter templates by the words
		restrictTemplates(qb, mb.searchNames());

		return QB(qb).list();
	}


	/* Utilities */

	public int cleanup(long timeout)
	{
		final String Q =
"delete from ReportRequest where (loadTime < :time)";

		return Q(Q, "time", new Date(System.currentTimeMillis() - timeout)).
		  executeUpdate();
	}

	public int erase(long timeout)
	{
		final String Q =
"delete from ReportRequest where (time < :time)";

		return Q(Q, "time", new Date(System.currentTimeMillis() - timeout)).
		  executeUpdate();
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