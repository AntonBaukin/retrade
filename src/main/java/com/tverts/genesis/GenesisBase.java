package com.tverts.genesis;

/* standard Java classes */

import java.util.List;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.ObjectParams;

/* com.tverts: support */

import com.tverts.support.LO;

import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sLo;
import static com.tverts.support.SU.sXe;


/**
 * Stores basic properties of any {@link Genesis} unit.
 *
 * @author anton.baukin@gmail.com
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

	public Genesis   clone()
	{
		try
		{
			return (Genesis)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
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

	public void      parameters(List<ObjectParam> params)
	{
		StringBuilder sb = new StringBuilder(32);
		ObjectParam[] ps = ObjectParams.find(this);

		for(ObjectParam p : ps)
		{
			sb.delete(0, sb.length());

			if(getName() != null)
				sb.append(getName());
			else
				sb.append('?').append(getClass().getSimpleName());

			//~: assign property name
			sb.append('.').append(p.getName());
			p.setName(sb.toString());

			//~: add the parameter
			params.add(p);
		}
	}


	/* public: GenesisBase bean interface */

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


	/* protected: logging */

	/**
	 * Returns the logging destination
	 * of this Genesis unit.
	 */
	protected String log(GenCtx ctx)
	{
		return ctx.log();
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

	private String name;
	private String aboutEn;
	private String aboutLo;
}