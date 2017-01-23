package com.tverts.genesis;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.ObjectParams;

/* com.tverts: support */

import com.tverts.support.LO;
import com.tverts.support.SU;


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
		this.name = SU.sLo(this.getClass().getSimpleName());
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
		if(LO.LANG_LO.equals(lang) && !SU.sXe(getAboutLo()))
			return getAboutLo();

		return getAboutEn();
	}

	public void      parameters(List<ObjectParam> params)
	{
		addOwnParameters(params,
		  Arrays.asList(ObjectParams.find(this)));
	}


	/* Genesis Base */

	public void      setName(String name)
	{
		if((name = SU.s2s(name)) == null)
			name = SU.sLo(this.getClass().getSimpleName());

		this.name = name;
	}

	private String name;

	public String    getAboutEn()
	{
		return aboutEn;
	}

	private String aboutEn;

	public void      setAboutEn(String aboutEn)
	{
		this.aboutEn = SU.s2s(aboutEn);
	}

	public String    getAboutLo()
	{
		return aboutLo;
	}

	private String aboutLo;

	public void      setAboutLo(String aboutLo)
	{
		this.aboutLo = SU.s2s(aboutLo);
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
		List<ObjectParam> tmp = new ArrayList<>(4);
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
		if(LO.LANG_RU.equals(lang))
			return SU.cats("Модуль генезиса {", getName(), "}");

		return SU.cats("Genesis Unit {", getName(), "}");
	}

	protected String logsig()
	{
		return logsig(LO.LANG_EN);
	}
}