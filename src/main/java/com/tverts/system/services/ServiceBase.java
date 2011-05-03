package com.tverts.system.services;

/* standard Java classes */

import java.util.Collections;
import java.util.List;

/* tverts.com: system */

import com.tverts.system.Service;
import com.tverts.system.ServiceInfo;
import com.tverts.system.ServiceReference;
import com.tverts.system.ServicesPoint;
import com.tverts.system.ServiceStatus;

/* tverts.com: support */

import static com.tverts.support.EX.e2en;
import static com.tverts.support.EX.e2lo;
import static com.tverts.support.LO.LANG_EN;
import static com.tverts.support.LO.LANG_LO;
import static com.tverts.support.LO.LANG_RU;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sLo;

/**
 * Implements basic functions of a Service.
 * Does not provide support for active services.
 * Forwards status and info ports to itself.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ServiceBase
       implements     Service, ServiceReference
{
	/* public: constructor */

	public ServiceBase()
	{
		this.serviceInfo  = createServiceInfo();
		this.serviceState = createServiceState();
	}

	/* public: Service interface */

	public ServiceInfo   getServiceInfo()
	{
		return this.serviceInfo;
	}

	public ServiceStatus getServiceStatus()
	{
		return this.serviceState;
	}

	/* public: ServiceReference interface */

	public List<Service> dereferObjects()
	{
		return Collections.<Service>singletonList(this);
	}

	/* public: ServiceInfo interface */

	public String  getServiceName()
	{
		return (serviceName != null)?(serviceName):
		  (sLo(this.getClass().getSimpleName()));
	}

	public String  getServiceSignature()
	{
		String cname = getServiceName();
		String icode = Integer.toString(
		  System.identityHashCode(this));

		return new StringBuilder(
		    cname.length() + icode.length() + 1).
		  append(cname).append('#').append(icode).
		  toString();
	}

	/**
	 * Sets the name of the service. Note that
	 * this method may be invoked only once:
	 * following calls with differ value would
	 * raise an {@link IllegalStateException}.
	 */
	public void    setServiceName(String name)
	{
		if(name == null)
			throw new IllegalArgumentException();

		if((serviceName != null) && !serviceName.equals(name))
			throw new IllegalStateException();
		serviceName = name;
	}

	public String  getServiceTitle(String lang)
	{
		if(LANG_LO.equals(lang) && (serviceTitleLo != null))
			return serviceTitleLo;
		return (serviceTitleEn != null)?(serviceTitleEn):(serviceTitleLo);
	}

	public void    setServiceTitleEn(String serviceTitleEn)
	{
		this.serviceTitleEn = s2s(serviceTitleEn);
	}

	public void    setServiceTitleLo(String serviceTitleLo)
	{
		this.serviceTitleLo = s2s(serviceTitleLo);
	}

	/**
	 * Basic implementation always returns {@code false}.
	 * Compare with {@link ActiveServiceBase#isActiveService()}.
	 */
	public boolean isActiveService()
	{
		return false;
	}

	/* public: ServiceStatus interface */

	public abstract String    getStateName(String lang);

	public abstract boolean   isReady();

	public abstract boolean   isActive();

	public abstract boolean   isRunning();

	public Throwable          getServiceError()
	{
		return this.serviceError;
	}

	public String             getErrorText(String lang)
	{
		if(getServiceError() == null)
			return null;

		if(LANG_LO.equals(lang) && (errorTextLo != null))
			return errorTextLo;
		return (errorTextEn != null)?(errorTextEn):(errorTextLo);
	}

	/* protected: service state */

	/**
	 * Sets the error of the service. Stateful services
	 * may change their state on this call!
	 *
	 * If the exception is {@code null} the error texts
	 * are cleared.
	 */
	protected void setServiceError(Throwable e)
	{
		if(e == null)
			this.errorTextEn = this.errorTextLo = null;
		else
		{
			this.errorTextEn = e2en(e);
			this.errorTextLo = e2lo(e);
		}

		this.serviceError = e;
	}

	/**
	 * Sets the English text of the error. By default
	 * this value is assigned from the exception, but
	 * more readable variant may be assigned.
	 *
	 * Note that error must be set via
	 * {@link #setServiceError(Throwable)}, or
	 * {@link IllegalStateException} would be raised.
	 */
	protected void setErrorTextEn(String errorTextEn)
	{
		if(getServiceError() == null)
			throw new IllegalStateException();
		this.errorTextEn = errorTextEn;
	}

	/**
	 * Sets the localized text of the error.
	 *
	 * Note that error must be set via
	 * {@link #setServiceError(Throwable)}, or
	 * {@link IllegalStateException} would be raised.
	 */
	protected void setErrorTextLo(String errorTextLo)
	{
		if(getServiceError() == null)
			throw new IllegalStateException();
		this.errorTextLo = errorTextLo;
	}

	/* protected: aggregated ServiceInfo */

	protected ServiceInfo createServiceInfo()
	{
		return new ServiceInfoBridge();
	}

	protected class      ServiceInfoBridge
	          implements ServiceInfo
	{
		/* public: ServiceInfo interface */

		public String  getServiceName()
		{
			return ServiceBase.this.getServiceName();
		}

		public String getServiceSignature()
		{
			return ServiceBase.this.getServiceSignature();
		}

		public String  getServiceTitle(String lang)
		{
			return ServiceBase.this.getServiceTitle(lang);
		}

		public boolean isActiveService()
		{
			return ServiceBase.this.isActiveService();
		}
	}

	/* protected: aggregated ServiceStatus */

	protected ServiceStatus createServiceState()
	{
		return new ServiceStatusBridge();
	}

	protected class      ServiceStatusBridge
	          implements ServiceStatus
	{
		/* public: ServiceStatus interface */

		public String    getStateName(String lang)
		{
			return ServiceBase.this.getStateName(lang);
		}

		public boolean   isReady()
		{
			return ServiceBase.this.isReady();
		}

		public boolean   isActive()
		{
			return ServiceBase.this.isActive();
		}

		public boolean   isRunning()
		{
			return ServiceBase.this.isRunning();
		}

		public Throwable getServiceError()
		{
			return ServiceBase.this.getServiceError();
		}

		public String    getErrorText(String lang)
		{
			return ServiceBase.this.getErrorText(lang);
		}
	}

	/* protected: misc support */

	protected String getStateNameEmpty(String lang)
	{
		return LANG_RU.equals(lang)?
		  ("не готов"):("not ready");
	}

	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}

	/**
	 * Returns the logging name of the service in format:
	 * <tt>service 'name'</tt>.
	 */
	protected String logsig(String lang)
	{
		String one = LANG_RU.equals(lang)?
		  ("сервис"):("service");

		String two = getServiceName();
		if(two == null) two = "???";

		return String.format("%s '%s'", one, two);
	}

	protected String logsig()
	{
		return this.logsig(LANG_EN);
	}

	/**
	 * Returns the signature of the service in format:
	 * <tt>service 'name' [state]</tt>.
	 */
	protected String sig4state(String lang)
	{
		String one = LANG_RU.equals(lang)?("сервис"):("service");

		String two = getServiceName();
		if(two == null) two = "???";

		String thr = getStateName(lang);
		if(thr == null) thr = "???";

		return String.format("%s '%s' [%s]", one, two, thr);
	}

	protected String sig4state()
	{
		return sig4state(LANG_EN);
	}

	/* private: service settings */

	private String serviceName;
	private String serviceTitleEn;
	private String serviceTitleLo;

	/* private: service state */

	private ServiceInfo   serviceInfo;
	private ServiceStatus serviceState;
	private Throwable     serviceError;
	private String        errorTextEn;
	private String        errorTextLo;
}