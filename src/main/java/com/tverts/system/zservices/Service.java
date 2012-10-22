package com.tverts.system.zservices;

/**
 * Z-Services are system components that
 * do execute various tasks as reaction
 * on events (messages) given to them.
 *
 * The events are stored in the application
 * (in-memory) queues. Services may cooperate
 * with each other sending events via
 * {@link Servicer} coordinator.
 *
 * Service must be synchronized.
 * Better, fully reentable.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Service
{
	/* public: ZService interface */

	/**
	 * Returns application-unique name code
	 * of this service instance (object).
	 */
	public String   uid();

	/**
	 * Returns UIDs of the services this service
	 * depends on, or {@code null} array if the
	 * service is independent.
	 */
	public String[] depends();

	public void     init(Servicer servicer);

	public void     service(Event event);
}