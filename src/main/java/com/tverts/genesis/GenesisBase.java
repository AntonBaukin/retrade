package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
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
		addOwnParameters(params,
		  Arrays.asList(ObjectParams.find(this)));
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


	/* protected: support for parameters */

	protected void      addOwnParameters
	  (List<ObjectParam> dest, List<ObjectParam> params)
	{
		StringBuilder sb = new StringBuilder(32);

		for(ObjectParam p : params)
		{
			sb.delete(0, sb.length());

			//~: this genesis name
			sb.append(getGenesisParamPrefix(this));

			//~: assign property name
			sb.append(" (").append(p.getName()).append(')');
			p.setName(sb.toString());

			//~: add the parameter
			dest.add(p);
		}
	}

	protected void      addNestedParameters
	  (List<ObjectParam> dest, Genesis g)
	{
		List<ObjectParam> tmp = new ArrayList<ObjectParam>(4);
		StringBuilder     sb  = new StringBuilder(32);

		//~: collect the parameters
		g.parameters(tmp);

		//~: change the names
		for(ObjectParam p : tmp)
		{
			sb.delete(0, sb.length());

			//~: this genesis name
			sb.append(getGenesisParamPrefix(this));

			//~: prefix the name
			sb.append(" : ").append(p.getName());
			p.setName(sb.toString());
		}

		//~: add that parameters
		dest.addAll(tmp);
	}

	protected String    getGenesisParamPrefix(Genesis g)
	{
		return (g.getName() != null)?(g.getName()):
		  String.format("?%s", g.getClass().getSimpleName());
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
		  ("Модуль генезиса {%s}"):("Genesis Unit {%s}"),
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