package de.shhcm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import de.shhcm.beans.TestBean;
import de.shhcm.mbeans.SayHello;

public class TestvectorServiceServletContext implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("contextDestroyed() called.");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("contextInitialized() called.");
        
        try {
            // Lookup JNDI resources. (path to spring.xml and log4j.properties)
            InitialContext initialContext = new InitialContext();
            Context envContext = (Context) initialContext.lookup("java:comp/env");
            System.out.println("Got Context...");
            String pathToLog4jProperties = (String) envContext.lookup("log4j_config_file_path"); // TODO
            
            
            // Read log4j properties
            Properties props = new Properties();
            props.load(new FileInputStream(pathToLog4jProperties));
            PropertyConfigurator.configure(props);
        } catch(NamingException e) {
            System.out.println("Cannot retrieve JNDI resources...");
            throw new RuntimeException(e);
        } catch(IOException e) {
            System.out.println("Cannot read log4j properties file...");
            throw new RuntimeException(e);
        }
        
        try {
            // Create JMX agent.
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName name = new ObjectName("de.shhcm.mbeans:type=SayHello"); 
            SayHello mbean = new SayHello(); 
            mbs.registerMBean(mbean, name);
        } catch(Exception e) {
            System.out.println("Error creating jmx agent/ mbean server.");
            throw new RuntimeException(e);
        }
    }

}
