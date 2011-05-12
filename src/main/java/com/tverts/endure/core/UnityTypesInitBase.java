package com.tverts.endure.core;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import static com.tverts.support.SU.s2a;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;

/**
 * Support class for {@link UnityType} initializer.
 *
 * It parsers the strings encoding the unity types
 * of the following format:
 * <tt>
 *
 *   [ALE] class-name type-name
 *
 * </tt>
 *
 * First comes optional character of the type flag.
 * 'A' means attribute type, 'L' means link type,
 * 'E' means entity type.
 *
 * Class name is a FQN of the class, or a simple
 * name.
 *
 * Type name is any string without the entries separator.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityTypesInitBase
{
	/* protected: parse entry structure */

	protected static class ParseEntry
	{
		/* parsed properties */

		/**
		 * FQN of the class, may be null.
		 */
		public String    className;

		/**
		 * Simple name of the class. Defined only if
		 * {@link #className} is not.
		 */
		public String    simpleName;

		public String    typeName;

		/**
		 * Defined if was in the entry.
		 */
		public Character typeFlag;


		/* supplied properties */

		/**
		 * Unity type class.
		 */
		public Class     typeClass;

		/**
		 * Unity type created or loaded from the database.
		 */
		public UnityType unityType;
	}

	/* protected: entry parsing */

	protected static final String TYPE_FLAGS = "ALE";

	/**
	 * Parses the given entry string and writes the results
	 * in the entry structure. Returns {@code false} in the
	 * case of the format error.
	 */
	protected boolean    parseEntry(String es, ParseEntry pe)
	{
		if(sXe(es)) return false;

		//~: first space
		int fw = es.indexOf(' ');
		if(fw == -1) return false;

		//~: skip following whitespaces
		while(Character.isWhitespace(es.charAt(fw)) && (fw < es.length()))
			fw++;

		//~: second space
		int sw = es.indexOf(' ', fw + 1);
		if(sw == -1) sw = es.length();

		//~: first, second string
		String fs = s2s(es.substring(0, fw));
		String ss = s2s(es.substring(fw, sw));
		String sx = s2s(es.substring(fw)); //<-- whole tail

		//!: at least two strings must be
		if((fs == null) || (ss == null))
			return false;

		//~: third string (optional)
		String ts = null;

		if(sw < es.length())
			ts = s2s(es.substring(sw));

		//?: {first string is a key character}
		if((fs.length() == 1) && (TYPE_FLAGS.indexOf(fs) != -1))
			pe.typeFlag = fs.charAt(0); //<-- this is not final
		else
			pe.typeFlag = null;

		//?: {have only two strings defined}
		if(ts == null)
		{
			pe.simpleName = fs;
			pe.typeName   = ss;
			pe.typeFlag   = null; //<-- assume one character class name
		}

		//?: {have three strings and the type flag} precious match
		if((ts != null) && (pe.typeFlag != null))
		{
			pe.simpleName = ss;
			pe.typeName   = ts;
		}

		//?: {have three strings without the type flag}
		if((ts != null) && (pe.typeFlag == null))
		{
			pe.simpleName = fs;
			pe.typeName   = sx; //<-- just have a space in the type name
		}


		//~: done parsing, process the name ...

		//?: {FQN}
		int fqndot = pe.simpleName.lastIndexOf('.');

		if(fqndot != -1)
		{
			pe.className  = pe.simpleName;
			pe.simpleName = pe.simpleName.substring(fqndot + 1);
		}

		return true;
	}

	/* protected: entries list parsing */

	protected ParseEntry createEntry()
	{
		return new ParseEntry();
	}

	protected String[]   splitWhole(String whole)
	{
		return s2a(whole, ';');
	}

	/**
	 * Parses the string with the entries encoded invoking
	 * the handle* callbacks.
	 */
	protected List<ParseEntry>
	                     parseWhole(String whole)
	{
		ArrayList<ParseEntry> res = new ArrayList<ParseEntry>(8);

		for(String es : splitWhole(whole))
		{
			if((es = s2s(es)) == null)
				continue;

			ParseEntry pe = createEntry();

			if(parseEntry(es, pe))
			{
				handleEntry(pe);
				res.add(pe);
			}
			else
				handleError(es);
		}

		return res;
	}

	protected void       handleEntry(ParseEntry pe)
	{
		//?: {has full class name} load the class
		if((pe.typeClass == null) && (pe.className != null)) try
		{
			pe.typeClass = Thread.currentThread().
			  getContextClassLoader().loadClass(pe.className);
		}
		catch(Exception e)
		{
			throw new IllegalStateException(String.format(
			  "Can't register Unity Type '%s' as class '%s' was not found!",
			  pe.typeName, pe.className
			));
		}
	}

	protected void       handleError(String es)
	{
		throw new UnknownFormatConversionException(es);
	}

	/* protected: unity types registration */

	protected void       registerUnityTypes(List<ParseEntry> pes)
	{
		Map<Class, Map<String, UnityType>> types0 =
		  new HashMap<Class, Map<String, UnityType>>(pes.size());

		//~: collect all unity types in two-layer map
		for(ParseEntry pe : pes)
		{
			if(pe.unityType == null)
				continue;

			Map<String, UnityType> types1 =
			  types0.get(pe.unityType.getTypeClass());

			if(types1 == null)
				types0.put(pe.unityType.getTypeClass(),
				  types1 = new HashMap<String, UnityType>(5));

			types1.put(pe.unityType.getTypeName(), pe.unityType);
		}

		//~: register them
		for(Map.Entry<Class, Map<String, UnityType>> e0 : types0.entrySet())
			UnityTypes.getInstance().setTypes(e0.getKey(), e0.getValue());
	}
}