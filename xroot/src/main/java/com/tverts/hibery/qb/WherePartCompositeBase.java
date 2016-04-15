package com.tverts.hibery.qb;

/* standard Java classes */

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Bone implementation of a 'where' clause part composite.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class WherePartCompositeBase
       extends        WherePartBase
       implements     WherePartComposite
{
	/* public: WherePartComposite interface */

	public WherePartComposite
	                 addPart(WherePart part)
	{
		return this.addPart(null, part);
	}

	public String    lastPartName()
	{
		Map.Entry<String, WherePart> p = getLastPart();
		return (p != null)?(p.getKey()):(null);
	}

	public WherePartComposite
	                 addPart(String name, WherePart part)
	{
		//?: {do remove}
		if(part == null)
		{
			getParts().remove(name);
			return this;
		}

		//?: {the name of the part is not defined} poll the sequence
		if((name = s2s(name)) == null)
			name = genNextName();

		//~: put the part by the name
		getParts().put(name, part);

		//?: {the part is a composite} assign the prefix of the aggregated part
		if(part instanceof WherePartComposite)
			assignCompositePrefix((WherePartComposite)part, name);

		return this;
	}

	public boolean   isEmpty()
	{
		return getParts().isEmpty();
	}

	public WhereText addPart(String name, String query)
	{
		WhereText res;

		addPart(name, res = new WhereText(query));
		return res;
	}

	public WhereText addPart(String query)
	{
		return addPart(null, query);
	}

	public String    getGlobalPrefix()
	{
		return globalPrefix;
	}

	public void      setGlobalPrefix(String prefix)
	{
		this.globalPrefix = prefix = s2s(prefix);

		//~: create prefix base
		StringBuilder ps = new StringBuilder(8);
		int           pi;

		if(prefix != null)
			ps.append(prefix).append('_');
		pi = ps.length(); //<-- the fixed part of the prefix

		//c: for all composite parts
		for(Entry<String, WherePart> e : getParts().entrySet())
			if(e.getValue() instanceof WherePartComposite)
			{
				//~: append the part own global prefix
				ps.delete(pi, ps.length());
				ps.append(e.getKey()); //<-- local part

				//~: set it's global prefix
				((WherePartComposite)e.getValue()).
				  setGlobalPrefix(ps.toString());
			}
	}

	public void      collectParams(Map<String, Object> result)
	{
		collectOwnParams(result);
		collectAggregatedParams(result);
	}


	/* protected: composite structure support */

	protected String genNextName()
	{
		return Integer.toString(namesSequence++);
	}

	protected void   assignCompositePrefix(WherePartComposite part, String name)
	{
		String p = name;

		//HINT:  only the root where part has no global prefix.

		//?: {has global prefix} take it
		if(getGlobalPrefix() != null)
			p = new StringBuilder(getGlobalPrefix().length() + 1 + name.length()).
			  append(getGlobalPrefix()).append('_').append(name).toString();

		part.setGlobalPrefix(p);
	}

	protected Map<String, WherePart>
	                 getParts()
	{
		return (parts != null)?(parts):
		  (parts = new LinkedHashMap<String, WherePart>(7));
	}

	protected Entry<String, WherePart>
	                 getLastPart()
	{
		Iterator<Entry<String, WherePart>> i =
		  getParts().entrySet().iterator();
		Entry<String, WherePart>           r = null;

		while(i.hasNext())
			r = i.next();
		return r;
	}


	/* protected: composite parameters aggregation */

	protected void   collectOwnParams(Map<String, Object> result)
	{
		Map<String, String> names = //: own map name -> global map name
		  new HashMap<String, String>(getParams().size());

		//~: assign the names to the parameters owned by this composite
		nameOwnParams(names);

		//~: copy the parameters named to the result
		for(Entry<String, String> e : names.entrySet())
			result.put(e.getValue(), getParts().get(e.getKey()));
	}

	protected void   collectAggregatedParams(Map<String, Object> result)
	{
		collectCompositePartsParams(result);
		collectSimplePartsParams(result);
	}

	protected void   collectCompositePartsParams(Map<String, Object> result)
	{
		//c: collect parameters of aggregated composite parts
		for(Entry<String, WherePart> e : getParts().entrySet())
			//?: {it is a composite part} use it directly
			if(e.getValue() instanceof WherePartComposite)
				((WherePartComposite)e.getValue()).collectParams(result);
	}

	protected void   collectSimplePartsParams(Map<String, Object> result)
	{
		Map<String, String> names = new HashMap<String, String>(17);

		//c: collect parameters of aggregated simple parts
		for(Entry<String, WherePart> e : getParts().entrySet())
		{
			//?: {this is a composite part} skip it
			if(e.getValue() instanceof WherePartComposite)
				continue;

			Map<String, Object> params = e.getValue().getParams();

			//~: obtain the names of the parameters
			names.clear();
			nameSimplePartsParams(e.getKey(), names);

			//c: add the parameters mapped
			for(Entry<String, String> pe : names.entrySet())
				result.put(pe.getValue(), params.get(pe.getKey()));
		}
	}


	/* protected: composite parameters naming */

	/**
	 * Builds the names mapping of the own parameters.
	 *
	 * The key of the mapping is the name of the parameter
	 * in the (local) query text. The mapped value is the
	 * name of the parameter in the collected params.
	 */
	protected void   nameOwnParams(Map<String, String> names)
	{
		StringBuilder ps = new StringBuilder(8);

		//?: {has unique parameters prefix} add it 'as-is'
		if(getParamsPrefix() != null)
			ps.append(getParamsPrefix());
		//?: {has global composite prefix}
		else if(getGlobalPrefix() != null)
		{
			ps.append(getGlobalPrefix()).append('_');
			validatePrefix(ps);
		}

		int           pi = ps.length();

		//c: for all own parameters
		for(String p : getParams().keySet())
		{
			//~: add own global prefix
			ps.delete(pi, ps.length());
			ps.append(p);

			//put prefixed name
			names.put(p, ps.toString());
		}
	}

	/**
	 * Builds the name of the parameters of the part given.
	 *
	 * This method may be invoked on composite parts, but
	 * this is not needed as composite parts rename their
	 * parameters without assistance.
	 */
	protected void   nameSimplePartsParams(String part, Map<String, String> names)
	{
		WherePart     wp = getParts().get(part);
		if(wp == null) return;

		StringBuilder ps = new StringBuilder(8);
		int           pi;

		//?: {has global parameters prefix} set it 'as-is'
		if(s2s(wp.getParamsPrefix()) != null)
			ps.append(s2s(wp.getParamsPrefix()));
		else
		{
			if(getGlobalPrefix() != null)
				ps.append(getGlobalPrefix()).append('_');

			ps.append(part); //<-- local part name
			ps.append('_');

			validatePrefix(ps);
		}

		pi = ps.length();

		//c: for all the parameters of the part given
		for(String p : wp.getParams().keySet())
		{
			//~: prepare parameter key' prefix
			ps.delete(pi, ps.length());
			ps.append(p);

			//~:map prefixed
			names.put(p, ps.toString());
		}
	}

	protected void   validatePrefix(StringBuilder ps)
	{
		//?: {has no global prefix & part name starts with digit}
		if((ps.length() != 0) && !Character.isJavaIdentifierStart(ps.charAt(0)))
			ps.insert(0, 'x');
	}


	/**
	 * Replaces the original named parameters of the query
	 * with the names mapped. The original names are the
	 * keys of the mapping.
	 *
	 * Note that given implementation des no HQL parsing,
	 * and takes all the string with ':' as a candidate
	 * to named parameter.
	 */
	protected void renameQueryParams (
	                 StringBuilder       query,
	                 Map<String, String> names
	               )
	{
		StringBuilder ps = new StringBuilder(16);
		String        p;

		for(Entry<String, String> e : names.entrySet())
		{
			//~: form the expected parameter name
			p = ps.delete(0, ps.length()).
			  append(':').append(e.getKey()).toString();

			//c: scan the query for the parameter
			for(int i = query.indexOf(p, 0);(i != -1);)
			{
				query.replace(
				  i + 1, //<-- skip ':'
				  i + p.length(),
				  e.getValue());

				//~: search for the next occurrence
				i = query.indexOf(p, i + e.getValue().length());
			}
		}
	}


	/* private: aggregated parts */

	private Map<String, WherePart> parts;


	/* private: composite issues */

	private String globalPrefix;
	private int    namesSequence;
}