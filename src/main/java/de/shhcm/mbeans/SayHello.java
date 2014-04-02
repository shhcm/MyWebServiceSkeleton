package de.shhcm.mbeans;

public class SayHello implements SayHelloMBean{
    @Override
    public String sayHello() {
        return "MBean says Hello!";
    }
}
