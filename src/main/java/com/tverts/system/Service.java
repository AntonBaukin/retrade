package com.tverts.system;

/**
 * Service is a dedicated component of the
 * system with the controlled life cycle.
 *
 * When system is booting, all the services
 * found are first initialized, then started.
 *
 * When system is shutting down, all the
 * services are first stopped, then freed.
 *
 * @author anton.baukin@gmail.com
 */
public interface Service
{
	/* public: Service interface */

	/**
	 * Informative view on the service.
	 */
	public ServiceInfo   getServiceInfo();

	/**
	 * A port to monitor the service's state.
	 */
	public ServiceStatus getServiceStatus();

	/**
	 * Prepares the service to handle the requests.
	 * After this call a service must be ready.
	 *
	 * Note that repeating invocations of this method
	 * have no effect.
	 */
	public void          initService();

	/**
	 * If the service is an active service, starts
	 * own behaviour (threads). Service must be ready
	 * before this call. For inactive services
	 * does nothing.
	 *
	 * Note that this call is asynchronous: service's
	 * threads may be alive yet after this call. Invoke
	 * {@link #waitService()} to wait them.
	 *
	 * Note that repeating invocations of this method
	 * have no effect.
	 */
	public void          startService();

	/**
	 * If the service is an active service, stops
	 * own behaviour (threads). Service must be started
	 * before this call.
	 *
	 * Note that this call is asynchronous: service's
	 * threads may be alive after this call. Invoke
	 * {@link #waitService()} after this call to wait.
	 *
	 * Note that repeating invocations of this method
	 * have no effect.
	 */
	public void          stopService();

	/**
	 * Waits until the last asynchronous operation
	 * completes. These operations are: start and stop.
	 * This call has no effect on inactive services.
	 *
	 * Note that service control functions are blocked
	 * until this call completes. With existing now
	 * implementation of the services, the waiting clients
	 * are unblocked as the last operation of the thread's
	 * body. But some threads (going to death) may still
	 * exist after this call. Use thread pool and wait
	 * on it to fully control the situation.
	 *
	 * It is possible that service would be freed right
	 * after start, and client going to wait the start to
	 * complete is actually unblocked by free complete.
	 * It is the reason why there are no wait operations
	 * for each asynchronous operation.
	 */
	public void          waitService()
		throws InterruptedException;

	/**
	 * Deinitializes the service. After this call the
	 * service would not be able to handle the requests.
	 * To call this method started services must be stopped.
	 *
	 * Note that repeating invocations of this method
	 * have no effect.
	 */
	public void          freeService();
}