package com.tverts.support;

/* Java */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/* com.tverts: support */

import com.tverts.support.misc.CharArraySequence;


/**
 * Strings helper functions.
 *
 * @author anton.baukin@gmail.com
 */
public class SU
{
	/* Simplifications */

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

	@SuppressWarnings("unchecked")
	public static int      sXl(Object... objs)
	{
		int size = 0;

		if(objs != null) for(Object s : objs)
			if(s instanceof CharSequence)
				size += ((CharSequence)s).length();
			else if(s instanceof Object[])
				for(Object x : (Object[])s)
					size += sXl(x);
			else if(s instanceof Collection)
				for(Object x : (Collection)s)
					size += sXl(x);

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

	public static String   lenum(int len, long num)
	{
		String        n = Long.toString(num);
		StringBuilder s = new StringBuilder(
		  (len > n.length())?(len):(n.length()));

		for(int i = n.length();(i < len); i++)
			s.append('0');
		s.append(n);

		return s.toString();
	}

	public static char     first(CharSequence s)
	{
		return (s.length() == 0)?('\0'):(s.charAt(0));
	}

	public static char     last(CharSequence s)
	{
		int l = s.length();
		return (l == 0)?('\0'):(s.charAt(l - 1));
	}


	/* Comparisons */

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


	/* Escape Routines */

	/**
	 * Escapes string to place into Java Script
	 * source text. Note that XML entities
	 * are not encoded here, and you must
	 * protected XML text properly with CDATA
	 * sections.
	 */
	public static String  jss(Object sobj)
	{
		if(sobj == null) return "";

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

	public static String   camelize(String s)
	{
		if(s == null) return null;

		//?: {has no dash}
		if(s.indexOf('-') == -1)
			return s;

		//~: seek and destroy...
		StringBuilder x = new StringBuilder(s.length());
		boolean       d = false; //<-- true when had dash

		for(int i = 0;(i < s.length());i++)
		{
			char c = s.charAt(i);

			if(c == '-')
			{
				d = true;
				continue;
			}

			if(d) c = Character.toUpperCase(c);
			d = false;

			x.append(c);
		}

		return x.toString();
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


	/* Formatting Routines */

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

	/**
	 * Converts string to be a database sort value.
	 * Removes all the symbols except alpha-numeric
	 * and ';,.-'. Replaces all multiple blanks
	 * with single space ' '.
	 */
	public static String sort(String s)
	{
		return sXe(s)?(null):s.toLowerCase().
		  replaceAll("\\s+", " ").
		  replaceAll("[^а-я\\w;,\\.\\- ]", "");
	}


	/* Buffering */

	/**
	 * Concatenates the objects previously converted
	 * to strings. Handles {@code null} values just
	 * skipping them.
	 */
	public static StringBuilder cat(Object... objs)
	{
		StringBuilder s = new StringBuilder(sXl(objs));
		scat(s, "", Arrays.asList(objs));
		return s;
	}

	public static String        cats(Object... objs)
	{
		return cat(objs).toString();
	}

	@SuppressWarnings("unchecked")
	public static String        scats(String sep, Object... objs)
	{
		StringBuilder s = new StringBuilder(
		  sep.length() * objs.length + sXl(objs));

		scat(s, sep, Arrays.asList(objs));
		return s.toString();
	}

	public static StringBuilder scat(StringBuilder s, String sep, Object... objs)
	{
		if(s == null) s = new StringBuilder(
		  sep.length() * objs.length + sXl(objs));

		scat(s, sep, Arrays.asList(objs));
		return s;
	}

	public static void          scat(StringBuilder s, String sep, Collection objs)
	{
		_scat(s, s.length(), sep, objs);
	}

	@SuppressWarnings("unchecked")
	private static void         _scat
	  (StringBuilder s, int l, String sep, Collection objs)
	{
		for(Object o : objs) if(o != null)
		{
			if(o instanceof Object[])
				o = Arrays.asList((Object[])o);

			if(o instanceof Collection)
			{
				_scat(s, l, sep, (Collection)o);
				continue;
			}

			if(s.length() != l)
				s.append(sep);
			s.append(o);
		}
	}

	public static String        catif(Object test, Object... objs)
	{
		if(test == null) return "";

		if((test instanceof String) && sXe((String) test))
			return "";

		if((test instanceof Boolean) && Boolean.FALSE.equals(test))
			return "";

		return cats(objs);
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

	public static StringBuilder cat
	  (StringBuilder buf, String sep, CharSequence... a)
	{
		return cat(buf, sep, Arrays.asList(a));
	}

	/**
	 * Concatenation for Ox-Search texts.
	 */
	public static String        catx(Object... objs)
	{
		StringBuilder s = scat(null, "\f", objs);
		int l = s.length(); if(l == 0) return null;

		//~: turn to lower-case
		for(int i = 0;(i < s.length());i++)
			s.setCharAt(i, Character.toLowerCase(s.charAt(i)));

		String x = s.toString();

		//~: replace spaces
		x = x.replaceAll("\\s+", " ");

		//~: replace wrong symbols
		return x.replaceAll("[^\\s\\w\\-\\+\\.а-я]", "");
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

	public static String        a2s(String[] strings)
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


	/* Delayed String */

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


	/* Hexadecimal Strings */

	public static String  i2h(int n)
	{
		char[] s = new char[9];
		int    m = 0; if(n < 0) { m = 1; n = -n; s[0] = '-'; }

		//~: translate
		for(int i = m, j = 28;(j >= 0);j -= 4, i++)
			s[i] = BYTES2HEX[ (n >>> j) & 0x0F ];

		//~: index leading zeros
		int x; for(x = m;(x < 9);x++)
			if(s[x] != '0') break;
		x -= m;

		//~: move to that positions
		if(x != 0) for(int i = m;(i + x < 9);i++)
			s[i] = s[i + x];

		//~: the valuable length
		int l = 9 - (1 - m) - x;
		if(l == 0) { s[0] = '0'; l = 1; }

		return new String(s, 0, l);
	}

	public static String  i2h(long n)
	{
		char[] s = new char[17];
		int    m = 0; if(n < 0) { m = 1; n = -n; s[0] = '-'; }

		//~: translate
		for(int i = m, j = 60;(j >= 0);j -= 4, i++)
			s[i] = BYTES2HEX[ (int)(n >>> j) & 0x0F ];

		//~: index leading zeros
		int x; for(x = m;(x < 17);x++)
			if(s[x] != '0') break;
		x -= m;

		//~: move to that positions
		if(x != 0) for(int i = m;(i + x < 17);i++)
			s[i] = s[i + x];

		//~: the valuable length
		int l = 17 - (1 - m) - x;
		if(l == 0) { s[0] = '0'; l = 1; }

		return new String(s, 0, l);
	}

	public static char[]  bytes2hex(byte[] a)
	{
		if(a == null) return null;

		char[] c = new char[a.length * 2];

		for(int i = 0, j = 0;(i < a.length);i++, j += 2)
		{
			int b = a[i];

			//HINT: higher comes first!

			c[j    ] = BYTES2HEX[ (b & 0xF0) >> 4 ];
			c[j + 1] = BYTES2HEX[ (b & 0x0F)      ];
		}

		return c;
	}

	public static void    bytes2hex(byte[] a, StringBuilder sb, int offset)
	{
		if(a == null) return;
		sb.ensureCapacity(offset + a.length*2);

		for(int i = 0, j = 0;(i < a.length);i++, j += 2)
		{
			int  b = a[i];
			char h = BYTES2HEX[ (b & 0xF0) >> 4 ];
			char l = BYTES2HEX[ (b & 0x0F)      ];

			//HINT: higher comes first!

			if(j >= sb.length())
				sb.append(h).append(l);
			else
			{
				sb.setCharAt(j, h);

				if(j + 1 >= sb.length())
					sb.append(l);
				else
					sb.setCharAt(j + 1, l);
			}
		}
	}

	private static char[] BYTES2HEX =
	  "0123456789ABCDEF".toCharArray();


	public static byte[]  hex2bytes(char[] c)
	{
		return hex2bytes(new CharArraySequence(c));
	}

	public static byte[]  hex2bytes(CharSequence c)
	{
		byte[] a = new byte[c.length() / 2];
		int    l = 0, h = 16;

		for(int i = 0;(i < c.length());i++)
		{
			int x = (int)c.charAt(i);

			//?: {not ASCII character}
			if((x & 0xFF00) != 0)
				continue;

			int b = HEX2BYTES[x];

			//?: {not a HEX character}
			if(b == 16)
				continue;

			//HINT: higher comes first!

			//?: {higher is not set yet}
			if(h == 16)
			{
				h = b;
				continue;
			}

			//HINT: 'b' is a lower part here...
			a[l++] = (byte)(b | (h << 4));
			h = 16;
		}

		//?: {resulting array is longer}
		if(l != a.length)
		{
			byte[] a2 = new byte[l];
			System.arraycopy(a, 0, a2, 0, l);
			a = a2;
		}

		return a;
	}

	private static byte[] HEX2BYTES =
	  new byte[256]; //<-- values are: 0 .. 15, 16

	static
	{
		char[] hex = "0123456789abcdef".toCharArray();
		char[] HEX = "0123456789ABCDEF".toCharArray();


		for(int i = 0;(i < 256);i++)
			HEX2BYTES[i] = 16;

		for(byte j = 0;(j < 16);j++)
		{
			HEX2BYTES[((int)hex[j]) & 0xFF] = j;
			HEX2BYTES[((int)HEX[j]) & 0xFF] = j;
		}
	}


	/* URL Encoding */

	public static String urle(String s)
	{
		if(s == null) return null;

		try
		{
			return URLEncoder.encode(s, "UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String urld(String s)
	{
		if(s == null) return null;

		try
		{
			return URLDecoder.decode(s, "UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/* String to Value Translations */

	public static Object s2v(Class t, String s)
	{
		if(t == null) throw new IllegalArgumentException();
		if(sXe(s))    return null;

		S2V s2v = S2V_MAP.get(t);
		if(s2v == null) throw new IllegalStateException(String.format(
		  "Don't know how to convert to type [%s] string [%s]!",
		  t.getName(), s
		));

		try
		{
			return s2v.s2v(t, s2s(s));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static interface S2V
	{
		public Object s2v(Class t, String s)
		  throws Exception;
	}

	public static final Map<Class, S2V> S2V_MAP;

	private static final Object[] S2VS = new Object[]
	{
	  //strings
	  CharSequence.class, String.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return s;
		  }
	  },

	  //integers
	  int.class, Integer.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Integer.valueOf(s);
		  }
	  },

	  //longs
	  long.class, Long.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Long.valueOf(s);
		  }
	  },

	  //floats
	  float.class, Float.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Float.valueOf(s);
		  }
	  },

	  //doubles
	  double.class, Double.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Double.valueOf(s);
		  }
	  },

	  //boolean
	  boolean.class, Boolean.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Boolean.valueOf(s);
		  }
	  },

	  //decimals
	  BigDecimal.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return new BigDecimal(s);
		  }
	  },

	  //big integers
	  BigInteger.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return new BigInteger(s);
		  }
	  },

	  //short integers
	  short.class, Short.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Short.valueOf(s);
		  }
	  },

	  //bytes
	  byte.class, Byte.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				return Byte.valueOf(s);
		  }
	  },

	  //characters
	  char.class, Character.class,

	  new S2V()
	  {
		  public Object s2v(Class t, String s)
		    throws Exception
		  {
				if(s.length() != 1)
					throw new IllegalArgumentException(String.format(
					  "String [%s] is too long to be character!", s
					));
				return s.charAt(0);
		  }
	  }
	};

	static
	{
		HashMap<Class, S2V> map =
		  new HashMap<Class, S2V>(11);

		ArrayList<Class>    cls =
		  new ArrayList<Class>(2);

		for(Object o : S2VS)
			if(o instanceof Class)
				cls.add((Class)o);
			else if(o instanceof S2V)
			{
				if(cls.isEmpty())
					throw new IllegalStateException();

				for(Class c : cls)
					map.put(c, (S2V)o);
				cls.clear();
			}
			else
				throw new IllegalStateException();

		if(!cls.isEmpty())
			throw new IllegalStateException();

		S2V_MAP = Collections.unmodifiableMap(map);
	}
}