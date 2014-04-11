package com.tverts.hibery.qb;

/* Java */

import java.util.Map;


/**
 * An item within {@link WhereClause}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface WherePart
{
	/* public: WherePart interface */

	public Map<String, Object> getParams();

	/**
	 * Allows to specify a prefix of the parameters that would
	 * be used in the global HQL text without automatically
	 * prefix it with the name of the part in the composite.
	 *
	 * HINT: in complex queries there are nested where parts,
	 * and each part except the root one gets the prefix combined
	 * from the prefixes of the ancestor composites. Global
	 * parameters prefix overwrites this behaviour.
	 *
	 * Prefix is inserted 'as is', without intermediate characters.
	 */
	public String getParamsPrefix();

	/**
	 * Creates HQL text representation of the query part.
	 *
	 * This text contains the named parameters having no
	 * prefixes even when {@link #getParamsPrefix()} exists.
	 * The prefixes are inserted automatically.
	 *
	 * Note that as no HQL syntax parsing is done, omit
	 * using string constants with ':' followed by a
	 * parameter name.
	 */
	public String buildText();
}