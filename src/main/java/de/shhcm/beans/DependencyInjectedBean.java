package de.shhcm.beans;

import org.springframework.stereotype.Component;

@Component
public class DependencyInjectedBean {
    private String bar = "foo";

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
    
}
