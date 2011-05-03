package com.tverts.shunts.protocol;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntReport;

/**
 * Implements stateful HTTP conversation with this system.
 * By default it creates {@link SeShWebClientCommons} client.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShProtocolWebBase
       extends        SeShProtocolBase
{
	/* public: protocol interface */

	public void             openProtocol()
	  throws SeShProtocolError, InterruptedException
	{
		//0: create the web client
		installWebClient();

		//1: create the initial request
		SeShRequestInitial request = createInitialRequest();
		logInitialRequest(request);

		//2: make initial connection & the response
		shuntRequest = getWebClient().connect(request);
	}

	public boolean          sendNextRequest()
	  throws SeShProtocolError, InterruptedException
	{
		//?: {there is no next request} exit the protocol
		if(shuntRequest == null)
			return false;

		//!: issue the request
		SeShResponse response = getWebClient().
		  request(shuntRequest);

		//process the response
		shuntRequest = processResponse(response);

		return (shuntRequest != null);
	}

	public SelfShuntReport  closeProtocol()
	  throws SeShProtocolError, InterruptedException
	{
		releaseWebClient();
		return super.closeProtocol();
	}

	public void             interruptProtocol()
	  throws SeShProtocolError
	{
		SeShWebClient client = getWebClient();
		if(client != null) client.breakClient();
	}

	/* protected: web client access */

	protected void          installWebClient()
	  throws SeShProtocolError
	{
		if(this.webClient != null)
			throw new IllegalStateException();

		this.webClient = createWebClient();
	}

	protected void          releaseWebClient()
	  throws SeShProtocolError
	{
		if(this.webClient == null) return;

		this.webClient.release();
		this.webClient = null;
	}

	protected SeShWebClient createWebClient()
	  throws SeShProtocolError
	{
		return new SeShWebClientCommons();
	}

	/**
	 * Returns the web client instance previously created.
	 * May not create a client itself.
	 */
	protected SeShWebClient getWebClient()
	{
		return this.webClient;
	}

	/* protected, private: protocol state */

	/**
	 * Current request instance.
	 */
	protected SeShRequest          shuntRequest;

	/**
	 * The (stateful) web client used.
	 */
	private volatile SeShWebClient webClient;
}