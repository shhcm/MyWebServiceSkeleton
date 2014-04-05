package de.shhcm;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import de.shhcm.beans.DependencyInjectedBean;
import de.shhcm.beans.TestBean;

/**
 * Testing:
 * mvn jetty:run
 * curl -v --request POST  -H 'Content-type: application/json' localhost:8081/rest/myresource
 * 
 * -or-
 * 
 * Configuration for run-jetty-run eclipse plugin for jetty 9.0 (run configurations):
 *   Show Advanced Options -> JNDI Support, Additional jetty.xml
 *  
 *   Important: To run this via the run-jetty-run plugin, the
 *   postgres-9.3-1101-jdbc41.jar must be on the jetty classpath.
 *   (not just the webapp-classpath, as they are different!)
 *   This must be configured in the run-jetty-run run configuration
 *   for this plugin!
 */

@Path("myresource")
@Component
public class TestvectorService {
    
    @Autowired
    private DependencyInjectedBean dependencyInjectedBean;
    private EntityManagerFactory entityManagerFactory;
    
    public static Logger logger = Logger.getLogger(TestvectorService.class);

    public void init() throws NamingException, FileNotFoundException, IOException {
        // Lookup JNDI resources. (path to spring.xml and log4j.properties)
        InitialContext initialContext = new InitialContext();
        Context envContext = (Context) initialContext.lookup("java:comp/env");
        System.out.println("Got Context...");
        String pathToSpringXml = (String) envContext.lookup("spring_xml_file_path");
        
        // Get instance of FileSystemXmlApplicationContext, need to pass a URI here: file:///path
        FileSystemXmlApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext(pathToSpringXml);
        TestBean testBean = (TestBean) fileSystemXmlApplicationContext.getBean("TestBean");
        System.out.println("Bean loaded via FileSystemApplicationContext says " + testBean.getFoo());
        fileSystemXmlApplicationContext.close();
        
        //Improve: Do this using dependency injection.
        // Create Wrapper bean with singleton scope that gets injected into this class.
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("testdb");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN) // Client sends header "Accept: text/plain"
    public Response getItText() {
        logger.info("GET received!");
        System.out.println("Bean loaded via DI says " + dependencyInjectedBean.getBar());
        return Response.ok("Got it!").build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML) // Client sends header "Accept: application/xml"
    public Response getItXml() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("GET received!");
        System.out.println("Bean loaded via DI says " + dependencyInjectedBean.getBar());
        System.out.println("Trying to get EntityManager instance...");
        System.out.println("SessionFactory: " + entityManagerFactory.createEntityManager().toString());
        return Response.ok("<xml>Got it!</xml>").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML) // Client sends Header "Content-Type: application/xml"
    public Response postItXml() {
        logger.info("POST received!");
        return Response.ok("<xml>Got xml!</xml>").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_JSON) // Client sends Header "Content-Type: application/json"
    public Response postItJson() {
        logger.info("POST received!");
        return Response.ok("<xml>Got json!</xml>").build();
    }
}
