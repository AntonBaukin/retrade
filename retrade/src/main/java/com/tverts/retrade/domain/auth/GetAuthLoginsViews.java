package com.tverts.retrade.domain.auth;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;
import com.tverts.hibery.qb.WhereLogic;
import com.tverts.hibery.qb.WherePartLogic;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (authentication) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.AuthLoginView;
import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Extends {@link GetAuthLogin} adding loading
 * of the Authentication Logins Views.
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAuthLoginsViews")
public class GetAuthLoginsViews extends GetAuthLogin
{
	/* Get Authentication Logins Views */

	public long countLogins(AuthLoginsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("Login l");
		qb.nameEntity("Login", AuthLogin.class);

		//~: select clause
		qb.setClauseSelect("count(l.id)");


		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "l.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//?: {persons only}
		if(mb.isPersonsOnly())
			qb.getClauseWhere().addPart(
			  "l.computer is null"
			);

		//~: restrict by the search words
		loginsWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictLoginsOfSelSet(qb, mb.getSelSet());

		return ((Number) QB(qb).uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<AuthLoginView> selectLoginViews(AuthLoginsModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

/*

Login l left outer join l.person p
  left outer join l.computer c

*/

		//~: from clause
		qb.nameEntity("Login", AuthLogin.class);
		qb.setClauseFrom(

"Login l left outer join l.person p\n" +
"  left outer join l.computer c"

		);

		//~: select clause
		qb.setClauseSelect("l, p, c");

		//~: order by
		qb.setClauseOrderBy("lower(l.code)");


		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		//~: restrict the domain
		qb.getClauseWhere().addPart(
		  "l.domain.id = :domain"
		).
		  param("domain", mb.domain());

		//?: {persons only}
		if(mb.isPersonsOnly())
			qb.getClauseWhere().addPart(
			  "c is null"
			);

		//~: restrict by the search words
		loginsWordsSearch(qb, mb.getSearchNames());

		//~: restrict to the selection set
		restrictLoginsOfSelSet(qb, mb.getSelSet());

		//~: select the logins
		List rows  = (List<Object[]>) QB(qb).list();
		List views = new ArrayList<AuthLoginView>(rows.size());

		for(Object row : rows)
			views.add(new AuthLoginView().init(row));

		return (List<AuthLoginView>) views;
	}


	/* private: shortage routines */

	private void loginsWordsSearch(QueryBuilder qb, String[] words)
	{
		if(words != null) for(String w : words) if((w = s2s(w)) != null)
		{
			w = "%" + w.toLowerCase() + "%";

			WherePartLogic p = new WherePartLogic().setOp(WhereLogic.OR);

			p.addPart("lower(l.code) like :w").param("w", w);
			p.addPart("lower(l.name) like :w").param("w", w);

			qb.getClauseWhere().addPart(p);
		}
	}

	private void restrictLoginsOfSelSet(QueryBuilder qb, String selset)
	{
		if(selset == null) return;

/*

 l.id in (select si.object from SelItem si join si.selSet ss
   where (ss.name = :set) and (ss.login.id = :login))

*/

		qb.getClauseWhere().addPart(
 "l.id in (select si.object from SelItem si join si.selSet ss " +
 "   where (ss.name = :set) and (ss.login.id = :login))"
		).
		  param("set",   selset).
		  param("login", SecPoint.login());
	}
}