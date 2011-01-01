package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/* Apache Log4J */

import org.apache.log4j.Logger;

/* com.tverts: support */

import static com.tverts.support.OU.sig;

public class   TestServletContextListener
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		logContextOpened(sce.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		logContextReleased(sce.getServletContext());
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
	  Logger.getLogger("com.tverts.tests.TestServletContextListener");

	protected void logContextOpened(ServletContext ctx)
	{
		if(!LOG.isInfoEnabled()) return;

		LOG.info(String.format(
		  "Webapp context listener [%s] OPENED context #{%s}",

		  getTraceCode(), sig(ctx)
		));
	}

	protected void logContextReleased(ServletContext ctx)
	{
		if(!LOG.isInfoEnabled()) return;

		LOG.info(String.format(
		  "Webapp context listener [%s] RELEASED context #{%s}",

		  getTraceCode(), sig(ctx)
		));
	}

	/* private: the code of the listener */

	private String traceCode;
}

/*

  <bean name  = 'servletContextListenerPoint' factory-method = 'getInstance'
        class = 'com.tverts.servlet.listeners.ServletContextListenerPoint'>

    <property name = 'references'>
      <list>
        <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
          <property name = 'traceCode' value = 'ZERO'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
          <property name = 'traceCode' value = '1'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.ServletContextListenerBean'>
          <property name = 'references'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '2.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '2.2'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletContextListenerBean'>
                <property name = 'references'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                      <property name = 'traceCode' value = '2.3.1'/>
                    </bean>

                    <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                      <property name = 'traceCode' value = '2.3.2'/>
                    </bean>
                  </list>
                </property>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '2.4'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletContextListenerBean'>
                <property name = 'references'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                      <property name = 'traceCode' value = '2.5.1'/>
                    </bean>

                    <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                      <property name = 'traceCode' value = '2.5.2'/>
                    </bean>
                  </list>
                </property>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '2.6'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '2.7'/>
              </bean>
            </list>
          </property>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
          <property name = 'traceCode' value = '3'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.ServletContextListenerBean'>
          <property name = 'references'>
            <list>
              <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                <property name = 'traceCode' value = '4.1'/>
              </bean>

              <bean class = 'com.tverts.servlet.listeners.ServletContextListenerBean'>
                <property name = 'references'>
                  <list>
                    <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
                      <property name = 'traceCode' value = '4.2.1'/>
                    </bean>
                  </list>
                </property>
              </bean>
             </list>
          </property>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
          <property name = 'traceCode' value = '5'/>
        </bean>

        <bean class = 'com.tverts.servlet.listeners.TestServletContextListener'>
          <property name = 'traceCode' value = 'FINAL'/>
        </bean>
      </list>
    </property>
  </bean>

*/