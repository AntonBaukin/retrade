package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.Map;

/**
 * Represents a {@link WherePart} aggregating other parts.
 *
 * The parts are referenced by names. If no name is provided,
 * the name is selected as a next integer from the internally
 * incremented sequence.
 *
 * When collecting parameters of the global query the parts
 * having no unique prefix would be prefixed with the part name.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface WherePartComposite extends WherePart
{
	/* public: WherePartComposite interface */

	/**
	 * Add the part with auto-generated name.
	 */
	public WherePartComposite
	              addPart(WherePart part);

	/**
	 * Adds, or replaces, or removes the part by the key given.
	 * To remove just pass {@code null} value.
	 */
	public WherePartComposite
	              addPart(String name, WherePart part);

	/**
	 * Returns the name of the last part had been added
	 * and not removed yet.
	 */
	public String lastPartName();

	/**
	 * When adding this composite into the surrounding one,
	 * the global prefix is used to collect the parameters
	 * and to auto-prefix the parameters in the HQL text.
	 *
	 * The global prefix is a path of the prefixes of the
	 * composites tree with '_' separators.
	 *
	 * If no global prefix is set, this composite is a top-level,
	 * and the prefix has the value of empty string.
	 */
	public void   setGlobalPrefix(String prefix);

	public void   collectParams(Map<String, Object> result);
}