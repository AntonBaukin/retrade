package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/* com.tverts: support */

import static com.tverts.support.OU.sig;
import com.tverts.support.LU;

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

	protected static final String LOG =
	  TestServletContextListener.class.getName();

	protected void logContextOpened(ServletContext ctx)
	{
		if(!LU.isI(LOG)) return;

		LU.I(LOG, "Webapp context listener [", getTraceCode(),
		 "] OPENED context #{", sig(ctx), "}");
	}

	protected void logContextReleased(ServletContext ctx)
	{
		if(!LU.isI(LOG)) return;

		LU.I(LOG, "Webapp context listener [", getTraceCode(),
		 "] RELEASED context #{", sig(ctx), "}");
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