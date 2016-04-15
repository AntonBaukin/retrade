package com.tverts.hibery.qb;

/* JUnit library */

import static org.junit.Assert.assertEquals;


/**
 * Checks the resulting queries created by
 * composite query builder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TestQueryBuilder
{
	@org.junit.Test
	public void testSimpleQuery()
	{
		QueryBuilder qb = new QueryBuilder();
		String       QT;

		qb.setClauseFrom("Master m");

		qb.getClauseWhere().addPart(
		  new WhereText("m.name = :master_name").
		  param("master_name", "Putin"));

		QT = "from Master m where (m.name = :x0_master_name)";

		assertEquals(QT, qb.buildQueryText());
	}

	@org.junit.Test
	public void testExistsSubquery()
	{
		QueryBuilder qb = new QueryBuilder();
		WhereExists  ex = new WhereExists();
		String       QT;

		qb.setClauseSelect("m.name, m.position");
		qb.setClauseFrom("Master m");
		qb.getClauseWhere().
		  addPart("master",
		    new WhereText("m.name = :name").
		    param("name", "Putin")).
		  addPart(ex);

		ex.setClauseFrom("Padavan p");
		ex.getClauseWhere().addPart(
		  new WhereText("p.name = :name").
		  setParamsPrefix("padavan_").
		  param("name", "Medvedev"));
		ex.getClauseWhere().addPart(
		  new WhereText("p.master = m"));

		QT =
		  "select m.name, m.position from Master m " +
		  "where (m.name = :master_name) and " +
		  "(exists (from Padavan p where (p.name = :padavan_name) and (p.master = m)))";

		assertEquals(QT, qb.buildQueryText());
	}

	@org.junit.Test
	public void testEntitiesNaming()
	{
		QueryBuilder qb = new QueryBuilder();
		String       QT;

		qb.setClauseSelect("s.value, d.day");
		qb.setClauseFrom("String s, Date d");

		qb.getClauseWhere().addPart(
		  new WhereText("length(s.value) <= :length").
		  param("length", 10));

		qb.getClauseWhere().addPart(
		  new WhereText("d between :mind and :maxd").
		  param("mind", new java.util.Date()).
		  param("maxd", new java.util.Date()));

		qb.nameEntity(java.util.Date.class);
		qb.nameEntity(String.class);

		QT =
		  "select s.value, d.day from " +
		    "java.lang.String s, java.util.Date d " +
		  "where (length(s.value) <= :x0_length) " +
		    "and (d between :x1_mind and :x1_maxd)";


		assertEquals(QT, qb.buildQueryText());
	}
}