package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/* Apache Log4J */

import org.apache.log4j.Logger;

/* com.tverts: support */

import static com.tverts.support.OU.sig;

/**
 * Runtime test for {@link ServletRequestListenerBean}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      TestServletRequestListener
       implements ServletRequestListener
{
	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent sre)
	{
		if(sre.getServletRequest() instanceof HttpServletRequest)
			logRequestInit((HttpServletRequest)sre.getServletRequest());
	}

	public void requestDestroyed(ServletRequestEvent sre)
	{
		if(sre.getServletRequest() instanceof HttpServletRequest)
			logRequestFree((HttpServletRequest)sre.getServletRequest());
	}

	/* public: bean interface */

	public String getTraceCode()
	{
		return traceCode;
	}

	public void   setTraceCode(String traceCode)
	{
		this.traceCode = traceCode;
	}

	/* protected: logging */

	protected static final Logger LOG =
	  Logger.getLogger("com.tverts.tests.TestServletRequestListener");

	protected void logRequestInit(HttpServletRequest request)
	{
		if(!LOG.isInfoEnabled()) return;

		LOG.info(String.format(
		  "HTTP request listener [%s] STARTED request #{%s}: \n%s",

		  getTraceCode(), sig(request), request.getRequestURI()
		));
	}

	protected void logRequestFree(HttpServletRequest request)
	{
		if(!LOG.isInfoEnabled()) return;

		LOG.info(String.format(
		  "HTTP request listener [%s] has DONE request #{%s}",

		  getTraceCode(), sig(request)
		));
	}

	/* private: the code of the listener */

	private String traceCode;
}

/*

  <bean name  = 'servletRequestListenerPoint' factory-method = 'getInstance'
        class = 'com.tverts.servlet.listeners.ServletRequestListenerPoint'>

    <property name = 'listeners'>
      <list>
        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = 'ZERO'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = '1'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
          <property name = 'listeners'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.2'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
                <property name = 'listeners'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                      <property name = 'traceCode' value = '2.3.1'/>
                    </bean>

                    <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                      <property name = 'traceCode' value = '2.3.2'/>
                    </bean>
                  </list>
                </property>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.4'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
                <property name = 'listeners'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                      <property name = 'traceCode' value = '2.5.1'/>
                    </bean>

                    <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                      <property name = 'traceCode' value = '2.5.2'/>
                    </bean>
                  </list>
                </property>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.6'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.7'/>
              </bean>
            </list>
          </property>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = '3'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
          <property name = 'listeners'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '4.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
                <property name = 'listeners'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                      <property name = 'traceCode' value = '4.2.1'/>
                    </bean>
                  </list>
                </property>
              </bean>
             </list>
          </property>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = '5'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = 'FINAL'/>
        </bean>
      </list>
    </property>
  </bean>

*/