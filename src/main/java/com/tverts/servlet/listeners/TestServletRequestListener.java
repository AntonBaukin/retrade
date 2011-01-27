package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/* com.tverts: support */

import com.tverts.support.LU;

import static com.tverts.support.OU.sig;

/**
 * Runtime test for {@link ServletRequestListenerBean}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   TestServletRequestListener
       extends ServletRequestListenerBase
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

	protected static final String LOG =
	  TestServletRequestListener.class.getName();

	protected void logRequestInit(HttpServletRequest request)
	{
		if(!LU.isI(LOG)) return;

		LU.I(LOG, "HTTP request listener [", getTraceCode(),
		  "] STARTED request #{", sig(request), "}: \n",
		  request.getRequestURI());
	}

	protected void logRequestFree(HttpServletRequest request)
	{
		if(!LU.isI(LOG)) return;

		LU.I(LOG, "HTTP request listener [", getTraceCode(),
		  "] has DONE request #{", sig(request), "}");
	}

	/* private: the code of the listener */

	private String traceCode;
}

/*

  <bean name  = 'servletRequestListenerPoint' factory-method = 'getInstance'
        class = 'com.tverts.servlet.listeners.ServletRequestListenerPoint'>

    <property name = 'references'>
      <list>
        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = 'ZERO'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
          <property name = 'traceCode' value = '1'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
          <property name = 'references'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '2.2'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
                <property name = 'references'>
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
                <property name = 'references'>
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
          <property name = 'references'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletRequestListener'>
                <property name = 'traceCode' value = '4.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletRequestListenerBean'>
                <property name = 'references'>
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