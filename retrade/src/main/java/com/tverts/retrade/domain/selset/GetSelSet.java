package com.tverts.retrade.domain.selset;

/* Java */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* Hibernate Persistence Layer */

import org.hibernate.Query;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;
import com.tverts.hibery.HiberPoint;
import com.tverts.hibery.OxBytes;
import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: support */

import com.tverts.support.EX;


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

	public SelSet getSelSet(String name)
	{
		return getSelSet(SecPoint.login(), name);
	}

	public SelSet getSelSet(Long login, String name)
	{
		EX.assertn(login);
		EX.assertn(name);

		final String Q =
"  from SelSet where (login.id = :login) and (name = :name)";

		return object(SelSet.class, Q, "login", login, "name",  name);
	}

	public List<Long> getSelItems(SelSet set)
	{
		EX.assertn(set);

		final String Q =
"  select si.object from SelItem si where (si.selSet = :sset)";

		return list(Long.class, Q, "sset", set);
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


	/* Processing Selection Set Items */

	public void clearSelSet(Long set)
	{
		EX.assertn(set);

		final String Q =
"  delete from SelItem si where (si.selSet.id = :sset)";

		Q(Q, "sset", set).executeUpdate();
	}

	public void removeSelSetItemsByObjects(Long set, Set<Long> objects)
	{
		EX.assertn(set);
		EX.assertn(objects);

		final String Q =
"  delete from SelItem si where (si.selSet.id = :sset) and (si.object = :obj)";

		//~: create the query
		Query q = Q(Q, "sset", set);

		//c: each object given
		for(Long obj : objects)
			if(obj != null)
			{
				q.setLong("obj", obj);
				q.executeUpdate();
			}
	}

	public void removeSelSetItemsByIds(Set<Long> ids)
	{
		EX.assertn(ids);

		final String Q =
"  delete from SelItem si where (si.id = :iid)";

		//~: create the query
		Query q = Q(Q);

		//c: each object given
		for(Long id : ids)
			if(id != null)
			{
				q.setLong("iid", id);
				q.executeUpdate();
			}
	}


	/* Typed Objects Support */

	public <T> List<T> getTypedItems(String name, Class<T> oxClass, boolean shortName)
	{
		return getTypedItems(SecPoint.login(), name, oxClass, shortName);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getTypedItems(
	  Long login, String name, Class<T> oxClass, boolean shortName)
	{
		EX.assertn(login);
		EX.assertn(name);
		EX.assertn(oxClass);

/*

 select oxBytes from SelItem where (selSet.login.id = :login) and
   (selSet.name = :name) and (oxClass = :oxClass) and (oxBytes is not null)

*/
		final String Q =

"select oxBytes from SelItem where (selSet.login.id = :login) and\n" +
"  (selSet.name = :name) and (oxClass = :oxClass) and (oxBytes is not null)";

		//~: class name
		String cls = (shortName)?(oxClass.getSimpleName()):(oxClass.getName());

		//~: load the object bytes
		List<OxBytes> objs = list(OxBytes.class, Q,
		  "login", login, "name", name, "oxClass", cls);

		List<T> result = new ArrayList<>(objs.size());
		for(OxBytes oxb : objs)
		{
			//~: decode the object
			Object x = oxb.getOx();

			//?: {not of interest}
			if((x == null) || !oxClass.isAssignableFrom(x.getClass()))
				continue;

			result.add((T)x);
		}

		return result;
	}
}