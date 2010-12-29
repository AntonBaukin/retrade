package com.tverts.support;

/* standard Java classes */

import java.util.Locale;

/**
 * Various localization issues.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class LO
{
	/* public: constants */

	public static final String LANG_EN   = "en";

	public static final String LANG_RU   = "ru";

	public static final String LANG_LO   = LANG_RU;

	public static final Locale LOCALE_EN = Locale.US;

	public static final Locale LOCALE_RU = new Locale(LANG_RU);

	public static final Locale LOCALE_LO = LOCALE_RU;
}