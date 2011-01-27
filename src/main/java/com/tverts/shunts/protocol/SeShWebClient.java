package com.tverts.shunts.protocol;

/**
 * Implements HTTP conversation with this server.
 * Support class for {@link SeShProtocolWebBase}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface SeShWebClient
{
	/* public: SeShWebClient interface */

	/**
	 * Connects to the server issuing the
	 * {@link SeShRequestInitial} provided.
	 *
	 * Note that {@code null} results means
	 * that there are no shunts to run.
	 */
	public SeShRequest  connect(SeShRequestInitial request)
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Sends actual Self Shunt request to the server.
	 */
	public SeShResponse request(SeShRequest request)
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Orders the client to break the operation pending.
	 * As the result the thread actually running this
	 * client would gain {@link InterruptedException}.
	 *
	 * After breaking the original thread will always
	 * invoke {@link #release()}.
	 *
	 * Has no effect on a closed client.
	 */
	public void        breakClient();

	/**
	 * Releases all the resources acquired by the client.
	 * This method is always invoked when the initial
	 * connection was successful.
	 */
	public void        release();
}