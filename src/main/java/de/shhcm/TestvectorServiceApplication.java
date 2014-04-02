package de.shhcm;

import org.glassfish.jersey.server.ResourceConfig;

public class TestvectorServiceApplication extends ResourceConfig {
    /**
     * Register JAX-RS application components.
     */
    
    public TestvectorServiceApplication() {
        register(TestvectorService.class);
    }
}
