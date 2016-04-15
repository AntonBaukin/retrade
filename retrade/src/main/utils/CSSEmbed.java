/* Java */

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes input CSS file and the base URL
 * from where it was loaded and replaces
 * each URL with Base64 encoded content.
 *
 * The implementation is self-sufficient!
 * No additional class files are required.
 *
 * @author anton.baukin@gmail.com.
 */
public class CSSEmbed
{
	public static void main(String[] argv)
	  throws Exception
	{
		if(argv.length != 2)
		{
			ou.println("Usage: java CSSEmbed css-file base-url");
			ou.println("\tReplaces each external url-resource");
			ou.println("\twith Base-64 encoded content.");
			return;
		}

		css = loadFile(argv[0]);

		base = argv[1];
		if(!base.endsWith("/")) base += "/";
		ou.printf("Using base URL: %s\n", base);

		scan = scanFile();

		scan.forEach(CSSEmbed::detectType);
		scan.forEach(CSSEmbed::loadResource);
		scan.forEach(CSSEmbed::formSubstitute);

		String result = substAllScans();
		saveFile(argv[0] + ".out", result);
	}

	static PrintStream ou = System.out;
	static PrintStream er = System.err;

	static String css;
	static String base;
	static List<Object[]> scan;

	static String loadFile(String name)
	  throws Exception
	{
		ByteArrayOutputStream o = new ByteArrayOutputStream(256 * 1024);
		byte[] b = new byte[2048];

		try(FileInputStream i = new FileInputStream(name))
		{
			int s; while((s = i.read(b)) > 0)
				o.write(b, 0, s);
		}

		byte[] bytes = o.toByteArray();
		ou.printf("Loaded source CSS file having %dKb\n", bytes.length/1024);

		return new String(bytes, "UTF-8");
	}

	/**
	 * [0] full URL to load the resource;
	 * [1] string CSS-type of the resource;
	 * [2] start position in the original file;
	 * [3] excluding-end position in the file;
	 * [4] bytes of the loaded resource in Base64;
	 * [5] URL substitution string.
	 */
	static List<Object[]> scanFile()
	  throws Exception
	{
		char[] QS  = new char[]{ '\'', '\"' };
		char[] QSE = new char[]{ '\'', '\"', ')' };

		List<Object[]> res = new ArrayList<>();

		int i = 0; while(i < css.length())
		{
			int b = css.indexOf("url", i);
			if(b == -1) break;
			b += "url".length();

			b = skipWs(css, b);
			if(b == -1) break;

			i = b + 1;
			if(css.charAt(b++) != '(')
				continue;

			int x = skipWs(css, b);
			if(x == -1) break;

			x = skipCh(css, x, QS);
			if(x == -1) break;

			int e = skipNotCh(css, x, QSE);
			if(e == -1) break;

			e = skipWs(css, e);
			if(e == -1) break;

			e = skipCh(css, e, QS);
			if(e == -1) break;

			e = skipWs(css, e);
			if(e == -1) break;

			i = e + 1;
			if(css.charAt(e) != ')') continue;

			String p = css.substring(x, e).trim();
			p = strip(p, QS).trim();
			i = e + 1;
			if(p.isEmpty()) continue;

			if(p.startsWith("data:")) continue;
			if(p.startsWith("/")) p = p.substring(1);
			URI url = new URL(base + p).toURI().normalize();

			Object[] r = new Object[6];
			res.add(r);

			r[0] = url.toURL();
			r[2] = b;
			r[3] = e;
		}

		return res;
	}

	static boolean in(char c, char[] cs)
	{
		for(int i = 0;(i < cs.length);i++)
			if(cs[i] == c) return true;
		return false;
	}

	static int skipWs(String s, int i)
	{
		for(;(i < s.length());i++)
			if(!Character.isWhitespace(css.charAt(i)))
				break;
		return (i == css.length())?(-1):(i);
	}

	static int skipCh(String s, int i, char[] chs)
	{
		for(;(i < s.length());i++)
			if(!in(css.charAt(i), chs))
				break;
		return (i == css.length())?(-1):(i);
	}

	static int skipNotCh(String s, int i, char[] chs)
	{
		for(;(i < s.length());i++)
			if(in(css.charAt(i), chs))
				break;
		return (i == css.length())?(-1):(i);
	}

	static String strip(String s, char[] chs)
	{
		int b, e;
		for(b = 0;(b < s.length());b++)
			if(!in(s.charAt(b), chs))
				break;
		for(e = s.length() - 1;(e > b);e--)
			if(!in(s.charAt(e), chs))
				break;
		e++;

		if((b == 0) && (e == s.length())) return s;
		return (b >= e)?(""):(s.substring(b, e));
	}

