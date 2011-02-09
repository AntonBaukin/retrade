package com.tverts.genesis;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

/* com.tverts: support */

import com.tverts.support.LO;

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sLo;
import static com.tverts.support.SU.sXe;

/**
 * Stores basic properties of any {@link Genesis} unit.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class GenesisBase
       implements     Genesis
{
	/* public: constructor */

	public GenesisBase()
	{
		this.name = sLo(this.getClass().getSimpleName());
	}

	/* public: Genesis interface */

	public Predicate getCondition()
	{
		return predicate;
	}

	public String    getName()
	{
		return name;
	}

	public String    getAbout(String lang)
	{
		if(LO.LANG_LO.equals(lang) && !sXe(getAboutLo()))
			return getAboutLo();
		return getAboutEn();
	}

	/* public: GenesisBase bean interface */

	public void      setCondition(Predicate predicate)
	{
		this.predicate = predicate;
	}

	public void      setName(String name)
	{
		if((name = s2s(name)) == null)
			name = sLo(this.getClass().getSimpleName());

		this.name = name;
	}

	public String    getAboutEn()
	{
		return aboutEn;
	}

	public void      setAboutEn(String aboutEn)
	{
		this.aboutEn = s2s(aboutEn);
	}

	public String    getAboutLo()
	{
		return aboutLo;
	}

	public void      setAboutLo(String aboutLo)
	{
		this.aboutLo = s2s(aboutLo);
	}

	public boolean   isAllowed()
	{
		Predicate p = getCondition();
		return (p == null) || p.evalPredicate(this);
	}

	/* protected: logging */

	protected String getLog()
	{
		return GenesisPoint.LOG_GENESIS;
	}

	protected String logsig(String lang)
	{
		return String.format((LO.LANG_RU.equals(lang))?
		  ("Модуль генезиса '%s'"):("Genesis Unit '%s'"),
		  getName());
	}

	protected String logsig()
	{
		return logsig(LO.LANG_EN);
	}

	/* private: parameters of this genesis unit */

	private Predicate predicate;
	private String    name;
	private String    aboutEn;
	private String    aboutLo;
}