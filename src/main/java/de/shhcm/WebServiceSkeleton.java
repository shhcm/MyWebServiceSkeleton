package de.shhcm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

import de.shhcm.beans.SpringXmlBean;
import de.shhcm.dao.DependencyInjectedDao;
import de.shhcm.jaxb.EventCounter;
import de.shhcm.jaxb.SerializableEvent;
import de.shhcm.model.Event;

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
 *
 * Better Configuration: just use eclipse-ee's m2 with goal jetty:run!
 *
 * TODO: Add integration tests via maven fail safe plugin.
 * TODO: Check whether we can use the applicationContext.xml to set up DI wiring.
 *      According to https://jersey.java.net/nonav/documentation/latest/spring.html
 *      only wiring via spring annotations is supported.
 */

@Path("myresource")
@Component
public class WebServiceSkeleton {
    
    @Autowired
    private DependencyInjectedDao dependencyInjectedDao;
    private SpringXmlBean springXmlBean;
    
    public static Logger logger = Logger.getLogger(WebServiceSkeleton.class);

    private void init() throws NamingException, FileNotFoundException, IOException {
        // Lookup JNDI resources. (path to spring.xml and log4j.properties)
        InitialContext initialContext = new InitialContext();
        Context envContext = (Context) initialContext.lookup("java:comp/env");
        System.out.println("Got Context...");
        String pathToSpringXml = (String) envContext.lookup("spring_xml_file_path");
        
        // Get instance of FileSystemXmlApplicationContext, need to pass a URI here: file:///path
        // As the jersey-spring3 module does not support Spring XML configuration,
        // we may want to load some classes from a given spring xml programmatically like this.
        FileSystemXmlApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext(pathToSpringXml);
        springXmlBean = (SpringXmlBean) fileSystemXmlApplicationContext.getBean("TestBean");
        System.out.println("Bean loaded via FileSystemApplicationContext says " + springXmlBean.getFoo());
        fileSystemXmlApplicationContext.close();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN) // Client sends header "Accept: text/plain"
    public Response getText() {
        logger.info("GET received!");
        System.out.println("Bean loaded via DI says " + dependencyInjectedDao.getBar());
        return Response.ok("Got text!").build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SerializableEvent getEventAsJson() {
        // Return a JSON DTO.
        SerializableEvent serializableEvent = new SerializableEvent();
        serializableEvent.setTitle("JSON representation of an event.");
        serializableEvent.setEventCounter(new EventCounter());
        serializableEvent.getEventCounter().setCount(0);
        return serializableEvent;
    }
    
    /**
     * Writes an event object to the DB.
     * curl --request GET -H 'Accept:application/xml' localhost:8081/rest/myresource.
     * @return Response
     */
    
    @GET
    @Produces(MediaType.APPLICATION_XML) // Client sends header "Accept: application/xml"
    public SerializableEvent getEventAsXml() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("GET received!");
        System.out.println("Bean loaded via DI says " + dependencyInjectedDao.getBar());
        
        // Add a new event to the DB.
        Event event = new Event();
        event.setDate( new Date(System.currentTimeMillis()));
        event.setTitle("GET request");
        
        dependencyInjectedDao.saveEvent(event);
        int eventCount = dependencyInjectedDao.countEvents();
        
        // Return an XML DTO.
        SerializableEvent serializableEvent = new SerializableEvent();
        serializableEvent.setTitle(event.getTitle());
        serializableEvent.setEventCounter(new EventCounter());
        serializableEvent.getEventCounter().setCount(eventCount);
        return serializableEvent;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML) // Client sends Header "Content-Type: application/xml"
    public Response postXml() {
        logger.info("POST received!");
        return Response.ok("<xml>Got xml!</xml>").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_JSON) // Client sends Header "Content-Type: application/json"
    public Response postJson() {
        logger.info("POST received!");
        return Response.ok("<xml>Got json!</xml>").build();
    }
}
