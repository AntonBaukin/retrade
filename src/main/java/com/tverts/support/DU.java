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
	/* public: arithmetics */

	public static int    diffMins(Date one, Date two)
	{
		Date x, X;

		if(one.after(two)) {x = two; X = one;}
		else               {x = one; X = two;}

		return (int)((X.getTime() - x.getTime()) / (1000L*60));
	}

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

	/* public: formatting routines */

	public static String date2str(Date d)
	{
		StringBuilder sb = new StringBuilder(10);
		date2str(d, sb, null);
		return sb.toString();
	}

	public static void   date2str (
	                       Date          d,
	                       StringBuilder sb,
	                       Calendar cl
	                     )
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

	public static void   time2str (
	                       Date          t,
	                       StringBuilder sb,
	                       Calendar      cl
	                     )
	{
		if(cl == null) cl =  Calendar.getInstance();
		cl.setTime(t);

		//hour
		lennum(cl.get(Calendar.HOUR_OF_DAY), 2, sb);
		sb.append(':');
		//minute
		lennum(cl.get(Calendar.MINUTE), 2, sb);
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
	public static void   namedDateTimeToStrRu (
	                       Date          dt,
	                       StringBuilder sb,
	                       String        ws,
	                       Calendar      cl
	                     )
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

	private static final String[] MONTHS_RU =
	{
	  "Января", "Февраля", "Марта",
	  "Апреля", "Мая", "Июня", "Июля",
	  "Августа", "Сентября", "Октября",
	  "Ноября", "Декабря"
	};

	private static final String[] WEEKDAYS_RU =
	{
	  "", "Воскресенье", "Понедельник", "Вторник",
	  "Среда", "Четверг", "Пятница", "Суббота",
	};
}