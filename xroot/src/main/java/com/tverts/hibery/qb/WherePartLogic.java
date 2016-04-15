package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * A {@link WherePartComposite} that concatenates
 * the parts aggregated with the logic operators
 * from {@link WhereLogic} enumeration.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class WherePartLogic extends WherePartCompositeBase
{
	/* public: WherePartLogic interface */

	/**
	 * Returns the binary logic operator set.
	 * By default, it is AND.
	 */
	public WhereLogic     getBinaryOp()
	{
		return (and_or)?(WhereLogic.AND):(WhereLogic.OR);
	}

	public boolean        isNegated()
	{
		return negated;
	}

	/**
	 * This method works as follows. When AND or OR
	 * {@link WhereLogic} constant is given, the
	 * operator used to connect the parts is changed.
	 *
	 * But when NOT is set, the binary operator is
	 * not changed, and the negation flag is reversed.
	 *
	 * This trick allows to write not(and) or not(or)
	 * bracketed operations without nesting a sub-part.
	 */
	public WherePartLogic setOp(WhereLogic logic)
	{
		if(WhereLogic.NOT.equals(logic))
			negated = !negated;
		else
			and_or  = WhereLogic.AND.equals(logic);

		return this;
	}


	/* public: WherePart interface */

	public String         buildText()
	{
		StringBuilder text  = new StringBuilder(256);
		StringBuilder xtext = null;
		boolean       inso  = false;
		Map<String, String>
		              names = new HashMap<String, String>(7);

		//?: {negated} open
		if(negated) text.append("not (");

		//c: for all aggregated parts
		for(Entry<String, WherePart> epart : getParts().entrySet())
		{
			//build the text of that composite
			String ptext = s2s(epart.getValue().buildText());

			//?: {has no effective text}
			if(ptext == null) continue;

			//?: {it is a simple part} rename the parameters manually
			if(!(epart.getValue() instanceof WherePartComposite))
			{
				//~: build the names mapping of that part
				names.clear();
				nameSimplePartsParams(epart.getKey(), names);

				//~: copy the part text
				if(xtext == null)
					xtext = new StringBuilder(ptext);
				else
					xtext.delete(0, xtext.length()).append(ptext);

				//~: rename the parameters in the query
				renameQueryParams(xtext, names);
				ptext = xtext.toString();
			}

			//?: {insert operator}
			if(inso) text.append(and_or?(" and "):(" or "));
			inso = true;
			text.append('(').append(ptext).append(')');
		}

		//?: {negated} close
		if(negated) text.append(')');

		return text.toString();
	}

	public WherePartLogic setParamsPrefix(String prefix)
	{
		super.setParamsPrefix(prefix);
		return this;
	}


	/* protected: logic state */

	/**
	 * Use AND operator when {@code true},
	 * OR operator when {@code false}.
	 */
	protected boolean and_or = true; //<-- and, by default

	/**
	 * Indicates that the entire predicate is negated.
	 */
	protected boolean negated;
}
