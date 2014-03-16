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

/**
 * Root resource (exposed at "myresource" path)
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
        String pathToLog4jProperties = (String) envContext.lookup("log4j_config_file_path");
        System.out.println(pathToLog4jProperties);
        
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