	static void detectType(Object[] r)
	{
		//--> search for @font-face
		final String FF = "@font-face";
		final char[]  B = new char[]{ '{' };

		int i = (Integer) r[2];
		i = unskipNotCh(css, i, B);
		i = unskipWs(css, i);
		if(i == -1) return;

		if(endsWith(css, i, FF))
			{ r[1] = FF; return; }

		//--> search for background, background-image
		final String BG = "background";
		final String BI = "background-image";
		final char[] BC = new char[]{ '{', ':' };

		i = (Integer) r[2];
		i = unskipNotCh(css, i, BC);
		i = unskipWs(css, i);

		if(endsWith(css, i, BG) || endsWith(css, i, BI))
			{ r[1] = BG; return; }

		er.printf("URL entry is neither font, nor background: %s\n", r[0].toString());
	}

	static int unskipWs(String s, int i)
	{
		for(;(i >= 0);i--)
			if(!Character.isWhitespace(css.charAt(i)))
				break;
		return i;
	}

	static int unskipNotCh(String s, int i, char[] chs)
	{
		for(;(i >= 0);i--)
			if(in(css.charAt(i), chs))
				break;
		return i;
	}

	static boolean endsWith(String s, int e, String x)
	{
		return (e >= x.length()) &&
		  x.equals(s.substring(e - x.length(), e));
	}

	static Map<URL, String> loaded = new HashMap<>();

	static void loadResource(Object[] r)
	{
		URL url = (URL) r[0];

		if(loaded.containsKey(url))
		{
			r[4] = loaded.get(url);
			return;
		}

		ou.printf("Loading %s...\n", url.toString());

		ByteArrayOutputStream o = new ByteArrayOutputStream(32 * 1024);
		try(InputStream i = url.openStream())
		{
			byte b[] = new byte[1024];
			int s; while((s = i.read(b)) > 0)
				o.write(b, 0, s);
		}
		catch(Throwable e)
		{
			er.printf("Couldn't load resource: %s!\n", url.toString());
			loaded.put(url, null);
			return;
		}

		byte[] bytes = o.toByteArray();
		ou.printf("\tdone bytes: %d\n", bytes.length);

		String b64 = Base64.getEncoder().encodeToString(bytes);
		loaded.put(url, b64);
		r[4] = b64;
	}

	static void formSubstitute(Object[] r)
	{
		if(r[4] == null)
			return;

		if("@font-face".equals(r[1]))
			substFont(r);

		if("background".equals(r[1]))
			substImage(r);
	}

	static void substFont(Object[] r)
	{
		String m, s = fileSuffix((URL) r[0]);

		if("woff".equals(s))
			m = "application/x-font-woff";
		else if("woff2".equals(s))
			m = "application/font-woff2";
		else if("eot".equals(s))
			m = "application/vnd.ms-fontobject";
		else if("ttf".equals(s))
			m = "application/font-ttf";
		else if("otf".equals(s))
			m = "application/font-otf";
		else if("svg".equals(s))
			return;
		else
		{
			er.printf("Unknown font format for: %s\n", r[0].toString());
			return;
		}

		r[5] = "data:" + m + ";base64," + (String)r[4];
	}

	static void substImage(Object[] r)
	{
		String m, s = fileSuffix((URL) r[0]);

		if("gif".equals(s))
			m = "image/gif";
		else if("jpeg".equals(s))
			m = "image/jpeg";
		else if("jpg".equals(s))
			m = "image/jpeg";
		else if("png".equals(s))
			m = "image/png";
		else if("svg".equals(s))
			m = "image/svg+xml";
		else
		{
			er.printf("Unknown image format for: %s\n", r[0].toString());
			return;
		}

		r[5] = "data:" + m + ";base64," + (String)r[4];
	}

	static String fileSuffix(URL url)
	{
		String s = url.getFile();
		int    i = s.lastIndexOf('/');
		if(i != -1) s = s.substring(i + 1);

		i = s.indexOf('?');
		if(i != -1) s = s.substring(0, i);

		i = s.lastIndexOf('.');
		if(i == -1) return "";
		s = s.substring(i + 1).toLowerCase();

		return s;
	}

	static String substAllScans()
	{
		//~: will replace starting from the end
		Collections.sort(scan, (Object[] l, Object[] r) ->
			-Integer.compare((Integer)l[2], (Integer)r[2])
		);

		StringBuilder xss = new StringBuilder(css);
		for(Object[] r : scan)
		{
			if(r[5] == null) continue;

			int b = (Integer)r[2];
			int e = (Integer)r[3];

			xss.delete(b, e);
			xss.insert(b, (String) r[5]);
		}

		return xss.toString();
	}

	static void saveFile(String file, String text)
	  throws Exception
	{
		try(FileOutputStream o = new FileOutputStream(file))
		{
			byte[] bytes = text.getBytes("UTF-8");

			ou.printf("Saving %dKb to file: %s\n", bytes.length/1024, file);
			o.write(bytes);
		}
	}
}