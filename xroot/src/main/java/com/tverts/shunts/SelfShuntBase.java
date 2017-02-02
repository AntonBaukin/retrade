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
import com.tverts.support.LU;
import static com.tverts.support.SU.s2s;
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
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntBase
       implements     SelfShunt
{
	/* public: SelfShunt interface */

	public String    getName()
	{
		return (this.name != null)?(this.name):(getNameDefault());
	}

	/**
	 * The Self-Shunt Unit name to configure via Spring.
	 */
	public void      setName(String name)
	{
		this.name = s2s(name);
	}

	/**
	 * This default implementation returns an empty
	 * array of group names.
	 */
	public String[]  getGroups()
	{
		return EMPTY_GROUPS;
	}

	public SelfShunt clone()
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

	public void      shunt(SelfShuntCtx ctx)
	{
		try
		{
			//~: init the report
			initUnitReport(ctx.getReport());

			//~: init the environment
			initShuntEnvironment();

			//~: set the default success status
			ctx.getReport().setSuccess(true);

			//c: for all shunt methods
			for(Method m : collectMethods())
			{
				//0: create the report & read the annotation
				SelfShuntTaskReport mr = createReport(m);

				//1: do before tasks
				if(!beforeMethod(ctx, m, mr))
					continue; //<-- this method must be skipped

				//~: add to the tasks report
				ctx.getReport().getTaskReports().add(mr);

				//2: invoke the method
				callMethod(m, mr);

				//3: do after tasks
				if(!afterMethod(m, mr))
				{
					//HINT: we have no total success if at least
					//  one shunt methods fails, but this failure
					//  may be not so critical to stop shunting.

					ctx.getReport().setSuccess(false);

					//?: {is not a critical error} continue shunting
					if(!mr.isCritical())
						continue;

					//!: mark critical error
					ctx.getReport().setCritical(true);
					ctx.getReport().setError(mr.getError());

					break;
				}
			}
		}
		catch(Throwable e)
		{
			//~: set shunt system internal error
			ctx.getReport().setSuccess(false);
			ctx.getReport().setCritical(true); //<-- system errors are critical
			ctx.getReport().setError(e);
		}
		finally
		{
			try
			{
				freeShuntEnvironment();
			}
			catch(Throwable e)
			{
				if(ctx.getReport().getError() == null)
				{
					ctx.getReport().setSuccess(false);
					ctx.getReport().setError(e);
				}
			}
		}

		//~: mark shunt end timestamp
		ctx.getReport().setEndTime(
		  System.currentTimeMillis());

		//!: check whether to rollback
		inspectUnitRollback(ctx.getReport());
	}


	/* public: SelfShuntBase interface */

	/**
	 * Tells that the transactions this shunt unit
	 * was participated must not be committed, and
	 * they must be marked for rollback only.
	 *
	 * This flag has meaning only when the Shunt
	 * Context is not read-only.
	 */
	public boolean    isRollbackOnly()
	{
		return rollbackOnly;
	}

	public  void      setRollbackOnly(boolean rollbackOnly)
	{
		this.rollbackOnly = rollbackOnly;
	}


	/* protected: basic implementation */

	/**
	 * Returns the default name of the Unit
	 * when no distinct name is configured.
	 */
	protected String  getNameDefault()
	{
		return sLo(getTarget().getClass().getSimpleName());
	}


	/* protected: shunt unit invocation */

	protected void    initUnitReport(SelfShuntUnitReport report)
	{
		//~: timestamp
		report.setRunTime(System.currentTimeMillis());

		//~: shunt class name
		report.setUnitClass(getTarget().getClass());

		//~: the shunt name
		report.setUnitName(getName());
	}

	protected void    initShuntEnvironment()
	  throws Exception
	{}

	protected void    freeShuntEnvironment()
	{}

	protected void    inspectUnitRollback(SelfShuntUnitReport report)
	{
		//?: {critically failed the shunt | rollback only} set rollback flag
		if((!report.isSuccess() && report.isCritical()) || isRollbackOnly())
			doMarkRollback();
	}

	protected void    doMarkRollback()
	{
		TxPoint.txContext().setRollbackOnly();
	}


	/* protected: shunt methods discovery */

	/**
	 * This method collects all shunt methods.
	 * The result is used to create a list.
	 */
	protected abstract Collection<Method> findMethods();

	/**
	 * Defines the order to call the shunt methods.
	 */
	protected abstract void sortMethods(ArrayList<Method> methods);

	protected List<Method>  collectMethods()
	{
		ArrayList<Method> res =
		  new ArrayList<Method>(findMethods());

		sortMethods(res);
		return res;
	}


	/* protected: shunt methods invocation */

	/**
	 * Performs tasks before the method invocation.
	 * Tells whether the method must be invoked.
	 */
	protected boolean beforeMethod (
	    SelfShuntCtx        ctx,
	    Method              m,
	    SelfShuntTaskReport report
	  )
	  throws Exception
	{
		//?: {context is read only}
		if(ctx.isReadonly())
			//?: {shunt is editing}
			if(report.isEditing())
			{
				LU.W(getLog(), "skip task [", report.getTaskName(),
				  "], or it would edit read-only context");

				return false;
			}

		return true;
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

	/**
	 * Calls the method on the {@link #getTarget()}.
	 */
	protected void    callMethod(Method m, SelfShuntTaskReport report)
	{
		Throwable error = null;

		//~: invoke the test method
		try
		{
			//~: mark the task (method) run time
			report.setRunTime(System.currentTimeMillis());

			//!: invoke the method on the target
			m.invoke(getTarget());
		}
		catch(Throwable e)
		{
			error = e;
		}

		//~: mark the task (method) finish time
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

	protected String  getLog()
	{
		return this.getClass().getName();
	}


	/* private: the name of the shunt */

	private String  name;


	/* private: the state of the shunt */

	private boolean rollbackOnly;


	/* private: misc */

	protected static final String[] EMPTY_GROUPS =
	  new String[0];
}