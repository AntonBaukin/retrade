package com.tverts.support;

/* standard Java classes */

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Strings helper functions.
 *
 * @author anton.baukin@gmail.com
 */
public class SU
{
	/* public: simplifications */

	/**
	 * Breaks string into words. The words
	 * are separated by ',' ';' characters.
	 */
	public static String[] s2a(String s)
	{
		if((s = s2s(s)) == null)
			return new String[0];

		String            sx;
		ArrayList<String> sa = new ArrayList<String>(
		  Arrays.asList(s.split("[,;]")
		));

		for(ListIterator<String> i = sa.listIterator();(i.hasNext());)
			if((sx = s2s(i.next())) != null)
				i.set(sx);
			else
				i.remove();

		if(sa.isEmpty())
			return new String[0];

		return sa.toArray(new String[sa.size()]);
	}

	/**
	 * Breaks string into words. The words
	 * are separated by blank characters.
	 */
	public static String[] s2aws(String s)
	{
		if((s = s2s(s)) == null)
			return new String[0];

		String            sx;
		ArrayList<String> sa = new ArrayList<String>(
		  Arrays.asList(s.split("\\s+")
		));

		for(ListIterator<String> i = sa.listIterator();(i.hasNext());)
			if((sx = s2s(i.next())) != null)
				i.set(sx);
			else
				i.remove();

		if(sa.isEmpty())
			return new String[0];

		return sa.toArray(new String[sa.size()]);
	}

