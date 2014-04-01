package de.shhcm;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestvectorServiceServletContext implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("contextDestroyed() called.");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("contextInitialized() called.");
    }

}
