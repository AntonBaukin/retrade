package com.tverts.data;

/* Java */

import java.util.Collections;
import java.util.List;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Implementation base for a Data Source.
 *
 * @author anton.baukin@gmail.com.
 */
public abstract class DataSourceBase
       implements     DataSource, DataSourceReference
{
	/* public: Data Source Reference */

	public List<DataSource> dereferObjects()
	{
		return Collections.<DataSource> singletonList(this);
	}


	/* public: Data Source (info) */

	public String  did()
	{
		return did;
	}

	public void    setDid(String did)
	{
		this.did = did;
	}

	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		this.name = EX.assertn(SU.s2s(name));
	}

	public String  getNameLo()
	{
		return (nameLo != null)?(nameLo):(name);
	}

	public void    setNameLo(String nameLo)
	{
		this.nameLo = SU.s2s(nameLo);
	}

	public String  getDescr()
	{
		return descr;
	}

	public void    setDescr(String descr)
	{
		this.descr = SU.s2s(descr);
	}

	public String  getDescrLo()
	{
		return descrLo;
	}

	public void    setDescrLo(String descrLo)
	{
		this.descrLo = SU.s2s(descrLo);
	}

	public String  getUiPath()
	{
		return uiPath;
	}

	public void    setUiPath(String uiPath)
	{
		this.uiPath = EX.assertn(SU.s2s(uiPath));
	}

	public boolean isSystem()
	{
		return true;
	}


	/* private: instance configuration */

	private String  did;
	private String  name;
	private String  nameLo;
	private String  descr;
	private String  descrLo;
	private String  uiPath;
}