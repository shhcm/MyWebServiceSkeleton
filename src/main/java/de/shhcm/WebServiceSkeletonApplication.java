package de.shhcm;

import org.glassfish.jersey.server.ResourceConfig;

public class WebServiceSkeletonApplication extends ResourceConfig {
    /**
     * Register JAX-RS application components.
     */
    
    public WebServiceSkeletonApplication() {
        register(WebServiceSkeleton.class);
    }
}
