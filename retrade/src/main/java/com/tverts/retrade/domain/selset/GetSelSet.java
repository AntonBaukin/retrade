package com.tverts.retrade.domain.selset;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.qb.QueryBuilder;


/**
 * Handler for the Selection Sets..
 *
 * @author anton.baukin@gmail.com
 */
@Component("getSelSet")
public class GetSelSet extends GetObjectBase
{
	/* Get Selection Set */

	@SuppressWarnings("unchecked")
	public List<String> getSelSets(Long login)
	{

/*

select ss.name from SelSet ss where
  (ss.login.id = :login)
order by ss.id asc

*/
		String Q =

"select ss.name from SelSet ss where\n" +
"  (ss.login.id = :login)\n" +
"order by ss.id asc ";

		if(HiberPoint.isTestPrimaryKey(login))
			Q = Q.replace(" asc ", " desc");

		return (List<String>) Q(Q).
		  setLong("login", login).
		  list();
	}

	public SelSet getSelSet(Long login, String name)
	{

// from SelSet where (login.id = :login) and (name = :name)

		return (SelSet) Q(
		  "from SelSet where (login.id = :login) and (name = :name)"
		).
		  setLong  ("login", login).
		  setString("name",  name).
		  uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Long> getSelItems(SelSet set)
	{

// select si.object from SelItem si where (si.selSet = :sset)

		return (List<Long>) Q(
"select si.object from SelItem si where (si.selSet = :sset)"
		).
		  setParameter("sset", set).
		  list();
	}

	public int countSelSetSize(SelSetModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SelItem si join si.selSet sels");
		qb.nameEntity("SelItem", SelItem.class);

		//~: select clause
		qb.setClauseSelect("count(si.id)");

		//~: selection set
		qb.getClauseWhere().addPart(
		  "(sels.login.id = :login) and (sels.name = :name)"
		).
		  param("login", mb.getLogin()).
		  param("name",  mb.getSelSet());


		return ((Number) QB(qb).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<SelItem> selectSelSetItems(SelSetModelBean mb)
	{
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.setClauseFrom("SelItem si join si.selSet sels");
		qb.nameEntity("SelItem", SelItem.class);

		//~: select clause
		qb.setClauseSelect("si");

		//~: order by
		if(HiberPoint.isTestPrimaryKey(mb.getLogin()))
			qb.setClauseOrderBy("si.id asc");
		else
			qb.setClauseOrderBy("si.id desc");

		//~: selection set
		qb.getClauseWhere().addPart(
		  "(sels.login.id = :login) and (sels.name = :name)"
		).
		  param("login", mb.getLogin()).
		  param("name",  mb.getSelSet());

		//~: the selection limits
		qb.setFirstRow(mb.getDataStart());
		qb.setLimit(mb.getDataLimit());

		return (List<SelItem>) QB(qb).list();
	}
}