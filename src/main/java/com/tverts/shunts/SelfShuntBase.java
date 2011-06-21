package com.tverts.shunts;

/* standard Java classes */

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* com.tverts: transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: support */

import static com.tverts.support.EX.e2en;
import static com.tverts.support.EX.e2lo;
import static com.tverts.support.SU.sLo;


/**
 * As it is in JUnit library, the shunt units
 * may be defined via inheritance or by the
 * annotation.
 *
 * This library also supports annotations, see
 * {@link SelfShuntUnit}, but in more strict manner.
 * The shunt handling subsystem does not know
 * about the annotation. It expects any shunt
 * unit to implement {@link SelfShunt} interface.
 *
 * {@link SelfShuntBase} provides support for
 * early implementations independently of the
 * kind of unit discovery and binding.
 *
 * The implementation expects that a unit
 * consists of a number of shunt methods
 * that are invoked in some defined order.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntBase
       implements     SelfShunt
{
	/* public: SelfShunt interface */

	public String     getShuntUnitName()
	{
		return sLo(getTarget().getClass().getSimpleName());
	}

	protected static final String[] EMPTY_GROUPS =
	  new String[0];

	/**
	 * This default implementation returns an empty
	 * array of group names.
	 */
	public String[]   getShuntGroups()
	{
		return EMPTY_GROUPS;
	}

	public void       runShunt(SelfShuntUnitReport report)
	{
		try
		{
			initUnitReport(report);
			initShuntEnvironment();

			for(Method m : collectMethods())
			{
				//0: create the report & read the annotation
				SelfShuntTaskReport mr = createReport(m);

				//1: do before tasks
				if(!beforeMethod(m, mr))
					continue;
				report.getTaskReports().add(mr);

				//2: invoke the method
				callMethod(m, mr);

				//3: do after tasks
				if(!afterMethod(m, mr))
				{
					report.setSuccess(false);
					break;
				}
			}

			report.setSuccess(true);
		}
		catch(Throwable e)
		{
			report.setSuccess(false);
			report.setError(e);
		}
		finally
		{
			try
			{
				freeShuntEnvironment();
			}
			catch(Throwable e)
			{
				if(report.getError() == null)
				{
					report.setSuccess(false);
					report.setError(e);
				}
			}
		}

		report.setEndTime(System.currentTimeMillis());

		if(isRollbackOnly())
			doRollback();
	}

	/* public: SelfShuntBase interface */

	/**
	 * Tells that the transactions this shunt unit
	 * was participated must not be committed, and
	 * they must be marked for rollback only.
	 */
	public boolean    isRollbackOnly()
	{
		return rollbackOnly;
	}

	public  void      setRollbackOnly(boolean rollbackOnly)
	{
		this.rollbackOnly = rollbackOnly;
	}


	/* public: Cloneable interface */

	public SelfShuntBase clone()
	{
		try
		{
			return (SelfShuntBase)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

	/* protected: shunt unit invocation */

	protected void    initUnitReport(SelfShuntUnitReport report)
	{
		report.setRunTime(System.currentTimeMillis());
		report.setUnitClass(getTarget().getClass());
		report.setUnitName(getShuntUnitName());
	}

	/**
	 * The object to find the annotations.
	 * In the most cases {@code this} object.
	 */
	protected Object  getTarget()
	{
		return this;
	}

	/**
	 * Creates the report for the shunt method given.
	 * The method is one of that returned by
	 * {@link #collectMethods()}.
	 */
	protected SelfShuntTaskReport
	                  createReport(Method m)
	{
		SelfShuntTaskReport r = new SelfShuntTaskReport();

		r.setTaskName(m.getName());
		r.setCritical(true); //<-- critical by default

		return r;
	}

	protected void    initShuntEnvironment()
	  throws Exception
	{}

	protected void    freeShuntEnvironment()
	{}

	protected void    doRollback()
	{
		TxPoint.txContext().setRollbackOnly();
	}

	/* protected: shunt methods discovery */

	/**
	 * This method collects all shunt methods.
	 * The result is used to create a list.
	 */
	protected abstract Collection<Method>
	                  findMethods();

	/**
	 * Defines the order to call the shunt methods.
	 */
	protected abstract void
	                  sortMethods(ArrayList<Method> methods);

	protected List<Method>
	                  collectMethods()
	{
		ArrayList<Method> res = new ArrayList<Method>(
		  findMethods());

		sortMethods(res);
		return res;
	}

	/* protected: shunt methods invocation */

	/**
	 * Performs tasks before the method invocation.
	 * Tells whether the method must be invoked.
	 */
	protected boolean beforeMethod(Method m, SelfShuntTaskReport report)
	  throws Exception
	{
		return true;
	}

	/**
	 * Calls the method on the {@link #getTarget()}.
	 */
	protected void    callMethod(Method m, SelfShuntTaskReport report)
	{
		Throwable error = null;

		//invoke the test method
		try
		{
			report.setRunTime(System.currentTimeMillis());

			//!: invoke the method on the target
			m.invoke(getTarget());
		}
		catch(Throwable e)
		{
			error = e;
			report.setSuccess(false);
		}

		report.setEndTime(System.currentTimeMillis());

		//?: {has no error}
		if(error == null)
			report.setSuccess(true);
		else
		{
			report.setError(error);
			initMethodError(m, report);
		}
	}

	/**
	 * Inspects the results of the method invocation and
	 * tells whether the tests must be continued.
	 */
	protected boolean afterMethod(Method m, SelfShuntTaskReport report)
	{
		return report.isSuccess() || !report.isCritical();
	}

	/**
	 * Initializes the error (stored in the method report)
	 * properties and saves them to the report. That properties
	 * may be defined from the method annotations, for example.
	 *
	 * This method is allowed to wipe out the error and set
	 * successful result on the report. This method is invoked
	 * before {@link #afterMethod(Method, SelfShuntTaskReport)}.
	 *
	 * This basic implementation copies the error text from an
	 * {@link AssertionError} exceptions.
	 */
	protected void    initMethodError(Method m, SelfShuntTaskReport report)
	{
		if(report.getError() instanceof AssertionError)
		{
			report.setErrorTextEn(e2en(report.getError()));
			report.setErrorTextLo(e2lo(report.getError()));
		}
	}

	/* private: the state of the shunt */

	private boolean rollbackOnly = true;
}