package com.tverts.endure;

/**
 * Interface of an Object Extracting Entity
 * that has special support for full-text
 * search. It collects all text-alike values
 * and combines them in one lower-cased
 * string with '\f' used as the separators.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface OxSearch extends Ox
{
	/* Object Extraction */

	public String getOxSearch();
}