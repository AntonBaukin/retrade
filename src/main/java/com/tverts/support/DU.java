package com.tverts.support;

/* standard Java classes */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Various date utils.
 *
 * @author anton.baukin@gmail.com
 */
public class DU
{
	/* arithmetic */

	public static int    diffDays(Date one, Date two)
	{
		return diffDays(one, two, null);
	}

	public static int    diffDays(Date one, Date two, Calendar cl)
	{
		Date x, X;
		int  y, Y;
		int  d, D;
		int  r = 0;

		if(one.after(two)) {x = two; X = one;}
		else               {x = one; X = two;}

		if(cl == null) cl = Calendar.getInstance();
		cl.setTime(x);
		y = cl.get(Calendar.YEAR);
		d = cl.get(Calendar.DAY_OF_YEAR);
		cl.setTime(X);
		Y = cl.get(Calendar.YEAR);
		D = cl.get(Calendar.DAY_OF_YEAR);

		for(;(y != Y);y++)
		{
			r += 365;
			if(((GregorianCalendar)cl).isLeapYear(y))
				r++;
		}

		return r + D - d;
	}

	public static Date   addDays(Date d, int days)
	{
		Calendar cl = Calendar.getInstance();

		cl.setTime(d);
		cl.add(Calendar.DAY_OF_YEAR, days);
		return cl.getTime();
	}

	public static Date   addDaysClean(Date d, int days)
	{
		return cleanTime(addDays(d, days));
	}


	/* helpers */

	public static Date   cleanTime(Date d)
	{
		return cleanTime(d, null);
	}

	/**
	 * Returns the timestamp of the day
	 * having all time parts set to zeros.
	 */
	public static Date   cleanTime(Date d, Calendar cl)
	{
		if(d == null) return null;

		//~: init the calendar
		if(cl == null) cl = Calendar.getInstance();
		cl.setTime(d);

		//clear the time parts
		cl.set(Calendar.HOUR_OF_DAY, 0);
		cl.set(Calendar.MINUTE,      0);
		cl.set(Calendar.SECOND,      0);
		cl.set(Calendar.MILLISECOND, 0);

		return cl.getTime();
	}

	/**
	 * Returns the timestamp of the day
	 * being the last its moment.
	 */
	public static Date   lastTime(Date d)
	{
		return lastTime(d, null);
	}

	public static Date   lastTime(Date d, Calendar cl)
	{
		if(d == null) return null;

		//~: init the calendar
		if(cl == null) cl = Calendar.getInstance();
		cl.setTime(d);

		//clear the time parts
		cl.set(Calendar.HOUR_OF_DAY, 23);
		cl.set(Calendar.MINUTE,      59);
		cl.set(Calendar.SECOND,      59);
		cl.set(Calendar.MILLISECOND, 999);

		return cl.getTime();
	}

	public static Date   merge(Date d, Date t)
	{
		return merge(d, t, null);
	}

	public static Date   merge(Date d, Date t, Calendar cl)
	{
		if(d == null) return t;
		if(t == null) return d;

		if(cl == null) cl = Calendar.getInstance();

		cl.setTime(t);

		int hh = cl.get(Calendar.HOUR_OF_DAY);
		int mm = cl.get(Calendar.MINUTE);
		int ss = cl.get(Calendar.SECOND);
		int ms = cl.get(Calendar.MILLISECOND);

		cl.setTime(d);

		cl.set(Calendar.HOUR_OF_DAY,  hh);
		cl.set(Calendar.MINUTE,       mm);
		cl.set(Calendar.SECOND,       ss);
		cl.set(Calendar.MILLISECOND,  ms);

		return cl.getTime();
	}


	/* formatting */

	public static String date2str(Date d)
	{
		StringBuilder sb = new StringBuilder(10);
		date2str(d, sb, null);
		return sb.toString();
	}

	public static void   date2str(Date d, StringBuilder sb, Calendar cl)
	{
		if(cl == null) cl =  Calendar.getInstance();
		cl.setTime(d);

		//day
		lennum(cl.get(Calendar.DAY_OF_MONTH), 2, sb);
		sb.append('.');
		//month
		lennum(cl.get(Calendar.MONTH) + 1, 2, sb);
		sb.append('.');
		//year
		lennum(cl.get(Calendar.YEAR), 4, sb);
	}

	public static String time2str(Date t)
	{
		StringBuilder sb = new StringBuilder(5);
		time2str(t, sb, null);
		return sb.toString();
	}

	public static String monthAbbr(Date d)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(d);

