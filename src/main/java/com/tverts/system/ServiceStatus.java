package com.tverts.system;

/**
 * Provides access port to the service's
 * runtime state.
 *
 * @author anton.baukin@gmail.com
 */
public interface ServiceStatus
{
	/* public: ServiceStatus interface */

	public String    getStateName(String lang);

	/**
	 * Each service must be ready to handle
	 * income requests. A service becomes ready
	 * after {@link Service#initService()} call.
	 * It is not ready after the call to
	 * {@link Service#freeService()}.
	 */
	public boolean   isReady();

	/**
	 * Tells whether the service has it's own
	 * behaviour on. A service may return {@code true}
	 * value only if it is designed to be an active
	 * service: {@link ServiceInfo#isActiveService()}.
	 *
	 * Note that the service may tell that it is
	 * active, but at the call time has no active
	 * tasks (threads) running.
	 */
	public boolean   isActive();

	/**
	 * Tells whether this active service has at least
	 * one pending task (thread).
	 */
	public boolean   isRunning();

	/**
	 * When error occures, service may react in
	 * differ ways. This error value means an error
	 * that have caused the service to stop or free.
	 * Services handling repeating requests do not
	 * store errors here.
	 */
	public Throwable getServiceError();

	/**
	 * Advanced text of the error caught.
	 */
	public String    getErrorText(String lang);
}