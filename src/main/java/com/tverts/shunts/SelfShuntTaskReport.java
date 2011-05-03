package com.tverts.shunts;

/* com.tverts: support */

import static com.tverts.support.LO.LANG_LO;

/**
 * A report on invocation of a single test task.
 * Mostly it is a method of test unit class.
 *
 * @author anton.baukin@gmail.com
 */
public class      SelfShuntTaskReport
       implements java.io.Serializable
{
	public static final long serialVersionUID = 0L;

	/* public: definitive properties */

	/**
	 * The invoked task (method) name.
	 * Obligatory property.
	 */
	public String    getTaskName()
	{
		return taskName;
	}

	public void      setTaskName(String taskName)
	{
		this.taskName = taskName;
	}

	/**
	 * Describes what the test for. Optional.
	 */
	public String    getDescription(String lang)
	{
		return LANG_LO.equals(lang)?
		  getDescriptionLo():getDescriptionEn();
	}

	public String    getDescriptionEn()
	{
		return descriptionEn;
	}

	public void      setDescriptionEn(String text)
	{
		this.descriptionEn = text;
	}

	public String    getDescriptionLo()
	{
		return descriptionLo;
	}

	public void      setDescriptionLo(String text)
	{
		this.descriptionLo = text;
	}

	/* public: invocation error properties */

	/**
	 * Tells the result of the invocation.
	 *
	 * Note that the invocation may be successful
	 * even if there is an error returned by
	 * {@link #getError()}.
	 */
	public boolean   isSuccess()
	{
		return success;
	}

	public void      setSuccess(boolean success)
	{
		this.success = success;
	}

	/**
	 * Defines the test to be critical for the
	 * shunt bean. If critical test method fails
	 * the whole bean is stopped and marked as
	 * failed.
	 */
	public boolean   isCritical()
	{
		return critical;
	}

	public void      setCritical(boolean critical)
	{
		this.critical = critical;
	}

	/**
	 * Optional error defined what was wrong.
	 */
	public Throwable getError()
	{
		return error;
	}

	public void      setError(Throwable error)
	{
		this.error = error;
	}

	/**
	 * Optional error description. May not be set
	 * when {@link #getError()} returns {@code null}.
	 *
	 * If there is no error it contains the failed
	 * assertion description.
	 */
	public String    getErrorText(String lang)
	{
		return LANG_LO.equals(lang)?
		  getErrorTextLo():getErrorTextEn();
	}

	public String    getErrorTextEn()
	{
		return errorTextEn;
	}

	public void      setErrorTextEn(String errorText)
	{
		this.errorTextEn = errorText;
	}

	public String    getErrorTextLo()
	{
		return errorTextLo;
	}

	public void      setErrorTextLo(String errorText)
	{
		this.errorTextLo = errorText;
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

	/* private: the report attributes */

	private String    taskName;
	private String    descriptionEn;
	private String    descriptionLo;
	private Throwable error;
	private String    errorTextEn;
	private String    errorTextLo;
	private long      runTime;
	private long      endTime;
	private boolean   success;
	private boolean   critical;
}