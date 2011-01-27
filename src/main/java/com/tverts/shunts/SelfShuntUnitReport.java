package com.tverts.shunts;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: support */

import static com.tverts.support.LO.LANG_LO;

/**
 * Collection of {@link SelfShuntUnitReport}s representing
 * a unit of tests. The collection is usually scoped
 * within the unit class and (optionally) it's ancestors.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SelfShuntUnitReport
       implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;

	/* public: definitive properties */

	/**
	 * Obligatory property. The name of the test unit.
	 * If {@link #getUnitClass()} is defined the value
	 * may be the short name of that class.
	 */
	public String    getUnitName()
	{
		return unitName;
	}

	public void      setUnitName(String unitName)
	{
		this.unitName = unitName;
	}

	/**
	 * If the test unit is represented by a class,
	 * this property defines that class.
	 *
	 * Optional, but always set in the basic
	 * implementation.
	 */
	public Class     getUnitClass()
	{
		return unitClass;
	}

	public void      setUnitClass(Class unitClass)
	{
		this.unitClass = unitClass;
	}

	public String    getDescription(String lang)
	{
		return LANG_LO.equals(lang)?
		  getDescriptionLo():getDescriptionEn();
	}

	public String    getDescriptionEn()
	{
		return descriptionEn;
	}

	public void      setDescriptionEn(String descriptionEn)
	{
		this.descriptionEn = descriptionEn;
	}

	public String    getDescriptionLo()
	{
		return descriptionLo;
	}

	public void      setDescriptionLo(String descriptionLo)
	{
		this.descriptionLo = descriptionLo;
	}

	/* public: invocation results properties */

	public List<SelfShuntTaskReport>
	                 getTaskReports()
	{
		return (taskReports != null)?(taskReports):
		  (taskReports = createTaskReports());
	}

	public void      setTaskReports(List<SelfShuntTaskReport> reports)
	{
		this.taskReports = reports;
	}

	public boolean   isSuccess()
	{
		return success;
	}

	public void      setSuccess(boolean success)
	{
		this.success = success;
	}

	public boolean   isCritical()
	{
		return critical;
	}

	public void      setCritical(boolean critical)
	{
		this.critical = critical;
	}

	public Throwable getError()
	{
		return error;
	}

	public void      setError(Throwable error)
	{
		this.error = error;
	}

	/* public: invocation time */

	public long      getRunTime()
	{
		return runTime;
	}

	public void      setRunTime(long runTime)
	{
		this.runTime = runTime;
	}

	public long      getEndTime()
	{
		return endTime;
	}

	public void      setEndTime(long endTime)
	{
		this.endTime = endTime;
	}

	/* protected: misc routines */

	protected List<SelfShuntTaskReport>
	                 createTaskReports()
	{
		return new ArrayList<SelfShuntTaskReport>(8);
	}

	/* private: aggregated task reports */

	private List<SelfShuntTaskReport> taskReports;

	/* private: the report attributes */

	private String    unitName;
	private Class     unitClass;
	private String    descriptionEn;
	private String    descriptionLo;
	private Throwable error;
	private long      runTime;
	private long      endTime;
	private boolean   success;
	private boolean   critical;
}