		return new StringBuilder(8).
		  append(MONTHS_ABBR_RU[cl.get(Calendar.MONTH)]).
		  append(' ').append(cl.get(Calendar.YEAR)).
		  toString();
	}

	public static void   time2str(Date t, StringBuilder sb, Calendar cl)
	{
		if(cl == null) cl =  Calendar.getInstance();
		cl.setTime(t);

		//hour
		lennum(cl.get(Calendar.HOUR_OF_DAY), 2, sb);
		sb.append(':');
		//minute
		lennum(cl.get(Calendar.MINUTE), 2, sb);
	}

	public static String datetime2str(Date d)
	{
		if(d == null) return null;

		StringBuilder sb = new StringBuilder(14);
		Calendar      cl = Calendar.getInstance();

		date2str(d, sb, cl);
		sb.append(' ');
		time2str(d, sb, cl);

		return sb.toString();
	}

	public static Date   str2date(String s)
	{
		return str2date(s, null);
	}

	public static Date   str2date(String s, Calendar cl)
	{
		if((s = SU.s2s(s)) == null) return null;

		int d0 = s.indexOf('.');
		if(d0 == -1) throw new IllegalArgumentException();

		int d1 = s.indexOf('.', d0 + 1);
		if(d1 == -1) throw new IllegalArgumentException();

		int dd = Integer.parseInt(s.substring(0,      d0));
		int mm = Integer.parseInt(s.substring(d0 + 1, d1));
		int yy = Integer.parseInt(s.substring(d1 + 1    ));

		if(cl == null) cl = Calendar.getInstance();
		cl.clear();
		cl.set(Calendar.DAY_OF_MONTH, dd);
		cl.set(Calendar.MONTH,        mm - 1);
		cl.set(Calendar.YEAR,         yy);

		return cl.getTime();
	}

	public static Date   str2time(String s)
	{
		return str2time(s, null);
	}

	/**
	 * The format of the time supported is 24 hours timestamp
	 * HH:mm[:SS[.sss]] with option seconds and milliseconds.
	 */
	public static Date   str2time(String s, Calendar cl)
	{
		if((s = SU.s2s(s)) == null) return null;

		int sl = s.length();
		int c0 = s.indexOf(':');
		if(c0 == -1) throw new IllegalArgumentException();

		int c1 = s.indexOf(':', c0 + 1);
		if(c1 == -1) c1 = sl;

		int d2 = (c1 == sl)?(-1):(s.indexOf('.', c1 + 1));
		if(d2 == -1) d2 = sl;

		int hh = Integer.parseInt(s.substring(0,      c0));
		int mm = Integer.parseInt(s.substring(c0 + 1, c1));
		int ss = (c1 == sl)?(0):
		  Integer.parseInt(s.substring(c1 + 1,  d2));
		int ms = (d2 == sl)?(0):
		  Integer.parseInt(s.substring(d2 + 1,  sl));

		if(cl == null) cl = Calendar.getInstance();
		cl.clear();
		cl.set(Calendar.HOUR_OF_DAY, hh);
		cl.set(Calendar.MINUTE,      mm);
		cl.set(Calendar.SECOND,      ss);
		cl.set(Calendar.MILLISECOND, ms);

		return cl.getTime();
	}

	public static void   lennum(int num, int len, StringBuilder sb)
	{
		String str = Integer.toString(num);

		for(int i = str.length(); (i < len); i++)
			sb.append('0');
		sb.append(str);
	}

	/**
	 * Writes month as a name. First word is a week day name.
	 * Parameter 'ws' is used to insert whitespace string.
	 */
	public static void   namedDateTimeToStrRu
	  (Date dt, StringBuilder sb, String ws, Calendar cl)
	{
		if(cl == null) cl =  Calendar.getInstance();
		cl.setTime(dt);

		//week day
		sb.append(WEEKDAYS_RU[cl.get(Calendar.DAY_OF_WEEK)]).
		   append(',').append(ws);

		//day
		lennum(cl.get(Calendar.DAY_OF_MONTH), 2, sb);
		sb.append('.');

		//month as name
		sb.append(ws).
		   append(MONTHS_RU[cl.get(Calendar.MONTH)]).
		   append(ws);

		//year
		lennum(cl.get(Calendar.YEAR), 4, sb);
	}

	private static final String[] MONTHS_RU   =
	{
	  "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля",
	  "Августа", "Сентября", "Октября", "Ноября", "Декабря"
	};

	private static final String[] MONTHS_ABBR_RU   =
	{
	  "Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл",
	  "Авг", "Сен", "Окт", "Ноя", "Дек"
	};

	private static final String[] WEEKDAYS_RU =
	{
	  "", "Воскресенье", "Понедельник", "Вторник",
	  "Среда", "Четверг", "Пятница", "Суббота",
	};
}