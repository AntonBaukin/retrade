package com.tverts.shunts.protocol;

/* com.tverts: servlet */

import com.tverts.servlet.RequestPoint;

/* com.tverts: shunts */

import com.tverts.shunts.protocol.SeShProtocolBase.SeShConnectionFailed;
import com.tverts.shunts.protocol.SeShProtocolBase.SeShSystemFailure;

/* com.tverts: support */

import static com.tverts.support.SU.s2a;
import static com.tverts.support.SU.s2s;

/**
 * Provides implementation base that would
 * be handy for any actual shunts HTTP client.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class SeShWebClientBase
       implements     SeShWebClient
{
	/* public: SeShWebClient interface */

	public SeShRequest  connect(SeShRequestInitial request)
	  throws SeShProtocolError, InterruptedException
	{
		SeShResponse response;

		for(int attempt = 0;(attempt < getConnAttempts());attempt++)
		{
			if(breaked) throw new InterruptedException();

			//~: try the ports provided
			for(String port : getPorts())
			{
				if(breaked) throw new InterruptedException();
				response = connectionAttempt(port, request);

				//?: {attempt had failed} take the next port
				if(response == null) continue;

				//?: {the response contains the system error}
				if(response.getSystemError() != null)
					throw new SeShSystemFailure(response);

				//!: define the connection
				defineConnection(port);
				return response.getNextRequest();
			}

			//make reconnect pause
			if(breaked) throw new InterruptedException();
			if(getReconnPause() != 0)
				pause(getReconnPause());
		}

		//!: report that could not connect
		throw new SeShConnectionFailed();
	}

	public SeShResponse request(SeShRequest request)
	  throws SeShProtocolError, InterruptedException
	{
		return sendRequest(port, request);
	}

	public void         breakClient()
	{
		this.breaked = true;
	}

	public void         release()
	{}

	/* public: client parameters */

	public static final String DEF_SERVLET   =
	  SelfShuntFilter.DEF_SERVLET;

	/**
	 * The path to the shunt processing servlet.
	 * The request is always passed to the localhost
	 * (and the context of this web application) to
	 * the port selected from the configured ports.
	 */
	public String   getShuntServlet()
	{
		return shuntServlet;
	}

	public void     setShuntServlet(String servlet)
	{
		if((servlet = s2s(servlet)) == null)
			throw new IllegalArgumentException();

		this.shuntServlet = servlet;
	}

	public static final String[] DEF_PORTS    =
	  new String[]{ "80", "8080" };

	/**
	 * The ports to try to connect to.
	 */
	public String[] getPorts()
	{
		return ports;
	}

	public void     setPorts(String[] ports)
	{
		if((ports == null) || (ports.length == 0))
			throw new IllegalArgumentException();

		this.ports = ports;
	}

	public void     setPortsString(String ports)
	{
		setPorts(s2a(ports));
	}

	public static final int DEF_SO_TIMEOUT    = 32000;

	/**
	 * Known as TCP SO_TIMEOUT parameter.
	 * The number of milliseconds to wait
	 * for the next TCP segment.
	 *
	 * Zero means no timeout. This value is
	 * set by default.
	 */
	public int      getSoTimeout()
	{
		return (soTimeout >= 0) //!: 0 means no timeout
		  ?(soTimeout):(DEF_SO_TIMEOUT);
	}

	public void     setSoTimeout(int soTimeout)
	{
		this.soTimeout = soTimeout;
	}

	public static final int DEF_CONN_TIMEOUT  = 8000;

	/**
	 * The connection establish timeout in milliseconds.
	 */
	public int      getConnTimeout()
	{
		return connTimeout;
	}

	public void     setConnTimeout(int connTimeout)
	{
		if(connTimeout < 0)
			throw new IllegalArgumentException();

		this.connTimeout = connTimeout;
	}

	public static final int DEF_CONN_ATTEMPTS = 4;

	/**
	 * The number of attempts to connect
	 * to the shunting servlet on this server.
	 */
	public int      getConnAttempts()
	{
		return (connAttempts > 0)
		  ?(connAttempts):(DEF_CONN_ATTEMPTS);
	}

	public void     setConnAttempts(int connAttempts)
	{
		this.connAttempts = connAttempts;
	}

	public static final int DEF_RECONN_PAUSE  = 8000;

	/**
	 * The pause in milliseconds between the attempts
	 * to connect to the server. Note that the ports
	 * scan is done without any pauses.
	 */
	public int      getReconnPause()
	{
		return (reconnPause >= 0)
		  ?(reconnPause):(DEF_RECONN_PAUSE);
	}

	public void     setReconnPause(int reconnPause)
	{
		this.reconnPause = reconnPause;
	}

	/* protected: connection issues */

	protected abstract SeShResponse
	                  sendRequest (
	                    String      port,
	                    SeShRequest request
	                  )
	  throws SeShProtocolError, InterruptedException;

	/**
	 * Attempts to connect to the server by the port given.
	 * Connection establish errors are not reported as they
	 * are expected. Returns the server response on success.
	 *
	 * Hint: the system error of the response provided
	 * is already handled outside of this call.
	 */
	protected SeShResponse
	                  connectionAttempt (
	                    String             port,
	                    SeShRequestInitial request
	                  )
	  throws SeShProtocolError, InterruptedException
	{
		try
		{
			return sendRequest(port, request);
		}
		catch(SeShConnectionFailed e)
		{
			return null;
		}
	}

	/**
	 * Allocates the additional resources (if needed)
	 * to issue all the following requests on the
	 * port discovered during the connection phase.
	 */
	protected void    defineConnection(String port)
	{
		this.port = port;
	}

	/**
	 * Creates the full URL to the shunt servlet
	 * located on this host (localhost).
	 */
	protected String  createURL(String port)
	{
		String servlet = getShuntServlet();
		if(servlet.startsWith("/"))
			servlet = servlet.substring(1);

		return String.format(
		  "http://localhost%s%s/%s",

		  //port
		  ("80".equals(port))?(""):(":" + port),

		  //context path
		  RequestPoint.context().getContextPath(),

		  //path to the shunt servlet
		  servlet
		);
	}

	protected void    pause(long timeout)
	  throws InterruptedException
	{
		synchronized(waitee())
		{
			waitee().wait(timeout);
		}
	}

	/**
	 * The pause waiting object.
	 */
	protected Object  waitee()
	{
		return waitee;
	}

	/* private: protocol parameters access*/

	private String   shuntServlet = DEF_SERVLET;
	private String[] ports        = DEF_PORTS;
	private int      soTimeout    = 0; //<-- no timeout!
	private int      connTimeout  = DEF_CONN_TIMEOUT;
	private int      connAttempts = DEF_CONN_ATTEMPTS;
	private int      reconnPause  = DEF_RECONN_PAUSE;

	/* protected + private: protocol state */

	/**
	 * This port is selected during the connection.
	 */
	protected String port;

	/**
	 * Set when the protocol wants the client to
	 * break it's activity. This flag is checked
	 * across all the implementation.
	 *
	 * If this flag is noticed to be set, exception
	 * {@link InterruptedException} is raised.
	 */
	protected volatile boolean  breaked;

	private static final Object waitee = new Object();
}