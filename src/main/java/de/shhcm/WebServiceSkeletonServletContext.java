package de.shhcm;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.shhcm.mbeans.SayHello;

public class WebServiceSkeletonServletContext implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("contextDestroyed() called.");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("contextInitialized() called.");
        FileSystemXmlApplicationContext xmlApplicationContext;
        try {
            // Lookup JNDI resources. (path to spring.xml and log4j.properties)
            // For usage of datasource via JNDI, see persistence.xml.
            InitialContext initialContext = new InitialContext();
            Context envContext = (Context) initialContext.lookup("java:comp/env");
            System.out.println("Got Context...");
            String pathToLog4jProperties = (String) envContext.lookup("log4j_config_file_path");
            // Read log4j properties
            Properties props = new Properties();
            props.load(new FileInputStream(pathToLog4jProperties));
            PropertyConfigurator.configure(props);
            
            String pathToSpringXml = (String) envContext.lookup("spring_xml_file_path");
            xmlApplicationContext = new FileSystemXmlApplicationContext(pathToSpringXml);
            
        } catch(NamingException e) {
            System.out.println("Cannot retrieve JNDI resources...");
            throw new RuntimeException(e);
        } catch(IOException e) {
            System.out.println("Cannot read log4j properties file...");
            throw new RuntimeException(e);
        }
        
        tryCreateMBeanServer();
        servletContextEvent.getServletContext().setAttribute(
                "fileSystemXmlApplicationContext",
                xmlApplicationContext);
    }

    public void tryCreateMBeanServer() {
        try {
            // Create JMX agent.
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("de.shhcm.mbeans:type=SayHello");
            SayHello mbean = new SayHello();
            // Not checking for the registered object name pevents hot deployment
            // as this bean is already registered and throws an exception (on jetty restart).
            if(!mbs.isRegistered(name)) {
                mbs.registerMBean(mbean, name);
            }
        } catch(Exception e) {
            System.out.println("Error creating jmx agent/ mbean server.");
            throw new RuntimeException(e);
        }
    }

}
