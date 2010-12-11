package com.tverts.support;

/* standart Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Strings helper functions.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class SU
{
	/* public: simplifications */

	/**
	 * Breaks string into words. The words are separated
	 * with ' ', ',', ';' characters.
	 */
	public static String[] s2a(String s)
	{
		s = s2s(s); if(s == null) return new String[0];

		ArrayList<String> sa = new ArrayList<String>(Arrays.asList(
		  s.split("(\\s+|[,;])")
		));

		for(ListIterator<String> i = sa.listIterator();(i.hasNext());)
		{
			String sai = i.next();
			sai = (sai != null)?(sai.trim()):("");

			if(sai.length() != 0)
				i.set(sai);
			else
				i.remove();
		}

		return sa.toArray(new String[sa.size()]);
	}

	/**
	 * Checks whether the string is {@code null}
	 * or has only whitespaces: if so, returns
	 * {@code null}.
	 *
	 * Otherwise returns the string trimmed. 
	 */
	public static String   s2s(String s)
	{
		return (s == null)?(null):
		  ((s = s.trim()).length() != 0)?(s):(null);
	}

	public static String   s2s(CharSequence s)
	{
		return (s == null)?(null):(s2s(s.toString()));
	}

	public static int      sXl(String... strs)
	{
		int size = 0;

		if(strs != null) for(String s : strs)
			if(s != null)
				size += s.length();

		return size;
	}

	/* public: escape routines */

	/**
	 * Escapes string to place into Java Script
	 * source text. Note that XML entities
	 * are not encoded here, and you must
	 * protected XML text properly with CDATA
	 * sections.
	 */
	public static String   escapeJSString(Object sobj)
	{
		if(sobj == null) return null;

		CharSequence  s = (sobj instanceof CharSequence)
		  ?((CharSequence)sobj):(sobj.toString());
		StringBuilder b;
		int           l = s.length();
		int           e = 0;

		//count the number of escaped characters
		for(int i = 0;(i < l);i++)
			if(Arrays.binarySearch(JS_ESC_K, s.charAt(i)) >= 0)
				e++;

		//?: {no escape symbols found} return the original string
		if(e == 0) return s.toString();
		b = new StringBuilder(l + e);

		// encode the string

		for(int i = 0;(i < l);i++)
		{
			char c = s.charAt(i);

			//lookup the replacement
			e = Arrays.binarySearch(JS_ESC_K, c);

			//?: {replacement not found}
			if(e < 0) b.append(c);
			else b.append('\\').append(JS_ESC_V[e]); //<-- escape it
		}

		//~encode the string

		return b.toString();
	}

	private static char[] JS_ESC_K =
	  {'\'', '\"', '\\', '\t', '\n', '\r'};

	private static char[] JS_ESC_V =
	  {'\'', '\"', '\\', 't', 'n', 'r'};

	static
	{
		TreeMap<Character, Character> m =
		  new TreeMap<Character, Character>();

		for(int i = 0;(i < JS_ESC_K.length);i++)
			m.put(JS_ESC_K[i], JS_ESC_V[i]);

		int i = 0;
		for(Iterator it = m.entrySet().iterator();(it.hasNext());i++)
		{
			Entry e = (Entry)(it.next());
			JS_ESC_K[i] = (Character)e.getKey();
			JS_ESC_V[i] = (Character)e.getValue();
		}
	}

	public static String   escapeXML(Object sobj)
	{
		return (sobj == null)?(null)
		  :(escapeXML(sobj, null));
	}

	public static String   escapeXML(Object sobj, StringBuilder sb)
	{
		if(sobj == null) return null;

		CharSequence s = (sobj instanceof CharSequence)
		  ?((CharSequence)sobj):(sobj.toString());
		int          l = s.length();

		if(sb != null)
			sb.delete(0,sb.length());
		else
			sb = new StringBuilder(l*108/100);

		for(int j = 0;(j < l);j++)
		{
			char c = s.charAt(j);
			int  p = Arrays.binarySearch(XESC_SYMS,c);

			if(p < 0) sb.append(c);
			else      sb.append(XESC_REPL[p]);
		}

		return sb.toString();
	}
	
	private static char[]   XESC_SYMS = new char[]
	  {'<',    '>',    '\"',     '\'',     '&'};

	private static String[] XESC_REPL = new String[]
	  {"&lt;", "&gt;", "&quot;", "&#39;", "&amp;"};

	static
	{
		char[]   TXESC_SYMS = new char[XESC_SYMS.length];
		String[] TXESC_REPL = new String[XESC_REPL.length];

		//copy symbols & sort them
		System.arraycopy(XESC_SYMS, 0,
		  TXESC_SYMS, 0, XESC_SYMS.length);
		Arrays.sort(TXESC_SYMS);

		//fit replacement strings
		for(int i = 0;(i < XESC_SYMS.length); i++)
			TXESC_REPL[i] = XESC_REPL[
			  indexOf(XESC_SYMS, TXESC_SYMS[i])];

		//set the arrays
		XESC_SYMS = TXESC_SYMS;
		XESC_REPL = TXESC_REPL;
	}

	private static int indexOf(char[] chs, char ch)
	{
		for(int i = 0;(i < chs.length);i++)
			if(chs[i] == ch)
				return i;
		return -1;
	}

	/* public: formatting routines */

	/**
	 * Formats the currency value with fixed fraction part
	 * ('.xx') and thousands separator ('ts') given that
	 * may be {@code null}.
	 */
	public static String formatCurrency (
	                       BigDecimal    n,
	                       String        ts,
	                       StringBuilder sb
	                     )
	{
		boolean ng = (n.compareTo(BigDecimal.ZERO) < 0);
		if(ng) n = n.negate();

		//convert to text string
		int     bg = sb.length();
		sb.append(n.toString());

		// deal with ',' and '.'
		int     dp = sb.indexOf(",", bg);

		//?: {found ','}
		if(dp != -1)
			//?: {has NO other ','} ',' -> '.'
			if((sb.indexOf(",", dp+1) == -1) &&
			   (sb.indexOf(".", bg)   == -1)
			  )
				sb.setCharAt(dp, '.');
			//remove all ',' (thousands separators)
			else while(((dp = sb.indexOf(",", bg)) != -1))
				sb.deleteCharAt(dp);

		dp = sb.indexOf(".", bg); //<-- fraction point 
		//~deal with ',' and '.'

		//?: {is an integer} append ".00"
		if(dp == -1)
			sb.append(".00");
		//!: append trailing zeros
		else while(sb.length() < dp + 3)
			sb.append('0');

		//insert thousands spaces
		if(ts != null)
			for(dp -= 3;(dp > bg);dp -= 3)
				sb.insert(dp, ts);

		//?: {was negative}
		if(ng) sb.insert(bg, '-');

		return sb.toString();
	}

}