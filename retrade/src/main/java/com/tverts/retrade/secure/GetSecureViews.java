package com.tverts.retrade.secure;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: endure (secure) */

import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecAble;
import com.tverts.endure.secure.SecAbleView;
import com.tverts.endure.secure.SecRule;
import com.tverts.endure.secure.SecRuleView;
import com.tverts.endure.secure.SecSet;
import com.tverts.endure.secure.SecSetView;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Extends {@link GetSecure} to load data related
 * to {@link SecRulesModelBean} and it's views.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getSecureViews")
public class GetSecureViews extends GetSecure
{
	/* Secure Rules Views */

	public long countRules(SecRulesModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SecRule r");
		qb.nameEntity("SecRule", SecRule.class);

		//~: select clause
		qb.setClauseSelect("count(r.id)");


		//~: restrict the domain and not hidden
		qb.getClauseWhere().addPart(
		  "(r.domain.id = :domain) and (r.hidden = false)"
		).
		  param("domain", mb.domain());


		//~: restrict by the search words
		rulesWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictRulesBySelSet(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<SecRuleView> selectRules(SecRulesModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SecRule r");
		qb.nameEntity("SecRule", SecRule.class);

		//~: order by
		qb.setClauseOrderBy("r.titleLo");


		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the domain and not hidden
		qb.getClauseWhere().addPart(
		  "(r.domain.id = :domain) and (r.hidden = false)"
		).
		  param("domain", mb.domain());


		//~: restrict by the search words
		rulesWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictRulesBySelSet(qb, mb.getSelSet());


		//~: select the rules
		List<SecRule> rules = (List<SecRule>) QB(qb).list();

		//~: create the views
		List<SecRuleView> views = new ArrayList<SecRuleView>(rules.size());
		for(SecRule r : rules)
			views.add(new SecRuleView().init(r));

		return views;
	}


	/* Secure Ables Views */

	public long countAbles(SecAblesModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		// SecAble a join a.login l join a.rule r join a.set s

		//~: from clause
		qb.setClauseFrom("SecAble a join a.login l join a.rule r join a.set s");
		qb.nameEntity("SecAble", SecAble.class);

		//~: select clause
		qb.setClauseSelect("count(a.id)");


		//~: restrict the login and not hidden rule
		qb.getClauseWhere().addPart(
		  "(l.id = :login) and (r.hidden = false)"
		).
		  param("login", mb.getAuthLogin());

		//~: restrict by the secure set
		if(mb.getSecSet() != null)
			qb.getClauseWhere().addPart(
			  "s.id = :set"
			).
			  param("set", mb.getSecSet());


		//~: restrict by the search words
		ablesWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictAblesBySelSet(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<SecAbleView> selectAbles(SecAblesModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		// SecAble a join a.login l join a.rule r join a.set s

		//~: from clause
		qb.setClauseFrom("SecAble a join a.login l join a.rule r join a.set s");
		qb.nameEntity("SecAble", SecAble.class);

		//~: select clause
		qb.setClauseSelect("l, r, s, a");

		//~: order by
		qb.setClauseOrderBy("r.titleLo, lower(s.name)");


		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());


		//~: restrict the login and not hidden rule
		qb.getClauseWhere().addPart(
		  "(l.id = :login) and (r.hidden = false)"
		).
		  param("login", mb.getAuthLogin());

		//~: restrict by the secure set
		if(mb.getSecSet() != null)
			qb.getClauseWhere().addPart(
			  "s.id = :set"
			).
			  param("set", mb.getSecSet());


		//~: restrict by the search words
		ablesWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictAblesBySelSet(qb, mb.getSelSet());


		//~: select the ables
		List ables = QB(qb).list();

		//~: create the views
		List<SecAbleView> views = new ArrayList<SecAbleView>(ables.size());
		for(Object row : ables)
			views.add(new SecAbleView().init(row));

		return views;
	}


	/* Secure Sets Views */

	public long countSets(SecSetsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SecSet s");
		qb.nameEntity("SecSet", SecSet.class);

		//~: select clause
		qb.setClauseSelect("count(s.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "s.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//~: exclude system set (empty name)
		qb.getClauseWhere().addPart(
		  "s.name <> ''"
		);

		//~: restrict by the search words
		setsWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictSetsBySelSet(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<SecSetView> selectSets(SecSetsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SecSet s");
		qb.nameEntity("SecSet", SecSet.class);

		//~: order by
		qb.setClauseOrderBy("lower(s.name)");

		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "s.domain.id = :domain"
		).
		  param("domain", mb.domain());


		//~: exclude system set (empty name)
		qb.getClauseWhere().addPart(
		  "s.name <> ''"
		);

		//~: restrict by the search words
		setsWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictSetsBySelSet(qb, mb.getSelSet());


		//~: select the sets
		List<SecSet> sets = (List<SecSet>) QB(qb).list();

		//~: create the views
		List<SecSetView> views = new ArrayList<SecSetView>(sets.size());
		for(SecSet s : sets)
			views.add(new SecSetView().init(s));

		return views;
	}


	/* private: shortage routines */

	private void rulesWordsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			qb.getClauseWhere().
			  addPart("r.titleLo like :w").param("w", w);
		}
	}

	private void restrictRulesBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 r.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 r.related.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

*/

		p.addPart(

"r.id in (select si.object from SelItem si join si.selSet ss " +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());


		p.addPart(

"r.related.id in (select si.object from SelItem si join si.selSet ss " +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}

	private void ablesWordsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(s.name) like :w").param("w", w);
			p.addPart("r.titleLo like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void restrictAblesBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 r.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

 s.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

*/

		p.addPart(

"r.id in (select si.object from SelItem si join si.selSet ss " +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set", selset).
		  param("login", SecPoint.login());


		p.addPart(

"s.id in (select si.object from SelItem si join si.selSet ss " +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}

	private void setsWordsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			qb.getClauseWhere().
			  addPart("lower(s.name) like :w").param("w", w);
		}
	}

	private void restrictSetsBySelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

		WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);
		qb.getClauseWhere().addPart(p);

/*

 s.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

*/

		p.addPart(

"s.id in (select si.object from SelItem si join si.selSet ss\n" +
"   where (ss.name = :set) and (ss.login.id = :login))"

		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}
}