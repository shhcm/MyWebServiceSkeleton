package de.shhcm;

import javax.annotation.PostConstruct;
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

import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.shhcm.beans.TestBean;

/**
 * Testing:
 * mvn jetty:run
 * curl -v --request POST  -H 'Content-type: application/json' localhost:8081/rest/myresource
 * 
 * -or-
 * 
 * Configuration for run-jetty-run eclipse plugin for jetty 9.0 (run configurations):
 * Show Advanced Options -> JNDI Support, Additional jetty.xml
 * 
 * TODO: WebService that returns test vectors.
 *  
 *  - Allow to post a symmetric key (BASE64), block size, plain text (not longer than the block size), 
 *      all packaged as JSON or XML. Return the test vector in a JSON or XML format.
 *
 *  - Allow to post a symmetric key (BASE64), block size, a plain text (arbitrary length), an operating mode, 
 *      all packaged as JSON or XML. Return the test vector in a JSON or XMLformat.
 *  
 *  - Do the same restfully as GET request.
 *  - Implement an idempotent and all-or-noting strategy. 
 */
@Path("myresource")
public class TestvectorService {

    @PostConstruct
    public void init() throws NamingException {
        System.out.println("Initialized!");
        
        // Lookup JNDI resources. (path to spring.xml and log4j.properties)
        InitialContext initialContext = new InitialContext();
        Context envContext = (Context) initialContext.lookup("java:comp/env");
        System.out.println("Got Context...");
        String pathToLog4jProperties = (String) envContext.lookup("log4j_config_file_path"); // TODO
        System.out.println(pathToLog4jProperties);
        String pathToSpringXml = (String) envContext.lookup("spring_xml_file_path");
        
        // Get instance of FileSystemXmlApplicationContext, need to pass a URI here: file:///path
        FileSystemXmlApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext(pathToSpringXml);
        TestBean testBean = (TestBean) fileSystemXmlApplicationContext.getBean("TestBean");
        System.out.println(testBean.getFoo());
        fileSystemXmlApplicationContext.close();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN) // Client sends header "Accept: text/plain"
    public Response getItText() {
        return Response.ok("Got it!").build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML) // Client sends header "Accept: application/xml"
    public Response getItXml() {
        return Response.ok("<xml>Got it!</xml>").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML) // Client sends Header "Content-Type: application/xml"
    public Response postItXml() {
        return Response.ok("<xml>Got xml!</xml>").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_JSON) // Client sends Header "Content-Type: application/json"
    public Response postItJson() {
        return Response.ok("<xml>Got json!</xml>").build();
    }
}
