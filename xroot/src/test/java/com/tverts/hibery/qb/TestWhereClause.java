package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* JUnit library */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Checks WHERE clause of composite query builder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TestWhereClause
{
	@org.junit.Test
	public void testSimpleTextOne()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.addPart(new WhereText("test = true"));
		RESULT = "where (test = true)";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testSimpleTextTwo()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.addPart(new WhereText("test = true"));
		clause.addPart(new WhereText("other = false"));
		RESULT = "where (test = true) and (other = false)";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testSimpleTextThree()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.setOp(WhereLogic.OR);
		clause.setOp(WhereLogic.NOT);
		clause.addPart(new WhereText("test = true"));
		clause.addPart(new WhereText("other = false"));
		clause.addPart(new WhereText("rest = 'master'"));


		RESULT = "where not ((test = true) or (other = false) " +
		         "or (rest = 'master'))";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testSimpleParamsTextOne()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.addPart(new WhereText("test = not(:flag)").
		  param("flag", true));

		RESULT = "where (test = not(:x0_flag))";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testSimpleParamsTextTwo()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.setOp(WhereLogic.OR);
		clause.setOp(WhereLogic.NOT);
		clause.addPart(new WhereText("test = :flag").
		  param("flag", true));
		clause.addPart(new WhereText("other = :flag").
		  param("flag", false));
		clause.addPart(
		  new WhereText("rest = :rest_type").
		  setParamsPrefix("M").
		  param("rest_type", "master"));

		RESULT = "where not ((test = :x0_flag) or " +
		         "(other = :x1_flag) or (rest = :Mrest_type))";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testSimpleParamsTextThree()
	{
		WhereClause clause = new WhereClause();
		String      RESULT;

		clause.addPart(new WhereText("test = :flag").
		  param("flag", true));
		clause.addPart(new WhereText("other = :flag").
		  param("flag", false));
		clause.addPart("MASTER",
		  new WhereText("rest = lower(:rest_type) or fly = :fly").
		  param("rest_type", "master"));

		RESULT = "where (test = :x0_flag) and (other = :x1_flag) " +
		         "and (rest = lower(:MASTER_rest_type) or fly = :fly)";

		assertEquals(RESULT, clause.buildText());
	}

	@org.junit.Test
	public void testCompositeTextOne()
	{
		WhereClause    main = new WhereClause();
		WherePartLogic one  = new WherePartLogic();
		WherePartLogic two  = new WherePartLogic();
		String         RESULT;

		main.addPart(new WhereText("first = true"));
		main.addPart("one", one);
		main.addPart(new WhereText("inner = true"));
		main.addPart("two", two);
		main.addPart(new WhereText("last = true"));

		RESULT = "where (first = true) and (inner = true) and (last = true)";

		assertEquals(RESULT, main.buildText());
	}

	@org.junit.Test
	public void testCompositeTextTwo()
	{
		WhereClause    main = new WhereClause();
		WherePartLogic one  = new WherePartLogic();
		WherePartLogic two  = new WherePartLogic();
		String         RESULT;

		main.addPart(new WhereText("first = true"));

		one.setOp(WhereLogic.OR);
		one.addPart(new WhereText("test = 1"));
		one.addPart(new WhereText("test = 2"));
		one.addPart(new WhereText("test = 3"));

		main.addPart("ONE", one);
		main.addPart(new WhereText("inner = true"));
		main.addPart("TWO", two);
		main.addPart(new WhereText("last = true"));

		two.setOp(WhereLogic.OR);
		two.setOp(WhereLogic.NOT);
		two.addPart(new WhereText("test = 5"));
		two.addPart(new WhereText("test = 6"));

		RESULT =
		  "where (first = true) and " +
		  "((test = 1) or (test = 2) or (test = 3)) and " +
		  "(inner = true) and " +
		  "(not ((test = 5) or (test = 6))) and " +
		  "(last = true)";

		assertEquals(RESULT, main.buildText());
	}

	@org.junit.Test
	public void testCompositeTextParams()
	{
		WhereClause    main = new WhereClause();
		WherePartLogic one  = new WherePartLogic();
		WherePartLogic two  = new WherePartLogic();
		String         RESULT;

		main.addPart(new WhereText("first = :flag").
		  param("flag", true));

		one.setOp(WhereLogic.OR);
		one.addPart(new WhereText("test = :test").
		  setParamsPrefix("T0x").
		  param("test", 1));
		one.addPart(new WhereText("test = :test").
		  param("test", 2));
		one.addPart(new WhereText("test = :test").
		  param("test", 3));

		main.addPart("ONE", one);
		main.addPart("INNER", new WhereText("inner = :flag").
		  param("flag", true));
		main.addPart("TWO", two);
		main.addPart(new WhereText("last = :flag").
		  param("flag", true));

		two.setOp(WhereLogic.OR);
		two.setOp(WhereLogic.NOT);
		two.addPart(new WhereText("test = :test").
		  param("test", 5));
		two.addPart(new WhereText("test = :test").
		  param("test", 6));

		RESULT =
		  "where (first = :x0_flag) and " +
		  "((test = :T0xtest) or (test = :ONE_1_test) or (test = :ONE_2_test)) and " +
		  "(inner = :INNER_flag) and " +
		  "(not ((test = :TWO_0_test) or (test = :TWO_1_test))) and (last = :x1_flag)";

		assertEquals(RESULT, main.buildText());
	}

	@org.junit.Test
	public void testCollectParamsOne()
	{
		WhereClause         main = new WhereClause();
		WherePartLogic      one  = new WherePartLogic();
		WherePartLogic      two  = new WherePartLogic();
		Map<String, Object> prms = new HashMap<String, Object>();

		main.addPart(new WhereText("first = true"));
		main.addPart("one", one);
		main.addPart(new WhereText("inner = true"));
		main.addPart("two", two);
		main.addPart(new WhereText("last = true"));

		main.collectParams(prms);

		assertTrue(prms.isEmpty());
	}

	@org.junit.Test
	public void testCollectParamsTwo()
	{
		WhereClause         main = new WhereClause();
		WherePartLogic      one  = new WherePartLogic();
		WherePartLogic      two  = new WherePartLogic();
		Map<String, Object> prms = new HashMap<String, Object>();

		main.addPart(new WhereText("first = :flag").
		  param("flag", true));
		main.addPart("one", one);
		main.addPart(new WhereText("inner = true"));
		main.addPart("two", two);
		main.addPart(new WhereText("last = true"));

		main.collectParams(prms);

		assertEquals(1, prms.size());
		assertEquals(Boolean.TRUE, prms.get("x0_flag"));
	}

	@org.junit.Test
	public void testCollectParamsThree()
	{
		WhereClause         clause = new WhereClause();
		Map<String, Object> params = new HashMap<String, Object>();

		clause.addPart(new WhereText("test = :flag").
		  param("flag", true));
		clause.addPart("OTHER",
		  new WhereText("other = :priority").
		  param("priority", 1));
		clause.addPart("MASTER",
		  new WhereText("rest = lower(:rest_type) or fly = :fly").
		  param("rest_type", "master"));

		clause.collectParams(params);

		assertEquals(3, params.size());
		assertEquals(Boolean.TRUE, params.get("x0_flag"));
		assertEquals(1, params.get("OTHER_priority"));
		assertEquals("master", params.get("MASTER_rest_type"));
	}

	@org.junit.Test
	public void testCollectParamsFour()
	{
		WhereClause    main = new WhereClause();
		WherePartLogic one  = new WherePartLogic();
		WherePartLogic two  = new WherePartLogic();
		Map<String, Object>
		               prms = new HashMap<String, Object>();

		main.addPart(new WhereText("first = :flag").
		  param("flag", true));

		one.setOp(WhereLogic.OR);
		one.addPart(new WhereText("test = :test").
		  setParamsPrefix("T0x").
		  param("test", 1));
		one.addPart(new WhereText("test = :test").
		  param("test", 2));
		one.addPart(new WhereText("test = :test").
		  param("test", 3));

		main.addPart("ONE", one);
		main.addPart("INNER", new WhereText("inner = :flag").
		  param("flag", false));
		main.addPart("TWO", two);
		main.addPart(new WhereText("last = :flag").
		  param("flag", true));

		two.setOp(WhereLogic.OR);
		two.setOp(WhereLogic.NOT);
		two.addPart(new WhereText("test = :test").
		  param("test", 5));
		two.addPart(new WhereText("test = :test").
		  param("test", 6));

		main.collectParams(prms);

		assertEquals(8, prms.size());
		assertEquals(Boolean.TRUE, prms.get("x0_flag"));
		assertEquals(1, prms.get("T0xtest"));
		assertEquals(2, prms.get("ONE_1_test"));
		assertEquals(3, prms.get("ONE_2_test"));
		assertEquals(Boolean.FALSE, prms.get("INNER_flag"));
		assertEquals(5, prms.get("TWO_0_test"));
		assertEquals(6, prms.get("TWO_1_test"));
		assertEquals(Boolean.TRUE, prms.get("x1_flag"));
	}
}