	/**
	 * Splits the string by the separator given.
	 * Note that whitespaces are not trimmed here.
	 */
	public static String[] s2a(String s, char x)
	{
		if(s == null) return new String[0];

		ArrayList<String> a = new ArrayList<String>(4);

		for(int i, b = 0;(b < s.length());b = i + 1)
		{
			i = s.indexOf(x, b);
			if(i == -1) i = s.length();

			a.add(s.substring(b, i));
		}

		return a.toArray(new String[a.size()]);
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

	/**
	 * Returns an array of not-whitespace-strings
	 * collected from the array provided.
	 *
	 * Omits {@code null} strings. Returns always
	 * not {@code null}!
	 *
	 * A copy of the original array is always created.
	 */
	public static String[] a2a(String[] a)
	{
		if(a == null) return new String[0];

		String[] r;
		int      i = 0, l = 0;

		for(String s : a)
			if(!sXe(s)) l++;

		r = new String[l];
		for(String s : a)
			if(!sXe(s)) r[i++] = s2s(s);

		return r;
	}

	public static String   sXs(String s)
	{
		return (s != null)?(s):("");
	}

	public static int      sXl(Object... objs)
	{
		int size = 0;

		if(objs != null) for(Object s : objs)
			if(s instanceof CharSequence)
				size += ((CharSequence)s).length();

		return size;
	}

	public static int      sXl(String... strs)
	{
		int size = 0;

		if(strs != null) for(String s : strs)
			if(s != null)
				size += s.length();

		return size;
	}

	/**
	 * Returns the truncated string where the first
	 * character (letter) is turned to lower case.
	 *
	 * For {@code null} whitespace-empty strings
	 * returns {@code null}.
	 */
	public static String   sLo(String s)
	{
		if((s = s2s(s)) == null)
			return null;

		StringBuilder sb = new StringBuilder(s);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

	public static String   sFl(String s, int len)
	{
		if(s.length() >= len) return s;

		StringBuilder sb = new StringBuilder(len);
		sb.append(s);

		while(sb.length() < len)
			sb.append(' ');
		return sb.toString();
	}

	public static int      digit(char c)
	{
		return "0123456789".indexOf(c);
	}

	public static int      digit(CharSequence s, int i)
	{
		if((s == null) || (i >= s.length())) return -1;
		return digit(s.charAt(i));
	}

	public static char     digit(int i)
	{
		if((i < 0) | (i > 9)) return '\0';
		return "0123456789".charAt(i);
	}


	/* public: comparisons */

	/**
	 * Returns {@code true} if the string is {@code null},
	 * empty, or contains only whitespaces.
	 */
	public static boolean sXe(String s)
	{
		return (s == null) || (s.length() == 0) ||
		  (s.trim().length() == 0);
	}

	/**
	 * Compares the characters with ending position exclusive.
	 */
	public static boolean eq(CharSequence x, CharSequence y, int b, int e)
	{
		if((x == null) && (y == null)) return true;
		if((x == null) || (y == null)) return false;

		int xl = x.length(), yl = y.length();

		for(int i = b;(i < e);i++)
		{
			//?: {both are longer then the check segment}
			if((i >= xl) && (i >= yl))
				return true;

			if((i >= xl) || (i >= yl) || (x.charAt(i) != y.charAt(i)))
				return false;
		}
		return true;
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
	public static String formatCurrency
	  (BigDecimal n, String ts, StringBuilder sb)
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

	/* public: buffering */

	/**
	 * Concatenates the objects previously converted
	 * to strings. Handles {@code null} values just
	 * skipping them.
	 */
	public static CharSequence  cat(Object... objs)
	{
		int l = _cslen_(objs); if(l == 0) l = 32;

		//~: write to the buffer
		StringBuilder s = new StringBuilder(l);
		_csapnd_(objs, s);

		return s;
	}

	public static String        cats(Object... objs)
	{
		return cat(objs).toString();
	}

	public static String        scat(String sep, Object... objs)
	{
		StringBuilder s = new StringBuilder(
		  sep.length() * objs.length + sXl(objs));

		for(Object o : objs) if(o != null)
		{
			String x = o.toString().trim();
			if(x.length() == 0) continue;

			if(s.length() != 0)
				s.append(sep);
			s.append(x);
		}

		return s.toString();
	}

	/**
	 * Concatenates the strings using the separator given.
	 * Returns the resulting buffer.
	 *
	 * Omits {@code null} values and empty strings. Trims
	 * the strings before appending.
	 *
	 * If the income buffer is {@code null} creates
	 * the new one. Else, returns the given buffer.
	 *
	 * If the separator is {@code null}, an empty
	 * string is taken.
	 */
	public static StringBuilder cat
	  (StringBuilder buf, String sep, Collection<? extends CharSequence> a)
	{
		int l = 0, x = (buf == null)?(0):(buf.length());

		//?: {has no separator} take an empty string
		if(sep == null) sep = "";

		//0: define the resulting length
		for(CharSequence s : a) if(s != null)
			l += s.length() + sep.length();

		//2: ensure the buffer capacity
		if(buf == null)
			buf = new StringBuilder(l);
		else
			buf.ensureCapacity(x + l);

		//3: append the strings
		for(CharSequence s : a) if((s = s2s(s)) != null)
			buf.append((buf.length() != x)?(sep):("")).append(s);

		return buf;
	}

	/**
	 * Concatenates the strings given using {@code ", "}
	 * as the separator.
	 */
	public static String        a2s(CharSequence... strings)
	{
		return (strings ==  null)?(null):
		  cat(null, ", ", Arrays.asList(strings)).toString();
	}

	/**
	 * Concatenates the strings given using {@code ", "}
	 * as the separator.
	 */
	public static String        a2s(Collection<? extends CharSequence> strings)
	{
		return cat(null, ", ", strings).toString();
	}

	public static String        replace(String s, String t, Object r)
	{
		if((s == null) || (r == null) || (t == null) || (t.length() == 0))
			return s;

		return !s.contains(t)?(s):(s.replace(t, r.toString()));
	}

	public static DelayedString delay(Object... items)
	{
		return new DelayedString(items);
	}


	/* public static: delayed string */

	public static class DelayedString
	{
		/* public: constructor */

		public DelayedString(Object... items)
		{
			this.items = items;
		}

		/* public: Object interface */

		public String toString()
		{
			return (result != null)?(result):
			  (result = cat(items).toString());
		}


		/* private: delayed items + result */

		private Object[] items;
		private String   result;
	}


	/* private: helpers */

	private static int  _cslen_(Object cs)
	{
		if(cs instanceof CharSequence)
			return ((CharSequence)cs).length();

		int l = 0;

		if(cs instanceof Collection)
			for(Object s : (Collection)cs)
				l += _cslen_(s);
		else if(cs instanceof Object[])
			for(Object s : (Object[])cs)
				l += _cslen_(s);

		return l;
	}

	private static void _csapnd_(Object cs, StringBuilder sb)
	{
		if(cs instanceof CharSequence)
			sb.append((CharSequence)cs);
		else if(cs instanceof Collection)
			for(Object s : (Collection)cs)
				_csapnd_(s, sb);
		else if(cs instanceof Object[])
			for(Object s : (Object[])cs)
				_csapnd_(s, sb);
		else if(cs != null)
			sb.append(cs);
	}
}